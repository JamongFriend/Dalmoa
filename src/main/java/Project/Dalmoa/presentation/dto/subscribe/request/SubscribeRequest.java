package Project.Dalmoa.presentation.dto.subscribe.request;

import Project.Dalmoa.domain.subscribe.Currency;
import Project.Dalmoa.domain.subscribe.SubCategory;
import Project.Dalmoa.domain.subscribe.Term;

import java.time.LocalDate;

public record SubscribeRequest(
        String name,
        Double price,
        Currency currency,
        LocalDate date,
        SubCategory subCategory,
        String customCategoryTag,
        Term term
) {
}
