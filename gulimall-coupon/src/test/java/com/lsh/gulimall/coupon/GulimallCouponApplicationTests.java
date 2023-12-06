package com.lsh.gulimall.coupon;

import org.junit.Test;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

//@RunWith(SpringRunner.class)
//@SpringBootTest
public class GulimallCouponApplicationTests {

    @Test
    public void contextLoads() {


        // 当前日期时间
        LocalDateTime startTime = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        // 当前日期时间 +3天
        LocalDateTime enbTime = startTime.plus(Duration.ofDays(3)).withHour(23).withMinute(59).withSecond(59);

        // 现在日期
        LocalDate startTime1=LocalDate.now();
        // 设置时间 最小 00:00
        LocalDateTime _startTime = LocalDateTime.of(startTime1, LocalTime.MIN);

        // 现在日期 设置时间 最大 23：59:59
        LocalDateTime now = LocalDateTime.of(startTime1, LocalTime.MAX);
        LocalDateTime _enbTime = now.plus(Duration.ofDays(3));


        System.out.println(_startTime);
        System.out.println(_enbTime);
//        System.out.println(startTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
//        System.out.println(enbTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
    }

}
