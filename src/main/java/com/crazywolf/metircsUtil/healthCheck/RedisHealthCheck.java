package com.crazywolf.metircsUtil.healthCheck;

//import com.codahale.metrics.health.HealthCheck;
//import redis.clients.jedis.Jedis;
//import redis.clients.jedis.exceptions.JedisConnectionException;
//
///**
// * Created by cmdgjw@hotmail.com
// * 2017/3/30 11:06
// */
//public class RedisHealthCheck extends HealthCheck {
//    private Jedis jedis;
//
//    public static void main(String[] args) throws Exception {
//        RedisHealthCheck check = new RedisHealthCheck();
//        check.run();
//    }
//
//    public void run() throws Exception {
//
////        String resoult = jedis.ping();
////        System.out.println(resoult);
//        while (true) {
//            Result result = this.check();
//            boolean isHealth = result.isHealthy();
//            System.out.println(isHealth);
//            if (!isHealth) {
//                System.out.println(result.getError());
//                System.out.println(result.getMessage());
//            }
//            Thread.sleep(1000L);
//        }
//    }
//
//    @Override
//    public Result check() {
//        jedis = new Jedis("172.16.2.54", 6379);
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
