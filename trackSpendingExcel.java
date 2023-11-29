package Javapro;

import java.util.ArrayList;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.poi.xssf.usermodel.*;

public class trackSpendingExcel {
    private String excelPath;
    private final String userName;
    private XSSFWorkbook workbook;
    private ArrayList<XSSFSheet> sheets = new ArrayList<XSSFSheet>();;

    private FileInputStream fis;
    private FileOutputStream fos;

    public void changeExcelPath(String newExcelPath) throws Exception{
        String oldExcelPath = excelPath;
        newExcelPath += "\\" + userName + ".xlsx";
        try{
            fos.close();
        } catch (NullPointerException e) {}
        fos = new FileOutputStream(newExcelPath);
        workbook.write(fos);
        excelPath = newExcelPath;
        fis.close();
        upDate();
        new File(oldExcelPath).delete();
    }
    public void setExcelPath(String excelPath) {
        this.excelPath = excelPath + "\\" + userName + ".xlsx";
    }
    public trackSpendingExcel(String userName, String excelPath) throws Exception{
        this.userName = userName;
        setExcelPath(excelPath);
        if(!Files.exists(Paths.get(this.excelPath))){
            workbook = new XSSFWorkbook();
            sheets.add(workbook.createSheet("總覽"));
            sheets.get(0).setColumnWidth(0, 15 * 256);
            XSSFRow row = sheets.get(0).createRow(0);
            row.createCell(0).setCellValue("時間");
            row.createCell(1).setCellValue("類別");
            row.createCell(2).setCellValue("名稱");
            row.createCell(3).setCellValue("收入");
            row.createCell(4).setCellValue("支出");
            row.createCell(5).setCellValue("結算");
            row.createCell(6).setCellValue("備註");
            for(int i=0; i<AllTypes.size; ++i) {
                sheets.add(workbook.createSheet(AllTypes.types[i]));
                row = sheets.get(i+1).createRow(0);
                sheets.get(i+1).setColumnWidth(0, 15 * 256);
                row.createCell(0).setCellValue("時間");
                row.createCell(1).setCellValue("名稱");
                row.createCell(2).setCellValue("收入");
                row.createCell(3).setCellValue("支出");
                row.createCell(4).setCellValue("結算");
                row.createCell(5).setCellValue("備註");
            }
            fos = new FileOutputStream(this.excelPath);
            workbook.write(fos);
            fis = new FileInputStream(this.excelPath);
            return;
        }
        fis = new FileInputStream(this.excelPath);
        workbook = new XSSFWorkbook(fis);
        sheets.add(workbook.getSheet("總覽"));
        for(int i=0; i<AllTypes.size; ++i) {
            sheets.add(workbook.getSheet(AllTypes.types[i]));
        }
    }
    public void upDate() throws Exception{
        fis = new FileInputStream(excelPath);
        workbook = new XSSFWorkbook(fis);
        sheets.set(0,workbook.getSheet("總覽"));
        for(int i=0; i<AllTypes.size; ++i) {
            sheets.set(i+1,workbook.getSheet(AllTypes.types[i]));
        }
    }
    public void write(int type, String date, String name, long amount, boolean isEarn, String remark) throws Exception{
        fos = new FileOutputStream(excelPath);
        XSSFSheet nowSheet = sheets.get(type+1);
        int newRow = nowSheet.getPhysicalNumberOfRows();
        XSSFRow row = nowSheet.createRow(newRow);
        if(newRow == 1) {
            row.createCell(0).setCellValue(date);
            row.createCell(1).setCellValue(name);
            row.createCell(isEarn? 2 : 3).setCellValue(amount);
            row.createCell(4).setCellValue(amount*(isEarn?1:-1));
            row.createCell(5).setCellValue(remark);
            writeOverview(type, date, name, amount, isEarn, remark);
            workbook.write(fos);
            upDate();
            return;
        }
        String formula = "E"+ newRow + "+C" + (newRow+1) + "-D" +(newRow+1);
        row.createCell(0).setCellValue(date);
        row.createCell(1).setCellValue(name);
        row.createCell(isEarn? 2 : 3).setCellValue(amount);
        row.createCell(4).setCellFormula(formula);
        row.createCell(5).setCellValue(remark);

        writeOverview(type, date, name, amount, isEarn, remark);

        workbook.write(fos);
        upDate();
        return;
    }
    private void writeOverview(int type, String date, String name, long amount, boolean isEarn, String remark){
        XSSFSheet overviewSheet = sheets.get(0);
        int newRow = overviewSheet.getPhysicalNumberOfRows();
        XSSFRow row = overviewSheet.createRow(newRow);
        String item = AllTypes.types[type];
        if(newRow == 1) {
            row.createCell(0).setCellValue(date);
            row.createCell(1).setCellValue(item.substring(0, item.indexOf(' ')));
            row.createCell(2).setCellValue(name);
            row.createCell(isEarn? 3 : 4).setCellValue(amount);
            row.createCell(5).setCellValue(amount*(isEarn?1:-1));
            row.createCell(6).setCellValue(remark);
            return;
        }
        String formula = "F"+ newRow + "+D" + (newRow+1) + "-E" +(newRow+1);
        row.createCell(0).setCellValue(date);
        row.createCell(1).setCellValue(item.substring(0, item.indexOf(' ')));
        row.createCell(2).setCellValue(name);
        row.createCell(isEarn? 3 : 4).setCellValue(amount);
        row.createCell(5).setCellFormula(formula);
        row.createCell(6).setCellValue(remark);
    }
    public void removeRow(String TypeName, int Type, int nowRow) throws Exception{
        fos = new FileOutputStream(excelPath);
        XSSFSheet nowSheet = sheets.get(Type);
        int rowCount = nowSheet.getPhysicalNumberOfRows()-1;
        for(int i=nowRow; i<rowCount ; ++i) {
            XSSFRow row = nowSheet.getRow(i), newRow = nowSheet.getRow(i+1);
            for(int j=0; j<row.getPhysicalNumberOfCells(); ++j) {
                if(j==4) continue;
                XSSFCell newCell = newRow.getCell(j);
                if(newCell==null) continue;
                switch (newCell.getCellType()) {
                    case STRING:
                        row.getCell(j).setCellValue(newCell.getStringCellValue());
                        break;
                    case NUMERIC:
                        if(row.getCell(j)==null){
                            row.createCell(j);
                            XSSFCell cell = row.getCell(j==2?3:2);
                            row.removeCell(cell);
                        }
                        row.getCell(j).setCellValue((long)newCell.getNumericCellValue());
                        break;
                    default:
                }
            }
        }
        if(nowRow==1) {
            XSSFRow row = nowSheet.getRow(1);
            long t;
            t = (row.getCell(2) == null && row.getCell(3) == null)?0:
            (row.getCell(2) == null)?(long)row.getCell(3).getNumericCellValue():(long)row.getCell(2).getNumericCellValue();
            row.getCell(4).setCellValue(t);
        }
        XSSFRow row = nowSheet.getRow(rowCount);
        nowSheet.removeRow(row);
        overviewRemoveRow(TypeName, nowRow);
        workbook.write(fos);
        upDate();
    }
    private void overviewRemoveRow(String TypeName, int rowCount) {
        XSSFSheet nowSheet = sheets.get(0);
        int counter=0, i=1;
        for(; counter<rowCount; ++i) {
            XSSFRow row = nowSheet.getRow(i);
            if(row.getCell(1).getStringCellValue().equals(TypeName)) counter++;
        }
        for(--i; i<nowSheet.getPhysicalNumberOfRows()-1; ++i) {
            XSSFRow row = nowSheet.getRow(i), newRow = nowSheet.getRow(i+1);
            for(int j=0; j<row.getPhysicalNumberOfCells(); ++j) {
                if(j==5) continue;
                XSSFCell newCell = newRow.getCell(j);
                if(newCell==null) continue;
                switch (newCell.getCellType()) {
                    case STRING:
                        row.getCell(j).setCellValue(newCell.getStringCellValue());
                        break;
                    case NUMERIC:
                        if(row.getCell(j)==null){
                            row.createCell(j);
                            XSSFCell cell = row.getCell(j==3?4:3);
                            row.removeCell(cell);
                        }
                        row.getCell(j).setCellValue((long)newCell.getNumericCellValue());
                        break;
                    default:
                }
            }
        }
        if(i==2) {
            XSSFRow row = nowSheet.getRow(1);
            long t;
            t = (row.getCell(3) == null && row.getCell(4) == null)?0:
            (row.getCell(3) == null)?(long)row.getCell(4).getNumericCellValue():(long)row.getCell(3).getNumericCellValue();
            row.getCell(5).setCellValue(t);
        }
        XSSFRow row = nowSheet.getRow(i);
        nowSheet.removeRow(row);
    }
    public void editRow(String TypeName, int Type, int nowRow, Object[] newRow) throws Exception{
        fos = new FileOutputStream(excelPath);
        XSSFSheet nowSheet = sheets.get(Type);
        XSSFRow row = nowSheet.getRow(nowRow);
        row.getCell(0).setCellValue((String)newRow[0]);
        row.getCell(1).setCellValue((String)newRow[1]);
        if((boolean)newRow[3]){//收入
            if(row.getCell(2)==null){
                row.removeCell(row.getCell(3));
                row.createCell(2).setCellValue((long)newRow[2]);
            }
            else{
                row.getCell(2).setCellValue((long)newRow[2]);
            }
            if(nowRow==1) {
                nowSheet.getRow(1).getCell(4).setCellValue((long)newRow[2]);
            }
        }
        else{//支出
            if(row.getCell(3)==null){
                row.removeCell(row.getCell(2));
                row.createCell(3).setCellValue((long)newRow[2]);
            }
            else{
                row.getCell(3).setCellValue((long)newRow[2]);
            }
            if(nowRow==1) {
                nowSheet.getRow(1).getCell(4).setCellValue((long)newRow[2]*-1);
            }
        }
        row.getCell(5).setCellValue((String)newRow[4]);
        overviewEditRow(TypeName, nowRow, newRow);
        workbook.write(fos);
        upDate();
        return;
    }
    public void overviewEditRow(String TypeName, int rowCount, Object[] newRow) {
        XSSFSheet nowSheet = sheets.get(0);
        int counter=0, i=1;
        XSSFRow row = nowSheet.getRow(i);
        for(; counter<rowCount; ++i) {
            row = nowSheet.getRow(i);
            if(row.getCell(1).getStringCellValue().equals(TypeName)) counter++;
        }
        row.getCell(0).setCellValue((String)newRow[0]);
        row.getCell(2).setCellValue((String)newRow[1]);
        if((boolean)newRow[3]){//收入
            if(row.getCell(3)==null){
                row.removeCell(row.getCell(4));
                row.createCell(3).setCellValue((long)newRow[2]);
            }
            else{
                row.getCell(3).setCellValue((long)newRow[2]);
            }
            if(i==2) {
                nowSheet.getRow(1).getCell(5).setCellValue((long)newRow[2]);
            }
        }
        else{//支出
            if(row.getCell(4)==null){
                row.removeCell(row.getCell(3));
                row.createCell(4).setCellValue((long)newRow[2]);
            }
            else{
                row.getCell(4).setCellValue((long)newRow[2]);
            }
            if(i==2) {
                nowSheet.getRow(1).getCell(5).setCellValue((long)newRow[2]*-1);
            }
        }
        row.getCell(6).setCellValue((String)newRow[4]);
    }
    public Object[][] getData(int idx){
        XSSFSheet nowSheet = sheets.get(idx);
        //扣除第一行
        int rowCount = nowSheet.getPhysicalNumberOfRows()-1;
        int columnCount = nowSheet.getRow(0).getPhysicalNumberOfCells();
        Object[][] data = new Object[rowCount][columnCount];

        for(int i=0; i<rowCount; ++i) {
            XSSFRow nowRow = nowSheet.getRow(i+1);
            for(int j=0; j<columnCount; ++j) {
                XSSFCell nowCell = nowRow.getCell(j);
                if (nowCell != null) {
                    switch (nowCell.getCellType()) {
                        case STRING:
                            data[i][j] = nowCell.getStringCellValue();
                            break;
                        case NUMERIC:
                            data[i][j] = (long)nowCell.getNumericCellValue();
                            break;
                        case FORMULA:
                            //不知道為啥用data[i][2]==""會顯示data[i][2]是null
                            //但用data[i][2]==null還是錯
                            long a = String.valueOf(data[i][2]).equals("")?0:(long)data[i][2];
                            long b = String.valueOf(data[i][3]).equals("")?0:(long)data[i][3];
                            data[i][j] = (long)data[i-1][4]+a-b;
                            break;
                        default:
                            data[i][j] = "";
                            break;
                    }
                } else {
                    data[i][j] = "";
                }
            }
        }

        return data;
    }
}