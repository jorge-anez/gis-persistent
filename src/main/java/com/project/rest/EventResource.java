package com.project.rest;

import com.project.services.EventService;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/event")
public class EventResource {
    @Autowired
    EventService eventService;
    private WKTReader reader = new WKTReader();

    @RequestMapping(value = "/index", produces = MediaType.TEXT_HTML_VALUE)
    public String greeting() {
        return "Hello, it is person rest API";
    }

    @RequestMapping(value = "/save",method = RequestMethod.POST)
    public void save() {
         String format = String.format("POINT(%s %s)", 4, 65);
        Point point = null;
        try {
            point = (Point) reader.read(format);
            eventService.create(point);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        // this is ot the most elegant way to build a point

    }

    @RequestMapping("/listAll")
    public List<Point> getAll() {

        return null;
    }
}

/*
@RequestMapping(value = "/person/save"
            , method = RequestMethod.POST
//            , produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE}
//            , consumes = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE}
    )
 */