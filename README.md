
# Satellite

> **Satellite**는 지구 궤도를 돌며 정보와 신호를 전달하는 인공위성처럼,<br>
> 각자의 우주를 품은 사람들이 서로의 생각과 발견을 공유하고 연결되는 플랫폼입니다.

<br>

**🌍 Service URL**  
https://colie.site

<br>

🔗 Frontend Repository  
https://github.com/100-hours-a-week/3-colie-han-community-FE

<br>

  

## 🔭 목차

- [소개](#-소개)
- [시연 영상](#-시연-영상)
- [기술 스택](#-기술-스택)
- [구현 기능](#-구현-기능)
- [데이터베이스](#-데이터베이스)
- [아키텍처](#-아키텍처)

<br>


## 🌌 소개

- Satellite는 우주를 사랑하는 사람들을 위한 커뮤니티입니다.
- 사용자는 자유롭게 게시글을 작성하고 사람들과 소통할 수 있습니다.
<!--- 게시글 작성 시점을 기준으로 은하수 좌표를 계산해, 모든 사용자가 함께 우주 지도를 만들 수 있습니다.
-->

<br>

## 🎥 시연 영상


https://github.com/user-attachments/assets/9c3443c5-2a27-4e76-8bfc-0371f59e98d9


<br>

## 🛰️ 기술 스택

**Backend**
- Java, Spring Boot, Spring Security, JPA(Hibernate)
- DB: MySQL / H2
- Build: Gradle

**Frontend**
- Vanilla JavaScript
- HTML, CSS
- Express

**Infra & DevOps**
- AWS EC2, RDS, S3
- Nginx, Docker, Docker Compose
- GitHub Actions, Self-hosted Runner (CI/CD)
- Private Docker Registry (Basic Auth)

<br>

## 🌠 구현 기능

- 인증 / 사용자 기능
    - 회원가입 / 로그인 / 로그아웃 (JWT 기반)
    - JWT Access / Refresh Token을 HttpOnly Cookie로 관리
    - 프로필 이미지 업로드 (S3 저장)
    - 프로필 수정 / 비밀번호 변경 / 회원탈퇴  
  

- 게시글 기능
    - 게시글 CRUD
    - 이미지 업로드 (S3 저장)
    - 좋아요, 조회수 기능
    - 댓글 CRUD
    - 페이지네이션
  

<!--
- 우주 테마 기능
  - 사용자가 선택하는 테마: 수금지화목토천해
  - 게시글 작성 시각 기반 은하수 좌표 자동 산출 및 커뮤니티 우주 지도 생성-->

<br>

## 🚀 데이터베이스

<img width="913" height="552" alt="Image" src="https://github.com/user-attachments/assets/af156299-8d34-4947-a90f-3c7e7072b9d5" />

<br><br>

## 🪐 아키텍처

<img width="547" height="484" alt="Image" src="https://github.com/user-attachments/assets/877e2387-156f-46ec-9fcd-5d028ca03d6e" />


- Nginx (Public Subnet)
  - 모든 외부 요청을 받아 Private Subnet에 위치한 FE/BE로 Reverse Proxy
  - HTTPS 트래픽 처리 및 인증서(SSL) 관리
  - 보안 그룹을 통해 외부에서 접근 가능한 유일한 진입 지점  
  

- FE/BE (Private Subnet)
  - Nginx를 통해 받은 요청 처리
  - Docker-Compose를 통해 이미지 실행
  - Private Docker Registry에서 이미지를 Pull하여 최신 버전으로 배포
  

- AWS S3
  - 사용자 프로필/게시글 이미지 파일 저장소
  - DB에는 이미지의 메타데이터와 URL만 저장
  

- AWS RDS (MySQL)
  - 사용자, 게시글, 댓글 등 모든 데이터 저장
  - JPA/Hibernate를 통한 ORM 기반 데이터 접근
  

- Private Docker Registry (Basic Auth)
  - FE/BE Docker 이미지를 직접 관리하는 자체 레지스트리
  - EC2 인스턴스들이 Registry에서 이미지를 Pull하여 배포
   

- GitHub Actions + Self-Hosted Runner (CI/CD)
  - 소스 코드 변경 시 자동 빌드 및 이미지 생성
  - 생성된 이미지를 Private Registry에 Push
  - Self-Hosted Runner가 새로운 이미지를 Pull → 컨테이너 재배포
