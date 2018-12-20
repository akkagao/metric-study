# Metrics调研
## Metrics 简介
Metrics提供了一种强大的工具包，用于统计生产环境中关键组件的性能数据,已经被使用在Hadoop、Kafka、Spark、Jstorm知名开源项目中
- 统计Api接口每秒调用次数（TPS）
- 平均每个请求处理时间
- 请求处理最长耗时（耗时分布情况）
## 实现效果
结合spring使用，可以用很少的代码,实现统计所有接口调用数据
```
com.crazywolf.controller.UserController.getUsers-time
          count = 116                         -- 执行次数
      mean rate = 1.93 calls/second           -- 平均调用频率
  1-minute rate = 1.88 calls/second           -- 最近1分钟调用频率
  5-minute rate = 1.82 calls/second           -- 最近5分钟调用频率 
 15-minute rate = 1.81 calls/second           -- 最近15分钟调用频率
            min = 4.19 milliseconds           -- 最快一次调用时间
            max = 991.65 milliseconds         -- 最慢一次调用
           mean = 515.92 milliseconds         -- 平均调用时间
         stddev = 283.65 milliseconds         -- 标准偏差
         median = 542.47 milliseconds         -- 中位数
           75% <= 750.04 milliseconds         -- 75% 的请求执行时间小于750 ms
           95% <= 936.60 milliseconds         -- 95% 的请求执行时间小于936 ms
           98% <= 975.13 milliseconds         -- 98% 的请求执行时间小于975 ms
           99% <= 988.94 milliseconds         -- 99% 的请求执行时间小于988 ms
         99.9% <= 991.65 milliseconds         -- 99.9% 的请求执行时间小于991 ms
```
## 统计数据展示（上报）方式
- JMX

