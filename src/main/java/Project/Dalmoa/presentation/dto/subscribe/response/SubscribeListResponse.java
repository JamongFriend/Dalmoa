package Project.Dalmoa.presentation.dto.subscribe.response;

import Project.Dalmoa.domain.subscribe.Currency;
import Project.Dalmoa.domain.subscribe.SubCategory;
import Project.Dalmoa.domain.subscribe.Subscribe;
import Project.Dalmoa.domain.subscribe.Term;

import java.time.LocalDateTime;

public record SubscribeListResponse(
        Long id,
        String name,
        SubCategory category,
        String customCategoryTag,
        LocalDateTime date,
        Double price,
        Currency currency,
        Term term,
        Double convertedPriceKrw
) {
    public static SubscribeListResponse from(Subscribe s, double convertedPriceKrw) {
        return new SubscribeListResponse(
                s.getId(), s.getName(), s.getSubCategory(),
                s.getCustomCategoryTag(),
                s.getDate(), s.getPrice(), s.getCurrency(),
                s.getTerm(),
                convertedPriceKrw
        );
    }
}
