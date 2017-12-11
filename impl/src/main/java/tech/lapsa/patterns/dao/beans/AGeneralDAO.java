package tech.lapsa.patterns.dao.beans;

import java.io.Serializable;
import java.util.Collection;
import java.util.stream.Collectors;

import javax.ejb.EJBException;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceException;
import javax.persistence.TypedQuery;

import tech.lapsa.java.commons.function.MyObjects;
import tech.lapsa.java.commons.function.MyOptionals;
import tech.lapsa.java.commons.function.MyStrings;
import tech.lapsa.patterns.dao.GeneralDAO;
import tech.lapsa.patterns.dao.NotFound;

public abstract class AGeneralDAO<T extends Serializable, I extends Serializable> implements GeneralDAO<T, I> {

    protected final Class<T> entityClass;

    protected AGeneralDAO(final Class<T> entityClazz) {
	this.entityClass = entityClazz;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public T getById(final I id) throws IllegalArgumentException, NotFound {
	MyObjects.requireNonNull(id, "id");
	return MyOptionals.of(getEntityManager().find(entityClass, id)) //
		.orElseThrow(() -> new NotFound(
			String.format("Not found %1$s with id = '%2$s'", entityClass.getSimpleName(), id)));
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public <ET extends T> ET save(ET entity) throws IllegalArgumentException {
	try {
	    ET merged = getEntityManager().merge(entity);
	    return merged;
	} catch (PersistenceException e) {
	    throw new EJBException(e);
	}
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public <ET extends T> ET restore(final ET entity) throws IllegalArgumentException, NotFound {
	try {
	    ET merged = getEntityManager().merge(entity);
	    getEntityManager().refresh(merged);
	    return merged;
	} catch (EntityNotFoundException e) {
	    throw new NotFound(String.format("Entity is not persisted %1$s", entityClass.getCanonicalName()), e);
	} catch (PersistenceException e) {
	    throw new EJBException(e);
	}
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public <ET extends T> Collection<ET> saveAll(final Collection<ET> entities) throws IllegalArgumentException {
	try {
	    MyObjects.requireNonNull(entities, "entities");
	    Collection<ET> ret = entities.stream() //
		    .map(getEntityManager()::merge) //
		    .collect(Collectors.toList());
	    return ret;
	} catch (PersistenceException e) {
	    throw new EJBException(e);
	}
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void deleteById(I id) throws IllegalArgumentException, NotFound {
	delete(getById(id));
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public <ET extends T> void delete(ET entity) throws IllegalArgumentException, NotFound {
	try {
	    getEntityManager().remove(entity);
	} catch (IllegalArgumentException e) {
	    throw new NotFound(MyStrings.format("Entity %1$s is not persistent", entityClass.getName()), e);
	} catch (PersistenceException e) {
	    throw new EJBException(e);
	}
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public <ET extends T> ET detach(ET entity) throws IllegalArgumentException, NotFound {
	try {
	    getEntityManager().detach(entity);
	    return entity;
	} catch (IllegalArgumentException e) {
	    throw new NotFound(String.format("Entity %1$s is not persistent", entityClass.getName()), e);
	}
    }

    // PROTECTED

    protected abstract EntityManager getEntityManager();

    protected <X> X signleResult(final TypedQuery<X> query) throws NotFound {
	try {
	    return query.setMaxResults(1).getSingleResult();
	} catch (NoResultException e) {
	    throw new NotFound(e);
	} catch (NonUniqueResultException e) {
	    throw new EJBException(e);
	} catch (PersistenceException e) {
	    throw new EJBException(e);
	}
    }

    // PRIVATE

}
