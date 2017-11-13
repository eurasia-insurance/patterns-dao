package tech.lapsa.patterns.dao;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

public interface GeneralDAO<T, I> {

    T getById(I id) throws NotFound;

    default Optional<T> optionalById(I id) {
	try {
	    return Optional.of(getById(id));
	} catch (NotFound e) {
	    return Optional.empty();
	}
    }

    T getByIdByPassCache(I id) throws NotFound;

    default Optional<T> optionalByIdByPassCache(I id) {
	try {
	    return Optional.of(getByIdByPassCache(id));
	} catch (NotFound e) {
	    return Optional.empty();
	}
    }

    <ET extends T> ET save(ET entity);

    <ET extends T> ET restore(ET entity) throws NotFound;

    default <ET extends T> Collection<ET> saveAll(Collection<ET> entities) {
	return entities.stream() //
		.map(this::save) //
		.collect(Collectors.toList());
    }

    default void deleteById(I id) throws NotFound {
	delete(getById(id));
    }

    <ET extends T> void delete(ET entity) throws NotFound;

    <ET extends T> void detach(ET entity) throws NotFound;
}
