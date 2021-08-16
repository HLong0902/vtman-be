package com.viettel.vtman.cms.controller;

import com.viettel.vtman.cms.entity.AutoContent;
import org.apache.commons.io.FileUtils;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class ExcelExporter {
    private XSSFWorkbook workbook;
    private XSSFSheet sheet;
    private List<AutoContent> listAutoContent;

    public ExcelExporter(List<AutoContent> listAutoContent) {
        this.listAutoContent = Objects.isNull(listAutoContent) ? null : new ArrayList<>(listAutoContent);
        workbook = new XSSFWorkbook();
    }

    private void writeTitle() {
        String title = "Danh sách tin nhắn tự động";
        sheet = workbook.createSheet(title);
        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setBold(true);
        font.setFontHeight(15);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);

        Row firstRow = sheet.createRow(0);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 3));
        createCell(firstRow, 0, title.toUpperCase(), style);
    }
    private void writeHeaderLine() {
        CellStyle style = workbook.createCellStyle();

        XSSFFont font = workbook.createFont();
        font.setBold(true);
        font.setFontHeight(12);
        style.setFont(font);

        Row row = sheet.createRow(2);
//        createCell(row, 0, "STT", style);
        createCell(row, 0, "Loại tin nhắn tự động", style);
        createCell(row, 1, "Nội dung tin nhắn tự động", style);
        createCell(row, 2, "Ghi chú", style);
        createCell(row, 3, "Trạng thái", style);
//        sheet.setColumnWidth(0, 1500);
        sheet.setColumnWidth(0, 7000);
        sheet.setColumnWidth(1, 17000);
        sheet.setColumnWidth(2, 17000);
        sheet.setColumnWidth(3, 4500);
        style.setFillForegroundColor(IndexedColors.PALE_BLUE.index);
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
    }
     
    private void createCell(Row row, int columnCount, Object value, CellStyle style) {
        Cell cell = row.createCell(columnCount);
        if (value instanceof Integer) {
            cell.setCellValue((Integer) value);
        } else if (value instanceof Boolean) {
            cell.setCellValue((Boolean) value);
        }else {
            cell.setCellValue((String) value);
        }
        cell.setCellStyle(style);
    }
     
    private void writeDataLines() {
        int rowCount = 3;

        CellStyle style1 = workbook.createCellStyle();
        style1.setBorderTop(BorderStyle.THIN);
        style1.setBorderBottom(BorderStyle.THIN);
        style1.setAlignment(HorizontalAlignment.CENTER);

        CellStyle style = workbook.createCellStyle();
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setAlignment(HorizontalAlignment.LEFT);
        style.setVerticalAlignment(VerticalAlignment.TOP);
        style.setWrapText(true);
        XSSFFont font = workbook.createFont();
        font.setFontHeight(12);
        style.setFont(font);
        style1.setFont(font);

        for (Object autoContent : listAutoContent) {
            HashMap<String, Object> map = (HashMap<String, Object>) autoContent;
            Row row = sheet.createRow(rowCount++);
            int columnCount = 0;

//            createCell(row, columnCount++, rowCount-3, style1);
            createCell(row, columnCount++, map.get("type") , style);
            createCell(row, columnCount++, map.get("automaticContentName"), style);
            createCell(row, columnCount++, map.get("description"), style);
            createCell(row, columnCount++, (long) map.get("isActive") == 1L ? "Hoạt động" : "Không hoạt động", style);
        }
    }
     
    public byte[] export() throws IOException {
        writeTitle();
        writeHeaderLine();
        writeDataLines();


        File outputFile = File.createTempFile("test", ".xls");
        try (FileOutputStream fos = new FileOutputStream(outputFile)) {
            workbook.write(fos);
        }
        byte[] fileContent = FileUtils.readFileToByteArray(outputFile);

        workbook.close();

        return fileContent;
         
    }
}