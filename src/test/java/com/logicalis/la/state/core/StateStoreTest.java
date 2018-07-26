package com.logicalis.la.state.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;

class StateStoreTest {

	@Test
	void testGetInstance() {
		StateStore state = StateStore.getInstance();
		assertNotNull(state);
	}

	@Test
	void testSetStringString() {
		StateStore state = StateStore.getInstance();
		String key = "string";
		String value = "a string value";
		state.set(key, value);
		try {
			Map<String, Object> result = state.getState();
			assertEquals(value, result.get(key));
			assertFalse(value == result.get(key), "Should be a copy, not a shallow copy or a reference");

			Map<String, Object> state2 = state.getState();
			assertEquals(value, state2.get(key), "String must be kept between getState() calls");

		} catch (InvalidDataTypeException e) {
			fail("Should not throw anything");
		}
	}

	@Test
	void testSetStringDouble() {
		StateStore state = StateStore.getInstance();
		String key = "double";
		Double value = new Double(42.6);
		state.set(key, value);
		try {
			Map<String, Object> result = state.getState();
			assertEquals(value, result.get(key));
			assertFalse(value == result.get(key), "Should be a copy, not a shallow copy or a reference");

			Map<String, Object> state2 = state.getState();
			assertEquals(value, state2.get(key), "Double must be kept between getState() calls");

		} catch (InvalidDataTypeException e) {
			fail("Should not throw anything");
		}
	}

	@Test
	void testSetStringLong() {
		StateStore state = StateStore.getInstance();
		String key = "long";
		Long value = new Long(4500L);
		state.set(key, value);
		try {
			Map<String, Object> result = state.getState();
			assertEquals(value, result.get(key));
			assertFalse(value == result.get(key), "Should be a copy, not a shallow copy or a reference");

			Map<String, Object> state2 = state.getState();
			assertEquals(value, state2.get(key), "Long must be kept between getState() calls");

		} catch (InvalidDataTypeException e) {
			fail("Should not throw anything");
		}
	}

	@Test
	@SuppressWarnings("rawtypes")
	void testSetStringMapOfStringQ() {
		StateStore state = StateStore.getInstance();
		String key = "secondLevelMap";
		String innerKey = "innerString";
		String innerValue = "a string in a map";
		Map<String, String> value = new HashMap<>();
		value.put(innerKey, innerValue);
		state.set(key, value);
		try {
			Map<String, Object> result = state.getState();
			assertEquals(innerValue, ((Map) result.get(key)).get(innerKey));
			assertFalse(value == result.get(key), "Should be a copy, not a shallow copy or a reference");
			assertNotSame(value.get(innerKey), ((Map) result.get(key)).get(innerKey));

			Map<String, Object> state2 = state.getState();
			assertEquals(innerValue, ((Map) state2.get(key)).get(innerKey),
					"Map must be kept between getState() calls");

		} catch (InvalidDataTypeException e) {
			fail("Should not throw anything");
		}
	}

	@Test
	@SuppressWarnings("unchecked")
	void testAddToSet() {
		StateStore state = StateStore.getInstance();
		String key = "set";
		String[] values = new String[] { "s1", "s2", "s3", "s1", "s3", "s2", "s3" };
		for (int i = 0; i < values.length; i++)
			state.addToSet(key, values[i]);
		try {
			Map<String, Object> result = state.getState();
			Set<String> resultset = (Set<String>) result.get(key);
			assertEquals(resultset.size(), 3);
			state.addToSet(key, "s4");
			assertEquals(resultset.size(), 3, "Should be a copy, not a shallow copy or a reference");

		} catch (InvalidDataTypeException e) {
			fail("Should not throw anything");
		}
	}

	@Test
	@SuppressWarnings("unchecked")
	void testAddToList() {
		StateStore state = StateStore.getInstance();
		String key = "list";
		String[] values = new String[] { "s1", "s2", "s3", "s3" };
		for (int i = 0; i < values.length; i++)
			state.addToList(key, values[i]);
		try {
			Map<String, Object> result = state.getState();
			List<String> resultset = (List<String>) result.get(key);
			assertEquals(resultset.size(), 3);
			state.addToList(key, "s4");
			assertEquals(resultset.size(), 3, "Should be a copy, not a shallow copy or a reference");

		} catch (InvalidDataTypeException e) {
			fail("Should not throw anything");
		}
	}

	@Test
	void testRemoveString() {
		StateStore state = StateStore.getInstance();
		String key = "string";
		String value = "a string value";
		state.set(key, value);
		try {
			Map<String, Object> result = state.getState();
			assertEquals(value, result.get(key));

			state.remove(key);
			result = state.getState();
			assertFalse(result.containsKey(key), "Should be removed");

		} catch (InvalidDataTypeException e) {
			fail("Should not throw anything");
		}
	}

