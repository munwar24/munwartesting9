package com.ii.mobile.payload;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.ii.mobile.soap.gson.GJon;
import com.ii.mobile.soap.gson.GetEmployeeAndTaskStatusByEmployeeID;
import com.ii.mobile.soap.gson.GetTaskInformationByTaskNumberAndFacilityID;
import com.ii.mobile.soap.gson.ValidateUser;
import com.ii.mobile.util.L;

public enum Dictionary {
	INSTANCE;
	List<String> list = new ArrayList<String>();
	List<String> literals = new ArrayList<String>();

	byte OPEN_CONTENT = "{".getBytes()[0];
	byte CLOSE_CONTENT = "}".getBytes()[0];
	byte QUOTE = "\"".getBytes()[0];
	byte BACK_SLASH = "\\".getBytes()[0];

	public List<String> getDictionary() {
		if (list.size() == 0)
			initDictionary();
		return list;
	}

	private void initDictionary() {
		parseClass(StatusWrapper.class);
		parseClass(PayloadWrapper.class);
		parseClass(GetEmployeeAndTaskStatusByEmployeeID.class);
		parseClass(GetTaskInformationByTaskNumberAndFacilityID.class);
		parseClass(ValidateUser.class);
		parseClass(Monitor.class);
		parseClass(GJon.class);
		// parseClass(Persist.class);
		// literals.add("Null");
		// literals.add("True");
		// literals.add("False");
		// literals.add("Available");
		// literals.add("{{");
		// literals.add("}}");
		// L.out("before cap list: " + list);
		removeDuplicatesDictionary();
		capitalizeDictionary();
		// L.out(" cap list: " + list);
		sortDictionary();
		L.out("\n\n    Generate dictionary: " + (list.size() +
				literals.size()));
		prettyPrint(list);
		prettyPrint(literals);
		L.out(" size: " + list.size() + " sorted list: " + list);
	}

	private void removeDuplicatesDictionary() {
		// L.out("pre list size: " + list.size());
		@SuppressWarnings("unused")
		int oldSize = list.size();
		Set<String> noDuplicateSet = new HashSet<String>(list);
		list = new ArrayList<String>(noDuplicateSet);
		// L.out("	  remove Duplicates: " + oldSize + " -> " + list.size());

	}

	@SuppressWarnings("unused")
	private void prettyPrint(List<String> entries) {
		int count = 0;
		String temp = "";
		for (String string : entries) {
			temp += "  " + count + ": " + string;
			count += 1;
			if (count % 4 == 0)
				temp += "\n";
		}
		L.out(temp);
	}

	// private void prettyPrint(Class<?> theClass, Field[] fields) {
	// // String temp = "Class Fields for: " + theClass+"\n";
	// String temp = "";
	// for (int i = 0; i < fields.length; i++) {
	// temp += "  " + i + ": " + fields[i].getName();
	// if (i + 1 % 4 == 0)
	// temp += "\n";
	// }
	// L.out(temp);
	// }

	private void sortDictionary() {
		Collections.sort(list, new Comparator<String>() {

			// @Override
			public int compare(String o1, String o2) {
				// if (o2.indexOf(o1) != -1 && o1.length() > o2.length()) {
				if (o1.startsWith(o2)) {
					// L.out("starts 1: " + o1 + " " + o2);
					return -1;
				} else if (o2.startsWith(o1)) {
					// L.out("starts 1: " + o1 + " " + o2);
					return 1;
				}
				return o1.compareTo(o2);
			}
		});
		// L.out("	  Sort dictionary: " + list.size() + " items");
		// L.out("pre size: " + list.size() + " sorted list: " + list);

	}

	private void parseClass(Class<?> theClass) {
		// L.out("parseClass: " + theClass + " size: " + list.size());
		Class<?>[] classes = theClass.getDeclaredClasses();
		// list.add(theClass.getSimpleName());
		list.addAll(getFields(theClass));
		for (int i = 0; i < classes.length; i++) {
			parseClass(classes[i]);
		}
		// L.out("list: " + list.size());
	}

	private Collection<? extends String> getFields(Class<?> theClass) {
		List<String> newList = new ArrayList<String>();
		newList.add("*" + theClass.getSimpleName());
		Field[] fields = theClass.getDeclaredFields();

		// prettyPrint(theClass, fields);

		for (int i = 0; i < fields.length; i++) {
			Field field = fields[i];
			// L.out("field: " + field + " type: " + field.getType());
			if (true || field.getType().equals(String.class) || field.getType().equals(List.class)
					|| field.getType().equals(boolean.class)) {
				newList.add(fields[i].getName());
			}
		}
		return newList;
	}

