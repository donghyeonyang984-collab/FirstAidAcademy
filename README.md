# .gitignore for Spring Boot Project (First Aid Academy LMS)

# Build directories
/target/
/build/
/out/

# IDE settings
.idea/
*.iml
.vscode/

# OS files
.DS_Store
Thumbs.db

# Logs
*.log

# Environment files (keep secrets safe)
.env
application-local.properties
application-prod.properties

# Video / media files
/src/main/resources/static/videos/
*.mp4
*.avi
*.mov

# Compiled files
*.class

# Temporary
*.tmp
*.bak

------------------------------------------------------------

# README.md (포트폴리오용 깃허브 메인 페이지)

# 🩺 First Aid Academy LMS
> Spring Boot & JDBC 기반으로 개발된 온라인 교육 + 미니게임 융합형 LMS 플랫폼

## 📘 프로젝트 소개
First Aid Academy LMS는 관리자가 등록한 강의를 수강하고, 모든 강좌를 이수한 후 AI 기반 시험 문제를 풀어 합격 시 **이수증을 발급**받을 수 있는 온라인 교육 플랫폼입니다.

또한 Unity 기반으로 제작된 미니게임을 3개 이상 클리어하면 별/뱃지를 획득하여 동일하게 이수증 발급이 가능한 **게이미피케이션형 학습 시스템**을 제공합니다.

---

## 🧑‍💻 주요 기능
- 로그인 / 회원가입 (권한 기반 접근 제어)
- 강의 목록, 수강 목록, 강의 재생
- 문의사항 등록 및 답변 (파일 업로드 포함)
- 관리자 강의 등록 / 수정 / 삭제 기능
- 강의 진도율 및 자동 이수 처리
- AI 기반 시험 문제 자동 생성 (설계 반영)
- Unity 미니게임과 LMS 간 연동 구조 설계

---

## ⚙️ 기술 스택
- **Backend:** Spring Boot, JDBC
- **Database:** MySQL
- **Frontend:** HTML / CSS / JS (Thymeleaf)
- **Collaboration:** GitHub
- **File Handling:** MultiPart(파일 처리), PDF(출력)
- **Etc:** Unity(미니게임)

---

## 🔥 핵심 구현 포인트
- **권한 기반 로그인 구조 설계:** 사용자 유형(Admin/User)에 따른 기능 분리
- **강의 진도율 관리:** 재생 상태 저장 및 자동 이수 처리 로직 구현
- **MultiPart 파일 업로드:** 문의사항 등록 시 첨부파일 업로드 + 서버 저장 경로 관리
- **문서 자동화:** PDF 기반 이수증 출력 기능 구현
- **팀 관리 및 문서화:** PM, DB 설계, API 명세서 작성 및 일정 관리 주도

---

## 🧩 프로젝트 구조 (간단 예시)
```
FirstAidAcademyLMS/
 ┣ src/
 ┃ ┣ main/
 ┃ ┃ ┣ java/com/example/lms/
 ┃ ┃ ┣ resources/
 ┃ ┃ ┃ ┣ static/
 ┃ ┃ ┃ ┗ templates/
 ┃ ┗ test/
 ┣ pom.xml
 ┗ README.md
```

---

## 🧠 팀 구성 및 역할
- 팀장 (PM / BE / DB / 문서 총괄): **DW**
- 프론트엔드 / 게임 연동 담당: 팀원 2명
- 테스트 및 배포 지원: 팀원 1명

---

## 🗓 개발 기간
**2025.11.03 ~ 2025.12.01 (4주)**

---

## 📦 실행 방법
```bash
git clone https://github.com/donghyeonyang984-collab/FirstAidAcademy.git
cd FirstAidAcademy
mvn clean package
java -jar target/firstaidacademy-0.0.1-SNAPSHOT.jar
```

---

## 🔗 관련 링크
- **포트폴리오 문서:** [Notion 바로가기]([https://www.notion.so/2be91ca4e4f0806283aff8df0a5021ec](https://tasty-ear-c9b.notion.site/2be91ca4e4f0806283aff8df0a5021ec)
- **PDF 포트폴리오:** (추후 추가 예정)

---

## 📜 License
이 프로젝트는 개인 학습 및 포트폴리오 용도로 작성되었습니다.
