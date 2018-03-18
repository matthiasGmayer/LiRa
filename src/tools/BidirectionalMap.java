package tools;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class BidirectionalMap<KeyType, ValueType> {
	private Map<KeyType, ValueType> keyToValueMap = new HashMap<KeyType, ValueType>();
	private Map<ValueType, KeyType> valueToKeyMap = new HashMap<ValueType, KeyType>();

	synchronized public void put(KeyType key, ValueType value) {
		keyToValueMap.put(key, value);
		valueToKeyMap.put(value, key);
	}

	synchronized public ValueType removeByKey(KeyType key) {
		ValueType removedValue = keyToValueMap.remove(key);
		valueToKeyMap.remove(removedValue);
		return removedValue;
	}

	synchronized public KeyType removeByValue(ValueType value) {
		KeyType removedKey = valueToKeyMap.remove(value);
		keyToValueMap.remove(removedKey);
		return removedKey;
	}

	public boolean containsKey(KeyType key) {
		return keyToValueMap.containsKey(key);
	}

	public boolean containsValue(ValueType value) {
		return keyToValueMap.containsValue(value);
	}

	public KeyType getKey(ValueType value) {
		return valueToKeyMap.get(value);
	}

	public ValueType get(KeyType key) {
		return keyToValueMap.get(key);
	}

	public Set<KeyType> getKeySet() {
		return keyToValueMap.keySet();
	}

	public Set<ValueType> getValueSet() {
		return valueToKeyMap.keySet();
	}
}