package com.tin.projectlist.app.library.reader.parser.text.linbreak;

public interface LineBreakerInterface {

	public void init();
	public void setLineBreaksForCharArray(char[] data, int offset, int length, String lang, byte[] breaks);
	public void setLineBreaksForString(String data, String lang, byte[] breaks);

}
