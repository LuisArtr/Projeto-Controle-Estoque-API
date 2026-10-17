package com.luisartr.bakery.stock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ExpiredBatchJob {

    private static final Logger log = LoggerFactory.getLogger(ExpiredBatchJob.class);

    private final StockService service;

    public ExpiredBatchJob(StockService service) {
        this.service = service;
    }

    @Scheduled(cron = "0 0 2 * * *")
    public void run() {
        int count = service.processExpiredBatches();
        if (count > 0) {
            log.info("Lotes vencidos processados: {}", count);
        }
    }
}