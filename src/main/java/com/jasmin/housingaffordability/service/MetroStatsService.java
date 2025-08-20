package com.jasmin.housingaffordability.service;

import org.springframework.stereotype.Service;

import java.util.List;

import com.jasmin.housingaffordability.repository.HousingDataRepository;
import com.jasmin.housingaffordability.dto.*;

@Service
public class MetroStatsService {
  private final HousingDataRepository repo;

  public MetroStatsService (HousingDataRepository repo) {
    this.repo = repo;
  }

  // Result (a list of Object[]):
//   row[0] = data_count                (Long)
//   row[1] = avg_income                (Double)
//   row[2] = median_housing_cost*      (Double)  *approximated via AVG(costmed)
//   row[3] = less_than_30_percent      (Double; 0.0–1.0)
//   row[4] = between_30_and_50_percent (Double; 0.0–1.0)
//   row[5] = greater_than_50_percent   (Double; 0.0–1.0)

  public MetroStatsResult computeMetroStats (Integer regionCode, Integer metroCode) {
    String metroName;

    List<Object[]> row = repo.findMetroStatsForRegion(regionCode, metroCode);


    if (metroCode == null) throw new IllegalArgumentException("Unknown metro code: " + metroCode);
    else if (metroCode == 1) {
      metroName = "Central City";
    }
    else if (metroCode == 3) {
      metroName = "Suburban Area";
    }
    else if (metroCode == 5) {
      metroName = "Nonmetropolitan area";
    }
    else {
      throw new IllegalArgumentException("Unknown metro name: " + metroCode);
    }
    double avgIncome = ( (Number)row.get(0)[1] ).doubleValue();
    double medianHousingCost = ( (Number)row.get(0)[2] ).doubleValue();
    long dataCount = ( (Number)row.get(0)[0] ).longValue();
    double under30p = ( (Number)row.get(0)[3] ).doubleValue();
    double between30and50p = ( (Number)row.get(0)[4] ).doubleValue();
    double over50p = ( (Number)row.get(0)[5] ).doubleValue();

        // normalize tiny drift - helps to add up to exact 1.0 for missing data values
    double s = under30p + between30and50p + over50p;
    if (s > 0 && Math.abs(s - 1.0) > 1e-6) {
      under30p        /= s;
      between30and50p /= s;
      over50p         /= s;
    }

    return (new MetroStatsResult(
      metroCode,
      metroName,
      avgIncome,
      medianHousingCost,
      dataCount,
      new BurdenDto(under30p, between30and50p, over50p)
         )
    );
  }

}
