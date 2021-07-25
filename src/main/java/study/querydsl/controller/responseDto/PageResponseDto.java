package study.querydsl.controller.responseDto;

import com.querydsl.core.QueryResults;
import lombok.Data;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Data
public class PageResponseDto<DTO,ENTITY>{
    private List<DTO> dtoList;

    private long totalPage;

    private long page;

    private long size;

    private int start,end;

    private boolean prev,next;

    private List<Integer> pageList;

    public PageResponseDto(Page<ENTITY> entities, Function<ENTITY,DTO> function)
    {
        dtoList=entities.stream().map(function).collect(Collectors.toList());

        totalPage= entities.getTotalPages();

        Pageable pageable = entities.getPageable();
        this.page=pageable.getPageNumber()+1;
        this.size=pageable.getPageSize();

        long tempEnd=(long)(Math.ceil(page/10.0))*10;
        start=(int)tempEnd-9;

        prev=start>1;

        end= (int) (totalPage>tempEnd ? tempEnd:totalPage);

        next=totalPage>tempEnd;
        pageList= IntStream.rangeClosed(start,end).boxed().collect(Collectors.toList());
    }

    public PageResponseDto(QueryResults<DTO> entities)
    {
        dtoList= entities.getResults();

        totalPage=  entities.getTotal();

        this.page= (entities.getOffset()/entities.getLimit()+1);
        this.size=  entities.getLimit();

        int tempEnd=(int)(Math.ceil(page/10.0))*10;
        start=tempEnd-9;

        prev=start>1;

        end= totalPage>tempEnd ? tempEnd: (int) totalPage;

        next=totalPage>tempEnd;
        pageList= IntStream.rangeClosed(start,end).boxed().collect(Collectors.toList());
    }

}
