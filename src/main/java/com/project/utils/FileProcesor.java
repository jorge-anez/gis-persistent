package com.project.utils;

import java.util.List;
import java.util.Map;

/**
 * Created by user on 4/18/2017.
 */
public class FileProcesor {

    public static void processShapeFile(List<String> files) {
        Map<String, String> mapFiles = SpacialFileUtils.getFileExtension(files);
        System.out.println(mapFiles);
        if(!SpatialFileValidator.validShapeFileExtensions(mapFiles)) {
            System.out.println("NO valid shape");
            return;
        }

        System.out.println("REady to pocess");
    }
}
