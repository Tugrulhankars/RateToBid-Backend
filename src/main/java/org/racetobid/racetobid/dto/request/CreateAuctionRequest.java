package org.racetobid.racetobid.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.racetobid.racetobid.enums.AuctionStatus;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateAuctionRequest {
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private AuctionStatus status;


}
