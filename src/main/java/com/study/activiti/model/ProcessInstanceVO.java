package com.study.activiti.model;

import lombok.Data;

/**
 * @author lipo
 * @date 2020/7/15
 */
@Data
public class ProcessInstanceVO {
    private String id;
    private String processDefinitionName;
    private String processDefinitionKey;
    private Integer processDefinitionVersion;
}
