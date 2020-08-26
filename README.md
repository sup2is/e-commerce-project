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

### 회원 필드 정보
- member_id pk
- email unique
- password
- name
- address
- zip
- phone



## 주문



## 상품

- 상품 등록 / 수정 /  (판매자)
- 상품


## apis

- /auth/token -> client가 email과 password로 요청하고 member-service에서 email과 password가 일치하면 정상 token 발행
그리고 해당 유저는 security context에 집어넣음 이후 요청시 세션에 유저가 있으면 token 유효성 검사만 하고 filter pass
  - zuul -> auth-service (토큰 발행 담당 end-point) -> member-service (db에서 member 가져옴)

- /api/** -> /api는 매 요청마다 accessToken이 반드시 있어야함 zuul filter에서 검사함 accesstoken은 있고 security context에
없으면 단순히 member-service에서 user만 가져옴 

# Note.

- zuul에서 api 분기를 /api, /auth로 나눌것
  - /api는 인증된 세션 & token이 유효한 경우에만 접근 가능, zuul filter에서 처리
  - 실제 세션검사는 auth-service에서 feign client 사용할 것
  - /auth는 jwt 토큰 발급 분기

- 모든 서버들은 redis 로 session clustering 되어야함

- 일반적인 주문 프로세스
  - 로그인한 유저가 상품 목록을 불러들임 zuul -> product service
  - 해당 상품 목록에서 한개의 상품 상세 zuul -> product service
  - 실제 주문하기위해 product id 를 기반으로 zuul -> order service에 요청
  - order service에는 세션에서 멤버 id와 product id를 기반으로 주문데이터를 만듦
  - 이렇게하면 전부 하나의 트랜잭션밖에 안섞임 .. 두개가 섞일곳이 없네 ..

