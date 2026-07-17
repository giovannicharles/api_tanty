package com.NTFOODS.Api_tanty.modules.stock.application.service;

import com.NTFOODS.Api_tanty.modules.stock.domain.common.entity.StockItem;
import com.NTFOODS.Api_tanty.modules.stock.domain.common.entity.StockMovement;
import com.NTFOODS.Api_tanty.modules.stock.domain.dotation.entity.DotationRequest;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.draw.LineSeparator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * PdfReportService - Génère des PDFs professionnels avec logo, motif, tableaux et signatures
 */
@Service
public class PdfReportService {

  private static final Logger log = LoggerFactory.getLogger(PdfReportService.class);
  private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
  private static final DateTimeFormatter DATETIME_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy à HH:mm");

  // Black & white palette for printable reports
  private static final Color PRIMARY_COLOR = Color.BLACK;             // #000000
  private static final Color SECONDARY_COLOR = new Color(40, 40, 40); // dark gray
  private static final Color LIGHT_BG = new Color(245, 245, 245);     // #f5f5f5
  private static final Color HEADER_BG = Color.BLACK;                 // #000000
  private static final Color ALT_ROW = new Color(249, 249, 249);      // #f9f9f9
  private static final Color BORDER_COLOR = new Color(180, 180, 180); // #b4b4b4

  // Ordre officiel des codes produits TANTY / NT Foods (cf. LISTE DES CODES DES PRODUITS)
  private static final List<String> OFFICIAL_TANTY_CODES = List.of(
    "BBV 1L", "BBV 2L", "GGASS", "MGAE", "MGASS", "MGCA", "MGCR", "MGPL",
    "MOGAE", "MOGAS", "MOGPL", "PRBSA", "PRASS/RASP", "PTASS", "PTBSA", "PTBSL",
    "PTBSN", "PTBSP", "PTBSPA", "RASS", "RBSA", "RBSC", "RBSL", "RBSM", "RBSP",
    "RCP 1L", "RCP(2L)", "RCP 5L", "TASS", "TBSA", "TBSL", "TBSN", "TBSP", "TBSPA",
    "TC 10L", "TC 1L", "TC 2L", "TC 5L", "TCH", "TCP 2L", "TCP 5L", "THBP", "TFS"
  );

  // ── Public API ──────────────────────────────────────────────

  /**
   * Génère une fiche de synthèse vierge au format NTFoods (INFOS PRODUITS TANTY).
   * Seul le motif change. Les quantités sont laissées vierges pour être remplies à la main.
   */
  public byte[] generateFicheSyntheseNTFoods(List<String> codes, String motif,
                                             String nom, String ville, String zone,
                                             Integer nombreColis) throws IOException {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    Document doc = new Document(PageSize.A4, 30, 30, 30, 30);
    PdfWriter writer = PdfWriter.getInstance(doc, baos);
    doc.open();

    addNTFoodsHeader(doc, nom, ville, zone, nombreColis);
    addMotifCheckboxes(doc, motif);
    addNTFoodsCodesGrid(doc, codes);
    addNTFoodsSignatures(doc);

    doc.close();
    writer.close();
    return baos.toByteArray();
  }

  /**
   * Génère un PDF "Fiche de Synthèse" pour les dotations
   * Tableau code + quantité, divisé en deux colonnes si beaucoup de codes
   */
  public byte[] generateDotationFicheSynthese(DotationRequest dotation) throws IOException {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    Document doc = new Document(PageSize.A4, 36, 36, 50, 50);
    PdfWriter writer = PdfWriter.getInstance(doc, baos);
    doc.open();

    addHeader(doc, "FICHE DE SYNTHÈSE - DOTATION", dotation.getReferenceNumber() != null ? dotation.getReferenceNumber() : "Dotation");
    addDotationInfo(doc, dotation);
    addDotationItemsTable(doc, dotation);
    addSignatures(doc, new String[]{"Le Commercial", "Le Secrétaire", "Le Comptable", "Le Gestionnaire"});

    doc.close();
    writer.close();
    return baos.toByteArray();
  }

  /**
   * Génère un PDF pour les items de stock
   */
  public byte[] generateStockItemsPdf(List<StockItem> items, String locationName, String motif) throws IOException {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    Document doc = new Document(PageSize.A4, 36, 36, 50, 50);
    PdfWriter writer = PdfWriter.getInstance(doc, baos);
    doc.open();

    addHeader(doc, "RAPPORT DE STOCK - " + locationName.toUpperCase(), motif);
    addStockItemsTable(doc, items);
    addSignatures(doc, new String[]{"Gestionnaire Stock", "Directeur"});

    doc.close();
    writer.close();
    return baos.toByteArray();
  }

  /**
   * Fiche de synthèse stock central avec quantités sorties par produit
   */
  public byte[] generateFicheSyntheseStock(List<StockItem> items, List<StockMovement> movements,
                                           String locationName, String motif) throws IOException {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    Document doc = new Document(PageSize.A4, 36, 36, 50, 50);
    PdfWriter writer = PdfWriter.getInstance(doc, baos);
    doc.open();

    addHeader(doc, "FICHE DE SYNTHÈSE - STOCK " + locationName.toUpperCase(), motif);

    // Tableau principal: Code, Désignation, Qté en stock, Qté sortie
    addStockWithExitsTable(doc, items, movements);

    addSignatures(doc, new String[]{"Gestionnaire Stock", "Comptable", "Directeur"});

    doc.close();
    writer.close();
    return baos.toByteArray();
  }

  /**
   * Fiche de synthèse des sorties par produit (quantités sorties détaillées)
   */
  public byte[] generateFicheSyntheseSorties(List<StockMovement> movements, String motif,
                                             LocalDateTime periodStart, LocalDateTime periodEnd) throws IOException {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    Document doc = new Document(PageSize.A4, 36, 36, 50, 50);
    PdfWriter writer = PdfWriter.getInstance(doc, baos);
    doc.open();

    String periodStr = periodStart.format(DATE_FMT) + " au " + periodEnd.format(DATE_FMT);
    addHeader(doc, "FICHE DE SYNTHÈSE - SORTIES DE STOCK", motif + " | Période: " + periodStr);

    // Filtrer seulement les sorties (transferts vers mobile, ventes, pertes)
    List<StockMovement> exits = movements.stream()
      .filter(m -> isExitMovement(m))
      .collect(Collectors.toList());

    addExitsByProductTable(doc, exits);

    addSignatures(doc, new String[]{"Gestionnaire Stock", "Comptable", "Directeur"});

    doc.close();
    writer.close();
    return baos.toByteArray();
  }

  /**
   * Fiche de synthèse des entrées par produit
   */
  public byte[] generateFicheSyntheseEntrees(List<StockMovement> movements, String motif,
                                             LocalDateTime periodStart, LocalDateTime periodEnd) throws IOException {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    Document doc = new Document(PageSize.A4, 36, 36, 50, 50);
    PdfWriter writer = PdfWriter.getInstance(doc, baos);
    doc.open();

    String periodStr = periodStart.format(DATE_FMT) + " au " + periodEnd.format(DATE_FMT);
    addHeader(doc, "FICHE DE SYNTHÈSE - ENTRÉES DE STOCK", motif + " | Période: " + periodStr);

    List<StockMovement> entries = movements.stream()
      .filter(m -> isEntryMovement(m))
      .collect(Collectors.toList());

    addEntriesByProductTable(doc, entries);

    addSignatures(doc, new String[]{"Gestionnaire Stock", "Comptable", "Directeur"});

    doc.close();
    writer.close();
    return baos.toByteArray();
  }

  /**
   * Fiche de synthèse globale (entrées + sorties + stock restant)
   */
  public byte[] generateFicheSyntheseGlobale(List<StockItem> items, List<StockMovement> movements,
                                             String motif, LocalDateTime periodStart,
                                             LocalDateTime periodEnd) throws IOException {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    Document doc = new Document(PageSize.A4, 36, 36, 50, 50);
    PdfWriter writer = PdfWriter.getInstance(doc, baos);
    doc.open();

    String periodStr = periodStart.format(DATE_FMT) + " au " + periodEnd.format(DATE_FMT);
    addHeader(doc, "FICHE DE SYNTHÈSE GLOBALE - STOCK", motif + " | Période: " + periodStr);

    // Résumé
    addSummarySection(doc, items, movements, periodStart, periodEnd);

    // Tableau détaillé: Code, Qté entrée, Qté sortie, Qté stock
    addGlobalSynthesisTable(doc, items, movements, periodStart, periodEnd);

    addSignatures(doc, new String[]{"Gestionnaire Stock", "Comptable", "Directeur"});

    doc.close();
    writer.close();
    return baos.toByteArray();
  }

  /**
   * Génère un PDF pour les mouvements de stock
   */
  public byte[] generateStockMovementsPdf(List<StockMovement> movements, String motif) throws IOException {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    Document doc = new Document(PageSize.A4, 36, 36, 50, 50);
    PdfWriter writer = PdfWriter.getInstance(doc, baos);
    doc.open();

    addHeader(doc, "RAPPORT DES MOUVEMENTS DE STOCK", motif);
    addMovementsTable(doc, movements);
    addSignatures(doc, new String[]{"Gestionnaire Stock", "Directeur"});

    doc.close();
    writer.close();
    return baos.toByteArray();
  }

  /**
   * Rapport de valorisation du stock — Code, Désignation, Qté, Poids unit., Poids total, Conditionnement
   */
  public byte[] generateRapportValorisation(List<StockItem> items, Map<String, String> designations,
                                             String locationName, String motif) throws IOException {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    Document doc = new Document(PageSize.A4, 36, 36, 50, 50);
    PdfWriter writer = PdfWriter.getInstance(doc, baos);
    doc.open();

    addHeader(doc, "RAPPORT DE VALORISATION - " + locationName.toUpperCase(), motif);
    addValorisationTable(doc, items, designations);
    addSignatures(doc, new String[]{"Gestionnaire Stock", "Comptable", "Directeur"});

    doc.close();
    writer.close();
    return baos.toByteArray();
  }

