package com.project.rest;

import com.project.model.transfer.FileUploadForm;
import com.project.model.transfer.LayerDTO;
import com.project.services.SpatialDataService;
import com.project.services.SpatialLayerService;
import com.project.utils.SpacialFileUtils;
import com.vividsolutions.jts.geom.Geometry;
import org.apache.commons.io.IOUtils;
import org.geotools.data.*;
import org.geotools.data.memory.MemoryFeatureCollection;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.shapefile.ShapefileDataStoreFactory;
import org.geotools.data.shapefile.dbf.DbaseFileReader;

import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.data.simple.SimpleFeatureStore;
import org.geotools.feature.DefaultFeatureCollection;
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
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.charset.Charset;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

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

    @RequestMapping(value="/layer/{layerId}/download", method= RequestMethod.GET)
    public void listGeometries(@PathVariable("layerId") Long layerId, HttpServletResponse response){
        try {
            LayerDTO layerDTO = spatialLayerService.getLayerById(layerId);
            FeatureCollection<SimpleFeatureType, SimpleFeature>  features = spatialLayerService.getLayerInfo(layerId);
            FeatureIterator<SimpleFeature> featureIterator = features.features();
            Map<String, DefaultFeatureCollection> collectionMap = new HashMap<String, DefaultFeatureCollection>();

            while (featureIterator.hasNext()) {
                SimpleFeature feature = featureIterator.next();
                String classType = feature.getDefaultGeometry().getClass().getSimpleName().toLowerCase();
                DefaultFeatureCollection collection = collectionMap.get(classType);
                if(collection == null) {
                    collection = new DefaultFeatureCollection(null, features.getSchema());
                    collectionMap.put(classType, collection);
                }
                collection.add(feature);
            }

            List<String> fileNames = new ArrayList<String>();
            String layerName = layerDTO.getLayerName().replaceAll("\\s+","");
            for(Map.Entry<String, DefaultFeatureCollection> e: collectionMap.entrySet()) {
                String fileName = layerName + "_" + e.getKey();
                writeSHP(e.getValue(), dirTemp,  fileName);
                for(String ext : Arrays.asList(".shp", ".dbf", ".shx", ".prj", ".fix")) {
                    fileNames.add(fileName + ext);
                }
            }
            zipFile(dirTemp, layerName, fileNames);

            response.reset();
            response.resetBuffer();
            response.setContentType("application/zip");
            ServletOutputStream ouputStream = response.getOutputStream();
            InputStream inputStream = new FileInputStream(dirTemp + "/" + layerName + ".zip");
            IOUtils.copy(inputStream, ouputStream);
            ouputStream.flush();
            ouputStream.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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
                layerDTO.setLayerName(mapFiles.get("shp"));
                layerDTO.setEpsgCode(CRS.lookupEpsgCode(collection.getSchema().getCoordinateReferenceSystem(), true));
                spatialLayerService.createLayerFeatures(null, collection, null);
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
                json.setEncodeFeatureCollectionCRS(true);
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

    @RequestMapping(value="/temp/upload/zip", method= RequestMethod.POST)
    public void tempUploadZip(@ModelAttribute("uploadFile") MultipartFile file, HttpServletResponse response){
        if (!file.isEmpty() && file.getOriginalFilename().endsWith(".zip")) {
            try {
                File f = new File(dirTemp + file.getOriginalFilename());
                file.transferTo(f);
                List<String> shapeNames = unZip(dirTemp, file.getOriginalFilename());
                Collection<FeatureCollection> collection = new ArrayList<FeatureCollection>();
                for(String e: shapeNames) {
                    FeatureCollection featureCollection = readSHP(dirTemp + "/" + e);
                    collection.add(featureCollection);
                }
                //System.out.println(shapeNames);
                /*
                Map<String, String> mapFiles = SpacialFileUtils.getFileExtension(pathFiles);
                String p = dirTemp + mapFiles.get("shp") + ".shp";
                FeatureCollection<SimpleFeatureType, SimpleFeature> collection = readSHP(p);
                FeatureJSON json = new FeatureJSON();
                json.setEncodeFeatureCollectionCRS(true);
                response.reset();
                response.resetBuffer();
                response.setContentType("application/json");
                ServletOutputStream ouputStream = response.getOutputStream();
                json.writeFeatureCollection(collection, ouputStream);
                ouputStream.flush();
                ouputStream.close();
                */
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
        //FeatureCollection<SimpleFeatureType, SimpleFeature>  features = spatialLayerService.getLayerInfo(1L);
        //JSONParser parser = new JSONParser();
        //Object obj = parser.parse(reader);
        FeatureJSON json = new FeatureJSON();
        //json.setEncodeFeatureCRS(true);
        try {
            response.reset();
            response.resetBuffer();
            response.setContentType("application/json");
            ServletOutputStream ouputStream = response.getOutputStream();
            //json.writeFeatureCollection(features, ouputStream);
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

    public void writeSHP(FeatureCollection<SimpleFeatureType, SimpleFeature> collection, String dir, String fileName) throws Exception{
        String saveFilepath = dir + fileName + ".shp";
        File theFile = new File(saveFilepath);
        ShapefileDataStoreFactory dataStoreFactory = new ShapefileDataStoreFactory();
        Map<String, Serializable> params = new HashMap<String, Serializable>();
        params.put("url", theFile.toURI().toURL());
        params.put("create spatial index", Boolean.TRUE);
        ShapefileDataStore newDataStore = (ShapefileDataStore) dataStoreFactory.createNewDataStore(params);
        newDataStore.createSchema(collection.getSchema());
        String typeName = newDataStore.getTypeNames()[0];
        SimpleFeatureSource featureSource = newDataStore.getFeatureSource(typeName);
        try {
            if(!SimpleFeatureStore.class.isInstance(featureSource)) {
                throw new Exception(typeName + " does not support read/write access");
            } else {
                SimpleFeatureStore featureStore = (SimpleFeatureStore) featureSource;
                Transaction transaction = new DefaultTransaction("create");
                featureStore.setTransaction(transaction);
                try {
                    featureStore.addFeatures(collection);
                    transaction.commit();
                    transaction.close();
                } catch(Exception e) {
                    transaction.rollback();
                    transaction.close();
                    throw e;
                }
            }
        } catch(Exception e) {
            throw e;
        } finally {
            newDataStore.dispose();
        }
    }

    void zipFile(String dir, String layerName, List<String> fileNames) {
        byte[] buffer = new byte[1024];
        try{
            File file = new File(dir + "/" + layerName + ".zip");
            file.createNewFile();
            FileOutputStream fos = new FileOutputStream(file);
            ZipOutputStream zos = new ZipOutputStream(fos);
            for(String fileName: fileNames) {
                ZipEntry ze= new ZipEntry(fileName);
                zos.putNextEntry(ze);
                FileInputStream in = new FileInputStream(dir + "/" + fileName);
                int len;
                while ((len = in.read(buffer)) > 0) {
                    zos.write(buffer, 0, len);
                }
                in.close();
            }
            zos.finish(); // good practice
            zos.close();
        }catch(IOException ex){
            ex.printStackTrace();
        }
    }

    public List<String> unZip(String dirTemp, String zipPath) throws IOException {
        List<String> shapeNames = new ArrayList<String>();
        ZipFile zipFile = new ZipFile(dirTemp + "/" +zipPath);
        Enumeration<? extends ZipEntry> entries = zipFile.entries();
        while(entries.hasMoreElements()){
            ZipEntry entry = entries.nextElement();
            InputStream inputStream = zipFile.getInputStream(entry);
            OutputStream outputStream = new FileOutputStream(dirTemp + "/" + entry.getName());
            IOUtils.copy(inputStream, outputStream);
            if(entry.getName().endsWith(".shp"))
                shapeNames.add(entry.getName());
            inputStream.close();
            outputStream.close();
        }
        zipFile.close();
        return shapeNames;
    }
}
