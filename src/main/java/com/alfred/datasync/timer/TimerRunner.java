package com.alfred.datasync.timer;

import com.alfred.datasync.mapper.ImportDataStepMapper;
import com.alfred.datasync.mapper.ImportDataTaskMapper;
import com.alfred.datasync.service.HighImportDataService;
import com.alfred.datasync.util.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@Slf4j
public class TimerRunner {

    @Autowired
    private ImportDataTaskMapper importDataTaskMapper;
    @Autowired
    private ImportDataStepMapper stepMapper;

    @Autowired
    private HighImportDataService highImportDataService;

    private static final int IMPORT_TASK_AND_STEP_RETRY_TIME = 5;


    public void timeGo() {
        log.info("MultiThreadDing RunnerImport start 导入step and task 数据开始！");
        long start = System.currentTimeMillis();
        Date date = new Date();
        String day = DateUtil.format(date, DateUtil.DATE_FORMAT_DAY_SHORT);

        boolean flagImport = false;//标示task和step初始化状态，成功 true 失败 false
        int importTaskAndStepTime = 0;//失败重试次数，作用于task和step表
        while (importTaskAndStepTime < IMPORT_TASK_AND_STEP_RETRY_TIME && !flagImport) {
            //查询task表是否有任务数
            boolean todayIsTryImport = importDataTaskMapper.queryTodayTaskIsImport(day) > 0;
            if (todayIsTryImport) {
                log.info("今日 {} 数据已导入importDataTask 和 importDataStep表");
            } else {
                //初始化数据到task and step
                highImportDataService.dataSyncPrehandle(day);
            }
            flagImport = todayIsTryImport;
            importTaskAndStepTime++;
        }
        if (flagImport) {
            boolean recordHandleImport = false;
            int recordHandleImportTime = 0;
            while (recordHandleImportTime < IMPORT_TASK_AND_STEP_RETRY_TIME && !recordHandleImport) {
                recordHandleImport = stepMapper.queryTodayStepIsImportSuccess(day) == 0;
                if (recordHandleImport) {
                    log.info("import_x_x 该数据已导入完毕！");
                } else {
                    //多线程处理数据同步
                    highImportDataService.recordHandleImport(day);
                }
                recordHandleImportTime++;
            }
            if (!recordHandleImport){
                log.info("import_x_x_x 导入失败，重试结束等待下次任务！");
                return;
            }
        }else {
            log.info("task and step 未执行导入！");
            return;
        }
        long end = System.currentTimeMillis();
        log.info("导入生成全部 task and step and import_x_x_x 需要时间 time：{} ms",end-start);
    }
}
