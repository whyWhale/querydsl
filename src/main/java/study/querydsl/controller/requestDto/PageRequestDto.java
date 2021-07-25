package study.querydsl.controller.requestDto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;


@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Data
public class PageRequestDto {

    private Integer page;
    private Integer size;

    @Builder
    public PageRequestDto(Integer page, Integer size) {
        if ((page == null || page<1) || (size == null|| size<1)) {
            this.page = 1;
            this.size = 10;
        } else {

            this.page=page;
            this.size=size;
        }
    }

    public Pageable getPageable(Sort sort) {
        return PageRequest.of(page - 1, size, sort);
    }
}
