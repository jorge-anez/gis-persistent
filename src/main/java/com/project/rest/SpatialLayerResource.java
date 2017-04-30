package com.project.rest;

import com.project.model.transfer.BaseResponse;
import com.project.model.transfer.DataResponse;
import com.project.model.transfer.LayerDTO;
import com.project.services.SpatialLayerService;
import org.geotools.feature.FeatureCollection;
import org.geotools.geojson.feature.FeatureJSON;
import org.geotools.referencing.CRS;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

/**
 * Created by JORGE-HP on 29/4/2017.
 */
@RestController
@RequestMapping("/layer")
public class SpatialLayerResource {
    @Autowired
    private SpatialLayerService spatialLayerService;

    //list all geometries of a layer
    @RequestMapping(value="/{layerId}/listGeometries", method= RequestMethod.GET)
    public void list(@PathVariable("layerId") Long layerId, HttpServletResponse response){
        try {
            FeatureCollection<SimpleFeatureType, SimpleFeature>  features = spatialLayerService.getLayerInfo(layerId);
            FeatureJSON geojson = new FeatureJSON();
            geojson.setEncodeFeatureCollectionCRS(true);
            response.reset();
            response.resetBuffer();
            response.setContentType("application/json");
            ServletOutputStream ouputStream = response.getOutputStream();
            geojson.writeFeatureCollection(features, ouputStream);
            ouputStream.flush();
            ouputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
            String sms = "{\"success\": false}";
            try {
                response.getOutputStream().write(sms.getBytes());
                response.getOutputStream().flush();
                response.getOutputStream().close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }


    //save all geometries of a layer
    @RequestMapping(value="/{layerId}/saveGeometries", method= RequestMethod.POST)
    public BaseResponse saveGeometriesLayer(@PathVariable("layerId") Long layerId, HttpServletRequest request){
        BaseResponse response = new BaseResponse();
        try {
            JSONParser parser = new JSONParser();
            JSONObject obj =(JSONObject) parser.parse(new InputStreamReader(request.getInputStream()));
            FeatureJSON fJSON = new FeatureJSON();
            fJSON.setEncodeFeatureCollectionCRS(true);
            fJSON.setEncodeFeatureCRS(true);
            fJSON.setEncodeNullValues(true);
            FeatureCollection<SimpleFeatureType, SimpleFeature> features = fJSON.readFeatureCollection(obj.toJSONString());
            LayerDTO layerDTO = new LayerDTO();
            layerDTO.setLayerId(layerId);
            spatialLayerService.createLayerFeatures(layerDTO, features);
        } catch (Exception e) {
            e.printStackTrace();
            response.setSuccess(Boolean.FALSE);
            response.setErrorCode(4);
            response.setErrorMessage("Not able to save geometries");
        }
        return response;
    }

    //update all geometries of a layer
    @RequestMapping(value="/{layerId}/updateGeometries", method= RequestMethod.POST)
    public String upadteGeometriesLayer(@PathVariable("layerId") Long layerId, HttpServletRequest request){
        //TODO
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
        try {
            FeatureCollection<SimpleFeatureType, SimpleFeature>  features = spatialLayerService.getLayerInfo(1L);
            //JSONParser parser = new JSONParser();
            //Object obj = parser.parse(reader);
            FeatureJSON json = new FeatureJSON();
            json.setEncodeFeatureCRS(true);
            response.reset();
            response.resetBuffer();
            response.setContentType("application/json");
            ServletOutputStream ouputStream = response.getOutputStream();
            json.writeFeatureCollection(features, ouputStream);
            ouputStream.flush();
            ouputStream.close();
        } catch (Exception e) {
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
