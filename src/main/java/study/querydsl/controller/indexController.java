package study.querydsl.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import study.querydsl.Service.MemberService;
import study.querydsl.controller.requestDto.MemberSearchCondition;
import study.querydsl.controller.requestDto.PageRequestDto;
import study.querydsl.controller.responseDto.MemberTeamResponseDto;
import study.querydsl.controller.responseDto.PageResponseDto;
import study.querydsl.domain.Member;

@Slf4j
@RequiredArgsConstructor
@Controller
public class indexController {

    private final MemberService memberService;

    @GetMapping("main")
    public void index(PageRequestDto requestDto, Model model
            , MemberSearchCondition memberSearchCondition)
    {
        log.info("main page ..... ");
        PageResponseDto<MemberTeamResponseDto, Member> dto
                = memberService.memberSearchConditionList(requestDto, memberSearchCondition);
        log.info("dto.getDtoList() -> "+dto.getDtoList());
        model.addAttribute("result",dto);
    }

}


