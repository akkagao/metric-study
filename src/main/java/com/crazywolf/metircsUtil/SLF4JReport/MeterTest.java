package com.crazywolf.metircsUtil.SLF4JReport;

import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Slf4jReporter;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

/**
 * Created by cmdgjw@hotmail.com
 * 2017/3/30 10:29
 */
public class MeterTest {
    public static void main(String[] args) throws InterruptedException {
        MetricRegistry registry = new MetricRegistry();
        Slf4jReporter reporter = Slf4jReporter.forRegistry(registry)
                .outputTo(LoggerFactory.getLogger(MeterTest.class))
                .convertRatesTo(TimeUnit.SECONDS)
                .convertDurationsTo(TimeUnit.MILLISECONDS)
                .build();
        reporter.start(1, TimeUnit.MINUTES);

        Meter meter = registry.meter(MetricRegistry.name(MeterTest.class, "request", "tps"));

        while (true) {
            request(meter);
            Thread.sleep(1000L);
        }
    }

    private static void request(Meter meter) {
        int flag = ThreadLocalRandom.current().nextInt(100);
        System.out.println(flag);
        for (int i = 0; i < flag; i++) {
            meter.mark();
        }
    }
}
