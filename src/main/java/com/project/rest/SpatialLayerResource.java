package com.project.rest;

import com.project.model.transfer.BaseResponse;
import com.project.model.transfer.DataResponse;
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
import org.geotools.geojson.feature.FeatureCollectionHandler;
import org.geotools.referencing.CRS;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.Filter;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.wololo.jts2geojson.GeoJSONReader;
import org.wololo.jts2geojson.GeoJSONWriter;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.charset.Charset;
import java.util.*;

/**
 * Created by JORGE-HP on 29/4/2017.
 */
@RestController
@RequestMapping("/layer")
public class SpatialLayerResource {
    @Autowired
    private SpatialLayerService spatialLayerService;

    //list all geometries of a layer
    @RequestMapping(value="/listGeometries", method= RequestMethod.GET)
    public void list(@RequestParam("layerId") Long layerId, HttpServletResponse response){
        FeatureCollection<SimpleFeatureType, SimpleFeature>  features = spatialLayerService.getLayerInfo(layerId);
        FeatureJSON geojson = new FeatureJSON();
        geojson.setEncodeFeatureCollectionCRS(true);
        try {
            response.reset();
            response.resetBuffer();
            response.setContentType("application/json");
            ServletOutputStream ouputStream = response.getOutputStream();
            geojson.writeFeatureCollection(features, ouputStream);
            ouputStream.flush();
            ouputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    //save all geometries of a layer
    @RequestMapping(value="/{layerId}/saveGeometries", method= RequestMethod.POST)
    public String saveGeometriesLayer(@PathVariable("layerId") Long layerId, HttpServletRequest request){
        try {
            JSONParser parser = new JSONParser();
            JSONObject obj =(JSONObject) parser.parse(new InputStreamReader(request.getInputStream()));
            FeatureJSON fJSON = new FeatureJSON();
            fJSON.setEncodeFeatureCollectionCRS(true);
            fJSON.setEncodeNullValues(true);
            FeatureCollection<SimpleFeatureType, SimpleFeature> features = fJSON.readFeatureCollection(obj.toJSONString());
            LayerDTO layerDTO = new LayerDTO();
            layerDTO.setLayerId(layerId);
            spatialLayerService.persistLayerFeatures(layerDTO, features);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "yes";
    }

    //save layer
    @RequestMapping(value="/save", method= RequestMethod.POST)
    public DataResponse<LayerDTO> saveLayer(@RequestBody LayerDTO layerDTO) {
        DataResponse<LayerDTO> dataResponse  = new DataResponse<LayerDTO>();
        try{
            spatialLayerService.createSpatialLayer(layerDTO);
            dataResponse.setData(layerDTO);
        }catch (Exception exp) {
            dataResponse.setData(null);
            dataResponse.setSuccess(Boolean.FALSE);
            dataResponse.setErrorCode(1);
            dataResponse.setErrorMessage("No able to save");
        }
        return dataResponse;
    }

    @RequestMapping(value="/update", method= RequestMethod.POST)
    public BaseResponse updateLayer(@RequestBody LayerDTO layerDTO) {
        BaseResponse response = new BaseResponse();
        try{
            spatialLayerService.updateSpatialLayer(layerDTO);
        }catch (Exception exp) {
            response.setSuccess(Boolean.FALSE);
            response.setErrorCode(2);
            response.setErrorMessage("No able to update");
        }
        return response;
    }

    @RequestMapping(value="/delete", method= RequestMethod.POST)
    public BaseResponse deleteLayer(@RequestBody LayerDTO layerDTO) {
        BaseResponse response = new BaseResponse();
        try{
            spatialLayerService.deleteSpatialLayer(layerDTO);
        }catch (Exception exp) {
            response.setSuccess(Boolean.FALSE);
            response.setErrorCode(3);
            response.setErrorMessage("No able to delete");
        }
        return response;
    }

    @RequestMapping(value="/listAll", method= RequestMethod.GET)
    public void listAllLayers(HttpServletResponse response){
        FeatureCollection<SimpleFeatureType, SimpleFeature>  features = spatialLayerService.getLayerInfo(1L);
        //JSONParser parser = new JSONParser();
        //Object obj = parser.parse(reader);
        FeatureJSON json = new FeatureJSON();
        json.setEncodeFeatureCRS(true);
        try {
            response.reset();
            response.resetBuffer();
            response.setContentType("application/json");
            ServletOutputStream ouputStream = response.getOutputStream();
            json.writeFeatureCollection(features, ouputStream);
            ouputStream.flush();
            ouputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    private StringBuffer readInputStream(InputStream inputStream) throws IOException {
        StringBuffer buff = new StringBuffer();
        BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
        String str;
        while ((str = in.readLine()) != null) {
            buff.append(str);
        }
        return buff;
    }
}
