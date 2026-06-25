package org.racetobid.racetobid.controller;

/*import org.racetobid.racetobid.entity.AuctionItem;
import org.racetobid.racetobid.entity.User;
import org.racetobid.racetobid.enums.AuctionItemStatus;
import org.racetobid.racetobid.repository.AuctionItemRepository;
import org.racetobid.racetobid.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/test")
public class TestDataController {

    private final UserRepository userRepository;
    private final AuctionItemRepository auctionItemRepository;
    private final PasswordEncoder passwordEncoder;

    public TestDataController(UserRepository userRepository, 
                             AuctionItemRepository auctionItemRepository,
                             PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.auctionItemRepository = auctionItemRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/create-data")
    public String createTestData() {
        // Test kullanıcıları oluştur
        User seller = userRepository.findByEmail("seller@test.com").orElse(null);
        if (seller == null) {
            seller = new User();
            seller.setFirstName("Satıcı");
            seller.setLastName("Test");
            seller.setEmail("seller@test.com");
            seller.setPhoneNumber("5551112233");
            seller.setPassword(passwordEncoder.encode("123456"));
            seller = userRepository.save(seller);
        }

        // Test açık artırmaları oluştur
        if (auctionItemRepository.count() == 0) {
            // Aktif açık artırma
            AuctionItem item1 = new AuctionItem();
            item1.setName("Vintage Saat");
            item1.setDescription("1950'lerden kalma nadir vintage saat. Mükemmel durumda.");
            item1.setStartPrice(500.0);
            item1.setCurrentPrice(500.0);
            item1.setStartDate(LocalDateTime.now().minusDays(1));
            item1.setEndDate(LocalDateTime.now().plusDays(2));
            item1.setStatus(AuctionItemStatus.ACTIVE);
            item1.setSeller(seller);
            auctionItemRepository.save(item1);

            // Aktif açık artırma 2
            AuctionItem item2 = new AuctionItem();
            item2.setName("Antika Tablo");
            item2.setDescription("19. yüzyıldan kalma değerli antika tablo.");
            item2.setStartPrice(1000.0);
            item2.setCurrentPrice(1000.0);
            item2.setStartDate(LocalDateTime.now().minusHours(5));
            item2.setEndDate(LocalDateTime.now().plusDays(1));
            item2.setStatus(AuctionItemStatus.ACTIVE);
            item2.setSeller(seller);
            auctionItemRepository.save(item2);

            // Aktif açık artırma 3
            AuctionItem item3 = new AuctionItem();
            item3.setName("Klasik Gitar");
            item3.setDescription("Profesyonel klasik gitar. Yüksek kalite.");
            item3.setStartPrice(750.0);
            item3.setCurrentPrice(750.0);
            item3.setStartDate(LocalDateTime.now().minusDays(2));
            item3.setEndDate(LocalDateTime.now().plusHours(12));
            item3.setStatus(AuctionItemStatus.ACTIVE);
            item3.setSeller(seller);
            auctionItemRepository.save(item3);
        }

        return "Test verileri oluşturuldu!";
    }
}*/

