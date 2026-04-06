package Project.Dalmoa.presentation.dto.calculation;

import java.util.Map;

public record ExchangeRateResponse(
        String result,
        String base_code,
        Map<String, Double> conversion_rates
) {
}
