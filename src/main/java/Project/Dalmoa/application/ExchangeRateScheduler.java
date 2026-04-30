package Project.Dalmoa.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ExchangeRateScheduler {

    private final ExchangeRateService exchangeRateService;

    // Update every 12 hours (43,200,000 milliseconds)
    @Scheduled(fixedDelay = 43200000)
    public void scheduleExchangeRateUpdate() {
        log.info("Starting scheduled exchange rate update...");
        exchangeRateService.updateExchangeRates();
    }

    // Run once when the application is ready
    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        log.info("Application ready. Performing initial exchange rate update...");
        exchangeRateService.updateExchangeRates();
    }
}
