package com.alfred.datasync.mapper;

import com.alfred.datasync.dataobject.PointIdMaxAndMinDO;
import com.alfred.datasync.entity.Point;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PointMapper {

    long getMaxPointId();

    Integer insertBatch(@Param("list") List<Point> list);

    PointIdMaxAndMinDO getMaxAndMinPointId(String day);

    List<Point> queryAllByPointId(@Param("start") Long start, @Param("end") Long end);
}
