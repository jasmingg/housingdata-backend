package com.jasmin.housingaffordability.controller;

import com.jasmin.housingaffordability.dto.*;
import com.jasmin.housingaffordability.dto.RegionMetroResponse;
import com.jasmin.housingaffordability.service.MetroStatsService;
import com.jasmin.housingaffordability.service.RegionStatsService;
import com.jasmin.housingaffordability.repository.*;
import com.jasmin.housingaffordability.util.*;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


import org.springframework.web.bind.annotation.GetMapping;
import java.util.List;


@RestController  // tells springboot: this class handles HTTP requests and returns JSON
@RequestMapping("/api") // every endpoint in the class must start with /api
public class HousingAPIController {

  private final MetroStatsService metroStatsService;
  private final RegionStatsService regionStatsService;
  private final HousingDataRepository repo;

  public HousingAPIController (RegionStatsService regionStatsService, HousingDataRepository repo, MetroStatsService metroStatsService) {
    this.regionStatsService = regionStatsService;
    this.repo = repo;
    this.metroStatsService = metroStatsService;
  }

  // GET request for /api?state="ohio"&metro=1
  @GetMapping // tells springboot: this method handles GET requests to "/api"
  public RegionMetroResponse allData (@RequestParam String state, @RequestParam Integer metro) {
    Integer regionCode = StateRegionMapper.getRegionCode(state);
    String regionName = StateRegionMapper.getRegionName(regionCode);
    return new RegionMetroResponse(
      state,
      regionName,
      regionStatsService.computeRegionStats(state),
      metroStatsService.computeMetroStats(regionCode, metro));
  }

  @GetMapping("/region-stats")
  public RegionStatsResult regionRank(@RequestParam String state) {
    return regionStatsService.computeRegionStats(state);
}

  @GetMapping("/metro-stats")
  public MetroStatsResult getMethodName(@RequestParam String state, @RequestParam Integer metro) {
    Integer regionCode = StateRegionMapper.getRegionCode(state);
      return metroStatsService.computeMetroStats(regionCode, metro);
  }
  

// for testing/debugging purposes, not for production
  @GetMapping("/debug/summary")
public DebugSummaryResponse debugSummary() {
  long total = repo.countAll();

  List<RegionCount> byRegion = repo.countByRegion().stream()
    .map(r -> new RegionCount(((Number) r[0]).intValue(), ((Number) r[1]).longValue()))
    .toList();

  List<Object[]> statsRow = repo.lmedStats();
  Object [] stats = statsRow.get(0);
  double min = ((Number) stats[0]).doubleValue();
  double avg = ((Number) stats[1]).doubleValue();
  double max = ((Number) stats[2]).doubleValue();

  return new DebugSummaryResponse(total, byRegion, min, avg, max);
}

  
}
