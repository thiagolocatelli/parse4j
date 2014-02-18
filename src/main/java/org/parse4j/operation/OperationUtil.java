package org.parse4j.operation;

public class OperationUtil {

	static Number addNumbers(Number first, Number second) {
		if (((first instanceof Double)) || ((second instanceof Double)))
			return Double.valueOf(first.doubleValue() + second.doubleValue());
		if (((first instanceof Float)) || ((second instanceof Float)))
			return Float.valueOf(first.floatValue() + second.floatValue());
		if (((first instanceof Long)) || ((second instanceof Long)))
			return Long.valueOf(first.longValue() + second.longValue());
		if (((first instanceof Integer)) || ((second instanceof Integer)))
			return Integer.valueOf(first.intValue() + second.intValue());
		if (((first instanceof Short)) || ((second instanceof Short)))
			return Integer.valueOf(first.shortValue() + second.shortValue());
		if (((first instanceof Byte)) || ((second instanceof Byte))) {
			return Integer.valueOf(first.byteValue() + second.byteValue());
		}
		throw new RuntimeException("Unknown number type.");
	}

}
