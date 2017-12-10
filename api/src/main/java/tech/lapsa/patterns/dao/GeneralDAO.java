package tech.lapsa.patterns.dao;

import java.io.Serializable;
import java.util.Collection;

public interface GeneralDAO<T extends Serializable, I extends Serializable> {

    T getById(I id) throws NotFound;

    T getByIdByPassCache(I id) throws NotFound;

    <ET extends T> ET save(ET entity);

    <ET extends T> ET restore(ET entity) throws NotFound;

    <ET extends T> Collection<ET> saveAll(Collection<ET> entities);

    void deleteById(I id) throws NotFound;

    <ET extends T> void delete(ET entity) throws NotFound;

    <ET extends T> void detach(ET entity) throws NotFound;
}
