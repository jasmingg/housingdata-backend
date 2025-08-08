// dto/RegionRankResult.java
package com.jasmin.housingaffordability.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record RegionRankResult(
    @JsonProperty("region_rank") int regionRank,
    @JsonProperty("avg_income") double avgIncome,
    @JsonProperty("data_count") int dataCount
) {}
