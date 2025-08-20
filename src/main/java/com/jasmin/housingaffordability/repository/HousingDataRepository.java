package com.jasmin.housingaffordability.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import com.jasmin.housingaffordability.entity.HousingData;

import java.util.List;

public interface HousingDataRepository extends JpaRepository<HousingData, String> {
    // using JPQL - talks to the entity HousingData.java
        // not raw SQL table/column names. JPQL gets translated to SQL
        // each row in the list will be an object in form [region, count for how many people in region, average income]

// Purpose: Return region codes ranked by average income (highest first).
// Result: List<Number> where each item = region code.

  @Query("""
     SELECT hd.region AS region
     FROM HousingData hd
     GROUP BY hd.region
     ORDER BY AVG(hd.lmed) DESC
   """)
  List<Object []> rankRegions();

// Purpose: Calculate stats for an entire region (all metro types combined)
// (filtered by :regionCode), using numeric burden ratios directly instead of text categories.
//
// Result (a list of Object[]):
//   row[0] = data_count                (Long)   total number of rows
//   row[1] = avg_income                (Double) average of lmed (median household income)
//   row[2] = median_housing_cost       (Double) average of costmed (median housing cost)
//   row[3] = less_than_30_percent      (Double; 0.0–1.0) fraction with burden < 0.30
//   row[4] = between_30_and_50_percent (Double; 0.0–1.0) fraction with 0.30 ≤ burden ≤ 0.50
//   row[5] = greater_than_50_percent   (Double; 0.0–1.0) fraction with burden > 0.50

  @Query(value = """
    SELECT
      COUNT(*)                                                   AS data_count,
      AVG(hd.lmed)::float                                        AS avg_income,
      AVG(hd.costmed)::float                                     AS median_housing_cost,
      -- Fractions of non-null burden rows in each bin:
      SUM(CASE WHEN hd.burden IS NOT NULL AND hd.burden < 0.30 THEN 1 ELSE 0 END)::float
        / NULLIF(SUM(CASE WHEN hd.burden IS NOT NULL THEN 1 ELSE 0 END), 0)        AS lt30,
      SUM(CASE WHEN hd.burden IS NOT NULL AND hd.burden >= 0.30 AND hd.burden <= 0.50 THEN 1 ELSE 0 END)::float
        / NULLIF(SUM(CASE WHEN hd.burden IS NOT NULL THEN 1 ELSE 0 END), 0)        AS btw30_50,
      SUM(CASE WHEN hd.burden IS NOT NULL AND hd.burden > 0.50 THEN 1 ELSE 0 END)::float
        / NULLIF(SUM(CASE WHEN hd.burden IS NOT NULL THEN 1 ELSE 0 END), 0)        AS gt50
    FROM housing_data hd
    WHERE hd.region = :regionCode
  """, nativeQuery = true)
  List<Object[]> findRegionDetailStats(@Param("regionCode") Integer regionCode);


// Purpose: Calculate stats for a single metro type within a specific region
// (filtered by :regionCode and :metroType), using numeric burden ratios directly
    // Result (a list of Object[]):
//   row[0] = data_count                (Long)   total number of rows
//   row[1] = avg_income                (Double) average of lmed (median household income)
//   row[2] = median_housing_cost       (Double) average of costmed (median housing cost)
//   row[3] = less_than_30_percent      (Double; 0.0–1.0) fraction with burden < 0.30
//   row[4] = between_30_and_50_percent (Double; 0.0–1.0) fraction with 0.30 ≤ burden ≤ 0.50
//   row[5] = greater_than_50_percent   (Double; 0.0–1.0) fraction with burden > 0.50

@Query(value = """
  SELECT
    COUNT(*)                                                   AS data_count,
    AVG(hd.lmed)::float                                        AS avg_income,
    AVG(hd.costmed)::float                                     AS median_housing_cost,
    -- Fractions of non-null burden rows in each bin:
    SUM(CASE WHEN hd.burden IS NOT NULL AND hd.burden < 0.30 THEN 1 ELSE 0 END)::float
      / NULLIF(SUM(CASE WHEN hd.burden IS NOT NULL THEN 1 ELSE 0 END), 0)        AS lt30,
    SUM(CASE WHEN hd.burden IS NOT NULL AND hd.burden >= 0.30 AND hd.burden <= 0.50 THEN 1 ELSE 0 END)::float
      / NULLIF(SUM(CASE WHEN hd.burden IS NOT NULL THEN 1 ELSE 0 END), 0)        AS btw30_50,
    SUM(CASE WHEN hd.burden IS NOT NULL AND hd.burden > 0.50 THEN 1 ELSE 0 END)::float
      / NULLIF(SUM(CASE WHEN hd.burden IS NOT NULL THEN 1 ELSE 0 END), 0)        AS gt50
  FROM housing_data hd
  WHERE hd.region = :regionCode
    AND hd.metro3 = :metroType
""", nativeQuery = true)
List<Object[]> findMetroStatsForRegion(@Param("regionCode") Integer regionCode,
                                       @Param("metroType") Integer metroType);


// the remainder of these queries are strictly for debugging endpoint purposes (/debug/summary)
    @Query("SELECT COUNT(hd) FROM HousingData hd")
    long countAll();

    @Query("""
    SELECT hd.region, COUNT(hd)
    FROM HousingData hd
    GROUP BY hd.region
    ORDER BY hd.region
    """)
    List<Object[]> countByRegion();

    @Query("SELECT MIN(hd.lmed), AVG(hd.lmed), MAX(hd.lmed) FROM HousingData hd")
    List<Object[]> lmedStats();
}