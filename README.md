# dev-community: 개발자를 위한 커뮤니티

## 주요 기능과 로직

- 검색: 게시글 제목, 본문 또는 제목 + 본문 으로 게시글 검색 가능
- 페이징 기능: 게시글, 알림, 스크랩 목록 등의 결과를 10개 또는 15개의 씩 페이징하여 백에서 프론트로 보내줌  
- 알림: 댓글 채택, 댓글 달림, 댓글 좋아요, 게시글 좋아요 누를 시 해당 게시글 또는 게시글의 회원에게 알림이 간다. 
- 스크랩: 게시글을 스크랩한다. (중복 스크랩 방지)
- 좋아요: 댓글, 게시글에 좋아요를 누를 수 있다. (중복 좋아요 방지)
- 채택: 게시글의 댓글을 채택할 수 있다. (채택된 댓글은 수정, 삭제 불가)
- 정렬: 시간, 좋아요, 댓글 수, 조회 수 등 으로 정렬 가능
- 주간 best 게시글: 현재 날짜를 기점으로 7일 간의 좋아요가 많은 게시글을 불러옴 
- 회원 점수: 게시글 작성, 댓글 작성, 채택 시 회원의 활동 점수 증가
- 배포: AWS EC2로 배포 (jar파일로 빌드)
- DB: AWS EC2에 MYSQL 설치 후 연동

## 기술 스택
### Front
- HTML, CSS, JavaScript, React
### Back
- Java - version 11, SpringBoot, Spring Data JPA, Querydsl, Spring Security, JWT, Gradle, Junit5, MySQL, AWS(EC2), Swagger(문서 작성)

## DB 설계
![board-entity](https://user-images.githubusercontent.com/66157892/149706422-b5a9a773-bec0-4854-8ab9-e1e7338f2e20.png)

## 서비스 구조
![service](https://user-images.githubusercontent.com/66157892/149710773-2f53a3e2-a4c6-4728-8cc7-5b35cdf46785.PNG)
