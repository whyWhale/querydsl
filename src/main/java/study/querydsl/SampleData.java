package study.querydsl;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import study.querydsl.domain.Member;
import study.querydsl.domain.Team;
import study.querydsl.domain.TeamRepository.TeamRepository;
import study.querydsl.domain.TeamRepository.TeamRepositoryCustom;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

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
