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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit4.SpringRunner;
import study.querydsl.Config.TestConfig;
import study.querydsl.controller.requestDto.MemberSearchCondition;
import study.querydsl.controller.responseDto.MemberTeamResponseDto;
import study.querydsl.domain.Member.Member;
import study.querydsl.domain.Team.Team;
import study.querydsl.domain.Member.memberRepository.MemberRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
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
                .username(null).teamName("A").ageGoe(5).ageLoe(15) // 6, 8 , 10, 12, 14
                .build();                                   // offset-0, 1 ,  2,  3,  4

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
        log.info("OFFSET -> "+memberTeamResponseDtoQueryResults.getOffset()); //2
        log.info("LIMIT  -> "+memberTeamResponseDtoQueryResults.getLimit()); // 2
        log.info("TOTAL  -> "+memberTeamResponseDtoQueryResults.getTotal()); // 5

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
    @Test
    public void SearchTest3(){
        // given
        MemberSearchCondition memberSearchCondition = MemberSearchCondition.builder().teamName("B").build();

        PageRequest pageRequest1 = PageRequest.of(0, 50); // 첫 페이지
        PageRequest pageRequest2 = PageRequest.of(2, 3); // offset 0 3 6  -> 중간 페이지
        PageRequest pageRequest3 = PageRequest.of(9, 1);  // 1 3 5 7 9 11 13 15 17 19  -> 마지막 페이지

        // when
        Page<MemberTeamResponseDto> memberTeamResponseDtos = memberRepository.searchByConditions(pageRequest1, memberSearchCondition);
        Page<MemberTeamResponseDto> memberTeamResponseDtos2 = memberRepository.searchByConditions(pageRequest2, memberSearchCondition);
        Page<MemberTeamResponseDto> memberTeamResponseDtos3 = memberRepository.searchByConditions(pageRequest3, memberSearchCondition);

        // then
        List<MemberTeamResponseDto> content = memberTeamResponseDtos.getContent();
        content.forEach(memberTeamResponseDto -> log.info("memberSearchCondition 1-> "+memberTeamResponseDto));
        Assertions.assertThat(content.size()).isEqualTo(10); // 1 3 5 7 9 11 13 15 17 19


        List<MemberTeamResponseDto> content2 = memberTeamResponseDtos2.getContent();
        content2.forEach(memberTeamResponseDto -> log.info("memberSearchCondition 2-> "+memberTeamResponseDto));
        Assertions.assertThat(content2).extracting("username").containsExactly("13","15","17");


        List<MemberTeamResponseDto> content3 = memberTeamResponseDtos3.getContent();
        content3.forEach(memberTeamResponseDto -> log.info("memberSearchCondition 3-> "+memberTeamResponseDto));
        Assertions.assertThat(content3).extracting("username").containsExactly("19");
    }
    @Test
    public void SearchTestImprove(){
        // given
        MemberSearchCondition memberSearchCondition = MemberSearchCondition.builder().teamName("B").build();

        PageRequest pageRequest1 = PageRequest.of(0, 50); // 첫 페이지
        PageRequest pageRequest2 = PageRequest.of(2, 3); // offset 0 3 6  -> 중간 페이지
        PageRequest pageRequest3 = PageRequest.of(5, 2);  // 1 3 5 7 9 11 13 15 17 19  -> 마지막 페이지

        // when
        Page<MemberTeamResponseDto> memberTeamResponseDtos = memberRepository.pagingCntImprove(pageRequest1, memberSearchCondition);
        Page<MemberTeamResponseDto> memberTeamResponseDtos2 = memberRepository.pagingCntImprove(pageRequest2, memberSearchCondition);
        Page<MemberTeamResponseDto> memberTeamResponseDtos3 = memberRepository.pagingCntImprove(pageRequest3, memberSearchCondition);

        // then
        List<MemberTeamResponseDto> content = memberTeamResponseDtos.getContent();
        content.forEach(memberTeamResponseDto -> log.info("memberSearchCondition 1-> "+memberTeamResponseDto));
                // not Count Query
        Assertions.assertThat(content.size()).isEqualTo(10); // 1 3 5 7 9 11 13 15 17 19


        List<MemberTeamResponseDto> content2 = memberTeamResponseDtos2.getContent();
        content2.forEach(memberTeamResponseDto -> log.info("memberSearchCondition 2-> "+memberTeamResponseDto));
        Assertions.assertThat(content2).extracting("username").containsExactly("13","15","17");


        List<MemberTeamResponseDto> content3 = memberTeamResponseDtos3.getContent();
        content3.forEach(memberTeamResponseDto -> log.info("memberSearchCondition 3-> "+memberTeamResponseDto));
        // not Count Query (마지막 페이지일때 offset+size= 전체 사이즈와 비교.
        Assertions.assertThat(content3).extracting("username").containsExactly();
    }
}
