package com.lsh.gulimall.seckill.scheduled;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Service;

/**
 * //TODO
 *
 * @Description: @EnableScheduling 开启定时任务
 * @Author: shihe
 * @Date: 2021-10-19 23:22
 */
@EnableScheduling
@Slf4j
@Service
@EnableAsync
public class HelloSchedule {


    /**
     * //TODO
     *
     * @param
     * @return: void
     * @Description: 第10秒启动 每5秒执行一次 秒 分 时 日（一个月中的某一天） 月 周（一周中的某一天）
     * spring中的区别
     *  1、@Scheduled 只支持6位 不支持第七位 （年）
     *  2、周一到周日 1-7 （原来 0-6）
     *  3、默认阻塞 一个任务阻塞了 下一个任务无法执行
     * 解决：1、以异步方式运行（提交到线程池）自己配置
     *      2、配置 TaskSchedulingProperties 配置调度任务线程池大小 spring.task.scheduling.pool.size=8
     *      3、让定时任务异步执行 springboot配置异步任务
     *          @EnableAsync 类上标注 开启异步任务功能
     *          @Async 方法上标注
     *          自动配置类：TaskExecutionAutoConfiguration 配置类：TaskExecutionProperties 前缀： spring.task.execution 默认8线程
     *
     */
//    @Scheduled(cron = "10/5 * * ? * *")
//    @Async
//    public void hello() throws InterruptedException {
//        log.warn("定时任务 hello...........");
//        // 默认阻塞 解决阻塞以后  Thread.sleep不影响任务定时执行
//        Thread.sleep(3000);
//    }
}
