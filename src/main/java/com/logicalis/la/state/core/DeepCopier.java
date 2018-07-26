package com.logicalis.la.state.core;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class DeepCopier {

	private DeepCopier() {
	}

	/**
	 * Deep copy of a map (not a shallow copy).
	 * 
	 * @param o
	 *              map to be copied
	 * @return a deep copy of object
	 * @throws InvalidDataTypeException
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Map<?, ?> clone(Map<?, ?> o) throws InvalidDataTypeException {
		try {
			Map r = (Map) o.getClass().newInstance();
			for (Object key : o.keySet()) {
				r.put(key, clone(o.get(key)));
			}
			return r;

		} catch (InstantiationException | IllegalAccessException e) {
			throw new InvalidDataTypeException(e);
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static Set<?> clone(Set<?> o) throws InvalidDataTypeException {
		try {
			Set r = (Set) o.getClass().newInstance();
			for (Object e : o) {
				r.add(clone(e));
			}
			return r;

		} catch (InstantiationException | IllegalAccessException e) {
			throw new InvalidDataTypeException(e);
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static List<?> clone(List<?> o) throws InvalidDataTypeException {
		try {
			List r = (List) o.getClass().newInstance();
			for (Object e : o) {
				r.add(clone(e));
			}
			return r;

		} catch (InstantiationException | IllegalAccessException e) {
			throw new InvalidDataTypeException(e);
		}
	}

	/**
	 * Valid data types: Map, Set, List, String, Long or Double
	 * 
	 * @param o
	 *              object to be copied
	 * 
	 * @return a new instance of object
	 */
	@SuppressWarnings("rawtypes")
	private static Object clone(Object o) throws InvalidDataTypeException {
		Object r = null;
		if (Map.class.isInstance(o)) {
			r = clone((Map) o);

		} else if (Set.class.isInstance(o)) {
			r = clone((Set) o);

		} else if (List.class.isInstance(o)) {
			r = clone((List) o);

		} else if (o instanceof String) {
			r = new String((String) o);

		} else if (o instanceof Long) {
			r = new Long(((Long) o).longValue());

		} else if (o instanceof Double) {
			r = new Double(((Double) o).doubleValue());

		} else {
			throw new InvalidDataTypeException();
		}
		return r;
	}
}
