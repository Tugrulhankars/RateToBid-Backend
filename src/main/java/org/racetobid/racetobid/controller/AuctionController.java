package org.racetobid.racetobid.controller;

import org.racetobid.racetobid.dto.request.CreateAuctionRequest;
import org.racetobid.racetobid.entity.Auction;
import org.racetobid.racetobid.entity.User;
import org.racetobid.racetobid.service.AuctionService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/auctions")
public class AuctionController {

    private final AuctionService auctionService;

    public AuctionController(AuctionService auctionService) {
        this.auctionService = auctionService;
    }

    @PostMapping("/createAuction")
    public ResponseEntity<String> createAuction(@RequestBody CreateAuctionRequest request) {
        return ResponseEntity.ok(auctionService.createAuction(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Optional<Auction>> getAuctionDetailsById(@PathVariable Long id) {
        return ResponseEntity.ok(auctionService.getAuctionDetailsById(id));
    }

    @GetMapping("/getAllAuctions")
    public ResponseEntity<List<Auction>> getAllAuctions() {
        return ResponseEntity.ok(auctionService.getAllAuctions());
    }

    @GetMapping("/getAuctionsByCategoryId/{categoryId}")
    public ResponseEntity<List<Auction>> getAuctionsByCategoryId(@PathVariable Long categoryId) {
        return ResponseEntity.ok(auctionService.getAuctionsByCategoryId(categoryId));
    }

    @GetMapping("/my-auctions")
    public ResponseEntity<List<Auction>> getMyAuctions(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(auctionService.getMyAuctions(user));
    }

    @GetMapping("/bid-auctions")
    public ResponseEntity<List<Auction>> getBidAuctions(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(auctionService.getBidAuctions(user));
    }

    @GetMapping("/active")
    public ResponseEntity<List<Auction>> getActiveAuctions() {
        return ResponseEntity.ok(auctionService.getActiveAuctions());
    }

    @GetMapping("/past")
    public ResponseEntity<List<Auction>> getPastAuctions() {
        return ResponseEntity.ok(auctionService.getPastAuctions());
    }

    @GetMapping("/scheduled")
    public ResponseEntity<List<Auction>> getScheduledAuctions() {
        return ResponseEntity.ok(auctionService.getScheduledAuctions());
    }

    @PutMapping("/updateAuction")
    public ResponseEntity<String> updateAuction(@PathVariable Long id, @RequestBody CreateAuctionRequest request) {
        return ResponseEntity.ok(auctionService.updateAuction(id, request));
    }

    @DeleteMapping("/deleteAuction/{id}")
    public ResponseEntity<String> deleteAuction(@PathVariable Long id) {
        return ResponseEntity.ok(auctionService.deleteAuction(id));
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<?> cancelAuction(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        try {
            return ResponseEntity.ok(auctionService.cancelAuction(id, user.getEmail()));
        } catch (org.springframework.security.access.AccessDeniedException e) {
            return ResponseEntity.status(403).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
