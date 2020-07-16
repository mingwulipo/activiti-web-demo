package com.study.activiti.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lipo
 * @date 2020/7/13
 */
@Slf4j
public class CollectionUtil {

    /**
     * 复制属性
     * @param source
     * @param c
     * @return
     */
    public static <T> T convert(Object source, Class<T> c) {
        try {
            T t = c.newInstance();
            BeanUtils.copyProperties(source, t);
            return t;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 复制属性成集合
     * @param sourceList
     * @param c
     * @return
     */
    public static  <T> List<T> convert(List<?> sourceList, Class<T> c) {
        List<T> list = new ArrayList<>();
        sourceList.forEach(src -> list.add(convert(src, c)));
        return list;
    }
}
