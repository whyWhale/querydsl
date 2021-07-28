package study.querydsl.domain.Member.memberRepository;

import org.springframework.data.jpa.repository.JpaRepository;
import study.querydsl.domain.Member.Member;

public interface MemberRepository extends JpaRepository<Member,Long> ,MemberRepositoryCustom{
}
