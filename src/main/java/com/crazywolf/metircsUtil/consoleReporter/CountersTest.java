package com.crazywolf.metircsUtil.consoleReporter;

import com.codahale.metrics.ConsoleReporter;
import com.codahale.metrics.Counter;
import com.codahale.metrics.MetricRegistry;

import java.util.concurrent.TimeUnit;

/**
 * Created by cmdgjw@hotmail.com
 * 2017/3/30 9:52
 */
public class CountersTest {

    public static Counter counter;

    public static void main(String[] args) {
        MetricRegistry registry = new MetricRegistry();
        ConsoleReporter reporter = ConsoleReporter.forRegistry(registry).build();
        reporter.start(1, TimeUnit.SECONDS);

        counter = registry.counter(MetricRegistry.name(CountersTest.class, "queue", "size"));

        while (true) {
            try {
                Thread.sleep(1000L);
                counter.inc();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
