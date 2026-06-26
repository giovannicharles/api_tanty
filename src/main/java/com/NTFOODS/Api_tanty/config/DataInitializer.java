package com.NTFOODS.Api_tanty.config;

import com.NTFOODS.Api_tanty.modules.stock.infrastructure.persistence.product.jpa.BrandJpaEntity;
import com.NTFOODS.Api_tanty.modules.stock.infrastructure.persistence.product.jpa.BrandJpaRepository;
import com.NTFOODS.Api_tanty.modules.stock.infrastructure.persistence.product.jpa.ProductJpaEntity;
import com.NTFOODS.Api_tanty.modules.stock.infrastructure.persistence.product.jpa.ProductJpaRepository;
import com.NTFOODS.Api_tanty.modules.stock.infrastructure.persistence.product.jpa.ProductLineJpaEntity;
import com.NTFOODS.Api_tanty.modules.stock.infrastructure.persistence.product.jpa.ProductLineJpaRepository;
import com.NTFOODS.Api_tanty.modules.stock.infrastructure.persistence.product.jpa.ProductVariantJpaEntity;
import com.NTFOODS.Api_tanty.modules.stock.infrastructure.persistence.product.jpa.ProductVariantJpaRepository;
import com.NTFOODS.Api_tanty.modules.users.infrastructure.persistence.user.jpa.UserJpaEntity;
import com.NTFOODS.Api_tanty.modules.users.infrastructure.persistence.user.jpa.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final BrandJpaRepository brandRepository;
    private final ProductLineJpaRepository productLineRepository;
    private final ProductVariantJpaRepository productVariantRepository;
    private final ProductJpaRepository productRepository;
    private final UserJpaRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        log.info("Starting data initialization...");
        
        // Check if data already exists
        if (brandRepository.count() > 0) {
            log.info("Data already exists, skipping initialization");
            return;
        }
        
        initializeBrands();
        initializeProductLines();
        initializeProductVariants();
        initializeProducts();
        initializeUsers();
        
        log.info("Data initialization completed");
    }

    private void initializeBrands() {
        log.info("Initializing brands...");
        
        // TANTY Brand (NTFoods SARL)
        brandRepository.save(new BrandJpaEntity(1L, "TANTY", "TNT", true));
        
        log.info("Brands initialized: {}", brandRepository.count());
    }

    private void initializeProductLines() {
        log.info("Initializing product lines...");
        
        // TANTY Brand - Product Lines
        productLineRepository.save(new ProductLineJpaEntity(1L, 1L, "TANTY Bouillies de soja", "TBS", true));
        productLineRepository.save(new ProductLineJpaEntity(2L, 1L, "REINE Bouillies de soja", "RBS", true));
        productLineRepository.save(new ProductLineJpaEntity(3L, 1L, "REINE Custard Powder", "RCP", true));
        productLineRepository.save(new ProductLineJpaEntity(4L, 1L, "TANTYA Grignoter", "TAG", true));
        productLineRepository.save(new ProductLineJpaEntity(5L, 1L, "Ingrédients Culinaires", "INC", true));
        productLineRepository.save(new ProductLineJpaEntity(6L, 1L, "TANTY CHOCO", "TCH", true));
        
        log.info("Product lines initialized: {}", productLineRepository.count());
    }

    private void initializeProductVariants() {
        log.info("Initializing product variants...");
        
        // TANTY Bouillies de soja
        productVariantRepository.save(new ProductVariantJpaEntity(1L, 1L, "Arachide", "AR"));
        productVariantRepository.save(new ProductVariantJpaEntity(2L, 1L, "Nature", "NAT"));
        productVariantRepository.save(new ProductVariantJpaEntity(3L, 1L, "Poisson", "POI"));
        productVariantRepository.save(new ProductVariantJpaEntity(4L, 1L, "Parfumé", "PAR"));
        productVariantRepository.save(new ProductVariantJpaEntity(5L, 1L, "Lactée", "LAC"));
        
        // REINE Bouillies de soja
        productVariantRepository.save(new ProductVariantJpaEntity(6L, 2L, "Arachide", "AR"));
        productVariantRepository.save(new ProductVariantJpaEntity(7L, 2L, "Classic", "CLA"));
        productVariantRepository.save(new ProductVariantJpaEntity(8L, 2L, "Poisson", "POI"));
        productVariantRepository.save(new ProductVariantJpaEntity(9L, 2L, "Lactée croissance", "LAC"));
        productVariantRepository.save(new ProductVariantJpaEntity(10L, 2L, "Multi-céréales", "MUL"));
        
        // REINE Custard Powder
        productVariantRepository.save(new ProductVariantJpaEntity(11L, 3L, "Banane", "BAN"));
        productVariantRepository.save(new ProductVariantJpaEntity(12L, 3L, "Fraise", "FRA"));
        productVariantRepository.save(new ProductVariantJpaEntity(13L, 3L, "Lactée Biscuité", "LAC"));
        productVariantRepository.save(new ProductVariantJpaEntity(14L, 3L, "Fruits et Œufs", "FRO"));
        
        // TANTYA Grignoter
        productVariantRepository.save(new ProductVariantJpaEntity(15L, 4L, "Beignets soufflés", "BEI"));
        productVariantRepository.save(new ProductVariantJpaEntity(16L, 4L, "Arachides enrobées", "ARA"));
        productVariantRepository.save(new ProductVariantJpaEntity(17L, 4L, "Caramels", "CAR"));
        productVariantRepository.save(new ProductVariantJpaEntity(18L, 4L, "Croquettes", "CRO"));
        productVariantRepository.save(new ProductVariantJpaEntity(19L, 4L, "Chips de plantain", "CHP"));
        
        // Ingrédients Culinaires
        productVariantRepository.save(new ProductVariantJpaEntity(20L, 5L, "Chapelure", "CHA"));
        productVariantRepository.save(new ProductVariantJpaEntity(21L, 5L, "Farine de soja", "FAR"));
        productVariantRepository.save(new ProductVariantJpaEntity(22L, 5L, "Huile d'arachide", "HUI"));
        
        // TANTY CHOCO
        productVariantRepository.save(new ProductVariantJpaEntity(23L, 6L, "Chocolat", "CHO"));
        
        log.info("Product variants initialized: {}", productVariantRepository.count());
    }

    private void initializeProducts() {
        log.info("Initializing products...");
        
        // TANTY Bouillies de soja - Sachets (25 per carton, 62g)
        productRepository.save(new ProductJpaEntity(1L, "TNT-AR-SAC", 1L, "1234567890001", "Bouillies", "sachet", new BigDecimal("250.00"), 7, 14, true, "SACHET", 25, new BigDecimal("62.00"), null, null));
        productRepository.save(new ProductJpaEntity(2L, "TNT-NAT-SAC", 1L, "1234567890002", "Bouillies", "sachet", new BigDecimal("250.00"), 7, 14, true, "SACHET", 25, new BigDecimal("62.00"), null, null));
        productRepository.save(new ProductJpaEntity(3L, "TNT-POI-SAC", 1L, "1234567890003", "Bouillies", "sachet", new BigDecimal("250.00"), 7, 14, true, "SACHET", 25, new BigDecimal("62.00"), null, null));
        productRepository.save(new ProductJpaEntity(4L, "TNT-PAR-SAC", 1L, "1234567890004", "Bouillies", "sachet", new BigDecimal("250.00"), 7, 14, true, "SACHET", 25, new BigDecimal("62.00"), null, null));
        productRepository.save(new ProductJpaEntity(5L, "TNT-LAC-SAC", 1L, "1234567890005", "Bouillies", "sachet", new BigDecimal("250.00"), 7, 14, true, "SACHET", 25, new BigDecimal("62.00"), null, null));
        
        // TANTY Bouillies de soja - Etuis (12 per carton, 200g)
        productRepository.save(new ProductJpaEntity(6L, "TNT-AR-ETUI", 1L, "1234567890006", "Bouillies", "étui", new BigDecimal("750.00"), 7, 14, true, "ETUI", 12, new BigDecimal("200.00"), null, null));
        productRepository.save(new ProductJpaEntity(7L, "TNT-NAT-ETUI", 1L, "1234567890007", "Bouillies", "étui", new BigDecimal("750.00"), 7, 14, true, "ETUI", 12, new BigDecimal("200.00"), null, null));
        productRepository.save(new ProductJpaEntity(8L, "TNT-POI-ETUI", 1L, "1234567890008", "Bouillies", "étui", new BigDecimal("750.00"), 7, 14, true, "ETUI", 12, new BigDecimal("200.00"), null, null));
        productRepository.save(new ProductJpaEntity(9L, "TNT-PAR-ETUI", 1L, "1234567890009", "Bouillies", "étui", new BigDecimal("750.00"), 7, 14, true, "ETUI", 12, new BigDecimal("200.00"), null, null));
        productRepository.save(new ProductJpaEntity(10L, "TNT-LAC-ETUI", 1L, "1234567890010", "Bouillies", "étui", new BigDecimal("750.00"), 7, 14, true, "ETUI", 12, new BigDecimal("200.00"), null, null));
        
        // REINE Bouillies de soja - Sachets (25 per carton, 60g)
        productRepository.save(new ProductJpaEntity(11L, "REI-AR-SAC", 2L, "1234567890011", "Bouillies", "sachet", new BigDecimal("250.00"), 7, 14, true, "SACHET", 25, new BigDecimal("60.00"), null, null));
        productRepository.save(new ProductJpaEntity(12L, "REI-CLA-SAC", 2L, "1234567890012", "Bouillies", "sachet", new BigDecimal("250.00"), 7, 14, true, "SACHET", 25, new BigDecimal("60.00"), null, null));
        productRepository.save(new ProductJpaEntity(13L, "REI-POI-SAC", 2L, "1234567890013", "Bouillies", "sachet", new BigDecimal("250.00"), 7, 14, true, "SACHET", 25, new BigDecimal("60.00"), null, null));
        productRepository.save(new ProductJpaEntity(14L, "REI-LAC-SAC", 2L, "1234567890014", "Bouillies", "sachet", new BigDecimal("250.00"), 7, 14, true, "SACHET", 25, new BigDecimal("60.00"), null, null));
        productRepository.save(new ProductJpaEntity(15L, "REI-MUL-SAC", 2L, "1234567890015", "Bouillies", "sachet", new BigDecimal("250.00"), 7, 14, true, "SACHET", 25, new BigDecimal("60.00"), null, null));
        
        // REINE Bouillies de soja - Etuis (12 per carton, 200g)
        productRepository.save(new ProductJpaEntity(16L, "REI-AR-ETUI", 2L, "1234567890016", "Bouillies", "étui", new BigDecimal("750.00"), 7, 14, true, "ETUI", 12, new BigDecimal("200.00"), null, null));
        productRepository.save(new ProductJpaEntity(17L, "REI-CLA-ETUI", 2L, "1234567890017", "Bouillies", "étui", new BigDecimal("750.00"), 7, 14, true, "ETUI", 12, new BigDecimal("200.00"), null, null));
        productRepository.save(new ProductJpaEntity(18L, "REI-POI-ETUI", 2L, "1234567890018", "Bouillies", "étui", new BigDecimal("750.00"), 7, 14, true, "ETUI", 12, new BigDecimal("200.00"), null, null));
        productRepository.save(new ProductJpaEntity(19L, "REI-LAC-ETUI", 2L, "1234567890019", "Bouillies", "étui", new BigDecimal("750.00"), 7, 14, true, "ETUI", 12, new BigDecimal("200.00"), null, null));
        productRepository.save(new ProductJpaEntity(20L, "REI-MUL-ETUI", 2L, "1234567890020", "Bouillies", "étui", new BigDecimal("750.00"), 7, 14, true, "ETUI", 12, new BigDecimal("200.00"), null, null));
        
        // REINE Custard Powder - Seaux (4 per carton, 950g)
        productRepository.save(new ProductJpaEntity(21L, "REI-CUS-BAN", 3L, "1234567890021", "Custard", "seau", new BigDecimal("2500.00"), 7, 14, true, "SEAU", 4, new BigDecimal("950.00"), null, null));
        productRepository.save(new ProductJpaEntity(22L, "REI-CUS-FRA", 3L, "1234567890022", "Custard", "seau", new BigDecimal("2500.00"), 7, 14, true, "SEAU", 4, new BigDecimal("950.00"), null, null));
        productRepository.save(new ProductJpaEntity(23L, "REI-CUS-LAC", 3L, "1234567890023", "Custard", "seau", new BigDecimal("2500.00"), 7, 14, true, "SEAU", 4, new BigDecimal("950.00"), null, null));
        productRepository.save(new ProductJpaEntity(24L, "REI-CUS-FRO", 3L, "1234567890024", "Custard", "seau", new BigDecimal("2500.00"), 7, 14, true, "SEAU", 4, new BigDecimal("950.00"), null, null));
        
        // TANTYA Grignoter - Mini (80 sachets per carton, 30g)
        productRepository.save(new ProductJpaEntity(25L, "TAN-GRN-MIN-BEI", 4L, "1234567890025", "Grignoter", "sachet", new BigDecimal("100.00"), 5, 10, true, "SACHET", 80, new BigDecimal("30.00"), null, null));
        productRepository.save(new ProductJpaEntity(26L, "TAN-GRN-MIN-ARA", 4L, "1234567890026", "Grignoter", "sachet", new BigDecimal("100.00"), 5, 10, true, "SACHET", 80, new BigDecimal("30.00"), null, null));
        productRepository.save(new ProductJpaEntity(27L, "TAN-GRN-MIN-CAR", 4L, "1234567890027", "Grignoter", "sachet", new BigDecimal("100.00"), 5, 10, true, "SACHET", 80, new BigDecimal("30.00"), null, null));
        productRepository.save(new ProductJpaEntity(28L, "TAN-GRN-MIN-CRO", 4L, "1234567890028", "Grignoter", "sachet", new BigDecimal("100.00"), 5, 10, true, "SACHET", 80, new BigDecimal("30.00"), null, null));
        productRepository.save(new ProductJpaEntity(29L, "TAN-GRN-MIN-CHP", 4L, "1234567890029", "Grignoter", "sachet", new BigDecimal("100.00"), 5, 10, true, "SACHET", 80, new BigDecimal("30.00"), null, null));
        
        // TANTYA Grignoter - Moyen (20 sachets per carton, 80g)
        productRepository.save(new ProductJpaEntity(30L, "TAN-GRN-MOY-CHP", 4L, "1234567890030", "Grignoter", "doypack", new BigDecimal("250.00"), 5, 10, true, "DOYPACK", 20, new BigDecimal("80.00"), null, null));
        
        // TANTYA Grignoter - Grand (14 sachets per carton, 350g)
        productRepository.save(new ProductJpaEntity(31L, "TAN-GRN-GRD-ARA", 4L, "1234567890031", "Grignoter", "doypack", new BigDecimal("1000.00"), 5, 10, true, "DOYPACK", 14, new BigDecimal("350.00"), null, null));
        productRepository.save(new ProductJpaEntity(32L, "TAN-GRN-GRD-CAR", 4L, "1234567890032", "Grignoter", "doypack", new BigDecimal("1000.00"), 5, 10, true, "DOYPACK", 14, new BigDecimal("350.00"), null, null));
        productRepository.save(new ProductJpaEntity(33L, "TAN-GRN-GRD-CRO", 4L, "1234567890033", "Grignoter", "doypack", new BigDecimal("1000.00"), 5, 10, true, "DOYPACK", 14, new BigDecimal("350.00"), null, null));
        
        // Ingrédients Culinaires - Chapelure (12 étuis per carton, 200g)
        productRepository.save(new ProductJpaEntity(34L, "TAN-CHA-ETUI", 5L, "1234567890034", "Ingrédients", "étui", new BigDecimal("600.00"), 10, 20, true, "ETUI", 12, new BigDecimal("200.00"), null, null));
        
        // Ingrédients Culinaires - Farine de soja (12 étuis per carton, 200g)
        productRepository.save(new ProductJpaEntity(35L, "TAN-FAR-ETUI", 5L, "1234567890035", "Ingrédients", "étui", new BigDecimal("500.00"), 10, 20, true, "ETUI", 12, new BigDecimal("200.00"), null, null));
        
        // Ingrédients Culinaires - Huile d'arachide (8 bouteilles per carton, 1L)
        productRepository.save(new ProductJpaEntity(36L, "TAN-HUI-BOT", 5L, "1234567890036", "Ingrédients", "bouteille", new BigDecimal("2500.00"), 14, 28, true, "BOUTEILLE", 8, null, "1L", null));
        
        // TANTY CHOCO - Seaux (1L, 2L, 5L, 10L)
        productRepository.save(new ProductJpaEntity(37L, "TAN-CHO-1L", 6L, "1234567890037", "Chocolat", "seau", new BigDecimal("1900.00"), 7, 14, true, "SEAU", null, null, "1L", null));
        productRepository.save(new ProductJpaEntity(38L, "TAN-CHO-2L", 6L, "1234567890038", "Chocolat", "seau", new BigDecimal("5000.00"), 7, 14, true, "SEAU", 4, null, "2L", null));
        productRepository.save(new ProductJpaEntity(39L, "TAN-CHO-5L", 6L, "1234567890039", "Chocolat", "seau", new BigDecimal("8000.00"), 7, 14, true, "SEAU", null, null, "5L", null));
        productRepository.save(new ProductJpaEntity(40L, "TAN-CHO-10L", 6L, "1234567890040", "Chocolat", "seau", new BigDecimal("16500.00"), 7, 14, true, "SEAU", null, null, "10L", null));
        
        log.info("Products initialized: {}", productRepository.count());
    }

    private void initializeUsers() {
        log.info("Initializing users...");
        
        String password = passwordEncoder.encode("password123");
        
        // GESTIONNAIRE DE STOCK
        userRepository.save(new UserJpaEntity(UUID.fromString("550e8400-e29b-41d4-a716-446655440001"), "GEST001", "Jean", "Dupont", "+237699123456", password, "ROLE_STOCK", "ACTIVE"));
        
        // ADMIN
        userRepository.save(new UserJpaEntity(UUID.fromString("550e8400-e29b-41d4-a716-446655440002"), "ADMIN001", "Marie", "Martin", "+237699123457", password, "ROLE_ADMIN", "ACTIVE"));
        
        // COMMERCIAL
        userRepository.save(new UserJpaEntity(UUID.fromString("550e8400-e29b-41d4-a716-446655440003"), "COMM001", "Pierre", "Bernard", "+237699123458", password, "ROLE_COMMERCIAL", "ACTIVE"));
        
        // CHEF PRODUCTION
        userRepository.save(new UserJpaEntity(UUID.fromString("550e8400-e29b-41d4-a716-446655440004"), "PROD001", "Sophie", "Petit", "+237699123459", password, "ROLE_PRODUCTION", "ACTIVE"));
        
        // VALIDATEUR
        userRepository.save(new UserJpaEntity(UUID.fromString("550e8400-e29b-41d4-a716-446655440005"), "VALI001", "Luc", "Dubois", "+237699123460", password, "ROLE_VALIDATEUR", "ACTIVE"));
        
        log.info("Users initialized: {}", userRepository.count());
    }
}
