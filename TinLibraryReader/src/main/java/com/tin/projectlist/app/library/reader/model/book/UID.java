package com.tin.projectlist.app.library.reader.model.book;
/**
 * 类名： UID.java<br>
 * 描述： 文件唯一标示信息封装<br>
 * 创建者： jack<br>
 * 创建日期：2013-4-25<br>
 * 版本：  <br>
 * 修改者： <br>
 * 修改日期：<br>
 */
public class UID {
	public final String Type;  //摘要算法的标准名称
	public final String Id;  //

	public UID(String type, String id) {
		Type = type;
		Id = id;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof UID)) {
			return false;
		}
		final UID u = (UID)o;
		return Type.equals(u.Type) && Id.equals(u.Id);
	}

	@Override
	public int hashCode() {
		return Type.hashCode() + Id.hashCode();
	}
}
