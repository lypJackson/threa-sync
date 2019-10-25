package com.alfred.datasync.entity;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class ImportPointKey {
    private String day;

    private Integer userid;
}
