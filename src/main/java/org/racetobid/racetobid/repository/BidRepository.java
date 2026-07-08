package org.racetobid.racetobid.repository;

import org.racetobid.racetobid.entity.Bid;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BidRepository extends JpaRepository<Bid, Long> {
    List<Bid> findByAuctionAuctionItemId(Long auctionItemId);
    boolean existsByUserIdAndAuctionId(Long userId, Long auctionId);

    // Bir auction'daki en yüksek teklifi getir
    @Query("SELECT b FROM Bid b WHERE b.auction.id = :auctionId ORDER BY b.amount DESC")
    List<Bid> findByAuctionIdOrderByAmountDesc(@Param("auctionId") Long auctionId);

    // Bir auction'daki belirli kullanıcının tekliflerini getir
    List<Bid> findByAuctionIdAndUserId(Long auctionId, Long userId);
}
