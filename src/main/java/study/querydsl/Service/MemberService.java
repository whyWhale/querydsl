package study.querydsl.Service;

import com.querydsl.core.QueryResults;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import study.querydsl.controller.requestDto.MemberSearchCondition;
import study.querydsl.controller.requestDto.PageRequestDto;
import study.querydsl.controller.responseDto.MemberResponseDto;
import study.querydsl.controller.responseDto.MemberTeamResponseDto;
import study.querydsl.controller.responseDto.PageResponseDto;
import study.querydsl.domain.Member;
import study.querydsl.domain.memberRepository.MemberRepository;

import java.util.function.Function;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class MemberService {
    private final MemberRepository memberRepository;

    @Transactional
    public PageResponseDto<MemberResponseDto, Member> memberList(PageRequestDto requestDto) {
        Pageable pageable = requestDto.getPageable(Sort.by("id"));
        Page<Member> pages = memberRepository.findAll(pageable);
        Function<Member, MemberResponseDto> function = Member::toDto;
        return new PageResponseDto<>(pages, function);
    }

    @Transactional
    public PageResponseDto<MemberTeamResponseDto, Member> memberSearchConditionList
            (PageRequestDto requestDto, MemberSearchCondition memberSearchCondition) {
        Pageable pageable=requestDto.getPageable(Sort.by("id"));
        QueryResults<MemberTeamResponseDto> QueryResults
                = memberRepository.searchByBuilderQueryResults(pageable, memberSearchCondition);
        return new PageResponseDto<MemberTeamResponseDto, Member>(QueryResults);
    }

}
