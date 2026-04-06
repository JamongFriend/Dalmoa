package Project.Dalmoa.presentation.dto.subscribe.response;

import Project.Dalmoa.domain.subscribe.Currency;
import Project.Dalmoa.domain.subscribe.SubType;
import Project.Dalmoa.domain.subscribe.Subscribe;

import java.time.LocalDateTime;

public record SubscribeListResponse(
        Long id,
        String name,
        SubType type,
        LocalDateTime date,
        Double price,
        Currency currency
) {
    public static SubscribeListResponse from(Subscribe s) {
        return new SubscribeListResponse(
                s.getId(), s.getName(), s.getSubType(),
                s.getDate(), s.getPrice(), s.getCurrency()
        );
    }
}
