package study.querydsl.domain;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.QueryResults;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import study.querydsl.controller.responseDto.MemberResponseDto;
import study.querydsl.controller.responseDto.QMemberResponseDto;
import study.querydsl.domain.Member.Member;
import study.querydsl.domain.Member.QMember;
import study.querydsl.domain.Team.QTeam;
import study.querydsl.domain.Team.Team;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceUnit;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static study.querydsl.domain.Member.QMember.*;
import static study.querydsl.domain.Team.QTeam.*;

@SpringBootTest
@Slf4j
@Transactional
public class MemberQuerydslTest {
    @PersistenceContext
    EntityManager em;

    @Autowired
    JPAQueryFactory queryFactory;

    @BeforeEach
    public void before() {
        Team A = new Team("A");
        Team B = new Team("B");
        em.persist(A);
        em.persist(B);

        Member a = Member.builder().username("Q").age(10).build();
        a.setTeam(A);
        Member b = Member.builder().username("W").age(27).build();
        b.setTeam(B);
        Member c = Member.builder().username("E").age(22).build();
        c.setTeam(B);
        Member d = Member.builder().username("R").age(33).build();
        d.setTeam(B);

        em.persist(a);
        em.persist(b);
        em.persist(c);
        em.persist(d);
    }

    @Test
    public void JPQL() {
        //given

        //when
        Member singleResult = em.createQuery("select m from Member m where m.username=:username", Member.class)
                .setParameter("username", "Q").getSingleResult();
        //then
        assertThat(singleResult.getUsername()).isEqualTo("Q");
    }

    @Test
    public void QueryDsl() {
        //given

        //when
        JPAQueryFactory queryFactory = new JPAQueryFactory(em);
        QMember qMember = new QMember("m");
        Member result = queryFactory.select(qMember).from(qMember).where(qMember.username.eq("A")).fetchOne();

        //then
        assertThat(result).isNull();
    }


    @Test
    public void sortTest() {
        //given
        Member a = Member.builder().username(null).age(100).build();
        Member b = Member.builder().username("BB").age(100).build();
        Member c = Member.builder().username(null).age(100).build();
        Member d = Member.builder().username("DD").age(100).build();
        em.persist(a);
        em.persist(b);
        em.persist(c);
        em.persist(d);

        //when
        List<Member> members = queryFactory.selectFrom(member).orderBy(defaultCondition()).fetch();

        List<Member> members2 = queryFactory.selectFrom(member).orderBy(TwoCondition()).fetch();

        //then
        assertThat(members2.get(0).getUsername()).isNull();
        assertThat(members2.get(1).getUsername()).isNull();
    }

    private OrderSpecifier[] TwoCondition() {
        return new OrderSpecifier[]{member.age.desc(), member.username.asc().nullsFirst()};
    }

    private OrderSpecifier[] defaultCondition() {
        return new OrderSpecifier[]{member.id.asc()};
    }


    @Test
    public void pagingTest() {
        //given
//        Q - A (10)
//        W,E,R - B (27,22,33)
        //when
        List<Member> members = queryFactory.selectFrom(member).orderBy(member.age.desc()).offset(0).limit(2).fetch();

        QueryResults<Member> memberQueryResults = queryFactory.selectFrom(member).orderBy(member.age.desc()).offset(0).limit(3).fetchResults();
        //then
        assertThat(members.get(0).getUsername()).isEqualTo("R");
        assertThat(members.get(1).getUsername()).isEqualTo("W");

        assertThat(memberQueryResults.getTotal()).isEqualTo(4);
        assertThat(memberQueryResults.getLimit()).isEqualTo(3);
        assertThat(memberQueryResults.getOffset()).isEqualTo(0);
        assertThat(memberQueryResults.getResults().size()).isEqualTo(3);
    }

    @Test
    public void aggregation() throws Exception {
        //given
        //when
        List<Tuple> memberInfo = queryFactory.select(  // tuple 여러개의 타입.
                member.count(),
                member.age.sum(),
                member.age.max(),
                member.age.avg(),
                member.age.min()
        ).from(member).fetch();
        //then
        Tuple tuple = memberInfo.get(0);
        assertThat(tuple.get(member.count())).isEqualTo(4);
        assertThat(tuple.get(member.age.sum())).isEqualTo(92);
        assertThat(tuple.get(member.age.min())).isEqualTo(10);
        assertThat(tuple.get(member.age.max())).isEqualTo(33);
    }

