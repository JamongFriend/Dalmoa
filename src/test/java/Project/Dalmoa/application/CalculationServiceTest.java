package Project.Dalmoa.application;

import Project.Dalmoa.domain.subscribe.Currency;
import Project.Dalmoa.domain.subscribe.SubCategory;
import Project.Dalmoa.domain.subscribe.Subscribe;
import Project.Dalmoa.domain.subscribe.Term;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.YearMonth;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ExtendWith(MockitoExtension.class)
class CalculationServiceTest {
    private CalculationService calculationService;

    @BeforeEach
    void setUp() {
        calculationService = new CalculationService("testApiKey", "http://Test.com/");
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
}