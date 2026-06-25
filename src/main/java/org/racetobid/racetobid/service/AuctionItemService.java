package org.racetobid.racetobid.service;

import org.racetobid.racetobid.dto.request.CreateAuctionItemRequest;
import org.racetobid.racetobid.entity.AuctionItem;

import java.util.List;

public interface AuctionItemService {
    String addAuctionItem(CreateAuctionItemRequest request);
    List<AuctionItem> getAllAuctionItemsByCategory(Long categoryId);
    AuctionItem getAuctionItemDetailsById(Long id);
    String updateAuctionItem(Long id, CreateAuctionItemRequest request);
    String deleteAuctionItem(Long id);
    List<AuctionItem> getAuctionItemsByAuctionId(Long auctionId);
}
