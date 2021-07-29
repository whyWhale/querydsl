package study.querydsl.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import study.querydsl.controller.requestDto.MemberSaveRequestDto;
import study.querydsl.controller.requestDto.MemberSearchCondition;
import study.querydsl.controller.requestDto.MemberUpdateRequestDto;
import study.querydsl.controller.requestDto.PageRequestDto;
import study.querydsl.controller.responseDto.MemberResponseDto;
import study.querydsl.controller.responseDto.MemberTeamResponseDto;
import study.querydsl.controller.responseDto.PageResponseDto;
import study.querydsl.domain.Member.Member;
import study.querydsl.domain.Member.memberRepository.MemberRepository;

import java.util.function.Function;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class MemberService {
    private final MemberRepository memberRepository;
    // C R U D
    @Transactional
    public void Save(MemberSaveRequestDto memberSaveRequestDto)
    {
        memberRepository.save(memberSaveRequestDto.toEntity());
    }

    public MemberResponseDto Detail(Long memberId)
    {
        return memberRepository.findById(memberId).orElseThrow(() -> new RuntimeException("noEntity")).toDto();
    }

    @Transactional
    public void Update(MemberUpdateRequestDto memberUpdateRequestDto)
    {
        Member updateMember = memberRepository.findById(memberUpdateRequestDto.getId()).orElseThrow(() -> new RuntimeException("noEntity"));
        updateMember.update(memberUpdateRequestDto);
    }



    // MemberTeam Search Feat.
    public PageResponseDto<MemberResponseDto, Member> memberList(PageRequestDto requestDto) {
        Pageable pageable = requestDto.getPageable(Sort.by("id"));
        Page<Member> pages = memberRepository.findAll(pageable);
        Function<Member, MemberResponseDto> function = Member::toDto;
        return new PageResponseDto<>(pages, function);
    }

    public PageResponseDto<MemberTeamResponseDto, Member> memberSearchConditionList
            (PageRequestDto requestDto, MemberSearchCondition memberSearchCondition) {
        Pageable pageable=requestDto.getPageable(Sort.by("id"));

        Page<MemberTeamResponseDto> memberTeamResponseDtos = memberRepository.searchByConditions(pageable, memberSearchCondition);
        return new PageResponseDto<>(memberTeamResponseDtos);
    }

    public PageResponseDto<MemberTeamResponseDto, Member> pagingCntImprove
            (PageRequestDto requestDto, MemberSearchCondition memberSearchCondition) {
        Pageable pageable=requestDto.getPageable(Sort.by("id"));

        Page<MemberTeamResponseDto> memberTeamResponseDtos = memberRepository.pagingCntImprove(pageable, memberSearchCondition);
        return new PageResponseDto<>(memberTeamResponseDtos);
    }


}
