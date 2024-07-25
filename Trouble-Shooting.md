
# Trouble-Shooting

### 목차
- [좋아요 기능 개선을 위한 낙관적 락 적용시 문제발생](#좋아요-기능-개선)
- [배포 후 REST API 예외 처리가 되지 않는 문제](#배포-후-REST-API-예외-처리가-되지-않는-문제)

</br>


# 좋아요 기능 개선

## 낙관적 락과 비관적 락
> 다중 스레드 환경에서 동시에 요청이 들어올 경우 발생할 수 있는 동시성 문제를 해결하기 위해 Lock을 적용

|구분|낙관적 락|비관적 락|
|:---:|:---|:---|
|설명|버전을 이용해 데이터의 정합성을 보장|데이터에 실제로 락을 걸어 동시 접근을 제어|
|시점|데이터 변경 시|데이터 조회 시|
|장점|1. 동시성이 높음 <br> 2. 실제로 락을 걸지 않기 때문에 비관적락에 비해 성능상 이점 존재|1. 정합성 보장 <br> 2. 자주 업데이트 되는 경우 낙관적 락에 비해 성능상 이점 존재|
|단점|1. 충돌 발생 가능성 <br> 2. 경합이 빈번한 경우 성능 저하 <br> 3. 실패 시 재시도 로직 필요|1. 동시성이 낮음 <br> 2. 락으로 인한 성능 저하 가능성 <br> 3. 데드락 발생 가능|

- 성능과 정합성을 고려하여 자주 업데이트될 것으로 예상되는 글에는 `비관적 락`을 적용하고, 그렇지 않은 경우에는 `낙관적 락`을 적용

<br>

## 낙관적 락 적용시 문제발생

### 문제 인식
>  다수의 스레드가 동시에 좋아요를 요청할 때, 낙관적 락을 사용하여 리뷰의 좋아요 수를 업데이트하는 과정에서 일부 요청만 성공하는 문제
 
- 격리 레벨을 MySQL의 기본 격리 수준인 `REPEATABLE READ`으로 설정하였을 때 100개중 12개의 요청만 성공
  
    <img width="518" alt="image" src="https://github.com/user-attachments/assets/5417066f-7657-4b8d-b613-0653150fcfa9">

- 반면 격리 수준을 `READ COMMITTED`로 설정하였을 때는 모든 요청이 성공
  
    <img width="518" alt="image" src="https://github.com/user-attachments/assets/82e26d20-d7a5-4b17-88dc-df7a403042ac">

</br>

### 원인 분석
> 최초의 스레드는 업데이트에 성공하지만, 다른 스레드는 재시도 로직이 존재함에도 불구하고 업데이트 시 여전히 이전 데이터를 읽어와 업데이트에 실패

  |구분|REPEATABLE READ|READ COMMITTED|
  |:---:|:---|:---|
  |특징|1. 트랜잭션이 시작된 시점의 데이터를 읽음</br>2. 트랜잭션이 종료되기 전까지 같은 데이터를 반복해서 읽을 때 항상 같은 결과를 반환|1. 트랜잭션이 시작된 후 커밋된 최신 데이터를 읽음</br>2. 다른 트랜잭션이 커밋한 변경 사항을 즉시 볼 수 있음|

- 트랜잭션의 범위가 너무 넓어, 낙관적 락 충돌이 자주 발생하여 일부 요청만 성공
- 즉, `REPEATABLE READ` 격리 수준에서는 `@Transactional` 내에서 시작된 시점의 데이터만 읽기 때문에, 최신의 데이터를 반영하지 못하고 충돌 발생
  
</br>

### 문제 해결
> 업데이트 로직과 재시도 로직을 분리하고, 업데이트 로직에만 `@Transactional`을 적용

```java
 public ReviewLikesDTO likeReview(Long reviewId, Long userId) throws InterruptedException {
        validateUser(userId);

        boolean alreadyLiked = reviewLikesRepository.findByUserIdAndReviewId(userId, reviewId); // 좋아요 확인
        int success = 0; // 업데이트 성공 확인

        while (success < 1) {
            try {
                success = likeReviewWithLock(reviewId, userId, alreadyLiked);
            } catch (Exception e) {
                Thread.sleep(50);
            }
        }

        // 사용자 좋아요 업데이트
        reviewLikesRepository.saveOrUpdateLike(userId, reviewId);

        Review updatedReview = existsById(reviewId);
        ReviewLikesDTO reviewLikesDTO = new ReviewLikesDTO();
        reviewLikesDTO.setLiked(!alreadyLiked);
        reviewLikesDTO.setLikes(updatedReview.getLikes());

        return reviewLikesDTO;
    }
```

```java
    @Transactional
    public int likeReviewWithLock(Long reviewId, Long userId, boolean alreadyLiked) {
        Review review = existsById(reviewId);
        checkAuthorMatch(userId, review, true);
        log.info("리뷰 조회 결과: {}", review.toString());

        Long currentVersion = review.getVersion(); // 버전 확인
        int likeChange = alreadyLiked ? -1 : 1; // 좋아요 상태 토글
        int success = 0; // 업데이트 성공 확인

        // 좋아요 수가 50 이상인 경우 비관적 락 적용
        if (review.getLikes() >= 50) {
            reviewRepository.findByIdForUpdate(reviewId);
        }
        // 좋아요 수가 50 미만인 경우 낙관적 락 적용
        success = reviewRepository.updateLikes(reviewId, likeChange, currentVersion);
        log.info("리뷰 좋아요 수 업데이트 시도: {}", success);

        return success;
    }
```

</br>

### 결과 확인
- 모든 요청이 성공적으로 처리되어 데이터의 일관성이 보장됨

</br>

<div align="right">
 
[목차로](#목차)

</div>

</br>

## 배포 후 REST API 예외 처리가 되지 않는 문제

### 문제 인식
> 배포 후, REST API에서 예외 처리가 제대로 되지 않고 모든 예외가 500 서버 에러로 처리되는 문제 발생

- 로컬 환경에서 실행 시
  
    ![image](https://github.com/user-attachments/assets/6fb19680-1594-42a9-83cd-b7ff2b93e7e8)
- EC2 서버에서 실행 시
  
    ![image](https://github.com/user-attachments/assets/f406168c-63bc-43ec-b468-7c75e0631b75)

</br>


### 원인 분석
> 에러 처리가 제대로 되고 있는 것인지 확인 하기 위해 Controller에 try-catch 문을 추가

```java
@PostMapping("/{id}/likes")
    public ResponseEntity<CustomApiResponse> likeReview(@PathVariable(name = "id") Long reviewId,
                                                        HttpServletRequest request) throws InterruptedException {
        Long userId = (Long) request.getSession().getAttribute("userId");
        ReviewLikesDTO reviewLikesDTO = null;
        try {
            reviewService.likeReview(reviewId, userId);
            log.info("reviewLikesDTO: {}", reviewLikesDTO, reviewLikesDTO);
        } catch (UserException  e) {
            e.printStackTrace();
            return ApiResponseUtil.errorResponse(e.getHttpStatus(), e.getMessage());
        } catch (ReviewException e) {
            return ApiResponseUtil.errorResponse(e.getHttpStatus(), e.getMessage());
        }
        return ApiResponseUtil.successResponse(HttpStatus.OK, "좋아요가 반영되었습니다.", reviewLikesDTO);
    }
```

- REST API의 예외 처리기인 `ApiExceptionHandler`는 정상적으로 실행되고 있으나, 에러 페이지의 예외 처리기인 `ExceptionHandlers`가 먼저 실행되는 것으로 보임
- 즉, `@ControllAdvice`가 두 개 정의되어 있고, 실행되는 순서가 명확하지 않아 발생한 문제
  
</br>


### 문제 해결
> `@ControllerAdvice`에 `@Order`를 추가하여, 예외 처리기의 우선순위를 설정
> 
```java
@RestControllerAdvice(annotations = RestController.class)
@Order(1)
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(ReviewException.class)
    public ResponseEntity<ErrorResponse> handleReviewException(ReviewException ex) {
        ErrorResponse errorResponse = reviewErrorResponse(ex);
        log.warn("handleReviewException: code = {}, message = {}", ex.getCode(), ex.getMessage());
        return new ResponseEntity<>(errorResponse, ex.getHttpStatus());
    }
```

```java
@ControllerAdvice
@Order(2)
public class ExceptionHandlers {

    // 500 에러 페이지
    @ExceptionHandler({Exception.class})
    @ResponseStatus(INTERNAL_SERVER_ERROR)
    public ModelAndView handleAllException(Exception ex, Model model) {
        int errorCode = CommonErrorCode.INTERNAL_SERVER_ERROR.getCode();
        String errorMessage = CommonErrorCode.INTERNAL_SERVER_ERROR.getMessage();

        model.addAttribute("errorCode", errorCode);
        model.addAttribute("errorMessage", errorMessage);

        return new ModelAndView("error", model.asMap());
    }
}
```

</br>


### 결과 확인
- EC2 서버에서도 예외가 올바르게 처리됨을 확인

    ![image](https://github.com/user-attachments/assets/8c668946-fcc6-4d9d-ba71-ae5e3490f20f)

</br>

### 결론 및 개선 사항
- `@ControllerAdvice`를 여러 개 사용할 경우, `@Order`를 사용하여 우선순위를 명확히 설정하는 것이 중요
- 무중단 배포를 구현하여, 코드 수정 후 배포 및 테스트 과정을 간소화하고 빠르게 반복할 수 있도록 개선 필요

</br>

<div align="right">
 
[목차로](#목차)

</div>

</br>
