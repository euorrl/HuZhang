# Best Bike Paths (BBP)

Best Bike Paths (BBP) is a prototype web system developed for the course  
**Software Engineering 2 – A.Y. 2025–2026**.

The platform allows users to explore, record, and share cycling routes, combining community-generated data with map-based visualization and basic safety and condition reporting.

---

## Live Demo

- **Frontend (Vercel)**  
  https://hu-zhang.vercel.app

- **Backend API (Railway)**  
  https://huzhang-production.up.railway.app  
  Health check: `https://huzhang-production.up.railway.app/api/health`

---

## Main Features

- **Explore Routes**  
  Search public routes by origin/destination and view them on an interactive map, with distance and safety/condition indicators.

- **Community**  
  Browse routes published by other users and visualize their recorded paths.

- **My Trips**  
  Record personal trips (mock GPS), view history, and display full route geometry on the map.

- **Report Path**  
  Publish personal trips as public routes with safety and condition ratings.

- **Login (Mock Authentication)**  
  Prototype login system for enabling user-specific features.

---

## Technology Stack

- **Frontend**  
  - Vue 3 + Vite  
  - Leaflet (map visualization)  
  - Deployed on Vercel

- **Backend**  
  - Java 8  
  - Spring Boot (Maven)  
  - REST API  
  - Deployed on Railway

- **Database**  
  - Supabase (PostgreSQL)

---

## Project Structure

```text
DeliveryFolder/
  Contains final deliverables (RASD and DD in PDF format)

src/
  backend/
    springboot/
      Spring Boot backend source code

  frontend/
    Vue 3 frontend source code


## Acceptance Testing Infrastructure

This project can be tested using:

Desktop web browsers (Chrome, Firefox, Edge)

Mobile browsers (Android / iOS)

Android Emulator

iOS Emulator (macOS)

All core system functions are accessible through the web interface.


## Deployment

- **Frontend**

  - Connected to GitHub

  - Automatically deployed by Vercel on each push to main

- **Backend**

  - Connected to GitHub

  - Automatically built and deployed by Railway
