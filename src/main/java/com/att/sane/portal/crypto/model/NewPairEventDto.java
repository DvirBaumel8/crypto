package com.att.sane.portal.crypto.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class NewPairEventDto {
    private String token0;
    private String token1;
    private String pairAddress;
}
