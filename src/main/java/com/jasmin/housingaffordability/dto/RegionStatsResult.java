// dto/RegionRankResult.java
package com.jasmin.housingaffordability.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record RegionStatsResult(
    @JsonProperty("region_rank") int regionRank,
    @JsonProperty("data_count") long dataCount,
    @JsonProperty("avg_income") double avgIncome,
    @JsonProperty("median_housing_cost") double medianHousingCost,
    @JsonProperty("burden_distribution") BurdenDto burdenDistribution
) {}