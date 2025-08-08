package com.jasmin.housingaffordability.controller;

import com.jasmin.housingaffordability.dto.*;
import com.jasmin.housingaffordability.service.RegionRankService;
import com.jasmin.housingaffordability.repository.*;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


import org.springframework.web.bind.annotation.GetMapping;
import java.util.List;


@RestController  // tells springboot: this class handles HTTP requests and returns JSON
@RequestMapping("/api") // every endpoint in the class must start with /api
public class HousingAPIController {
  private final RegionRankService regionRankService;
  private final HousingDataRepository repo;

  public HousingAPIController (RegionRankService regionRankService, HousingDataRepository repo) {
    this.regionRankService = regionRankService;
    this.repo = repo;
  }

  // GET request for /api?state="ohio"
  @GetMapping // tells springboot: this method handles GET requests to "/api"
  public RegionRankResult allData (@RequestParam String state) {;
    return regionRankService.computeRegionRankWithStats(state);
  }

  @GetMapping("/region-rank-stats")
  public RegionRankResult regionRank(@RequestParam String state) {
    return regionRankService.computeRegionRankWithStats(state);
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
