package study.querydsl.controller.requestDto;

import lombok.Builder;
import lombok.Data;
import study.querydsl.domain.Member.Member;

import javax.validation.constraints.*;

@Data
public class MemberSaveRequestDto {
    @NotBlank(message = "이름을 입력해주세요!")
    @Size(min = 2, max = 10, message = "이름은 1 ~ 10자 이여야 합니다!")
    private String username;

    @NotNull(message = "나이를 입력해주세요!")
    @Max(value = 25, message = "25세 이하만 가능합니다.")
    @Min(value = 18, message = "18살 이상만 가능합니다.")
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
