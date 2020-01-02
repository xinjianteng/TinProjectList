/*
 * Copyright (C) 2007-2013 Geometer Plus <contact@geometerplus.com>
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301, USA.
 */

package com.core.object;

/**
 * 描述：自定义布尔类，三个值的布尔类 <br>
 * 创建者： 符晨<br>
 * 创建日期：2013-3-29<br>
 */
public enum GBBoolean3 {
	B3_FALSE("false"),
	B3_TRUE("true"),
	B3_UNDEFINED("undefined");

	public final String Name;

	private GBBoolean3(String name) {
		Name = name;
	}

	public static GBBoolean3 getByName(String name) {
		for (GBBoolean3 b3 : values()) {
			if (b3.Name.equals(name)) {
				return b3;
			}
		}
		return B3_UNDEFINED;
	}
}