  /**
   * Rapport d'alertes — produits sous le seuil de réappro ou stock de sécurité
   */
  public byte[] generateRapportAlertes(List<StockItem> items, Map<String, String> designations,
                                        String locationName) throws IOException {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    Document doc = new Document(PageSize.A4, 36, 36, 50, 50);
    PdfWriter writer = PdfWriter.getInstance(doc, baos);
    doc.open();

    addHeader(doc, "RAPPORT D'ALERTES DE STOCK - " + locationName.toUpperCase(), "Seuils de réapprovisionnement");

    List<StockItem> alertItems = items.stream()
      .filter(i -> {
        BigDecimal qty = i.getQuantity();
        BigDecimal reorder = i.getReorderPoint();
        BigDecimal safety = i.getSafetyStock();
        return (reorder != null && qty != null && qty.compareTo(reorder) <= 0)
            || (safety != null && qty != null && qty.compareTo(safety) <= 0);
      })
      .collect(Collectors.toList());

    addAlertesTable(doc, alertItems, designations);
    addSignatures(doc, new String[]{"Gestionnaire Stock", "Directeur"});

    doc.close();
    writer.close();
    return baos.toByteArray();
  }

  /**
   * Inventaire complet — Code, Désignation, Qté, Cartons, Poids, Volume, Conditionnement, Seuils
   */
  public byte[] generateInventaireComplet(List<StockItem> items, Map<String, String> designations,
                                          String locationName, String motif) throws IOException {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    Document doc = new Document(PageSize.A4, 30, 30, 40, 40);
    PdfWriter writer = PdfWriter.getInstance(doc, baos);
    doc.open();

    addHeader(doc, "INVENTAIRE COMPLET - " + locationName.toUpperCase(), motif);
    addInventaireCompletTable(doc, items, designations);
    addSignatures(doc, new String[]{"Gestionnaire Stock", "Comptable", "Directeur"});

    doc.close();
    writer.close();
    return baos.toByteArray();
  }

  /**
   * Rapport de rotation du stock — Code, Qté entrée, Qté sortie, Qté stock, Taux de rotation
   */
  public byte[] generateRapportRotation(List<StockItem> items, List<StockMovement> movements,
                                        Map<String, String> designations,
                                        String locationName, String motif,
                                        LocalDateTime periodStart, LocalDateTime periodEnd) throws IOException {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    Document doc = new Document(PageSize.A4, 36, 36, 50, 50);
    PdfWriter writer = PdfWriter.getInstance(doc, baos);
    doc.open();

    String periodStr = periodStart.format(DATE_FMT) + " au " + periodEnd.format(DATE_FMT);
    addHeader(doc, "RAPPORT DE ROTATION DU STOCK - " + locationName.toUpperCase(), motif + " | Période: " + periodStr);
    addRotationTable(doc, items, movements, designations);
    addSignatures(doc, new String[]{"Gestionnaire Stock", "Comptable", "Directeur"});

    doc.close();
    writer.close();
    return baos.toByteArray();
  }

  // ── Valorisation table ──────────────────────────────────────

  private void addValorisationTable(Document doc, List<StockItem> items, Map<String, String> designations) throws IOException {
    Paragraph title = new Paragraph("Valorisation du stock par produit",
      new Font(Font.HELVETICA, 11, Font.BOLD, PRIMARY_COLOR));
    doc.add(title);
    doc.add(Chunk.NEWLINE);

    if (items == null || items.isEmpty()) {
      doc.add(new Paragraph("Aucun produit en stock.", new Font(Font.HELVETICA, 10, Font.ITALIC, Color.GRAY)));
      return;
    }

    PdfPTable table = new PdfPTable(6);
    table.setWidthPercentage(100);
    table.setWidths(new float[]{2f, 3f, 1f, 1.2f, 1.2f, 1.5f});

    String[] headers = {"Code", "Désignation", "Qté", "Poids unit. (g)", "Poids total (g)", "Conditionnement"};
    for (String h : headers) {
      PdfPCell cell = new PdfPCell(new Phrase(h, new Font(Font.HELVETICA, 8, Font.BOLD, Color.WHITE)));
      cell.setBackgroundColor(HEADER_BG);
      cell.setBorderColor(BORDER_COLOR);
      cell.setHorizontalAlignment(Element.ALIGN_CENTER);
      cell.setPadding(5);
      table.addCell(cell);
    }

    BigDecimal totalWeight = BigDecimal.ZERO;
    for (int i = 0; i < items.size(); i++) {
      StockItem item = items.get(i);
      boolean altRow = (i % 2 == 1);
      Color rowBg = altRow ? ALT_ROW : Color.WHITE;
      Font dataFont = new Font(Font.HELVETICA, 8, Font.NORMAL, Color.DARK_GRAY);

      addTableCell(table, item.getProductSku() != null ? item.getProductSku() : "—", rowBg, dataFont, Element.ALIGN_LEFT);
      addTableCell(table, designations.getOrDefault(item.getProductSku(), "—"), rowBg, dataFont, Element.ALIGN_LEFT);
      addTableCell(table, String.valueOf(item.getQuantity()), rowBg, dataFont, Element.ALIGN_CENTER);

      String unitWeight = item.getUnitWeight() != null ? String.valueOf(item.getUnitWeight()) : "—";
      addTableCell(table, unitWeight, rowBg, dataFont, Element.ALIGN_CENTER);

      BigDecimal totalItemWeight = (item.getUnitWeight() != null && item.getQuantity() != null)
        ? item.getUnitWeight().multiply(item.getQuantity()) : BigDecimal.ZERO;
      totalWeight = totalWeight.add(totalItemWeight);
      addTableCell(table, String.valueOf(totalItemWeight), rowBg, dataFont, Element.ALIGN_CENTER);

      addTableCell(table, item.getPackagingType() != null ? item.getPackagingType() : "—", rowBg, dataFont, Element.ALIGN_LEFT);
    }

    doc.add(table);
    doc.add(Chunk.NEWLINE);
    Paragraph totalPara = new Paragraph();
    totalPara.add(new Phrase("Total références: " + items.size() + " | Poids total du stock: " + totalWeight + " g",
      new Font(Font.HELVETICA, 10, Font.BOLD, PRIMARY_COLOR)));
    doc.add(totalPara);
  }

  // ── Alertes table ───────────────────────────────────────────

  private void addAlertesTable(Document doc, List<StockItem> alertItems, Map<String, String> designations) throws IOException {
    Paragraph title = new Paragraph("Produits sous le seuil de réapprovisionnement ou de sécurité",
      new Font(Font.HELVETICA, 11, Font.BOLD, PRIMARY_COLOR));
    doc.add(title);
    doc.add(Chunk.NEWLINE);

    if (alertItems == null || alertItems.isEmpty()) {
      doc.add(new Paragraph("Aucune alerte de stock. Tous les produits sont au-dessus des seuils.",
        new Font(Font.HELVETICA, 10, Font.ITALIC, Color.GRAY)));
      return;
    }

    PdfPTable table = new PdfPTable(6);
    table.setWidthPercentage(100);
    table.setWidths(new float[]{2f, 3f, 1f, 1.2f, 1.2f, 1.2f});

    String[] headers = {"Code", "Désignation", "Qté actuelle", "Seuil réappro.", "Stock sécurité", "Niveau alerte"};
    for (String h : headers) {
      PdfPCell cell = new PdfPCell(new Phrase(h, new Font(Font.HELVETICA, 8, Font.BOLD, Color.WHITE)));
      cell.setBackgroundColor(HEADER_BG);
      cell.setBorderColor(BORDER_COLOR);
      cell.setHorizontalAlignment(Element.ALIGN_CENTER);
      cell.setPadding(5);
      table.addCell(cell);
    }

    for (int i = 0; i < alertItems.size(); i++) {
      StockItem item = alertItems.get(i);
      boolean altRow = (i % 2 == 1);
      Color rowBg = altRow ? ALT_ROW : Color.WHITE;
      Font dataFont = new Font(Font.HELVETICA, 8, Font.NORMAL, Color.DARK_GRAY);

      addTableCell(table, item.getProductSku() != null ? item.getProductSku() : "—", rowBg, dataFont, Element.ALIGN_LEFT);
      addTableCell(table, designations.getOrDefault(item.getProductSku(), "—"), rowBg, dataFont, Element.ALIGN_LEFT);
      addTableCell(table, String.valueOf(item.getQuantity()), rowBg, dataFont, Element.ALIGN_CENTER);
      addTableCell(table, item.getReorderPoint() != null ? String.valueOf(item.getReorderPoint()) : "—", rowBg, dataFont, Element.ALIGN_CENTER);
      addTableCell(table, item.getSafetyStock() != null ? String.valueOf(item.getSafetyStock()) : "—", rowBg, dataFont, Element.ALIGN_CENTER);

      String alertLevel;
      if (item.getSafetyStock() != null && item.getQuantity() != null && item.getQuantity().compareTo(item.getSafetyStock()) <= 0) {
        alertLevel = "CRITIQUE";
      } else {
        alertLevel = "FAIBLE";
      }
      addTableCell(table, alertLevel, rowBg, dataFont, Element.ALIGN_CENTER);
    }

    doc.add(table);
    doc.add(Chunk.NEWLINE);
    Paragraph totalPara = new Paragraph();
    totalPara.add(new Phrase("Total produits en alerte: " + alertItems.size(),
      new Font(Font.HELVETICA, 10, Font.BOLD, PRIMARY_COLOR)));
    doc.add(totalPara);
  }

  // ── Inventaire complet table ────────────────────────────────

