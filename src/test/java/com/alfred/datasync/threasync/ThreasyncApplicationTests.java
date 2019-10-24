package com.alfred.datasync.threasync;

import com.alfred.datasync.ThreasyncApplication;
import com.alfred.datasync.entity.Point;
import com.alfred.datasync.mapper.PointMapper;
import com.alfred.datasync.timer.TimerRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ThreasyncApplication.class)
@EnableAutoConfiguration
public class ThreasyncApplicationTests {

    @Autowired
    private PointMapper pointMapper;


    @Autowired
    private TimerRunner timerRunner;

    @Test
    public void importAll() {

        try {
            timerRunner.timeGo();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private int threadCount = 5; //子线程数

    private CountDownLatch countDownLatch = new CountDownLatch(threadCount);

    /**
     * 多线程插入测试数据入口
     *
     * @throws InterruptedException
     */
    @Test
    public void createBatchData() throws InterruptedException {
        /**
         * https://cloud.tencent.com/developer/article/1404322
         * io密集型：估算公式 【CPU核数/1-阻塞系数】其中阻塞系数在0.8-0.9之间
         */
        ExecutorService executorService = Executors.newFixedThreadPool(40);
        for (int i = 0; i < 6; i++) {
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        ArrayList<Point> points = new ArrayList<>();
                        for (int j = 0; j < 5000; j++) {
                            Point point = new Point()
                                    .setUser(ThreadLocalRandom.current().nextInt(900000000))
                                    .setAvailablePoints(new BigDecimal(10000))
                                    .setDelayUpdateMode(j)
                                    .setFrozenPoints(new BigDecimal(10000))
                                    .setLastUpdateTime(new Date())
                                    .setLatestPointLogId(j)
                                    .setVersion(0);
                            points.add(point);
                        }
                        pointMapper.insertBatch(points);
                    } finally {
                        countDownLatch.countDown();
                    }

                }
            });
        }
        System.out.println("== 处理中 == ");
        countDownLatch.await();
        System.out.println("== 插入结束 == ");
    }

}
