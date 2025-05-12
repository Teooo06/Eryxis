package com.eryxis.eryxis.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Azioni {
    private String symbol;
    private String name;
    private double price;
    private String exchange;
    private String exchangeShortName;
    private String type;

}
