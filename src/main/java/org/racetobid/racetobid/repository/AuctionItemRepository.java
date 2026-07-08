package org.racetobid.racetobid.repository;

import org.racetobid.racetobid.entity.AuctionItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import jakarta.persistence.LockModeType;

import java.util.List;
import java.util.Optional;

@Repository
public interface AuctionItemRepository extends JpaRepository<AuctionItem,Long> {

    @Query("SELECT a from AuctionItem  a where  a.category.id=:categoryId")
    List<AuctionItem> findAllAuctionItemsByCategoryId(Long categoryId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT a FROM AuctionItem a WHERE a.id = :id")
    Optional<AuctionItem> findByIdForUpdate(@Param("id") Long id);
}
