package study.querydsl.domain;

import lombok.*;

import javax.persistence.*;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(exclude = {"team"})
@Entity
public class Member {
    @Id
    @GeneratedValue
    @Column(name = "member_id")
    private Long id;

    private String username;

    private int age;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;

    @Builder
    public Member(String username, int age) {
        this.username = username;
        this.age = age;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public void changeTeam(Team team)
    {
        this.team=team;
        team.getMemberList().add(this);
    }
}
