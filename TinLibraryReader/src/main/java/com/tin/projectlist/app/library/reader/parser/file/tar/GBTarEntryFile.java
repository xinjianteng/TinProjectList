package com.tin.projectlist.app.library.reader.parser.file.tar;

import com.core.file.GBFile;
import com.core.file.zip.GBArchiveEntryFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
/**
 * 类名： GBTarEntryFile.java#ZLTarEntryFile<br>
 * 描述： tar压缩文件封装类<br>
 * 创建者： jack<br>
 * 创建日期：2013-4-10<br>
 * 版本：  <br>
 * 修改者： <br>
 * 修改日期：<br>
 */
public final class GBTarEntryFile extends GBArchiveEntryFile {
	/**
	 * 功能描述： 获取tar压缩文件中的所有文件列表<br>
	 * 创建者： jack<br>
	 * 创建日期：2013-4-10<br>
	 * @param archive
	 * @return
	 */
	public static List<GBFile> archiveEntries(GBFile archive) {
		try {
			InputStream stream = archive.getInputStream();
			if (stream != null) {
				LinkedList<GBFile> entries = new LinkedList<GBFile>();
				GBTarHeader header = new GBTarHeader();
				while (header.read(stream)) {
					if (header.IsRegularFile) {
						entries.add(new GBTarEntryFile(archive, header.Name));
					}
					final int lenToSkip = (header.Size + 0x1ff) & -0x200;
					if (lenToSkip < 0) {
						break;
					}
					if (stream.skip(lenToSkip) != lenToSkip) {
						break;
					}
					header.erase();
				}
				stream.close();
				return entries;
			}
		} catch (IOException e) {
		}
		return Collections.emptyList();
	}

	public GBTarEntryFile(GBFile parent, String name) {
		super(parent, name);
	}

	@Override
	public boolean exists() {
		// TODO: optimize
		return myParent.exists() && archiveEntries(myParent).contains(this);
	}

	@Override
	public long size() {
		throw new RuntimeException("Not implemented yet.");
	}

	@Override
	public InputStream getInputStream() throws IOException {
		return new GBTarInputStream(myParent.getInputStream(), myName);
	}
}
