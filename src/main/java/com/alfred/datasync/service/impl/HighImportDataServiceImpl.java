package com.alfred.datasync.service.impl;

import com.alfred.datasync.entity.ImportDataTask;
import com.alfred.datasync.service.HighImportDataService;
import com.alfred.datasync.util.Constant;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class HighImportDataServiceImpl implements HighImportDataService {

    @Override
    public void dataSyncPrehandle(String day) {

    }


    /**
     * ===============================   查询 point 参数拼接 =========================
     */
    public ImportDataTask queryPointTask(String day) {
        ImportDataTask importDataTask = new ImportDataTask()
                .setCreateTime(new Date())
                .setUpdateTime(new Date())
                .setDay(day)
                .setStatus(Constant.IMPORT_NOT_DEAL)
                .setType(Constant.IMPORTTYPE.point.name())
                .setVersion(1);
        return importDataTask;
    }


}
