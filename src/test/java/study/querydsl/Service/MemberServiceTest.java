package study.querydsl.Service;

import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import study.querydsl.controller.requestDto.PageRequestDto;
import study.querydsl.controller.responseDto.MemberResponseDto;
import study.querydsl.controller.responseDto.PageResponseDto;
import study.querydsl.domain.Member;
import study.querydsl.domain.memberRepository.MemberRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

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
        for (int i = 0; i < 20; i++) {
            Member member = Member.builder().username(String.valueOf(i)).age(i).build();
            em.persist(member);
        }
    }

    @Test
    public void getPagingList() {
        PageRequestDto pageRequestDto = PageRequestDto.builder().page(2).size(5).build();
        PageResponseDto<MemberResponseDto, Member> pagingList = memberService.getPagingList(pageRequestDto);
        pagingList.getDtoList().forEach(System.out::println);
    }
}