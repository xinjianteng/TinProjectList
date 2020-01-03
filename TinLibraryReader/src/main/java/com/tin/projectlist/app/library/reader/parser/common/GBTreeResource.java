package com.tin.projectlist.app.library.reader.parser.common;

import com.core.file.GBFile;
import com.core.file.GBResourceFile;
import com.core.support.Language;
import com.core.xml.GBStringMap;
import com.core.xml.GBXMLReaderAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

/**
 * 资源文件树 使用方法见父类
 * @author fuchen
 * @date 2013-4-11
 */
final class GBTreeResource extends GBResource {
	private static interface Condition {
		abstract boolean accepts(int number);
	}

	private static class ValueCondition implements Condition {
		private final int myValue;

		ValueCondition(int value) {
			myValue = value;
		}

		@Override
		public boolean accepts(int number) {
			return myValue == number;
		}
	}

	private static class RangeCondition implements Condition {
		private final int myMin;
		private final int myMax;

		RangeCondition(int min, int max) {
			myMin = min;
			myMax = max;
		}

		@Override
		public boolean accepts(int number) {
			return myMin <= number && number <= myMax;
		}
	}

	private static class ModRangeCondition implements Condition {
		private final int myMin;
		private final int myMax;
		private final int myBase;

		ModRangeCondition(int min, int max, int base) {
			myMin = min;
			myMax = max;
			myBase = base;
		}

		@Override
		public boolean accepts(int number) {
			number = number % myBase;
			return myMin <= number && number <= myMax;
		}
	}

	private static class ModCondition implements Condition {
		private final int myMod;
		private final int myBase;

		ModCondition(int mod, int base) {
			myMod = mod;
			myBase = base;
		}

		@Override
		public boolean accepts(int number) {
			return number % myBase == myMod;
		}
	}

