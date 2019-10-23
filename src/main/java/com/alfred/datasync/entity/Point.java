package com.alfred.datasync.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;

@Data
@Accessors(chain = true)
public class Point {

    private Integer pointId;

    private BigDecimal availablePoints;

    private BigDecimal frozenPoints;

    private Integer version;

    private Integer user;

    private Date lastUpdateTime;

    private Integer delayUpdateMode;

    private Integer latestPointLogId;

}
