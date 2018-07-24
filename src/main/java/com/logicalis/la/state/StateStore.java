package com.logicalis.la.state;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 * 
 * A Simple State Store.
 * <p>
 * State attributes as kept in a Map and can be String, Integer, Long, Double,
 * Set or Map. Inner maps also are restricted to those data types.
 * </p>
 * <p>
 * All getters and setters are <i>synchronized</i> to avoid concurrency
 * exceptions. Since writes are expected to be much more frequent than reads,
 * read and write locks are pretty useless (like ReentrantReadWriteLock). In
 * current Java VM, <i>synchronized</i> methods for this scenario are almost as
 * fast as the new Java8 StampedLock. So ...
 * </p>
 */
public class StateStore {

	// Singleton
	private static StateStore _instance;

	// state values
	private Map<String, Object> _map;

	// how many updates since last getState() call
	private int updateCount = 0;

	/**
	 * 
	 */
	private StateStore() {
		// ordered map (because we like ordered attributes)
		_map = new TreeMap<>();
	}

	/**
	 * 
	 * @return instance of StateStore
	 */
	public static StateStore getInstance() {
		if (_instance == null)
			_instance = new StateStore();
		return _instance;
	}

	/**
	 * Sets a 'string' value
	 * 
	 * @param name
	 *                  name of entry
	 * @param value
	 *                  value of state entry
	 */
	public synchronized void set(String name, String value) {
		_map.put(name, value);
		updateCount++;
	}

	/**
	 * Sets a 'floating point' value
	 * 
	 * @param name
	 *                  name of entry
	 * @param value
	 *                  value of state entry
	 */
	public synchronized void set(String name, Double value) {
		_map.put(name, value);
		updateCount++;
	}

	/**
	 * Sets a 'integer' value
	 * 
	 * @param name
	 *                  name of entry
	 * @param value
	 *                  value of state entry
	 */
	public synchronized void set(String name, Long value) {
		_map.put(name, value);
		updateCount++;
	}

	/**
	 * Sets a 'map' value
	 * 
	 * @param name
	 *                  name of entry
	 * @param value
	 *                  map of state entry
	 */
	public synchronized void set(String name, Map<String, ?> value) {
		_map.put(name, value);
		updateCount++;
	}

	/**
	 * Add a message list. It uses a hashset to avoid sending duplicate messages.
	 * 
	 * @param event
	 *                    name of entries list
	 * @param message
	 *                    message to append to state entry
	 */
	@SuppressWarnings("unchecked")
	public synchronized void add(String event, String message) {
		Object events = _map.get(event);
		if (events == null || !(events instanceof HashSet<?>))
			events = new HashSet<>();
		((Set<String>) events).add(message);
		_map.put(event, events);
		updateCount++;
	}

	/**
	 * Retrieves current state's json, resets updates counter and any entry list or
	 * map.
	 * 
	 * @return json document of current state
	 * @throws Exception
	 */
	public synchronized String getState() throws InvalidDataTypeException {

		StringBuilder sb = new StringBuilder();
		_map.put("_updates", updateCount);
		mapToJson(sb, _map);
		updateCount = 0;
		return sb.toString();
	}

	/**
	 * Converts a map to a json string.
	 * 
	 * @param map
	 *                map to convert
	 * @return json string
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	private void mapToJson(StringBuilder sb, Map<String, Object> map) throws InvalidDataTypeException {
		sb.append("{");
		boolean first = true;
		for (String key : map.keySet()) {
			sb.append(first ? "" : ",").append('\"').append(key).append("\":");
			Object value = map.get(key);
			if (Map.class.isInstance(value)) {
				Map<String, Object> innerMap = (Map<String, Object>) value;
				mapToJson(sb, innerMap);
				innerMap.clear();

			} else if (Set.class.isInstance(value)) {
				Set<String> events = (Set<String>) value;
				sb.append("[\"").append(events.stream().collect(Collectors.joining("\",\""))).append("\"]");
				events.clear();

			} else if (value instanceof String) {
				sb.append("\"").append(value).append("\"");

			} else if (value instanceof Double || value instanceof Long || value instanceof Integer) {
				sb.append(value);

			} else {
				// limiting data types to avoid more complex marshallers, like ObjectMappers
				throw new InvalidDataTypeException(
						String.format("Attribute '%s' is a %s: should be Map, Set, String, Long or Double.", key,
								value.getClass().getSimpleName()));
			}
			first = false;
		}
		sb.append("}");
	}
}
