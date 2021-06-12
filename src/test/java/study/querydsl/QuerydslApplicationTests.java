package study.querydsl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
class QuerydslApplicationTests {

    @PersistenceContext
    EntityManager em;

    @Test
    void contextLoads() {

        entity entity = new entity();
        em.persist(entity);

        JPAQueryFactory queryFactory=new JPAQueryFactory(em);
        Qentity qentity = Qentity.entity;

        entity queryDsl = queryFactory.selectFrom(qentity).fetchOne();

        assertThat(entity).isEqualTo(queryDsl);
        assertThat(entity.getId()).isEqualTo(queryDsl.getId());

    }

}
