package com.jasmin.housingaffordability.dto;

public record RegionCount(
  // Region code, such as a census region identifier.
  int region,

  // Number of rows belonging to that region.
  long n
) {}