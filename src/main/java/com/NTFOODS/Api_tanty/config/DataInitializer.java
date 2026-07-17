package com.NTFOODS.Api_tanty.config;

import com.NTFOODS.Api_tanty.modules.stock.application.service.StockItemService;
import com.NTFOODS.Api_tanty.modules.stock.application.service.StockLocationService;
import com.NTFOODS.Api_tanty.modules.stock.domain.common.enums.StockLocationType;
import com.NTFOODS.Api_tanty.modules.stock.domain.common.enums.StockMovementType;
import com.NTFOODS.Api_tanty.modules.stock.domain.common.valueobject.StockLocationId;
import com.NTFOODS.Api_tanty.modules.stock.infrastructure.persistence.product.jpa.BrandJpaEntity;
import com.NTFOODS.Api_tanty.modules.stock.infrastructure.persistence.product.jpa.BrandJpaRepository;
import com.NTFOODS.Api_tanty.modules.stock.infrastructure.persistence.product.jpa.ProductJpaEntity;
import com.NTFOODS.Api_tanty.modules.stock.infrastructure.persistence.product.jpa.ProductJpaRepository;
import com.NTFOODS.Api_tanty.modules.stock.infrastructure.persistence.product.jpa.ProductLineJpaEntity;
import com.NTFOODS.Api_tanty.modules.stock.infrastructure.persistence.product.jpa.ProductLineJpaRepository;
import com.NTFOODS.Api_tanty.modules.stock.infrastructure.persistence.product.jpa.ProductVariantJpaEntity;
import com.NTFOODS.Api_tanty.modules.stock.infrastructure.persistence.product.jpa.ProductVariantJpaRepository;
import com.NTFOODS.Api_tanty.modules.stock.infrastructure.persistence.product.jpa.ProductPriceJpaEntity;
import com.NTFOODS.Api_tanty.modules.stock.infrastructure.persistence.product.jpa.ProductPriceJpaRepository;
import com.NTFOODS.Api_tanty.modules.stock.infrastructure.persistence.stock.jpa.StockItemJpaEntity;
import com.NTFOODS.Api_tanty.modules.stock.infrastructure.persistence.stock.jpa.StockLocationJpaEntity;
import com.NTFOODS.Api_tanty.modules.stock.infrastructure.persistence.stock.jpa.StockMovementJpaEntity;
import com.NTFOODS.Api_tanty.modules.stock.infrastructure.persistence.stock.repository.StockItemRepository;
import com.NTFOODS.Api_tanty.modules.stock.infrastructure.persistence.stock.repository.StockLocationRepository;
import com.NTFOODS.Api_tanty.modules.stock.infrastructure.persistence.stock.repository.StockMovementRepository;
import com.NTFOODS.Api_tanty.modules.users.infrastructure.persistence.user.jpa.UserJpaEntity;
import com.NTFOODS.Api_tanty.modules.users.infrastructure.persistence.user.jpa.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

  private final BrandJpaRepository brandRepository;
  private final ProductLineJpaRepository productLineRepository;
  private final ProductVariantJpaRepository productVariantRepository;
  private final ProductJpaRepository productRepository;
  private final ProductPriceJpaRepository productPriceRepository;
  private final UserJpaRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final StockLocationRepository stockLocationRepository;
  private final StockItemRepository stockItemRepository;
  private final StockMovementRepository stockMovementRepository;
  private final StockLocationService stockLocationService;
  private final StockItemService stockItemService;

  @Override
  public void run(String... args) {
    log.info("=== DEMARRAGE SEEDING ERP TANTY (NT Foods SARL - Douala, Cameroun) ===");

    if (brandRepository.count() > 0) {
      log.info("Donnees deja presentes, seeding ignore.");
      return;
    }

    initializeBrands();
    initializeProductLines();
    initializeProductVariants();
    initializeProducts();
    initializeProductPrices();
    initializeUsers();
    initializeStockLocations();
    initializeStockItems();
    initializeStockMovements();

    log.info("=== SEEDING TERMINE ===");
  }

  // ===================== MARQUES =====================
  private void initializeBrands() {
    log.info("Initialisation des marques...");
    brandRepository.save(new BrandJpaEntity(1L, "TANTY", "TNT", true));
    log.info("Marques initialisees: {}", brandRepository.count());
  }

  // ===================== GAMMES (4 gammes) =====================
  private void initializeProductLines() {
    log.info("Initialisation des gammes (4 gammes)...");
    productLineRepository.save(new ProductLineJpaEntity(1L, 1L, "Bouillie de Soja", "BDS", true));
    productLineRepository.save(new ProductLineJpaEntity(2L, 1L, "A Grignoter", "AGR", true));
    productLineRepository.save(new ProductLineJpaEntity(3L, 1L, "Ingredients Culinaires", "ING", true));
    productLineRepository.save(new ProductLineJpaEntity(4L, 1L, "TANTY Choco", "TCH", true));
    log.info("Gammes initialisees: {}", productLineRepository.count());
  }

  // ===================== VARIANTES =====================
  private void initializeProductVariants() {
    log.info("Initialisation des variantes...");
    // Gamme 1: Bouillie de Soja (6 variantes)
    productVariantRepository.save(new ProductVariantJpaEntity(1L, 1L, "REINE Sachets", "RBS"));
    productVariantRepository.save(new ProductVariantJpaEntity(2L, 1L, "TANTY Sachets", "TBS"));
    productVariantRepository.save(new ProductVariantJpaEntity(3L, 1L, "REINE Prestige Etuis", "PRB"));
    productVariantRepository.save(new ProductVariantJpaEntity(4L, 1L, "TANTY Prestige Etuis", "PTB"));
    productVariantRepository.save(new ProductVariantJpaEntity(5L, 1L, "REINE Custard Powder", "RCP"));
    productVariantRepository.save(new ProductVariantJpaEntity(6L, 1L, "BabyVita", "BBV"));
    // Gamme 2: A Grignoter (3 variantes)
    productVariantRepository.save(new ProductVariantJpaEntity(7L, 2L, "Mini", "MIN"));
    productVariantRepository.save(new ProductVariantJpaEntity(8L, 2L, "Moyen", "MOY"));
    productVariantRepository.save(new ProductVariantJpaEntity(9L, 2L, "Grand", "GRA"));
    // Gamme 3: Ingredients Culinaires (1 variante — Chapelure uniquement selon catalogue Excel)
    productVariantRepository.save(new ProductVariantJpaEntity(10L, 3L, "Chapelure", "CHA"));
    // Gamme 4: TANTY Choco (1 variante)
    productVariantRepository.save(new ProductVariantJpaEntity(13L, 4L, "Choco Seaux", "CHS"));
    log.info("Variantes initialisees: {}", productVariantRepository.count());
  }

  // ===================== PRODUITS (47 produits selon catalogue Excel + matières premières + consommables) =====================
  private void initializeProducts() {
    log.info("Initialisation des produits (catalogue réel NT Foods - 47 produits)...");

    // === REINE Bouillies Sachets 57g (25/carton) — produits 1-5 ===
    productRepository.save(new ProductJpaEntity(1L, "RBS-AR", 1L, "6001234500001", "Bouillies", "sachet", new BigDecimal("250.00"), 7, 14, true, "SACHET", 25, new BigDecimal("57.00"), null, null));
    productRepository.save(new ProductJpaEntity(2L, "RBS-CL", 1L, "6001234500002", "Bouillies", "sachet", new BigDecimal("250.00"), 7, 14, true, "SACHET", 25, new BigDecimal("57.00"), null, null));
    productRepository.save(new ProductJpaEntity(3L, "RBS-LC", 1L, "6001234500003", "Bouillies", "sachet", new BigDecimal("250.00"), 7, 14, true, "SACHET", 25, new BigDecimal("57.00"), null, null));
    productRepository.save(new ProductJpaEntity(4L, "RBS-MC", 1L, "6001234500004", "Bouillies", "sachet", new BigDecimal("250.00"), 7, 14, true, "SACHET", 25, new BigDecimal("57.00"), null, null));
    productRepository.save(new ProductJpaEntity(5L, "RBS-PO", 1L, "6001234500005", "Bouillies", "sachet", new BigDecimal("250.00"), 7, 14, true, "SACHET", 25, new BigDecimal("57.00"), null, null));

    // === TANTY Bouillies Sachets 57g (25/carton) — produits 6-10 ===
    productRepository.save(new ProductJpaEntity(6L, "TBS-AR", 2L, "6001234500006", "Bouillies", "sachet", new BigDecimal("250.00"), 7, 14, true, "SACHET", 25, new BigDecimal("57.00"), null, null));
    productRepository.save(new ProductJpaEntity(7L, "TBS-LA", 2L, "6001234500007", "Bouillies", "sachet", new BigDecimal("250.00"), 7, 14, true, "SACHET", 25, new BigDecimal("57.00"), null, null));
    productRepository.save(new ProductJpaEntity(8L, "TBS-NA", 2L, "6001234500008", "Bouillies", "sachet", new BigDecimal("250.00"), 7, 14, true, "SACHET", 25, new BigDecimal("57.00"), null, null));
    productRepository.save(new ProductJpaEntity(9L, "TBS-PA", 2L, "6001234500009", "Bouillies", "sachet", new BigDecimal("250.00"), 7, 14, true, "SACHET", 25, new BigDecimal("57.00"), null, null));
    productRepository.save(new ProductJpaEntity(10L, "TBS-PO", 2L, "6001234500010", "Bouillies", "sachet", new BigDecimal("250.00"), 7, 14, true, "SACHET", 25, new BigDecimal("57.00"), null, null));

    // === REINE Prestige Etuis 150g (12/carton) — produits 11-15 ===
    productRepository.save(new ProductJpaEntity(11L, "PRB-AR", 3L, "6001234500011", "Bouillies", "etui", new BigDecimal("750.00"), 7, 14, true, "ETUI", 12, new BigDecimal("150.00"), null, null));
    productRepository.save(new ProductJpaEntity(12L, "PRB-CL", 3L, "6001234500012", "Bouillies", "etui", new BigDecimal("750.00"), 7, 14, true, "ETUI", 12, new BigDecimal("150.00"), null, null));
    productRepository.save(new ProductJpaEntity(13L, "PRB-LC", 3L, "6001234500013", "Bouillies", "etui", new BigDecimal("750.00"), 7, 14, true, "ETUI", 12, new BigDecimal("150.00"), null, null));
    productRepository.save(new ProductJpaEntity(14L, "PRB-MC", 3L, "6001234500014", "Bouillies", "etui", new BigDecimal("750.00"), 7, 14, true, "ETUI", 12, new BigDecimal("150.00"), null, null));
    productRepository.save(new ProductJpaEntity(15L, "PRB-PO", 3L, "6001234500015", "Bouillies", "etui", new BigDecimal("750.00"), 7, 14, true, "ETUI", 12, new BigDecimal("150.00"), null, null));

    // === TANTY Prestige Etuis 150g (12/carton) — produits 16-20 ===
    productRepository.save(new ProductJpaEntity(16L, "PTB-AR", 4L, "6001234500016", "Bouillies", "etui", new BigDecimal("750.00"), 7, 14, true, "ETUI", 12, new BigDecimal("150.00"), null, null));
    productRepository.save(new ProductJpaEntity(17L, "PTB-LA", 4L, "6001234500017", "Bouillies", "etui", new BigDecimal("750.00"), 7, 14, true, "ETUI", 12, new BigDecimal("150.00"), null, null));
    productRepository.save(new ProductJpaEntity(18L, "PTB-NA", 4L, "6001234500018", "Bouillies", "etui", new BigDecimal("750.00"), 7, 14, true, "ETUI", 12, new BigDecimal("150.00"), null, null));
    productRepository.save(new ProductJpaEntity(19L, "PTB-PA", 4L, "6001234500019", "Bouillies", "etui", new BigDecimal("750.00"), 7, 14, true, "ETUI", 12, new BigDecimal("150.00"), null, null));
    productRepository.save(new ProductJpaEntity(20L, "PTB-PO", 4L, "6001234500020", "Bouillies", "etui", new BigDecimal("750.00"), 7, 14, true, "ETUI", 12, new BigDecimal("150.00"), null, null));

    // === REINE Costard Powder 1L (12 seaux x 390g) — produits 21-24 ===
    productRepository.save(new ProductJpaEntity(21L, "RCP-1L-BA", 5L, "6001234500021", "Custard", "seau", new BigDecimal("2500.00"), 7, 14, true, "SEAU", 12, new BigDecimal("390.00"), "1L", null));
    productRepository.save(new ProductJpaEntity(22L, "RCP-1L-FR", 5L, "6001234500022", "Custard", "seau", new BigDecimal("2500.00"), 7, 14, true, "SEAU", 12, new BigDecimal("390.00"), "1L", null));
    productRepository.save(new ProductJpaEntity(23L, "RCP-1L-FO", 5L, "6001234500023", "Custard", "seau", new BigDecimal("2500.00"), 7, 14, true, "SEAU", 12, new BigDecimal("390.00"), "1L", null));
    productRepository.save(new ProductJpaEntity(24L, "RCP-1L-LB", 5L, "6001234500024", "Custard", "seau", new BigDecimal("2500.00"), 7, 14, true, "SEAU", 12, new BigDecimal("390.00"), "1L", null));

    // === REINE Costard Powder 2L (4 seaux x 880g) — produits 25-28 ===
    productRepository.save(new ProductJpaEntity(25L, "RCP-2L-BA", 5L, "6001234500025", "Custard", "seau", new BigDecimal("5000.00"), 7, 14, true, "SEAU", 4, new BigDecimal("880.00"), "2L", null));
    productRepository.save(new ProductJpaEntity(26L, "RCP-2L-FR", 5L, "6001234500026", "Custard", "seau", new BigDecimal("5000.00"), 7, 14, true, "SEAU", 4, new BigDecimal("880.00"), "2L", null));
    productRepository.save(new ProductJpaEntity(27L, "RCP-2L-FO", 5L, "6001234500027", "Custard", "seau", new BigDecimal("5000.00"), 7, 14, true, "SEAU", 4, new BigDecimal("880.00"), "2L", null));
    productRepository.save(new ProductJpaEntity(28L, "RCP-2L-LB", 5L, "6001234500028", "Custard", "seau", new BigDecimal("5000.00"), 7, 14, true, "SEAU", 4, new BigDecimal("880.00"), "2L", null));

    // === REINE Costard Powder 5L (seau de 1600g) — produit 29 ===
    productRepository.save(new ProductJpaEntity(29L, "RCP-5L-FO", 5L, "6001234500029", "Custard", "seau", new BigDecimal("8000.00"), 7, 14, true, "SEAU", 1, new BigDecimal("1600.00"), "5L", null));

    // === BabyVita (12 seaux x 390g / 4 seaux x 880g) — produits 30-31 ===
    productRepository.save(new ProductJpaEntity(30L, "BBV-1L", 6L, "6001234500030", "BabyVita", "seau", new BigDecimal("1900.00"), 7, 14, true, "SEAU", 12, new BigDecimal("390.00"), "1L", null));
    productRepository.save(new ProductJpaEntity(31L, "BBV-2L", 6L, "6001234500031", "BabyVita", "seau", new BigDecimal("5000.00"), 7, 14, true, "SEAU", 4, new BigDecimal("880.00"), "2L", null));

    // === Gamme 2: A Grignoter — Mini (variantId=7) — produits 32-36 ===
    productRepository.save(new ProductJpaEntity(32L, "MGN-AE", 7L, "6001234500032", "Grignoter", "sachet", new BigDecimal("100.00"), 5, 10, true, "SACHET", 80, new BigDecimal("35.00"), null, null));
    productRepository.save(new ProductJpaEntity(33L, "MGN-CA", 7L, "6001234500033", "Grignoter", "sachet", new BigDecimal("100.00"), 5, 10, true, "SACHET", 80, new BigDecimal("45.00"), null, null));
    productRepository.save(new ProductJpaEntity(34L, "MGN-CR", 7L, "6001234500034", "Grignoter", "sachet", new BigDecimal("100.00"), 5, 10, true, "SACHET", 80, new BigDecimal("30.00"), null, null));
    productRepository.save(new ProductJpaEntity(35L, "MGN-PL", 7L, "6001234500035", "Grignoter", "sachet", new BigDecimal("100.00"), 5, 10, true, "SACHET", 60, new BigDecimal("35.00"), null, null));
    productRepository.save(new ProductJpaEntity(36L, "MGN-BS", 7L, "6001234500036", "Grignoter", "doypack", new BigDecimal("150.00"), 5, 10, true, "DOYPACK", 60, new BigDecimal("36.00"), null, null));

    // === A Grignoter — Moyen (variantId=8) — produits 37-40 ===
    productRepository.save(new ProductJpaEntity(37L, "MGT-AE", 8L, "6001234500037", "Grignoter", "doypack", new BigDecimal("250.00"), 5, 10, true, "DOYPACK", 24, new BigDecimal("125.00"), null, null));
    productRepository.save(new ProductJpaEntity(38L, "MGT-CA", 8L, "6001234500038", "Grignoter", "doypack", new BigDecimal("250.00"), 5, 10, true, "DOYPACK", 24, new BigDecimal("136.00"), null, null));
    productRepository.save(new ProductJpaEntity(39L, "MGT-CR", 8L, "6001234500039", "Grignoter", "doypack", new BigDecimal("250.00"), 5, 10, true, "DOYPACK", 24, new BigDecimal("95.00"), null, null));
    productRepository.save(new ProductJpaEntity(40L, "MGT-PL", 8L, "6001234500040", "Grignoter", "doypack", new BigDecimal("300.00"), 5, 10, true, "DOYPACK", 20, new BigDecimal("100.00"), null, null));

    // === A Grignoter — Grand (variantId=9) — produits 41-43 ===
    productRepository.save(new ProductJpaEntity(41L, "GGT-AE", 9L, "6001234500041", "Grignoter", "doypack", new BigDecimal("1000.00"), 5, 10, true, "DOYPACK", 14, new BigDecimal("273.00"), null, null));
    productRepository.save(new ProductJpaEntity(42L, "GGT-CA", 9L, "6001234500042", "Grignoter", "doypack", new BigDecimal("1000.00"), 5, 10, true, "DOYPACK", 14, new BigDecimal("328.00"), null, null));
    productRepository.save(new ProductJpaEntity(43L, "GGT-CR", 9L, "6001234500043", "Grignoter", "doypack", new BigDecimal("1000.00"), 5, 10, true, "DOYPACK", 14, new BigDecimal("227.00"), null, null));

    // === Gamme 3: Ingrédients Culinaires — produit 44 (Chapelure uniquement selon catalogue Excel) ===
    // Chapelure (variantId=10) — 12 etuis x 200g, détail=600
    productRepository.save(new ProductJpaEntity(44L, "TCHAP", 10L, "6001234500044", "Ingredients", "etui", new BigDecimal("600.00"), 10, 20, true, "ETUI", 12, new BigDecimal("200.00"), null, null));

    // === Gamme 4: TANTY Choco (variantId=13) — produits 47-49 ===
    // Choco 2L (4 seaux x 2,2kg), détail=5000
    productRepository.save(new ProductJpaEntity(47L, "TCH-2L", 13L, "6001234500047", "Chocolat", "seau", new BigDecimal("5000.00"), 7, 14, true, "SEAU", 4, new BigDecimal("2200.00"), "2L", null));
    // Choco 5L (seau 4,1kg), détail=8000
    productRepository.save(new ProductJpaEntity(48L, "TCH-5L", 13L, "6001234500048", "Chocolat", "seau", new BigDecimal("8000.00"), 7, 14, true, "SEAU", 1, new BigDecimal("4100.00"), "5L", null));
    // Choco 10L (seau 9,2kg), détail=16500
    productRepository.save(new ProductJpaEntity(49L, "TCH-10L", 13L, "6001234500049", "Chocolat", "seau", new BigDecimal("16500.00"), 7, 14, true, "SEAU", 1, new BigDecimal("9200.00"), "10L", null));

    // === MATIERES PREMIERES (approvisionnement production, pas de vitrine commerciale) ===
    productRepository.save(new ProductJpaEntity(101L, "MP-SOJA", null, null, "Soja décortiqué", "KG", null, 21, 45, true, "SAC", 50, null, null, null, "MATIERE_PREMIERE"));
    productRepository.save(new ProductJpaEntity(102L, "MP-MAIS", null, null, "Maïs jaune (grain)", "KG", null, 14, 30, true, "SAC", 50, null, null, null, "MATIERE_PREMIERE"));
    productRepository.save(new ProductJpaEntity(103L, "MP-ARACHIDE", null, null, "Arachide décortiquée", "KG", null, 21, 30, true, "SAC", 50, null, null, null, "MATIERE_PREMIERE"));
    productRepository.save(new ProductJpaEntity(104L, "MP-SUCRE", null, null, "Sucre cristallisé", "KG", null, 10, 30, true, "SAC", 50, null, null, null, "MATIERE_PREMIERE"));
    productRepository.save(new ProductJpaEntity(105L, "MP-LAIT-POUDRE", null, null, "Lait en poudre", "KG", null, 30, 45, true, "SAC", 25, null, null, null, "MATIERE_PREMIERE"));
    productRepository.save(new ProductJpaEntity(106L, "MP-CACAO-POUDRE", null, null, "Cacao en poudre", "KG", null, 30, 45, true, "SAC", 25, null, null, null, "MATIERE_PREMIERE"));
    productRepository.save(new ProductJpaEntity(107L, "MP-SEL", null, null, "Sel fin alimentaire", "KG", null, 15, 30, true, "SAC", 50, null, null, null, "MATIERE_PREMIERE"));
    productRepository.save(new ProductJpaEntity(108L, "MP-HUILE-VEG", null, null, "Huile végétale (vrac)", "L", null, 20, 30, true, "BIDON", 20, null, null, null, "MATIERE_PREMIERE"));
    productRepository.save(new ProductJpaEntity(109L, "MP-POISSON-POUDRE", null, null, "Poudre de poisson", "KG", null, 21, 30, true, "SAC", 25, null, null, null, "MATIERE_PREMIERE"));
    productRepository.save(new ProductJpaEntity(110L, "MP-PLANTAIN-SEC", null, null, "Plantain séché (chips brutes)", "KG", null, 14, 21, true, "SAC", 25, null, null, null, "MATIERE_PREMIERE"));
    productRepository.save(new ProductJpaEntity(111L, "MP-PAIN", null, null, "Pain (pour chapelure)", "KG", null, 14, 21, true, "SAC", 25, null, null, null, "MATIERE_PREMIERE"));

    // === CONSOMMABLES (emballages vierges et fournitures, pas de vitrine commerciale) ===
    productRepository.save(new ProductJpaEntity(201L, "CONS-SACHET-VIERGE", null, null, "Sachets d'emballage vierges", "UNITE", null, 10, 20, true, "ROULEAU", 5000, null, null, null, "CONSOMMABLE"));
    productRepository.save(new ProductJpaEntity(202L, "CONS-ETUI-VIERGE", null, null, "Étuis carton vierges", "UNITE", null, 10, 20, true, "CARTON", 1000, null, null, null, "CONSOMMABLE"));
    productRepository.save(new ProductJpaEntity(203L, "CONS-SEAU-1L-VIDE", null, null, "Seaux plastique vides 1L", "UNITE", null, 14, 20, true, "CARTON", 200, null, null, null, "CONSOMMABLE"));
    productRepository.save(new ProductJpaEntity(204L, "CONS-SEAU-2L-VIDE", null, null, "Seaux plastique vides 2L", "UNITE", null, 14, 20, true, "CARTON", 100, null, null, null, "CONSOMMABLE"));
    productRepository.save(new ProductJpaEntity(205L, "CONS-CARTON-EMBALLAGE", null, null, "Cartons d'emballage (fardeau)", "UNITE", null, 10, 15, true, "PALETTE", 250, null, null, null, "CONSOMMABLE"));
    productRepository.save(new ProductJpaEntity(206L, "CONS-ETIQUETTE", null, null, "Étiquettes autocollantes", "UNITE", null, 10, 15, true, "ROULEAU", 2000, null, null, null, "CONSOMMABLE"));
    productRepository.save(new ProductJpaEntity(207L, "CONS-FILM-PLASTIQUE", null, null, "Film plastique operculage", "UNITE", null, 10, 15, true, "ROULEAU", 1, null, null, null, "CONSOMMABLE"));
    productRepository.save(new ProductJpaEntity(208L, "CONS-DOYPACK-VIERGE", null, null, "Doypacks vierges", "UNITE", null, 10, 20, true, "CARTON", 1000, null, null, null, "CONSOMMABLE"));

    log.info("Produits initialises: {}", productRepository.count());
  }

  // ===================== PRIX PRODUITS (grille tarifaire réelle) =====================
  private void initializeProductPrices() {
    log.info("Initialisation des prix produits (grille tarifaire 4 niveaux)...");

    // --- Bouillies Sachets (produits 1-10): Distrib=4500, Gross=4750, Détail=5000, Unité=250 ---
    for (long pid = 1L; pid <= 10L; pid++) {
      createPrice(pid, "DISTRIBUTOR", new BigDecimal("4500"), new BigDecimal("100"));
      createPrice(pid, "WHOLESALE", new BigDecimal("4750"), new BigDecimal("25"));
      createPrice(pid, "RETAIL", new BigDecimal("5000"), new BigDecimal("1"));
      createPrice(pid, "DETAIL", new BigDecimal("250"), new BigDecimal("1"));
    }

    // --- Prestige Etuis (produits 11-20): Distrib=6600, Gross=6600, Détail=7200, Unité=750 ---
    for (long pid = 11L; pid <= 20L; pid++) {
      createPrice(pid, "DISTRIBUTOR", new BigDecimal("6600"), new BigDecimal("100"));
      createPrice(pid, "WHOLESALE", new BigDecimal("6600"), new BigDecimal("25"));
      createPrice(pid, "RETAIL", new BigDecimal("7200"), new BigDecimal("1"));
      createPrice(pid, "DETAIL", new BigDecimal("750"), new BigDecimal("1"));
    }

    // --- Custard 1L (produits 21-24): Distrib=9600, Gross=9600, Détail=10200, Unité=1000 ---
    for (long pid = 21L; pid <= 24L; pid++) {
      createPrice(pid, "DISTRIBUTOR", new BigDecimal("9600"), new BigDecimal("100"));
      createPrice(pid, "WHOLESALE", new BigDecimal("9600"), new BigDecimal("25"));
      createPrice(pid, "RETAIL", new BigDecimal("10200"), new BigDecimal("1"));
      createPrice(pid, "DETAIL", new BigDecimal("1000"), new BigDecimal("1"));
    }

    // --- Custard 2L (produits 25-28): Distrib=8000, Gross=8400, Détail=8800, Unité=2500 ---
    for (long pid = 25L; pid <= 28L; pid++) {
      createPrice(pid, "DISTRIBUTOR", new BigDecimal("8000"), new BigDecimal("100"));
      createPrice(pid, "WHOLESALE", new BigDecimal("8400"), new BigDecimal("25"));
      createPrice(pid, "RETAIL", new BigDecimal("8800"), new BigDecimal("1"));
      createPrice(pid, "DETAIL", new BigDecimal("2500"), new BigDecimal("1"));
    }

    // --- Custard 5L (produit 29): Distrib=3850, Gross=3850, Détail=4000, Unité=4500 ---
    createPrice(29L, "DISTRIBUTOR", new BigDecimal("3850"), new BigDecimal("100"));
    createPrice(29L, "WHOLESALE", new BigDecimal("3850"), new BigDecimal("25"));
    createPrice(29L, "RETAIL", new BigDecimal("4000"), new BigDecimal("1"));
    createPrice(29L, "DETAIL", new BigDecimal("4500"), new BigDecimal("1"));

    // --- BabyVita 1L (produit 30): mêmes prix que Custard 1L ---
    createPrice(30L, "DISTRIBUTOR", new BigDecimal("9600"), new BigDecimal("100"));
    createPrice(30L, "WHOLESALE", new BigDecimal("9600"), new BigDecimal("25"));
    createPrice(30L, "RETAIL", new BigDecimal("10200"), new BigDecimal("1"));
    createPrice(30L, "DETAIL", new BigDecimal("1000"), new BigDecimal("1"));

    // --- BabyVita 2L (produit 31): mêmes prix que Custard 2L ---
    createPrice(31L, "DISTRIBUTOR", new BigDecimal("8000"), new BigDecimal("100"));
    createPrice(31L, "WHOLESALE", new BigDecimal("8400"), new BigDecimal("25"));
    createPrice(31L, "RETAIL", new BigDecimal("8800"), new BigDecimal("1"));
    createPrice(31L, "DETAIL", new BigDecimal("2500"), new BigDecimal("1"));

    // --- À Grignoter Mini (produits 32-36): pas de distributeur, Détail=6400, Unité=100 ---
    for (long pid = 32L; pid <= 36L; pid++) {
      createPrice(pid, "RETAIL", new BigDecimal("6400"), new BigDecimal("1"));
      createPrice(pid, "DETAIL", new BigDecimal("100"), new BigDecimal("1"));
    }

    // --- À Grignoter Moyen (produits 37-40): Détail=9120 (24-pack) ou 4000 (20-pack), Unité=500 ou 250 ---
    // 37-39: 24 doypack, prix=9120, unité=500
    for (long pid = 37L; pid <= 39L; pid++) {
      createPrice(pid, "RETAIL", new BigDecimal("9120"), new BigDecimal("1"));
      createPrice(pid, "DETAIL", new BigDecimal("500"), new BigDecimal("1"));
    }
    // 40: 20 doypack, prix=4000, unité=250
    createPrice(40L, "RETAIL", new BigDecimal("4000"), new BigDecimal("1"));
    createPrice(40L, "DETAIL", new BigDecimal("250"), new BigDecimal("1"));

    // --- À Grignoter Grand (produits 41-43): Détail=12000, Unité=1000 ---
    for (long pid = 41L; pid <= 43L; pid++) {
      createPrice(pid, "RETAIL", new BigDecimal("12000"), new BigDecimal("1"));
      createPrice(pid, "DETAIL", new BigDecimal("1000"), new BigDecimal("1"));
    }

    // --- Ingrédients Culinaires ---
    // Chapelure (produit 44): ≥25=5200, <25=5500, Unité=600
    createPrice(44L, "WHOLESALE", new BigDecimal("5200"), new BigDecimal("25"));
    createPrice(44L, "RETAIL", new BigDecimal("5500"), new BigDecimal("1"));
    createPrice(44L, "DETAIL", new BigDecimal("600"), new BigDecimal("1"));

    // --- TANTY Choco ---
    // Choco 2L (produit 47): ≥25=17600, <25=18400, Unité=5000
    createPrice(47L, "WHOLESALE", new BigDecimal("17600"), new BigDecimal("25"));
    createPrice(47L, "RETAIL", new BigDecimal("18400"), new BigDecimal("1"));
    createPrice(47L, "DETAIL", new BigDecimal("5000"), new BigDecimal("1"));
    // Choco 5L (produit 48): ≥25=7000, <25=7400, Unité=8000
    createPrice(48L, "WHOLESALE", new BigDecimal("7000"), new BigDecimal("25"));
    createPrice(48L, "RETAIL", new BigDecimal("7400"), new BigDecimal("1"));
    createPrice(48L, "DETAIL", new BigDecimal("8000"), new BigDecimal("1"));
    // Choco 10L (produit 49): ≥25=15500, <25=16000, Unité=16500
    createPrice(49L, "WHOLESALE", new BigDecimal("15500"), new BigDecimal("25"));
    createPrice(49L, "RETAIL", new BigDecimal("16000"), new BigDecimal("1"));
    createPrice(49L, "DETAIL", new BigDecimal("16500"), new BigDecimal("1"));

    log.info("Prix produits initialises: {}", productPriceRepository.count());
  }

  private void createPrice(Long productId, String priceType, BigDecimal price, BigDecimal minQuantity) {
    ProductPriceJpaEntity p = new ProductPriceJpaEntity();
    p.setProductId(productId);
    p.setPriceType(priceType);
    p.setPrice(price);
    p.setCurrency("XAF");
    p.setMinQuantity(minQuantity);
    p.setActive(true);
    productPriceRepository.save(p);
  }

  // ===================== UTILISATEURS =====================
  private void initializeUsers() {
    log.info("Initialisation des utilisateurs...");
    String pwd = passwordEncoder.encode("password123");

    // Gestionnaire de Stock
    userRepository.save(new UserJpaEntity(UUID.fromString("550e8400-e29b-41d4-a716-446655440001"), "GEST001", "Giovanni", "Charles", "+237699123456", pwd, "ROLE_STOCK", "ACTIVE"));
    // Administrateur
    userRepository.save(new UserJpaEntity(UUID.fromString("550e8400-e29b-41d4-a716-446655440002"), "ADMIN001", "Aminata", "Bello", "+237699123457", pwd, "ROLE_ADMIN", "ACTIVE"));
    // Commercial Douala
    userRepository.save(new UserJpaEntity(UUID.fromString("550e8400-e29b-41d4-a716-446655440003"), "COMM001", "Hervé", "Kamga", "+237699123458", pwd, "ROLE_COMMERCIAL", "ACTIVE"));
    // Chef Production
    userRepository.save(new UserJpaEntity(UUID.fromString("550e8400-e29b-41d4-a716-446655440004"), "PROD001", "Sandrine", "Foka", "+237699123459", pwd, "ROLE_PRODUCTION", "ACTIVE"));
    // Validateur (Controleur de Gestion)
    userRepository.save(new UserJpaEntity(UUID.fromString("550e8400-e29b-41d4-a716-446655440005"), "VALI001", "Olivier", "Mvondo", "+237699123460", pwd, "ROLE_VALIDATEUR", "ACTIVE"));
    // Commercial Yaoundé
    userRepository.save(new UserJpaEntity(UUID.fromString("550e8400-e29b-41d4-a716-446655440006"), "COMM002", "Ange", "Nkoulou", "+237695543210", pwd, "ROLE_COMMERCIAL", "ACTIVE"));
    // Commercial Bafoussam
    userRepository.save(new UserJpaEntity(UUID.fromString("550e8400-e29b-41d4-a716-446655440007"), "COMM003", "Patrice", "Tchinda", "+237677889900", pwd, "ROLE_COMMERCIAL", "ACTIVE"));
    // Direction (DG)
    userRepository.save(new UserJpaEntity(UUID.fromString("550e8400-e29b-41d4-a716-446655440008"), "DG001", "Paul", "Ntanty", "+237699000001", pwd, "ROLE_DIRECTION", "ACTIVE"));
    // Finance (Comptable)
    userRepository.save(new UserJpaEntity(UUID.fromString("550e8400-e29b-41d4-a716-446655440009"), "FIN001", "Claire", "Mbakop", "+237699111222", pwd, "ROLE_FINANCE", "ACTIVE"));
    // Magasinier
    userRepository.save(new UserJpaEntity(UUID.fromString("550e8400-e29b-41d4-a716-446655440010"), "MAG001", "Emmanuel", "Nganou", "+237699333444", pwd, "ROLE_MAGASINIER", "ACTIVE"));

    log.info("Utilisateurs initialises: {}", userRepository.count());
  }

  // ===================== EMPLACEMENTS DE STOCK =====================
  private void initializeStockLocations() {
    log.info("Initialisation des emplacements de stock...");
    stockLocationService.initializeDefaultLocations();

    // Stock mobile pour chaque commercial
    assignMobileStock("COMM001", "Stock Mobile - Hervé Kamga (Douala)");
    assignMobileStock("COMM002", "Stock Mobile - Ange Nkoulou (Yaounde)");
    assignMobileStock("COMM003", "Stock Mobile - Patrice Tchinda (Bafoussam)");

    log.info("Emplacements initialises: {}", stockLocationRepository.count());
  }

  private void assignMobileStock(String matricule, String name) {
    StockLocationJpaEntity existing = stockLocationRepository.findByAssignedUserId(matricule).orElse(null);
    if (existing == null) {
      StockLocationId id = stockLocationService.createLocation(StockLocationType.STOCK_MOBILE, name, "Stock mobile du commercial " + matricule);
      stockLocationService.assignLocationToUser(id, new com.NTFOODS.Api_tanty.shared.kernel.valueobject.UserId(matricule));
    }
  }

  // ===================== STOCK INITIAL (max 75, descendant, seuils raisonnables) =====================
  private void initializeStockItems() {
    log.info("Initialisation du stock initial (47 produits finis + matières premières + consommables)...");

    var centralLocs = stockLocationRepository.findByType(StockLocationType.STOCK_CENTRAL);
    if (centralLocs.isEmpty()) return;
    UUID centralId = centralLocs.get(0).getLocationId();

    // REINE Bouillies Sachets — produits 1-5
    createStockItem(centralId, 1L, "SACHET", new BigDecimal("75"), new BigDecimal("20"), new BigDecimal("10"));
    createStockItem(centralId, 2L, "SACHET", new BigDecimal("72"), new BigDecimal("20"), new BigDecimal("10"));
    createStockItem(centralId, 3L, "SACHET", new BigDecimal("68"), new BigDecimal("18"), new BigDecimal("9"));
    createStockItem(centralId, 4L, "SACHET", new BigDecimal("65"), new BigDecimal("18"), new BigDecimal("9"));
    createStockItem(centralId, 5L, "SACHET", new BigDecimal("60"), new BigDecimal("15"), new BigDecimal("8"));

    // TANTY Bouillies Sachets — produits 6-10
    createStockItem(centralId, 6L, "SACHET", new BigDecimal("58"), new BigDecimal("15"), new BigDecimal("8"));
    createStockItem(centralId, 7L, "SACHET", new BigDecimal("55"), new BigDecimal("15"), new BigDecimal("8"));
    createStockItem(centralId, 8L, "SACHET", new BigDecimal("52"), new BigDecimal("14"), new BigDecimal("7"));
    createStockItem(centralId, 9L, "SACHET", new BigDecimal("48"), new BigDecimal("14"), new BigDecimal("7"));
    createStockItem(centralId, 10L, "SACHET", new BigDecimal("45"), new BigDecimal("12"), new BigDecimal("6"));

    // REINE Prestige Etuis — produits 11-15
    createStockItem(centralId, 11L, "ETUI", new BigDecimal("42"), new BigDecimal("12"), new BigDecimal("6"));
    createStockItem(centralId, 12L, "ETUI", new BigDecimal("40"), new BigDecimal("12"), new BigDecimal("6"));
    createStockItem(centralId, 13L, "ETUI", new BigDecimal("38"), new BigDecimal("10"), new BigDecimal("5"));
    createStockItem(centralId, 14L, "ETUI", new BigDecimal("35"), new BigDecimal("10"), new BigDecimal("5"));
    createStockItem(centralId, 15L, "ETUI", new BigDecimal("32"), new BigDecimal("10"), new BigDecimal("5"));

    // TANTY Prestige Etuis — produits 16-20
    createStockItem(centralId, 16L, "ETUI", new BigDecimal("30"), new BigDecimal("10"), new BigDecimal("5"));
    createStockItem(centralId, 17L, "ETUI", new BigDecimal("28"), new BigDecimal("8"), new BigDecimal("4"));
    createStockItem(centralId, 18L, "ETUI", new BigDecimal("25"), new BigDecimal("8"), new BigDecimal("4"));
    createStockItem(centralId, 19L, "ETUI", new BigDecimal("22"), new BigDecimal("8"), new BigDecimal("4"));
    createStockItem(centralId, 20L, "ETUI", new BigDecimal("20"), new BigDecimal("6"), new BigDecimal("3"));

    // REINE Costard Powder 1L — produits 21-24
    createStockItem(centralId, 21L, "SEAU", new BigDecimal("18"), new BigDecimal("6"), new BigDecimal("3"));
    createStockItem(centralId, 22L, "SEAU", new BigDecimal("16"), new BigDecimal("6"), new BigDecimal("3"));
    createStockItem(centralId, 23L, "SEAU", new BigDecimal("15"), new BigDecimal("5"), new BigDecimal("3"));
    createStockItem(centralId, 24L, "SEAU", new BigDecimal("14"), new BigDecimal("5"), new BigDecimal("3"));

    // REINE Costard Powder 2L — produits 25-28
    createStockItem(centralId, 25L, "SEAU", new BigDecimal("12"), new BigDecimal("4"), new BigDecimal("2"));
    createStockItem(centralId, 26L, "SEAU", new BigDecimal("10"), new BigDecimal("4"), new BigDecimal("2"));
    createStockItem(centralId, 27L, "SEAU", new BigDecimal("8"), new BigDecimal("3"), new BigDecimal("2"));
    createStockItem(centralId, 28L, "SEAU", new BigDecimal("6"), new BigDecimal("3"), new BigDecimal("2"));

    // REINE Costard Powder 5L — produit 29
    createStockItem(centralId, 29L, "SEAU", new BigDecimal("5"), new BigDecimal("2"), new BigDecimal("1"));

    // BabyVita — produits 30-31
    createStockItem(centralId, 30L, "SEAU", new BigDecimal("15"), new BigDecimal("5"), new BigDecimal("3"));
    createStockItem(centralId, 31L, "SEAU", new BigDecimal("10"), new BigDecimal("4"), new BigDecimal("2"));

    // === Gamme 2: A Grignoter — Mini (produits 32-36) ===
    createStockItem(centralId, 32L, "SACHET", new BigDecimal("75"), new BigDecimal("20"), new BigDecimal("10"));
    createStockItem(centralId, 33L, "SACHET", new BigDecimal("70"), new BigDecimal("18"), new BigDecimal("9"));
    createStockItem(centralId, 34L, "SACHET", new BigDecimal("65"), new BigDecimal("18"), new BigDecimal("9"));
    createStockItem(centralId, 35L, "SACHET", new BigDecimal("60"), new BigDecimal("15"), new BigDecimal("8"));
    createStockItem(centralId, 36L, "DOYPACK", new BigDecimal("55"), new BigDecimal("15"), new BigDecimal("8"));

    // A Grignoter — Moyen (produits 37-40)
    createStockItem(centralId, 37L, "DOYPACK", new BigDecimal("50"), new BigDecimal("14"), new BigDecimal("7"));
    createStockItem(centralId, 38L, "DOYPACK", new BigDecimal("45"), new BigDecimal("12"), new BigDecimal("6"));
    createStockItem(centralId, 39L, "DOYPACK", new BigDecimal("40"), new BigDecimal("12"), new BigDecimal("6"));
    createStockItem(centralId, 40L, "DOYPACK", new BigDecimal("35"), new BigDecimal("10"), new BigDecimal("5"));

    // A Grignoter — Grand (produits 41-43)
    createStockItem(centralId, 41L, "DOYPACK", new BigDecimal("30"), new BigDecimal("8"), new BigDecimal("4"));
    createStockItem(centralId, 42L, "DOYPACK", new BigDecimal("25"), new BigDecimal("8"), new BigDecimal("4"));
    createStockItem(centralId, 43L, "DOYPACK", new BigDecimal("20"), new BigDecimal("6"), new BigDecimal("3"));

    // === Gamme 3: Ingrédients Culinaires (produit 44 — Chapelure) ===
    createStockItem(centralId, 44L, "ETUI", new BigDecimal("20"), new BigDecimal("6"), new BigDecimal("3"));

    // === Gamme 4: TANTY Choco (produits 47-49) ===
    createStockItem(centralId, 47L, "SEAU", new BigDecimal("12"), new BigDecimal("4"), new BigDecimal("2"));
    createStockItem(centralId, 48L, "SEAU", new BigDecimal("8"), new BigDecimal("3"), new BigDecimal("2"));
    createStockItem(centralId, 49L, "SEAU", new BigDecimal("5"), new BigDecimal("2"), new BigDecimal("1"));

    // Matières premières (stock plus volumineux)
    createStockItem(centralId, 101L, "SAC", new BigDecimal("75"), new BigDecimal("20"), new BigDecimal("10"));
    createStockItem(centralId, 102L, "SAC", new BigDecimal("70"), new BigDecimal("20"), new BigDecimal("10"));
    createStockItem(centralId, 103L, "SAC", new BigDecimal("65"), new BigDecimal("18"), new BigDecimal("9"));
    createStockItem(centralId, 104L, "SAC", new BigDecimal("60"), new BigDecimal("15"), new BigDecimal("8"));
    createStockItem(centralId, 105L, "SAC", new BigDecimal("50"), new BigDecimal("15"), new BigDecimal("8"));
    createStockItem(centralId, 106L, "SAC", new BigDecimal("45"), new BigDecimal("12"), new BigDecimal("6"));
    createStockItem(centralId, 107L, "SAC", new BigDecimal("40"), new BigDecimal("12"), new BigDecimal("6"));
    createStockItem(centralId, 108L, "BIDON", new BigDecimal("35"), new BigDecimal("10"), new BigDecimal("5"));
    createStockItem(centralId, 109L, "SAC", new BigDecimal("30"), new BigDecimal("10"), new BigDecimal("5"));
    createStockItem(centralId, 110L, "SAC", new BigDecimal("25"), new BigDecimal("8"), new BigDecimal("4"));
    createStockItem(centralId, 111L, "SAC", new BigDecimal("20"), new BigDecimal("8"), new BigDecimal("4"));

    // Consommables
    createStockItem(centralId, 201L, "ROULEAU", new BigDecimal("75"), new BigDecimal("20"), new BigDecimal("10"));
    createStockItem(centralId, 202L, "CARTON", new BigDecimal("60"), new BigDecimal("15"), new BigDecimal("8"));
    createStockItem(centralId, 203L, "CARTON", new BigDecimal("50"), new BigDecimal("15"), new BigDecimal("8"));
    createStockItem(centralId, 204L, "CARTON", new BigDecimal("40"), new BigDecimal("12"), new BigDecimal("6"));
    createStockItem(centralId, 205L, "PALETTE", new BigDecimal("30"), new BigDecimal("10"), new BigDecimal("5"));
    createStockItem(centralId, 206L, "ROULEAU", new BigDecimal("55"), new BigDecimal("15"), new BigDecimal("8"));
    createStockItem(centralId, 207L, "ROULEAU", new BigDecimal("45"), new BigDecimal("12"), new BigDecimal("6"));
    createStockItem(centralId, 208L, "CARTON", new BigDecimal("50"), new BigDecimal("15"), new BigDecimal("8"));

    // Stock Tampon — TOUS les produits finis (47 produits selon catalogue Excel)
    var bufferLocs = stockLocationRepository.findByType(StockLocationType.STOCK_BUFFER);
    if (!bufferLocs.isEmpty()) {
      UUID bufferId = bufferLocs.get(0).getLocationId();

      // REINE Bouillies Sachets — produits 1-5
      createStockItem(bufferId, 1L, "SACHET", new BigDecimal("30"), new BigDecimal("10"), new BigDecimal("5"));
      createStockItem(bufferId, 2L, "SACHET", new BigDecimal("28"), new BigDecimal("10"), new BigDecimal("5"));
      createStockItem(bufferId, 3L, "SACHET", new BigDecimal("27"), new BigDecimal("9"), new BigDecimal("5"));
      createStockItem(bufferId, 4L, "SACHET", new BigDecimal("26"), new BigDecimal("9"), new BigDecimal("5"));
      createStockItem(bufferId, 5L, "SACHET", new BigDecimal("24"), new BigDecimal("8"), new BigDecimal("4"));

      // TANTY Bouillies Sachets — produits 6-10
      createStockItem(bufferId, 6L, "SACHET", new BigDecimal("23"), new BigDecimal("8"), new BigDecimal("4"));
      createStockItem(bufferId, 7L, "SACHET", new BigDecimal("22"), new BigDecimal("8"), new BigDecimal("4"));
      createStockItem(bufferId, 8L, "SACHET", new BigDecimal("21"), new BigDecimal("7"), new BigDecimal("4"));
      createStockItem(bufferId, 9L, "SACHET", new BigDecimal("19"), new BigDecimal("7"), new BigDecimal("4"));
      createStockItem(bufferId, 10L, "SACHET", new BigDecimal("18"), new BigDecimal("6"), new BigDecimal("3"));

      // REINE Prestige Etuis — produits 11-15
      createStockItem(bufferId, 11L, "ETUI", new BigDecimal("17"), new BigDecimal("6"), new BigDecimal("3"));
      createStockItem(bufferId, 12L, "ETUI", new BigDecimal("16"), new BigDecimal("6"), new BigDecimal("3"));
      createStockItem(bufferId, 13L, "ETUI", new BigDecimal("15"), new BigDecimal("5"), new BigDecimal("3"));
      createStockItem(bufferId, 14L, "ETUI", new BigDecimal("14"), new BigDecimal("5"), new BigDecimal("3"));
      createStockItem(bufferId, 15L, "ETUI", new BigDecimal("13"), new BigDecimal("5"), new BigDecimal("3"));

      // TANTY Prestige Etuis — produits 16-20
      createStockItem(bufferId, 16L, "ETUI", new BigDecimal("12"), new BigDecimal("5"), new BigDecimal("3"));
      createStockItem(bufferId, 17L, "ETUI", new BigDecimal("11"), new BigDecimal("4"), new BigDecimal("2"));
      createStockItem(bufferId, 18L, "ETUI", new BigDecimal("10"), new BigDecimal("4"), new BigDecimal("2"));
      createStockItem(bufferId, 19L, "ETUI", new BigDecimal("9"), new BigDecimal("4"), new BigDecimal("2"));
      createStockItem(bufferId, 20L, "ETUI", new BigDecimal("8"), new BigDecimal("3"), new BigDecimal("2"));

      // REINE Costard Powder 1L — produits 21-24
      createStockItem(bufferId, 21L, "SEAU", new BigDecimal("7"), new BigDecimal("3"), new BigDecimal("2"));
      createStockItem(bufferId, 22L, "SEAU", new BigDecimal("6"), new BigDecimal("3"), new BigDecimal("2"));
      createStockItem(bufferId, 23L, "SEAU", new BigDecimal("6"), new BigDecimal("3"), new BigDecimal("2"));
      createStockItem(bufferId, 24L, "SEAU", new BigDecimal("6"), new BigDecimal("3"), new BigDecimal("2"));

      // REINE Costard Powder 2L — produits 25-28
      createStockItem(bufferId, 25L, "SEAU", new BigDecimal("5"), new BigDecimal("2"), new BigDecimal("1"));
      createStockItem(bufferId, 26L, "SEAU", new BigDecimal("4"), new BigDecimal("2"), new BigDecimal("1"));
      createStockItem(bufferId, 27L, "SEAU", new BigDecimal("3"), new BigDecimal("2"), new BigDecimal("1"));
      createStockItem(bufferId, 28L, "SEAU", new BigDecimal("3"), new BigDecimal("2"), new BigDecimal("1"));

      // REINE Costard Powder 5L — produit 29
      createStockItem(bufferId, 29L, "SEAU", new BigDecimal("2"), new BigDecimal("1"), new BigDecimal("1"));

      // BabyVita — produits 30-31
      createStockItem(bufferId, 30L, "SEAU", new BigDecimal("6"), new BigDecimal("3"), new BigDecimal("2"));
      createStockItem(bufferId, 31L, "SEAU", new BigDecimal("4"), new BigDecimal("2"), new BigDecimal("1"));

      // A Grignoter Mini — produits 32-36
      createStockItem(bufferId, 32L, "SACHET", new BigDecimal("30"), new BigDecimal("10"), new BigDecimal("5"));
      createStockItem(bufferId, 33L, "SACHET", new BigDecimal("28"), new BigDecimal("9"), new BigDecimal("5"));
      createStockItem(bufferId, 34L, "SACHET", new BigDecimal("26"), new BigDecimal("9"), new BigDecimal("5"));
      createStockItem(bufferId, 35L, "SACHET", new BigDecimal("24"), new BigDecimal("8"), new BigDecimal("4"));
      createStockItem(bufferId, 36L, "DOYPACK", new BigDecimal("22"), new BigDecimal("8"), new BigDecimal("4"));

      // A Grignoter Moyen — produits 37-40
      createStockItem(bufferId, 37L, "DOYPACK", new BigDecimal("20"), new BigDecimal("7"), new BigDecimal("4"));
      createStockItem(bufferId, 38L, "DOYPACK", new BigDecimal("18"), new BigDecimal("6"), new BigDecimal("3"));
      createStockItem(bufferId, 39L, "DOYPACK", new BigDecimal("16"), new BigDecimal("6"), new BigDecimal("3"));
      createStockItem(bufferId, 40L, "DOYPACK", new BigDecimal("14"), new BigDecimal("5"), new BigDecimal("3"));

      // A Grignoter Grand — produits 41-43
      createStockItem(bufferId, 41L, "DOYPACK", new BigDecimal("12"), new BigDecimal("4"), new BigDecimal("2"));
      createStockItem(bufferId, 42L, "DOYPACK", new BigDecimal("10"), new BigDecimal("4"), new BigDecimal("2"));
      createStockItem(bufferId, 43L, "DOYPACK", new BigDecimal("8"), new BigDecimal("3"), new BigDecimal("2"));

      // Ingrédients Culinaires — produit 44 (Chapelure)
      createStockItem(bufferId, 44L, "ETUI", new BigDecimal("8"), new BigDecimal("3"), new BigDecimal("2"));

      // TANTY Choco — produits 47-49
      createStockItem(bufferId, 47L, "SEAU", new BigDecimal("5"), new BigDecimal("2"), new BigDecimal("1"));
      createStockItem(bufferId, 48L, "SEAU", new BigDecimal("3"), new BigDecimal("2"), new BigDecimal("1"));
      createStockItem(bufferId, 49L, "SEAU", new BigDecimal("2"), new BigDecimal("1"), new BigDecimal("1"));
    }

    log.info("Stock initial cree: {} items", stockItemRepository.count());
  }

  private void createStockItem(UUID locationId, Long productId, String packagingType, BigDecimal quantity,
                               BigDecimal reorderPoint, BigDecimal safetyStock) {
    ProductJpaEntity product = productRepository.findById(productId).orElse(null);
    String sku = product != null ? product.getSku() : productId.toString();
    StockItemJpaEntity item = new StockItemJpaEntity(
      locationId, productId, sku, packagingType,
      quantity, null, null, null, null);
    item.setReorderPoint(reorderPoint);
    item.setSafetyStock(safetyStock);
    stockItemRepository.save(item);
  }

  // ===================== MOUVEMENTS DE STOCK (HISTORIQUE) =====================
  private void initializeStockMovements() {
    log.info("Initialisation des mouvements de stock...");

    var centralLocs = stockLocationRepository.findByType(StockLocationType.STOCK_CENTRAL);
    var bufferLocs = stockLocationRepository.findByType(StockLocationType.STOCK_BUFFER);
    if (centralLocs.isEmpty() || bufferLocs.isEmpty()) return;

    UUID centralId = centralLocs.get(0).getLocationId();
    UUID bufferId = bufferLocs.get(0).getLocationId();

    // Mouvements de reception production
    createMovement(StockMovementType.RECEPTION_PRODUCTION, null, centralId, 1L, "RBS-AR", "SACHET", new BigDecimal("50"), "LOT-2026-001", "Reception lot REINE Arachide sachets", "COMPLETED", LocalDateTime.now().minusDays(15));
    createMovement(StockMovementType.RECEPTION_PRODUCTION, null, centralId, 6L, "TBS-AR", "SACHET", new BigDecimal("45"), "LOT-2026-002", "Reception lot TANTY Arachide sachets", "COMPLETED", LocalDateTime.now().minusDays(12));
    createMovement(StockMovementType.RECEPTION_PRODUCTION, null, centralId, 32L, "MGN-AE", "SACHET", new BigDecimal("55"), "LOT-2026-003", "Reception lot Mini Grignoter Arachides", "COMPLETED", LocalDateTime.now().minusDays(10));
    createMovement(StockMovementType.RECEPTION_PRODUCTION, null, centralId, 21L, "RCP-1L-BA", "SEAU", new BigDecimal("20"), "LOT-2026-004", "Reception lot REINE Custard 1L Banane", "COMPLETED", LocalDateTime.now().minusDays(8));

    // Transferts central vers tampon
    createMovement(StockMovementType.TRANSFER_CENTRAL_TO_BUFFER, centralId, bufferId, 1L, "RBS-AR", "SACHET", new BigDecimal("20"), "TRF-2026-001", "Reapprovisionnement tampon bouillies REINE", "COMPLETED", LocalDateTime.now().minusDays(5));
    createMovement(StockMovementType.TRANSFER_CENTRAL_TO_BUFFER, centralId, bufferId, 32L, "MGN-AE", "SACHET", new BigDecimal("25"), "TRF-2026-002", "Reapprovisionnement tampon grignoter mini", "COMPLETED", LocalDateTime.now().minusDays(3));

    // Mouvement en attente
    createMovement(StockMovementType.RECEPTION_PRODUCTION, null, centralId, 47L, "TCH-2L", "SEAU", new BigDecimal("15"), "LOT-2026-005", "Lot TANTY Choco 2L en attente validation", "PENDING", LocalDateTime.now().minusDays(1));

    log.info("Mouvements initialises: {}", stockMovementRepository.count());
  }

  private void createMovement(StockMovementType type, UUID from, UUID to, Long productId,
                              String sku, String packaging, BigDecimal qty,
                              String ref, String notes, String status, LocalDateTime date) {
    StockMovementJpaEntity m = new StockMovementJpaEntity(type, from, to, productId, sku, packaging, qty, null, null, ref, notes);
    m.setStatus(status);
    m.setRequestedAt(date);
    stockMovementRepository.save(m);
  }
}
