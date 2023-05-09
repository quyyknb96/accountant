package vn.core.accountant.util;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import vn.core.accountant.dto.IndexCell;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExcelUtil {
    private static final String REGEX_ADDRESS = "([a-zA-Z]+)(\\d+)";

    public static Object getCellValue(Cell cell) {
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

    public static String nextColumn(IndexCell indexCell) {
        int colIdx = CellReference.convertColStringToIndex(indexCell.getColName()) + 1;
        String colName = CellReference.convertNumToColString(colIdx);
        return colName + indexCell.getRowNum();
    }

    public static String nextColumn(String colName) {
        return nextColumn(colName, 1);
    }

    public static String nextColumn(String colName, int step) {
        int colIdx = CellReference.convertColStringToIndex(colName) + step;
        return CellReference.convertNumToColString(colIdx);
    }

    public static IndexCell getIndexCell(String address) {
        Pattern pattern = Pattern.compile(REGEX_ADDRESS);
        Matcher matcher = pattern.matcher(address);
        if (!matcher.find())
            return null;
        return new IndexCell(matcher.group(1), Integer.valueOf(matcher.group(2)));
    }

    public static Object getDataBtAddress(XSSFSheet sheet, String address) {
        CellReference cellReference = new CellReference(address);
        XSSFRow row = sheet.getRow(cellReference.getRow());
        if (row == null)
            return null;
        XSSFCell cell = row.getCell(cellReference.getCol());
        if (cell == null)
            return null;
        return getCellValue(cell);
    }
}
