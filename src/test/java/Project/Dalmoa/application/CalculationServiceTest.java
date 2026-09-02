package Project.Dalmoa.application;

import Project.Dalmoa.domain.subscribe.Currency;
import Project.Dalmoa.domain.subscribe.SubCategory;
import Project.Dalmoa.domain.subscribe.Subscribe;
import Project.Dalmoa.domain.subscribe.Term;
import Project.Dalmoa.presentation.dto.calculation.ExchangeRateResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestClient;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CalculationServiceTest {
    private static final String BASE_URL = "http://Test.com/";
    private static final String API_KEY = "testApiKey";

    private RestClient restClient;
    private CalculationService calculationService;

    @BeforeEach
    void setUp() {
        restClient = mock(RestClient.class, RETURNS_DEEP_STUBS);
        calculationService = new CalculationService(restClient, API_KEY, BASE_URL);
    }

    @Test
    void totalAmount_여러구독의_합계를_반환() {
        //given
        Subscribe monthlySubscribe = Subscribe.createSubscribe(
                null, "넷플릭스", 17000.0, Currency.KRW,
                LocalDateTime.of(2026, 8, 1, 0, 0),
                SubCategory.OTT, null, Term.MONTH
        );
        Subscribe yearlySubscribe = Subscribe.createSubscribe(
                null, "포켓몬 챔피언스 구독", 84000.0, Currency.KRW,
                LocalDateTime.of(2026, 4, 1, 0, 0),
                SubCategory.GAME, null, Term.YEAR
        );

        //when
        double result = calculationService.totalAmount(List.of(monthlySubscribe, yearlySubscribe));

        //then
        assertThat(result).isEqualTo(17000.0 + 7000.0);
    }

    @Test
    void calculateGroupedAmountByTerm_구독을_Term별로_합산하여_그룹핑() {
        //given
        Subscribe monthlySubscribe = Subscribe.createSubscribe(
                null,
                "넷플릭스",
                17000.0,
                Currency.KRW,
                LocalDateTime.of(2026, 8, 1, 0, 0),
                SubCategory.OTT,
                null,
                Term.MONTH
        );
        Subscribe yearlySubscribe = Subscribe.createSubscribe(
                null,
                "포켓몬 챔피언스 구독",
                84000.0,
                Currency.KRW,
                LocalDateTime.of(2026, 4, 1, 0, 0),
                SubCategory.GAME,
                null,
                Term.YEAR
        );

        //when
        Map<Term, Double> result = calculationService.calculateGroupedAmountByTerm(
                List.of(monthlySubscribe, yearlySubscribe)
        );

        //then
        assertThat(result)
                .containsEntry(Term.MONTH, 17000.0)
                .containsEntry(Term.YEAR, 7000.0);
    }

    @Test
    void monthlyKrwAmount_연간_구독은_12로_나눈값_반환() {
        Subscribe subscribe = Subscribe.createSubscribe(
                null,
                "포켓몬 챔피언스 구독",
                70000.0,
                Currency.KRW,
                LocalDateTime.of(2026, 4, 1, 0, 0),
                SubCategory.GAME,
                null,
                Term.YEAR
        );
        YearMonth targetMonth = YearMonth.of(2026, 4);

        //when
        double result = calculationService.monthlyKrwAmount(subscribe, targetMonth);

        //then
        assertThat(result).isEqualTo((70000.0 / 12));
    }

    @Test
    void monthlyKrwAmount_주간_구독은_해당월_결제횟수만큼_반환() {
        //given
        Subscribe subscribe = Subscribe.createSubscribe(
                null,
                "밀키트 정기배송",
                10000.0,
                Currency.KRW,
                LocalDateTime.of(2026, 8, 3, 0, 0),   // 2026-08-03 = 월요일
                SubCategory.ETC,
                null,
                Term.WEEK
        );
        YearMonth targetMonth = YearMonth.of(2026, 8);   // 8월 중 월요일: 3, 10, 17, 24, 31 → 5번

        //when
        double result = calculationService.monthlyKrwAmount(subscribe, targetMonth);

        //then
        assertThat(result).isEqualTo(10000.0 * 5);
    }

    @Test
    void monthlyKrwAmount_주간_구독은_시작전_달에는_0_반환() {
        //given
        Subscribe subscribe = Subscribe.createSubscribe(
                null,
                "연말 한정 밀키트",
                10000.0,
                Currency.KRW,
                LocalDateTime.of(2026, 12, 1, 0, 0),   // 구독 시작일이 12월
                SubCategory.ETC,
                null,
                Term.WEEK
        );
        YearMonth targetMonth = YearMonth.of(2026, 8);   // 계산하려는 달은 그보다 이전인 8월

        //when
        double result = calculationService.monthlyKrwAmount(subscribe, targetMonth);

        //then
        assertThat(result).isEqualTo(0.0);
    }

    @Test
    void monthlyKrwAmount_월간_구독은_가격그대로_반환() {
        //given
        Subscribe subscribe = Subscribe.createSubscribe(
                null,
                "넷플릭스",
                17000.0,
                Currency.KRW,
                LocalDateTime.of(2026, 8, 1, 0, 0),
                SubCategory.OTT,
                null,
                Term.MONTH
        );
        YearMonth targetMonth = YearMonth.of(2026, 8);

        //when
        double result = calculationService.monthlyKrwAmount(subscribe, targetMonth);

        //then
        assertThat(result).isEqualTo((17000.0));
    }

    @Test
    void getExchangeRate_외화는_외부API에서_받은_환율을_반환() {
        //given
        ExchangeRateResponse response = new ExchangeRateResponse(
                "success", "USD", Map.of("KRW", 1350.0)
        );
        when(restClient.get()
                .uri(BASE_URL + API_KEY + "/latest/" + Currency.USD.name())
                .retrieve()
                .body(ExchangeRateResponse.class)
        ).thenReturn(response);

        //when
        double result = calculationService.getExchangeRate(Currency.USD);

        //then
        assertThat(result).isEqualTo(1350.0);
    }

    @Test
    void getExchangeRate_API_실패시_USD는_기본값_1300을_반환() {
        //given
        when(restClient.get()
                .uri(BASE_URL + API_KEY + "/latest/" + Currency.USD.name())
                .retrieve()
                .body(ExchangeRateResponse.class)
        ).thenReturn(null);

        //when
        double result = calculationService.getExchangeRate(Currency.USD);

        //then
        assertThat(result).isEqualTo(1300.0);
    }

    @Test
    void getExchangeRate_KRW는_API호출없이_1을_반환() {
        //when
        double result = calculationService.getExchangeRate(Currency.KRW);

        //then
        assertThat(result).isEqualTo(1.0);
    }
}