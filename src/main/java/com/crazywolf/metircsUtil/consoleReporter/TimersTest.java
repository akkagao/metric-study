package com.crazywolf.metircsUtil.consoleReporter;

import com.codahale.metrics.ConsoleReporter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

/**
 * Created by cmdgjw@hotmail.com
 * 2017/3/30 10:58
 */
public class TimersTest {
    public static void main(String[] args) throws InterruptedException {
        MetricRegistry registry = new MetricRegistry();
        ConsoleReporter reporter = ConsoleReporter.forRegistry(registry).build();
        reporter.start(5, TimeUnit.SECONDS);
        Timer timer = registry.timer(MetricRegistry.name(TimersTest.class, "get-latency"));
        Timer.Context ctx;
        while (true) {
            ctx = timer.time();
            Thread.sleep(ThreadLocalRandom.current().nextInt(1000));
            ctx.stop();
        }
    }
}
