package com.project.services;

import com.project.dao.GenericDAOImpl;
import com.project.model.Event;
import com.vividsolutions.jts.geom.Point;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * Created by JORGE-HP on 18/4/2017.
 */
@Service
public class EventServiceImpl implements EventService {

    @Autowired
    private SessionFactory sessionFactory;
    private GenericDAOImpl<Event, Long> eventDAO;

    @PostConstruct
    public void init() {
        eventDAO = new GenericDAOImpl<Event, Long>(sessionFactory, Event.class);
    }

    @Transactional
    public void create(Point point) {
        System.out.print("Hello saving");
        Event event = new Event();
        event.setLocation(point);
        eventDAO.save(event);

    }

    @Transactional
    public List<Point> listAll() {
        return null;
    }
}
