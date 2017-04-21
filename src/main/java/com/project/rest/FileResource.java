package com.project.rest;

import com.project.services.EventService;
import com.project.services.SpatialDataService;
import com.project.utils.FileProcesor;
import com.project.utils.SpacialFileUtils;
import com.vividsolutions.jts.geom.Geometry;
import org.apache.commons.io.FilenameUtils;
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
import org.opengis.feature.Feature;
import org.opengis.feature.Property;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.Filter;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
    EventService eventService;

    @Autowired
    SpatialDataService spatialDataService;

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
                FeatureIterator<SimpleFeature> featureIterator = readSHP(p);
                spatialDataService.persistFeatures(featureIterator);
                return "You successfully uploaded ";
            } catch (Exception e) {
                return "You failed to upload  => " + e.getMessage();
            }

        } else
            return "You failed to upload  because the file was empty.";

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


    public FeatureIterator<SimpleFeature> readSHP(String p) throws Exception {
        File file = new File(p);
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("url", file.toURI().toURL());
        DataStore dataStore = DataStoreFinder.getDataStore(map);
        String typeName = dataStore.getTypeNames()[0];

        FeatureSource<SimpleFeatureType, SimpleFeature> source = dataStore.getFeatureSource(typeName);
        Filter filter = Filter.INCLUDE; // ECQL.toFilter("BBOX(THE_GEOM, 10,20,30,40)")

        //SimpleFeatureType schema = source.getSchema();
        //CoordinateReferenceSystem sourceCRS = CRS.decode("EPSG:5352");
        //System.out.println(sourceCRS.toString());


        FeatureCollection<SimpleFeatureType, SimpleFeature> collection = source.getFeatures(filter);
        FeatureIterator<SimpleFeature> features = collection.features();
        return features;

        /*
        while (features.hasNext()) {
            SimpleFeature feature = features.next();

            for (Property e: feature.getProperties()){
                System.out.println(e.getName() + " => " + e.getValue());
            }
            //System.out.println(feature.getDefaultGeometryProperty().getValue());
        }
        dataStore.dispose();
        */
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
    public String list(@ModelAttribute("uploadFile") FileUploadForm files){
        FeatureCollection  features = spatialDataService.getSpacialData();
        System.out.println(features.size());

        //JSONParser parser = new JSONParser();
        //Object obj = parser.parse(reader);
        FeatureJSON json = new FeatureJSON();

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        try {
            json.writeFeatureCollection(features, stream);
            String str = new String(stream.toByteArray());
            System.out.println(str);
        } catch (IOException e) {
            e.printStackTrace();
        }


        //FeatureCollection fc = fJSON.readFeatureCollection(obj.toString());
        //FeatureIterator<SimpleFeature> features = fc.features();
        return "hello";
    }

}
