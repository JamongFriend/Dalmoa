package Project.Dalmoa.application;

import Project.Dalmoa.domain.subscribe.Currency;
import Project.Dalmoa.domain.subscribe.SubCategory;
import Project.Dalmoa.domain.subscribe.Subscribe;
import Project.Dalmoa.presentation.dto.calculation.ExchangeRateResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CalculationService {
    private final RestClient restClient;
    private final String apiKey;
    private final String baseUrl;

    public CalculationService(
            @Value("${exchange.api.key}") String apiKey,
            @Value("${exchange.api.base-url}") String baseUrl
    ) {
        this.restClient = RestClient.create();
        this.apiKey = apiKey;
        this.baseUrl = baseUrl;
    }

    public double totalAmount(List<Subscribe> list) {
        return list.stream()
                .mapToDouble(this::convertToKrw)
                .sum();
    }

    public Map<SubCategory, Double> calculateGroupedAmount(List<Subscribe> list) {
        return list.stream()
                .collect(Collectors.groupingBy(
                        Subscribe::getSubCategory,
                        Collectors.summingDouble(this::convertToKrw)
                ));
    }

    private double convertToKrw(Subscribe s) {
        if (s.getCurrency() == Currency.KRW) {
            return s.getPrice();
        }
        double rate = getExchangeRate(s.getCurrency());
        return s.getPrice() * rate;
    }

    @Cacheable(value = "exchangeRate", key = "#currency")
    public double getExchangeRate(Currency currency) {
        if (currency == Currency.KRW) return 1.0;

        try {
            log.info("Fetching exchange rate for {} from external API", currency);
            
            // Base currency를 해당 통화(예: USD)로 설정하여 KRW 환율을 가져옴
            ExchangeRateResponse response = restClient.get()
                    .uri(baseUrl + apiKey + "/latest/" + currency.name())
                    .retrieve()
                    .body(ExchangeRateResponse.class);

            if (response == null || !"success".equals(response.result()) || response.conversion_rates() == null) {
                log.error("Failed to fetch exchange rate for {}. Response: {}", currency, response);
                throw new RuntimeException("환율 정보를 가져오지 못했습니다.");
            }

            Double krwRate = response.conversion_rates().get("KRW");
            if (krwRate == null) {
                throw new RuntimeException(currency.name() + "에 대한 KRW 환율 정보가 없습니다.");
            }

            return krwRate;
        } catch (Exception e) {
            log.warn("API Error occurred: {}. Falling back to default rate (1300.0 for USD).", e.getMessage());
            // API 오류 발생 시 기본값 제공 (필요 시 조정)
            if (currency == Currency.USD) return 1300.0;
            return 1.0;
        }
    }
}
