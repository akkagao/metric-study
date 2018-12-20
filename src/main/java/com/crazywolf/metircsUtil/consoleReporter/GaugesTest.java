package com.crazywolf.metircsUtil.consoleReporter;

import com.codahale.metrics.ConsoleReporter;
import com.codahale.metrics.Gauge;
import com.codahale.metrics.MetricRegistry;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.TimeUnit;

/**
 * Created by cmdgjw@hotmail.com
 * 2017/3/30 9:52
 */
public class GaugesTest {
    public static Queue<String> queue = new LinkedList<String>();

    public static void main(String[] args) {
        MetricRegistry registry = new MetricRegistry();
        ConsoleReporter reporter = ConsoleReporter.forRegistry(registry).build();
        reporter.start(1, TimeUnit.SECONDS);

        registry.register(MetricRegistry.name(GaugesTest.class, "queue", "size"), new Gauge<Integer>() {
            public Integer getValue() {
                return queue.size();
            }
        });


        while (true) {
            try {
                Thread.sleep(1000L);
                queue.add("ss");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
