package com.jasmin.housingaffordability.util;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class StateRegionMapper {
  private static final Map<String, Integer> stateToRegion;


    static {
      LinkedHashMap<String, Integer> m = new LinkedHashMap<>();
      // note: keeping everything lower-case simplifies the logic
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

  public static Integer getRegionCode(String state) {
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

  private static final Map<Integer, String> regionCodeToRegion;
  static {
    LinkedHashMap<Integer, String> m = new LinkedHashMap<>();
    m.put(1, "Northeast");
    m.put(2, "Midwest");
    m.put(3, "South");
    m.put(4, "West");
    regionCodeToRegion = Collections.unmodifiableMap(m);
  }

  public static String getRegionName(Integer regionCode) {
    return regionCodeToRegion.get(regionCode);
  }
}
