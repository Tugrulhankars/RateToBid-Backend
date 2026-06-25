package org.racetobid.racetobid.controller;


import org.racetobid.racetobid.dto.request.CreateAuctionRequest;
import org.racetobid.racetobid.entity.Auction;
import org.racetobid.racetobid.service.AuctionService;
import org.springframework.http.ResponseEntity;
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

    @PutMapping("/updateAuction")
    public ResponseEntity<String> updateAuction(@PathVariable Long id, @RequestBody CreateAuctionRequest request) {
        return ResponseEntity.ok(auctionService.updateAuction(id, request));
    }

    @DeleteMapping("/deleteAuction/{id}")
    public ResponseEntity<String> deleteAuction(@PathVariable Long id) {
        return ResponseEntity.ok(auctionService.deleteAuction(id));
    }
}
