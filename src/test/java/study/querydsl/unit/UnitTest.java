package study.querydsl.unit;

import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;
import study.querydsl.Config.TestConfig;
import study.querydsl.domain.Member;
import study.querydsl.domain.memberRepository.MemberRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;

@RunWith(SpringRunner.class)
@Import(TestConfig.class)
@Slf4j
@DataJpaTest
public class UnitTest {
    @PersistenceContext
    private EntityManager em;

    @Autowired
    private MemberRepository memberRepository;

    @Before
    public void settingData()
    {
        for (int i = 0; i < 100; i++) {
            Member m=Member.builder().username("member"+i).age(i).build();
            em.persist(m);
        }
    }
    @Test
    public void envTest(){
        // given

        // when
        List<Member> all = memberRepository.findAll();
        Optional<Member> findByName = memberRepository.findByName(1L);
        // then
        all.forEach(member -> log.info("member : "+member));
        findByName.ifPresent(member -> log.info("findByName : "+member));
    }
}
