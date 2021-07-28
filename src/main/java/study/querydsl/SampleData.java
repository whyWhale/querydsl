package study.querydsl;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import study.querydsl.domain.Member.Member;
import study.querydsl.domain.Team.Team;
import study.querydsl.domain.Team.TeamRepository.TeamRepository;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;

@Profile("local")
@Component
@RequiredArgsConstructor
public class SampleData {
    private final Init init;

    @PostConstruct
    public void Init() {
        init.init();
    }

    @Component
    @RequiredArgsConstructor
    static class Init {

        private final EntityManager em;
        private final TeamRepository teamRepository;

        @Transactional
        public void init() {
            for (int i = 0; i < 5; i++) {
                Team team = Team.builder().name(String.valueOf(i)).build();
                em.persist(team);
            }
            for (int i = 0; i < 100; i++) {
                Member mem = Member.builder().username(String.valueOf(i)).age(i).build();

                mem.setTeam(teamRepository.findByName(String.valueOf(i % 5)).orElseThrow(RuntimeException::new));
                em.persist(mem);
            }
        }
    }
}
