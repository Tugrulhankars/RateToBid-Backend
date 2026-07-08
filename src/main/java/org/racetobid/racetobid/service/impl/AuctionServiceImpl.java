package org.racetobid.racetobid.service.impl;

import org.racetobid.racetobid.dto.request.CreateAuctionRequest;
import org.racetobid.racetobid.entity.Auction;
import org.racetobid.racetobid.entity.User;
import org.racetobid.racetobid.enums.AuctionStatus;
import org.racetobid.racetobid.repository.AuctionRepository;
import org.racetobid.racetobid.repository.AuctionItemRepository;
import org.racetobid.racetobid.repository.BidRepository;
import org.racetobid.racetobid.service.AuctionService;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class AuctionServiceImpl implements AuctionService {
    private final AuctionRepository auctionRepository;
    private final AuctionItemRepository auctionItemRepository;
    private final BidRepository bidRepository;
    private final DataSource dataSource;

    public AuctionServiceImpl(AuctionRepository auctionRepository, 
                              AuctionItemRepository auctionItemRepository,
                              BidRepository bidRepository,
                              DataSource dataSource) {
        this.auctionRepository = auctionRepository;
        this.auctionItemRepository = auctionItemRepository;
        this.bidRepository = bidRepository;
        this.dataSource = dataSource;
    }

    @jakarta.annotation.PostConstruct
    public void cleanupBrokenAuctions() {
        try {
            // Tekil kısıtlamasını kaldır (Kullanıcı birden fazla ihale oluşturabilsin)
            try (java.sql.Connection conn = dataSource.getConnection();
                 java.sql.Statement stmt = conn.createStatement()) {
                stmt.execute("ALTER TABLE auctions DROP CONSTRAINT IF EXISTS ukbcues5vm09y5po5cxai7qf6jx");
            } catch (Exception e) {
                e.printStackTrace();
            }

            var auctions = auctionRepository.findAll();
            for (Auction auction : auctions) {
                if (auction.getAuctionItem() == null || auction.getAuctionTitle() == null) {
                    auctionRepository.delete(auction);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public String createAuction(CreateAuctionRequest request) {
        Auction auction = new Auction();
        auction.setAuctionTitle(request.getAuctionTitle());
        auction.setStartDate(request.getStartDate());
        auction.setEndDate(request.getEndDate());
        if (request.getStatus() == AuctionStatus.PENDING) {
            auction.setStatus(AuctionStatus.SCHEDULED);
        } else {
            auction.setStatus(request.getStatus());
        }

        if (request.getAuctionItemId() != null) {
            var itemOpt = auctionItemRepository.findById(request.getAuctionItemId());
            if (itemOpt.isPresent()) {
                auction.setAuctionItem(itemOpt.get());
                auction.setUser(itemOpt.get().getSeller());
            }
        }

        try {
            auctionRepository.save(auction);
            return "Auction created successfully";
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Auction> getAllAuctions() {
        return auctionRepository.findByEndDateAfter(LocalDateTime.now());
    }

    @Override
    public Optional<Auction> getAuctionDetailsById(Long auctionId) {
        return auctionRepository.findById(auctionId);
    }

    @Override
    public String updateAuction(Long id, CreateAuctionRequest request) {
        return "";
    }

    @Override
    public String deleteAuction(Long id) {
        auctionRepository.deleteById(id);
        return "";
    }

    @Override
    public List<Auction> getAuctionsByCategoryId(Long categoryId) {
        return auctionRepository.findByAuctionItemCategoryIdAndEndDateAfter(categoryId, LocalDateTime.now());
    }

    @Override
    public List<Auction> getMyAuctions(User user) {
        return auctionRepository.findByUserId(user.getId());
    }

    @Override
    public List<Auction> getBidAuctions(User user) {
        return auctionRepository.findAuctionsByBidsUserId(user.getId());
    }

    @Override
    public List<Auction> getActiveAuctions() {
        return auctionRepository.findActiveAuctions(LocalDateTime.now());
    }

    @Override
    public List<Auction> getPastAuctions() {
        return auctionRepository.findPastAuctions(LocalDateTime.now());
    }

    @Override
    public List<Auction> getScheduledAuctions() {
        return auctionRepository.findScheduledAuctions(LocalDateTime.now());
    }

    @Scheduled(fixedRate = 10000) // Her 10 saniyede bir çalışır
    @Transactional
    public void checkAndTransitionAuctionStatuses() {
        LocalDateTime now = LocalDateTime.now();
        
        // 1. Zamanı gelmiş SCHEDULED ihaleleri ACTIVE yap
        List<Auction> toActivate = auctionRepository.findAuctionsToActivate(now);
        if (!toActivate.isEmpty()) {
            for (Auction auction : toActivate) {
                auction.setStatus(AuctionStatus.ACTIVE);
                auctionRepository.save(auction);
            }
        }
        
        // 2. Zamanı geçmiş ACTIVE ve SCHEDULED ihaleleri FINISHED yap
        List<Auction> toFinish = auctionRepository.findAuctionsToFinish(now);
        if (!toFinish.isEmpty()) {
            for (Auction auction : toFinish) {
                auction.setStatus(AuctionStatus.FINISHED);
                auctionRepository.save(auction);
            }
        }
    }

    @Override
    @Transactional
    public String cancelAuction(Long auctionId, String userEmail) {
        Auction auction = auctionRepository.findById(auctionId)
                .orElseThrow(() -> new RuntimeException("İhale bulunamadı"));

        // Zaten bitti veya iptal edilmişse
        if (auction.getStatus() == AuctionStatus.FINISHED) {
            throw new RuntimeException("Tamamlanmış bir ihale iptal edilemez");
        }
        if (auction.getStatus() == AuctionStatus.CANCELLED) {
            throw new RuntimeException("İhale zaten iptal edilmiş");
        }

        // Sahiplik kontrolü — ihaleyi oluşturan kullanıcı mı?
        if (auction.getUser() == null || !auction.getUser().getEmail().equalsIgnoreCase(userEmail)) {
            throw new AccessDeniedException("Sadece ihale sahibi iptali gerçekleştirebilir");
        }

        auction.setStatus(AuctionStatus.CANCELLED);
        auctionRepository.save(auction);
        return "İhale başarıyla iptal edildi";
    }
}
