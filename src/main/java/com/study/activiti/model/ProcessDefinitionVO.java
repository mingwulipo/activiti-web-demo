package com.study.activiti.model;

import lombok.Data;

/**
 * @author lipo
 * @date 2020/7/15
 */
@Data
public class ProcessDefinitionVO {
    private String id;
    private String name;
    private String key;
    private Integer version;
}
