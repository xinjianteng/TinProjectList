package com.tin.projectlist.app.library.reader.parser.zip;

import java.io.IOException;

/**
 * 描述：Zip异常 <br>
 * 创建者： 符晨<br>
 * 创建日期：2013-4-1<br>
 */
@SuppressWarnings("serial")
public class ZipException extends IOException {
	ZipException(String message) {
		super(message);
	}
}
