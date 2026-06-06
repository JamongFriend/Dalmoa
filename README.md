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

## ✨ Key Features
*   **Authentication**: JWT 기반 로그인 및 Refresh Token 관리
*   **Subscription Management**: 구독 서비스 등록, 수정, 삭제 및 목록 조회
*   **Calculation Engine**: 환율 API를 활용한 원화 환산 및 대시보드 통계 계산
*   **Notification**: 구독 결제일 관련 알림 로직 (Scheduler 활용)

---

## ☁️ Infrastructure

*   **Cloud**: AWS EC2 (Ubuntu)
*   **Containerization**: Docker Compose
    *   `dalmoa-db`: MySQL 8.0
    *   `dalmoa-backend`: Spring Boot 앱
    *   `dalmoa-nginx`: Nginx 리버스 프록시
    *   `dalmoa-certbot`: SSL 인증서 관리

### 서버 배포
```bash
# EC2 접속
ssh -i "키파일.pem" ubuntu@{EC2_IP}

# 코드 업데이트 및 재시작
docker-compose down && docker-compose up -d
```

### DB 접속 (MySQL Workbench)
SSH 터널을 통해 접속합니다. 외부에 포트를 열지 않아 보안을 유지합니다.

*   **Connection Method**: Standard TCP/IP over SSH
*   **SSH Hostname**: `{EC2_IP}`
*   **SSH Username**: `ubuntu`
*   **SSH Key File**: EC2 키페어 `.pem` 파일
*   **MySQL Hostname**: `127.0.0.1`
*   **MySQL Port**: `3306`

---

## 🚀 Getting Started

### Prerequisites
*   JDK 21
*   Gradle 8.x 이상

### Environment Variables
이 프로젝트는 외부 환율 API 연동을 위해 API Key가 필요합니다. `application.properties`를 수정하거나 환경 변수를
설정하세요.
* `EXCHANGE_API_KEY`: [ExchangeRate-API](https://www.exchangerate-api.com/)에서 발급받은 키
 
### Run (Local)
  ./gradlew bootRun

### Run (Server)
  docker-compose up -d