package org.racetobid.racetobid;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class RaceToBidApplication {

    public static void main(String[] args) {
        SpringApplication.run(RaceToBidApplication.class, args);
    }

}
