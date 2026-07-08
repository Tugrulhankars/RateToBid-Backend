package org.racetobid.racetobid.service.impl;


import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.trace.Tracer;
import org.racetobid.racetobid.dto.request.BidRequest;
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
    private final Tracer tracer;

    public BiddingServiceImpl(AuctionItemRepository auctionItemRepository, 
                              BidRepository bidRepository, 
                              SimpMessagingTemplate messagingTemplate,
                              UserRepository userRepository, 
                              AuctionService auctionService,
                              OpenTelemetry openTelemetry) {
        this.auctionItemRepository = auctionItemRepository;
        this.bidRepository = bidRepository;
        this.messagingTemplate = messagingTemplate;
        this.userRepository = userRepository;
        this.auctionService = auctionService;
        this.tracer = openTelemetry.getTracer(BiddingServiceImpl.class.getName());
    }

    @Override
    @Transactional
    public BidResponse placeBid(BidRequest request, String userEmail){
        var span=tracer.spanBuilder("placeBid")
                        .startSpan();
        log.info("TEKLİF WS: user={} itemId={} amount={}", userEmail, request.getAuctionItemId(), request.getAmount());

        //Auction bul
        Auction auction = auctionService.getAuctionDetailsById(request.getAuctionId())
                .orElseThrow(() -> new RuntimeException("Açık artırma bulunamadı"));

        // Auction item'ı bul (Kilitli olarak getir - Concurrency control)
        AuctionItem item = auctionItemRepository.findByIdForUpdate(request.getAuctionItemId())
                .orElseThrow(() -> new RuntimeException("Açık artırma ürünü bulunamadı"));
        log.info("Auction item bulundu id={}, mevcut fiyat={}", item.getId(), item.getCurrentPrice());

        // Kullanıcıyı bul
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));
        log.info("User bulundu: {}", user.getEmail());

        // Mağaza hesapları teklif veremez
        if (user.getUserType() == org.racetobid.racetobid.enums.UserType.STORE) {
            throw new RuntimeException("Mağaza hesapları açık artırmaya teklif veremez");
        }

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
        span.setAttribute("bid.userId",bid.getUser().getId());
        span.setAttribute("bid.Id",bid.getId());
        span.setAttribute("bid.amount",bid.getAmount().toString());
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
        response.setFirstName(user.getFirstName());
        response.setLastName(user.getLastName());
        log.info("Broadcast: /topic/auction/{} response={}", item.getId(), response);

        // Tüm dinleyicilere yeni teklifi gönder
        // Frontend: subscribe("/topic/auction/" + auctionItemId)
        messagingTemplate.convertAndSend("/topic/auction/" + item.getId(), response);
        span.end();
        return response;
    }

    @Override
    public List<Bid> getAllBidsByAuctionItemId(Long auctionItemId) {
        return bidRepository.findByAuctionAuctionItemId(auctionItemId);
    }

    @Override
    @Transactional
    public String withdrawBid(Long bidId, String userEmail) {
        // Teklifi bul
        Bid bid = bidRepository.findById(bidId)
                .orElseThrow(() -> new RuntimeException("Teklif bulunamadı"));

        // Sahiplik kontrolü
        if (!bid.getUser().getEmail().equalsIgnoreCase(userEmail)) {
            throw new org.springframework.security.access.AccessDeniedException("Bu teklif size ait değil");
        }

        // 5 dakika süresi kontrolü
        if (bid.getBidTime() == null ||
                java.time.Duration.between(bid.getBidTime(), LocalDateTime.now()).toMinutes() >= 5) {
            throw new RuntimeException("Teklif geri çekme süresi (5 dakika) dolmuştur");
        }

        // İhale aktif mi?
        Auction auction = bid.getAuction();
        if (auction.getStatus() == org.racetobid.racetobid.enums.AuctionStatus.FINISHED ||
            auction.getStatus() == org.racetobid.racetobid.enums.AuctionStatus.CANCELLED) {
            throw new RuntimeException("Tamamlanmış veya iptal edilmiş bir ihalede teklif geri çekilemez");
        }

        AuctionItem item = auction.getAuctionItem();

        // Bu teklif en yüksek teklifse, önceki en yüksek teklife geri dön
        List<Bid> allBids = bidRepository.findByAuctionIdOrderByAmountDesc(auction.getId());
        boolean isHighestBid = !allBids.isEmpty() && allBids.get(0).getId().equals(bidId);

        bidRepository.delete(bid);

        if (isHighestBid) {
            // Silindikten sonra kalanlar arasında en yüksek teklifi bul
            List<Bid> remainingBids = bidRepository.findByAuctionIdOrderByAmountDesc(auction.getId());
            if (remainingBids.isEmpty()) {
                // Hiç teklif kalmadıysa başlangıç fiyatına dön
                item.setCurrentPrice(item.getStartPrice());
            } else {
                item.setCurrentPrice(remainingBids.get(0).getAmount());
            }
            auctionItemRepository.save(item);

            // Tüm dinleyicilere geri çekme haberini gönder
            BidResponse rollbackResponse = new BidResponse();
            rollbackResponse.setId(-1L); // sentinel — geri çekme sinyali
            rollbackResponse.setCurrentPrice(item.getCurrentPrice());
            rollbackResponse.setUsername("SYSTEM");
            messagingTemplate.convertAndSend("/topic/auction/" + item.getId(), rollbackResponse);
        }

        return "Teklif başarıyla geri çekildi";
    }
}
