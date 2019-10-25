package com.alfred.datasync.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;

@Data
@Accessors(chain = true)
public class ImportPoint extends ImportPointKey {

    private BigDecimal availablepoints = BigDecimal.ZERO;

    private BigDecimal frozenpoints = BigDecimal.ZERO;

    private Date createTime;

}
