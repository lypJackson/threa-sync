package com.alfred.datasync.mapper;

import com.alfred.datasync.entity.ImportDataTask;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface ImportDataTaskMapper {
    /**
     * 查询task表今天是否有数据导入
     *
     * @param day
     * @return
     */
    Integer queryTodayTaskIsImport(String day);

    int insert(ImportDataTask dataTask);

    List<ImportDataTask> queryTaskDatas(@Param("type") String type, @Param("day") String day);

    void updateByTaskId(@Param("id") Integer id, @Param("status") Integer status, @Param("updateTime") Date updateTime);
}
