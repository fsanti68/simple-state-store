package com.logicalis.la.state.core;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * 
 * A Simple State Store.
 * <p>
 * State attributes are kept in a Map and can be String, Integer, Long, Double,
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
	 * Add a message to a set (avoid duplicates).
	 * 
	 * @param event
	 *                    name of entries list
	 * @param message
	 *                    message to be appended to state entry
	 */
	@SuppressWarnings("unchecked")
	public synchronized void addToSet(String event, String message) {
		Object events = _map.get(event);
		if (events == null || !(events instanceof HashSet<?>))
			events = new HashSet<>();
		((Set<String>) events).add(message);
		_map.put(event, events);
		updateCount++;
	}

	/**
	 * Add a message to a list. It uses an arraylist to keep insertion order.
	 * 
	 * @param event
	 *                    name of entries list
	 * @param message
	 *                    message to be appended to state entry
	 */
	@SuppressWarnings("unchecked")
	public synchronized void addToList(String event, String message) {
		Object events = _map.get(event);
		if (events == null || !(events instanceof ArrayList<?>))
			events = new ArrayList<>();
		List<String> list = (List<String>) events;
		// append only if last element differs
		if (list.isEmpty() || !list.get(list.size() - 1).equals(message))
			list.add(message);
		_map.put(event, list);
		updateCount++;
	}

	/**
	 * Removes an entry from state.
	 * 
	 * @param name
	 *                 entry key to be removed.
	 */
	public synchronized void remove(String name) {
		_map.remove(name);
	}

	/**
	 * 
	 * @return a copy of state map (as an immutable map)
	 * @throws InvalidDataTypeException
	 */
	@SuppressWarnings("unchecked")
	public synchronized Map<String, Object> getState() throws InvalidDataTypeException {
		TreeMap<String, Object> map = (TreeMap<String, Object>) _map;
		map.put("_updates", new Long(updateCount));
		updateCount = 0;

		Map<String, Object> immutableMap = (Map<String, Object>) DeepCopier.clone(_map);
		resetEvents(_map);
		return immutableMap;
	}

	/**
	 * Empties all lists, sets or maps inside the state.
	 * 
	 * @param map
	 *                the state map or any inner map entry
	 */
	@SuppressWarnings("rawtypes")
	private void resetEvents(Map map) {
		for (Object key : map.keySet()) {
			Object value = map.get(key);
			if (Set.class.isInstance(value)) {
				((Set) value).clear();

			} else if (List.class.isInstance(value)) {
				((List) value).clear();
			}
		}
	}
}
