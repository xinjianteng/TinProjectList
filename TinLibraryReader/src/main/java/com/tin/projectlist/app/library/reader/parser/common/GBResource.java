package com.core.common;

import com.core.file.GBFile;
import com.core.file.GBResourceFile;
import com.core.option.GBStringOption;
import com.core.support.Language;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * 资源类
 * @author fuchen
 * @date 2013-4-11
 */
abstract public class GBResource {
	public final String Name;

	public static final GBStringOption LanguageOption =
			new GBStringOption("LookNFeel", "Language", Language.SYSTEM_CODE);

	private static final List<Language> ourLanguages = new LinkedList<Language>();

	/**
	 * 获取资源文件中Language列表
	 * @return
	 * @author fuchen
	 * @date 2013-4-11
	 */
	public static List<Language> languages() {
		if (ourLanguages.isEmpty()) {
			final GBResource resource = GBResource.resource("language-self");
			final GBFile dir = GBResourceFile.createResourceFile("resources/application");
			for (GBFile file : dir.children()) {
				final String name = file.getShortName();
				if (name.endsWith(".xml")) {
					final String code = name.substring(0, name.length() - 4);
					ourLanguages.add(new Language(code, resource.getResource(code).getValue()));
				}
			}
			Collections.sort(ourLanguages);
		}
		final List<Language> allLanguages = new ArrayList<Language>(ourLanguages.size() + 1);
		allLanguages.add(new Language(Language.SYSTEM_CODE));
		allLanguages.addAll(ourLanguages);
		return allLanguages;
	}

	/**
	 * 根据key获取资源
	 * @param key
	 * @return
	 * @author fuchen
	 * @date 2013-4-11
	 */
	public static GBResource resource(String key) {
		GBTreeResource.buildTree();
		if (GBTreeResource.ourRoot == null) {
			return GBMissingResource.Instance;
		}
		return GBTreeResource.ourRoot.getResource(key);
	}

	protected GBResource(String name) {
		Name = name;
	}

	/**
	 * 资源是否有值
	 * @return
	 * @author fuchen
	 * @date 2013-4-11
	 */
	abstract public boolean hasValue();

	/**
	 * 获取值
	 * @return
	 * @author fuchen
	 * @date 2013-4-11
	 */
	abstract public String getValue();

	/**
	 * 从ConditionalValues中获取值
	 * @param condition
	 * @return
	 * @author fuchen
	 * @date 2013-4-11
	 */
	abstract public String getValue(int condition);


	/**
	 * 获取字节点
	 * @param key
	 * @return
	 * @author fuchen
	 * @date 2013-4-11
	 */
	abstract public GBResource getResource(String key);
}
