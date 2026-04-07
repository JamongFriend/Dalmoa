package Project.Dalmoa.presentation.dto.subscribe.response;

import Project.Dalmoa.domain.subscribe.Currency;
import Project.Dalmoa.domain.subscribe.SubCategory;
import Project.Dalmoa.domain.subscribe.Subscribe;

import java.time.LocalDateTime;

public record SubscribeListResponse(
        Long id,
        String name,
        SubCategory category,
        LocalDateTime date,
        Double price,
        Currency currency
) {
    public static SubscribeListResponse from(Subscribe s) {
        return new SubscribeListResponse(
                s.getId(), s.getName(), s.getSubCategory(),
                s.getDate(), s.getPrice(), s.getCurrency()
        );
    }
}
