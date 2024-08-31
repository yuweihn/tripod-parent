package com.yuweix.tripod.core.office.excel.reader;


import com.yuweix.tripod.core.json.JsonUtil;
import com.yuweix.tripod.core.office.excel.annotation.ExcelKey;
import com.yuweix.tripod.core.office.excel.model.PoiSheet;
import org.apache.poi.ss.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author yuwei
 */
public abstract class PoiExcelReader {
	private static final Logger log = LoggerFactory.getLogger(PoiExcelReader.class);


	public static<T> List<T> read(byte[] bytes, Class<T> clz) {
		List<T> list = new ArrayList<>();

		Field[] fields = clz.getDeclaredFields();
		if (fields == null || fields.length <= 0) {
			return list;
		}

		List<Map<String, Object>> mapList = read(bytes);
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

	public static List<PoiSheet<Map<String, Object>>> read(byte[] bytes) {
		InputStream is = null;
		try {
			List<PoiSheet<Map<String, Object>>> sheetList = new ArrayList<>();
			is = new ByteArrayInputStream(bytes);
			Workbook wb = WorkbookFactory.create(is);
			int sheetCount = wb.getNumberOfSheets();
			for (int i = 0; i < sheetCount; i++) {
				Sheet sheet = wb.getSheetAt(i);
				List<Map<String, Object>> sheetDataList = read(sheet);
				PoiSheet<Map<String, Object>> poiSheet = new PoiSheet<>();
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
					log.error("", e);
				}
			}
		}
	}

	private static List<Map<String, Object>> read(Sheet sheet) {
		/**
		 * 读取头部，第一行
		 **/
		List<String> headList = getInputHeadList(sheet.getRow(0));
		/**
		 * 读取数据部分，从第二行开始
		 **/
		return getInputDataList(sheet, headList);
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

	private static<T> List<T> getInputDataList(Sheet sheet, List<String> keyList) {
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
				map.put(keyList.get(idx), getCellValue(cell));
			}
			list.add(map);
		}
		return list;
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

