package org.racetobid.racetobid.dto.response;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
@AllArgsConstructor
@RequiredArgsConstructor
public class BidResponse {
    private Long id;
    private BigDecimal currentPrice;
    private String username;
    private String firstName;
    private String lastName;
}
