<div align="center">

# 🍽️ Delivery

### Spring Boot 기반 음식 주문 관리 플랫폼

**사용자와 음식점 사장님을 연결하는 통합 음식 주문 서비스**

회원가입부터 음식점 관리, 메뉴 관리, 주문, 결제,
리뷰 및 사장님 답글 기능까지 하나의 플랫폼에서 제공합니다.

실무 환경을 고려하여 **Spring Security**, **JWT**, **Redis**, **Docker**, **AWS EC2**, **GitHub Actions** 등을 적용한 백엔드 프로젝트입니다.

<br>

![Java](https://img.shields.io/badge/Java-17-orange?logo=openjdk)
![Spring Boot](https://img.shields.io/badge/SpringBoot-3.5.16-6DB33F?logo=springboot)
![Spring Security](https://img.shields.io/badge/SpringSecurity-6DB33F?logo=springsecurity)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-17-336791?logo=postgresql)
![Redis](https://img.shields.io/badge/Redis-DC382D?logo=redis)
![Docker](https://img.shields.io/badge/Docker-2496ED?logo=docker)
![GitHub Actions](https://img.shields.io/badge/GitHub_Actions-CI-2088FF?logo=githubactions)
![AWS](https://img.shields.io/badge/AWS-EC2-FF9900?logo=amazonaws)

</div>

---

# 📖 프로젝트 소개

Delivery는 사용자와 음식점 사장님을 연결하는 **음식 주문 관리 플랫폼**입니다.

단순한 CRUD 프로젝트가 아닌 **실무에서 사용하는 기술과 협업 방식**을 경험하는 것을 목표로 개발하였습니다.

프로젝트 전반에 걸쳐 Domain 기반 설계, JWT 인증, Redis, Docker, AWS EC2, GitHub Actions, 테스트 코드 등을 적용하여 유지보수성과 확장성을 고려하였습니다.

---

# 🎯 프로젝트 목표

- Spring Boot 기반 REST API 서버 개발
- Domain 중심 설계 적용
- JWT 기반 인증 및 인가
- Redis 활용
- Docker 기반 개발환경 통일
- AWS EC2 서버 운영
- GitHub Actions CI 구축
- 팀 협업 경험

---

# 🛠 Tech Stack

| 분야 | 기술 |
|------|------|
| Language | Java 17 |
| Framework | Spring Boot 3.5 |
| Security | Spring Security, JWT |
| Database | PostgreSQL 17 |
| Cache | Redis |
| ORM | Spring Data JPA |
| DevOps | Docker, Docker Compose, AWS EC2 |
| CI | GitHub Actions |
| API Docs | Swagger, Scalar |
| Logging | Logback |
| Test | JUnit5, Mockito |

---

# 🏗 Architecture

> 프로젝트 아키텍처 이미지

---

# 🗂 ERD

> ERD 이미지

---

# 📂 프로젝트 구조

```text
src
├── common
├── domain
│   ├── auth
│   ├── user
│   ├── store
│   ├── menu
│   ├── order
│   ├── payment
│   ├── review
│   └── reviewreply
│
├── global
│
└── resources
```

---

# ✨ 주요 기능

### 👤 User

- 회원가입
- 로그인(JWT)
- 권한 관리

### 🏪 Store

- 음식점 CRUD
- 평균 평점 조회

### 🍔 Menu

- 메뉴 CRUD

### 📦 Order

- 주문
- 주문 상태 변경
- 주문 취소

### 💳 Payment

- 주문 결제
- 결제 상태 관리

### ⭐ Review

- 리뷰 CRUD
- 리뷰 정렬
- 리뷰 페이징
- 평균 평점
- 사장님 답글 CRUD

---

# 🚀 핵심 기술

## 🔐 인증 및 보안

- Spring Security
- JWT Access / Refresh Token
- Redis 로그아웃
- Role 기반 권한 관리

---

## ⭐ Review

- 주문 완료 후 리뷰 작성
- 작성자만 수정/삭제
- Pageable 기반 페이징
- Sort 기반 정렬
- JPQL 평균 평점 조회
- Soft Delete

---

## 💬 Review Reply

- Review : Reply = 1 : 1
- Store Owner 검증
- 답글 중복 방지
- Soft Delete

---

## ⚙️ 공통

- RestApiResponse
- Global Exception Handler
- BaseEntity
- Logback

---

# 🧪 테스트

프로젝트의 핵심 비즈니스 로직을 검증하기 위해 **JUnit5**와 **Mockito**를 활용한 단위 테스트를 작성하였습니다.

### 테스트 대상

- Authentication
- User
- Store
- Menu
- Order
- Payment
- Review
- Review Reply
- Cart
- AI

### 테스트 내용

- 서비스 로직 검증
- 권한 검증
- CRUD 기능 검증
- 예외 처리 검증
- 비즈니스 로직 검증
- Mockito 기반 Mock 테스트

---

# 📈 프로젝트 성과

- ✅ Domain 기반 패키지 구조 설계
- ✅ Spring Security + JWT 인증 및 권한 관리
- ✅ Redis 기반 로그아웃 구현
- ✅ Docker Compose 기반 개발 환경 통일
- ✅ AWS EC2 서버 배포
- ✅ GitHub Actions 기반 CI 구축
- ✅ Swagger · Scalar API 문서화
- ✅ Logback 기반 운영 로그 적용
- ✅ Service Layer 단위 테스트 작성
- ✅ Pageable 기반 리뷰 조회
- ✅ JPQL 평균 평점 계산
- ✅ Soft Delete 적용

---

# 🔥 Trouble Shooting

## 1️⃣ 리뷰 중복 작성 문제

### 문제

삭제된 리뷰를 다시 작성할 수 있었다.

### 해결

삭제 여부와 관계없이

```java
existsByOrderId()
```

를 사용하여 검증하였다.

---

## 2️⃣ 리뷰 답글 권한 문제

### 문제

OWNER 권한만 있으면 모든 리뷰에 답글을 작성할 수 있었다.

### 해결

Store Owner와 로그인 사용자를 비교하여 해결하였다.

---

# 📑 API 문서

### Swagger

```
http://localhost:8080/swagger-ui/index.html
```

### Scalar

```
http://localhost:8080/scalar
```

---

# 🚀 실행 방법

### Docker

```bash
docker compose up -d
```

### Build

```bash
./gradlew build
```

### Run

```bash
./gradlew bootRun
```

---

# 📌 향후 고도화

- GitHub Actions CD 구축
- Redis Cache 적용
- QueryDSL
- Elasticsearch
- Prometheus + Grafana
- 성능 최적화

---

# 🤝 협업

- Git Flow 전략 기반 협업
- Feature Branch 개발
- Pull Request 코드 리뷰
- GitHub Actions CI
- Swagger API 공유

---

# 👥 Team

| 이름 | 담당 |
|------|------|
| 이강석 | Authentication |
| 정수민 | Store |
| 임은택 | Menu |
| 안예진 | Order |
| 송채영 | Payment |
| 이용현 | Review · Review Reply · Docker · AWS EC2 |

---

<div align="center">

## 🍽️ Delivery

**"기능 구현을 넘어 협업과 운영 환경까지 경험한 Spring Boot 팀 프로젝트"**

Made with ❤️ by Delivery Team

</div>
