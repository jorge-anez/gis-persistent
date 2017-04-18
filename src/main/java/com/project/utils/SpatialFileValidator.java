package com.project.utils;

import org.apache.commons.io.FilenameUtils;

import java.util.*;

/**
 * Created by user on 4/18/2017.
 */
public class SpatialFileValidator {
    public static boolean validShapeFileExtensions(Map<String, String> files) {
        if(new HashSet<String>(files.values()).size() != 1)
                return false;

        Set<String> e = new TreeSet<String>();
        e.add("shp"); e.add("SHP");
        e.add("dbf"); e.add("DBF");
        e.add("shx"); e.add("SHX");
        for (String x: files.keySet()) {
            if(x.equals("shp")){ e.remove("SHP"); e.remove("shp");}
            if(x.equals("SHP")){ e.remove("shp"); e.remove("SHP");}
            if(x.equals("dbf")){ e.remove("DBF"); e.remove("dbf");}
            if(x.equals("DBF")){ e.remove("dbf"); e.remove("DBF");}
            if(x.equals("shx")){ e.remove("SHX"); e.remove("shx");}
            if(x.equals("SHX")){ e.remove("shx"); e.remove("SHX");}
        }
        return e.isEmpty();
    }
}
