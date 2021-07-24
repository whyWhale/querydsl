package study.querydsl.domain.TeamRepository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import study.querydsl.domain.QTeam;
import study.querydsl.domain.Team;

import java.util.List;
import java.util.Optional;

import static study.querydsl.domain.QTeam.*;
import static study.querydsl.domain.QTeam.team;

@RequiredArgsConstructor
public class TeamRepositoryImpl implements TeamRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<Team> findByName(String name) {
        Team team = queryFactory.selectFrom(QTeam.team).where(QTeam.team.name.eq(name)).fetchOne();
        return Optional.ofNullable(team);
    }
}
