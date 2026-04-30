package Project.Dalmoa.application.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Map;

@Getter
@NoArgsConstructor
public class ExchangeRateResponse {
    private String result;
    private String base_code;
    private Map<String, Double> conversion_rates;
}
