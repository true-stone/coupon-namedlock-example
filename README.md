# Named Lock 기반 쿠폰 발급 시스템

## 프로젝트 개요

이 프로젝트는 Spring Boot + MySQL 환경에서 Named Lock을 이용해 **쿠폰 중복 발급과 재고 초과를 방지**하는 구조를 예제로 구현한 것입니다.

- MySQL Named Lock
- 트랜잭션 분리
- 커넥션 풀 분리
- 테스트 코드 포함

## 주요기능

- 쿠폰 발급 기능
  - 사용자 ID와 쿠폰 코드를 기반으로 쿠폰을 발급하는 REST API 제공
- 재고 기반 발급 수량 관리
  - `CouponStock` 테이블의 `remaining_quantity` 값을 기반으로 발급 가능 수량을 실시간으로 관리
- **Named Lock**을 사용한 동시성 제어
  - `SELECT GET_LOCK()` / `RELEASE_LOCK()`으로 쿠폰 발급 시 경합 방지 및 Race Condition 제거
- 유저별 쿠폰 **중복 발급 방지**
  - 동일한 유저가 동일 쿠폰을 여러 번 발급받지 못하도록 검증 로직 포함
- 재고 초과 발급 방지
  - 락 처리용 트랜잭션과 비즈니스 로직용 트랜잭션 분리
- P6Spy 기반 트랜잭션 SQL 로깅
- Junit + AssertJ 기반 테스트 코드 제공
