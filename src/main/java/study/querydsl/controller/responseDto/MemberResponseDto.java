package study.querydsl.controller.responseDto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
@Getter
public class MemberResponseDto {
    private String name;
    private int age;


    @QueryProjection
    public MemberResponseDto(String name, int age) {
        this.name = name;
        this.age = age;
    }


}
