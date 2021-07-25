package study.querydsl.controller.responseDto;

import lombok.Builder;
import lombok.Data;

@Data
public class MemberTeamResponseDto {
    private Long memberId;
    private String username;
    private int age;
    private Long teamId;
    private String teamName;

    @Builder
    public MemberTeamResponseDto(Long memberId, String username, int age, Long teamId, String teamName) {
        this.memberId = memberId;
        this.username = username;
        this.age = age;
        this.teamId = teamId;
        this.teamName = teamName;
    }
}
