package tech.lapsa.patterns.dao.beans;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;

public final class Predictates {

    private Predictates() {
    }

    public static enum MatchMode {
	AND, OR
    };

    public static Optional<Predicate> textMatches(final CriteriaBuilder cb, final Expression<String> expression,
	    final String matchesTo) {
	return textMatches(MatchMode.AND, cb, expression, matchesTo);
    }

    public static Optional<Predicate> textMatches(final MatchMode matchMode, final CriteriaBuilder cb,
	    final Expression<String> expression,
	    final String matchesTo) {
	if (matchesTo == null || matchesTo.trim().isEmpty())
	    return Optional.empty();
	final List<Predicate> words = new ArrayList<>();
	for (final String verb : matchesTo.split("\\s+")) {
	    final String pattern = '%' + verb.replaceAll("%", "") + '%';
	    words.add(cb.like(expression, pattern));
	}
	final Predicate[] list = words.toArray(new Predicate[0]);
	switch (matchMode) {
	case OR:
	    return Optional.of(cb.or(list));
	case AND:
	default:
	    return Optional.of(cb.and(list));
	}
    }
}
