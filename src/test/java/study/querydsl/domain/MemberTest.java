package study.querydsl.domain;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
//@Commit
class MemberTest {

    @PersistenceContext
    EntityManager em;

    @Test
    public void testEntity() {
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

        em.flush();
        em.clear();


        List<Member> MemberFindAll = em.createQuery("select m from Member m left join m.team t on m.team.id = t.id",Member.class)
                .getResultList();


        assertThat(MemberFindAll.size()).isEqualTo(4);


    }
}