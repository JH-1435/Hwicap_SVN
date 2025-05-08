# 휘캡(Hwicap) - 프로젝트 개편 VER
## 휘캡(Hwicap) 프로젝트는 키캡 온라인 쇼핑몰 콘셉트의 웹 사이트 입니다.
- 기술 스택 학습 목적의 프로젝트로 기술 스택 복기 및 실력향상 초점에 맞추어 제작하였습니다.

### 개발환경
- Back-End: Java 8, Spring Framework, Node.js (Express), REST API, Tomcat 8.5, Mybatis
- Front-End: Javascript, CSS, HTML, jQuery, AJAX, JSP, JSTL
- DataBase: MySQL 8.x, DBeaver
- DevOps: Linux, Docker, Jenkins CI/CD, Kubernetes, Ansible
- Etc:  AWS EC2, AWS RDS, SVN, Apache Maven, Eclipse
 
### 주요 기능
- JWT: 로그인-> Node.js 서버를 통해 JWT 발급함
- 회원가입: 회원가입-> Node.js 서버를 통해 회원에게 가입환영 이메일발송
- 회원: 상품 구매 및장바구니, 상품 찜하기, 리뷰작성, 문의 등을할수있음
- 관리자: 관리자 페이지에서 유저및상품관리, 문의답변, 상품등록등을할수있음
- 회원구분: 인터셉터(Interceptor)로 해당 회원 및 관리자 정보가 세션에 저장되어 회원과관리자를
구분함
- 문의글: AJAX를 사용하여, 해당 게시글을등록한 회원과로그인한회원이일치할경우에만
조회할수있음

- 소셜로그인: 도메인이 필요하여현재코드만구현된상태

### 기술적 고민
JWT 발급 구현 문제
=> JWT 발급을 직접 구현할지, Node.js의 JWT 라이브러리를 사용할지 고민했으며, 보안과 효율성을 고려해 라이브러리 사용을 선택하여 해결

장바구니 기능 개발 당시 데이터의 일관성을 유지 시켜야 하는 문제
=> SpringFramework에서 제공하는 트랜잭션 어노테이션을 통해 해결
