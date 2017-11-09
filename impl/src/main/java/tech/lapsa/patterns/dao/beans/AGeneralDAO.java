package tech.lapsa.patterns.dao.beans;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.CacheRetrieveMode;
import javax.persistence.CacheStoreMode;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.TypedQuery;

import tech.lapsa.java.commons.function.MyMaps;
import tech.lapsa.java.commons.function.MyObjects;
import tech.lapsa.patterns.dao.GeneralDAO;
import tech.lapsa.patterns.dao.NotFound;
import tech.lapsa.patterns.dao.TooMuchFound;

public abstract class AGeneralDAO<T, I> implements GeneralDAO<T, I> {

    protected static final String HINT_JAVAX_PERSISTENCE_CACHE_STORE_MODE = "javax.persistence.cache.storeMode";
    protected static final String HINT_JAVAX_PERSISTENCE_CACHE_RETREIVE_MODE = "javax.persistence.cache.retreiveMode";

    protected final Class<T> entityClass;

    protected AGeneralDAO(final Class<T> entityClazz) {
	this.entityClass = entityClazz;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public T getById(final I id) throws NotFound {
	return getByIdAndHint(id, null);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public T getByIdByPassCache(final I id) throws NotFound {
	return getByIdAndHint(id, MyMaps.of( //
		HINT_JAVAX_PERSISTENCE_CACHE_RETREIVE_MODE, CacheRetrieveMode.BYPASS, //
		HINT_JAVAX_PERSISTENCE_CACHE_STORE_MODE, CacheStoreMode.REFRESH //
	));
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public <ET extends T> ET save(ET entity) {
	ET merged = getEntityManager().merge(entity);
	getEntityManager().flush();
	return merged;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public <ET extends T> ET restore(final ET entity) throws NotFound {
	try {
	    ET merged = getEntityManager().merge(entity);
	    getEntityManager().refresh(merged);
	    return merged;
	} catch (EntityNotFoundException e) {
	    throw new NotFound(String.format("Entity is not persisted %1$s", entityClass.getCanonicalName()), e);
	}
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public <ET extends T> Collection<ET> saveAll(final Collection<ET> entities) {
	MyObjects.requireNonNull(entities, "entities");
	Collection<ET> ret = entities.stream() //
		.map(getEntityManager()::merge) //
		.collect(Collectors.toList());
	getEntityManager().flush();
	return ret;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void deleteById(I id) throws NotFound {
	delete(getById(id));
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public <ET extends T> void delete(ET entity) throws NotFound {
	try {
	    getEntityManager().flush();
	    getEntityManager().remove(entity);
	    getEntityManager().flush();
	} catch (IllegalArgumentException e) {
	    throw new NotFound(String.format("Entity %1$s is not persistent", entityClass.getName()), e);
	}
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public <ET extends T> void prepareSerializing(ET entity) throws NotFound {
	try {
	    getEntityManager().detach(entity);
	} catch (IllegalArgumentException e) {
	    throw new NotFound(String.format("Entity %1$s is not persistent", entityClass.getName()), e);
	}
    }

    // PROTECTED

    protected abstract EntityManager getEntityManager();

    protected <X> TypedQuery<X> putNoCacheHints(final TypedQuery<X> query) {
	return query
		.setHint(HINT_JAVAX_PERSISTENCE_CACHE_RETREIVE_MODE, CacheRetrieveMode.BYPASS)
		.setHint(HINT_JAVAX_PERSISTENCE_CACHE_STORE_MODE, CacheStoreMode.REFRESH);
    }

    protected <X> List<X> resultListNoCached(final TypedQuery<X> query) {
	return putNoCacheHints(query)
		.getResultList();
    }

    protected <X> X signleResultNoCached(final TypedQuery<X> query) throws NotFound, TooMuchFound {
	return signleResult(putNoCacheHints(query));
    }

    protected <X> X signleResult(final TypedQuery<X> query) throws NotFound, TooMuchFound {
	try {
	    return query.getSingleResult();
	} catch (NoResultException e) {
	    throw new NotFound(e);
	} catch (NonUniqueResultException e) {
	    throw new TooMuchFound(e);
	}
    }

    // PRIVATE

    private T getByIdAndHint(final I id, Map<String, Object> hints)
	    throws NotFound {
	MyObjects.requireNonNull(id, "id");
	return Optional.ofNullable( //
		MyObjects.nonNull(hints) //
			? getEntityManager().find(entityClass, id, hints) //
			: getEntityManager().find(entityClass, id))
		.orElseThrow(() -> new NotFound(
			String.format("Not found %1$s with id = '%2$s'", entityClass.getSimpleName(), id)));
    }
}
