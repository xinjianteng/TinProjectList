package com.tin.projectlist.app.library.reader.model.book;

public final class Author implements Comparable<Author> {
	public static final Author NULL = new Author("", "");

	public final String DisplayName;
	public final String SortKey;

	public Author(String displayName, String sortKey) {
		DisplayName = displayName;
		SortKey = sortKey;
	}

	public static int hashCode(Author author) {
		return author == null ? 0 : author.hashCode();
	}

	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}
		if (!(o instanceof Author)) {
			return false;
		}
		Author a = (Author)o;
		return SortKey.equals(a.SortKey) && DisplayName.equals(a.DisplayName);
	}

	@Override
	public int hashCode() {
		return SortKey.hashCode() + DisplayName.hashCode();
	}

	@Override
	public int compareTo(Author other) {
		final int byKeys = SortKey.compareTo(other.SortKey);
		return byKeys != 0 ? byKeys : DisplayName.compareTo(other.DisplayName);
	}
}
