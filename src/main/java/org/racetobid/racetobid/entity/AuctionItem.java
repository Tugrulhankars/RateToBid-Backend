package org.racetobid.racetobid.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.racetobid.racetobid.enums.AuctionItemStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "auction_items")
@Getter
@Setter
public class AuctionItem extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(length = 2000)
    private String description;

    private BigDecimal startPrice;

    private BigDecimal currentPrice;

    @Version
    private Long version;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id")
    private User seller;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;
}
