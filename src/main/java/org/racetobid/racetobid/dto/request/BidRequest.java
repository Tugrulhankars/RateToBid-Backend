package org.racetobid.racetobid.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BidRequest {
    private Long auctionId;
    private Long auctionItemId;
    private BigDecimal amount;
}