![Markdown](http://i4.buimg.com/589938/49e42d676e8cd213.png)
- console

统计信息输出到控制台，上一章节中的信息就是console输出效果
- SLF4J
  ​      

和console类似，只是把信息输出到日志文件中
- CVS

输出到CVS文件中
```
t	count	mean_rate	m1_rate	m5_rate	m15_rate	rate_unit
1490845886	73	73.19439	0	0	0	events/second
1490845887	97	48.627953	0	0	0	events/second
1490845888	129	43.077718	0	0	0	events/second
1490845889	217	54.324342	0	0	0	events/second
```
## Metrics统计类型（工具）
以下使用Console方式展示数据,运行以下demo需要引入包
```xml
<dependencies>
    <dependency>
        <groupId>io.dropwizard.metrics</groupId>
        <artifactId>metrics-core</artifactId>
        <version>3.1.0</version>
    </dependency>
     <dependency>
            <groupId>com.codahale.metrics</groupId>
            <artifactId>metrics-healthchecks</artifactId>
            <version>3.0.2</version>
        </dependency>
</dependencies>
```
### Gauges
最简单的一种统计类型，只返回一个值。
```java
public class GaugeTest {
    public static Queue<String> q = new LinkedList<String>();
    public static void main(String[] args) throws InterruptedException {
        MetricRegistry registry = new MetricRegistry();
        ConsoleReporter reporter = ConsoleReporter.forRegistry(registry).build();
        reporter.start(1, TimeUnit.SECONDS);
        registry.register(MetricRegistry.name(GaugeTest.class, "queue", "size"), 
        new Gauge<Integer>() {
            public Integer getValue() {
                return q.size();
            }
        });
        while(true){
            Thread.sleep(1000);
            q.add("Job-xxx");
        }
    }
}

运行之后的结果如下：
-- Gauges ------------------------------------------------
com.alibaba.wuchong.metrics.GaugeTest.queue.size
             value = 6
```
### Counters
Counter 就是计数器，Counter 只是用 Gauge 封装了 AtomicLong 。我们可以使用如下的方法，使得获得队列大小更加高效
```java
public class CounterTest {
    public static Queue<String> q = new LinkedBlockingQueue<String>();
    public static Counter pendingJobs;
    public static Random random = new Random();
    public static void addJob(String job) {
        pendingJobs.inc();
        q.offer(job);
    }
    public static String takeJob() {
        pendingJobs.dec();
        return q.poll();
    }
    public static void main(String[] args) throws InterruptedException {
        MetricRegistry registry = new MetricRegistry();
        ConsoleReporter reporter = ConsoleReporter.forRegistry(registry).build();
        reporter.start(1, TimeUnit.SECONDS);
        pendingJobs = registry.counter(MetricRegistry.name(Queue.class,"pending-jobs","size"));
        int num = 1;
        while(true){
            Thread.sleep(200);
            if (random.nextDouble() > 0.7){
                String job = takeJob();
                System.out.println("take job : "+job);
            }else{
                String job = "Job-"+num;
                addJob(job);
                System.out.println("add job : "+job);
            }
            num++;
        }
    }
}
运行之后的结果大致如下：

add job : Job-15
add job : Job-16
take job : Job-8
take job : Job-10
add job : Job-19
15-8-1 16:11:31 ============================================
-- Counters ----------------------------------------------
java.util.Queue.pending-jobs.size
             count = 5
```
### Meters
Meter度量一系列事件发生的速率(rate)，例如TPS。Meters会统计最近1分钟，5分钟，15分钟，还有全部时间的速率。
```java
public class MeterTest {
    public static Random random = new Random();
    public static void request(Meter meter){
        System.out.println("request");
        meter.mark();
    }
    public static void request(Meter meter, int n){
        while(n > 0){
            request(meter);
            n--;
        }
    }
    public static void main(String[] args) throws InterruptedException {
        MetricRegistry registry = new MetricRegistry();
        ConsoleReporter reporter = ConsoleReporter.forRegistry(registry).build();
        reporter.start(1, TimeUnit.SECONDS);
        Meter meterTps = registry.meter(MetricRegistry.name(MeterTest.class,"request","tps"));
        while(true){
            request(meterTps,random.nextInt(5));
            Thread.sleep(1000);
        }
    }
}
运行结果大致如下：

request
15-8-1 16:23:25 ============================================
-- Meters ------------------------------------------------
com.alibaba.wuchong.metrics.MeterTest.request.tps
             count = 134
         mean rate = 2.13 events/second
     1-minute rate = 2.52 events/second
     5-minute rate = 3.16 events/second
    15-minute rate = 3.32 events/second
注：非常像 Unix 系统中 uptime 和 top 中的 load。
```
### Histograms
Histogram统计数据的分布情况。比如最小值，最大值，中间值，还有中位数，75百分位, 90百分位, 95百分位, 98百分位, 99百分位, 和 99.9百分位的值(percentiles)。

比如request的大小的分布：
```
public class HistogramTest {
    public static Random random = new Random();
    public static void main(String[] args) throws InterruptedException {
        MetricRegistry registry = new MetricRegistry();
        ConsoleReporter reporter = ConsoleReporter.forRegistry(registry).build();
        reporter.start(1, TimeUnit.SECONDS);
        Histogram histogram = new Histogram(new ExponentiallyDecayingReservoir());
        registry.register(MetricRegistry.name(HistogramTest.class, "request", "histogram"), histogram);
        
        while(true){
            Thread.sleep(1000);
            histogram.update(random.nextInt(100000));
        }
    }
}
运行之后结果大致如下：

-- Histograms --------------------------------------------
java.util.Queue.queue.histogram
             count = 56
               min = 1122
               max = 99650
              mean = 48735.12
            stddev = 28609.02
            median = 49493.00
              75% <= 72323.00
              95% <= 90773.00
              98% <= 94011.00
              99% <= 99650.00
            99.9% <= 99650.00
```
### Timers
Timer其实是 Histogram 和 Meter 的结合， histogram 某部分代码/调用的耗时， meter统计TPS。
```java
public class TimerTest {
    public static Random random = new Random();
    public static void main(String[] args) throws InterruptedException {
        MetricRegistry registry = new MetricRegistry();
        ConsoleReporter reporter = ConsoleReporter.forRegistry(registry).build();
        reporter.start(1, TimeUnit.SECONDS);
        Timer timer = registry.timer(MetricRegistry.name(TimerTest.class,"get-latency"));
        Timer.Context ctx;
        while(true){
            ctx = timer.time();
            Thread.sleep(random.nextInt(1000));
            ctx.stop();
        }
    }
}
运行之后结果如下：

-- Timers ------------------------------------------------
com.alibaba.wuchong.metrics.TimerTest.get-latency
             count = 38
         mean rate = 1.90 calls/second
     1-minute rate = 1.66 calls/second
     5-minute rate = 1.61 calls/second
    15-minute rate = 1.60 calls/second
               min = 13.90 milliseconds
               max = 988.71 milliseconds
              mean = 519.21 milliseconds
            stddev = 286.23 milliseconds
            median = 553.84 milliseconds
              75% <= 763.64 milliseconds
              95% <= 943.27 milliseconds
              98% <= 988.71 milliseconds
              99% <= 988.71 milliseconds
            99.9% <= 988.71 milliseconds
```
----
## 以上为Metrics初体验，我们项目中怎么使用呢？
我们的项目使用springBoot做RESTful 接口，如果要统计每个接口调用次数、耗时以便找出可能的性能瓶颈。最简洁的方法就是使用spring 的aop机制拦截所有的controller，然后统计执行次数和执行时间

1. 加入mvn配置
```xml
<dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-aop</artifactId>
            <version>${spring.version}</version>
        </dependency>
        <dependency>
            <groupId>org.aspectj</groupId>
            <artifactId>aspectjrt</artifactId>
            <version>1.6.12</version>
        </dependency>
        <dependency>
            <groupId>org.aspectj</groupId>
            <artifactId>aspectjweaver</artifactId>
            <version>1.6.12</version>
        </dependency>
        <dependency>
            <groupId>cglib</groupId>
            <artifactId>cglib</artifactId>
            <version>2.2</version>
        </dependency>
        <dependency>
            <groupId>com.codahale.metrics</groupId>
            <artifactId>metrics-core</artifactId>
            <version>3.0.2</version>
        </dependency>
```
2. ReportApiApplication 项目启动入口类main方法中增加Metrics数据展示频率
```java
   public static void main(String[] args) throws Exception {
        ApplicationContext ctx = SpringApplication.run(ReportApiApplication.class, args);
        //启动用户信息清理定时任务
        //UserInfoTimer.startTimer();

        // 设置每5秒钟展示一次统计数据
        ConsoleReporter reporter = ctx.getBean(ConsoleReporter.class);
        reporter.start(5, TimeUnit.SECONDS);
    }
```
3. 初始化MetricRegistry，并且注册ConsoleReporter
`ps:MetricRegistry类是Metrics的核心，它是存放应用中所有metrics的容器。也是我们使用 Metrics 库的起点。`
```java
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
}

```
4. Aop 拦截(使用环绕通知拦截所有的controller)
```java
@Component
@Aspect
public class MetricsAspect {
    @Autowired
    MetricRegistry metricRegistry;

    @Pointcut("execution(* com.crazywolf.controller..*(..))")
    public void aspect() {
    }

    //配置环绕通知,使用在方法aspect()上注册的切入点
    @Around("aspect()")
    public Object around(ProceedingJoinPoint joinPoint) {
        long start = System.currentTimeMillis();

//            Meter meter = metricRegistry.meter(joinPoint.getTarget().getClass().getName() + "." + joinPoint.getSignature().getName());
//            meter.mark();

//            获取Timer对象
        Timer responses = metricRegistry.timer(joinPoint.getTarget().getClass().getName() + "." + joinPoint.getSignature().getName() + "-" + "timer");
        final Timer.Context context = responses.time();
        try {
//            System.out.println("around " + joinPoint + "\tUse time : " + (end - start) + " ms!");
            //调用执行目标方法
            Object obj = joinPoint.proceed();
            return obj;
        } catch (Throwable e) {
            long end = System.currentTimeMillis();
//            System.out.println("around " + joinPoint + "\tUse time : " + (end - start) + " ms with exception : " + e.getMessage());
            e.printStackTrace();
            return null;
        } finally {
            context.stop();
        }
    }
}
```
5. 结果
```
com.crazywolf.controller.UserController.getUsers-time
          count = 116                         -- 执行次数
      mean rate = 1.93 calls/second           -- 平均调用频率
  1-minute rate = 1.88 calls/second           -- 最近1分钟调用频率
  5-minute rate = 1.82 calls/second           -- 最近5分钟调用频率 
 15-minute rate = 1.81 calls/second           -- 最近15分钟调用频率
            min = 4.19 milliseconds           -- 最快一次调用时间
            max = 991.65 milliseconds         -- 最慢一次调用
           mean = 515.92 milliseconds         -- 平均调用时间
         stddev = 283.65 milliseconds         -- 标准偏差
         median = 542.47 milliseconds         -- 中位数
           75% <= 750.04 milliseconds         -- 75% 的请求执行时间小于750 ms
           95% <= 936.60 milliseconds         -- 95% 的请求执行时间小于936 ms
           98% <= 975.13 milliseconds         -- 98% 的请求执行时间小于975 ms
           99% <= 988.94 milliseconds         -- 99% 的请求执行时间小于988 ms
         99.9% <= 991.65 milliseconds         -- 99.9% 的请求执行时间小于991 ms
```
---
## 畅想未来
利用Metrics+influxdb+grafana 或者Metrics+ganglia构建监控平台

grafana 效果图
![Markdown](http://i4.buimg.com/589938/ce6195bda7c96423.png)
ganglia 效果图
![Markdown](http://i4.buimg.com/589938/756e0c3f79303627.jpg)

---
## 扩展了解
### 除此之外，Metrics还提供了 HealthCheck
（健康检查）,可以用于检查项目依赖的第三方服务是否还可以继续提供服务
```java
public class RedisHealthCheckFull extends HealthCheck {
    private static Jedis jedis;

    public static void main(String[] args) throws Exception {
        RedisHealthCheckFull check = new RedisHealthCheckFull();
        jedis = new Jedis("127.0.0.1", 6379);

        HealthCheckRegistry healthChecks = new HealthCheckRegistry();
        healthChecks.register(RedisHealthCheckFull.class.getName(), new RedisHealthCheckFull());

        final Map<String, Result> results = healthChecks.runHealthChecks();
        for (Map.Entry<String, Result> entry : results.entrySet()) {
            if (entry.getValue().isHealthy()) {
                System.out.println(entry.getKey() + " is healthy");
            } else {
                System.err.println(entry.getKey() + " is UNHEALTHY: " + entry.getValue().getMessage());
                final Throwable e = entry.getValue().getError();
                if (e != null) {
                    e.printStackTrace();
                }
            }
        }
    }


    @Override
    public Result check() {
        try {
            if ("PONG".equals(jedis.ping())) {
                return Result.healthy();
            }
        } catch (JedisConnectionException se) {
            return Result.unhealthy("Can't ping redis 172.16.2.54");
        }
        return Result.unhealthy("Can't ping redis 172.16.2.54");
    }
}
运行结果
com.crazywolf.metircsUtil.healthCheck.RedisHealthCheckFull is healthy
```
### Metrics for Spring
1. mvn 配置
```xml
<dependency>
    <groupId>com.ryantenney.metrics</groupId>
    <artifactId>metrics-spring</artifactId>
    <version>3.1.3</version>
</dependency>
```
2. Spring Context XML (metrics.xml放在项目resources目录下)
```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:metrics="http://www.ryantenney.com/schema/metrics"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd http://www.ryantenney.com/schema/metrics
           http://www.ryantenney.com/schema/metrics/metrics.xsd">
    <bean id="helloService" class="com.crazywolf.bean.TestBean"></bean>

    <metrics:metric-registry id="metricRegistry" />

    <metrics:health-check-registry id="health" />

    <metrics:annotation-driven metric-registry="metricRegistry" health-check-registry="health" />

    <metrics:reporter type="console" metric-registry="metricRegistry" period="5s" />

    <metrics:register metric-registry="metricRegistry">
        <bean metrics:name="jvm.gc" class="com.codahale.metrics.jvm.GarbageCollectorMetricSet" />
        <bean metrics:name="jvm.memory" class="com.codahale.metrics.jvm.MemoryUsageGaugeSet" />
        <bean metrics:name="jvm.thread-states" class="com.codahale.metrics.jvm.ThreadStatesGaugeSet" />
        <bean metrics:name="jvm.fd.usage" class="com.codahale.metrics.jvm.FileDescriptorRatioGauge" />
    </metrics:register>
</beans>
```
3. 注册report，并且设置信息输出频率
```java
@Configuration
@EnableMetrics
@ImportResource(locations = {"classpath:metrics.xml"})
public class SpringConfiguringClass extends MetricsConfigurerAdapter {
    @Override
    public void configureReporters(MetricRegistry metricRegistry) {
        registerReporter(ConsoleReporter
                .forRegistry(metricRegistry)
                .build())
                .start(10, TimeUnit.SECONDS);
    }
}
```
4. 结果
```
-- Gauges ----------------------------------------------------------------------
gauge.response.user.getUsers
             value = 52.0
jvm.fd.usage
             value = NaN
jvm.gc.PS-MarkSweep.count                      -- 老年带GC次数
             value = 5
jvm.gc.PS-MarkSweep.time                       -- 老年代GC耗时
             value = 4677
jvm.gc.PS-Scavenge.count                       -- 新生代GC次数
             value = 25
jvm.gc.PS-Scavenge.time                        -- 新生代GC耗时
             value = 1575
jvm.memory.heap.committed                      -- 以下为内存使用情况
             value = 1306525696
jvm.memory.heap.init
             value = 134217728
jvm.memory.heap.max
             value = 1897922560
jvm.memory.heap.usage
             value = 0.5651632319497799
jvm.memory.heap.used
             value = 1072799744
jvm.memory.non-heap.committed
             value = 51707904
jvm.memory.non-heap.init
             value = 2555904
jvm.memory.non-heap.max
             value = -1
jvm.memory.non-heap.usage
             value = -5.0357776E7
jvm.memory.non-heap.used
             value = 50357776
jvm.memory.pools.Code-Cache.usage
             value = 0.042731984456380205
jvm.memory.pools.Compressed-Class-Space.usage
             value = 0.004149474203586578
jvm.memory.pools.Metaspace.usage
             value = 0.971599413001019
jvm.memory.pools.PS-Eden-Space.usage
             value = 0.6430536058213976
jvm.memory.pools.PS-Old-Gen.usage
             value = 0.4929169656602498
jvm.memory.pools.PS-Survivor-Space.usage
             value = 0.9962044534412956
jvm.memory.total.committed
             value = 1358233600
jvm.memory.total.init
             value = 136773632
jvm.memory.total.max
             value = 1897922559
jvm.memory.total.used
             value = 1123874520
jvm.thread-states.blocked.count                      --线程统计信息
             value = 0
jvm.thread-states.count
             value = 124
jvm.thread-states.daemon.count
             value = 122
jvm.thread-states.deadlock.count
             value = 0
jvm.thread-states.deadlocks
             value = []
jvm.thread-states.new.count
             value = 0
jvm.thread-states.runnable.count
             value = 52
jvm.thread-states.terminated.count
             value = 0
jvm.thread-states.timed_waiting.count
             value = 70
jvm.thread-states.waiting.count
             value = 3
```
参考资料

[http://metrics.dropwizard.io/3.1.0/manual/](http://metrics.dropwizard.io/3.1.0/manual/)

[http://wuchong.me/blog/2015/08/01/getting-started-with-metrics/](http://wuchong.me/blog/2015/08/01/getting-started-with-metrics/)

[https://github.com/ryantenney/metrics-spring](https://github.com/ryantenney/metrics-spring)

[http://www.jianshu.com/p/e4f70ddbc287](http://www.jianshu.com/p/e4f70ddbc287)
[http://www.jianshu.com/p/fadcf4d92b0e](http://www.jianshu.com/p/fadcf4d92b0e)

[http://www.cnblogs.com/super-d2/p/5002061.html](http://www.cnblogs.com/super-d2/p/5002061.html)

[http://play.grafana.org/dashboard/db/grafana-play-home](http://play.grafana.org/dashboard/db/grafana-play-home)
