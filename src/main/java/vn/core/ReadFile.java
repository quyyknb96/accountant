package vn.core;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.util.Strings;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;
import vn.core.accountant.dto.ConvertData;
import vn.core.accountant.dto.IndexCell;
import vn.core.accountant.dto.Range;
import vn.core.accountant.util.FileUtil;

import java.io.*;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static vn.core.accountant.util.ExcelUtil.*;

public class ReadFile {
    private static final String START_VALUE = "Giá trị hóa đơn";
    private static final String REGEX_TARGET = "Tiền về\\s*(\\d{1,2})(\\S|\\s)(\\d{1,2})(\\S|\\s)(\\d{2,4})";

    public static ConvertData getFromFileExcel(MultipartFile multipartFile, String sheetName) throws IOException {
        File folderUpload = new File(FileUtil.PATH_TEMP);
        if (!folderUpload.exists()) {
            folderUpload.mkdirs();
        }
        XSSFWorkbook workbook;
        InputStream inputStream = new BufferedInputStream(multipartFile.getInputStream());
        //
        String fileExtension = FilenameUtils.getExtension(multipartFile.getOriginalFilename());
        File file = new File( FileUtil.PATH_TEMP + "/" + FileUtil.FILE_NAME_INPUT + "."+ fileExtension);
        OutputStream outputStream = new FileOutputStream(file);
        IOUtils.copy(inputStream, outputStream);
        outputStream.close();
        //
        inputStream = new BufferedInputStream(multipartFile.getInputStream());
        workbook = new XSSFWorkbook(inputStream);
        XSSFSheet sheet;
        if (Strings.isBlank(sheetName))
            sheet = workbook.getSheetAt(0);
        else
            sheet = workbook.getSheet(sheetName);

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
        data.setFilename(multipartFile.getOriginalFilename());

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

        inputStream.close();

        return data;
    }
}
