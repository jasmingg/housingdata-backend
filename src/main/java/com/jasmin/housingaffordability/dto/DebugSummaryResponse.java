package com.jasmin.housingaffordability.dto;

import java.util.List;

public record DebugSummaryResponse(
  // Total number of rows in the dataset.
  long totalRows,

  // Count of rows grouped by region code.
  List<RegionCount> byRegion,

  // Minimum value of lmed across all rows.
  double minLmed,

  // Average value of lmed across all rows.
  double avgLmed,

  // Maximum value of lmed across all rows.
  double maxLmed
) {}
