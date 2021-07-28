package study.querydsl.domain.Team.TeamRepository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import study.querydsl.domain.QTeam;
import study.querydsl.domain.Team.Team;

import java.util.Optional;

@RequiredArgsConstructor
public class TeamRepositoryImpl implements TeamRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<Team> findByName(String name) {
        Team team = queryFactory.selectFrom(QTeam.team).where(QTeam.team.name.eq(name)).fetchOne();
        return Optional.ofNullable(team);
    }
}