	private void capitalizeDictionary() {
		List<String> newList = new ArrayList<String>();
		for (String string : list) {
			newList.add(capitalizeFirstLetter(string));
		}
		list = newList;
	}

	private String capitalizeFirstLetter(String original) {
		if (original.length() == 0)
			return original;
		return original.substring(0, 1).toUpperCase() + original.substring(1);
	}

	public String substitute(String string) {
		getDictionary();
		String newString = string.replaceAll("\\\\", "");

		try {
			byte[] bytes = string.getBytes("UTF-8");
			// L.out("string: " + string.length() + " bytes: " + bytes.length);
		} catch (UnsupportedEncodingException e) {
			L.out("e: " + e);
		}
		int code = 0;
		for (String token : literals) {
			// String test = "\"" + token + "\"";
			int intCode = (char) code | 1 << 8;
			code += 1;
			newString = newString.replaceAll("(?i)" + token, ((char) intCode) + "");
		}
		for (String token : list) {
			// String test = "\"" + token + "\"";
			int intCode = (char) code | 1 << 8;
			String replace = "\"" + token + "\"";
			newString = newString.replaceAll("(?i)," + replace + ":", ((char) intCode) + "");
			newString = newString.replaceAll("(?i)" + replace + ":", ((char) intCode) + "");
			newString = newString.replaceAll("(?i)" + replace, ((char) intCode) + "");
			code += 1;
		}
		return newString;
	}

	private String replaceBackSlash(String newString) {
		int index = 0;

		while ((index = newString.indexOf("JSon\"", index + 1)) != -1) {
			// L.out("index: " + index);
			byte[] bytes = newString.getBytes();
			newString = fixJSonBackSlash(bytes, index + 7);
		}
		newString = newString.replaceAll("\\{,", "{");
		newString = newString.replaceAll(":\\}", "}");
		return newString;
	}

	private String fixJSonBackSlash(byte[] bytes, int index) {
		int stackCounter = 0;
		// will break if 200 variables quoted!
		byte[] copy = new byte[bytes.length + 200];

		// L.out("constants: " + OPEN_CONTENT + " " + CLOSE_CONTENT + " " +
		// QUOTE + " " + BACK_SLASH + " "
		// + "{".getBytes().length);

		for (int j = 0; j < index; j++)
			copy[j] = bytes[j];

		int copyIndex = index;
		for (int i = index; i < bytes.length; i++) {
			byte b = bytes[i];
			if (b == OPEN_CONTENT) {
				stackCounter += 1;
			} else if (b == CLOSE_CONTENT) {
				stackCounter -= 1;
				if (stackCounter == 0) {
					copy[copyIndex] = b;
					copyIndex += 1;
					for (int k = i; k < bytes.length; k++) {
						copy[copyIndex] = bytes[k];
						copyIndex += 1;
					}
					break;
				}

			} else if (b == QUOTE) {
				copy[copyIndex] = BACK_SLASH;
				copyIndex += 1;
			}
			copy[copyIndex] = b;
			copyIndex += 1;

		}
		String temp = new String(copy).substring(0, copyIndex);
		// L.out("temp: " + temp);
		return temp;
	}

	public String unSubstitute(String string) {
		getDictionary();
		String temp = "";
		// L.out("unSubstitute: " + (1 << 8) + " size: " + string.length());
		char[] chars = string.toCharArray();
		// L.out("chars: " + chars.length);
		for (int i = 0; i < chars.length; i++) {
			char c = chars[i];
			// int test = c & 1 << 8;
			if ((c & 1 << 8) != 0) {
				// int code = b ^ 1 << 8;
				int index = c & 0x7f;
				// L.out(i + ": code: " + (int) (c) + " char: " + c + " index: "
				// + index + " byteasInt: "
				// + (int) c);
				temp += getIndex(index);
			} else {
				temp += c;
			}
		}
		// L.out("before replace: " + temp);
		temp = replaceBackSlash(temp);
		return temp.replaceAll(":,", ",");
	}

	private String getIndex(int index) {
		try {
			if (index < literals.size())
				return literals.get(index);
			String token = list.get(index - literals.size());
			if (token.startsWith("*"))
				return "\"" + token.substring(1, token.length()) + "\":";
			return ",\"" + token + "\":";
		} catch (Exception e) {
			L.out("e: " + e + " +list: " + list.size());
		}
		return "*ERROR*";
	}
}