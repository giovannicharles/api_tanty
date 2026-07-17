package com.NTFOODS.Api_tanty.modules.stock.application.service;

import com.NTFOODS.Api_tanty.modules.stock.domain.common.entity.StockItem;
import com.NTFOODS.Api_tanty.modules.stock.domain.common.entity.StockMovement;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xddf.usermodel.chart.*;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ExcelReportService {

    private static final DateTimeFormatter DT_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public byte[] generateStockItemsExcel(List<StockItem> items, String locationName, Map<String, String> designations) throws IOException {
        try (XSSFWorkbook wb = new XSSFWorkbook(); ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            XSSFSheet sheet = wb.createSheet("Stock " + locationName);
            createTitleRow(wb, sheet, "RAPPORT DE STOCK - " + locationName.toUpperCase());

            int headerRow = 2;
            String[] headers = {"SKU", "Désignation", "Quantité", "Qté/Carton", "Cartons", "Poids Unitaire", "Volume", "Cartons/Assortiment"};
            createHeaderRow(wb, sheet, headerRow, headers);

            int rowIdx = headerRow + 1;
            for (StockItem item : items) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(item.getProductSku());
                row.createCell(1).setCellValue(designations != null ? designations.getOrDefault(item.getProductSku(), "—") : "—");
                row.createCell(2).setCellValue(item.getQuantity().doubleValue());
                row.createCell(3).setCellValue(item.getQuantityPerCarton() != null ? item.getQuantityPerCarton().doubleValue() : 0);
                row.createCell(4).setCellValue(item.calculateCartons().doubleValue());
                row.createCell(5).setCellValue(item.getUnitWeight() != null ? item.getUnitWeight().doubleValue() : 0);
                row.createCell(6).setCellValue(item.getVolume() != null ? item.getVolume() : "—");
                row.createCell(7).setCellValue(item.getCartonsPerAssortiment() != null ? item.getCartonsPerAssortiment() : 0);
            }

            autoSizeColumns(sheet, headers.length);
            addStockQuantityChart(wb, sheet, items, rowIdx + 2, designations);

            wb.write(baos);
            return baos.toByteArray();
        }
    }

    public byte[] generateStockMovementsExcel(List<StockMovement> movements) throws IOException {
        try (XSSFWorkbook wb = new XSSFWorkbook(); ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            XSSFSheet sheet = wb.createSheet("Mouvements");
            createTitleRow(wb, sheet, "RAPPORT DES MOUVEMENTS DE STOCK");

            int headerRow = 2;
            String[] headers = {"Référence", "Type", "SKU", "Quantité", "Date", "Statut", "Notes"};
            createHeaderRow(wb, sheet, headerRow, headers);

            int rowIdx = headerRow + 1;
            for (StockMovement m : movements) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(m.getReferenceNumber() != null ? m.getReferenceNumber() : "—");
                row.createCell(1).setCellValue(m.getType() != null ? m.getType().toString() : "—");
                row.createCell(2).setCellValue(m.getProductSku() != null ? m.getProductSku() : "—");
                row.createCell(3).setCellValue(m.getQuantity().doubleValue());
                row.createCell(4).setCellValue(m.getRequestedAt() != null ? m.getRequestedAt().format(DT_FMT) : "—");
                row.createCell(5).setCellValue(m.getStatus() != null ? m.getStatus().toString() : "—");
                row.createCell(6).setCellValue(m.getNotes() != null ? m.getNotes() : "—");
            }

            autoSizeColumns(sheet, headers.length);
            addMovementTypeChart(wb, sheet, movements, rowIdx + 2);

            wb.write(baos);
            return baos.toByteArray();
        }
    }

    public byte[] generateValorisationExcel(List<StockItem> items, Map<String, String> designations,
                                            String locationName) throws IOException {
        try (XSSFWorkbook wb = new XSSFWorkbook(); ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            XSSFSheet sheet = wb.createSheet("Valorisation");
            createTitleRow(wb, sheet, "RAPPORT DE VALORISATION - " + locationName.toUpperCase());

            int headerRow = 2;
            String[] headers = {"SKU", "Désignation", "Quantité", "Poids Unitaire", "Poids Total", "Conditionnement"};
            createHeaderRow(wb, sheet, headerRow, headers);

            int rowIdx = headerRow + 1;
            for (StockItem item : items) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(item.getProductSku());
                row.createCell(1).setCellValue(designations != null ? designations.getOrDefault(item.getProductSku(), "—") : "—");
                row.createCell(2).setCellValue(item.getQuantity().doubleValue());
                row.createCell(3).setCellValue(item.getUnitWeight() != null ? item.getUnitWeight().doubleValue() : 0);
                BigDecimal totalWeight = item.getUnitWeight() != null ? item.getUnitWeight().multiply(item.getQuantity()) : BigDecimal.ZERO;
                row.createCell(4).setCellValue(totalWeight.doubleValue());
                row.createCell(5).setCellValue(item.getQuantityPerCarton() != null ? item.getQuantityPerCarton().intValue() + " unités/carton" : "—");
            }

            autoSizeColumns(sheet, headers.length);
            addValorisationChart(wb, sheet, items, rowIdx + 2, designations);

            wb.write(baos);
            return baos.toByteArray();
        }
    }

    public byte[] generateGlobalReportExcel(List<StockItem> items, List<StockMovement> movements,
                                            Map<String, String> designations, String locationName,
                                            LocalDateTime periodStart, LocalDateTime periodEnd) throws IOException {
        try (XSSFWorkbook wb = new XSSFWorkbook(); ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            // Sheet 1: Stock summary
            XSSFSheet stockSheet = wb.createSheet("Stock");
            createTitleRow(wb, stockSheet, "STOCK - " + locationName.toUpperCase());
            String[] stockHeaders = {"SKU", "Désignation", "Quantité", "Poids Unitaire", "Poids Total"};
            createHeaderRow(wb, stockSheet, 2, stockHeaders);
            int rowIdx = 3;
            for (StockItem item : items) {
                Row row = stockSheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(item.getProductSku());
                row.createCell(1).setCellValue(designations != null ? designations.getOrDefault(item.getProductSku(), "—") : "—");
                row.createCell(2).setCellValue(item.getQuantity().doubleValue());
                row.createCell(3).setCellValue(item.getUnitWeight() != null ? item.getUnitWeight().doubleValue() : 0);
                BigDecimal totalWeight = item.getUnitWeight() != null ? item.getUnitWeight().multiply(item.getQuantity()) : BigDecimal.ZERO;
                row.createCell(4).setCellValue(totalWeight.doubleValue());
            }
            autoSizeColumns(stockSheet, stockHeaders.length);
            addStockQuantityChart(wb, stockSheet, items, rowIdx + 2, designations);

            // Sheet 2: Movements
            XSSFSheet moveSheet = wb.createSheet("Mouvements");
            String periodStr = periodStart.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + " - " + periodEnd.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            createTitleRow(wb, moveSheet, "MOUVEMENTS | Période: " + periodStr);
            String[] moveHeaders = {"Référence", "Type", "SKU", "Quantité", "Date", "Statut"};
            createHeaderRow(wb, moveSheet, 2, moveHeaders);
            int mRowIdx = 3;
            for (StockMovement m : movements) {
                Row row = moveSheet.createRow(mRowIdx++);
                row.createCell(0).setCellValue(m.getReferenceNumber() != null ? m.getReferenceNumber() : "—");
                row.createCell(1).setCellValue(m.getType() != null ? m.getType().toString() : "—");
                row.createCell(2).setCellValue(m.getProductSku() != null ? m.getProductSku() : "—");
                row.createCell(3).setCellValue(m.getQuantity().doubleValue());
                row.createCell(4).setCellValue(m.getRequestedAt() != null ? m.getRequestedAt().format(DT_FMT) : "—");
                row.createCell(5).setCellValue(m.getStatus() != null ? m.getStatus().toString() : "—");
            }
            autoSizeColumns(moveSheet, moveHeaders.length);
            addMovementTypeChart(wb, moveSheet, movements, mRowIdx + 2);

            // Sheet 3: Summary with charts
            XSSFSheet summarySheet = wb.createSheet("Synthèse");
            createTitleRow(wb, summarySheet, "SYNTHÈSE GLOBALE");
            Row summaryHeader = summarySheet.createRow(2);
            summaryHeader.createCell(0).setCellValue("Indicateur");
            summaryHeader.createCell(1).setCellValue("Valeur");
            summaryHeader.getCell(0).setCellStyle(createHeaderStyle(wb));
            summaryHeader.getCell(1).setCellStyle(createHeaderStyle(wb));

            summarySheet.createRow(3).createCell(0).setCellValue("Total produits");
            summarySheet.getRow(3).createCell(1).setCellValue(items.size());
            summarySheet.createRow(4).createCell(0).setCellValue("Total mouvements");
            summarySheet.getRow(4).createCell(1).setCellValue(movements.size());
            BigDecimal totalQty = items.stream().map(StockItem::getQuantity).reduce(BigDecimal.ZERO, BigDecimal::add);
            summarySheet.createRow(5).createCell(0).setCellValue("Quantité totale en stock");
            summarySheet.getRow(5).createCell(1).setCellValue(totalQty.doubleValue());

            autoSizeColumns(summarySheet, 2);

            wb.write(baos);
            return baos.toByteArray();
        }
    }

    // ── Private helpers ──

    private void createTitleRow(XSSFWorkbook wb, XSSFSheet sheet, String title) {
        Row titleRow = sheet.createRow(0);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue(title);
        XSSFCellStyle titleStyle = wb.createCellStyle();
        XSSFFont font = wb.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 14);
        titleStyle.setFont(font);
        titleCell.setCellStyle(titleStyle);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 7));
    }

    private void createHeaderRow(XSSFWorkbook wb, XSSFSheet sheet, int rowIdx, String[] headers) {
        Row headerRow = sheet.createRow(rowIdx);
        XSSFCellStyle style = createHeaderStyle(wb);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(style);
        }
    }

    private XSSFCellStyle createHeaderStyle(XSSFWorkbook wb) {
        XSSFCellStyle style = wb.createCellStyle();
        XSSFFont font = wb.createFont();
        font.setBold(true);
        font.setColor(new XSSFColor(Color.WHITE, null));
        style.setFont(font);
        style.setFillForegroundColor(new XSSFColor(new Color(26, 107, 42), null));
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }

    private void autoSizeColumns(XSSFSheet sheet, int count) {
        for (int i = 0; i < count; i++) sheet.autoSizeColumn(i);
    }

    private void addStockQuantityChart(XSSFWorkbook wb, XSSFSheet sheet, List<StockItem> items,
                                       int startRow, Map<String, String> designations) {
        if (items.isEmpty()) return;
        int maxItems = Math.min(items.size(), 15);
        XSSFDrawing drawing = sheet.createDrawingPatriarch();
        XSSFClientAnchor anchor = drawing.createAnchor(0, 0, 0, 0, 9, startRow, 20, startRow + 20);

        XSSFChart chart = drawing.createChart(anchor);
        chart.setTitleText("Quantités par produit");
        chart.setTitleOverlay(false);

        XSSFSheet dataSheet = wb.createSheet("_chart_data_" + System.currentTimeMillis());
        Row dataHeader = dataSheet.createRow(0);
        dataHeader.createCell(0).setCellValue("Produit");
        dataHeader.createCell(1).setCellValue("Quantité");
        for (int i = 0; i < maxItems; i++) {
            StockItem item = items.get(i);
            Row r = dataSheet.createRow(i + 1);
            r.createCell(0).setCellValue(item.getProductSku());
            r.createCell(1).setCellValue(item.getQuantity().doubleValue());
        }

        XDDFDataSource<String> cats = XDDFDataSourcesFactory.fromStringCellRange(dataSheet, new CellRangeAddress(1, maxItems, 0, 0));
        XDDFNumericalDataSource<Double> vals = XDDFDataSourcesFactory.fromNumericCellRange(dataSheet, new CellRangeAddress(1, maxItems, 1, 1));
        XDDFBarChartData bar = (XDDFBarChartData) chart.createData(ChartTypes.BAR, null, null);
        bar.setBarDirection(BarDirection.COL);
        XDDFChartData.Series series = bar.addSeries(cats, vals);
        series.setTitle("Quantité", null);
        chart.plot(bar);
    }

    private void addMovementTypeChart(XSSFWorkbook wb, XSSFSheet sheet, List<StockMovement> movements, int startRow) {
        if (movements.isEmpty()) return;
        Map<String, Long> typeCounts = movements.stream()
            .collect(Collectors.groupingBy(m -> m.getType() != null ? m.getType().toString() : "Inconnu", Collectors.counting()));

        XSSFDrawing drawing = sheet.createDrawingPatriarch();
        XSSFClientAnchor anchor = drawing.createAnchor(0, 0, 0, 0, 8, startRow, 18, startRow + 18);

        XSSFChart chart = drawing.createChart(anchor);
        chart.setTitleText("Mouvements par type");
        chart.setTitleOverlay(false);

        XSSFSheet dataSheet = wb.createSheet("_chart_move_" + System.currentTimeMillis());
        Row dataHeader = dataSheet.createRow(0);
        dataHeader.createCell(0).setCellValue("Type");
        dataHeader.createCell(1).setCellValue("Nombre");
        int i = 0;
        for (Map.Entry<String, Long> e : typeCounts.entrySet()) {
            Row r = dataSheet.createRow(i + 1);
            r.createCell(0).setCellValue(e.getKey());
            r.createCell(1).setCellValue(e.getValue().doubleValue());
            i++;
        }

        int dataCount = typeCounts.size();
        XDDFDataSource<String> cats = XDDFDataSourcesFactory.fromStringCellRange(dataSheet, new CellRangeAddress(1, dataCount, 0, 0));
        XDDFNumericalDataSource<Double> vals = XDDFDataSourcesFactory.fromNumericCellRange(dataSheet, new CellRangeAddress(1, dataCount, 1, 1));
        XDDFBarChartData bar = (XDDFBarChartData) chart.createData(ChartTypes.BAR, null, null);
        bar.setBarDirection(BarDirection.COL);
        XDDFChartData.Series series = bar.addSeries(cats, vals);
        series.setTitle("Nombre", null);
        chart.plot(bar);
    }

    private void addValorisationChart(XSSFWorkbook wb, XSSFSheet sheet, List<StockItem> items,
                                      int startRow, Map<String, String> designations) {
        if (items.isEmpty()) return;
        int maxItems = Math.min(items.size(), 15);
        XSSFDrawing drawing = sheet.createDrawingPatriarch();
        XSSFClientAnchor anchor = drawing.createAnchor(0, 0, 0, 0, 7, startRow, 18, startRow + 20);

        XSSFChart chart = drawing.createChart(anchor);
        chart.setTitleText("Poids total par produit");
        chart.setTitleOverlay(false);

        XSSFSheet dataSheet = wb.createSheet("_chart_val_" + System.currentTimeMillis());
        Row dataHeader = dataSheet.createRow(0);
        dataHeader.createCell(0).setCellValue("Produit");
        dataHeader.createCell(1).setCellValue("Poids total");
        for (int i = 0; i < maxItems; i++) {
            StockItem item = items.get(i);
            Row r = dataSheet.createRow(i + 1);
            r.createCell(0).setCellValue(item.getProductSku());
            BigDecimal totalWeight = item.getUnitWeight() != null ? item.getUnitWeight().multiply(item.getQuantity()) : BigDecimal.ZERO;
            r.createCell(1).setCellValue(totalWeight.doubleValue());
        }

        XDDFDataSource<String> cats = XDDFDataSourcesFactory.fromStringCellRange(dataSheet, new CellRangeAddress(1, maxItems, 0, 0));
        XDDFNumericalDataSource<Double> vals = XDDFDataSourcesFactory.fromNumericCellRange(dataSheet, new CellRangeAddress(1, maxItems, 1, 1));
        XDDFBarChartData bar = (XDDFBarChartData) chart.createData(ChartTypes.BAR, null, null);
        bar.setBarDirection(BarDirection.COL);
        XDDFChartData.Series series = bar.addSeries(cats, vals);
        series.setTitle("Poids total", null);
        chart.plot(bar);
    }
}
