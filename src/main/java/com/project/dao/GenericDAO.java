package com.project.dao;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;

import java.util.List;

/**
 * Created by user on 3/21/2017.
 */
public interface GenericDAO<T, K> { //clase generica tabla, id
    K save(T t);// devuelve key de la entidad
    void update(T t);
    void remove(T t);
    T find(K key);
    List<T> findAll();
    List<T> findByCriteria(Criterion... criterions);
    public T findByCriteriauniqueResult(Criterion... criterions);
    Query getNamedQuery(String name);

    SessionFactory getSessionFactory();
    void setClassType(Class<T> personClass);
}

