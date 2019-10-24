package com.alfred.datasync.service.impl;

import com.alfred.datasync.dataobject.PointIdMaxAndMinDO;
import com.alfred.datasync.entity.ImportDataStep;
import com.alfred.datasync.entity.ImportDataTask;
import com.alfred.datasync.entity.Point;
import com.alfred.datasync.mapper.ImportDataStepMapper;
import com.alfred.datasync.mapper.ImportDataTaskMapper;
import com.alfred.datasync.mapper.PointMapper;
import com.alfred.datasync.service.HighImportDataService;
import com.alfred.datasync.service.RecordPointInsertTask;
import com.alfred.datasync.util.Constant;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.*;

@Service
@Slf4j
public class HighImportDataServiceImpl implements HighImportDataService {

    @Autowired
    private PointMapper pointMapper;

    @Autowired
    private ImportDataTaskMapper taskMapper;

    @Autowired
    private ImportDataStepMapper stepMapper;

    @Autowired
    private PlatformTransactionManager transactionManager;


    /**
     * 可用处理器的Java虚拟机的数量,当作核心线程数
     */
    private static final int corePoolSize = Runtime.getRuntime().availableProcessors();

    /**
     * 线程池名称格式
     */
    private static final String THREAD_POOL_NAME = "高可用改造专用线程池-%d";


    /**
     * 线程工厂名称
     */
    private static final ThreadFactory FACTORY = new ThreadFactoryBuilder().setNameFormat(THREAD_POOL_NAME).build();


    /**
     * 默认线程存活时间
     */
    private static final long DEFAULT_KEEP_ALIVE = 10L;

    /**
     * 默认队列大小
     */
    private static final int DEFAULT_SIZE = 1000;


    /**
     * 执行队列
     */
    private static BlockingQueue<Runnable> executeQueue = new LinkedBlockingQueue<>(DEFAULT_SIZE);


    /**
     * 数据分片初始化
     *
     * @param day
     */
    @Override
    public void dataSyncPrehandle(String day) {
        log.info("数据分片处理 start day:{}", day);
        //初始化组装task实体数据
        ImportDataTask pointTask = this.queryPointTask(day);
        //根据待同步point表来初始化 数据范围
        List<ImportDataStep> importDataSteps = this.queryImportDataSteps(pointTask, day);

        //为了保证step表和task表数据的一致性，这里手动处理事物，因为这是导入的基础，如果有一个发生失败则等待下次重试
        DefaultTransactionDefinition definition = new DefaultTransactionDefinition();
        TransactionStatus transaction = transactionManager.getTransaction(definition);
        try {
            taskMapper.insert(pointTask);
            for (ImportDataStep importDataStep : importDataSteps) {
                importDataStep.setTaskId(pointTask.getId());
            }
            stepMapper.insertBatch(importDataSteps);
            //提交事物
            transactionManager.commit(transaction);
        } catch (Exception e) {
            log.error("数据插入失败！！！回滚");
            transactionManager.rollback(transaction);
        }
    }


    /**
     * ===============================   查询 point 参数拼接 =========================
     */
    public ImportDataTask queryPointTask(String day) {
        ImportDataTask importDataTask = new ImportDataTask()
                .setCreateTime(new Date())
                .setUpdateTime(new Date())
                .setDay(day)
                .setStatus(Constant.IMPORT_NOT_DEAL)//首次默认 0:未处理
                .setType(Constant.IMPORTTYPE.point.name())
                .setVersion(1);
        return importDataTask;
    }


    /**
     * 数据分片
     *
     * @param day
     * @return
     */
    public List<ImportDataStep> queryImportDataSteps(ImportDataTask dataTask, String day) {
        ArrayList<ImportDataStep> lists = new ArrayList<>();
        //获取到point表待同步数据的最大id和最小id
        PointIdMaxAndMinDO maxAndMinPointId = pointMapper.getMaxAndMinPointId(day);
        if (null != maxAndMinPointId) {
            //【0-pointId】区间计算分片数(多少页)，例如：pointIdMax=30000，limitNew=10000
            //那么分片区间就为： 0-10000、10001-20000、20001-30000，inde也就等于3，表示分三次查询同步
            long index;
            if (maxAndMinPointId.getPointIdMax() % Constant.limitNew != 0) {
                index = maxAndMinPointId.getPointIdMax() / Constant.limitNew + 1;
            } else {
                index = maxAndMinPointId.getPointIdMax() / Constant.limitNew;
            }
            for (int i = 0; i < index; i++) {
                ImportDataStep step = new ImportDataStep()
                        .setCreateTime(new Date())
                        .setUpdateTime(new Date())
                        .setDay(day)
                        .setDay(day)
                        .setTaskId(dataTask.getId())
                        .setVersion(1)
                        .setStatus(Constant.IMPORT_NOT_DEAL)
                        .setType(Constant.IMPORTTYPE.point.name());
                //计算数据开始位置
                if (i == 0) {
                    step.setRangeStart((long) i * Constant.limitNew);
                } else {
                    step.setRangeStart((long) i * Constant.limitNew + 1);
                }
                //计算数据结束位置
                if (i == index - 1) {
                    step.setRangeEnd(maxAndMinPointId.getPointIdMax());
                } else {
                    step.setRangeEnd((long) (i + 1) * Constant.limitNew);
                }
                step.setMsg("point表数据同步,日期:" + day + " 每次按:" + Constant.limitNew + "条，分：" + index + "次同步，本次开始位置：" + step.getRangeStart() + "本次结束位置：" + step.getRangeEnd());
                //在这里加限制是为了防止有空挡插入step表
                if (maxAndMinPointId.getPointIdMin() <= step.getRangeEnd()) {
                    //走到此处说明分片的起始位置包含在最大id和最小id范围内
                    lists.add(step);
                }

            }
        }
        return lists;
    }


    /**
     * 多线程导入
     *
     * @param day
     */
    @Override
    public void recordHandleImport(String day) {
        //自定义线程池
        ExecutorService executor = new ThreadPoolExecutor(corePoolSize, corePoolSize + 1,
                DEFAULT_KEEP_ALIVE, TimeUnit.SECONDS, executeQueue, FACTORY);
        try {
            RecordPointInsertTask recordPointInsertTask = new RecordPointInsertTask(stepMapper, taskMapper, day, this);
            executor.execute(recordPointInsertTask);
        } catch (Exception e) {
            log.error("导入数据 import_x_x_x 线程池发生异常");
        }
        executor.shutdown();

        try {
            while (!executor.isTerminated()) {
                if (executor.awaitTermination(5, TimeUnit.SECONDS)) {
                    log.info("recordHandleImport thread pool close successfully");
                } else {
                    log.info("Waiting recordHandleImport thread pool close...");
                }
            }
        } catch (InterruptedException e) {
            log.error("线程池异常", e);
            // (Re-)Cancel if current thread also interrupted
            executor.shutdownNow();
            // Preserve interrupt status
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            e.printStackTrace();
            executor.shutdownNow();//试图停止当前正执行的task
        }
    }


    @Override
    public void insertPointTaskStep(List<ImportDataStep> steps) {

        for (ImportDataStep step : steps) {
            try {
                log.info("start  point 批量插入处理！rangeStart:{} rangeEnd:{}", step.getRangeStart(), step.getRangeEnd());
                List<Point> points = pointMapper.queryAllByPointId(step.getRangeStart(), step.getRangeEnd());



            } catch (Exception e) {

            }
        }

    }
}
