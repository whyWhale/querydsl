package study.querydsl.domain;

import com.querydsl.core.QueryResults;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceUnit;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static study.querydsl.domain.QMember.member;
import static study.querydsl.domain.QTeam.team;

@SpringBootTest
@Transactional
public class MemberQuerydslTest {
    @PersistenceContext
    EntityManager em;

    JPAQueryFactory queryFactory;

    @BeforeEach
    public void before() {
        queryFactory = new JPAQueryFactory(em);
        Team A = new Team("A");
        Team B = new Team("B");
        em.persist(A);
        em.persist(B);

        Member a = Member.builder().username("Q").age(10).build();
        a.setTeam(A);
        Member b = Member.builder().username("W").age(27).build();
        b.setTeam(B);
        Member c = Member.builder().username("N").age(22).build();
        c.setTeam(B);
        Member d = Member.builder().username("D").age(33).build();
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
                .setParameter("username", "A").getSingleResult();
        //then
        assertThat(singleResult.getUsername()).isEqualTo("A");
    }

    @Test
    public void QueryDsl() {
        //given

        //when
        JPAQueryFactory queryFactory = new JPAQueryFactory(em);
        QMember qMember = new QMember("m");
        Member result = queryFactory.select(qMember).from(qMember).where(qMember.username.eq("A")).fetchOne();

        //then
        assertThat(result.getUsername()).isEqualTo("A");
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

        //when
        List<Member> members = queryFactory.selectFrom(member).orderBy(member.age.desc()).offset(2).limit(2).fetch();

        QueryResults<Member> memberQueryResults = queryFactory.selectFrom(member).orderBy(member.age.desc()).offset(2).limit(2).fetchResults();

        //then
        assertThat(members.get(0).getUsername()).isEqualTo("N");
        assertThat(members.get(1).getUsername()).isEqualTo("A");

        assertThat(memberQueryResults.getTotal()).isEqualTo(4);
        assertThat(memberQueryResults.getLimit()).isEqualTo(2);
        assertThat(memberQueryResults.getOffset()).isEqualTo(2);
        assertThat(memberQueryResults.getResults().size()).isEqualTo(2);
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
        int b =  (27 + 22 + 33) / 3;

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
    public void Join() throws Exception{
        //given

        //when
        List<Member> teamA = queryFactory.selectFrom(member).join(member.team, team).where(team.name.eq("A"))
                .fetch();

        //then
        assertThat(teamA.isEmpty()).isEqualTo(false);
        assertThat(teamA).extracting("username").containsExactly("A");
    }
    
    @Test
    public void ThetaJoin() throws Exception{
        //given

        //when
        List<Member> ThetaMember = queryFactory.select(member).from(member, team).where(member.username.eq(team.name)).fetch();
        //then
        assertThat(ThetaMember).extracting("username").containsExactly("A","B");
    }


    @Test
    public void JoinOnFilter() throws Exception{
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
    public void JoinOn_notRelation() throws Exception{
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
       /* for (Tuple tuple : tuples) {
            System.out.println(tuple.get(member)+"  "+tuple.get(team));
        }*/
    }

    @PersistenceUnit
    EntityManagerFactory emf;

    @Test
    public void NotFetchJoin() throws Exception{
        //given
        em.flush();
        em.clear();

        //when
        List<Member> data = queryFactory.selectFrom(member).where(member.username.eq("Q")).fetch();
        //then
        System.out.println(data.get(0));
        boolean loaded = emf.getPersistenceUnitUtil().isLoaded(data.get(0).getTeam());
        assertThat(loaded).as("일반 적 조회(페치조인 x)").isFalse();

    }
    @Test
    public void FetchJoin() throws Exception{
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
    public void subQuery() throws Exception{
        //given
        QMember sub=new QMember("subMember");
        //when
        List<Member> maxAgeMembers = queryFactory.selectFrom(member).where(member.age.eq(JPAExpressions.select(sub.age.max()).from(sub))).fetch();

        //then
        assertThat(maxAgeMembers).extracting("age").containsExactly(33);
    }

    @Test
    public void subQuery2() throws Exception{
        //given
        int avg = (10 + 22 + 27 + 33)/4;
        QMember sub = new QMember("sub");
        //when
        List<Member> avgAgeGtMembers = queryFactory.selectFrom(member).where(member.age.gt(JPAExpressions.select(sub.age.avg()).from(sub))).fetch();

        //then
        for (Member avgAgeGtMember : avgAgeGtMembers) {
            System.out.println("avgAgeGtMember = " + avgAgeGtMember);
        }
        assertThat(avgAgeGtMembers).extracting("username").containsExactly("W","D");
    }

    @Test
    public void suqQuery3() throws Exception{
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
    public void subQuery4() throws Exception{
        //given
        QMember sub = new QMember("sub");
        //when
        List<Member> gt10Members = queryFactory.selectFrom(member).where(member.age.in(JPAExpressions
                .select(sub.age).from(sub).where(sub.age.gt(10)))).fetch();
        //then
        for (Member gt10Member : gt10Members) {
            System.out.println("gt10Member = " + gt10Member);
        }
        assertThat(gt10Members).extracting("age").containsExactly(27,22,33);
    }

}
