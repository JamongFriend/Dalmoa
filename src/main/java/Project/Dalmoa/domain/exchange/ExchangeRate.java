package Project.Dalmoa.domain.exchange;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ExchangeRate {

    @Id
    private String currencyCode; // e.g., KRW, JPY, EUR

    private Double rate;

    private String baseCurrency; // e.g., USD

    private LocalDateTime lastUpdated;

    public ExchangeRate(String currencyCode, Double rate, String baseCurrency, LocalDateTime lastUpdated) {
        this.currencyCode = currencyCode;
        this.rate = rate;
        this.baseCurrency = baseCurrency;
        this.lastUpdated = lastUpdated;
    }

    public void updateRate(Double rate, LocalDateTime lastUpdated) {
        this.rate = rate;
        this.lastUpdated = lastUpdated;
    }
}
