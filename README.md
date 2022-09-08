# 📑미니 프로젝트 - 맛집 탐방

### 개발 환경
* Java 11
* SpringBoot 2.7.2
* H2
* AWS S3

----------
### 🔗 [와이어프레임 / API](https://www.notion.so/4-SA-91890281a6cb48928174ab949f633acb)

#### 기능1
  * Security와 jwt 토큰을 활용한 로그인/회원가입 기능
  
#### 기능2
  * 로그인을 한 회원은 맛집 소개글 작성과 이미지 업로드가 가능한 게시글을 작성한다.
  * 저장 된 게시글을 불러오고 권한을 가진 회원은 게시글을 수정, 삭제 할 수 있다.
  * 게시글을 삭제할 때 해당 게시글의 댓글도 함께 삭제된다.
  
#### 기능3
  * 로그인을 한 회원은 맛집 게시글에 댓글을 달 수 있다.
  * 저장된 댓글을 해당 게시글에 모두 나타낸다. 
  * 권한을 가진 회원은 댓글을 수정, 삭제 할 수 있다.
  
-----------

#### 🌀 트러블슈팅
 **클라이언트에서 서버로 요청했을 때 CORS 에러 발생**
 * Spring Security에 CORS 허용 설정을 통해 문제 해결 
         
>  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
>  
>      http.cors(); //cors 활성화
>  }

>addAllowedOrigin("http://localhost:3000");
>>도메인 허용

>addAllowedHeader("*");
>>허용하는 request header 추가

>addAllowedMethod("*");
>>허용하는 http 메서드(get/post/put/delete) 추가

>addExposedHeader("Authorization");
>>노출할 response header 추가

>setAllowCredentials(true);
>>내 서버가 응답할 때 json 자바스크립트 허용
