# Dalmoa

**구독 관리 서비스 '달모아'의 API 서버입니다.**

사용자의 구독 정보를 관리하고, 환율 API를 연동하여 실시간 지출 계산 기능을 제공합니다

---

## 🔗 관련 저장소
**Android App**: [Dalmoa_Android 저장소 링크](https://github.com/...)

---

## 🛠 Tech Stack
*   **Language**: Java 21
*   **Framework**: Spring Boot 4.0.5
*   **Security**: Spring Security, JWT (Json Web Token)
*   **Database**: H2 (Development), JPA/Hibernate
*   **Build Tool**: Gradle

---

## ✨ Key Features
*   **Authentication**: JWT 기반 로그인 및 Refresh Token 관리
*   **Subscription Management**: 구독 서비스 등록, 수정, 삭제 및 목록 조회
*   **Calculation Engine**: 환율 API를 활용한 원화 환산 및 대시보드 통계 계산
*   **Notification**: 구독 결제일 관련 알림 로직 (Scheduler 활용)

---

## 🚀 Getting Started

### Prerequisites
*   JDK 21
*   Gradle 8.x 이상

### Environment Variables
이 프로젝트는 외부 환율 API 연동을 위해 API Key가 필요합니다. `application.properties`를 수정하거나 환경 변수를
설정하세요.
* `EXCHANGE_API_KEY`: [ExchangeRate-API](https://www.exchangerate-api.com/)에서 발급받은 키
 
### Run
  ./gradlew bootRun