package org.racetobid.racetobid.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.racetobid.racetobid.enums.AuctionStatus;

import java.time.LocalDateTime;

@Entity
@Table(name = "auctions")
@Getter
@Setter
public class Auction extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime startDate;
    private LocalDateTime endDate;

    @Enumerated(EnumType.STRING)
    private AuctionStatus status;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "auction_item_id")
    private AuctionItem auctionItem;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
}