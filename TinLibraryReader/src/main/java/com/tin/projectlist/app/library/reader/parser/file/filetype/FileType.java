package com.core.file.filetype;

import com.core.common.util.MimeType;
import com.core.file.GBFile;

import java.util.List;
/**
 * 类名： FileType.java<br>
 * 描述： 文件类型封装抽象类<br>
 * 创建者： jack<br>
 * 创建日期：2013-4-10<br>
 * 版本：  <br>
 * 修改者： <br>
 * 修改日期：<br>
 */
public abstract class FileType {
	//文件后缀
	public final String mExtension;

	protected FileType(String extension) {
		mExtension = extension;
	}
	/**
	 * acceptsFile
	 * 功能描述： 根据文件后缀进行文件校验<br>
	 * 创建者： jack<br>
	 * 创建日期：2013-4-10<br>
	 * @param file 要校验的文件
	 * @return
	 */
	public abstract boolean checkFile(GBFile file);

	//public abstract String extension(MimeType mimeType);
	/**
	 * 功能描述： 获取该文件包含的文件类型列表<br>
	 * 创建者： jack<br>
	 * 创建日期：2013-4-10<br>
	 * @return
	 */
	public abstract List<MimeType> mimeTypes();
	/**
	 * 功能描述： 获取文件的文件类型<br>
	 * 创建者： jack<br>
	 * 创建日期：2013-4-10<br>
	 * @param file 文件对象
	 * @return
	 */
	public abstract MimeType mimeType(GBFile file);
	/**
	 * 功能描述： 获取文件的原始（简单）文件类型<br>
	 * 创建者： jack<br>
	 * 创建日期：2013-4-10<br>
	 * @param file
	 * @return
	 */
	public MimeType simplifiedMimeType(GBFile file) {
		return mimeType(file);
	}
}
