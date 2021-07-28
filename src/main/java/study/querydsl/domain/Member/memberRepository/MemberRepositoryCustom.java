package study.querydsl.domain.Member.memberRepository;

import com.querydsl.core.QueryResults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import study.querydsl.controller.requestDto.MemberSearchCondition;
import study.querydsl.controller.responseDto.MemberTeamResponseDto;
import study.querydsl.domain.Member.Member;

import java.util.List;
import java.util.Optional;

public interface MemberRepositoryCustom {
    Optional<Member> findByName(Long memberId);

    List<MemberTeamResponseDto> searchByBuilder(Pageable pageable, MemberSearchCondition memberSearchCondition);

    QueryResults<MemberTeamResponseDto> searchByBuilderQueryResults(Pageable pageable, MemberSearchCondition memberSearchCondition);

    Page<MemberTeamResponseDto> searchByConditions(Pageable pageable, MemberSearchCondition memberSearchCondition);

}
