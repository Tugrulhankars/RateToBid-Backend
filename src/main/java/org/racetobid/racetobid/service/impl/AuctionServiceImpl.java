package org.racetobid.racetobid.service.impl;

import org.racetobid.racetobid.dto.request.CreateAuctionRequest;
import org.racetobid.racetobid.entity.Auction;
import org.racetobid.racetobid.repository.AuctionRepository;
import org.racetobid.racetobid.service.AuctionService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AuctionServiceImpl implements AuctionService {
    private final AuctionRepository auctionRepository;

    public AuctionServiceImpl(AuctionRepository auctionRepository) {
        this.auctionRepository = auctionRepository;
    }

    @Override
    public String createAuction(CreateAuctionRequest request) {
        Auction auction=new Auction();
        auction.setStartDate(request.getStartDate());
        auction.setEndDate(request.getEndDate());
        auction.setStatus(request.getStatus());
        try {
            auctionRepository.save(auction);
            return "Auction created successfull";

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public List<Auction> getAllAuctions() {
        var auctions=auctionRepository.findAll();
        return auctions;
    }

    @Override
    public Optional<Auction> getAuctionDetailsById(Long auctionId) {
        return  auctionRepository.findById(auctionId);

    }

    @Override
    public String updateAuction(Long id, CreateAuctionRequest request) {
        return "";
    }

    @Override
    public String deleteAuction(Long id) {
        auctionRepository.deleteById(id);
        return "";
    }

    @Override
    public List<Auction> getAuctionsByCategoryId(Long categoryId) {
        return List.of();
    }
}
