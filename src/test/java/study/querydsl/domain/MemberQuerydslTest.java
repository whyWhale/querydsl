package study.querydsl.domain;

import com.querydsl.core.QueryFactory;
import com.querydsl.core.QueryResults;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static study.querydsl.domain.QMember.*;

@SpringBootTest
@Transactional
public class MemberQuerydslTest {
    @PersistenceContext
    EntityManager em;

    JPAQueryFactory queryFactory;

    @BeforeEach
    public void before()
    {
        queryFactory=new JPAQueryFactory(em);
        Team A = new Team("A");
        Team B = new Team("B");
        em.persist(A);
        em.persist(B);


        Member a = Member.builder().username("A").age(10).build();
        a.setTeam(A);
        Member b = Member.builder().username("B").age(27).build();
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
    public void JPQL()
    {
        //given

        //when
        Member singleResult = em.createQuery("select m from Member m where m.username=:username", Member.class)
                .setParameter("username", "A").getSingleResult();
        //then
        assertThat(singleResult.getUsername()).isEqualTo("A");
    }

    @Test
    public void QueryDsl()
    {
        //given

        //when
        JPAQueryFactory queryFactory = new JPAQueryFactory(em);
        QMember qMember=new QMember("m");
        Member result = queryFactory.select(qMember).from(qMember).where(qMember.username.eq("A")).fetchOne();

        //then
        assertThat(result.getUsername()).isEqualTo("A");
    }


    @Test
    public void sortTest()
    {
        //given
        Member a = Member.builder().username(null).age(100).build();
        Member b = Member.builder().username("BB").age(100).build();
        Member c= Member.builder().username(null).age(100).build();
        Member d = Member.builder().username("DD").age(100).build();
        em.persist(a);
        em.persist(b);
        em.persist(c);
        em.persist(d);

        //when
        List<Member> members = queryFactory.selectFrom(member).orderBy(member.age.desc(), member.username.asc().nullsFirst()).fetch();

        //then
       assertThat(members.get(0).getUsername()).isNull();
       assertThat(members.get(1).getUsername()).isNull();
    }

    @Test
    public void pagingTest()
    {
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

}
