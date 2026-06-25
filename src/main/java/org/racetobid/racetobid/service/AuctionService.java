package org.racetobid.racetobid.service;

import org.racetobid.racetobid.dto.request.CreateAuctionRequest;
import org.racetobid.racetobid.entity.Auction;

import java.util.List;
import java.util.Optional;

public interface AuctionService {

    String createAuction(CreateAuctionRequest request);
    List<Auction> getAllAuctions();
    Optional<Auction> getAuctionDetailsById(Long auctionId);
    String updateAuction(Long id, CreateAuctionRequest request);
    String deleteAuction(Long id);
    List<Auction> getAuctionsByCategoryId(Long categoryId);
}
