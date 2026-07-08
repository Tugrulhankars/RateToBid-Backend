package org.racetobid.racetobid.controller;


import org.racetobid.racetobid.entity.Bid;
import org.racetobid.racetobid.entity.User;
import org.racetobid.racetobid.service.BiddingService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/bidding")
public class BiddingController {

    private final BiddingService biddingService;

    public BiddingController(BiddingService biddingService) {
        this.biddingService = biddingService;
    }

    @GetMapping("/getAllBidsByAuctionItemId")
    public ResponseEntity<List<Bid>> getAllBidsByAuctionItemId(@RequestParam Long auctionItemId) {
        return ResponseEntity.ok(biddingService.getAllBidsByAuctionItemId(auctionItemId));
    }

    @DeleteMapping("/{bidId}/withdraw")
    public ResponseEntity<?> withdrawBid(
            @PathVariable Long bidId,
            @AuthenticationPrincipal User user) {
        try {
            return ResponseEntity.ok(biddingService.withdrawBid(bidId, user.getEmail()));
        } catch (org.springframework.security.access.AccessDeniedException e) {
            return ResponseEntity.status(403).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
