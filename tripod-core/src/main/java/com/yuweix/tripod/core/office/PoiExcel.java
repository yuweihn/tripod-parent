package com.yuweix.tripod.core.office;


import java.beans.PropertyDescriptor;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.yuweix.tripod.core.json.JsonUtil;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.util.Assert;


/**
 * @author yuwei
 */
public abstract class PoiExcel {
	private static final Logger log = LoggerFactory.getLogger(PoiExcel.class);
	private static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
	private static final int DEFAULT_FONT_HEIGHT = 20;


	/**
	 * 读入excel工作簿文件，只读取第一个sheet
	 */
	public static List<Map<String, Object>> read(byte[] fileData) {
		InputStream is = null;
		try {
			is = new ByteArrayInputStream(fileData);
			Workbook wb = WorkbookFactory.create(is);
			Sheet sheet = wb.getSheetAt(0);
			/**
			 * 读取头部，第一行
			 **/
			List<String> headList = getInputHeadList(sheet.getRow(0));
			/**
			 * 读取数据部分，从第二行开始
			 **/
			return getInputDataList(sheet, headList);
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					log.error("", e);
				}
			}
		}
	}

	public static<T> List<T> read(byte[] fileData, Class<T> clz) {
		List<T> list = new ArrayList<>();

		Field[] fields = clz.getDeclaredFields();
		if (fields == null || fields.length <= 0) {
			return list;
		}

		List<Map<String, Object>> mapList = read(fileData);
		if (mapList == null || mapList.size() <= 0) {
			return list;
		}
		for (Map<String, Object> map: mapList) {
			Map<String, Object> fieldValueMap = new HashMap<>();
			for (Field field: fields) {
				ExcelKey excelKeyAno = field.getAnnotation(ExcelKey.class);
				if (excelKeyAno == null) {
					continue;
				}

				String key = excelKeyAno.title() == null || "".equals(excelKeyAno.title().trim()) ? field.getName() : excelKeyAno.title().trim();
				Object v = map.get(key);
				if (v == null) {
					continue;
				}
				fieldValueMap.put(field.getName(), v);
			}
			T t = JsonUtil.parseObject(JsonUtil.toJSONString(fieldValueMap), clz);
			list.add(t);
		}
		return list;
	}

	/**
	 * 导出数据
	 * @param dataList 数据
	 */
	public static<T> byte[] export(Class<T> clz, List<T> dataList) {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		export(clz, dataList, out);
		byte[] data = out.toByteArray();

		try {
			out.close();
		} catch (IOException e) {
			log.error("", e);
		}
		return data;
	}

	public static<T> void export(Class<T> clz, List<T> dataList, String fileName, HttpServletResponse resp) {
		resp.setContentType("application/vnd.ms-excel");
		resp.setCharacterEncoding("utf-8");
		resp.setHeader("Content-disposition", "attachment;filename=" + fileName);
		resp.setHeader("_filename", fileName);
		resp.setHeader("Access-Control-Expose-Headers", "_filename");
		try {
			export(clz, dataList, resp.getOutputStream());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 导出数据到输出流
	 * @param dataList 数据
	 */
	public static<T> void export(Class<T> clz, List<T> dataList, OutputStream out) {
		log.info("list size: {}", dataList == null ? 0 : dataList.size());
		SXSSFWorkbook workbook = null;
		try {
			workbook = new SXSSFWorkbook();
			workbook.setCompressTempFiles(true);
			/**
			 * 表头样式
			 */
			CellStyle titleStyle = workbook.createCellStyle();
			titleStyle.setAlignment(HorizontalAlignment.CENTER);
			Font titleFont = workbook.createFont();
			titleFont.setFontHeightInPoints((short) DEFAULT_FONT_HEIGHT);
			titleFont.setBold(true);
			titleStyle.setFont(titleFont);

			SXSSFSheet sheet = workbook.createSheet();
			sheet.trackAllColumnsForAutoSizing();

			/**
			 * 输出头部
			 **/
			List<String> headList = dataList != null && !dataList.isEmpty()
					? getOutputHeadList(dataList.get(0))
					: getOutputHeadList(clz);
			SXSSFRow headRow = sheet.createRow(0);
			for (int i = 0; i < headList.size(); i++) {
				String head = headList.get(i);
				SXSSFCell cell = headRow.createCell(i);
				cell.setCellValue(head);
				cell.setCellStyle(titleStyle);
				sheet.autoSizeColumn(i, true);
			}

			if (dataList != null && !dataList.isEmpty()) {
				List<String> keyList = getOutputKeyList(dataList.get(0));
				/**
				 * 输出数据部分
				 **/
				for (int i = 0; i < dataList.size(); i++) {
					T t = dataList.get(i);
					SXSSFRow dataRow = sheet.createRow(i + 1);
					List<Object> dList = getOutputDataList(keyList, t);
					for (int j = 0; j < dList.size(); j++) {
						SXSSFCell cell = dataRow.createCell(j);
						setCellValue(cell, dList.get(j));
					}
				}
			}

			workbook.write(out);
		} catch (Exception e) {
			log.error("", e);
		} finally {
			if (workbook != null) {
				try {
					workbook.close();
				} catch (IOException e) {
					log.error("", e);
				}
			}
		}
	}

	private static<T> List<String> getOutputHeadList(T t) {
		List<String> list = new ArrayList<>();
		if (Map.class.isAssignableFrom(t.getClass())) {
			Map<?, ?> map = (Map<?, ?>) t;
			for (Object o : map.keySet()) {
				list.add(o.toString());
			}
		} else {
			Field[] fields = t.getClass().getDeclaredFields();
			if (fields != null && fields.length > 0) {
				for (Field field: fields) {
					ExcelKey excelKeyAno = field.getAnnotation(ExcelKey.class);
					if (excelKeyAno != null) {
						list.add(excelKeyAno.title() == null || "".equals(excelKeyAno.title().trim()) ? field.getName() : excelKeyAno.title().trim());
					}
				}
			}
		}
		return list;
	}

	private static<T> List<String> getOutputHeadList(Class<T> clz) {
		List<String> list = new ArrayList<>();
		if (clz == null) {
			return list;
		}
		Field[] fields = clz.getDeclaredFields();
		if (fields != null && fields.length > 0) {
			for (Field field: fields) {
				ExcelKey excelKeyAno = field.getAnnotation(ExcelKey.class);
				if (excelKeyAno != null) {
					list.add(excelKeyAno.title() == null || "".equals(excelKeyAno.title().trim()) ? field.getName() : excelKeyAno.title().trim());
				}
			}
		}
		return list;
	}

	private static<T> List<String> getOutputKeyList(T t) {
		List<String> list = new ArrayList<>();
		if (Map.class.isAssignableFrom(t.getClass())) {
			Map<?, ?> map = (Map<?, ?>) t;
			for (Object o : map.keySet()) {
				list.add(o.toString());
			}
		} else {
			Field[] fields = t.getClass().getDeclaredFields();
			if (fields != null && fields.length > 0) {
				for (Field field: fields) {
					ExcelKey excelKeyAno = field.getAnnotation(ExcelKey.class);
					if (excelKeyAno != null) {
						list.add(field.getName());
					}
				}
			}
		}

		return list;
	}

	private static<T> List<Object> getOutputDataList(List<String> keyList, T t) {
		Assert.notEmpty(keyList, "[keyList] is required.");
		List<Object> list = new ArrayList<>();

		if (Map.class.isAssignableFrom(t.getClass())) {
			Map<?, ?> map = (Map<?, ?>) t;
			for (String key: keyList) {
				list.add(map.get(key));
			}
		} else {
			for (String key: keyList) {
				PropertyDescriptor pd = null;
				try {
					pd = new PropertyDescriptor(key, t.getClass());
					Method getMethod = pd.getReadMethod();
					Object o = getMethod.invoke(t);
					list.add(o);
				} catch (Exception e) {
					log.error("", e);
				}
			}
		}
		return list;
	}

	private static List<String> getInputHeadList(Row row) {
		List<String> list = new ArrayList<>();
		for (Cell cell: row) {
			String head = cell.toString();
			if (head == null || "".equals(head.trim())) {
				continue;
			}
			list.add(head.trim());
		}
		return list;
	}

	private static List<Map<String, Object>> getInputDataList(Sheet sheet, List<String> keyList) {
		List<Map<String, Object>> list = new ArrayList<>();
		for (Row row: sheet) {
			if (row.getRowNum() <= 0) {
				continue;
			}

			Map<String, Object> map = new HashMap<>();
			int keySize = keyList.size();
			for (Cell cell: row) {
				int idx = cell.getColumnIndex();
				if (idx < 0 || idx >= keySize) {
					continue;
				}
				map.put(keyList.get(idx), getCellValue(cell));
			}
			list.add(map);
		}
		return list;
	}

	private static void setCellValue(Cell cell, Object value) {
		if (value instanceof Number) {
			cell.setCellValue(value == null ? 0 : Double.parseDouble(value.toString()));
		} else if (value instanceof Boolean) {
			cell.setCellValue(value != null && Boolean.parseBoolean(value.toString()));
		} else if (value instanceof Date) {
			cell.setCellValue(value == null ? "" : new SimpleDateFormat(DEFAULT_DATE_FORMAT).format(value));
		} else {
			cell.setCellValue(value == null ? "" : value.toString());
		}
	}

	private static Object getCellValue(Cell cell) {
		CellType ct = cell.getCellType();
		if (CellType.NUMERIC == ct) {
			if (DateUtil.isCellDateFormatted(cell)) {
				return cell.getDateCellValue();
			}

			double d = cell.getNumericCellValue();
			int i = (int) d;
			if (d == i) {
				return i;
			} else {
				return d;
			}
//			return NumberToTextConverter.toText(cell.getNumericCellValue());
		}
		if (CellType.STRING == ct) {
			return cell.getRichStringCellValue().getString();
		}
		if (CellType.FORMULA == ct) {
			Workbook wb = cell.getSheet().getWorkbook();
			CreationHelper crateHelper = wb.getCreationHelper();
			FormulaEvaluator evaluator = crateHelper.createFormulaEvaluator();
			return getCellValue(evaluator.evaluateInCell(cell));
		}
		if (CellType.BLANK == ct) {
			return null;
		}
		if (CellType.BOOLEAN == ct) {
			return cell.getBooleanCellValue();
		}
		if (CellType.ERROR == ct) {
			return null;
		}
		return null;
	}
}

