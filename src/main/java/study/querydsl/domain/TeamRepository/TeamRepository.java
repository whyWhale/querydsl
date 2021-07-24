package study.querydsl.domain.TeamRepository;

import org.springframework.data.jpa.repository.JpaRepository;
import study.querydsl.domain.Team;

public interface TeamRepository extends JpaRepository<Team,Long>,TeamRepositoryCustom {
}
