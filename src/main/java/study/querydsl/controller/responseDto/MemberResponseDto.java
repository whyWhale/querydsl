package study.querydsl.controller.responseDto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
@Getter
public class MemberResponseDto {
    private Long id;
    private String name;
    private int age;
    private String createdDate;

    @QueryProjection
    public MemberResponseDto(String name, int age) {
        this.name = name;
        this.age = age;
    }

    @Builder
    public MemberResponseDto(Long id,String name, int age, LocalDateTime createdDate) {
        this.id=id;
        this.name = name;
        this.age = age;
        this.createdDate = formatDate(createdDate);
    }

    public String formatDate(LocalDateTime localDateTime)
    {
        return localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd-HH:ss"));
    }
}
