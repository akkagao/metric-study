package com.crazywolf.metircsUtil.consoleReporter;

import com.codahale.metrics.ConsoleReporter;
import com.codahale.metrics.ExponentiallyDecayingReservoir;
import com.codahale.metrics.Histogram;
import com.codahale.metrics.MetricRegistry;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

/**
 * Created by cmdgjw@hotmail.com
 * 2017/3/30 10:41
 */
public class HistogramsTest {
    public static void main(String[] args) throws InterruptedException {
        MetricRegistry registry = new MetricRegistry();
        ConsoleReporter reporter = ConsoleReporter.forRegistry(registry).build();
        reporter.start(1, TimeUnit.SECONDS);

        Histogram histogram = new Histogram(new ExponentiallyDecayingReservoir());
        registry.register(MetricRegistry.name(HistogramsTest.class, "request", "histogram"), histogram);

        while (true) {
            Thread.sleep(1000L);
            int value = ThreadLocalRandom.current().nextInt(10000);
            histogram.update(value);
            System.out.println(value);
        }

    }
}
