# e-commerce-project

Spring Cloud로 작성하는 E-Commerce 플랫폼

# 구현 목표


- Spring Cloud 사용하기
- Container 기반 인프라 구성
- TPS 최대한 끌어올릴 수 있는 설계
- 활용할 수 있는 기술 모두 사용하기
- 테스트코드 필수
- **기간은 중요하지 않다! 꾸준하게!**


# 애플리케이션 요구사항

## 회원
- 회원가입
- 회원가입 with SNS 
- 인증 모듈 with JWT
- 일반 고객 / 판매자 기능


## 주문



## 상품

- 상품 등록 / 수정 /  (판매자)
- 상품


# Note.

- zuul에서 api 분기를 /api, /auth로 나눌것
  - /api는 인증된 세션 & token이 유효한 경우에만 접근 가능, zuul filter에서 처리
  - 실제 세션검사는 auth-service에서 feign client 사용할 것
  - /auth는 jwt 토큰 발급 분기

- 모든 서버들은 redis 로 session clustering 되어야함


