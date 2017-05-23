package com.project.rest;

import com.project.services.SpatialDataService;
import com.project.services.SpatialLayerService;
import com.project.services.SpatialLayerStyleService;
import com.vividsolutions.jts.geom.*;
import com.vividsolutions.jts.geom.Polygon;
import org.apache.commons.io.IOUtils;
import org.geotools.data.DataUtilities;
import org.geotools.feature.DefaultFeatureCollection;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.map.FeatureLayer;
import org.geotools.map.Layer;
import org.geotools.map.MapContent;
import org.geotools.renderer.GTRenderer;
import org.geotools.renderer.lite.StreamingRenderer;
import org.geotools.sld.SLDConfiguration;
import org.geotools.styling.*;
import org.geotools.xml.Configuration;
import org.geotools.xml.Parser;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/style")
public class StyleResource {

    @Autowired
    private SpatialLayerStyleService spatialLayerStyleService;
    @Autowired
    private SpatialLayerService spatialLayerService;
    @Autowired
    private SpatialDataService spatialDataService;

    @Value("${dir.upload.cache}")
    private String dirTemp;

    @RequestMapping(value = "/default", method=RequestMethod.GET, produces = MediaType.IMAGE_PNG_VALUE)
    public void downloadLayer(HttpServletResponse response){
        try {
            //SLDParser stylereader = new SLDParser(styleFactory, url);
            // create the parser with the sld configuration

            Configuration configuration = new SLDConfiguration();
            Parser parser = new Parser(configuration);
            InputStream xml = new FileInputStream(dirTemp + "/defaultStyle.sld");
            StyledLayerDescriptor sld = (StyledLayerDescriptor) parser.parse(xml);
            xml.close();

            GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory();
            Coordinate[] coords  = new Coordinate[] {new Coordinate(0, 2), new Coordinate(2, 0), new Coordinate(8, 6) };
            LineString line = geometryFactory.createLineString(coords);


            Coordinate[] coords1  = new Coordinate[] {new Coordinate(4, 0), new Coordinate(2, 2),
                            new Coordinate(4, 4), new Coordinate(6, 2), new Coordinate(4, 0) };
            Polygon polygon = geometryFactory.createPolygon(coords1);

            SimpleFeatureType type = DataUtilities.createType("location","geom:LineString,name:String");
            SimpleFeatureType type1 = DataUtilities.createType("location","geom:Polygon,name:String");
            DefaultFeatureCollection featureCollection = new DefaultFeatureCollection("style");

            SimpleFeatureBuilder builder = new SimpleFeatureBuilder(type);
            builder.set("geom", line);
            builder.set("name", "line");
            featureCollection.add(builder.buildFeature("1"));

            builder = new SimpleFeatureBuilder(type1);
            builder.set("geom", polygon);
            builder.set("name", "polygon");
            featureCollection.add(builder.buildFeature("2"));

            MapContent map = new MapContent();
            map.setTitle("Styling");



            FeatureTypeStyle[] styles = SLD.featureTypeStyles(sld);
            FeatureTypeStyle typeStyle = styles[0];//SLD.featureTypeStyle(sld, type);

            StyleBuilder styleBuilder = new StyleBuilder();
            Style style = styleBuilder.createStyle();
            style.featureTypeStyles().add(typeStyle);
            style.featureTypeStyles().add(styles[1]);
            //style.featureTypeStyles().add(styles[1]);
            String str = "joder";

            Layer layer = new FeatureLayer(featureCollection, style);
            map.addLayer(layer);


            GTRenderer renderer = new StreamingRenderer();
            renderer.setMapContent(map);

            int imageWidth = 250;
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
            response.setContentType("image/png");
            ServletOutputStream ouputStream = response.getOutputStream();
            ImageIO.write(image, "png", ouputStream);
            ouputStream.flush();
            ouputStream.close();
            /*
            String string = "${Point.stroke}ksklsk${Point.fill}";
            List<String> list = find(string);
            Map<String, String> result =  spatialLayerStyleService.getSpatialLayerStyles(0L, list);
            System.out.println(replaceVariables(string, result));
            */

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @RequestMapping(value = "/layer/{layerId}/sld", method=RequestMethod.GET, produces = MediaType.IMAGE_PNG_VALUE)
    public void getSLD(Long layerId, HttpServletResponse response){
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


}