  private void addInventaireCompletTable(Document doc, List<StockItem> items, Map<String, String> designations) throws IOException {
    Paragraph title = new Paragraph("Inventaire détaillé de tous les produits",
      new Font(Font.HELVETICA, 11, Font.BOLD, PRIMARY_COLOR));
    doc.add(title);
    doc.add(Chunk.NEWLINE);

    if (items == null || items.isEmpty()) {
      doc.add(new Paragraph("Aucun produit en stock.", new Font(Font.HELVETICA, 10, Font.ITALIC, Color.GRAY)));
      return;
    }

    PdfPTable table = new PdfPTable(9);
    table.setWidthPercentage(100);
    table.setWidths(new float[]{1.8f, 2.5f, 0.9f, 0.9f, 0.9f, 0.8f, 1.2f, 1f, 1f});

    String[] headers = {"Code", "Désignation", "Qté", "Cartons", "Poids (g)", "Volume", "Conditionnement", "Seuil réappro.", "Stock séc."};
    for (String h : headers) {
      PdfPCell cell = new PdfPCell(new Phrase(h, new Font(Font.HELVETICA, 7, Font.BOLD, Color.WHITE)));
      cell.setBackgroundColor(HEADER_BG);
      cell.setBorderColor(BORDER_COLOR);
      cell.setHorizontalAlignment(Element.ALIGN_CENTER);
      cell.setPadding(4);
      table.addCell(cell);
    }

    for (int i = 0; i < items.size(); i++) {
      StockItem item = items.get(i);
      boolean altRow = (i % 2 == 1);
      Color rowBg = altRow ? ALT_ROW : Color.WHITE;
      Font dataFont = new Font(Font.HELVETICA, 7, Font.NORMAL, Color.DARK_GRAY);

      addTableCell(table, item.getProductSku() != null ? item.getProductSku() : "—", rowBg, dataFont, Element.ALIGN_LEFT);
      addTableCell(table, designations.getOrDefault(item.getProductSku(), "—"), rowBg, dataFont, Element.ALIGN_LEFT);
      addTableCell(table, String.valueOf(item.getQuantity()), rowBg, dataFont, Element.ALIGN_CENTER);
      addTableCell(table, String.valueOf(item.calculateCartons()), rowBg, dataFont, Element.ALIGN_CENTER);
      addTableCell(table, item.getUnitWeight() != null ? String.valueOf(item.getUnitWeight()) : "—", rowBg, dataFont, Element.ALIGN_CENTER);
      addTableCell(table, item.getVolume() != null ? item.getVolume() : "—", rowBg, dataFont, Element.ALIGN_CENTER);
      addTableCell(table, item.getPackagingType() != null ? item.getPackagingType() : "—", rowBg, dataFont, Element.ALIGN_LEFT);
      addTableCell(table, item.getReorderPoint() != null ? String.valueOf(item.getReorderPoint()) : "—", rowBg, dataFont, Element.ALIGN_CENTER);
      addTableCell(table, item.getSafetyStock() != null ? String.valueOf(item.getSafetyStock()) : "—", rowBg, dataFont, Element.ALIGN_CENTER);
    }

    doc.add(table);
    doc.add(Chunk.NEWLINE);
    Paragraph totalPara = new Paragraph();
    totalPara.add(new Phrase("Total références: " + items.size(),
      new Font(Font.HELVETICA, 10, Font.BOLD, PRIMARY_COLOR)));
    doc.add(totalPara);
  }

  // ── Rotation table ──────────────────────────────────────────

  private void addRotationTable(Document doc, List<StockItem> items, List<StockMovement> movements,
                                 Map<String, String> designations) throws IOException {
    Paragraph title = new Paragraph("Rotation du stock par produit (entrées / sorties / stock)",
      new Font(Font.HELVETICA, 11, Font.BOLD, PRIMARY_COLOR));
    doc.add(title);
    doc.add(Chunk.NEWLINE);

    if (items == null || items.isEmpty()) {
      doc.add(new Paragraph("Aucun produit en stock.", new Font(Font.HELVETICA, 10, Font.ITALIC, Color.GRAY)));
      return;
    }

    Map<String, BigDecimal> entryQtyBySku = new HashMap<>();
    Map<String, BigDecimal> exitQtyBySku = new HashMap<>();
    if (movements != null) {
      for (StockMovement m : movements) {
        if (m.getProductSku() == null) continue;
        if (isEntryMovement(m)) {
          entryQtyBySku.merge(m.getProductSku(), m.getQuantity(), BigDecimal::add);
        } else if (isExitMovement(m)) {
          exitQtyBySku.merge(m.getProductSku(), m.getQuantity(), BigDecimal::add);
        }
      }
    }

    PdfPTable table = new PdfPTable(6);
    table.setWidthPercentage(100);
    table.setWidths(new float[]{2f, 3f, 1.2f, 1.2f, 1.2f, 1.2f});

    String[] headers = {"Code", "Désignation", "Qté entrée", "Qté sortie", "Qté stock", "Taux rotation"};
    for (String h : headers) {
      PdfPCell cell = new PdfPCell(new Phrase(h, new Font(Font.HELVETICA, 8, Font.BOLD, Color.WHITE)));
      cell.setBackgroundColor(HEADER_BG);
      cell.setBorderColor(BORDER_COLOR);
      cell.setHorizontalAlignment(Element.ALIGN_CENTER);
      cell.setPadding(5);
      table.addCell(cell);
    }

    for (int i = 0; i < items.size(); i++) {
      StockItem item = items.get(i);
      boolean altRow = (i % 2 == 1);
      Color rowBg = altRow ? ALT_ROW : Color.WHITE;
      Font dataFont = new Font(Font.HELVETICA, 8, Font.NORMAL, Color.DARK_GRAY);

      addTableCell(table, item.getProductSku() != null ? item.getProductSku() : "—", rowBg, dataFont, Element.ALIGN_LEFT);
      addTableCell(table, designations.getOrDefault(item.getProductSku(), "—"), rowBg, dataFont, Element.ALIGN_LEFT);

      BigDecimal entryQty = entryQtyBySku.getOrDefault(item.getProductSku(), BigDecimal.ZERO);
      BigDecimal exitQty = exitQtyBySku.getOrDefault(item.getProductSku(), BigDecimal.ZERO);
      addTableCell(table, String.valueOf(entryQty), rowBg, dataFont, Element.ALIGN_CENTER);
      addTableCell(table, String.valueOf(exitQty), rowBg, dataFont, Element.ALIGN_CENTER);
      addTableCell(table, String.valueOf(item.getQuantity()), rowBg, dataFont, Element.ALIGN_CENTER);

      String rotationRate;
      if (item.getQuantity() != null && item.getQuantity().compareTo(BigDecimal.ZERO) > 0 && exitQty.compareTo(BigDecimal.ZERO) > 0) {
        BigDecimal rate = exitQty.divide(item.getQuantity(), 2, java.math.RoundingMode.HALF_UP);
        rotationRate = rate + "x";
      } else {
        rotationRate = "—";
      }
      addTableCell(table, rotationRate, rowBg, dataFont, Element.ALIGN_CENTER);
    }

    doc.add(table);
    doc.add(Chunk.NEWLINE);
    BigDecimal totalEntry = entryQtyBySku.values().stream().reduce(BigDecimal.ZERO, BigDecimal::add);
    BigDecimal totalExit = exitQtyBySku.values().stream().reduce(BigDecimal.ZERO, BigDecimal::add);
    Paragraph totalPara = new Paragraph();
    totalPara.add(new Phrase("Total références: " + items.size() + " | Total entrées: " + totalEntry + " | Total sorties: " + totalExit,
      new Font(Font.HELVETICA, 10, Font.BOLD, PRIMARY_COLOR)));
    doc.add(totalPara);
  }

  // ── Generic table cell helper ───────────────────────────────

  private void addTableCell(PdfPTable table, String text, Color bg, Font font, int align) {
    PdfPCell cell = new PdfPCell(new Phrase(text, font));
    cell.setBackgroundColor(bg);
    cell.setBorderColor(BORDER_COLOR);
    cell.setPadding(4);
    cell.setHorizontalAlignment(align);
    table.addCell(cell);
  }

  // ── Movement type helpers ──────────────────────────────────

  private boolean isExitMovement(StockMovement m) {
    String type = m.getType() != null ? m.getType().name() : "";
    return type.equals("TRANSFER_BUFFER_TO_MOBILE") || type.equals("SALE")
      || type.equals("LOSS") || type.equals("EXPIRATION") || type.equals("ADJUSTMENT");
  }

  private boolean isEntryMovement(StockMovement m) {
    String type = m.getType() != null ? m.getType().name() : "";
    return type.equals("RECEPTION_PRODUCTION") || type.equals("RECEPTION_CONSOMMABLE")
      || type.equals("RECEPTION_RAW_MATERIAL") || type.equals("TRANSFER_MOBILE_TO_CENTRAL");
  }

  private String movementTypeLabel(String type) {
    switch (type) {
      case "RECEPTION_PRODUCTION": return "Réception Production";
      case "RECEPTION_CONSOMMABLE": return "Réception Consommable";
      case "RECEPTION_RAW_MATERIAL": return "Réception Mat. Première";
      case "TRANSFER_CENTRAL_TO_BUFFER": return "Transfert Central→Tampon";
      case "TRANSFER_BUFFER_TO_MOBILE": return "Dotation/Sortie";
      case "TRANSFER_MOBILE_TO_CENTRAL": return "Retour Mobile→Central";
      case "SALE": return "Vente";
      case "ADJUSTMENT": return "Ajustement";
      case "LOSS": return "Perte";
      case "EXPIRATION": return "Expiration";
      default: return type;
    }
  }

  // ── Stock with exits table (Code, Désignation, Qté stock, Qté sortie) ──

  private void addStockWithExitsTable(Document doc, List<StockItem> items, List<StockMovement> movements) throws IOException {
    Paragraph title = new Paragraph("État du stock avec quantités sorties", new Font(Font.HELVETICA, 11, Font.BOLD, PRIMARY_COLOR));
    doc.add(title);
    doc.add(Chunk.NEWLINE);

    if (items == null || items.isEmpty()) {
      doc.add(new Paragraph("Aucun produit en stock.", new Font(Font.HELVETICA, 10, Font.ITALIC, Color.GRAY)));
      return;
    }

    // Calculer les quantités sorties par SKU
    Map<String, BigDecimal> exitQtyBySku = new HashMap<>();
    if (movements != null) {
      for (StockMovement m : movements) {
        if (isExitMovement(m) && m.getProductSku() != null) {
          exitQtyBySku.merge(m.getProductSku(), m.getQuantity(), BigDecimal::add);
        }
      }
    }

    int totalItems = items.size();
    int half = (int) Math.ceil(totalItems / 2.0);

    if (totalItems <= 12) {
      doc.add(createStockWithExitsSingleColumn(items, exitQtyBySku, 0, totalItems));
    } else {
      PdfPTable outerTable = new PdfPTable(2);
      outerTable.setWidthPercentage(100);
      outerTable.setWidths(new float[]{1f, 1f});

      PdfPCell leftCell = new PdfPCell();
      leftCell.setBorder(Rectangle.NO_BORDER);
      leftCell.setPaddingRight(5);
      leftCell.addElement(createStockWithExitsSingleColumn(items, exitQtyBySku, 0, half));
      outerTable.addCell(leftCell);

      PdfPCell rightCell = new PdfPCell();
      rightCell.setBorder(Rectangle.NO_BORDER);
      rightCell.setPaddingLeft(5);
      rightCell.addElement(createStockWithExitsSingleColumn(items, exitQtyBySku, half, totalItems));
      outerTable.addCell(rightCell);

      doc.add(outerTable);
    }

    doc.add(Chunk.NEWLINE);
    BigDecimal totalStock = items.stream().map(StockItem::getQuantity).reduce(BigDecimal.ZERO, BigDecimal::add);
    BigDecimal totalExit = exitQtyBySku.values().stream().reduce(BigDecimal.ZERO, BigDecimal::add);
    Paragraph totalPara = new Paragraph();
    totalPara.add(new Phrase("Total produits: " + totalItems + " | Total en stock: " + totalStock + " | Total sorties: " + totalExit,
      new Font(Font.HELVETICA, 10, Font.BOLD, PRIMARY_COLOR)));
    doc.add(totalPara);
  }

