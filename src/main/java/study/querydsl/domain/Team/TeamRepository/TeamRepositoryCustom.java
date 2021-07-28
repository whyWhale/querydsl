package study.querydsl.domain.Team.TeamRepository;

import study.querydsl.domain.Team.Team;

import java.util.Optional;

public interface TeamRepositoryCustom {
    Optional<Team> findByName(String name);
}
