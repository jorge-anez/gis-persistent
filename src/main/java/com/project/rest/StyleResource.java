package com.project.rest;

import com.project.model.transfer.BaseResponse;
import com.project.model.transfer.LayerDTO;
import com.project.services.SpatialDataService;
import com.project.services.SpatialLayerService;
import com.project.services.SpatialLayerStyleService;
import com.vividsolutions.jts.geom.*;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import org.apache.commons.io.IOUtils;
import org.geotools.data.DataUtilities;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.feature.DefaultFeatureCollection;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.map.FeatureLayer;
import org.geotools.map.Layer;
import org.geotools.map.MapContent;
import org.geotools.referencing.CRS;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.geotools.renderer.GTRenderer;
import org.geotools.renderer.lite.StreamingRenderer;
import org.geotools.sld.SLDConfiguration;
import org.geotools.styling.*;
import org.geotools.xml.Configuration;
import org.geotools.xml.Parser;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory;
import org.opengis.filter.FilterFactory2;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/style")
public class StyleResource {
    static StyleFactory styleFactory = CommonFactoryFinder.getStyleFactory();
    static FilterFactory filterFactory = CommonFactoryFinder.getFilterFactory();

    @Autowired
    private SpatialLayerStyleService spatialLayerStyleService;
    @Autowired
    private SpatialLayerService spatialLayerService;
    @Autowired
    private SpatialDataService spatialDataService;

    @Value("${dir.upload.cache}")
    private String dirTemp;

    @Value(value = "classpath:map-styles/clasificacion-sectorial.sld")
    private URL reportsSLD;

