package pro.parseq.ghop.datasources.filters;

import java.util.function.Predicate;

import pro.parseq.ghop.utils.PropertiesAware;

public class AttributeFilterAggregate {

	private final long id;
	private final Predicate<PropertiesAware> predicate;

	public AttributeFilterAggregate(long id, Predicate<PropertiesAware> predicate) {

		this.id = id;
		this.predicate = predicate;
	}

	public long getId() {
		return id;
	}

	public Predicate<PropertiesAware> getPredicate() {
		return predicate;
	}
}
