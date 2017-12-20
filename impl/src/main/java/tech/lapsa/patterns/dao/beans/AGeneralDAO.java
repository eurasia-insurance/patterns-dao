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

import tech.lapsa.java.commons.exceptions.IllegalArgument;
import tech.lapsa.java.commons.function.MyExceptions;
import tech.lapsa.java.commons.function.MyObjects;
import tech.lapsa.java.commons.function.MyOptionals;
import tech.lapsa.patterns.dao.GeneralDAO;
import tech.lapsa.patterns.dao.NotFound;

public abstract class AGeneralDAO<T extends Serializable, I extends Serializable> implements GeneralDAO<T, I> {

    protected final Class<T> entityClass;

    protected AGeneralDAO(final Class<T> entityClazz) {
	this.entityClass = entityClazz;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public T getById(final I id) throws IllegalArgument, NotFound {
	MyObjects.requireNonNull(IllegalArgument::new, id, "id");
	return MyOptionals.of(getEntityManager().find(entityClass, id)) //
		.orElseThrow(MyExceptions.supplier(NotFound::new, "Not found %1$s with id = '%2$s'",
			entityClass.getSimpleName(), id));
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public <ET extends T> ET save(final ET entity) throws IllegalArgument {
	MyObjects.requireNonNull(IllegalArgument::new, entity, "entity");
	try {
	    final ET merged = getEntityManager().merge(entity);
	    return merged;
	} catch (final PersistenceException e) {
	    throw new EJBException(e);
	}
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public <ET extends T> ET restore(final ET entity) throws IllegalArgument, NotFound {
	MyObjects.requireNonNull(IllegalArgument::new, entity, "entity");
	try {
	    final ET merged = getEntityManager().merge(entity);
	    getEntityManager().refresh(merged);
	    return merged;
	} catch (final EntityNotFoundException e) {
	    throw MyExceptions.format(NotFound::new, e, "Entity is not persisted %1$s", entityClass.getName());
	} catch (final PersistenceException e) {
	    throw new EJBException(e);
	}
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public <ET extends T> Collection<ET> saveAll(final Collection<ET> entities) throws IllegalArgument {
	MyObjects.requireNonNull(IllegalArgument::new, entities, "entities");
	try {
	    MyObjects.requireNonNull(entities, "entities");
	    final Collection<ET> ret = entities.stream() //
		    .map(getEntityManager()::merge) //
		    .collect(Collectors.toList());
	    return ret;
	} catch (final PersistenceException e) {
	    throw new EJBException(e);
	}
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void deleteById(final I id) throws IllegalArgument, NotFound {
	delete(getById(id));
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public <ET extends T> void delete(final ET entity) throws IllegalArgument, NotFound {
	MyObjects.requireNonNull(IllegalArgument::new, entity, "entity");
	try {
	    getEntityManager().remove(entity);
	} catch (final IllegalArgumentException e) {
	    throw MyExceptions.format(NotFound::new, e, "Entity %1$s is not persistent", entityClass.getName());
	} catch (final PersistenceException e) {
	    throw new EJBException(e);
	}
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public <ET extends T> ET detach(final ET entity) throws IllegalArgument, NotFound {
	MyObjects.requireNonNull(IllegalArgument::new, entity, "entity");
	try {
	    getEntityManager().detach(entity);
	    return entity;
	} catch (final IllegalArgumentException e) {
	    throw MyExceptions.format(NotFound::new, e, "Entity %1$s is not persistent", entityClass.getName());
	}
    }

    // PROTECTED

    protected abstract EntityManager getEntityManager();

    protected <X> X signleResult(final TypedQuery<X> query) throws NotFound {
	try {
	    return query.setMaxResults(1).getSingleResult();
	} catch (final NoResultException e) {
	    throw MyExceptions.format(NotFound::new, "Not found entity %1$s", entityClass.getName());
	} catch (final NonUniqueResultException e) {
	    throw new EJBException(e);
	} catch (final PersistenceException e) {
	    throw new EJBException(e);
	}
    }

    // PRIVATE

}
