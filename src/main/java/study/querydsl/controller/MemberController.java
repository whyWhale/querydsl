package study.querydsl.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import study.querydsl.Service.MemberService;
import study.querydsl.controller.requestDto.MemberSaveRequestDto;
import study.querydsl.controller.requestDto.MemberUpdateRequestDto;
import study.querydsl.controller.responseDto.MemberResponseDto;

@RequiredArgsConstructor
@Slf4j
@RequestMapping("/member")
@Controller
public class MemberController {

    private final MemberService memberService;

    /* CRUD*/
    @GetMapping("/create")
    public void MemberCreatePage(@ModelAttribute("memberSaveRequestDto") MemberSaveRequestDto memberSaveRequestDto, BindingResult bindingResult)
    {
        log.info("member Create Page .... ");
    }

    @PostMapping("")
    public String MemberCreate(MemberSaveRequestDto memberSaveRequestDto
            , BindingResult bindingResult, RedirectAttributes redirectAttributes)
    {
        log.info("member Create Featuring ...  ");
        if(bindingResult.hasErrors())
        {
            log.warn("not property Field Value Error");
            return "member/create";
        }
        memberService.Save(memberSaveRequestDto);

        redirectAttributes.addFlashAttribute("msg","멤버 등록 완료");
        return "redirect:/main";

    }


    @GetMapping({"/detail","/update"})
    public void MemberDetail(Long memberId, Model model)
    {
        log.info("member Detail Page.. id is "+memberId);
        MemberResponseDto detail_update_MemberResponseDto = memberService.Detail(memberId);
        model.addAttribute("memberResponseDto",detail_update_MemberResponseDto);
    }

   @PostMapping("/update")
    public String MemberUpdate(
            @ModelAttribute("memberUpdateRequestDto")MemberUpdateRequestDto memberUpdateRequestDto
            ,RedirectAttributes redirectAttributes, BindingResult bindingResult)
   {
       log.info("member Update Featuring .... -> "+memberUpdateRequestDto.toString());
       if(bindingResult.hasErrors())
       {
           log.warn("not property Field Value Error");
           return "member/create";
       }
       memberService.Update(memberUpdateRequestDto);
//       redirectAttributes.addFlashAttribute("msg","멤버 등록 완료");
       return "redirect:/member/detail?memberId="+memberUpdateRequestDto.getId();
   }
     /* @PostMapping("")
    public String save(@Valid MemberSaveRequestDto requestDto , BindingResult result)
    {
        if(result.hasErrors())
        {
            log.warn("{}","name Required");
            return "member/signUpForm";
        }
        log.info("{}","CREAT MEMBER"+requestDto.toString());
        memberService.Join(requestDto.toEntity());

        return "redirect:/";
    }*/
}