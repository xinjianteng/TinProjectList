package com.tin.projectlist.app.library.reader.model.parser;


public abstract class JavaFormatPlugin extends FormatPlugin {
	protected JavaFormatPlugin(String fileType) {
		super(fileType);
	}

	@Override
	public Type type() {
		return Type.JAVA;
	}
}
