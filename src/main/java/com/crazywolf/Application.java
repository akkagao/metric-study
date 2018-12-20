package com.crazywolf;

import com.codahale.metrics.ConsoleReporter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import java.util.concurrent.TimeUnit;

/**
 * Created by cmdgjw@hotmail.com
 * 2017/3/29 9:34
 */
@SpringBootApplication
@EnableAutoConfiguration
public class Application {
    public static void main(String[] args) {
        ApplicationContext ctx = SpringApplication.run(Application.class, args);

        // 启动Reporter
        ConsoleReporter reporter = ctx.getBean(ConsoleReporter.class);
        reporter.start(5, TimeUnit.SECONDS);
    }
}
