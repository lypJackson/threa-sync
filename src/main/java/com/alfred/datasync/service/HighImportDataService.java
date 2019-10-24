package com.alfred.datasync.service;

import com.alfred.datasync.entity.ImportDataStep;

import java.util.List;

public interface HighImportDataService {
    /**
     * 数据同步预处理：
     * 1、数据集分片(分页)
     * 2、将分片的数据保存到step表，记录任务数量到task表
     *
     * @param day
     */
    void dataSyncPrehandle(String day);

    void recordHandleImport(String day);

    void insertPointTaskStep(List<ImportDataStep> steps);


}