  private PdfPTable createStockWithExitsSingleColumn(List<StockItem> items, Map<String, BigDecimal> exitQtyBySku,
                                                      int start, int end) {
    PdfPTable table = new PdfPTable(3);
    table.setWidthPercentage(100);
    table.setWidths(new float[]{3f, 1.2f, 1.2f});

    String[] headers = {"Code", "Qté Stock", "Qté Sortie"};
    for (String h : headers) {
      PdfPCell cell = new PdfPCell(new Phrase(h, new Font(Font.HELVETICA, 9, Font.BOLD, Color.WHITE)));
      cell.setBackgroundColor(HEADER_BG);
      cell.setBorderColor(BORDER_COLOR);
      cell.setHorizontalAlignment(Element.ALIGN_CENTER);
      cell.setPadding(5);
      table.addCell(cell);
    }

    for (int i = start; i < end; i++) {
      StockItem item = items.get(i);
      boolean altRow = (i % 2 == 1);
      Color rowBg = altRow ? ALT_ROW : Color.WHITE;
      Font dataFont = new Font(Font.HELVETICA, 8, Font.NORMAL, Color.DARK_GRAY);

      PdfPCell codeCell = new PdfPCell(new Phrase(item.getProductSku() != null ? item.getProductSku() : "—", dataFont));
      codeCell.setBackgroundColor(rowBg);
      codeCell.setBorderColor(BORDER_COLOR);
      codeCell.setPadding(4);
      table.addCell(codeCell);

      PdfPCell stockCell = new PdfPCell(new Phrase(String.valueOf(item.getQuantity()), dataFont));
      stockCell.setBackgroundColor(rowBg);
      stockCell.setBorderColor(BORDER_COLOR);
      stockCell.setHorizontalAlignment(Element.ALIGN_CENTER);
      stockCell.setPadding(4);
      table.addCell(stockCell);

      BigDecimal exitQty = exitQtyBySku.getOrDefault(item.getProductSku(), BigDecimal.ZERO);
      PdfPCell exitCell = new PdfPCell(new Phrase(String.valueOf(exitQty), dataFont));
      exitCell.setBackgroundColor(rowBg);
      exitCell.setBorderColor(BORDER_COLOR);
      exitCell.setHorizontalAlignment(Element.ALIGN_CENTER);
      exitCell.setPadding(4);
      table.addCell(exitCell);
    }

    return table;
  }

  // ── Exits by product table ──────────────────────────────────

  private void addExitsByProductTable(Document doc, List<StockMovement> exits) throws IOException {
    Paragraph title = new Paragraph("Quantités sorties par produit", new Font(Font.HELVETICA, 11, Font.BOLD, PRIMARY_COLOR));
    doc.add(title);
    doc.add(Chunk.NEWLINE);

    if (exits == null || exits.isEmpty()) {
      doc.add(new Paragraph("Aucune sortie enregistrée sur la période.", new Font(Font.HELVETICA, 10, Font.ITALIC, Color.GRAY)));
      return;
    }

    // Grouper par SKU et sommer
    Map<String, BigDecimal> qtyBySku = new HashMap<>();
    Map<String, String> typeBySku = new HashMap<>();
    for (StockMovement m : exits) {
      String sku = m.getProductSku() != null ? m.getProductSku() : "—";
      qtyBySku.merge(sku, m.getQuantity(), BigDecimal::add);
      if (!typeBySku.containsKey(sku)) {
        typeBySku.put(sku, movementTypeLabel(m.getType() != null ? m.getType().name() : ""));
      }
    }

    int total = qtyBySku.size();
    int half = (int) Math.ceil(total / 2.0);
    List<Map.Entry<String, BigDecimal>> entries = qtyBySku.entrySet().stream()
      .sorted(Map.Entry.<String, BigDecimal>comparingByValue().reversed())
      .collect(Collectors.toList());

    if (total <= 15) {
      doc.add(createExitsSingleColumn(entries, typeBySku, 0, total));
    } else {
      PdfPTable outerTable = new PdfPTable(2);
      outerTable.setWidthPercentage(100);
      outerTable.setWidths(new float[]{1f, 1f});

      PdfPCell leftCell = new PdfPCell();
      leftCell.setBorder(Rectangle.NO_BORDER);
      leftCell.setPaddingRight(5);
      leftCell.addElement(createExitsSingleColumn(entries, typeBySku, 0, half));
      outerTable.addCell(leftCell);

      PdfPCell rightCell = new PdfPCell();
      rightCell.setBorder(Rectangle.NO_BORDER);
      rightCell.setPaddingLeft(5);
      rightCell.addElement(createExitsSingleColumn(entries, typeBySku, half, total));
      outerTable.addCell(rightCell);

      doc.add(outerTable);
    }

    doc.add(Chunk.NEWLINE);
    BigDecimal grandTotal = qtyBySku.values().stream().reduce(BigDecimal.ZERO, BigDecimal::add);
    Paragraph totalPara = new Paragraph();
    totalPara.add(new Phrase("Total produits sortis: " + total + " | Quantité totale sortie: " + grandTotal,
      new Font(Font.HELVETICA, 10, Font.BOLD, PRIMARY_COLOR)));
    doc.add(totalPara);
  }

  private PdfPTable createExitsSingleColumn(List<Map.Entry<String, BigDecimal>> entries,
                                            Map<String, String> typeBySku, int start, int end) {
    PdfPTable table = new PdfPTable(3);
    table.setWidthPercentage(100);
    table.setWidths(new float[]{3f, 1.5f, 1f});

    String[] headers = {"Code", "Motif sortie", "Qté sortie"};
    for (String h : headers) {
      PdfPCell cell = new PdfPCell(new Phrase(h, new Font(Font.HELVETICA, 9, Font.BOLD, Color.WHITE)));
      cell.setBackgroundColor(HEADER_BG);
      cell.setBorderColor(BORDER_COLOR);
      cell.setHorizontalAlignment(Element.ALIGN_CENTER);
      cell.setPadding(5);
      table.addCell(cell);
    }

    for (int i = start; i < end; i++) {
      Map.Entry<String, BigDecimal> entry = entries.get(i);
      boolean altRow = (i % 2 == 1);
      Color rowBg = altRow ? ALT_ROW : Color.WHITE;
      Font dataFont = new Font(Font.HELVETICA, 8, Font.NORMAL, Color.DARK_GRAY);

      PdfPCell codeCell = new PdfPCell(new Phrase(entry.getKey(), dataFont));
      codeCell.setBackgroundColor(rowBg);
      codeCell.setBorderColor(BORDER_COLOR);
      codeCell.setPadding(4);
      table.addCell(codeCell);

      PdfPCell motifCell = new PdfPCell(new Phrase(typeBySku.getOrDefault(entry.getKey(), "—"), dataFont));
      motifCell.setBackgroundColor(rowBg);
      motifCell.setBorderColor(BORDER_COLOR);
      motifCell.setPadding(4);
      table.addCell(motifCell);

      PdfPCell qtyCell = new PdfPCell(new Phrase(String.valueOf(entry.getValue()), dataFont));
      qtyCell.setBackgroundColor(rowBg);
      qtyCell.setBorderColor(BORDER_COLOR);
      qtyCell.setHorizontalAlignment(Element.ALIGN_CENTER);
      qtyCell.setPadding(4);
      table.addCell(qtyCell);
    }

    return table;
  }

  // ── Entries by product table ────────────────────────────────

  private void addEntriesByProductTable(Document doc, List<StockMovement> entries) throws IOException {
    Paragraph title = new Paragraph("Quantités entrées par produit", new Font(Font.HELVETICA, 11, Font.BOLD, PRIMARY_COLOR));
    doc.add(title);
    doc.add(Chunk.NEWLINE);

    if (entries == null || entries.isEmpty()) {
      doc.add(new Paragraph("Aucune entrée enregistrée sur la période.", new Font(Font.HELVETICA, 10, Font.ITALIC, Color.GRAY)));
      return;
    }

    Map<String, BigDecimal> qtyBySku = new HashMap<>();
    Map<String, String> typeBySku = new HashMap<>();
    for (StockMovement m : entries) {
      String sku = m.getProductSku() != null ? m.getProductSku() : "—";
      qtyBySku.merge(sku, m.getQuantity(), BigDecimal::add);
      if (!typeBySku.containsKey(sku)) {
        typeBySku.put(sku, movementTypeLabel(m.getType() != null ? m.getType().name() : ""));
      }
    }

    int total = qtyBySku.size();
    int half = (int) Math.ceil(total / 2.0);
    List<Map.Entry<String, BigDecimal>> sortedEntries = qtyBySku.entrySet().stream()
      .sorted(Map.Entry.<String, BigDecimal>comparingByValue().reversed())
      .collect(Collectors.toList());

    if (total <= 15) {
      doc.add(createEntriesSingleColumn(sortedEntries, typeBySku, 0, total));
    } else {
      PdfPTable outerTable = new PdfPTable(2);
      outerTable.setWidthPercentage(100);
      outerTable.setWidths(new float[]{1f, 1f});

      PdfPCell leftCell = new PdfPCell();
      leftCell.setBorder(Rectangle.NO_BORDER);
      leftCell.setPaddingRight(5);
      leftCell.addElement(createEntriesSingleColumn(sortedEntries, typeBySku, 0, half));
      outerTable.addCell(leftCell);

      PdfPCell rightCell = new PdfPCell();
      rightCell.setBorder(Rectangle.NO_BORDER);
      rightCell.setPaddingLeft(5);
      rightCell.addElement(createEntriesSingleColumn(sortedEntries, typeBySku, half, total));
      outerTable.addCell(rightCell);

      doc.add(outerTable);
    }

    doc.add(Chunk.NEWLINE);
    BigDecimal grandTotal = qtyBySku.values().stream().reduce(BigDecimal.ZERO, BigDecimal::add);
    Paragraph totalPara = new Paragraph();
    totalPara.add(new Phrase("Total produits entrés: " + total + " | Quantité totale entrée: " + grandTotal,
      new Font(Font.HELVETICA, 10, Font.BOLD, PRIMARY_COLOR)));
    doc.add(totalPara);
  }

