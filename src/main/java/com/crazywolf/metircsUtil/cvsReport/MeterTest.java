package com.crazywolf.metircsUtil.cvsReport;

import com.codahale.metrics.CsvReporter;
import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;

import java.io.File;
import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

/**
 * Created by cmdgjw@hotmail.com
 * 2017/3/30 10:29
 */
public class MeterTest {
    public static void main(String[] args) throws InterruptedException {
        MetricRegistry registry = new MetricRegistry();
         CsvReporter reporter = CsvReporter.forRegistry(registry)
                .formatFor(Locale.US)
                .convertRatesTo(TimeUnit.SECONDS)
                .convertDurationsTo(TimeUnit.MILLISECONDS)
                .build(new File("d://data/"));
        reporter.start(1, TimeUnit.SECONDS);

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
