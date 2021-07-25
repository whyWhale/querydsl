package study.querydsl.domain.memberRepository;

import com.querydsl.core.QueryResults;
import com.querydsl.core.Tuple;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import study.querydsl.controller.requestDto.MemberSearchCondition;
import study.querydsl.controller.responseDto.MemberTeamResponseDto;
import study.querydsl.domain.Member;

import java.util.List;
import java.util.Optional;

public interface MemberRepositoryCustom {
    Optional<Member> findByName(Long memberId);

    List<MemberTeamResponseDto> searchByBuilder(Pageable pageable, MemberSearchCondition memberSearchCondition);

    QueryResults<MemberTeamResponseDto> searchByBuilderQueryResults(Pageable pageable, MemberSearchCondition memberSearchCondition);

}
