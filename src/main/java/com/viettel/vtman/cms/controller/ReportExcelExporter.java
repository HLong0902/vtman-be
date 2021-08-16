package com.viettel.vtman.cms.controller;

import org.apache.commons.io.FileUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

public class ReportExcelExporter {
    private XSSFWorkbook workbook;
    private XSSFSheet sheet;
    private List<HashMap<String, Object>> list;

    public ReportExcelExporter(List<HashMap<String, Object>> list) {
        this.list = Objects.isNull(list) ? null : new ArrayList<>(list);
        workbook = new XSSFWorkbook();
    }

    public void writeTitle(String sheetname, String title, int len) {
        sheet = workbook.createSheet(sheetname);
        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setBold(true);
        font.setFontHeight(17);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        Row firstRow = sheet.createRow(0);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, len));
        createCell(firstRow, 0, title, style);
    }
    public void writeDate(Date startDate, Date endDate, int len) {
        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setFontHeight(14);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        Row firstRow = sheet.createRow(2);
        sheet.addMergedRegion(new CellRangeAddress(2, 2, 0, len));
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyy");
        StringBuilder text = new StringBuilder();
        if (Objects.nonNull(startDate)) {
            text.append("Từ ngày ");
            text.append(sdf.format(startDate));
            if (Objects.nonNull(endDate)) {
                text.append(" Đến ngày ");
                text.append(sdf.format(endDate));
            }
        }

        createCell(firstRow, 0, text.toString(), style);
    }
    public void writeHeaderLine(String[] headers) {
        CellStyle style = workbook.createCellStyle();

        XSSFFont font = workbook.createFont();
        font.setBold(true);
        font.setFontHeight(14);
        style.setFont(font);

        Row row = sheet.createRow(4);
        createCell(row, 0, "STT", style);
        int columnCount = 1;
        for (String header : headers) createCell(row, columnCount++, header, style);
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
        if (value instanceof Long) {
            cell.setCellValue((Long) value);
        } else if (value instanceof Double) {
            cell.setCellValue((Double) value);
        } else if (value instanceof Integer) {
            cell.setCellValue((Integer) value);
        } else {
            cell.setCellValue((String) value);
        }
        cell.setCellStyle(style);
    }

    private void writeDataLines(String[] fields) {
        int rowCount = 5;

        CellStyle style = workbook.createCellStyle();
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setAlignment(HorizontalAlignment.LEFT);
        XSSFFont font = workbook.createFont();
        font.setFontHeight(14);
        style.setFont(font);
        style.setWrapText(true);
        style.setVerticalAlignment(VerticalAlignment.TOP);
        CellStyle numberStyle = workbook.createCellStyle();
        numberStyle.cloneStyleFrom(style);
        numberStyle.setAlignment(HorizontalAlignment.CENTER);

        List<String> numberFields = Arrays.asList("answered", "percent", "total", "rated", "rating", "expired", "expired");

        for (HashMap map : list) {
            Row row = sheet.createRow(rowCount++);
            int columnCount = 0;
            createCell(row, columnCount, rowCount-5, numberStyle);
            sheet.autoSizeColumn(columnCount++);
            for (String field : fields) {
                createCell(row, columnCount, map.get(field) , numberFields.contains(field) ? numberStyle : style);
                sheet.autoSizeColumn(columnCount++);
            }
            sheet.setColumnWidth(3, 17000);
            sheet.setColumnWidth(4, 17000);


        }
    }

    public byte[] export(String sheetname, String title, String[] headers, String[] fields, Date startDate, Date endDate) throws IOException {
        writeTitle(sheetname, title, headers.length);
        writeDate(startDate, endDate, headers.length);
        writeHeaderLine(headers);
        writeDataLines(fields);

        File outputFile = File.createTempFile("test", ".xls");
        try (FileOutputStream fos = new FileOutputStream(outputFile)) {
            workbook.write(fos);
        }
        byte[] fileContent = FileUtils.readFileToByteArray(outputFile);

        workbook.close();

        return fileContent;
    }
}
