## QueryDSL Study



---

### QueryDSL

- Type 안전성.
    - ex ) String으로 연결된 쿼리는 가독성이 떨어지고 오타등으로 잘못된 참조를 Runtime 시점에 확인할 수 있다.
- 유지보수 용이
    - 도메인의 변경으로 인한 쿼리의 수정을 할 필요가 없다.
- 일관성
  - 쿼리 경로와 오퍼레이션은 모두 동일하며, QueryIFS는 공통의 상위 인터페이스를 갖는다.
  - 모든 쿼리 인스턴스는 재사용이 가능하고 쿼리 실행 이후 페이징 데이터, 프로젝션의 정의는 제거된다.


### JPQL DTO 변환

- new 명령어를 사용.
- Dto의 package이름을 다 적어줘야 한다.
- 생성자 방식만 지원.


### QueryDSL 빈 생상 : DTO 변환할 떄 사용.

- 프로퍼티
- 필드 직접
- 생성자 사용. (권장) @QueryProjection
  - 장점 : 컴파일 타입 체크로 타입 체크에 안전한 방법.
  - 단점 : compile QDto 생성
  



### DynamicQuery : BooleanBuilder

- where 조건에 null 값은 무시된다.
- 메서드를 다른 쿼리에서도 재활용 할 수 있다.
- 쿼리 자체의 가독성이 높아진다
