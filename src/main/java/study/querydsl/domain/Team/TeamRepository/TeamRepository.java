package study.querydsl.domain.Team.TeamRepository;

import org.springframework.data.jpa.repository.JpaRepository;
import study.querydsl.domain.Team.Team;

public interface TeamRepository extends JpaRepository<Team,Long>,TeamRepositoryCustom {
}
