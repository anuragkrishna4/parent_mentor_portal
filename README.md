# Parent-Mentor Portal

A full-stack university attendance management system that bridges the communication gap between institutions and parents. Built with a **Spring Boot Microservices** backend and a **React.js + Vite** frontend.

## Overview

Universities often fail to keep parents informed about their child's attendance in real time. Parent-Mentor Portal solves this by providing a secure, role-based digital platform where mentors upload attendance data and parents can view it instantly — before it becomes an academic problem.

## Tech Stack

**Backend:** Spring Boot · Spring Cloud Gateway · Netflix Eureka · Spring Data JPA · MySQL · JWT (HS256) · OpenCSV · Apache POI  
**Frontend:** React.js · Vite · Axios · React Router  

## Microservices Architecture

| Service | Port | Database |
|---|---|---|
| Eureka Discovery Server | 8761 | - |
| API Gateway | 8080 | - |
| Auth Service | 8081 | user_db |
| Mentor Service | 8082 | mentor_db |
| Parent Service | 8083 | parent_db |

## Key Features

- **Three-role model** - Admin, Mentor, and Parent, each with a dedicated dashboard
- **CSV / Excel attendance upload** - Each upload overrides the previous record; one row per student per subject, always
- **75% threshold rule** - Subjects below 75% attendance show a red **Shortage** badge; at or above show a green **On Track** badge
- **JWT-based stateless security** - Tokens validated at the API Gateway before any request reaches business logic
- **Bulk registration** - Admin uploads a single CSV to create students, parents, and auth accounts simultaneously
- **Feign client cross-service calls** - Parent Service fetches attendance from Mentor Service with JWT forwarded automatically

## Roles

- **Admin** - Manages students, subjects, mentor accounts, and bulk parent registration
- **Mentor** - Uploads attendance CSVs/Excel files and resets data at semester end
- **Parent** - Logs in with phone number and views their child's subject-wise attendance report.
