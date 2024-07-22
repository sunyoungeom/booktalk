# 📖 프로젝트 명: 북토크

### 개요
- 설명: 도서 검색을 통해 리뷰를 작성하고, 사용자간 리뷰를 공유할 수 있는 서비스
- 제작 기간: 2024.06.11 ~ 2024.07.21
- 개발 인원: 1인
- 배포 URL: [https://3.36.235.110.nip.io](https://3.36.235.110.nip.io)

### 기술 스택
- java 17 / Spring Boot 3
- Thymeleaf / javascript
- MySQL / MyBatis
- Gradle / Docker / AWS EC2
- Caddy

### 목차
- [주요 기능](#주요-기능)
- [프로젝트 구조](#프로젝트-구조)
- [아키텍처](#아키텍처)
- [ERD](#erd)
- [API 명세서](#api-명세서)
- [결과 및 성과](#결과-및-성과)
</br>

## 주요 기능
**[ 사용자 ]**
- 회원가입 및 로그인
- 회원 정보 수정
  
**[ 도서 ]**
- 스크래핑을 통한 베스트 셀러 출력
- Kakao API 를 사용한 도서 검색

**[ 리뷰 ]**
- 리뷰 CRUD
- 좋아요 기능
<div align="right">
  
[목차로](#목차)

</div>
  
## 프로젝트 구조
```java
📦booktalk
 ┣ 📂common
 ┃ ┣ 📜ApiResponseUtil.java
 ┃ ┣ 📜CustomApiResponse.java
 ┃ ┗ 📜Status.java
 ┣ 📂controller
 ┃ ┣ 📜BookController.java
 ┃ ┣ 📜ModalController.java
 ┃ ┣ 📜MypageController.java
 ┃ ┣ 📜ReviewApiController.java
 ┃ ┣ 📜ReviewController.java
 ┃ ┣ 📜UserApiController.java
 ┃ ┗ 📜UserController.java
 ┣ 📂domain
 ┃ ┣ 📜Book.java
 ┃ ┣ 📜Review.java
 ┃ ┣ 📜ReviewLikes.java
 ┃ ┣ 📜ReviewSort.java
 ┃ ┣ 📜User.java
 ┃ ┣ 📜UserRole.java
 ┃ ┗ 📜UserSignupType.java
 ┣ 📂dto
 ┃ ┣ 📜ReviewAddDTO.java
 ┃ ┣ 📜ReviewDTO.java
 ┃ ┣ 📜ReviewLikesDTO.java
 ┃ ┣ 📜ReviewUpdateDTO.java
 ┃ ┣ 📜UserDTO.java
 ┃ ┣ 📜UserJoinDTO.java
 ┃ ┣ 📜UserLoginDTO.java
 ┃ ┣ 📜UserNicknameUpdateDTO.java
 ┃ ┗ 📜UserPasswordUpdateDTO.java
 ┣ 📂exception
 ┃ ┣ 📂common
 ┃ ┃ ┣ 📜CommonErrorCode.java
 ┃ ┃ ┣ 📜CommonException.java
 ┃ ┃ ┣ 📜ErrorCode.java
 ┃ ┃ ┗ 📜ErrorResponse.java
 ┃ ┣ 📜ApiExceptionHandler.java
 ┃ ┣ 📜ExceptionHandlers.java
 ┃ ┣ 📜ReviewErrorCode.java
 ┃ ┣ 📜ReviewException.java
 ┃ ┣ 📜UserErrorCode.java
 ┃ ┗ 📜UserException.java
 ┣ 📂interceptor
 ┃ ┗ 📜LoginInterceptor.java
 ┣ 📂repository
 ┃ ┣ 📜ReviewLikesRepository.java
 ┃ ┣ 📜ReviewRepository.java
 ┃ ┗ 📜UserRepository.java
 ┣ 📂scheduler
 ┃ ┗ 📜BestsellerScheduler.java
 ┣ 📂service
 ┃ ┣ 📜ReviewService.java
 ┃ ┣ 📜SearchService.java
 ┃ ┗ 📜UserService.java
 ┣ 📜.DS_Store
 ┣ 📜AppConfig.java
 ┗ 📜BooktalkApplication.java
```
<div align="right">
 
[목차로](#목차)

</div>

## 아키텍처
<div align=center>
  
![image](https://github.com/user-attachments/assets/9730f6d1-edde-4595-bac2-977d197bb422)

</div>

<div align="right">
 
[목차로](#목차)

</div>

## ERD
<div align=center>
  
</br>

![image](https://github.com/user-attachments/assets/236d1199-903f-4449-8c97-86206d336a64)

</br>
  
</div>

<div align="right">
 
[목차로](#목차)

</div>

## API 명세서
<img width="701" alt="image" src="https://github.com/user-attachments/assets/6400c202-2c81-4445-9f1e-813b5dd57759">

<img width="701" alt="image" src="https://github.com/user-attachments/assets/dd40dab4-eb4c-4760-9642-28981ea6b801">

- API 명세서 보기: [https://3.36.235.110.nip.io/swagger-ui/index.html#/attendace-controller](https://3.36.235.110.nip.io/swagger-ui/index.html#/attendace-controller)
<div align="right">
 
[목차로](#목차)

</div>

## 결과 및 성과
**[ 좋아요 ]**
- `일반글`의 경우 `낙관적 락`으로 데이터 경합 방지
- `인기글`의 경우 성능 최적화를 위해 `비관적 락`를 사용

**[ 리뷰 ]**
<!-- `쿼리 최적화`를 통해 조회 시 응답 시간 단축 [\[자세히 보기\]](https://github.com/sunyoungeom/booktalk) -->
- 로그인 `인터셉터`를 활용하여 리뷰 작성 시 로그인 페이지로 리디렉션

**[ 베스트 셀러 ]**
- 베스트 셀러 스크래핑 결과를 `파일 시스템`에 캐싱하여 로딩 속도를 `약 0.8초 → 0.01초`로 `99%` 향상
- 스케줄러를 사용한 스크래핑 결과 업데이트시 `동시 접근 문제`를 `FileLock`으로 해결

**[ REST API ]**
- REST API의 `응답 형식을 구조화` 하여 `클라이언트`가 일관된 형식으로 응답을 처리할 수 있게 함
- API 데이터 반환 타입을 제네릭으로 정의하여 코드의 재사용성과 유지보수성을 향상

  ```java
  public class CustomApiResponse<T> {
      private Status status; // SUCCESS, FAIL, ERROR;
      private HttpStatus httpStatus;
      private String message;
      private T data;
  }
  ```
- `Swagger UI`를 통한 API 문서 자동화

**[ Exception ]**
- `@ControllerAdvice`를 이용한 전역 예외 처리
- `@Valid`를 이용한 유효성 검사

**[ Infra ]**
- `Docker`를 사용하여 `MySQL` 데이터베이스 컨테이너화 및 배포
- `AWS EC2`에서 애플리케이션을 배포하고 실행하여 서비스 제공
- `Caddy`를 사용하여 `HTTPS`를 자동으로 설정 및 관리

<div align="right">
 
[목차로](#목차)

</div>

<br>