    @Test
    public void groupBy() throws Exception {
        //given
        int a = 10 / 1;
        int b = (27 + 22 + 33) / 3;

        //when
        List<Tuple> joinTeamMember = queryFactory.select(team.name, member.age.avg())
                .from(member)
                .join(member.team, team)
                .groupBy(team.name)
                .fetch();

        Tuple tupleA = joinTeamMember.get(0);
        Tuple tupleB = joinTeamMember.get(1);
        //then
        assertThat(tupleA.get(team.name)).isEqualTo("A");
        assertThat(tupleB.get(team.name)).isEqualTo("B");

        assertThat(tupleA.get(member.age.avg())).isEqualTo(a);
        assertThat(Math.round(tupleB.get(member.age.avg()))).isEqualTo(b);
    }


    @Test
    public void Join() throws Exception {
        //given

        //when
        List<Member> teamA = queryFactory.selectFrom(member).join(member.team, team).where(team.name.eq("A")) // 내부 조인.
                .fetch();

        //then
        assertThat(teamA.isEmpty()).isEqualTo(false);
        assertThat(teamA).extracting("username").containsExactly("Q");
    }

    @Test
    public void ThetaJoin() throws Exception {
        //given
        em.persist(Team.builder().name("Q").build());
        em.persist(Team.builder().name("W").build());
        //when
        List<Member> ThetaMember = queryFactory.select(member)
                .from(member, team)
                .where(member.username.eq(team.name)).fetch();
        //then
        assertThat(ThetaMember).extracting("username").containsExactly("Q", "W");
    }

    @Test
    public void JoinOnFilter() throws Exception {
        //given
        Member a = Member.builder().username("ABC").age(100).build();
        Member b = Member.builder().username("DEF").age(100).build();
        em.persist(a);
        em.persist(b);

        //when

        List<Tuple> tuples = queryFactory.select(member, team).from(member).leftJoin(member.team, team)
                .on(team.name.eq("A").or(team.name.eq("B")))
                .fetch();
        //then
        assertThat(tuples.size()).isEqualTo(6);
    }

    @Test
    public void JoinOn_notRelation() throws Exception {
        //given
        Member a = Member.builder().age(100).username("A").build();
        Member b = Member.builder().age(100).username("B").build();
        em.persist(a);
        em.persist(b);
        //when
        List<Tuple> tuples = queryFactory.select(member, team).from(member)
                .leftJoin(team).on(member.username.eq(team.name)).fetch();
        //then
        assertThat(tuples.size()).isEqualTo(6);
        for (Tuple tuple : tuples) {
          log.info(tuple.get(member)+"  "+tuple.get(team));
        }
    }

    @PersistenceUnit
    EntityManagerFactory emf;

    @Test
    public void NotFetchJoin() throws Exception {
        //given
        em.flush();
        em.clear();

        //when
        List<Member> data = queryFactory.selectFrom(member).where(member.username.eq("Q")).fetch();
        //then
        log.info("data -> {}",data.get(0));
        boolean loaded = emf.getPersistenceUnitUtil().isLoaded(data.get(0).getTeam());
        assertThat(loaded).as("일반 적 조회(페치조인 x)").isFalse();

    }

    @Test
    public void FetchJoin() throws Exception {
        //given
        em.flush();
        em.clear();

        //when
        Member data = queryFactory.selectFrom(member).join(member.team, team).fetchJoin()
                .where(member.username.eq("Q")).fetchOne();
        boolean loaded = emf.getPersistenceUnitUtil().isLoaded(data.getTeam());
        //then
        assertThat(loaded).as("fetch Join ").isTrue();

    }

    @Test
    public void subQuery() throws Exception {
        //given
        QMember sub = new QMember("subMember");
        //when
        List<Member> maxAgeMembers = queryFactory.selectFrom(member).where(member.age.eq(JPAExpressions.select(sub.age.max()).from(sub))).fetch();

        //then
        assertThat(maxAgeMembers).extracting("age").containsExactly(33);
    }

