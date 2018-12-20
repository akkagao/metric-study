package com.crazywolf.common;

import com.codahale.metrics.ConsoleReporter;
import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * Created by cmdgjw@hotmail.com
 * 2017/3/31 10:28
 */
@Configuration
public class MetricConfig {
    @Bean
    public MetricRegistry metrics() {
        return new MetricRegistry();
    }

    /**
     * Reporter 数据的展现位置
     *
     * @param metrics
     * @return
     */
    @Bean
    public ConsoleReporter consoleReporter(MetricRegistry metrics) {
        return ConsoleReporter.forRegistry(metrics)
                .convertRatesTo(TimeUnit.SECONDS)
                .convertDurationsTo(TimeUnit.MILLISECONDS)
                .build();
    }

    /**
     * TPS 计算器
     *
     * @param metrics
     * @return
     */
//    @Bean
//    public Meter requestMeter(MetricRegistry metrics) {
//        return metrics.meter("request");
//    }


    /**
     * 计时器
     *
     * @param metrics
     * @return
     */
//    @Bean
//    public Timer responses(MetricRegistry metrics) {
//        return metrics.timer("executeTime");
//    }


}
