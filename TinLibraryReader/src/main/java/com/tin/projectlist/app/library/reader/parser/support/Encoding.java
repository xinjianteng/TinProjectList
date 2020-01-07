package com.tin.projectlist.app.library.reader.parser.support;

/**
 * 编码实体
 * @author fuchen
 * @date 2013-4-12
 */
public final class Encoding {
	public final String Family;
	public final String Name;
	public final String DisplayName;

	Encoding(String family, String name, String displayName) {
		Family = family;
		Name = name;
		DisplayName = displayName;
	}

	@Override
	public boolean equals(Object other) {
		return other instanceof Encoding && Name.equals(((Encoding)other).Name);
	}

	@Override
	public int hashCode() {
		return Name.hashCode();
	}

	public EncodingConverter createConverter() {
		return new EncodingConverter(Name);
	}
}
