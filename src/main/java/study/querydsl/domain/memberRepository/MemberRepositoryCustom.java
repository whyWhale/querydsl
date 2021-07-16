package study.querydsl.domain.memberRepository;

import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import study.querydsl.domain.Member;

import java.util.Optional;

public interface MemberRepositoryCustom {
    Optional<Member> findByName(Long memberId);
}
