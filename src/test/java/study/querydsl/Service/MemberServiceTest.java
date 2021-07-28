package study.querydsl.Service;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import study.querydsl.controller.requestDto.MemberSearchCondition;
import study.querydsl.controller.requestDto.PageRequestDto;
import study.querydsl.controller.responseDto.MemberResponseDto;
import study.querydsl.controller.responseDto.MemberTeamResponseDto;
import study.querydsl.controller.responseDto.PageResponseDto;
import study.querydsl.domain.Member.Member;
import study.querydsl.domain.Team.Team;
import study.querydsl.domain.Member.memberRepository.MemberRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.stream.IntStream;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
@Slf4j
public class MemberServiceTest {

    @PersistenceContext
    EntityManager em;

    @Autowired
    private MemberService memberService;
    @Autowired
    private MemberRepository memberRepository;

    @Before
    public void setUp() {
        Team A= Team.builder().name("A").build();
        Team B= Team.builder().name("B").build();
        em.persist(A);
        em.persist(B);
        IntStream.rangeClosed(1,51).forEach(longValue -> {
            Member member = Member.builder().username(String.valueOf(longValue)).age((int) longValue).build();
            if(longValue<=25)
                member.setTeam(A);
            else {
                member.setTeam(B);
            }
            em.persist(member);
        });
        em.flush();
        em.clear();
        em.close();
    }

    @Test
    public void getPagingList() {
        PageRequestDto pageRequestDto = PageRequestDto.builder().page(2).size(5).build();
        PageResponseDto<MemberResponseDto, Member> pagingList = memberService.memberList(pageRequestDto);
        pagingList.getDtoList().forEach(memberResponseDto -> log.info("memberResponseDto -> "+memberResponseDto));
        Assertions.assertThat(pagingList.getDtoList()).extracting("username")
                .containsExactly("6","7","8","9","10");
    }

    @Test
    public void Paging_MemberTeamResponseDto(){
        // given
        PageRequestDto pageRequestDto = PageRequestDto.builder().page(3).size(10).build(); //  [21-25]
        MemberSearchCondition memberSearchCondition = MemberSearchCondition.builder()
                .teamName("A").build();

        // when
        PageResponseDto<MemberTeamResponseDto, Member> memberTeamResponseDtos = memberService.memberSearchConditionList(pageRequestDto, memberSearchCondition);

        // then

        List<MemberTeamResponseDto> dtoList = memberTeamResponseDtos.getDtoList();
        Assertions.assertThat(dtoList).extracting("username").containsExactly("21","22","23","24","25");
        log.info("paging Dtos  -> "+ memberTeamResponseDtos.toString());
    }

    @Test
    public void pagingCntImprove(){
        // given
        PageRequestDto pageRequestDto1 = PageRequestDto.builder().page(1).size(50).build(); //  [26-50]
        PageRequestDto pageRequestDto2 = PageRequestDto.builder().page(2).size(20).build(); //  [46-50]

        MemberSearchCondition memberSearchCondition = MemberSearchCondition.builder()
                .teamName("B").build();
        // when
        PageResponseDto<MemberTeamResponseDto, Member> responseDto1 = memberService.pagingCntImprove(pageRequestDto1, memberSearchCondition);
        PageResponseDto<MemberTeamResponseDto, Member> responseDto2 = memberService.pagingCntImprove(pageRequestDto2, memberSearchCondition);
        // then
        responseDto1.getDtoList().forEach(memberTeamResponseDto -> log.info("memberTeamResponseDto -> "+memberTeamResponseDto));
        Assertions.assertThat(responseDto1.getTotalPage()).isEqualTo(1L);  // not Cnt Query

        log.info("= = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = ");

        responseDto2.getDtoList().forEach(memberTeamResponseDto -> log.info("memberTeamResponseDto2 -> "+memberTeamResponseDto));
        Assertions.assertThat(responseDto2.getTotalPage()).isEqualTo(2L);   // not Cnt Query

    }
}