package com.logicalis.la.state;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 * 
 * Simple State Store
 *
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
		// ordered map
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
	 * Retrieves current state's json, resets updates counter and any entry list.
	 * 
	 * @return json document of current state
	 */
	@SuppressWarnings("unchecked")
	public synchronized String getState() {

		StringBuilder sb = new StringBuilder();
		sb.append("{\"updates\":").append(updateCount);
		for (String key : _map.keySet()) {
			sb.append(",\"").append(key).append("\":");
			Object value = _map.get(key);
			if (value instanceof HashSet<?>) {
				Set<String> events = (Set<String>) value;
				sb.append("[\"").append(events.stream().collect(Collectors.joining("\",\""))).append("\"]");
				events.clear();

			} else if (value instanceof String) {
				sb.append("\"").append(value).append("\"");
			} else {
				sb.append(value);
			}
		}
		sb.append("}");
		updateCount = 0;
		return sb.toString();
	}
}
