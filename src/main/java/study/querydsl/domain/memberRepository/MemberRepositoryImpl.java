package study.querydsl.domain.memberRepository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import org.springframework.util.StringUtils;
import study.querydsl.controller.requestDto.MemberSearchCondition;
import study.querydsl.controller.responseDto.MemberTeamResponseDto;
import study.querydsl.domain.Member;
import study.querydsl.domain.QMember;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

import static org.springframework.util.StringUtils.hasText;
import static study.querydsl.domain.QMember.member;
import static study.querydsl.domain.QTeam.team;

@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepositoryCustom {
    private final EntityManager em;
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Optional<Member> findByName(Long memberId) {
        Member member = jpaQueryFactory.selectFrom(QMember.member)
                .where(QMember.member.id.eq(memberId))
                .fetchOne();
        return Optional.ofNullable(member);
    }

    @Override
    public List<MemberTeamResponseDto> searchByBuilder(Pageable pageable, MemberSearchCondition memberSearchCondition) {
        BooleanBuilder builder = new BooleanBuilder();
        if (hasText(memberSearchCondition.getUsername())) {
            builder.and(member.username.eq(memberSearchCondition.getUsername()));
        }
        if (hasText(memberSearchCondition.getTeamName())) {
            builder.and(team.name.eq(memberSearchCondition.getTeamName()));
        }
        if (memberSearchCondition.getAgeLoe() != null) {
            builder.and(member.age.goe(memberSearchCondition.getAgeGoe()));
        }
        if (memberSearchCondition.getAgeLoe() != null) {
            builder.and(member.age.loe(memberSearchCondition.getAgeLoe()));
        }

        return jpaQueryFactory.select(Projections.constructor(MemberTeamResponseDto.class,
                member.id.as("memberId"),
                member.username,
                member.age,
                team.id.as("teamId"),
                team.name.as("teamName")
        )).from(member)
                .leftJoin(member.team, team).where(builder)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
    }

    public QueryResults<MemberTeamResponseDto> searchByBuilderQueryResults(Pageable pageable, MemberSearchCondition memberSearchCondition) {
        return jpaQueryFactory.select(Projections.constructor(MemberTeamResponseDto.class,
                member.id.as("memberId"),
                member.username,
                member.age,
                team.id.as("teamId"),
                team.name.as("teamName")
        )).from(member)
                .leftJoin(member.team, team).where(
                        usernameEq(memberSearchCondition.getUsername()),
                        teamNameEq(memberSearchCondition.getTeamName()),
                       betweenAge(memberSearchCondition.getAgeLoe(), memberSearchCondition.getAgeGoe())
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();
    }

    private BooleanExpression betweenAge(Integer loe,Integer goe)
    {
        return ageGoe(goe).and(ageLoe(loe));
    }

    private BooleanExpression usernameEq(String username) {
        return StringUtils.hasText(username) ? member.username.eq(username) : null;
    }

    private BooleanExpression teamNameEq(String teamName) {
        return StringUtils.hasText(teamName) ? team.name.eq(teamName) : null;
    }

    private BooleanExpression ageGoe(Integer age) {
        return age != null ? member.age.goe(age) : null;
    }

    private BooleanExpression ageLoe(Integer age) {
        return age != null ? member.age.loe(age) : null;
    }


}
