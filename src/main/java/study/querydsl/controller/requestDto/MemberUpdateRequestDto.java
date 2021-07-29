package study.querydsl.controller.requestDto;

import lombok.Builder;
import lombok.Data;

@Data
public class MemberUpdateRequestDto {
    private Long id;
    private String username;
    private Integer age;

    @Builder
    public MemberUpdateRequestDto(Long memberId, String username, Integer age) {
        this.id = memberId;
        this.username = username;
        this.age = age;
    }
}
