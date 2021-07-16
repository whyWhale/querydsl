package study.querydsl.domain.memberRepository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;
import study.querydsl.domain.Member;
import study.querydsl.domain.QMember;

import javax.persistence.EntityManager;
import java.util.Optional;

@RequiredArgsConstructor
public class MemberRepositoryImpl  implements MemberRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Optional<Member> findByName(Long memberId) {
        Member member = jpaQueryFactory.selectFrom(QMember.member)
                .where(QMember.member.id.eq(memberId))
                .fetchOne();
        return Optional.ofNullable(member);
    }
}
