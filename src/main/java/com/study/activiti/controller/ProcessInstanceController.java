package com.study.activiti.controller;

import com.study.activiti.model.ActivityVO;
import com.study.activiti.model.ProcessInstanceVO;
import com.study.activiti.util.CollectionUtil;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.runtime.ProcessInstance;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Slf4j
@Controller
@RequestMapping("/processInstance")
public class ProcessInstanceController {

    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private RuntimeService runtimeService;

    @ResponseBody
    @RequestMapping("/list")
    public Object list() {
        List<ProcessInstance> list = runtimeService.createProcessInstanceQuery().orderByProcessDefinitionKey().desc().list();
        List<ProcessInstanceVO> convert = CollectionUtil.convert(list, ProcessInstanceVO.class);
        return convert;
    }

    /**
     * 页面跳转
     * @param processInstanceId
     * @return
     */
    @RequestMapping("/goImage")
    public String goImage(String processInstanceId, Model model) {
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
        model.addAttribute("processDefinitionId", processInstance.getProcessDefinitionId());

        ActivityVO activity = getActivity(processInstanceId);
        model.addAttribute("divImg", "<div style='position: absolute;border:1px solid red;top:"
                + activity.getY() + "px;left: " + activity.getX() + "px;width: " + activity.getWidth()
                + "px;height:" + activity.getHeight() + "px;'></div>");

        return "image";
    }

    /**
     * 流程图追踪
     * @param processInstanceId
     * @return
     */
    @ResponseBody
    @RequestMapping("/getActivity")
    public ActivityVO getActivity(String processInstanceId)  {

        ActivityVO vo = new ActivityVO();

        // 1. 获取到当前活动的ID
        ProcessInstance pi = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
        String currentActivitiId = pi.getActivityId();

        // 2. 获取到流程定义
        ProcessDefinitionEntity pd = (ProcessDefinitionEntity) repositoryService.getProcessDefinition(pi.getProcessDefinitionId());
        // 3. 使用流程定义通过currentActivitiId得到活动对象
        ActivityImpl activity =  pd.findActivity(currentActivitiId);
        // 4. 获取活动的坐标
        BeanUtils.copyProperties(activity, vo);

        //如果有多个流程活动节点（并发流程一般有多个活动节点）该方法应该返回一个list，代码应该使用下面的方法
        // 得到流程执行对象
/*        List<Execution> executions = runtimeService.createExecutionQuery()
                .processInstanceId(pi.getId()).list();
        // 得到正在执行的Activity的Id
        List<String> activityIds = new ArrayList<String>();
        for (Execution exe : executions) {
            List<String> ids = runtimeService.getActiveActivityIds(exe.getId());
            activityIds.addAll(ids);
        }
        List<Map<String, Integer>> list = new ArrayList<Map<String, Integer>>();
        for (String id : activityIds) {
            ActivityImpl activity1 = pd.findActivity(id);
            Map<String, Integer> map = new HashMap<String, Integer>();
            map.put("x", activity1.getX());
            map.put("y", activity1.getY());
            map.put("width", activity1.getWidth());
            map.put("height", activity1.getHeight());
            list.add(map);
        }*/

        return vo;
    }

}
