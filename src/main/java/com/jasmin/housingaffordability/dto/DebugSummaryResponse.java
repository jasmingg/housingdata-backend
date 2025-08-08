package com.jasmin.housingaffordability.dto;

import java.util.List;
public record DebugSummaryResponse (
  long totalRows,
  List<RegionCount> byRegion,
  double minLmed,
  double avgLmed,
  double maxLmed
)
{}
