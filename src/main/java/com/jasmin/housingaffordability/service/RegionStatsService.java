package com.jasmin.housingaffordability.service;

import org.springframework.stereotype.Service;

import com.jasmin.housingaffordability.repository.HousingDataRepository;
import com.jasmin.housingaffordability.dto.*;
import com.jasmin.housingaffordability.util.StateRegionMapper;

import java.util.List;

@Service
public class RegionStatsService {
  private final HousingDataRepository repo;

  public RegionStatsService (HousingDataRepository repo) {
    this.repo = repo;
  }

  public int calculateRanking(String state) {
    Integer userRegionCode = StateRegionMapper.getRegionCode(state); // best to use Integer object, it can hold null
    if (userRegionCode == null) throw new IllegalArgumentException("Unknown state: " + state);
    
    List<Object []> row = repo.rankRegions(); // each row: [regionCode]

    for (int i = 0; i < row.size(); i++) {
      int currRegionCode = ((Number) row.get(i)[0]).intValue(); // converting from Object[] to Number object to int
      if (currRegionCode == userRegionCode) {
        // rank is position + 1 because lists are 0-based
        int rank = i + 1;
        return rank;
      }
    }
    // if we didn’t see the user’s region among the 4, that’s a data problem
      throw new IllegalStateException("User region not found in region averages.");
  }

// To reference in List variable row
//   row[0] = data_count                (Long)
//   row[1] = avg_income                (Double)
//   row[2] = median_housing_cost*      (Double)  *approximation via AVG(costmed)
//   row[3] = less_than_30_percent      (Double; 0.0–1.0 proportion)
//   row[4] = between_30_and_50_percent (Double; 0.0–1.0 proportion)
//   row[5] = greater_than_50_percent   (Double; 0.0–1.0 proportion)

    // returning a record for clean JSON
  public RegionStatsResult computeRegionStats(String state) {
      Integer userRegionCode = StateRegionMapper.getRegionCode(state); // best to use Integer object, it can hold null
      if (userRegionCode == null) throw new IllegalArgumentException("Unknown state: " + state);
      int ranking = calculateRanking(state); // each row: [regionCode, dataPerRegion, avgIncome]
      List<Object[]> row = repo.findRegionDetailStats(userRegionCode);
      long dataCount = ((Number) row.get(0)[0]).longValue();
      double avgIncome = ((Number) row.get(0)[1]).doubleValue();
      double medianHousingCost = ((Number) row.get(0)[2]).doubleValue();
      double under30p = ((Number) row.get(0)[3]).doubleValue();
      double between30and50p = ((Number) row.get(0)[4]).doubleValue();
      double over50p = ((Number) row.get(0)[5]).doubleValue();

              // normalize tiny drift - helps to add up to exact 1.0 for missing data values
    double s = under30p + between30and50p + over50p;
    if (s > 0 && Math.abs(s - 1.0) > 1e-6) {
      under30p        /= s;
      between30and50p /= s;
      over50p         /= s;
    }
    
      return (new RegionStatsResult(
        ranking,
        dataCount,
        avgIncome,
        medianHousingCost,
        new BurdenDto(under30p, between30and50p, over50p)
      ));
      
  }
}

