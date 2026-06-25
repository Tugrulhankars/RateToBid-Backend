package org.racetobid.racetobid.service.impl;


import org.racetobid.racetobid.dto.request.BidRequest;
import org.racetobid.racetobid.dto.response.BidResponse;
import org.racetobid.racetobid.entity.Auction;
import org.racetobid.racetobid.entity.AuctionItem;
import org.racetobid.racetobid.entity.Bid;

import org.racetobid.racetobid.entity.User;
import org.racetobid.racetobid.enums.AuctionItemStatus;
import org.racetobid.racetobid.enums.AuctionStatus;
import org.racetobid.racetobid.repository.AuctionItemRepository;
import org.racetobid.racetobid.repository.AuctionRepository;
import org.racetobid.racetobid.repository.BidRepository;
//import org.racetobid.racetobid.repository.UserRepository;
import org.racetobid.racetobid.repository.UserRepository;
import org.racetobid.racetobid.service.AuctionService;
import org.racetobid.racetobid.service.BiddingService;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class BiddingServiceImpl implements BiddingService {
    private static final Logger log = LoggerFactory.getLogger(BiddingServiceImpl.class);
    private final AuctionItemRepository auctionItemRepository;
    private final BidRepository bidRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final UserRepository userRepository;
    private final AuctionService auctionService;

    public BiddingServiceImpl(AuctionItemRepository auctionItemRepository, BidRepository bidRepository, SimpMessagingTemplate messagingTemplate,
                              UserRepository userRepository, AuctionService auctionService
    ) {
        this.auctionItemRepository = auctionItemRepository;
        this.bidRepository = bidRepository;
        this.messagingTemplate = messagingTemplate;
        this.userRepository = userRepository;
        this.auctionService = auctionService;
    }

    @Override
    @Transactional
    public BidResponse placeBid(BidRequest request, String userEmail){
        log.info("TEKLİF WS: user={} itemId={} amount={}", userEmail, request.getAuctionItemId(), request.getAmount());

        //Auction bul
        Auction auction = auctionService.getAuctionDetailsById(request.getAuctionId())
                .orElseThrow(() -> new RuntimeException("Açık artırma bulunamadı"));

        // Auction item'ı bul
        AuctionItem item = auctionItemRepository.findById(request.getAuctionItemId())
                .orElseThrow(() -> new RuntimeException("Açık artırma ürünü bulunamadı"));
        log.info("Auction item bulundu id={}, mevcut fiyat={}", item.getId(), item.getCurrentPrice());

        // Kullanıcıyı bul
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));
        log.info("User bulundu: {}", user.getEmail());

        // Açık artırma durumu kontrolü
        if (auction.getStatus() == AuctionStatus.FINISHED){
            throw new RuntimeException("Açık artırma bitti");
        }

        // Zaman kontrolü
        if (auction.getEndDate() != null && LocalDateTime.now().isAfter(auction.getEndDate())) {
            throw new RuntimeException("Açık artırma süresi dolmuştur");
        }



        // Satıcı kendi ürününe teklif veremez
        if (user.getId().equals(auction.getUser().getId())){
            throw new RuntimeException("Satıcı kendi ürününe teklif veremez");
        }


        // Teklif miktarı kontrolü
        if (request.getAmount().compareTo(item.getCurrentPrice()) <= 0) {
            throw new RuntimeException("Teklif fiyatı mevcut fiyattan daha yüksek olmalıdır");
        }


        // Mevcut fiyattan daha yüksek teklif kontrolü


        // Yeni teklifi kaydet
        Bid bid = new Bid();
        bid.setUser(user);
        bid.setBidTime(LocalDateTime.now());
        bid.setAuction(auction);
        bid.setAmount(request.getAmount());


        bid = bidRepository.save(bid);
        log.info("Bid kaydedildi: id={} amount={}", bid.getId(), bid.getAmount());

        // Auction item'ın güncel fiyatını güncelle
        item.setCurrentPrice(request.getAmount());
        auctionItemRepository.save(item);
        log.info("Auction item güncellendi: id={}, fiyat={}", item.getId(), item.getCurrentPrice());

        // Response oluştur
        BidResponse response = new BidResponse();
        response.setId(bid.getId());
        response.setCurrentPrice(bid.getAmount());
        response.setUsername(user.getEmail());
        log.info("Broadcast: /topic/auction/{} response={}", item.getId(), response);

        // Tüm dinleyicilere yeni teklifi gönder
        // Frontend: subscribe("/topic/auction/" + auctionItemId)
        messagingTemplate.convertAndSend("/topic/auction/" + item.getId(), response);

        return response;
    }

    @Override
    public List<Bid> getAllBidsByAuctionItemId(Long auctionItemId) {
        return bidRepository.findByAuctionAuctionItemId(auctionItemId);
    }
}
