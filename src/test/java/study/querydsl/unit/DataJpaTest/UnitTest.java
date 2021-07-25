package study.querydsl.unit.DataJpaTest;

import com.querydsl.core.QueryResults;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit4.SpringRunner;
import study.querydsl.Config.TestConfig;
import study.querydsl.controller.requestDto.MemberSearchCondition;
import study.querydsl.controller.responseDto.MemberTeamResponseDto;
import study.querydsl.domain.Member;
import study.querydsl.domain.Team;
import study.querydsl.domain.memberRepository.MemberRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.awt.print.Pageable;
import java.util.List;

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
        Team A = Team.builder().name("A").build();
        Team B = Team.builder().name("B").build();
        em.persist(A);
        em.persist(B);
        for (int i = 0; i < 20; i++) {
            Member member = Member.builder().username(String.valueOf(i)).age(i).build();
            if(i%2==0)
            {
                member.setTeam(A);
            }
            else
            {
                member.setTeam(B);
            }
            em.persist(member);
        }

        em.flush();
        em.clear();
        em.close();
    }
    @Test
    public void SearchTest(){
        // given
        MemberSearchCondition memberSearchCondition = MemberSearchCondition.builder()
                .username(null).teamName("A").ageGoe(5).ageLoe(15)
                .build();
        // when
        PageRequest of = PageRequest.of(0, 5);

        List<MemberTeamResponseDto> memberTeamResponseDtos = memberRepository.searchByBuilder(of,memberSearchCondition);
        memberTeamResponseDtos.forEach(memberTeamResponseDto ->
        {
            log.info("memberTeamResponseDto --> "+memberTeamResponseDto);
        });

        // then
        Assertions.assertThat(memberTeamResponseDtos).isNotNull();
        Assertions.assertThat(memberTeamResponseDtos).extracting("username")
                .containsExactly("6","8","10","12","14");
    }
    @Test
    public void SearchTest2(){
        // given
        MemberSearchCondition memberSearchCondition = MemberSearchCondition.builder()
                .username(null).teamName("A").ageGoe(5).ageLoe(15)
                .build();

        MemberSearchCondition memberSearchCondition2 = MemberSearchCondition.builder()
                .username(null).teamName("A").ageGoe(0)
                .build();
        PageRequest of = PageRequest.of(1, 2);
        PageRequest of2 = PageRequest.of(1, 5);

        // when

        QueryResults<MemberTeamResponseDto> memberTeamResponseDtoQueryResults
                = memberRepository.searchByBuilderQueryResults(of, memberSearchCondition);
        memberTeamResponseDtoQueryResults.getResults().forEach(memberTeamResponseDto -> {
            log.info("memberTeamResponseDto -> "+memberTeamResponseDto);
        });
        log.info("OFFSET -> "+memberTeamResponseDtoQueryResults.getOffset());
        log.info("LIMIT  -> "+memberTeamResponseDtoQueryResults.getLimit());
        log.info("TOTAL  -> "+memberTeamResponseDtoQueryResults.getTotal());

        log.info("***************************************************************************");
        QueryResults<MemberTeamResponseDto> memberTeamResponseDtoQueryResults2 = memberRepository.searchByBuilderQueryResults(of2, memberSearchCondition2);
        memberTeamResponseDtoQueryResults2.getResults().forEach(memberTeamResponseDto -> {
            log.info("memberTeamResponseDto -> "+memberTeamResponseDto);
        });
        log.info("OFFSET -> "+memberTeamResponseDtoQueryResults2.getOffset());
        log.info("LIMIT  -> "+memberTeamResponseDtoQueryResults2.getLimit());
        log.info("TOTAL  -> "+memberTeamResponseDtoQueryResults2.getTotal());

        // then
        Assertions.assertThat(memberTeamResponseDtoQueryResults.getTotal()).isEqualTo(5);
        Assertions.assertThat(memberTeamResponseDtoQueryResults.getResults())
                .extracting("username").containsExactly("10","12");

        Assertions.assertThat(memberTeamResponseDtoQueryResults2.getTotal()).isEqualTo(10);
        Assertions.assertThat(memberTeamResponseDtoQueryResults2.getResults())
                .extracting("username").containsExactly("10","12","14","16","18");


    }
}
