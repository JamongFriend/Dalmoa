package Project.Dalmoa.application;

import Project.Dalmoa.domain.subscribe.Currency;
import Project.Dalmoa.domain.subscribe.SubCategory;
import Project.Dalmoa.domain.subscribe.Subscribe;
import Project.Dalmoa.domain.subscribe.Term;
import Project.Dalmoa.presentation.dto.calculation.ExchangeRateResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
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
            RestClient restClient,
            @Value("${exchange.api.key}") String apiKey,
            @Value("${exchange.api.base-url}") String baseUrl
    ) {
        this.restClient = restClient;
        this.apiKey = apiKey;
        this.baseUrl = baseUrl;
    }

    // 이번 달 기준, 구독 목록의 월 환산 총액을 원화로 합산
    public double totalAmount(List<Subscribe> list) {
        YearMonth targetMonth = YearMonth.now();
        return list.stream()
                .mapToDouble(s -> monthlyKrwAmount(s, targetMonth))
                .sum();
    }

    // 이번 달 기준, 카테고리별 월 환산 금액을 원화로 합산하여 그룹핑
    public Map<SubCategory, Double> calculateGroupedAmount(List<Subscribe> list) {
        YearMonth targetMonth = YearMonth.now();
        return list.stream()
                .collect(Collectors.groupingBy(
                        Subscribe::getSubCategory,
                        Collectors.summingDouble(s -> monthlyKrwAmount(s, targetMonth))
                ));
    }

    // 이번 달 기준, Term(주/월/연)별 월 환산 금액을 원화로 합산하여 그룹핑
    public Map<Term, Double> calculateGroupedAmountByTerm(List<Subscribe> list) {
        YearMonth targetMonth = YearMonth.now();
        return list.stream()
                .collect(Collectors.groupingBy(
                        Subscribe::getTerm,
                        Collectors.summingDouble(s -> monthlyKrwAmount(s, targetMonth))
                ));
    }

    // 구독 하나의 금액을 원화로 변환 (KRW는 그대로, 외화는 환율 적용)
    public double convertToKrw(Subscribe s) {
        if (s.getCurrency() == Currency.KRW) {
            return s.getPrice();
        }
        double rate = getExchangeRate(s.getCurrency());
        return s.getPrice() * rate;
    }

    // 구독 하나가 특정 월에 실제로 지출되는 금액을 원화로 환산
    // - 월간: 매달 결제 금액 그대로
    // - 연간: 연 결제 금액을 12로 나눈 평균값 (모든 달에 균등 반영)
    // - 주간: 결제 요일이 해당 월에 몇 번 돌아오는지(4~5회)를 세어 곱함
    public double monthlyKrwAmount(Subscribe s, YearMonth targetMonth) {
        double base = monthlyBaseAmount(s, targetMonth);
        if (s.getCurrency() == Currency.KRW) {
            return base;
        }
        return base * getExchangeRate(s.getCurrency());
    }

    // 주간 구독, 연간 구독을 월간 구독으로 계산
    private double monthlyBaseAmount(Subscribe s, YearMonth targetMonth) {
        return switch (s.getTerm()) {
            case YEAR -> s.getPrice() / 12.0;
            case WEEK -> s.getPrice() * countWeeklyOccurrencesInMonth(s, targetMonth);
            case MONTH -> s.getPrice();
        };
    }

    // 다음 결제일(요일)부터 시작해 7일 간격으로 반복될 때, 해당 월에 결제가 몇 번 발생하는지 계산
    private long countWeeklyOccurrencesInMonth(Subscribe s, YearMonth targetMonth) {
        LocalDate anchor = s.getDate().toLocalDate();
        LocalDate monthStart = targetMonth.atDay(1);
        LocalDate monthEnd = targetMonth.atEndOfMonth();

        LocalDate rangeStart = anchor.isAfter(monthStart) ? anchor : monthStart;
        if (rangeStart.isAfter(monthEnd)) {
            return 0;
        }

        DayOfWeek paymentDayOfWeek = anchor.getDayOfWeek();
        int shift = (paymentDayOfWeek.getValue() - rangeStart.getDayOfWeek().getValue() + 7) % 7;
        LocalDate firstOccurrence = rangeStart.plusDays(shift);

        long count = 0;
        for (LocalDate date = firstOccurrence; !date.isAfter(monthEnd); date = date.plusDays(7)) {
            count++;
        }
        return count;
    }

    // 외부 환율 API에서 해당 통화의 KRW 환율을 조회 (캐시 적용)
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