  private PdfPTable createEntriesSingleColumn(List<Map.Entry<String, BigDecimal>> entries,
                                              Map<String, String> typeBySku, int start, int end) {
    PdfPTable table = new PdfPTable(3);
    table.setWidthPercentage(100);
    table.setWidths(new float[]{3f, 1.5f, 1f});

    String[] headers = {"Code", "Motif entrée", "Qté entrée"};
    for (String h : headers) {
      PdfPCell cell = new PdfPCell(new Phrase(h, new Font(Font.HELVETICA, 9, Font.BOLD, Color.WHITE)));
      cell.setBackgroundColor(HEADER_BG);
      cell.setBorderColor(BORDER_COLOR);
      cell.setHorizontalAlignment(Element.ALIGN_CENTER);
      cell.setPadding(5);
      table.addCell(cell);
    }

    for (int i = start; i < end; i++) {
      Map.Entry<String, BigDecimal> entry = entries.get(i);
      boolean altRow = (i % 2 == 1);
      Color rowBg = altRow ? ALT_ROW : Color.WHITE;
      Font dataFont = new Font(Font.HELVETICA, 8, Font.NORMAL, Color.DARK_GRAY);

      PdfPCell codeCell = new PdfPCell(new Phrase(entry.getKey(), dataFont));
      codeCell.setBackgroundColor(rowBg);
      codeCell.setBorderColor(BORDER_COLOR);
      codeCell.setPadding(4);
      table.addCell(codeCell);

      PdfPCell motifCell = new PdfPCell(new Phrase(typeBySku.getOrDefault(entry.getKey(), "—"), dataFont));
      motifCell.setBackgroundColor(rowBg);
      motifCell.setBorderColor(BORDER_COLOR);
      motifCell.setPadding(4);
      table.addCell(motifCell);

      PdfPCell qtyCell = new PdfPCell(new Phrase(String.valueOf(entry.getValue()), dataFont));
      qtyCell.setBackgroundColor(rowBg);
      qtyCell.setBorderColor(BORDER_COLOR);
      qtyCell.setHorizontalAlignment(Element.ALIGN_CENTER);
      qtyCell.setPadding(4);
      table.addCell(qtyCell);
    }

    return table;
  }

  // ── Summary section for global fiche ────────────────────────

  private void addSummarySection(Document doc, List<StockItem> items, List<StockMovement> movements,
                                 LocalDateTime periodStart, LocalDateTime periodEnd) throws IOException {
    long totalRefs = items != null ? items.size() : 0;
    BigDecimal totalStockQty = items != null
      ? items.stream().map(StockItem::getQuantity).reduce(BigDecimal.ZERO, BigDecimal::add)
      : BigDecimal.ZERO;

    long totalEntries = movements != null ? movements.stream().filter(this::isEntryMovement).count() : 0;
    long totalExits = movements != null ? movements.stream().filter(this::isExitMovement).count() : 0;
    BigDecimal totalEntryQty = movements != null
      ? movements.stream().filter(this::isEntryMovement).map(StockMovement::getQuantity).reduce(BigDecimal.ZERO, BigDecimal::add)
      : BigDecimal.ZERO;
    BigDecimal totalExitQty = movements != null
      ? movements.stream().filter(this::isExitMovement).map(StockMovement::getQuantity).reduce(BigDecimal.ZERO, BigDecimal::add)
      : BigDecimal.ZERO;

    PdfPTable summaryTable = new PdfPTable(2);
    summaryTable.setWidthPercentage(100);
    summaryTable.setWidths(new float[]{1f, 1f});

    addInfoRow(summaryTable, "Références en stock", String.valueOf(totalRefs));
    addInfoRow(summaryTable, "Quantité totale en stock", String.valueOf(totalStockQty));
    addInfoRow(summaryTable, "Nombre d'entrées (période)", String.valueOf(totalEntries));
    addInfoRow(summaryTable, "Qté totale entrée", String.valueOf(totalEntryQty));
    addInfoRow(summaryTable, "Nombre de sorties (période)", String.valueOf(totalExits));
    addInfoRow(summaryTable, "Qté totale sortie", String.valueOf(totalExitQty));

    doc.add(summaryTable);
    doc.add(Chunk.NEWLINE);
  }

  // ── Global synthesis table (Code, Qté entrée, Qté sortie, Qté stock) ──

  private void addGlobalSynthesisTable(Document doc, List<StockItem> items, List<StockMovement> movements,
                                       LocalDateTime periodStart, LocalDateTime periodEnd) throws IOException {
    Paragraph title = new Paragraph("Synthèse par produit: entrées, sorties, stock restant",
      new Font(Font.HELVETICA, 11, Font.BOLD, PRIMARY_COLOR));
    doc.add(title);
    doc.add(Chunk.NEWLINE);

    if (items == null || items.isEmpty()) {
      doc.add(new Paragraph("Aucun produit en stock.", new Font(Font.HELVETICA, 10, Font.ITALIC, Color.GRAY)));
      return;
    }

    Map<String, BigDecimal> entryQtyBySku = new HashMap<>();
    Map<String, BigDecimal> exitQtyBySku = new HashMap<>();
    if (movements != null) {
      for (StockMovement m : movements) {
        if (m.getProductSku() == null) continue;
        if (isEntryMovement(m)) {
          entryQtyBySku.merge(m.getProductSku(), m.getQuantity(), BigDecimal::add);
        } else if (isExitMovement(m)) {
          exitQtyBySku.merge(m.getProductSku(), m.getQuantity(), BigDecimal::add);
        }
      }
    }

    int totalItems = items.size();
    int half = (int) Math.ceil(totalItems / 2.0);

    if (totalItems <= 10) {
      doc.add(createGlobalSynthesisSingleColumn(items, entryQtyBySku, exitQtyBySku, 0, totalItems));
    } else {
      PdfPTable outerTable = new PdfPTable(2);
      outerTable.setWidthPercentage(100);
      outerTable.setWidths(new float[]{1f, 1f});

      PdfPCell leftCell = new PdfPCell();
      leftCell.setBorder(Rectangle.NO_BORDER);
      leftCell.setPaddingRight(5);
      leftCell.addElement(createGlobalSynthesisSingleColumn(items, entryQtyBySku, exitQtyBySku, 0, half));
      outerTable.addCell(leftCell);

      PdfPCell rightCell = new PdfPCell();
      rightCell.setBorder(Rectangle.NO_BORDER);
      rightCell.setPaddingLeft(5);
      rightCell.addElement(createGlobalSynthesisSingleColumn(items, entryQtyBySku, exitQtyBySku, half, totalItems));
      outerTable.addCell(rightCell);

      doc.add(outerTable);
    }

    doc.add(Chunk.NEWLINE);
    Paragraph totalPara = new Paragraph();
    totalPara.add(new Phrase("Total références: " + totalItems,
      new Font(Font.HELVETICA, 10, Font.BOLD, PRIMARY_COLOR)));
    doc.add(totalPara);
  }

  private PdfPTable createGlobalSynthesisSingleColumn(List<StockItem> items,
                                                      Map<String, BigDecimal> entryQtyBySku,
                                                      Map<String, BigDecimal> exitQtyBySku,
                                                      int start, int end) {
    PdfPTable table = new PdfPTable(4);
    table.setWidthPercentage(100);
    table.setWidths(new float[]{3f, 1f, 1f, 1f});

    String[] headers = {"Code", "Qté Entrée", "Qté Sortie", "Qté Stock"};
    for (String h : headers) {
      PdfPCell cell = new PdfPCell(new Phrase(h, new Font(Font.HELVETICA, 9, Font.BOLD, Color.WHITE)));
      cell.setBackgroundColor(HEADER_BG);
      cell.setBorderColor(BORDER_COLOR);
      cell.setHorizontalAlignment(Element.ALIGN_CENTER);
      cell.setPadding(5);
      table.addCell(cell);
    }

    for (int i = start; i < end; i++) {
      StockItem item = items.get(i);
      boolean altRow = (i % 2 == 1);
      Color rowBg = altRow ? ALT_ROW : Color.WHITE;
      Font dataFont = new Font(Font.HELVETICA, 8, Font.NORMAL, Color.DARK_GRAY);

      PdfPCell codeCell = new PdfPCell(new Phrase(item.getProductSku() != null ? item.getProductSku() : "—", dataFont));
      codeCell.setBackgroundColor(rowBg);
      codeCell.setBorderColor(BORDER_COLOR);
      codeCell.setPadding(4);
      table.addCell(codeCell);

      BigDecimal entryQty = entryQtyBySku.getOrDefault(item.getProductSku(), BigDecimal.ZERO);
      PdfPCell entryCell = new PdfPCell(new Phrase(String.valueOf(entryQty), dataFont));
      entryCell.setBackgroundColor(rowBg);
      entryCell.setBorderColor(BORDER_COLOR);
      entryCell.setHorizontalAlignment(Element.ALIGN_CENTER);
      entryCell.setPadding(4);
      table.addCell(entryCell);

      BigDecimal exitQty = exitQtyBySku.getOrDefault(item.getProductSku(), BigDecimal.ZERO);
      PdfPCell exitCell = new PdfPCell(new Phrase(String.valueOf(exitQty), dataFont));
      exitCell.setBackgroundColor(rowBg);
      exitCell.setBorderColor(BORDER_COLOR);
      exitCell.setHorizontalAlignment(Element.ALIGN_CENTER);
      exitCell.setPadding(4);
      table.addCell(exitCell);

      PdfPCell stockCell = new PdfPCell(new Phrase(String.valueOf(item.getQuantity()), dataFont));
      stockCell.setBackgroundColor(rowBg);
      stockCell.setBorderColor(BORDER_COLOR);
      stockCell.setHorizontalAlignment(Element.ALIGN_CENTER);
      stockCell.setPadding(4);
      table.addCell(stockCell);
    }

    return table;
  }

