package com.crazywolf.metircsUtil.healthCheck;

//import com.codahale.metrics.health.HealthCheck;
//import com.codahale.metrics.health.HealthCheckRegistry;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisConnectionException;

import java.util.Map;

/**
 * Created by cmdgjw@hotmail.com
 * 2017/3/30 11:06
 */
//public class RedisHealthCheckFull extends HealthCheck {
//    private static Jedis jedis;
//
//    public static void main(String[] args) throws Exception {
//        RedisHealthCheckFull check = new RedisHealthCheckFull();
//        jedis = new Jedis("172.16.2.54", 6379);
//
//        HealthCheckRegistry healthChecks = new HealthCheckRegistry();
//        healthChecks.register(RedisHealthCheckFull.class.getName(), new RedisHealthCheckFull());
//        healthChecks.register(RedisHealthCheck.class.getName(), new RedisHealthCheck());
//
//        final Map<String, Result> results = healthChecks.runHealthChecks();
//        for (Map.Entry<String, Result> entry : results.entrySet()) {
//            if (entry.getValue().isHealthy()) {
//                System.out.println(entry.getKey() + " is healthy");
//            } else {
//                System.err.println(entry.getKey() + " is UNHEALTHY: " + entry.getValue().getMessage());
//                final Throwable e = entry.getValue().getError();
//                if (e != null) {
//                    e.printStackTrace();
//                }
//            }
//        }
//    }
//
//
//    @Override
//    public Result check() {
//        try {
//            if ("PONG".equals(jedis.ping())) {
//                return Result.healthy();
//            }
//        } catch (JedisConnectionException se) {
//            return Result.unhealthy("Can't ping redis 172.16.2.54");
//        }
//        return Result.unhealthy("Can't ping redis 172.16.2.54");
//    }
//}
