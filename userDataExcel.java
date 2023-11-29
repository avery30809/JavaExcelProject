package Javapro;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.poi.xssf.usermodel.*;

public class userDataExcel {
    private final String DEFAULT_PATH;
    private final String userDataPath;

    private XSSFWorkbook workbook;
    private XSSFSheet sheet;

    private FileInputStream fis;
    private FileOutputStream fos;

    public String getExcelPath(String userName){
        String excelPath = DEFAULT_PATH;
        int idx = findUserIndex(userName);
        if(idx!=-1) excelPath = getCellValue(sheet.getRow(idx).getCell(2));
        return excelPath;
    }
    public userDataExcel(String userDataPath) throws Exception{
        this.userDataPath = userDataPath;
        DEFAULT_PATH = userDataPath.substring(0, userDataPath.lastIndexOf('\\')) + "\\Excel";
        if(!Files.exists(Paths.get(userDataPath))){
            workbook = new XSSFWorkbook();
            sheet = workbook.createSheet("userData");
            XSSFRow row = sheet.createRow(0);
            row.createCell(0).setCellValue("username");
            row.createCell(1).setCellValue("password");
            row.createCell(2).setCellValue("ExcelPath");
            fos = new FileOutputStream(userDataPath);
            workbook.write(fos);
            fis = new FileInputStream(userDataPath);
            return;
        }
        upDate();
    }
    public boolean hasUserName(String userName) {
        int rowCount = sheet.getPhysicalNumberOfRows();
        for(int i=1; i<rowCount; ++i) {
            XSSFRow row = sheet.getRow(i);
            if(userName.equals(getCellValue(row.getCell(0)))) return true;
        }
        return false;
    }
    public String findUserPassword(String userName) {
        String password = "";
        
        int idx = findUserIndex(userName);
        if(idx!=-1) password = getCellValue(sheet.getRow(idx).getCell(1));

        return password;
    }
    public String getCellValue(XSSFCell cell) {
        switch(cell.getCellType()) {
            case NUMERIC:
                return String.valueOf(cell.getNumericCellValue());
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            default:
                return cell.getStringCellValue();
        }
    }
    public void write(String name, String password) throws Exception{
        fos = new FileOutputStream(userDataPath);
        int newRow = sheet.getPhysicalNumberOfRows();
        XSSFRow row = sheet.createRow(newRow);
        row.createCell(0).setCellValue(name);
        row.createCell(1).setCellValue(password);
        row.createCell(2).setCellValue(DEFAULT_PATH);
        workbook.write(fos);
        upDate();
    }
    public void upDate() throws Exception{
        fis = new FileInputStream(userDataPath);
        workbook = new XSSFWorkbook(fis);
        sheet = workbook.getSheet("userData");
    }
    public void editExcelPath(String userName, String newPath)throws Exception{
        fos = new FileOutputStream(userDataPath);
        int idx = findUserIndex(userName);
        sheet.getRow(idx).getCell(2).setCellValue(newPath);
        workbook.write(fos);
        upDate();
    }
    public int findUserIndex(String userName){
        for(int i=1 ; i<sheet.getPhysicalNumberOfRows(); ++i) {
            XSSFRow row = sheet.getRow(i);
            if(userName.equals(getCellValue(row.getCell(0))))
                return i;
        }
        return -1;
    }
}