package tech.lapsa.patterns.dao;

import java.io.Serializable;
import java.util.Collection;

import tech.lapsa.java.commons.exceptions.IllegalArgument;

public interface GeneralDAO<T extends Serializable, I extends Serializable> {

    T getById(I id) throws IllegalArgument, NotFound;

    <ET extends T> ET save(ET entity) throws IllegalArgument;

    <ET extends T> ET restore(ET entity) throws IllegalArgument, NotFound;

    <ET extends T> Collection<ET> saveAll(Collection<ET> entities) throws IllegalArgument;

    void deleteById(I id) throws IllegalArgument, NotFound;

    <ET extends T> void delete(ET entity) throws IllegalArgument, NotFound;

    <ET extends T> ET detach(ET entity) throws IllegalArgument, NotFound;
}
