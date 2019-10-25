package com.alfred.datasync.mapper;

import com.alfred.datasync.entity.Point;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface ImportPointMapper {

    Integer insertBatch(@Param("list") List<Point> list, @Param("table") String tableName, @Param("day") String day, @Param("createTime") Date createTime);


}
