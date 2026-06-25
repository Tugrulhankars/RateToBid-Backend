package org.racetobid.racetobid.controller;

import org.racetobid.racetobid.dto.request.BidRequest;
import org.racetobid.racetobid.dto.ErrorMessage;
import org.racetobid.racetobid.service.BiddingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;

@Controller
public class WebSocketController {
    private static final Logger log = LoggerFactory.getLogger(WebSocketController.class);

    private final BiddingService biddingService;
    private final SimpMessagingTemplate messagingTemplate;

    public WebSocketController(BiddingService biddingService, SimpMessagingTemplate messagingTemplate) {
        this.biddingService = biddingService;
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/bid")
    public void handleBid(@Payload BidRequest bidRequest, org.springframework.messaging.simp.SimpMessageHeaderAccessor headerAccessor) {
        // JWT'den kullanıcı bilgisini al - önce headerAccessor'dan, sonra SecurityContext'ten
        String userEmail = null;

        // 1. HeaderAccessor'dan user bilgisini al
        if (headerAccessor != null && headerAccessor.getUser() != null) {
            userEmail = headerAccessor.getUser().getName();
        }

        // 2. Eğer yoksa SecurityContext'ten al
        if (userEmail == null) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            userEmail = authentication != null ? authentication.getName() : null;
        }

        log.info("handleBid çağrıldı: userEmail={}, bidRequest={}", userEmail, bidRequest);

        if (userEmail == null || userEmail.equals("anonymousUser")) {
            log.warn("Kullanıcı kimliği doğrulanamadı (anonymousUser)");
            ErrorMessage error = new ErrorMessage("UNAUTHORIZED", "Kullanıcı kimliği doğrulanamadı");
            messagingTemplate.convertAndSend("/queue/errors", error);
            return;
        }

        try {
            biddingService.placeBid(bidRequest, userEmail);
            log.info("Bid servis çağrısı başarılı: userEmail={}, itemId={}", userEmail, bidRequest.getAuctionItemId());

            // Başarılı teklif tüm dinleyicilere gönderilir (zaten BiddingService'de yapılıyor)
        } catch (RuntimeException e) {
            log.error("Bid hatası:", e);
            ErrorMessage error = new ErrorMessage("BID_ERROR", e.getMessage());
            messagingTemplate.convertAndSendToUser(userEmail, "/queue/errors", error);
        } catch (Exception e) {
            log.error("Sistem hatası:", e);
            ErrorMessage error = new ErrorMessage("SYSTEM_ERROR", "Bir hata oluştu: " + e.getMessage());
            messagingTemplate.convertAndSendToUser(userEmail, "/queue/errors", error);
        }
    }
}

