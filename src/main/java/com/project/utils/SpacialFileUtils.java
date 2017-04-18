package com.project.utils;

import org.apache.commons.io.FilenameUtils;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * Created by user on 4/18/2017.
 */
public class SpacialFileUtils {
    public static Map<String, String> getFileExtension(List<String> files) {
        Map<String, String> stringMap = new HashMap<String, String>();
        for (String e: files){
            stringMap.put(FilenameUtils.getExtension(e), FilenameUtils.getBaseName(e));
        }
        return stringMap;
    }
}
