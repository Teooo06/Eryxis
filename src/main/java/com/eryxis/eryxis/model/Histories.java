package com.eryxis.eryxis.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Histories {
    private String symbol;
    private List<HistoricalEntry> historical;


    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Setter
    public static class HistoricalEntry {
        private String date;
        private double close;
    }
}
