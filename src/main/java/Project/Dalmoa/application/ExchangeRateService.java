package Project.Dalmoa.application;

import Project.Dalmoa.application.dto.ExchangeRateResponse;
import Project.Dalmoa.domain.exchange.ExchangeRate;
import Project.Dalmoa.domain.exchange.ExchangeRateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ExchangeRateService {

    private final ExchangeRateRepository exchangeRateRepository;
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${exchange.api.key}")
    private String apiKey;

    @Value("${exchange.api.base-url}")
    private String baseUrl;

    @Transactional
    public void updateExchangeRates() {
        String url = baseUrl + apiKey + "/latest/USD";
        try {
            ExchangeRateResponse response = restTemplate.getForObject(url, ExchangeRateResponse.class);
            if (response != null && "success".equals(response.getResult())) {
                Map<String, Double> rates = response.getConversion_rates();
                String baseCode = response.getBase_code();
                LocalDateTime now = LocalDateTime.now();

                rates.forEach((code, rate) -> {
                    exchangeRateRepository.findById(code)
                            .ifPresentOrElse(
                                    existing -> existing.updateRate(rate, now),
                                    () -> exchangeRateRepository.save(new ExchangeRate(code, rate, baseCode, now))
                            );
                });
                log.info("Exchange rates updated successfully at {}", now);
            }
        } catch (Exception e) {
            log.error("Failed to update exchange rates: {}", e.getMessage());
        }
    }

    public List<ExchangeRate> getAllExchangeRates() {
        return exchangeRateRepository.findAll();
    }
}
