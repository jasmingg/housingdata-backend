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

    // Purpose: Calculate stats for a single region (filtered by regionCode)
    // Result mapping (Object[]):
//   row[0] = data_count                (Long)
//   row[1] = avg_income                (Double)
//   row[2] = median_housing_cost*      (Double)  *approximation via AVG(costmed)
//   row[3] = less_than_30_percent      (Double; 0.0–1.0 proportion)
//   row[4] = between_30_and_50_percent (Double; 0.0–1.0 proportion)
//   row[5] = greater_than_50_percent   (Double; 0.0–1.0 proportion)
@Query("""
  SELECT
    COUNT(hd),      
    AVG(hd.lmed),   
    AVG(hd.costmed),
    AVG(CASE WHEN hd.burdenCategory = 'Less than 30%' THEN 1.0 ELSE 0.0 END),  
    AVG(CASE WHEN hd.burdenCategory = '30-50%'        THEN 1.0 ELSE 0.0 END),  
    AVG(CASE WHEN hd.burdenCategory = '>50%'          THEN 1.0 ELSE 0.0 END)   
  FROM HousingData hd
  WHERE hd.region = :regionCode
""")
List<Object[]> findRegionDetailStats(@Param("regionCode") Integer regionCode);


// Purpose: Calculate stats for a single metro type within a specific region
    //(filtered by :regionCode and :metroType): 
// Result (a list of Object[]):
//   row[0] = data_count                (Long)
//   row[1] = avg_income                (Double)
//   row[2] = median_housing_cost*      (Double)  *approximated via AVG(costmed)
//   row[3] = less_than_30_percent      (Double; 0.0–1.0)
//   row[4] = between_30_and_50_percent (Double; 0.0–1.0)
//   row[5] = greater_than_50_percent   (Double; 0.0–1.0)

    @Query("""
      SELECT
        COUNT(hd),                                                                
        AVG(hd.lmed),                                                             
        AVG(hd.costmed),                                                          
        AVG(CASE WHEN hd.burdenCategory = 'Less than 30%' THEN 1.0 ELSE 0.0 END), 
        AVG(CASE WHEN hd.burdenCategory = '30-50%'        THEN 1.0 ELSE 0.0 END), 
        AVG(CASE WHEN hd.burdenCategory = '>50%'          THEN 1.0 ELSE 0.0 END)  
      FROM HousingData hd
      WHERE hd.region = :regionCode AND hd.metro3 = :metroType
    """)
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