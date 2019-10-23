package com.alfred.datasync.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
@Accessors(chain = true)
public class ImportDataTask {
    /**
     * 自增id
     */
    private Integer id;

    /**
     * 抽取数据日期
     */
    private String day;

    /**
     * 1:point 2:point_log 3:user_loan_record 4:finance_plan_user_stat 5:user_stat
     */
    private String type;

    /**
     * 0 未处理,1已处理
     */
    private Integer status;

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