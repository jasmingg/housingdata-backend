# 🧠 Housing Data API — Backend
As part of a full-stack project, this is the repo for my **Java Spring Boot** API, which exposes housing-burden statistics from the **2013 American Housing Survey (AHS)**.

Deployed on **AWS Elastic Beanstalk**, it provides pre-aggregated data by U.S. state and metro category for consumption by both the frontend and static chart generator.

---
**Live site:** [https://housingdata.netlify.app](https://housingdata.netlify.app)  

**Live API endpoint:**  
[`/api?state=Virginia&metro=3`](http://myhousingdataapi.us-east-1.elasticbeanstalk.com/api?state=Virginia&metro=3)

> You can change the query parameters:
> - `state`: Any valid U.S. state name (e.g., `Texas`, `California`, `Ohio`)
> - `metro`: One of the following codes:
>   - `1` = Central City  
>   - `3` = Suburban  
>   - `5` = Non-metro (rural)

## 🚀 Highlights

- ☕ Java 17 + Spring Boot
- 🌐 RESTful API with query params: `?state=...&metro=...`
- ☁️ Hosted on AWS Elastic Beanstalk
- 📊 Serves data for all 50 states × 3 metro regions
- 🔌 Used by both the React frontend and Python chart renderer

---

📦 Project Overview – Housing Data Platform

This project consists of three repos working together, with the other two being:

- 🐍 [`housingdata-visualizer`](https://github.com/jasmingg/housingdata-visualizer) 

Python-based repo that uses GitHub Actions to generate 150 static pie chart images (50 states × 3 metro types).
These charts visualize housing burden distributions and are automatically deployed to GitHub Pages, along with an index.html for easy browsing.

- 🎨 [`housingdata-frontend`](https://github.com/jasmingg/housingdata-frontend)

A React + Vite frontend deployed on Netlify.
Uses a Netlify serverless function to proxy requests to this API and displays summary cards + comparison charts.

Together, these form a lightweight full-stack visualization platform for U.S. housing burden data.

## 🧪 Usage

Make a GET request with the previouslt discussed query parameters (state & metro)

**Example response:**


```json
{
  "state": "Ohio",
  "region": "Midwest",
  "region_stats": {
    "region_rank": 3,
    "avg_income": 65940.0,
    "median_housing_cost": 745.0,
    "data_count": 17400,
    "burden_distribution": {
      "less_than_30_percent": 0.52,
      "between_30_and_50_percent": 0.33,
      "greater_than_50_percent": 0.15
    }
  },
  "metro_stats": {
    "metro_code": "3",
    "metro_label": "Suburban",
    "avg_income": 68210.0,
    "median_housing_cost": 815.0,
    "data_count": 9800,
    "burden_distribution": {
      "less_than_30_percent": 0.49,
      "between_30_and_50_percent": 0.35,
      "greater_than_50_percent": 0.16
    }
  }
}