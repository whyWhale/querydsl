package study.querydsl.domain.Member;

import lombok.*;
import org.hibernate.annotations.DynamicUpdate;
import study.querydsl.controller.requestDto.MemberUpdateRequestDto;
import study.querydsl.controller.responseDto.MemberResponseDto;
import study.querydsl.controller.responseDto.MemberTeamResponseDto;
import study.querydsl.domain.BaseEntity;
import study.querydsl.domain.Team.Team;

import javax.persistence.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicUpdate
@ToString(exclude = {"team"})
@Entity
public class Member extends BaseEntity {
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

    public void update(MemberUpdateRequestDto memberUpdateRequestDto){
        this.username=memberUpdateRequestDto.getUsername();
        this.age=memberUpdateRequestDto.getAge();
    }
}
