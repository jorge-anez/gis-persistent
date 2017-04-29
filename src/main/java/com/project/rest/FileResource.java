package com.project.rest;

import com.project.model.transfer.FileUploadForm;
import com.project.model.transfer.LayerDTO;
import com.project.services.SpatialDataService;
import com.project.services.SpatialLayerService;
import com.project.utils.SpacialFileUtils;
import com.vividsolutions.jts.geom.Geometry;
import org.geotools.data.DataStore;
import org.geotools.data.DataStoreFinder;
import org.geotools.data.FeatureSource;
import org.geotools.data.memory.MemoryFeatureCollection;
import org.geotools.data.shapefile.dbf.DbaseFileReader;

import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.geojson.feature.FeatureJSON;
import org.geotools.referencing.CRS;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.json.simple.parser.JSONParser;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.Filter;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.charset.Charset;
import java.util.*;

/**
 * Created by user on 4/18/2017.
 */
@RestController
@RequestMapping("/file")
public class FileResource {

    @Autowired
    private SpatialLayerService spatialLayerService;
    @Autowired
    private SpatialDataService spatialDataService;

    @Value("${dir.upload.cache}")
    private String dirTemp;

    @RequestMapping(value="/upload", method= RequestMethod.POST)
    public String handleFileUpload(@ModelAttribute("uploadFile") FileUploadForm files){
        List<String> pathFiles = new ArrayList<String>();
        String filePath;
        if (!files.getFiles().isEmpty()) {
            try {
                File file;
                for (MultipartFile e: files.getFiles()) {
                    file = new File(dirTemp + e.getOriginalFilename());
                    e.transferTo(file);
                    pathFiles.add(e.getOriginalFilename());
                }
                //FileProcesor.processShapeFile(pathFiles);
                Map<String, String> mapFiles = SpacialFileUtils.getFileExtension(pathFiles);
                    String path = dirTemp + mapFiles.get("dbf") +".dbf";
                    String p = dirTemp + mapFiles.get("shp") +".shp";
                //readDBF(path);
                FeatureCollection<SimpleFeatureType, SimpleFeature> collection = readSHP(p);
                //spatialDataService.persistFeatures(featureIterator);
                LayerDTO layerDTO = new LayerDTO();
                layerDTO.setLayerName("Layer 1");
                layerDTO.setEpsgCode(CRS.lookupEpsgCode(collection.getSchema().getCoordinateReferenceSystem(), true));
                spatialLayerService.persistLayerFeatures(layerDTO, collection);
                return "You successfully uploaded ";
            } catch (Exception e) {
                return "You failed to upload  => " + e.getMessage();
            }

        } else
            return "You failed to upload  because the file was empty.";

    }


    @RequestMapping(value="/temp/upload", method= RequestMethod.POST)
    public void tempUpload(@ModelAttribute("uploadFile") FileUploadForm files, HttpServletResponse response){
        List<String> pathFiles = new ArrayList<String>();
        if (!files.getFiles().isEmpty()) {
            try {
                File file;
                for (MultipartFile e: files.getFiles()) {
                    file = new File(dirTemp + e.getOriginalFilename());
                    e.transferTo(file);
                    pathFiles.add(e.getOriginalFilename());
                }
                Map<String, String> mapFiles = SpacialFileUtils.getFileExtension(pathFiles);
                String p = dirTemp + mapFiles.get("shp") + ".shp";
                FeatureCollection<SimpleFeatureType, SimpleFeature> collection = readSHP(p);
                FeatureJSON json = new FeatureJSON();
                json.setEncodeFeatureCRS(true);
                response.reset();
                response.resetBuffer();
                response.setContentType("application/json");
                ServletOutputStream ouputStream = response.getOutputStream();
                json.writeFeatureCollection(collection, ouputStream);
                ouputStream.flush();
                ouputStream.close();
            } catch (Exception e) {

            }
        }
    }