	@Test
	void testRemoveLong() {
		StateStore state = StateStore.getInstance();
		String key = "long";
		Long value = new Long(4500L);
		state.set(key, value);
		try {
			Map<String, Object> result = state.getState();
			assertEquals(value, result.get(key));

			state.remove(key);
			result = state.getState();
			assertFalse(result.containsKey(key), "Should be removed");

		} catch (InvalidDataTypeException e) {
			fail("Should not throw anything");
		}
	}

	@Test
	void testRemoveDouble() {
		StateStore state = StateStore.getInstance();
		String key = "double";
		Double value = new Double(450.27);
		state.set(key, value);
		try {
			Map<String, Object> result = state.getState();
			assertEquals(value, result.get(key));

			state.remove(key);
			result = state.getState();
			assertFalse(result.containsKey(key), "Should be removed");

		} catch (InvalidDataTypeException e) {
			fail("Should not throw anything");
		}
	}

	@Test
	@SuppressWarnings("unchecked")
	void testRemoveMap() {
		StateStore state = StateStore.getInstance();
		String key = "map";
		Map<String, String> value = new HashMap<>();
		String innerKey = "entry";
		String innerValue = "data";
		value.put(innerKey, innerValue);
		state.set(key, value);
		try {
			Map<String, Object> result = state.getState();
			Map<String, String> resultmap = (Map<String, String>) result.get(key);
			assertNotNull(resultmap, "Should not be null");
			assertEquals(innerValue, resultmap.get(innerKey));

			state.remove(key);
			result = state.getState();
			assertFalse(result.containsKey(key), "Should be removed");

		} catch (InvalidDataTypeException e) {
			fail("Should not throw anything");
		}
	}

	@Test
	@SuppressWarnings("unchecked")
	void testRemoveSet() {
		StateStore state = StateStore.getInstance();
		String key = "set";
		state.addToSet(key, "a set entry");
		try {
			Map<String, Object> result = state.getState();
			Set<String> resultset = (Set<String>) result.get(key);
			assertNotNull(resultset, "Should not be null");

			state.remove(key);
			result = state.getState();
			assertFalse(result.containsKey(key), "Should be removed");

		} catch (InvalidDataTypeException e) {
			fail("Should not throw anything");
		}
	}

	@Test
	@SuppressWarnings("unchecked")
	void testRemoveList() {
		StateStore state = StateStore.getInstance();
		String key = "list";
		state.addToList(key, "a list entry");
		try {
			Map<String, Object> result = state.getState();
			List<String> resultset = (List<String>) result.get(key);
			assertNotNull(resultset, "Should not be null");

			state.remove(key);
			result = state.getState();
			assertFalse(result.containsKey(key), "Should be removed");

		} catch (InvalidDataTypeException e) {
			fail("Should not throw anything");
		}
	}

	@Test
	@SuppressWarnings("rawtypes")
	void testGetState() {
		StateStore state = StateStore.getInstance();
		state.set("string", "a string entry");
		state.set("long", new Long(12L));
		state.set("double", new Double(45.2));
		Map<String, String> map = new HashMap<>();
		map.put("map-entry", "a map entry value");
		state.set("map", map);
		state.addToList("list", "a list entry");
		state.addToList("list", "another list entry");
		state.addToSet("set", "a set entry");
		state.addToSet("set", "another set entry");
		state.addToSet("set", "yet another set entry");

		try {
			Map<String, Object> state1 = state.getState();
			assertEquals(state1.get("_updates"), 9L);
			assertNotNull(state1.get("string"));
			assertNotNull(state1.get("long"));
			assertNotNull(state1.get("double"));
			assertNotNull(state1.get("map"));
			assertNotNull(state1.get("list"));
			assertNotNull(state1.get("set"));
			assertEquals(((Map) state1.get("map")).size(), 1, "Map shoud have 1 element");
			assertEquals(((List) state1.get("list")).size(), 2, "List should have 2 elements");
			assertEquals(((Set) state1.get("set")).size(), 3, "List should have 3 elements");

			Map<String, Object> state2 = state.getState();
			assertEquals(state2.get("_updates"), 0L);
			assertEquals(state1.get("string"), state2.get("string"), "String entry should not be changed");
			assertEquals(state1.get("long"), state2.get("long"), "Long entry should not be changed");
			assertEquals(state1.get("double"), state2.get("double"), "Double entry should not be changed");
			assertEquals(state1.get("map"), state2.get("map"), "Map entry should not be changed");
			assertEquals(((List) state2.get("list")).size(), 0, "List should be emptied");
			assertEquals(((Set) state2.get("set")).size(), 0, "List should be emptied");

		} catch (InvalidDataTypeException e) {
			fail("Should not throw anything");
		}
	}
}
