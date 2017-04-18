package com.project.services;

import com.vividsolutions.jts.geom.Point;

import java.util.List;

public interface EventService {
    void create(Point point);
    List<Point> listAll();
}