    public FeatureIterator<SimpleFeature> readJSON(InputStreamReader reader) throws Exception{
            System.out.println("INTO PARSINGJSON");
            JSONParser parser = new JSONParser();
            Object obj = parser.parse(reader);
            FeatureJSON fJSON = new FeatureJSON();
            FeatureCollection fc = fJSON.readFeatureCollection(obj.toString());
            FeatureIterator<SimpleFeature> features = fc.features();

        Collection<SimpleFeature> featureCollection = new MemoryFeatureCollection(createFeatureType());
        return features;
    }

    private static SimpleFeatureType createFeatureType() {

        SimpleFeatureTypeBuilder builder = new SimpleFeatureTypeBuilder();
        builder.setName("Location");
        builder.setCRS(DefaultGeographicCRS.WGS84); // <- Coordinate reference system

        // add attributes in order
        builder.add("the_geom", Geometry.class);
        builder.length(15).add("Name", String.class); // <- 15 chars width for name field
        // build the type
        final SimpleFeatureType LOCATION = builder.buildFeatureType();

        return LOCATION;
    }


    public FeatureCollection<SimpleFeatureType, SimpleFeature> readSHP(String p) throws Exception {
        File file = new File(p);
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("url", file.toURI().toURL());
        DataStore dataStore = DataStoreFinder.getDataStore(map);
        String typeName = dataStore.getTypeNames()[0];
        FeatureSource<SimpleFeatureType, SimpleFeature> source = dataStore.getFeatureSource(typeName);
        Filter filter = Filter.INCLUDE; // ECQL.toFilter("BBOX(THE_GEOM, 10,20,30,40)")
        SimpleFeatureType schema = source.getSchema();
        FeatureCollection<SimpleFeatureType, SimpleFeature> collection = source.getFeatures(filter);
        dataStore.dispose();
        return collection;
    }

// Here's an example that should work (warning, I haven't
// tried to compile this).  The example assumes the first field has a
// character data type and the second has a numeric data type:
    public void readDBF(String path) throws IOException {

        FileInputStream fis = new FileInputStream( path );
        DbaseFileReader dbfReader =  new DbaseFileReader(fis.getChannel(),
                false,  Charset.forName("ISO-8859-1"));

        while ( dbfReader.hasNext() ){
            final Object[] fields = dbfReader.readEntry();

            //String field1 = (String) fields[0];
            //Integer field2 = (Integer) fields[1];

            System.out.println("DBF field : " + Arrays.toString(fields));
            //System.out.println("DBF field 2 value is: " + field2);
        }

        dbfReader.close();
        fis.close();

    }

    @RequestMapping(value="/list", method= RequestMethod.GET)
    public void list(HttpServletResponse response){
        FeatureCollection<SimpleFeatureType, SimpleFeature>  features = spatialLayerService.getLayerInfo(1L);
        //JSONParser parser = new JSONParser();
        //Object obj = parser.parse(reader);
        FeatureJSON json = new FeatureJSON();
        //json.setEncodeFeatureCRS(true);
        try {
            response.reset();
            response.resetBuffer();
            response.setContentType("application/json");
            ServletOutputStream ouputStream = response.getOutputStream();
            json.writeFeatureCollection(features, ouputStream);
            ouputStream.flush();
            ouputStream.close();
            //String str = new String(stream.toByteArray());
            //System.out.println(str);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //FeatureCollection fc = fJSON.readFeatureCollection(obj.toString());
        //FeatureIterator<SimpleFeature> features = fc.features();
    }

    @RequestMapping(value="/projection", method= RequestMethod.GET)
    public void getProjection(HttpServletResponse response){
        FeatureJSON json = new FeatureJSON();
        LayerDTO layerDTO = spatialLayerService.getLayerById(1L);
        try {
            response.reset();
            response.resetBuffer();
            response.setContentType("application/json");
            ServletOutputStream ouputStream = response.getOutputStream();
            json.writeCRS(CRS.decode("EPSG:"+layerDTO.getEpsgCode()), ouputStream);
            ouputStream.flush();
            ouputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAuthorityCodeException e) {
            e.printStackTrace();
        } catch (FactoryException e) {
            e.printStackTrace();
        }
    }
}
