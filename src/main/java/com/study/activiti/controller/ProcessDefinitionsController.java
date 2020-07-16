package com.study.activiti.controller;

import com.study.activiti.model.ProcessDefinitionVO;
import com.study.activiti.util.CollectionUtil;
import groovy.util.logging.Slf4j;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.repository.ProcessDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/processDefinitions")
public class ProcessDefinitionsController {

    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private RuntimeService runtimeService;

    @RequestMapping("/list")
    public Object list() {
        List<ProcessDefinition> list = repositoryService.createProcessDefinitionQuery().orderByProcessDefinitionId().desc().list();
        return CollectionUtil.convert(list, ProcessDefinitionVO.class);
    }

    @RequestMapping("/delete")
    public Object deleteOne(String processDefinitionId){
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionId(processDefinitionId).singleResult();
        repositoryService.deleteDeployment(processDefinition.getDeploymentId());
        return "ok";
    }

    /**
     * 启动实例
     * @return
     */
    @RequestMapping("/start")
    public Object start(String processDefinitionId) {
        runtimeService.startProcessInstanceById(processDefinitionId);
        return "ok";
    }

    /**
     * 查看流程图
     * @param processDefinitionId
     * @return
     */
    @RequestMapping("/showPng")
    public Object showPng(String processDefinitionId, HttpServletResponse response) throws IOException {
        InputStream in = repositoryService.getProcessDiagram(processDefinitionId);
        ServletOutputStream out = response.getOutputStream();
        // 把图片的输入流程写入resp的输出流中
        byte[] b = new byte[1024];
        for (int len = -1; (len= in.read(b))!=-1; ) {
            out.write(b, 0, len);
        }
        // 关闭流
        out.close();
        in.close();
        return null;
    }

    @RequestMapping(value = "/showPng1", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> getImage(String processDefinitionId) throws Exception{

        byte[] imageContent ;
        String path = "D:/leave.png";
        imageContent = fileToByte(new File(path));

        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_PNG);
        return new ResponseEntity<>(imageContent, headers, HttpStatus.OK);
    }

    public static byte[] fileToByte(File img) throws Exception {
        byte[] bytes = null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            BufferedImage bi;
            bi = ImageIO.read(img);
            ImageIO.write(bi, "png", baos);
            bytes = baos.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            baos.close();
        }
        return bytes;
    }
}
