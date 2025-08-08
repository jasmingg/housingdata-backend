package com.jasmin.housingaffordability.service;

import org.springframework.stereotype.Service;

import com.jasmin.housingaffordability.repository.HousingDataRepository;
import com.jasmin.housingaffordability.dto.RegionRankResult;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Collections;
import java.util.List;

@Service
public class RegionRankService {
  private final HousingDataRepository repo;
  private static final Map<String, Integer> stateToRegion;

  public RegionRankService (HousingDataRepository repo) {
    this.repo = repo;
  }

    static {
      LinkedHashMap<String, Integer> m = new LinkedHashMap<>();
      m.put("alabama", 3);
      m.put("alaska", 4);
      m.put("arizona", 4);
      m.put("arkansas", 3);
      m.put("california", 4);
      m.put("colorado", 4);
      m.put("connecticut", 1);
      m.put("delaware", 3);
      m.put("district of columbia", 3);
      m.put("florida", 3);
      m.put("georgia", 3);
      m.put("hawaii", 4);
      m.put("idaho", 4);
      m.put("illinois", 2);
      m.put("indiana", 2);
      m.put("iowa", 2);
      m.put("kansas", 2);
      m.put("kentucky", 3);
      m.put("louisiana", 3);
      m.put("maine", 1);
      m.put("maryland", 3);
      m.put("massachusetts", 1);
      m.put("michigan", 2);
      m.put("minnesota", 2);
      m.put("mississippi", 3);
      m.put("missouri", 2);
      m.put("montana", 4);
      m.put("nebraska", 2);
      m.put("nevada", 4);
      m.put("new hampshire", 1);
      m.put("new jersey", 1);
      m.put("new mexico", 4);
      m.put("new york", 1);
      m.put("north carolina", 3);
      m.put("north dakota", 2);
      m.put("ohio", 2);
      m.put("oklahoma", 3);
      m.put("oregon", 4);
      m.put("pennsylvania", 1);
      m.put("rhode island", 1);
      m.put("south carolina", 3);
      m.put("south dakota", 2);
      m.put("tennessee", 3);
      m.put("texas", 3);
      m.put("utah", 4);
      m.put("vermont", 1);
      m.put("virginia", 3);
      m.put("washington", 4);
      m.put("west virginia", 3);
      m.put("wisconsin", 2);
      m.put("wyoming", 4);
      stateToRegion = Collections.unmodifiableMap(m); //setting stateToRegion read-only version
    }   
    public Integer regionCodeForState(String state) {
    if (state == null) return null;
    // replacing upper-casing/whitespace
    String normalized = state.trim()
        .toLowerCase()
        .replace('_', ' ') // replace underscore with space
        .replace('-', ' ') // replace hyphen with space
        .replaceAll("\\s+", " "); // "\\s+" means one or more whitespace characters 
                                  // so it replaces/collapses multiple spaces into one
      return stateToRegion.get(normalized);
  }

  // returning a record for clean JSON
  public RegionRankResult computeRegionRankWithStats(String state) {
      Integer userRegionCode = regionCodeForState(state); // best to use Integer object, it can hold null
      if (userRegionCode == null) throw new IllegalArgumentException("Unknown state: " + state);
      
      List<Object[]> rows = repo.findRegionAverages(); // each row: [regionCode, dataPerRegion, avgIncome]

      // sort by AVG(lmed) descending
      rows.sort((a, b) -> Double.compare(
        ((Number) b[2]).doubleValue(),  // b's avg local median income
        ((Number) a[2]).doubleValue()   // a's avg local median income
      ));

      for (int i = 0; i < rows.size(); i++) {
        int currRegionCode = ((Number) rows.get(i)[0]).intValue(); // converting from Object[] to Number object to int
        if (currRegionCode == userRegionCode) {
          int avgIncome = ((Number)rows.get(i)[2]).intValue(); // [region, count, income]
          int dataCount = ((Number)rows.get(i)[1]).intValue(); // ^^
          // rank is position + 1 because lists are 0-based
          int rank = i + 1;
          return new RegionRankResult(rank, avgIncome, dataCount);
        }
      }
   // if we didn’t see the user’s region among the 4, that’s a data problem
    throw new IllegalStateException("User region not found in region averages.");
  }

}