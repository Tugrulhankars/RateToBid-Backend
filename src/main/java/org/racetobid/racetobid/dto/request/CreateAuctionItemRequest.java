package org.racetobid.racetobid.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;


@Setter
@Getter
public class CreateAuctionItemRequest {

    private String name;
    private String description;
    private BigDecimal startPrice;
    private BigDecimal currentPrice;
    private Long categoryId;
    private Long sellerId;
}
