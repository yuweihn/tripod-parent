package com.yuweix.tripod.core.office.excel;


import com.yuweix.tripod.core.json.JsonUtil;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.PropertyDescriptor;
import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.*;


/**
 * @author yuwei
 */
public abstract class PoiExcel {
	private static final Logger log = LoggerFactory.getLogger(PoiExcel.class);
	private static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
	private static final int DEFAULT_FONT_HEIGHT = 20;


	public static<T> List<T> read(byte[] bytes, Class<T> clz) {
		List<T> allList = new ArrayList<>();
		List<PoiSheet<T>> list = readSheet(bytes, clz);
		if (list == null || list.size() <= 0) {
			return allList;
		}
		for (PoiSheet<T> poiSheet: list) {
			List<T> subList = poiSheet.getList();
			if (subList == null || subList.size() <= 0) {
				continue;
			}
			allList.addAll(subList);
		}
		return allList;
	}

	public static<T> List<PoiSheet<T>> readSheet(byte[] bytes, Class<T> clz) {
		List<PoiSheet<T>> sheetList = new ArrayList<>();

		Map<String, String> fieldMap = getTitleFieldMap(clz);
		if (fieldMap == null || fieldMap.size() <= 0) {
			return sheetList;
		}
		InputStream is = null;
		try {
			is = new ByteArrayInputStream(bytes);
			Workbook wb = WorkbookFactory.create(is);
			int sheetCount = wb.getNumberOfSheets();
			for (int i = 0; i < sheetCount; i++) {
				Sheet sheet = wb.getSheetAt(i);
				List<T> sheetDataList = read(sheet, clz, fieldMap);
				PoiSheet<T> poiSheet = new PoiSheet<>();
				poiSheet.setSheetName(sheet.getSheetName());
				poiSheet.setList(sheetDataList);
				sheetList.add(poiSheet);
			}
			return sheetList;
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					log.error("Error: {}", new Object[] {e});
				}
			}
		}
	}

	public static List<String> getSheetNameList(byte[] bytes) {
		List<String> list = new ArrayList<>();
		InputStream is = null;
		try {
			is = new ByteArrayInputStream(bytes);
			Workbook wb = WorkbookFactory.create(is);
			int sheetCount = wb.getNumberOfSheets();
			for (int i = 0; i < sheetCount; i++) {
				Sheet sheet = wb.getSheetAt(i);
				list.add(sheet.getSheetName().trim());
			}
			return list;
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					log.error("Error: {}", new Object[] {e});
				}
			}
		}
	}

	public static<T> List<T> readSheet(byte[] bytes, Class<T> clz, String sheetName) {
		List<T> sheetList = new ArrayList<>();

		if (sheetName == null || "".equals(sheetName)) {
			return sheetList;
		}

		Map<String, String> fieldMap = getTitleFieldMap(clz);
		if (fieldMap == null || fieldMap.size() <= 0) {
			return sheetList;
		}
		InputStream is = null;
		try {
			is = new ByteArrayInputStream(bytes);
			Workbook wb = WorkbookFactory.create(is);
			int sheetCount = wb.getNumberOfSheets();
			for (int i = 0; i < sheetCount; i++) {
				Sheet sheet = wb.getSheetAt(i);
				if (!sheetName.trim().equals(sheet.getSheetName().trim())) {
					continue;
				}
				return read(sheet, clz, fieldMap);
			}
			return sheetList;
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					log.error("Error: {}", new Object[] {e});
				}
			}
		}
	}

	public static<T> List<T> readSheet(byte[] bytes, Class<T> clz, int sheetIdx) {
		List<T> sheetList = new ArrayList<>();

		Map<String, String> fieldMap = getTitleFieldMap(clz);
		if (fieldMap == null || fieldMap.size() <= 0) {
			return sheetList;
		}
		InputStream is = null;
		try {
			is = new ByteArrayInputStream(bytes);
			Workbook wb = WorkbookFactory.create(is);
			return read(wb.getSheetAt(0), clz, fieldMap);
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					log.error("Error: {}", new Object[] {e});
				}
			}
		}
	}

	private static<T> List<T> read(Sheet sheet, Class<T> clz, Map<String, String> fieldMap) {
		/**
		 * 读取头部，第一行
		 **/
		List<String> headList = getImportHeadList(sheet.getRow(0));
		/**
		 * 读取数据部分，从第二行开始
		 **/
		return getImportDataList(sheet, headList, clz, fieldMap);
	}

	public static List<String> getImportHeadList(byte[] bytes) {
		return getImportHeadList(bytes, null);
	}
	public static List<String> getImportHeadList(byte[] bytes, String sheetName) {
		InputStream is = null;
		try {
			is = new ByteArrayInputStream(bytes);
			Workbook wb = WorkbookFactory.create(is);
			if (sheetName == null || "".equals(sheetName.trim())) {
				return getImportHeadList(wb.getSheetAt(0).getRow(0));
			}
			int sheetCount = wb.getNumberOfSheets();
			for (int i = 0; i < sheetCount; i++) {
				Sheet sheet = wb.getSheetAt(i);
				if (!sheetName.trim().equals(sheet.getSheetName().trim())) {
					continue;
				}
				return getImportHeadList(sheet.getRow(0));
			}
			return new ArrayList<>();
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					log.error("Error: {}", new Object[] {e});
				}
			}
		}
	}

	private static List<String> getImportHeadList(Row row) {
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

	private static<T> List<T> getImportDataList(Sheet sheet, List<String> keyList, Class<T> clz, Map<String, String> fieldMap) {
		List<T> list = new ArrayList<>();
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
				String key = fieldMap == null ? null : fieldMap.get(keyList.get(idx));
				map.put(key, getCellValue(cell));
			}
			list.add(JsonUtil.parseObject(JsonUtil.toJSONString(map), clz));
		}
		return list;
	}

	/**
	 * 获取{@param clz}中{@link ExcelKey}注解的title与fieldName组成的键值对
	 */
	private static<T> Map<String, String> getTitleFieldMap(Class<T> clz) {
		Map<String, String> result = new HashMap<>();
		Field[] fields = clz.getDeclaredFields();
		if (fields == null || fields.length <= 0) {
			return result;
		}
		for (Field field : fields) {
			ExcelKey excelKeyAnn = field.getAnnotation(ExcelKey.class);
			if (excelKeyAnn == null) {
				continue;
			}
			String key = excelKeyAnn.title() == null || "".equals(excelKeyAnn.title().trim()) ? field.getName() : excelKeyAnn.title().trim();
			result.put(key, field.getName());
		}
		return result;
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
			titleStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
			titleStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

			SXSSFSheet sheet = workbook.createSheet();
			sheet.trackAllColumnsForAutoSizing();

			/**
			 * 输出头部
			 **/
			List<String> headList = dataList != null && !dataList.isEmpty()
					? getExportHeadList(dataList.get(0))
					: getExportHeadList(clz);
			SXSSFRow headRow = sheet.createRow(0);
			for (int i = 0; i < headList.size(); i++) {
				String head = headList.get(i);
				SXSSFCell cell = headRow.createCell(i);
				cell.setCellValue(head);
				cell.setCellStyle(titleStyle);
				sheet.autoSizeColumn(i, true);
			}

			if (dataList != null && !dataList.isEmpty()) {
				List<String> keyList = getExportKeyList(dataList.get(0));
				/**
				 * 输出数据部分
				 **/
				for (int i = 0; i < dataList.size(); i++) {
					T t = dataList.get(i);
					SXSSFRow dataRow = sheet.createRow(i + 1);
					List<Object> dList = getExportDataList(keyList, t);
					for (int j = 0; j < dList.size(); j++) {
						SXSSFCell cell = dataRow.createCell(j);
						setCellValue(cell, dList.get(j));
						sheet.autoSizeColumn(j, true);
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

	private static<T> List<String> getExportHeadList(T t) {
		List<String> list = new ArrayList<>();
		if (Map.class.isAssignableFrom(t.getClass())) {
			Map<?, ?> map = (Map<?, ?>) t;
			for (Object o : map.keySet()) {
				list.add(o.toString());
			}
			return list;
		} else {
			return getOrderedExportHeadList(t.getClass().getDeclaredFields());
		}
	}

	private static<T> List<String> getExportHeadList(Class<T> clz) {
		if (clz == null) {
			return new ArrayList<>();
		}
		return getOrderedExportHeadList(clz.getDeclaredFields());
	}

	private static List<String> getOrderedExportHeadList(Field[] fields) {
		List<String> list = new ArrayList<>();
		if (fields == null || fields.length <= 0) {
			return list;
		}
		Set<Field> fieldSet = new TreeSet<>(new Comparator<Field>() {
			@Override
			public int compare(Field f1, Field f2) {
				ExcelKey excelKey1 = f1.getAnnotation(ExcelKey.class);
				ExcelKey excelKey2 = f2.getAnnotation(ExcelKey.class);
				if (excelKey1 == null || excelKey2 == null) {
					return -1;
				}
				if (excelKey1.order() == excelKey2.order()) {
					return 1;
				}
				return excelKey1.order() - excelKey2.order();
			}
		});
		fieldSet.addAll(Arrays.asList(fields));
		for (Field field: fieldSet) {
			ExcelKey excelKey = field.getAnnotation(ExcelKey.class);
			if (excelKey != null) {
				list.add(excelKey.title() == null || "".equals(excelKey.title().trim()) ? field.getName() : excelKey.title().trim());
			}
		}
		return list;
	}

	private static<T> List<String> getExportKeyList(T t) {
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

	private static<T> List<Object> getExportDataList(List<String> keyList, T t) {
		List<Object> list = new ArrayList<>();
		if (keyList == null || keyList.size() <= 0) {
			return list;
		}

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
}