  // ── NTFoods style fiche de synthèse ───────────────────────────

  private void addNTFoodsHeader(Document doc, String nom, String ville, String zone,
                                Integer nombreColis) throws IOException {
    // Top title bar
    PdfPTable titleBar = new PdfPTable(1);
    titleBar.setWidthPercentage(100);
    PdfPCell titleCell = new PdfPCell();
    titleCell.setBackgroundColor(PRIMARY_COLOR);
    titleCell.setBorder(Rectangle.NO_BORDER);
    titleCell.setPadding(6);
    Paragraph title = new Paragraph("INFOS PRODUITS TANTY",
      new Font(Font.HELVETICA, 16, Font.BOLD, Color.WHITE));
    title.setAlignment(Element.ALIGN_CENTER);
    titleCell.addElement(title);
    titleBar.addCell(titleCell);
    doc.add(titleBar);
    doc.add(Chunk.NEWLINE);

    // Header info grid: Nom, Ville, Date, Zone, Nombre colis
    PdfPTable infoTable = new PdfPTable(2);
    infoTable.setWidthPercentage(100);
    infoTable.setWidths(new float[]{1f, 1f});

    addNTFoodsInfoCell(infoTable, "Nom:", nom != null ? nom : "____________________________");
    addNTFoodsInfoCell(infoTable, "Ville:", ville != null ? ville : "____________________________");
    addNTFoodsInfoCell(infoTable, "Date:", LocalDateTime.now().format(DATE_FMT));
    addNTFoodsInfoCell(infoTable, "Zone:", zone != null ? zone : "____________________________");
    addNTFoodsInfoCell(infoTable, "Nombre de colis:", nombreColis != null ? String.valueOf(nombreColis) : "____________________________");

    doc.add(infoTable);
    doc.add(Chunk.NEWLINE);
  }

  private void addNTFoodsInfoCell(PdfPTable table, String label, String value) {
    PdfPCell cell = new PdfPCell();
    cell.setBorder(Rectangle.NO_BORDER);
    cell.setPadding(3);
    Paragraph p = new Paragraph();
    p.add(new Phrase(label + " ", new Font(Font.HELVETICA, 10, Font.BOLD, PRIMARY_COLOR)));
    p.add(new Phrase(value, new Font(Font.HELVETICA, 10, Font.NORMAL, Color.BLACK)));
    cell.addElement(p);
    table.addCell(cell);
  }

  private void addMotifCheckboxes(Document doc, String selectedMotif) throws IOException {
    String[] motifs = {"Vente", "Promo", "Dotation", "EV", "AV", "Production"};

    Paragraph motifLabel = new Paragraph("Motifs:",
      new Font(Font.HELVETICA, 11, Font.BOLD, PRIMARY_COLOR));
    doc.add(motifLabel);

    PdfPTable checkboxTable = new PdfPTable(motifs.length);
    checkboxTable.setWidthPercentage(100);
    for (String m : motifs) {
      checkboxTable.addCell(createCheckboxCell(m, selectedMotif != null && selectedMotif.equalsIgnoreCase(m)));
    }
    doc.add(checkboxTable);
    doc.add(Chunk.NEWLINE);
  }

  private PdfPCell createCheckboxCell(String label, boolean checked) {
    PdfPCell cell = new PdfPCell();
    cell.setBorder(Rectangle.NO_BORDER);
    cell.setPadding(4);
    cell.setHorizontalAlignment(Element.ALIGN_CENTER);

    Font boxFont = new Font(Font.HELVETICA, 12, Font.BOLD, Color.BLACK);
    Font labelFont = new Font(Font.HELVETICA, 9, Font.NORMAL, Color.BLACK);

    Paragraph p = new Paragraph();
    p.add(new Phrase(checked ? "☑" : "☐", boxFont));
    p.add(new Phrase(" " + label, labelFont));
    p.setAlignment(Element.ALIGN_CENTER);
    cell.addElement(p);
    return cell;
  }

  private void addNTFoodsCodesGrid(Document doc, List<String> codes) throws IOException {
    if (codes == null || codes.isEmpty()) {
      doc.add(new Paragraph("Aucun code produit disponible.",
        new Font(Font.HELVETICA, 10, Font.ITALIC, Color.GRAY)));
      return;
    }

    // Normaliser, filtrer sur la liste officielle et réordonner selon l'ordre TANTY
    List<String> normalizedInput = codes.stream()
      .filter(c -> c != null && !c.isBlank())
      .map(String::trim)
      .distinct()
      .toList();

    List<String> orderedCodes = OFFICIAL_TANTY_CODES.stream()
      .filter(normalizedInput::contains)
      .collect(Collectors.toList());

    // Conserver les codes non répertoriés à la fin, triés
    List<String> extraCodes = normalizedInput.stream()
      .filter(c -> !OFFICIAL_TANTY_CODES.contains(c))
      .sorted()
      .collect(Collectors.toList());
    orderedCodes.addAll(extraCodes);

    if (orderedCodes.isEmpty()) {
      doc.add(new Paragraph("Aucun code produit reconnu.",
        new Font(Font.HELVETICA, 10, Font.ITALIC, Color.GRAY)));
      return;
    }

    // Build full grid with codes and blank cells: each code has 5 quantity columns
    int half = (int) Math.ceil(orderedCodes.size() / 2.0);
    List<String> leftCodes = orderedCodes.subList(0, half);
    List<String> rightCodes = orderedCodes.subList(half, orderedCodes.size());

    PdfPTable outerTable = new PdfPTable(2);
    outerTable.setWidthPercentage(100);
    outerTable.setWidths(new float[]{1f, 1f});

    // Left column
    PdfPCell leftCell = new PdfPCell();
    leftCell.setBorder(Rectangle.NO_BORDER);
    leftCell.setPaddingRight(5);
    leftCell.addElement(createCodesGridSingleColumn(leftCodes, 0, leftCodes.size()));
    outerTable.addCell(leftCell);

    // Right column
    PdfPCell rightCell = new PdfPCell();
    rightCell.setBorder(Rectangle.NO_BORDER);
    rightCell.setPaddingLeft(5);
    rightCell.addElement(createCodesGridSingleColumn(rightCodes, 0, rightCodes.size()));
    outerTable.addCell(rightCell);

    doc.add(outerTable);
  }

  private PdfPTable createCodesGridSingleColumn(List<String> codes, int start, int end) {
    // 6 columns: CODE + 5 blank quantity cells
    PdfPTable table = new PdfPTable(6);
    table.setWidthPercentage(100);
    table.setWidths(new float[]{2.2f, 1f, 1f, 1f, 1f, 1f});

    // Header row
    PdfPCell codeHeader = new PdfPCell(new Phrase("CODES",
      new Font(Font.HELVETICA, 8, Font.BOLD, Color.WHITE)));
    codeHeader.setBackgroundColor(HEADER_BG);
    codeHeader.setBorderColor(BORDER_COLOR);
    codeHeader.setHorizontalAlignment(Element.ALIGN_CENTER);
    codeHeader.setPadding(4);
    table.addCell(codeHeader);

    for (int i = 0; i < 5; i++) {
      PdfPCell qtyHeader = new PdfPCell(new Phrase("",
        new Font(Font.HELVETICA, 8, Font.BOLD, Color.WHITE)));
      qtyHeader.setBackgroundColor(HEADER_BG);
      qtyHeader.setBorderColor(BORDER_COLOR);
      qtyHeader.setPadding(4);
      table.addCell(qtyHeader);
    }

    for (int i = start; i < end; i++) {
      String code = codes.get(i);
      boolean altRow = (i % 2 == 1);
      Color rowBg = altRow ? ALT_ROW : Color.WHITE;

      PdfPCell codeCell = new PdfPCell(new Phrase(code != null ? code : "",
        new Font(Font.HELVETICA, 8, Font.BOLD, Color.BLACK)));
      codeCell.setBackgroundColor(rowBg);
      codeCell.setBorderColor(BORDER_COLOR);
      codeCell.setPadding(4);
      table.addCell(codeCell);

      for (int j = 0; j < 5; j++) {
        PdfPCell blankCell = new PdfPCell(new Phrase("",
          new Font(Font.HELVETICA, 8, Font.NORMAL, Color.BLACK)));
        blankCell.setBackgroundColor(rowBg);
        blankCell.setBorderColor(BORDER_COLOR);
        blankCell.setPadding(4);
        table.addCell(blankCell);
      }
    }

    return table;
  }

  private void addNTFoodsSignatures(Document doc) throws IOException {
    doc.add(Chunk.NEWLINE);
    doc.add(Chunk.NEWLINE);

    PdfPTable signTable = new PdfPTable(3);
    signTable.setWidthPercentage(100);
    signTable.setWidths(new float[]{1f, 1f, 1f});

    String[] roles = {"Concerné", "Supérieur Hiérarchique", "Direction"};
    for (String role : roles) {
      PdfPCell cell = new PdfPCell();
      cell.setBorder(Rectangle.NO_BORDER);
      cell.setPadding(8);
      cell.setHorizontalAlignment(Element.ALIGN_CENTER);

      Paragraph line = new Paragraph("____________________________",
        new Font(Font.HELVETICA, 10, Font.NORMAL, Color.BLACK));
      line.setAlignment(Element.ALIGN_CENTER);
      cell.addElement(line);

      Paragraph rolePara = new Paragraph(role,
        new Font(Font.HELVETICA, 9, Font.BOLD, PRIMARY_COLOR));
      rolePara.setAlignment(Element.ALIGN_CENTER);
      cell.addElement(rolePara);

      signTable.addCell(cell);
    }

    doc.add(signTable);

    // Footer
    Paragraph footer = new Paragraph("NT Foods SARL — Infos Produits TANTY",
      new Font(Font.HELVETICA, 7, Font.ITALIC, Color.GRAY));
    footer.setAlignment(Element.ALIGN_CENTER);
    doc.add(footer);
  }

