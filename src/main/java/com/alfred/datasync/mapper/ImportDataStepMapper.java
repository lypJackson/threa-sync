package com.alfred.datasync.mapper;

import com.alfred.datasync.entity.ImportDataStep;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface ImportDataStepMapper {

    Integer insertBatch(@Param("list") List<ImportDataStep> list);

    Integer queryTodayStepIsImportSuccess(String day);

    List<ImportDataStep> queryAllStepNotDealAndFail(@Param("taskId") Integer taskId, @Param("day") String day, @Param("type") String type);

    void updateByStepId(@Param("id") Integer id, @Param("status") Integer status, @Param("updateTime") Date updateTime);
}
