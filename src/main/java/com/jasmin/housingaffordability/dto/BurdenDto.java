package com.jasmin.housingaffordability.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record BurdenDto(
    @JsonProperty("less_than_30_percent") double lessThan30Percent,
    @JsonProperty("between_30_and_50_percent") double between30And50Percent,
    @JsonProperty("greater_than_50_percent") double greaterThan50Percent
) {}