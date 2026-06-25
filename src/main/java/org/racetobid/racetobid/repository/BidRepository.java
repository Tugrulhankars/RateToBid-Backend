package org.racetobid.racetobid.repository;

import org.racetobid.racetobid.entity.Bid;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BidRepository extends JpaRepository<Bid,Long> {
    List<Bid> findByAuctionAuctionItemId(Long auctionItemId);
}
