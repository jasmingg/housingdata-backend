package com.jasmin.housingaffordability.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.Table;

@Entity
@Table(name = "housing_data")
public class HousingData {
    @Id
    @Column(name = "control")
    private String control;  // Primary key (assuming it's unique; adjust if not!)

    private Integer metro3;
    private Integer region;
    private Double lmed;
    private Double fmr;
    private Double cost06;
    private Double cost08;
    private Double cost12;
    private Double costmed;
    private Double burden;
    private String burdenCategory;
    private String ownRent;
    private String status;
    private String type;

    // --- Constructors ---
    public HousingData() {}

    // --- Getters and Setters ---
    public String getControl() { return control; }
    public void setControl(String control) { this.control = control; }

    public Integer getMetro3() { return metro3; }
    public void setMetro3(Integer metro3) { this.metro3 = metro3; }

    public Integer getRegion() { return region; }
    public void setRegion(Integer region) { this.region = region; }

    public Double getLmed() { return lmed; }
    public void setLmed(Double lmed) { this.lmed = lmed; }

    public Double getFmr() { return fmr; }
    public void setFmr(Double fmr) { this.fmr = fmr; }

    public Double getCost06() { return cost06; }
    public void setCost06(Double cost06) { this.cost06 = cost06; }

    public Double getCost08() { return cost08; }
    public void setCost08(Double cost08) { this.cost08 = cost08; }

    public Double getCost12() { return cost12; }
    public void setCost12(Double cost12) { this.cost12 = cost12; }

    public Double getCostmed() { return costmed; }
    public void setCostmed(Double costmed) { this.costmed = costmed; }

    public Double getBurden() { return burden; }
    public void setBurden(Double burden) { this.burden = burden; }

    public String getBurden_category() { return burdenCategory; }
    public void setBurden_category(String burden_category) { this.burdenCategory = burden_category; }

    public String getOwnrent() { return ownRent; }
    public void setOwnrent(String ownrent) { this.ownRent = ownrent; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
}