  // ── Header with Logo + Motif ────────────────────────────────

  private void addHeader(Document doc, String title, String motif) throws IOException {
    PdfPTable headerTable = new PdfPTable(2);
    headerTable.setWidthPercentage(100);
    headerTable.setWidths(new float[]{3f, 7f});

    // Logo cell
    PdfPCell logoCell = new PdfPCell();
    logoCell.setBorder(Rectangle.NO_BORDER);
    logoCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
    logoCell.setHorizontalAlignment(Element.ALIGN_LEFT);
    logoCell.setPaddingLeft(5);

    try {
      ClassPathResource logoResource = new ClassPathResource("static/logo_tanty.jpg");
      if (logoResource.exists()) {
        InputStream logoStream = logoResource.getInputStream();
        byte[] logoBytes = logoStream.readAllBytes();
        logoStream.close();
        Image logo = Image.getInstance(logoBytes);
        logo.scaleToFit(80, 80);
        logoCell.addElement(logo);
      } else {
        logoCell.addElement(new Phrase("NT FOODS", new Font(Font.HELVETICA, 14, Font.BOLD, PRIMARY_COLOR)));
      }
    } catch (Exception e) {
      log.warn("Logo not found, using text fallback: {}", e.getMessage());
      logoCell.addElement(new Phrase("NT FOODS", new Font(Font.HELVETICA, 14, Font.BOLD, PRIMARY_COLOR)));
    }
    headerTable.addCell(logoCell);

    // Title + company info cell
    PdfPCell titleCell = new PdfPCell();
    titleCell.setBorder(Rectangle.NO_BORDER);
    titleCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
    titleCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
    titleCell.setPaddingRight(5);

    Paragraph company = new Paragraph("NT FOODS SARL", new Font(Font.HELVETICA, 16, Font.BOLD, Color.BLACK));
    company.setAlignment(Element.ALIGN_RIGHT);
    titleCell.addElement(company);

    Paragraph addr = new Paragraph("Douala, Cameroun", new Font(Font.HELVETICA, 8, Font.NORMAL, Color.DARK_GRAY));
    addr.setAlignment(Element.ALIGN_RIGHT);
    titleCell.addElement(addr);

    Paragraph contact = new Paragraph("Tel: +237 XXX XXX XXX", new Font(Font.HELVETICA, 8, Font.NORMAL, Color.DARK_GRAY));
    contact.setAlignment(Element.ALIGN_RIGHT);
    titleCell.addElement(contact);

    headerTable.addCell(titleCell);
    doc.add(headerTable);

    // Separator line
    LineSeparator sep = new LineSeparator(1f, 100f, PRIMARY_COLOR, Element.ALIGN_CENTER, 0);
    doc.add(sep);
    doc.add(Chunk.NEWLINE);

    // Title bar
    PdfPTable titleBar = new PdfPTable(1);
    titleBar.setWidthPercentage(100);
    PdfPCell titleBarCell = new PdfPCell(new Phrase(title, new Font(Font.HELVETICA, 14, Font.BOLD, Color.WHITE)));
    titleBarCell.setBackgroundColor(PRIMARY_COLOR);
    titleBarCell.setHorizontalAlignment(Element.ALIGN_CENTER);
    titleBarCell.setPadding(8);
    titleBarCell.setBorder(Rectangle.NO_BORDER);
    titleBar.addCell(titleBarCell);
    doc.add(titleBar);

    // Motif
    if (motif != null && !motif.isEmpty()) {
      doc.add(Chunk.NEWLINE);
      Paragraph motifPara = new Paragraph();
      motifPara.add(new Phrase("Motif: ", new Font(Font.HELVETICA, 10, Font.BOLD, PRIMARY_COLOR)));
      motifPara.add(new Phrase(motif, new Font(Font.HELVETICA, 10, Font.NORMAL, Color.DARK_GRAY)));
      doc.add(motifPara);
    }

    // Date
    Paragraph datePara = new Paragraph();
    datePara.add(new Phrase("Date d'édition: ", new Font(Font.HELVETICA, 9, Font.BOLD, Color.DARK_GRAY)));
    datePara.add(new Phrase(LocalDateTime.now().format(DATETIME_FMT), new Font(Font.HELVETICA, 9, Font.NORMAL, Color.DARK_GRAY)));
    datePara.setAlignment(Element.ALIGN_RIGHT);
    doc.add(datePara);
    doc.add(Chunk.NEWLINE);
  }

  // ── Dotation Info ───────────────────────────────────────────

  private void addDotationInfo(Document doc, DotationRequest dotation) throws IOException {
    PdfPTable infoTable = new PdfPTable(2);
    infoTable.setWidthPercentage(100);
    infoTable.setWidths(new float[]{1f, 2f});

    addInfoRow(infoTable, "Commercial", dotation.getCommercialName() != null ? dotation.getCommercialName() : "—");
    addInfoRow(infoTable, "Matricule", dotation.getCommercialMatricule() != null ? dotation.getCommercialMatricule() : "—");
    addInfoRow(infoTable, "Référence", dotation.getReferenceNumber() != null ? dotation.getReferenceNumber() : "—");
    addInfoRow(infoTable, "Date demande", dotation.getRequestedAt() != null ? dotation.getRequestedAt().format(DATE_FMT) : "—");
    addInfoRow(infoTable, "Justification", dotation.getJustification() != null ? dotation.getJustification() : "—");
    addInfoRow(infoTable, "Statut", dotation.getStatus() != null ? dotation.getStatus().toString() : "—");

    doc.add(infoTable);
    doc.add(Chunk.NEWLINE);
  }

  private void addInfoRow(PdfPTable table, String label, String value) {
    PdfPCell labelCell = new PdfPCell(new Phrase(label, new Font(Font.HELVETICA, 9, Font.BOLD, PRIMARY_COLOR)));
    labelCell.setBackgroundColor(LIGHT_BG);
    labelCell.setBorderColor(BORDER_COLOR);
    labelCell.setPadding(5);
    table.addCell(labelCell);

    PdfPCell valueCell = new PdfPCell(new Phrase(value, new Font(Font.HELVETICA, 9, Font.NORMAL, Color.DARK_GRAY)));
    valueCell.setBorderColor(BORDER_COLOR);
    valueCell.setPadding(5);
    table.addCell(valueCell);
  }

  // ── Dotation Items Table (two-column layout for many items) ─

  private void addDotationItemsTable(Document doc, DotationRequest dotation) throws IOException {
    List<DotationRequest.DotationItem> items = dotation.getItems();
    if (items == null || items.isEmpty()) {
      doc.add(new Paragraph("Aucun article dans cette dotation.", new Font(Font.HELVETICA, 10, Font.ITALIC, Color.GRAY)));
      return;
    }

    Paragraph title = new Paragraph("Détail des articles demandés", new Font(Font.HELVETICA, 11, Font.BOLD, PRIMARY_COLOR));
    doc.add(title);
    doc.add(Chunk.NEWLINE);

    int totalItems = items.size();
    int half = (int) Math.ceil(totalItems / 2.0);

    if (totalItems <= 12) {
      // Single column table
      PdfPTable table = createDotationSingleColumnTable(items, 0, totalItems);
      doc.add(table);
    } else {
      // Two-column layout: side by side tables
      PdfPTable outerTable = new PdfPTable(2);
      outerTable.setWidthPercentage(100);
      outerTable.setWidths(new float[]{1f, 1f});

      // Left column
      PdfPCell leftCell = new PdfPCell();
      leftCell.setBorder(Rectangle.NO_BORDER);
      leftCell.setPaddingRight(5);
      PdfPTable leftTable = createDotationSingleColumnTable(items, 0, half);
      leftCell.addElement(leftTable);
      outerTable.addCell(leftCell);

      // Right column
      PdfPCell rightCell = new PdfPCell();
      rightCell.setBorder(Rectangle.NO_BORDER);
      rightCell.setPaddingLeft(5);
      PdfPTable rightTable = createDotationSingleColumnTable(items, half, totalItems);
      rightCell.addElement(rightTable);
      outerTable.addCell(rightCell);

      doc.add(outerTable);
    }

    doc.add(Chunk.NEWLINE);

    // Total line
    Paragraph totalPara = new Paragraph();
    totalPara.add(new Phrase("Total articles: " + totalItems, new Font(Font.HELVETICA, 10, Font.BOLD, PRIMARY_COLOR)));
    doc.add(totalPara);
  }

  private PdfPTable createDotationSingleColumnTable(List<DotationRequest.DotationItem> items, int start, int end) {
    PdfPTable table = new PdfPTable(2);
    table.setWidthPercentage(100);
    table.setWidths(new float[]{3f, 1f});

    // Header
    PdfPCell codeHeader = new PdfPCell(new Phrase("Code", new Font(Font.HELVETICA, 9, Font.BOLD, Color.WHITE)));
    codeHeader.setBackgroundColor(HEADER_BG);
    codeHeader.setBorderColor(BORDER_COLOR);
    codeHeader.setPadding(5);
    table.addCell(codeHeader);

    PdfPCell qtyHeader = new PdfPCell(new Phrase("Quantité", new Font(Font.HELVETICA, 9, Font.BOLD, Color.WHITE)));
    qtyHeader.setBackgroundColor(HEADER_BG);
    qtyHeader.setBorderColor(BORDER_COLOR);
    qtyHeader.setHorizontalAlignment(Element.ALIGN_CENTER);
    qtyHeader.setPadding(5);
    table.addCell(qtyHeader);

    // Data rows
    for (int i = start; i < end; i++) {
      DotationRequest.DotationItem item = items.get(i);
      boolean altRow = (i % 2 == 1);
      Color rowBg = altRow ? ALT_ROW : Color.WHITE;

      PdfPCell codeCell = new PdfPCell(new Phrase(
        item.getProductSku() != null ? item.getProductSku() : "—",
        new Font(Font.HELVETICA, 8, Font.NORMAL, Color.DARK_GRAY)));
      codeCell.setBackgroundColor(rowBg);
      codeCell.setBorderColor(BORDER_COLOR);
      codeCell.setPadding(4);
      table.addCell(codeCell);

      PdfPCell qtyCell = new PdfPCell(new Phrase(
        String.valueOf(item.getRequestedQuantity()),
        new Font(Font.HELVETICA, 8, Font.NORMAL, Color.DARK_GRAY)));
      qtyCell.setBackgroundColor(rowBg);
      qtyCell.setBorderColor(BORDER_COLOR);
      qtyCell.setHorizontalAlignment(Element.ALIGN_CENTER);
      qtyCell.setPadding(4);
      table.addCell(qtyCell);
    }

    return table;
  }