	static private Condition parseCondition(String description) {
		final String[] parts = description.split(" ");
		try {
			if ("range".equals(parts[0])) {
				return new RangeCondition(Integer.parseInt(parts[1]), Integer.parseInt(parts[2]));
			} else if ("mod".equals(parts[0])) {
				return new ModCondition(Integer.parseInt(parts[1]), Integer.parseInt(parts[2]));
			} else if ("modrange".equals(parts[0])) {
				return new ModRangeCondition(Integer.parseInt(parts[1]), Integer.parseInt(parts[2]), Integer.parseInt(parts[3]));
			} else if ("value".equals(parts[0])) {
				return new ValueCondition(Integer.parseInt(parts[1]));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	static volatile GBTreeResource ourRoot;
	private static final Object ourLock = new Object();

	private static long ourTimeStamp = 0;
	private static String ourLanguage = null;
	private static String ourCountry = null;

	private boolean myHasValue;
	private	String myValue;
	private HashMap<String,GBTreeResource> myChildren;
	private LinkedHashMap<Condition,String> myConditionalValues;

	static void buildTree() {
		synchronized (ourLock) {
			if (ourRoot == null) {
				ourRoot = new GBTreeResource("", null);
				ourLanguage = "zh";
				ourCountry = "CN";
				loadData();
			}
		}
	}

	private static void setInterfaceLanguage() {
		final String custom = LanguageOption.getValue();
		final String language;
		final String country;
		if (Language.SYSTEM_CODE.equals(custom)) {
			final Locale locale = Locale.getDefault();
			language = locale.getLanguage();
			country = locale.getCountry();
		} else {
			final int index = custom.indexOf('_');
			if (index == -1) {
				language = custom;
				country = null;
			} else {
				language = custom.substring(0, index);
				country = custom.substring(index + 1);
			}
		}
		if ((language != null && !language.equals(ourLanguage)) ||
				(country != null && !country.equals(ourCountry))) {
			ourLanguage = language;
			ourCountry = country;
			loadData();
		}
	}

	private static void updateLanguage() {
		final long timeStamp = System.currentTimeMillis();
		if (timeStamp > ourTimeStamp + 1000) {
			synchronized (ourLock) {
				if (timeStamp > ourTimeStamp + 1000) {
					ourTimeStamp = timeStamp;
					setInterfaceLanguage();
				}
			}
		}
	}

	private static void loadData(ResourceTreeReader reader, String fileName) {
//		reader.readDocument(ourRoot, GBResourceFile.createResourceFile("resources/zlibrary/" + fileName));
		reader.readDocument(ourRoot, GBResourceFile.createResourceFile("resources/application/" + fileName));
//		reader.readDocument(ourRoot, GBResourceFile.createResourceFile("resources/lang.xml"));
	}

	private static void loadData() {
		ResourceTreeReader reader = new ResourceTreeReader();
		loadData(reader, ourLanguage + ".xml");
		loadData(reader, ourLanguage + "_" + ourCountry + ".xml");
	}

	private	GBTreeResource(String name, String value) {
		super(name);
		setValue(value);
	}

	private void setValue(String value) {
		myHasValue = value != null;
		myValue = value;
	}

	@Override
	public boolean hasValue() {
		return myHasValue;
	}

	@Override
	public String getValue() {
//		updateLanguage();
		return myHasValue ? myValue : GBMissingResource.Value;
	}

	@Override
	public String getValue(int number) {
//		updateLanguage();
		if (myConditionalValues != null) {
			for (Map.Entry<Condition,String> entry: myConditionalValues.entrySet()) {
				if (entry.getKey().accepts(number)) {
					return entry.getValue();
				}
			}
		}
		return myHasValue ? myValue : GBMissingResource.Value;
	}

	@Override
	public GBResource getResource(String key) {
		final HashMap<String,GBTreeResource> children = myChildren;
		if (children != null) {
			GBTreeResource child = children.get(key);
			if (child != null) {
				return child;
			}
		}
		return GBMissingResource.Instance;
	}

	private static class ResourceTreeReader extends GBXMLReaderAdapter {
		private static final String NODE = "node";
		private final ArrayList<GBTreeResource> myStack = new ArrayList<GBTreeResource>();

		public void readDocument(GBTreeResource root, GBFile file) {
			myStack.clear();
			myStack.add(root);
			readQuietly(file);
		}

		@Override
		public boolean dontCacheAttributeValues() {
			return true;
		}

		@Override
		public boolean startElementHandler(String tag, GBStringMap attributes) {
			final ArrayList<GBTreeResource> stack = myStack;
			if (!stack.isEmpty() && (NODE.equals(tag))) {
				final String name = attributes.getValue("name");
				final String condition = attributes.getValue("condition");
				final String value = attributes.getValue("value");
				final GBTreeResource peek = stack.get(stack.size() - 1);
				if (name != null) {
					GBTreeResource node;
					HashMap<String,GBTreeResource> children = peek.myChildren;
					if (children == null) {
						node = null;
						children = new HashMap<String,GBTreeResource>();
						peek.myChildren = children;
					} else {
						node = children.get(name);
					}
					if (node == null) {
						node = new GBTreeResource(name, value);
						children.put(name, node);
					} else {
						if (value != null) {
							node.setValue(value);
							node.myConditionalValues = null;
						}
					}
					stack.add(node);
				} else if (condition != null && value != null) {
					final Condition compiled = parseCondition(condition);
					if (compiled != null) {
						if (peek.myConditionalValues == null) {
							peek.myConditionalValues = new LinkedHashMap<Condition,String>();
						}
						peek.myConditionalValues.put(compiled, value);
					}
					stack.add(peek);
				}
			}
			return false;
		}

		@Override
		public boolean endElementHandler(String tag) {
			final ArrayList<GBTreeResource> stack = myStack;
			if (!stack.isEmpty() && (NODE.equals(tag))) {
				stack.remove(stack.size() - 1);
			}
			return false;
		}
	}
}
