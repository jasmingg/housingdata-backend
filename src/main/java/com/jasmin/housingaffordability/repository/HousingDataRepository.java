package com.jasmin.housingaffordability.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import com.jasmin.housingaffordability.entity.HousingData;

import java.util.List;

public interface HousingDataRepository extends JpaRepository<HousingData, String> {
    // using JPQL - talks to the entity HousingData.java
        // not raw SQL table/column names. JPQL gets translated to SQL
        // each row in the list will be an object in form [region, count for how many people in region, average income]
    //
    // steps of this query:
    // 1. Take all HousingData rows.
    // 2. Group them into buckets by the 'region' field.
    // 3. For each region bucket:
    //      - COUNT(hd) → how many rows are in that region
    //      - AVG(hd.lmed) → the average 'lmed' *local (areas) median income* for that region
    // 4. Sort the results by region (ORDER BY hd.region).
    //
    // The result is returned as a List<Object[]> where:
    //   row[0] = region
    //   row[1] = count of rows in that region (Long)
    //   row[2] = average lmed in that region (Double)
     @Query("""
        SELECT hd.region AS region,
                COUNT(hd) AS n,
                AVG(hd.lmed) AS avgLmed
        FROM HousingData hd
        GROUP BY hd.region
        ORDER BY hd.region
""")
    List<Object[]> findRegionAverages();


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