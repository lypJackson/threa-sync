package com.alfred.datasync.service;

import com.alfred.datasync.entity.ImportDataStep;
import com.alfred.datasync.entity.ImportDataTask;
import com.alfred.datasync.mapper.ImportDataStepMapper;
import com.alfred.datasync.mapper.ImportDataTaskMapper;
import com.alfred.datasync.util.Constant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Slf4j
public class RecordPointInsertTask implements Runnable {


    private ImportDataStepMapper importDataStepMapper;
    private ImportDataTaskMapper importDataTaskMapper;
    private HighImportDataService highImportDataService;
    private String day;

    public RecordPointInsertTask(ImportDataStepMapper importDataStepMapper,
                                 ImportDataTaskMapper importDataTaskMapper,
                                 String day,
                                 HighImportDataService highImportDataService) {
        this.importDataStepMapper = importDataStepMapper;
        this.importDataTaskMapper = importDataTaskMapper;
        this.highImportDataService = highImportDataService;
        this.day = day;
    }

    @Override
    public void run() {
        log.info("start ------>> point 批量同步处理！！！");
        //查询任务数
        List<ImportDataTask> importDataTasks = importDataTaskMapper.queryTaskDatas(Constant.IMPORTTYPE.point.name(), day);
        //开始时间
        long start = System.currentTimeMillis();
        for (ImportDataTask importDataTask : importDataTasks) {
            /**
             * 返回结果失败重试10次
             */
            final int ReTryTime = 10;
            int handleTime = 0;
            while (handleTime<ReTryTime){
                List<ImportDataStep> steps = importDataStepMapper.queryAllStepNotDealAndFail(importDataTask.getId(),
                        day, Constant.IMPORTTYPE.point.name());
                if (CollectionUtils.isEmpty(steps)) {
                    break;
                }
                log.info("point handleTime 重试：{}", handleTime);
                highImportDataService.insertPointTaskStep(steps);
                handleTime++;
            }
        }
        long end = System.currentTimeMillis();
        log.info("end ------>> point批量处理结束！耗时 time：{},ms", end - start);

    }
}
