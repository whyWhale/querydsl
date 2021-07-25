package study.querydsl.domain;

import lombok.*;
import study.querydsl.controller.responseDto.MemberResponseDto;
import study.querydsl.controller.responseDto.MemberTeamResponseDto;

import javax.persistence.*;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(exclude = {"team"})
@Entity
public class Member extends BaseEntity{
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

    public MemberResponseDto toDto()
    {
        return new MemberResponseDto(id,username,age,getCreatedDate());
    }

    public MemberTeamResponseDto toDto2()
    {
        return MemberTeamResponseDto.builder()
                .memberId(this.id)
                .teamId(this.team.getId())
                .teamName(this.team.getName())
                .age(this.age)
                .username(this.username)
                .build();

    }

}
