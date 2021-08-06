package com.bdnrfob;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class Greeting {
    private String myText = "Hello World as Json";
    private BigDecimal id = BigDecimal.ONE;
    private Instant timeUTC = Instant.now();
}
