package com.alfred.datasync.dataobject;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Point表pointId最大值和最小值
 */
@Data
@Accessors(chain = true)
public class PointIdMaxAndMinDO {

    private Long pointIdMax;

    private Long pointIdMin;

}
