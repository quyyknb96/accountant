package vn.core;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.ss.util.CellUtil;
import org.apache.poi.ss.util.SheetUtil;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

public class WriteFile {
    public static void main(String[] args) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(Files.newInputStream(Paths.get("setting.txt"))));
        reader.readLine(); // ignore
        String excelName = reader.readLine().trim();
        reader.readLine(); // ignore
        String sheetName = reader.readLine().trim();
        reader.readLine(); // ignore
        String colValue = reader.readLine().trim().toUpperCase();
        reader.readLine(); // ignore
        String line = reader.readLine().trim();
        int colValueFrom = Integer.parseInt(line.split(" ")[0]);
        reader.readLine(); // ignore
        String colPay = reader.readLine().trim().toUpperCase();
        reader.readLine(); // ignore
        line = reader.readLine().trim();
        int colPayFrom = Integer.parseInt(line.split(" ")[0]);
        int colPayTo = Integer.parseInt(line.split(" ")[1]);
        reader.close();
        //
        BufferedReader readFile = new BufferedReader(new InputStreamReader(Files.newInputStream(Paths.get("opt/write.txt"))));
        //
        String fileExcelPath = excelName;
        XSSFWorkbook workbook;
        FileInputStream inputStream;
        try {
            inputStream = new FileInputStream(fileExcelPath);
            workbook = new XSSFWorkbook(inputStream);
        } catch (IOException e) {
            System.out.println("[write] can not read file " + excelName);
            return;
        }
        XSSFSheet sheet;
        sheet = workbook.getSheet(sheetName);
        Cell cellValueFormat = SheetUtil.getCell(sheet, colValueFrom, CellReference.convertColStringToIndex(colValue));
        for (Row nextRow : sheet) {
            int rowNum = nextRow.getRowNum();
            if (rowNum + 1 < colPayFrom || rowNum + 1 > colPayTo)
                continue;
            String lineResult = readFile.readLine().trim();
            String[] split = lineResult.split(" ");
            int n = Integer.parseInt(split[0]);
            int columnIndex = CellReference.convertColStringToIndex(colPay) + 2;
            if (n == 0) {
                Cell cell = nextRow.getCell(columnIndex);
                if (cell == null)
                    cell = CellUtil.getCell(nextRow, columnIndex);
                CellStyle cellStyle = cellValueFormat.getCellStyle();
                cellStyle.setAlignment(HorizontalAlignment.LEFT);
                cell.setCellStyle(cellStyle);
                cell.setCellValue("Không tìm được đại vương ơi !!!");
            } else {
                for (int i = 0; i < n; i++) {
                    Cell cell = nextRow.getCell(columnIndex + i);
                    if (cell == null)
                        cell = CellUtil.getCell(nextRow, columnIndex + i);
                    CellStyle cellStyle = cellValueFormat.getCellStyle();
                    cellStyle.setAlignment(HorizontalAlignment.LEFT);
                    cell.setCellStyle(cellStyle);
                    cell.setCellValue(Long.parseLong(split[i + 1]));
                }
            }
        }
        try {
            inputStream.close();
            FileOutputStream fileOutputStream = new FileOutputStream(fileExcelPath);
            workbook.write(fileOutputStream);
            workbook.close();
        } catch (IOException e) {
            System.out.println("[write] can not override file. Close file before and re-run");
            System.out.println("[write] press Enter to close window");
            return;
        }
        readFile.close();
        System.out.println("[write] Process Successfully");
        System.out.println("[write] press Enter to close window");
    }
}
