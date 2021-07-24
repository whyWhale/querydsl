package study.querydsl.domain.TeamRepository;

import study.querydsl.domain.Team;

import java.util.Optional;

public interface TeamRepositoryCustom {
    Optional<Team> findByName(String name);
}
