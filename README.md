# Dalmoa

**구독 관리 서비스 '달모아'의 API 서버입니다.**

사용자의 구독 정보를 관리하고, 환율 API를 연동하여 실시간 지출 계산 기능을 제공합니다

---

## 🔗 관련 저장소
**Android App**: [Dalmoa_Android 링크](https://github.com/JamongFriend/Dalmoa-Android)

---

## 🛠 Tech Stack
*   **Language**: Java 21
*   **Framework**: Spring Boot 3.4.1
*   **Security**: Spring Security, JWT (Json Web Token)
*   **Database**: MySQL 8.0, JPA/Hibernate
*   **Build Tool**: Gradle
*   **Infra**: AWS EC2, Docker, NGINX

---

## ✨ 주요 기능

### 인증 / 회원
JWT 기반으로 로그인, 토큰 재발급, 로그아웃을 처리합니다. 로그인·회원가입 API는 Bucket4j 기반 Rate Limiter가 적용되어 있어 짧은 시간에 반복 요청이 오면 자동으로 차단합니다. 회원가입 후 프로필 조회 및 수정이 가능합니다.

### 구독 관리
사용자가 이용 중인 구독 서비스를 등록·수정·삭제하고 목록으로 조회할 수 있습니다. 등록된 구독들을 바탕으로 대시보드(총 지출, 통계 등)를 계산해 보여줍니다.

### 환율 계산
환율 API(ExchangeRate-API)와 연동해 통화별 환율 데이터를 서버가 직접 들고 있습니다. 앱 구동 시 1회, 이후 12시간마다 스케줄러가 환율을 자동 갱신하여, 외화로 등록된 구독의 원화 환산 금액을 항상 최신 환율로 계산합니다.

### 알림
매일 오전 9시, 스케줄러가 결제일이 임박한 구독을 확인해 알림을 생성합니다. 사용자는 알림 목록 조회, 읽음 처리, 안 읽은 알림 존재 여부를 확인할 수 있습니다.

### 공지사항
서비스 공지사항 목록/상세 조회 기능을 제공합니다.

---

## ☁️ Infrastructure

*   **Cloud**: AWS EC2 (Ubuntu)
*   **Containerization**: Docker Compose
    *   `dalmoa-db`: MySQL 8.0
    *   `dalmoa-backend`: Spring Boot 앱
    *   `dalmoa-nginx`: Nginx 리버스 프록시
    *   `dalmoa-certbot`: SSL 인증서 관리

---

## 🚀 Getting Started

### Prerequisites
*   JDK 21
*   Gradle 8.x 이상

### Environment Variables
비밀값은 `application.yml`에 하드코딩하지 않고 환경 변수로 주입합니다. 프로젝트 루트에 `.env.example`을 참고해 `.env` 파일을 만들고 아래 값을 채워주세요.
* `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`: MySQL 접속 정보
* `JWT_SECRET`: JWT 서명에 사용할 시크릿 (32자 이상)
* `EXCHANGE_API_KEY`: [ExchangeRate-API](https://www.exchangerate-api.com/)에서 발급받은 키

### Run (Local)
  ./gradlew bootRun