    @Test
    public void subQuery2() throws Exception {
        //given
        int avg = (10 + 22 + 27 + 33) / 4;
        QMember sub = new QMember("sub");
        //when
        List<Member> avgAgeGtMembers = queryFactory.selectFrom(member).where(member.age.gt(JPAExpressions.select(sub.age.avg()).from(sub))).fetch();

        //then
        for (Member avgAgeGtMember : avgAgeGtMembers) {
            System.out.println("avgAgeGtMember = " + avgAgeGtMember);
        }
        assertThat(avgAgeGtMembers).extracting("username").containsExactly("W", "R");
    }

    @Test
    public void suqQuery3() throws Exception {
        //given
        QMember sub = new QMember("sub");
        //when
        List<Tuple> members = queryFactory.select(member.username, JPAExpressions
                .select(sub.age.avg()).from(sub)).from(member).fetch();
        //then
        for (Tuple tuple : members) {
            System.out.println("tuple = " + tuple);
            System.out.println(tuple.get(JPAExpressions.select(sub.age.avg()).from(sub)));
        }

        for (int i = 0; i < 4; i++) {
            assertThat(members.get(i).get(JPAExpressions.select(sub.age.avg()).from(sub))).isEqualTo(23.0);
        }

    }

    @Test
    public void subQuery4() throws Exception {
        //given
        QMember sub = new QMember("sub");
        //when
        List<Member> gt10Members = queryFactory.selectFrom(member).where(member.age.in(JPAExpressions
                .select(sub.age).from(sub).where(sub.age.gt(10)))).fetch();
        //then
        for (Member gt10Member : gt10Members) {
            System.out.println("gt10Member = " + gt10Member);
        }
        assertThat(gt10Members).extracting("age").containsExactly(27, 22, 33);
    }


    @Test
    public void Case() throws Exception {
        //given

        //when
        List<String> CaseMembers = queryFactory.select(new CaseBuilder()
                .when(member.age.between(0, 20)).then("미성년자")
                .otherwise("성인")).from(member).fetch();
        //then
        assertThat(CaseMembers.get(0)).isEqualTo("미성년자");
        for (int i = 1; i < 4; i++) {
            assertThat(CaseMembers.get(i)).isEqualTo("성인");
        }
    }

    @Test
    public void Case2() throws Exception {
        //given

        //when
        NumberExpression<Integer> Rank = new CaseBuilder()
                .when(member.age.between(0, 20)).then(3)
                .when(member.age.between(21, 30)).then(2)
                .otherwise(1);

        List<Tuple> tuples = queryFactory.select(member.username, member.age, Rank)
                .from(member).orderBy(Rank.desc()).fetch();
        //then
        assertThat(tuples.get(0).get(Rank)).isEqualTo(3);
        assertThat(tuples.get(1).get(Rank)).isEqualTo(2);
        assertThat(tuples.get(2).get(Rank)).isEqualTo(2);
        assertThat(tuples.get(3).get(Rank)).isEqualTo(1);

    }

    @Test
    public void Constant() throws Exception {
        //given

        //when
        List<Tuple> tuples = queryFactory.select(member.username, Expressions.constant("A"))
                .from(member).fetch();

        List<String> stringList = queryFactory.select(member.username.concat("_").concat(member.age.stringValue()))
                .from(member).fetch();
        //then

        tuples.forEach(tuple -> System.out.println("tuple = " + tuple));

        stringList.forEach(System.out::println);
    }

    @Test
    public void ProjectionResult() {
        //given

        //when
        List<String> userName = queryFactory.select(member.username)
                .from(member)
                .fetch();

        List<Tuple> name_age = queryFactory.select(member.username, member.age).from(member).fetch();

        //then

        userName.forEach(s ->  log.info("userName : "+s));
        name_age.forEach(s ->  log.info("<Tuple>nameAge : "+s));

    }

    @Test
    public void toDto() {
        //given

        //when
        List<MemberResponseDto> resultList = em.createQuery("select new study.querydsl.controller.responseDto" +
                ".MemberResponseDto(m.username,m.age) from Member m", MemberResponseDto.class)
                .getResultList();
        //then
        resultList.forEach(System.out::println);
    }

