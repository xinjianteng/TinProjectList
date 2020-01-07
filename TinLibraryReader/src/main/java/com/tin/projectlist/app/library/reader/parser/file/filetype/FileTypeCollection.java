package com.tin.projectlist.app.library.reader.parser.file.filetype;


import com.tin.projectlist.app.library.reader.parser.common.util.MimeType;
import com.tin.projectlist.app.library.reader.parser.file.GBFile;

import java.util.Collection;
import java.util.Collections;
import java.util.TreeMap;
/**
 * 类名： FileTypeCollection.java<br>
 * 描述： 文件类型集合支持类<br>
 * 创建者： jack<br>
 * 创建日期：2013-4-10<br>
 * 版本：  <br>
 * 修改者： <br>
 * 修改日期：<br>
 */
public class FileTypeCollection {
	//单一模式
	public static final FileTypeCollection Instance = new FileTypeCollection();
	//支持的所有文件类型集合
	private final TreeMap<String,FileType> myTypes = new TreeMap<String,FileType>();

	private FileTypeCollection() {
		addType(new FileTypeFB2());
		addType(new FileTypeEpub());
		addType(new FileTypeMobipocket());
		addType(new FileTypeHtml());
		addType(new SimpleFileType("plain text", "txt", MimeType.TYPES_TXT));
		//addType(new FileTypeTxtGeb("plain text geb", "geb", MimeType.TYPES_TXT));
		addType(new SimpleFileType("RTF", "rtf", MimeType.TYPES_RTF));
		addType(new SimpleFileType("PDF", "pdf", MimeType.TYPES_PDF));
		addType(new FileTypeDjVu());
		addType(new SimpleFileType("ZIP archive", "zip", Collections.singletonList(MimeType.APP_ZIP)));
		addType(new SimpleFileType("MS Word document", "doc", MimeType.TYPES_DOC));
		//addType(new FileTypeGeb());
	}

	private void addType(FileType type) {
		myTypes.put(type.mExtension.toLowerCase(), type);
	}
	/**
	 * 功能描述： 获取所有的支持文件类型列表<br>
	 * 创建者： jack<br>
	 * 创建日期：2013-4-10<br>
	 * @return
	 */
	public Collection<FileType> types() {
		return myTypes.values();
	}
	/**
	 * 功能描述： 根据后缀名获取文件类型对象<br>
	 * 创建者： jack<br>
	 * 创建日期：2013-4-10<br>
	 * @param extension 后缀名
	 * @return
	 */
	public FileType typeById(String extension) {
		return myTypes.get(extension.toLowerCase());
	}
	/**
	 * 功能描述： 根据文件获取文件类型对象<br>
	 * 创建者： jack<br>
	 * 创建日期：2013-4-10<br>
	 * @param file 文件对象
	 * @return
	 */
	public FileType typeForFile(GBFile file) {
		for (FileType type : types()) {
			if (type.checkFile(file)) {
				return type;
			}
		}
		return null;
	}
	/**
	 * 功能描述： 获取文件类型<br>
	 * 创建者： jack<br>
	 * 创建日期：2013-4-10<br>
	 * @param file 文件对象
	 * @return
	 */
	public MimeType mimeType(GBFile file) {
		for (FileType type : types()) {
			final MimeType mime = type.mimeType(file);
			if (mime != MimeType.NULL) {
				return mime;
			}
		}
		return MimeType.UNKNOWN;
	}
	/**
	 * 功能描述： 获取基本（原始）文件类型<br>
	 * 创建者： jack<br>
	 * 创建日期：2013-4-10<br>
	 * @param file 文件对象
	 * @return
	 */
	public MimeType simplifiedMimeType(GBFile file) {
		for (FileType type : types()) {
			final MimeType mime = type.simplifiedMimeType(file);
			if (mime != MimeType.NULL) {
				return mime;
			}
		}
		return MimeType.UNKNOWN;
	}
}
