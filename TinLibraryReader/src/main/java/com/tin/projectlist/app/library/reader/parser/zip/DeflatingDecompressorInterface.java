package com.tin.projectlist.app.library.reader.parser.zip;


public interface DeflatingDecompressorInterface {
//	static {
//		System.loadLibrary("DeflatingDecompressor-v3");
//	}


	public int startInflating();
	public void endInflating(int inflatorId);
	public long inflate(int inflatorId, byte[] in, int inOffset, int inLength, byte[] out);
}
