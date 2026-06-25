package org.racetobid.racetobid.controller;


import org.racetobid.racetobid.dto.request.CreateAuctionItemRequest;
import org.racetobid.racetobid.entity.AuctionItem;
import org.racetobid.racetobid.service.AuctionItemService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/auction-items")
public class AuctionItemController {

    private final AuctionItemService auctionItemService;

    public AuctionItemController(AuctionItemService auctionItemService) {
        this.auctionItemService = auctionItemService;
    }


    @PostMapping("/createAuctionItem")
    public ResponseEntity<String> createAuctionItem(@RequestBody CreateAuctionItemRequest request) {
        return ResponseEntity.ok(auctionItemService.addAuctionItem(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AuctionItem> getAuctionItemDetailsById(@PathVariable Long id) {
        return ResponseEntity.ok(auctionItemService.getAuctionItemDetailsById(id));
    }

    @GetMapping("/getAuctionItemsByAuctionId/{auctionId}")
    public ResponseEntity<List<AuctionItem>> getAuctionItemsByAuctionId(@PathVariable Long auctionId) {
        return ResponseEntity.ok(auctionItemService.getAuctionItemsByAuctionId(auctionId));
    }

    @GetMapping("/getAllAuctionItemsByCategory")
    public ResponseEntity<List<AuctionItem>> getAllAuctionItemsByCategory(@PathVariable Long categoryId) {
        return ResponseEntity.ok(auctionItemService.getAllAuctionItemsByCategory(categoryId));
    }

    @PutMapping("/updateAuctionItem")
    public ResponseEntity<String> updateAuctionItem(@PathVariable Long id, @RequestBody CreateAuctionItemRequest request) {
        return ResponseEntity.ok(auctionItemService.updateAuctionItem(id, request));
    }

    @DeleteMapping("/deleteAuctionItem/{id}")
    public ResponseEntity<String> deleteAuctionItem(@PathVariable Long id) {
        return ResponseEntity.ok(auctionItemService.deleteAuctionItem(id));
    }
}
