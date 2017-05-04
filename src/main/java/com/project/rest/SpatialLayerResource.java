package com.project.rest;

import com.project.model.transfer.*;
import com.project.services.AttributeService;
import com.project.services.SpatialLayerService;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.geojson.feature.FeatureJSON;
import org.geotools.geojson.geom.GeometryJSON;
import org.geotools.referencing.CRS;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.util.List;

/**
 * Created by JORGE-HP on 29/4/2017.
 */
@RestController
@RequestMapping("/layer")
public class SpatialLayerResource {
    @Autowired
    private SpatialLayerService spatialLayerService;

    @RequestMapping(value="/index", method= RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
    public DataResponse index() {
        BaseResponse response = new BaseResponse();
        LayerDTO layerDTO = new LayerDTO();
        layerDTO.setLayerId(222L);
        layerDTO.setLayerName("Sample Layer");
        layerDTO.setEpsgCode(433);
        DataResponse<LayerDTO> sms = new DataResponse<LayerDTO>();
        sms.setData(layerDTO);
        return sms;
    }

    @RequestMapping(value="/baseLayer", method= RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
    public DataResponse getBaseLayer() {
        DataResponse<LayerDTO> response = new DataResponse<LayerDTO>();
        try {
            LayerDTO layerDTO = spatialLayerService.getBaseLayer();
            response.setData(layerDTO);
        }catch(Exception exp) {
            exp.printStackTrace();
            response.setSuccess(Boolean.FALSE);
            response.setErrorCode(100);
            response.setErrorMessage("No base layer set");
        }
        return response;
    }

    //list all geometries of a layer
    @RequestMapping(value="/{layerId}/listGeometries", method= RequestMethod.GET)
    public void listGeometries(@PathVariable("layerId") Long layerId, HttpServletResponse response){
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
    @RequestMapping(value="/{layerId}/saveGeometries", method= RequestMethod.POST, produces = {MediaType.APPLICATION_JSON_VALUE})
    public BaseResponse saveGeometriesLayer(@PathVariable("layerId") Long layerId, HttpServletRequest request){
        BaseResponse response = new BaseResponse();
        try {
            JSONParser parser = new JSONParser();
            JSONObject obj =(JSONObject) parser.parse(new InputStreamReader(request.getInputStream()));
            FeatureJSON fJSON = new FeatureJSON();
            fJSON.setFeatureType(createDefaultFeatureType());
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
    @RequestMapping(value="/save", method= RequestMethod.POST, produces = {MediaType.APPLICATION_JSON_VALUE})
    public DataResponse saveLayer(@RequestBody LayerDTO layerDTO, HttpSession httpSession) {
        DataResponse dataResponse  = new DataResponse();
        try{
            spatialLayerService.createSpatialLayer((Long)httpSession.getAttribute("projectId"), layerDTO);
            dataResponse.setData(layerDTO);
        }catch (Exception exp) {
            dataResponse.setData(null);
            dataResponse.setSuccess(Boolean.FALSE);
            dataResponse.setErrorCode(1);
            dataResponse.setErrorMessage("No able to save");
        }
        return dataResponse;
    }

    @RequestMapping(value="/update", method= RequestMethod.POST, produces = {MediaType.APPLICATION_JSON_VALUE})
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

    @RequestMapping(value="/listAll", method= RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ListResponse listAll(HttpSession httpSession){
        ListResponse<LayerDTO> response = new ListResponse<LayerDTO>();
        try {
            List<LayerDTO> list = spatialLayerService.list((Long)httpSession.getAttribute("projectId"));
            response.setList(list);
        } catch (Exception e) {
            e.printStackTrace();
            response.setSuccess(Boolean.FALSE);
            response.setErrorCode(90);
            response.setList(null);
            response.setErrorMessage("Not able to list");
        }
        return response;
    }

    @RequestMapping(value="/{layerId}/listAttributes", method= RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ListResponse listAllAttributes(@PathVariable("layerId") Long layerId){
        ListResponse<AttributeDTO> response = new ListResponse<AttributeDTO>();
        try {
            List<AttributeDTO> list = spatialLayerService.listAttributeForLayer(layerId);
            response.setList(list);
        } catch (Exception e) {
            e.printStackTrace();
            response.setSuccess(Boolean.FALSE);
            response.setErrorCode(91);
            response.setList(null);
            response.setErrorMessage("Not able to list attributes");
        }
        return response;
    }

    @RequestMapping(value="/projection", method= RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
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

    private SimpleFeatureType createDefaultFeatureType() throws FactoryException {
        SimpleFeatureTypeBuilder builder = new SimpleFeatureTypeBuilder();
        builder.setName("Location");
        builder.add("geometry", Geometry.class);
        final SimpleFeatureType featureType = builder.buildFeatureType();
        return featureType;
    }
}
