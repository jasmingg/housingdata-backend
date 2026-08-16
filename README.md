# 🏠 Housing Data API

A **Java 17 + Spring Boot REST API** serving pre-aggregated housing-burden statistics from the **2013 American Housing Survey (AHS)**.

The API provides state- and metro-level housing affordability data for consumption by the project's web frontend and automated visualization pipeline.

**Live Site:** https://housing.jasmingg.com

## 🚀 Highlights

- ☕ Java 17 + Spring Boot
- 🌐 RESTful API with query-based filtering
- 📊 Data covering all 50 U.S. states across three metro classifications
- ⚡ Pre-aggregated data for efficient API responses
- 🔌 Integrated with React frontend and automated chart generation
- ⚙️ Deployed and maintained self-hosted backend infrastructure

## 🔎 API

Query by state and metro classification:

```text
/api?state=Virginia&metro=3