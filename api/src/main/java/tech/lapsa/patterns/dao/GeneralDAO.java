package tech.lapsa.patterns.dao;

import java.io.Serializable;
import java.util.Collection;

public interface GeneralDAO<T extends Serializable, I extends Serializable> {

    T getById(I id) throws IllegalArgumentException, NotFound;

    <ET extends T> ET save(ET entity) throws IllegalArgumentException;

    <ET extends T> ET restore(ET entity) throws IllegalArgumentException, NotFound;

    <ET extends T> Collection<ET> saveAll(Collection<ET> entities) throws IllegalArgumentException;

    void deleteById(I id) throws IllegalArgumentException, NotFound;

    <ET extends T> void delete(ET entity) throws IllegalArgumentException, NotFound;

    <ET extends T> ET detach(ET entity) throws IllegalArgumentException, NotFound;
}
