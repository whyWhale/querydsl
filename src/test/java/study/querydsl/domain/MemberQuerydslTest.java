package study.querydsl.domain;

import com.querydsl.core.QueryFactory;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
public class MemberQuerydslTest {
    @PersistenceContext
    EntityManager em;


    @BeforeEach
    public void before()
    {
        Team A = new Team("A");
        Team B = new Team("B");
        em.persist(A);
        em.persist(B);


        Member a = Member.builder().username("A").age(10).build();
        a.setTeam(A);
        Member b = Member.builder().username("B").age(27).build();
        b.setTeam(B);
        Member c = Member.builder().username("C").age(22).build();
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

}
