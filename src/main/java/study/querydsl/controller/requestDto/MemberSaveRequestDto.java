package study.querydsl.controller.requestDto;

import lombok.Builder;
import lombok.Data;
import study.querydsl.domain.Member.Member;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;

@Data
public class MemberSaveRequestDto {
    @NotEmpty(message = "이름을 입력해주세요.")
    private String username;
    @Min(value = 1000,message = "100살 미만까지 입니다.")
    private Integer age;

    @Builder
    public MemberSaveRequestDto(String username, Integer age) {
        this.username = username;
        this.age = age;
    }

    public Member toEntity ()
    {
        return Member.builder().username(this.username).age(this.age).build();
    }
}
