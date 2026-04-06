package Project.Dalmoa.presentation.dto.subscribe.request;

import Project.Dalmoa.domain.subscribe.SubType;

import java.time.LocalDate;

public record SubscribeRequest(
        String name,
        Double price,
        LocalDate date,
        SubType type,
        String customTypeTag
) {
}
