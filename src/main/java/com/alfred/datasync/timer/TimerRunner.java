package com.alfred.datasync.timer;

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
    private HighImportDataService highImportDataService;

    private static final int IMPORT_TASK_AND_STEP_RETRY_TIME = 5;


    public void timeGo() {
        log.info("MultiThreadDing RunnerImport start 导入step and task 数据开始！");
        long start = System.currentTimeMillis();
        Date date = new Date();
        String day = DateUtil.format(date, DateUtil.DATE_FORMAT_DAY_SHORT);

        boolean flagImport = false;
        int importTaskAndStepTime = 0;
        while (importTaskAndStepTime < IMPORT_TASK_AND_STEP_RETRY_TIME && !flagImport) {
            boolean todayIsTryImport = importDataTaskMapper.queryTodayTaskIsImport(day) > 0;
            if (todayIsTryImport) {
                log.info("今日 {} 数据已导入importDataTask 和 importDataStep表");
            } else {
                highImportDataService.dataSyncPrehandle(day);
            }
            flagImport = todayIsTryImport;
            importTaskAndStepTime++;
        }


    }

}
