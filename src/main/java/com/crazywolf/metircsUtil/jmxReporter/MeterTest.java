package com.crazywolf.metircsUtil.jmxReporter;

import com.codahale.metrics.JmxReporter;
import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by cmdgjw@hotmail.com
 * 2017/3/30 10:29
 */
public class MeterTest {
    public static void main(String[] args) throws InterruptedException {
        MetricRegistry registry = new MetricRegistry();
        JmxReporter reporter = JmxReporter.forRegistry(registry).build();
        reporter.start();

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