  // ── Stock Items Table ───────────────────────────────────────

  private void addStockItemsTable(Document doc, List<StockItem> items) throws IOException {
    Paragraph title = new Paragraph("Inventaire des produits en stock", new Font(Font.HELVETICA, 11, Font.BOLD, PRIMARY_COLOR));
    doc.add(title);
    doc.add(Chunk.NEWLINE);

    if (items == null || items.isEmpty()) {
      doc.add(new Paragraph("Aucun produit en stock.", new Font(Font.HELVETICA, 10, Font.ITALIC, Color.GRAY)));
      return;
    }

    int totalItems = items.size();
    int half = (int) Math.ceil(totalItems / 2.0);

    if (totalItems <= 15) {
      PdfPTable table = createStockItemsSingleColumnTable(items, 0, totalItems);
      doc.add(table);
    } else {
      // Two-column layout
      PdfPTable outerTable = new PdfPTable(2);
      outerTable.setWidthPercentage(100);
      outerTable.setWidths(new float[]{1f, 1f});

      PdfPCell leftCell = new PdfPCell();
      leftCell.setBorder(Rectangle.NO_BORDER);
      leftCell.setPaddingRight(5);
      leftCell.addElement(createStockItemsSingleColumnTable(items, 0, half));
      outerTable.addCell(leftCell);

      PdfPCell rightCell = new PdfPCell();
      rightCell.setBorder(Rectangle.NO_BORDER);
      rightCell.setPaddingLeft(5);
      rightCell.addElement(createStockItemsSingleColumnTable(items, half, totalItems));
      outerTable.addCell(rightCell);

      doc.add(outerTable);
    }

    doc.add(Chunk.NEWLINE);
    Paragraph totalPara = new Paragraph();
    totalPara.add(new Phrase("Total références: " + totalItems, new Font(Font.HELVETICA, 10, Font.BOLD, PRIMARY_COLOR)));
    doc.add(totalPara);
  }

  private PdfPTable createStockItemsSingleColumnTable(List<StockItem> items, int start, int end) {
    PdfPTable table = new PdfPTable(2);
    table.setWidthPercentage(100);
    table.setWidths(new float[]{3f, 1f});

    // Header
    PdfPCell codeHeader = new PdfPCell(new Phrase("Code", new Font(Font.HELVETICA, 9, Font.BOLD, Color.WHITE)));
    codeHeader.setBackgroundColor(HEADER_BG);
    codeHeader.setBorderColor(BORDER_COLOR);
    codeHeader.setPadding(5);
    table.addCell(codeHeader);

    PdfPCell qtyHeader = new PdfPCell(new Phrase("Quantité", new Font(Font.HELVETICA, 9, Font.BOLD, Color.WHITE)));
    qtyHeader.setBackgroundColor(HEADER_BG);
    qtyHeader.setBorderColor(BORDER_COLOR);
    qtyHeader.setHorizontalAlignment(Element.ALIGN_CENTER);
    qtyHeader.setPadding(5);
    table.addCell(qtyHeader);

    for (int i = start; i < end; i++) {
      StockItem item = items.get(i);
      boolean altRow = (i % 2 == 1);
      Color rowBg = altRow ? ALT_ROW : Color.WHITE;

      PdfPCell codeCell = new PdfPCell(new Phrase(
        item.getProductSku() != null ? item.getProductSku() : "—",
        new Font(Font.HELVETICA, 8, Font.NORMAL, Color.DARK_GRAY)));
      codeCell.setBackgroundColor(rowBg);
      codeCell.setBorderColor(BORDER_COLOR);
      codeCell.setPadding(4);
      table.addCell(codeCell);

      PdfPCell qtyCell = new PdfPCell(new Phrase(
        String.valueOf(item.getQuantity()),
        new Font(Font.HELVETICA, 8, Font.NORMAL, Color.DARK_GRAY)));
      qtyCell.setBackgroundColor(rowBg);
      qtyCell.setBorderColor(BORDER_COLOR);
      qtyCell.setHorizontalAlignment(Element.ALIGN_CENTER);
      qtyCell.setPadding(4);
      table.addCell(qtyCell);
    }

    return table;
  }

  // ── Movements Table ─────────────────────────────────────────

  private void addMovementsTable(Document doc, List<StockMovement> movements) throws IOException {
    Paragraph title = new Paragraph("Liste des mouvements de stock", new Font(Font.HELVETICA, 11, Font.BOLD, PRIMARY_COLOR));
    doc.add(title);
    doc.add(Chunk.NEWLINE);

    if (movements == null || movements.isEmpty()) {
      doc.add(new Paragraph("Aucun mouvement enregistré.", new Font(Font.HELVETICA, 10, Font.ITALIC, Color.GRAY)));
      return;
    }

    PdfPTable table = new PdfPTable(5);
    table.setWidthPercentage(100);
    table.setWidths(new float[]{2f, 2f, 1.5f, 1f, 1.5f});

    // Header
    String[] headers = {"Référence", "Type", "SKU", "Quantité", "Date"};
    for (String h : headers) {
      PdfPCell cell = new PdfPCell(new Phrase(h, new Font(Font.HELVETICA, 9, Font.BOLD, Color.WHITE)));
      cell.setBackgroundColor(HEADER_BG);
      cell.setBorderColor(BORDER_COLOR);
      cell.setPadding(5);
      cell.setHorizontalAlignment(Element.ALIGN_CENTER);
      table.addCell(cell);
    }

    for (int i = 0; i < movements.size(); i++) {
      StockMovement m = movements.get(i);
      boolean altRow = (i % 2 == 1);
      Color rowBg = altRow ? ALT_ROW : Color.WHITE;
      Font dataFont = new Font(Font.HELVETICA, 8, Font.NORMAL, Color.DARK_GRAY);

      addMovementCell(table, m.getReferenceNumber() != null ? m.getReferenceNumber() : "—", rowBg, dataFont, Element.ALIGN_LEFT);
      addMovementCell(table, m.getType() != null ? m.getType().toString() : "—", rowBg, dataFont, Element.ALIGN_LEFT);
      addMovementCell(table, m.getProductSku() != null ? m.getProductSku() : "—", rowBg, dataFont, Element.ALIGN_LEFT);
      addMovementCell(table, String.valueOf(m.getQuantity()), rowBg, dataFont, Element.ALIGN_CENTER);
      addMovementCell(table, m.getRequestedAt() != null ? m.getRequestedAt().format(DATE_FMT) : "—", rowBg, dataFont, Element.ALIGN_CENTER);
    }

    doc.add(table);
    doc.add(Chunk.NEWLINE);
    Paragraph totalPara = new Paragraph();
    totalPara.add(new Phrase("Total mouvements: " + movements.size(), new Font(Font.HELVETICA, 10, Font.BOLD, PRIMARY_COLOR)));
    doc.add(totalPara);
  }

  private void addMovementCell(PdfPTable table, String text, Color bg, Font font, int align) {
    PdfPCell cell = new PdfPCell(new Phrase(text, font));
    cell.setBackgroundColor(bg);
    cell.setBorderColor(BORDER_COLOR);
    cell.setPadding(4);
    cell.setHorizontalAlignment(align);
    table.addCell(cell);
  }

  // ── Signatures ──────────────────────────────────────────────

  private void addSignatures(Document doc, String[] roles) throws IOException {
    doc.add(Chunk.NEWLINE);
    doc.add(Chunk.NEWLINE);
    doc.add(Chunk.NEWLINE);

    LineSeparator sep = new LineSeparator(0.5f, 100f, BORDER_COLOR, Element.ALIGN_CENTER, 0);
    doc.add(sep);
    doc.add(Chunk.NEWLINE);

    Paragraph signTitle = new Paragraph("SIGNATURES", new Font(Font.HELVETICA, 11, Font.BOLD, PRIMARY_COLOR));
    signTitle.setAlignment(Element.ALIGN_CENTER);
    doc.add(signTitle);
    doc.add(Chunk.NEWLINE);
    doc.add(Chunk.NEWLINE);

    int numCols = roles.length;
    PdfPTable signTable = new PdfPTable(numCols);
    signTable.setWidthPercentage(100);

    for (String role : roles) {
      PdfPCell cell = new PdfPCell();
      cell.setBorder(Rectangle.NO_BORDER);
      cell.setPadding(10);

      // Signature line
      Paragraph line = new Paragraph("____________________", new Font(Font.HELVETICA, 10, Font.NORMAL, Color.DARK_GRAY));
      line.setAlignment(Element.ALIGN_CENTER);
      cell.addElement(line);

      // Role label
      Paragraph rolePara = new Paragraph(role, new Font(Font.HELVETICA, 9, Font.BOLD, PRIMARY_COLOR));
      rolePara.setAlignment(Element.ALIGN_CENTER);
      cell.addElement(rolePara);

      // Date placeholder
      Paragraph datePara = new Paragraph("Date: ____/____/______", new Font(Font.HELVETICA, 8, Font.NORMAL, Color.GRAY));
      datePara.setAlignment(Element.ALIGN_CENTER);
      cell.addElement(datePara);

      signTable.addCell(cell);
    }

    doc.add(signTable);

    // Footer
    doc.add(Chunk.NEWLINE);
    Paragraph footer = new Paragraph(
      "Document généré électroniquement par NT FOODS ERP — " + LocalDateTime.now().format(DATETIME_FMT),
      new Font(Font.HELVETICA, 7, Font.ITALIC, Color.GRAY));
    footer.setAlignment(Element.ALIGN_CENTER);
    doc.add(footer);
  }
}
