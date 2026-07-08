package org.racetobid.racetobid.repository;

import org.racetobid.racetobid.entity.Auction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AuctionRepository extends JpaRepository<Auction, Long> {
    List<Auction> findByUserId(Long userId);

    @Query("SELECT DISTINCT b.auction FROM Bid b WHERE b.user.id = :userId")
    List<Auction> findAuctionsByBidsUserId(@Param("userId") Long userId);

    List<Auction> findByEndDateAfter(LocalDateTime dateTime);

    List<Auction> findByAuctionItemCategoryIdAndEndDateAfter(Long categoryId, LocalDateTime dateTime);

    @Query("SELECT a FROM Auction a WHERE (a.status = 'ACTIVE' OR a.status = 'SCHEDULED') AND a.startDate <= :now AND a.endDate >= :now")
    List<Auction> findActiveAuctions(@Param("now") LocalDateTime now);

    @Query("SELECT a FROM Auction a WHERE a.status = 'FINISHED' OR a.endDate < :now")
    List<Auction> findPastAuctions(@Param("now") LocalDateTime now);

    @Query("SELECT a FROM Auction a WHERE a.status = 'SCHEDULED' AND a.startDate > :now")
    List<Auction> findScheduledAuctions(@Param("now") LocalDateTime now);

    @Query("SELECT a FROM Auction a WHERE a.status = 'SCHEDULED' AND a.startDate <= :now AND a.endDate > :now")
    List<Auction> findAuctionsToActivate(@Param("now") LocalDateTime now);

    @Query("SELECT a FROM Auction a WHERE (a.status = 'ACTIVE' OR a.status = 'SCHEDULED') AND a.endDate <= :now")
    List<Auction> findAuctionsToFinish(@Param("now") LocalDateTime now);
}
