package vn.core;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import vn.core.dto.ConvertData;
import vn.core.dto.IndexCell;
import vn.core.dto.Range;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ReadFile {
    private static final String START_VALUE = "Giá trị hóa đơn";
    private static final String REGEX_TARGET = "Tiền về\\s*(\\d{1,2})(\\S|\\s)(\\d{1,2})(\\S|\\s)(\\d{2,4})";

    private static final String REGEX_ADDRESS = "([a-zA-Z]+)(\\d+)";

    public ConvertData getFromFileExcel() throws IOException {
        //
//        FileUtils.cleanDirectory(new File("/opt"));
//        System.out.println("[read] delete temp file");
        //
        String fileExcelPath = "/opt/Tách-VNPT.xlsx";
        XSSFWorkbook workbook;
        FileInputStream inputStream = new FileInputStream(fileExcelPath);
        workbook = new XSSFWorkbook(inputStream);
        XSSFSheet sheet;
        sheet = workbook.getSheet("VNPT");

        Iterator<Row> rows = sheet.iterator();
        List<String> valueNames = new LinkedList<>();
        List<String> targetNames = new LinkedList<>();
        int maxRow = 0;
        while (rows.hasNext()) {
            XSSFRow row = (XSSFRow) rows.next();
            maxRow = row.getRowNum() + 1;
            Iterator<Cell> cells = row.iterator();
            while (cells.hasNext()) {
                XSSFCell cell = (XSSFCell) cells.next();
                Object cellValue = getCellValue(cell);
                if (cellValue instanceof String) {
                    Pattern pattern = Pattern.compile(START_VALUE, Pattern.CASE_INSENSITIVE);
                    Matcher matcher = pattern.matcher(cellValue.toString());
                    if (matcher.find())
                        valueNames.add(cell.getAddress().toString());
                    pattern = Pattern.compile(REGEX_TARGET, Pattern.CASE_INSENSITIVE);
                    matcher = pattern.matcher(cellValue.toString());
                    if (matcher.find())
                        targetNames.add(cell.getAddress().toString());
                }
            }
        }

        ConvertData data = new ConvertData();

        List<Range> ranges = new LinkedList<>();
        Range range = new Range();

        for (int i = 0; i < valueNames.size(); i++) {
            IndexCell indexCell = getIndexCell(valueNames.get(i));
            if (indexCell == null)
                continue;
            if (sheet.isColumnHidden(CellReference.convertColStringToIndex(indexCell.getColName())))
                continue;
            boolean addNew = true;
            for (int j = indexCell.getRowNum() + 1; j <= maxRow; j++) {
                CellReference cellReference = new CellReference(indexCell.getColName() + j);
                XSSFRow row = sheet.getRow(cellReference.getRow());
                if (row == null) {
                    break;
                } else if (row.getZeroHeight()) {
                    addNew = true;
                } else {
                    XSSFCell cell = row.getCell(cellReference.getCol());
                    if (cell == null)
                        break;
                    Object cellValue = getCellValue(cell);
                    if (cellValue == null)
                        continue;
                    else if (cell.getCellType() == CellType.FORMULA)
                        break;
                    if (cellValue instanceof Double) {
                        if (addNew) {
                            range = new Range(indexCell.getColName(), j, j);
                            ranges.add(range);
                            addNew = false;
                        } else {
                            range.setEnd(j);
                        }
                    } else
                        break;
                }
            }
        }

        data.setValues(ranges);

        ranges = new LinkedList<>();
        range = new Range();

        for (int i = 0; i < targetNames.size(); i++) {
            IndexCell indexCell = getIndexCell(targetNames.get(i));
            if (indexCell == null)
                continue;
            if (sheet.isColumnHidden(CellReference.convertColStringToIndex(indexCell.getColName())))
                continue;
            CellReference cellReference = new CellReference(nextColumn(indexCell));
            XSSFRow row = sheet.getRow(cellReference.getRow());
            if (row == null || row.getZeroHeight()) {
                continue;
            } else {
                XSSFCell cell = row.getCell(cellReference.getCol());
                if (cell == null)
                    continue;
                Object cellValue = getCellValue(cell);
                if (cellValue == null)
                    continue;
                if (cellValue instanceof Double) {
                    if (nextColumn(indexCell.getColName()).equals(range.getName()) && indexCell.getRowNum() == range.getEnd() + 1) {
                        range.setEnd(indexCell.getRowNum());
                    } else {
                        range = new Range(nextColumn(indexCell.getColName()), indexCell.getRowNum(), indexCell.getRowNum());
                        ranges.add(range);
                    }
                }
            }
        }

        data.setTargets(ranges);
        return data;
    }



    private static String nextColumn(IndexCell indexCell) {
        int colIdx = CellReference.convertColStringToIndex(indexCell.getColName()) + 1;
        String colName = CellReference.convertNumToColString(colIdx);
        return colName + indexCell.getRowNum();
    }

    private static String nextColumn(String colName) {
        int colIdx = CellReference.convertColStringToIndex(colName) + 1;
        return CellReference.convertNumToColString(colIdx);
    }

    private static IndexCell getIndexCell(String address) {
        Pattern pattern = Pattern.compile(REGEX_ADDRESS);
        Matcher matcher = pattern.matcher(address);
        if (!matcher.find())
            return null;
        return new IndexCell(matcher.group(1), Integer.valueOf(matcher.group(2)));
    }

    private static Object getCellValue(Cell cell) {
        CellType cellType = cell.getCellType();
        Object cellValue = null;
        switch (cellType) {
            case BOOLEAN:
                cellValue = cell.getBooleanCellValue();
                break;
            case FORMULA:
                Workbook workbook = cell.getSheet().getWorkbook();
                FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
                cellValue = evaluator.evaluate(cell).getNumberValue();
                break;
            case NUMERIC:
                cellValue = cell.getNumericCellValue();
                break;
            case STRING:
                cellValue = cell.getStringCellValue();
                break;
            default:
                break;
        }
        return cellValue;
    }

    private static Long parseLong(Object value) {
        if (value instanceof Long)
            return (Long) value;
        if (value instanceof Double)
            return ((Double) value).longValue();
        return 0L;
    }
}
