package vn.core;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.util.Strings;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.ss.util.CellUtil;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import vn.core.accountant.dto.ConvertData;
import vn.core.accountant.dto.IndexCell;
import vn.core.accountant.dto.SolutionData;
import vn.core.accountant.util.ExcelUtil;

import java.io.*;
import java.util.List;

public class WriteFile {
    public static void saveFile(List<SolutionData> data, ConvertData convertData) throws IOException {
        String fileExcelPath = "/opt/input." + FilenameUtils.getExtension(convertData.getFilename());
        XSSFWorkbook workbook;
        FileInputStream inputStream = new FileInputStream(fileExcelPath);
        workbook = new XSSFWorkbook(inputStream);
        XSSFSheet sheet;
        if (Strings.isBlank(convertData.getSheetName()))
            sheet = workbook.getSheetAt(0);
        else
            sheet = workbook.getSheet(convertData.getSheetName());

        for (int i = 0; i < data.size(); i++) {
            SolutionData solution = data.get(i);
            IndexCell indexCell = ExcelUtil.getIndexCell(solution.getAddress());
            if (indexCell == null)
                continue;
            for (int j = 0;  j < solution.getList().size(); j++) {
                CellReference cellReference = new CellReference(ExcelUtil.nextColumn(indexCell.getColName(), j + 2) + indexCell.getRowNum());
                XSSFRow row = sheet.getRow(cellReference.getRow());
                if (row == null)
                    break;
                XSSFCell cell = row.getCell(cellReference.getCol());
                if (cell == null)
                    cell = (XSSFCell) CellUtil.getCell(row, cellReference.getCol());
                cell.setCellValue(solution.getList().get(j));
            }
        }


        FileOutputStream fileOutputStream = new FileOutputStream(fileExcelPath);
        workbook.write(fileOutputStream);
        workbook.close();
        inputStream.close();
    }
}
