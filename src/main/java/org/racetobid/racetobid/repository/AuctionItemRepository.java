package org.racetobid.racetobid.repository;

import org.racetobid.racetobid.entity.AuctionItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuctionItemRepository extends JpaRepository<AuctionItem,Long> {

    @Query("SELECT a from AuctionItem  a where  a.category.id=:categoryId")
    List<AuctionItem> findAllAuctionItemsByCategoryId(Long categoryId);
}
