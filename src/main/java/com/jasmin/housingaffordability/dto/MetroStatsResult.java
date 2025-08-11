package com.jasmin.housingaffordability.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record MetroStatsResult (
  @JsonProperty("metro_code") int metroCode,
  @JsonProperty ("metro_label") String metroLabel,
  @JsonProperty("avg_income") double avgIncome,
  @JsonProperty("median_housing_cost") double medianHousingCost,
  @JsonProperty("data_count") long dataCount,
  @JsonProperty("burden_distribution") BurdenDto burdenDistribution
)
{}
