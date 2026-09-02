package Project.Dalmoa.presentation.dto.subscribe.response;

import Project.Dalmoa.domain.subscribe.Currency;
import Project.Dalmoa.domain.subscribe.SubCategory;
import Project.Dalmoa.domain.subscribe.Subscribe;
import Project.Dalmoa.domain.subscribe.Term;

import java.time.LocalDateTime;

public record SubscribeDetailResponse(
        Long id,
        String name,
        Double price,
        Currency currency,
        SubCategory category,
        String customTag,
        Term term,
        LocalDateTime date
) {
    public static SubscribeDetailResponse from(Subscribe s) {
        return new SubscribeDetailResponse(
                s.getId(),
                s.getName(),
                s.getPrice(),
                s.getCurrency(),
                s.getSubCategory(),
                s.getCustomCategoryTag(),
                s.getTerm(),
                s.getDate()
        );
    }
}
