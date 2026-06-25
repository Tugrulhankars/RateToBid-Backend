package org.racetobid.racetobid.service;

import org.racetobid.racetobid.dto.request.BidRequest;
import org.racetobid.racetobid.dto.response.BidResponse;
import org.racetobid.racetobid.entity.Bid;

import java.util.List;

public interface BiddingService {
    BidResponse placeBid(BidRequest request, String userEmail);
    List<Bid> getAllBidsByAuctionItemId(Long auctionItemId);
}
