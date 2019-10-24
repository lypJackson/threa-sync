package com.alfred.datasync.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * 待同步数据分片范围
 */
@Data
@Accessors(chain = true)
public class ImportDataStep {

    /**
     * 自增id
     */
    private Integer id;

    /**
     * import_data_task id
     */
    private Integer taskId;
    /**
     * 数据开始位置
     */
    private Long rangeStart;

    /**
     * 数据结束位置
     */
    private Long rangeEnd;

    /**
     * 抽取数据日期
     */
    private String day;
    /**
     * 1:point 2:point_log 3:user_loan_record 4:finance_plan_user_stat
     */
    private String type;

    /**
     * 0:未处理 1:已处理 2:失败
     */
    private Integer status;
    /**
     * 其他信息
     */
    private String msg;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 更新时间
     */
    private Date updateTime;
    /**
     * 版本号
     */
    private Integer version;


}
