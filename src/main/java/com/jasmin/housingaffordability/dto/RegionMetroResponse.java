package com.jasmin.housingaffordability.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record RegionMetroResponse(
    @JsonProperty("state") String state,
    @JsonProperty("region") String region,
    @JsonProperty("region_stats") RegionStatsResult regionStats,
    @JsonProperty("metro_stats") MetroStatsResult metroStats
) {}