    @Test
    public void toDto2() {
        //given
        QMember sub = new QMember("sun");
        //when
        List<MemberResponseDto> memberDtos = queryFactory.select(Projections.constructor(MemberResponseDto.class,
                member.username.as("name"), ExpressionUtils.as(
                        JPAExpressions.select(sub.age.max()).from(sub), "age"))
        ).from(member).fetch();
        //then

        memberDtos.forEach(System.out::println);
    }

    @Test
    public void toDto3() {
        //given

        //when
        List<MemberResponseDto> memberDtos = queryFactory.select(new QMemberResponseDto(member.username, member.age))
                .from(member).fetch();
        //then
        memberDtos.forEach(System.out::println);
    }


    @Test
    public void DynamicQuery_BooleanBuilder() {
        //given
        String username = null;
        Integer age = null;

        String username2 = "N";
        Integer age2 = 10;

        String username3 = "R";
        Integer age3 = null;


        //when
        List<Member> members = ConditionSearchMember(username, age);
        List<MemberResponseDto> mappedDto = members.stream().map(Member::toDto).collect(Collectors.toList());

        List<Member> members2 = ConditionSearchMember(username2, age2);

        List<Member> members3 = ConditionSearchMember(username3, age3);


        //then
        assertThat(mappedDto.size()).isEqualTo(4);
        assertThat(members2.size()).isEqualTo(0);
        assertThat(members3.size()).isEqualTo(1);
    }

    private List<Member> ConditionSearchMember(String username, Integer age) {
        BooleanBuilder builder = new BooleanBuilder();
        System.out.println("username = " + username + ", age = " + age);
        ;
        if (username != null) {
            builder.and(member.username.eq(username));
        }
        if (age != null) {
            builder.and(member.age.eq(age));
        }

        return queryFactory.selectFrom(member).where(builder).fetch();
    }

    @Test
    public void WhereMultipleParameter() throws Exception {
        //given
        String username = "Q";
        Integer age = null;
        //when
        List<Member> members = queryFactory.selectFrom(member).where(usernameEq(username),(ageEq(age))).fetch();

        //then
        assertThat(members.get(0).getUsername()).isEqualTo("Q");
        assertThat(members.size()).isEqualTo(1);
        members.forEach(System.out::println);
    }

    private BooleanExpression usernameEq(String username) {
        return username != null ? member.username.eq(username) : null;
    }

    private BooleanExpression ageEq(Integer age) {
        return age != null ? member.age.eq(age) : null;
    }

    @Test
//    @Commit
    public void BulkUpdate() throws Exception{
        //given
        long cnt = queryFactory.update(member).set(member.username,"미성년자")
                .where(member.age.lt(20)).execute();
        em.flush();
        em.clear();
        //when   REAPEATABLE READ 발생 em.flush() ,em.clear()
        List<Member> members = queryFactory.selectFrom(member).fetch();

        members.forEach(System.out::println);

        //then
    }


    @Test
//    @Commit
    public void BulkUpdate2() throws Exception{
        //given

        //when
        long cnt = queryFactory.update(member).set(member.age, member.age.add(-5)).execute();
        em.flush(); em.clear();

        //then
        List<Member> members = queryFactory.selectFrom(member).fetch();
//        System.out.println("cnt : "+cnt);
        members.forEach(System.out::println);
    }

    @Test
    public void funcCall() throws Exception{
        //given

        //when
        List<String> members = queryFactory.select(Expressions.stringTemplate("function('replace' ,{0},{1},{2})"
                , member.username, "Q", "M")).from(member).fetch();
        //then
        members.forEach(System.out::println);
    }
    @Test
    public void funcCall2() throws Exception{
        //given

        //when
        List<String> membersUserName = queryFactory.select(member.username)
                .from(member)
//                .where(member.username.eq(Expressions.stringTemplate(
//                        "function ('lower',{0} )", member.username)))
                .where(member.username.eq(member.username.lower()))
                .fetch();
        //then
        membersUserName.forEach(System.out::println);
    }



}
