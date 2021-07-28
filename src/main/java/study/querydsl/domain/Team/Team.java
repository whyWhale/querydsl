package study.querydsl.domain.Team;

import lombok.*;
import study.querydsl.domain.BaseEntity;
import study.querydsl.domain.Member.Member;

import javax.persistence.*;
import java.util.LinkedList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(exclude = {"memberList"})
@Entity
public class Team extends BaseEntity {

    @Id
    @GeneratedValue
    @Column(name = "team_id")
    private Long id;

    private String name;

    @OneToMany(mappedBy = "team")
    List<Member> memberList=new LinkedList<>();

    @Builder
    public Team(String name) {
        this.name = name;
    }
}
