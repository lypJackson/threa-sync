package com.alfred.datasync.mapper;

import org.springframework.stereotype.Repository;

@Repository
public interface ImportDataTaskMapper {
    /**
     * 查询task表今天是否有数据导入
     *
     * @param day
     * @return
     */
    Integer queryTodayTaskIsImport(String day);
}
