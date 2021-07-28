package study.querydsl.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import study.querydsl.Service.MemberService;
import study.querydsl.controller.requestDto.PageRequestDto;

@RequiredArgsConstructor
@Slf4j
@RequestMapping("/member")
@Controller
public class MemberController {
    private final MemberService memberService;

    @GetMapping("")
    public String memberPagingList(PageRequestDto requestDto, Model model)
    {
      log.info("requestDto >>>> "+requestDto);

      model.addAttribute("result",memberService.memberList(requestDto));
      return "list";
    }
}