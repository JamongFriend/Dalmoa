package Project.Dalmoa.application;

import Project.Dalmoa.domain.subscribe.Currency;
import Project.Dalmoa.domain.subscribe.SubType;
import Project.Dalmoa.domain.subscribe.Subscribe;
import Project.Dalmoa.presentation.dto.calculation.ExchangeRateResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CalculationService {
    private final RestClient restClient = RestClient.create();

    private final String API_KEY = "YOUR_API_KEY";
    private final String BASE_URL = "https://v6.exchangerate-api.com/v6/";

    public double totalAmount(List<Subscribe> list) {
        Double usdToKrwRate = getExchangeRate(Currency.USD);

        return list.stream()
                .mapToDouble(s -> {
                    if (s.getCurrency() == Currency.USD) {
                        return s.getPrice() * usdToKrwRate;
                    }
                    return s.getPrice();
                })
                .sum();
    }

    public Map<SubType, Double> calculateGroupedAmount(List<Subscribe> list) {
        double usdToKrwRate = getExchangeRate(Currency.USD);

        return list.stream()
                .collect(Collectors.groupingBy(
                        Subscribe::getSubType,
                        Collectors.summingDouble(s -> {
                            if (s.getCurrency() == Currency.USD) return s.getPrice() * usdToKrwRate;
                            return s.getPrice();
                        })
                ));
    }

    @Cacheable(value = "exchangeRate", key = "#currency")
    private double getExchangeRate(Currency currency) {
        if (currency == Currency.KRW) return 1.0;

        ExchangeRateResponse response = restClient.get()
                .uri(BASE_URL + API_KEY + "/latest/USD")
                .retrieve()
                .body(ExchangeRateResponse.class);

        if (response == null || !"success".equals(response.result())) {
            throw new RuntimeException("환율 정보를 가져오지 못했습니다.");
        }

        return response.conversion_rates().get("KRW");
    }
}