    @RequestMapping(value = "/legend", method=RequestMethod.GET, produces = MediaType.IMAGE_PNG_VALUE)
    public void getPatternImage(@RequestParam String code, @RequestParam String geoType, HttpServletResponse response) {
        GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory();
        Coordinate[] coords;
        Geometry geom = null;
        SimpleFeatureTypeBuilder typeBuilder = new SimpleFeatureTypeBuilder();
        typeBuilder.setName("Location");
        typeBuilder.setCRS(DefaultGeographicCRS.WGS84);
        typeBuilder.add("geometryType", String.class);

        SimpleFeatureType featureType = null;
        SimpleFeatureBuilder featureBuilder = null;

        if(geoType.equals("Line")){
            coords  = new Coordinate[] {new Coordinate(2, 5), new Coordinate(18, 5)};
            geom = geometryFactory.createLineString(coords);
            typeBuilder.add("the_geom", LineString.class);
            featureType = typeBuilder.buildFeatureType();

            featureBuilder = new SimpleFeatureBuilder(featureType);
            featureBuilder.set("the_geom", geom);
            featureBuilder.set("geometryType", "Line");
        }else if(geoType.equals("Polygon")) {
            coords  = new Coordinate[] {new Coordinate(2, 2), new Coordinate(18, 2), new Coordinate(18, 8), new Coordinate(2, 8), new Coordinate(2, 2)};
            geom = geometryFactory.createPolygon(coords);
            typeBuilder.add("the_geom", Polygon.class);
            featureType = typeBuilder.buildFeatureType();

            featureBuilder = new SimpleFeatureBuilder(featureType);
            featureBuilder.set("the_geom", geom);
            featureBuilder.set("geometryType", "Polygon");
        } else {
            geom = geometryFactory.createPoint(new Coordinate(10, 5));
            typeBuilder.add("the_geom", Point.class);
            featureType = typeBuilder.buildFeatureType();

            featureBuilder = new SimpleFeatureBuilder(featureType);
            featureBuilder.set("the_geom", geom);
            featureBuilder.set("geometryType", "Point");
        }

        DefaultFeatureCollection featureCollection = new DefaultFeatureCollection();
        SimpleFeature feature = featureBuilder.buildFeature("1L");
        featureCollection.add(feature);

        LayerDTO layerDTO = spatialLayerService.getClasificacionSectorialLayer();

        try{
            String stringSLD = spatialLayerStyleService.readSLDStyle(layerDTO.getLayerId());
            StringReader reader = new StringReader(stringSLD);
            SLDParser stylereader = new SLDParser(styleFactory, reader);
            Style[] style = stylereader.readXML();

            MapContent map = new MapContent();
            map.setTitle("Styling");

            //Layer layer = new FeatureLayer(features, style[0]);
            map.addLayer(new FeatureLayer(featureCollection, style[0]));
            //map.addLayer(new FeatureLayer(resultFeatures, style[1]));

            GTRenderer renderer = new StreamingRenderer();
            renderer.setMapContent(map);

            int imageWidth = 500;
            Rectangle imageBounds = null;
            ReferencedEnvelope mapBounds = new ReferencedEnvelope(0.0, 20.0, 0.0, 10.0, DefaultGeographicCRS.WGS84);
            double heightToWidth = mapBounds.getSpan(1) / mapBounds.getSpan(0);
            imageBounds = new Rectangle(0, 0, imageWidth, (int) Math.round(imageWidth * heightToWidth));

            BufferedImage image = new BufferedImage(imageBounds.width, imageBounds.height, BufferedImage.TYPE_INT_ARGB);



            Graphics2D gr = image.createGraphics();

            gr.setComposite(AlphaComposite.Clear);
            gr.setPaint(Color.BLUE);

            gr.fill(imageBounds);
            renderer.paint(gr, imageBounds, mapBounds);

            response.reset();
            response.resetBuffer();
            response.setContentType("image/png");
            ServletOutputStream ouputStream = response.getOutputStream();
            ImageIO.write(image, "png", ouputStream);
            ouputStream.flush();
            ouputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @RequestMapping(value = "/default", method=RequestMethod.GET, produces = MediaType.IMAGE_PNG_VALUE)
    public void downloadLayer(@RequestParam List<String> geoCodes, HttpServletResponse response){
        try {
            LayerDTO layerDTO = spatialLayerService.getBaseLayer();
            FeatureCollection<SimpleFeatureType, SimpleFeature> features = spatialLayerService.getLayerInfo(layerDTO.getLayerId());
            FilterFactory2 ff = CommonFactoryFinder.getFilterFactory2();
            List<Filter> match = new ArrayList<Filter>();
            for (String geoCode : geoCodes) {
                Filter aMatch = ff.equals(ff.property("CODE"), ff.literal(geoCode));
                match.add(aMatch);
            }

            Filter filter = ff.or(match);
            FeatureCollection<SimpleFeatureType, SimpleFeature> resultFeatures = features.subCollection(filter);

            String result = spatialLayerStyleService.readSLDStyle(layerDTO.getLayerId());
            StringReader reader = new StringReader(result);
            SLDParser stylereader = new SLDParser(styleFactory, reader);
            Style[] style = stylereader.readXML();

            //Style s = style[0];
            System.out.println(resultFeatures.size());

            MapContent map = new MapContent();
            map.setTitle("Styling");

            //Layer layer = new FeatureLayer(features, style[0]);
            map.addLayer(new FeatureLayer(features, style[0]));
            //map.addLayer(new FeatureLayer(resultFeatures, style[1]));

            GTRenderer renderer = new StreamingRenderer();
            renderer.setMapContent(map);

            int imageWidth = 680;
            Rectangle imageBounds = null;
            ReferencedEnvelope mapBounds = null;
            mapBounds = map.getMaxBounds();
            double heightToWidth = mapBounds.getSpan(1) / mapBounds.getSpan(0);
            imageBounds = new Rectangle(0, 0, imageWidth, (int) Math.round(imageWidth * heightToWidth));
            BufferedImage image = new BufferedImage(imageBounds.width, imageBounds.height, BufferedImage.TYPE_INT_RGB);

            Graphics2D gr = image.createGraphics();
            gr.setPaint(Color.WHITE);
            gr.fill(imageBounds);
            renderer.paint(gr, imageBounds, mapBounds);

            response.reset();
            response.resetBuffer();
            response.setContentType("image/jpg");
            ServletOutputStream ouputStream = response.getOutputStream();
            ImageIO.write(image, "jpg", ouputStream);
            ouputStream.flush();
            ouputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @RequestMapping(value = "/layer/baseSLD", method=RequestMethod.GET, produces = MediaType.APPLICATION_XML_VALUE)
    public void getBaseSLD(HttpServletResponse response){
        try {
            String result = spatialLayerStyleService.readBaseSLDStyle();
            response.reset();
            response.resetBuffer();
            response.setContentType("application/xml");
            ServletOutputStream ouputStream = response.getOutputStream();
            IOUtils.write(result, ouputStream);
            ouputStream.flush();
            ouputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    @RequestMapping(value = "/layer/{layerId}/sld", method=RequestMethod.GET, produces = MediaType.APPLICATION_XML_VALUE)
    public void getSLD(@PathVariable("layerId") Long layerId, HttpServletResponse response){
        try {
            String result = spatialLayerStyleService.readSLDStyle(layerId);
            response.reset();
            response.resetBuffer();
            response.setContentType("application/xml");
            ServletOutputStream ouputStream = response.getOutputStream();
            IOUtils.write(result, ouputStream);
            ouputStream.flush();
            ouputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @RequestMapping(value = "/layer/{layerId}/geometryType/{geometryType}/save", method=RequestMethod.POST)
    public BaseResponse saveStyleSLD(@PathVariable("layerId") Long layerId, @PathVariable("geometryType") String geometryType, @RequestBody Map<String, String> attrs){
        BaseResponse response = new BaseResponse();
        try {
            spatialLayerStyleService.persistStyle(attrs, layerId, geometryType);
        } catch (Exception e) {
            e.printStackTrace();
            response.setSuccess(Boolean.FALSE);
        }
        return response;
    }


}
