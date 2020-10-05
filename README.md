# e-commerce-project

Spring Cloud로 작성하는 E-Commerce 플랫폼

# 사용 기술

- Java 8
- Spring Boot 2.3.1
- Maven
- Spring Cloud
  - Netflix Zuul API Gateway
  - Netflix Eureka (Service Discovery)
  - Netflix Feign Client
- Docker & Docker Compose
- Junit
- Intellij
- Redis
- Spring Data JPA
- Spring Rest Docs
- Jwt기반 인증
- H2
- Mysql
- 기타 등등

# 구현 목표


- Spring Cloud 사용하기
- Container 기반 인프라 구성
- TPS 최대한 끌어올릴 수 있는 설계
- 활용할 수 있는 기술 모두 사용하기
- 테스트코드 필수
- ★ 객체지향적으로, 일관성 + 책임을 기반으로 프로그래밍
- **기간은 중요하지 않다! 꾸준하게!**


# 애플리케이션 요구사항

## 회원
- 회원가입
- 인증 모듈 with JWT
- 일반 고객 / 판매자 기능

## 주문

- 주문 등록 / 수정 / (일반회원)
- 주문 조회

## 상품

- 상품 등록 / 수정 /  (판매자)
- 상품 조회
- 상품 검색 (카테고리별, 이름)

# 구성

![architecture](https://github.com/sup2is/e-commerce-project/blob/master/architecture.png)





<br>

<hr>



# Note.

- zuul에서 api 분기를 /api, /auth로 나눌것
  - /api는 인증된 세션 & token이 유효한 경우에만 접근 가능, zuul filter에서 처리
  - 실제 세션검사는 auth-service에서 feign client 사용할 것
  - /auth는 jwt 토큰 발급 분기
- /auth/token -> client가 email과 password로 요청하고 member-service에서 email과 password가 일치하면 정상 token 발행
그리고 해당 유저는 security context에 집어넣음 이후 요청시 세션에 유저가 있으면 token 유효성 검사만 하고 filter pass
  - zuul -> auth-service (토큰 발행 담당 end-point) -> member-service (db에서 member 가져옴)

- /api/** -> /api는 매 요청마다 accessToken이 반드시 있어야함 zuul filter에서 검사함 accesstoken은 있고 security context에
없으면 단순히 member-service에서 user만 가져옴 

- 일반적인 주문 프로세스
  - 로그인한 유저가 상품 목록을 불러들임 zuul -> product service
  - 해당 상품 목록에서 한개의 상품 상세 zuul -> product service
  - 실제 주문하기위해 product id 를 기반으로 zuul -> order service에 요청
  - order service에는 세션에서 멤버 id와 product id를 기반으로 주문데이터를 만듦
  - 이렇게하면 전부 하나의 트랜잭션밖에 안섞임 .. 두개가 섞일곳이 없네 ..

- 유저는 일반 회원으로 가입하거나 판매자로 가입할 수 있음
  - 한개의 아이디는 일반 회원과 판매자 권한을 둘 다 가질 수 있음

- product service의 seller id와 member service의 member id는 pk를 가짐,
seller 권한이 있는 member가 product service를 통해 상품을 등록할 수 있지만,
member의 enable 또는 member가 탈퇴했을때 product service에서 해당 seller id의 salable을
전부 disable로 바꿔야함 <- message 기반으로 ex kafka

- order는 한번에 여러개의 상품을 주문할 수 있어서 order와 order item 으로 풀어야함(1:N)
order item은 product id를 pk로 가짐 but, order를 등록하는 시점에 product id의 
무결성 & 재고를체크해야함.. how? 
redis에 product id를 넣고 set으로 관리하고 해당 상품이 product service에 의해
판매 불가상품이 된다면? <- message 기반으로 cache evict? 고민해봐야 할 문제.
아니면 order쪽에 product의 복제테이블을 둬서 관리하는것도 괜찮을 것 같음

- 각 서비스마다 필요한 dto를 .. 각 서비스마다 개별적으로 두는것으로 결정함 

- member는 서비스마다 공통적으로 필요한 요소임 ... 이걸 rest call로 가져올지
세션에 저장해서 가져올지 정말 고민됨

- 가격체크도 서버에서 해야할듯. product 재고 + 상품 가격으로 수정

- redis에서 write하는 부분은 하나의 모듈로 빼도 괜찮을듯

- order service에서 주문을 하는 시점에 product service에 주무 수량만큼의 재고가 차감되어야함
이걸 레스트로 쓰기에는 약간 부담스럽다는 결론. kafka 사용해야할듯
