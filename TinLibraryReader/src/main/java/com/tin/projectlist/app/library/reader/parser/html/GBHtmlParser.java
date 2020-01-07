package com.tin.projectlist.app.library.reader.parser.html;


import com.tin.projectlist.app.library.reader.parser.common.util.ArrayUtils;
import com.tin.projectlist.app.library.reader.parser.object.GBByteBuffer;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
/**
 * 类名： GBHtmlParser.java#ZLHtmlParser<br>
 * 描述： html文件解析类<br>
 * 创建者： jack<br>
 * 创建日期：2013-4-8<br>
 * 版本：  <br>
 * 修改者： <br>
 * 修改日期：<br>
 */
final class GBHtmlParser {
	//文件读取状态定义
	private static final byte START_DOCUMENT = 0;  //解析文件开始状态
	private static final byte START_TAG = 1;   //当解析完一个开始标签的状态例如 <p>
	private static final byte END_TAG = 2; //当解析完一个结束标签的状态例如 </p>
	private static final byte TEXT = 3; //解析到文本内容的状态
	//private static final byte IGNORABLE_WHITESPACE = 4;
	//private static final byte PROCESSING_INSTRUCTION = 5;
	private static final byte COMMENT = 6;  //解析到类似注释需要忽略的标签状态
	private static final byte LANGLE = 7;   //解析到 < 标签的状态
	private static final byte WS_AFTER_START_TAG_NAME = 8;
	private static final byte WS_AFTER_END_TAG_NAME = 9;
	private static final byte WAIT_EQUALS = 10;
	private static final byte WAIT_ATTRIBUTE_VALUE = 11;
	private static final byte SLASH = 12;
	private static final byte ATTRIBUTE_NAME = 13;
	private static final byte S_ATTRIBUTE_VALUE = 14;
	private static final byte DEFAULT_ATTRIBUTE_VALUE = 15;
	private static final byte COMMENT_MINUS = 17;
	private static final byte D_ATTRIBUTE_VALUE = 18;
	private static final byte SCRIPT = 19;
	private static final byte ENTITY_REF = 20;   //解析到 & 符号，表示有转义标签的状态

	private static GBByteBuffer unique(HashMap<GBByteBuffer,GBByteBuffer> strings, GBByteBuffer container) {
		GBByteBuffer s = strings.get(container);
		if (s == null) {
			s = new GBByteBuffer(container);
			strings.put(s, s);
		}
		container.clear();
		return s;
	}

	private final GBHtmlReader myReader;
	private final InputStream myStream;

	public GBHtmlParser(GBHtmlReader htmlReader, InputStream stream) throws IOException {
		myReader = htmlReader;
		myStream = stream;
	}
	/**
	 * 功能描述： 读取html文件，解析存储信息<br>
	 * 创建者： jack<br>
	 * 创建日期：2013-4-8<br>
	 * @throws IOException
	 */
	public void doIt() throws IOException {
		final InputStream stream = myStream;
		final GBHtmlReader htmlReader = myReader;
		byte[] buffer = new byte[8192];
		final GBByteBuffer tagName = new GBByteBuffer();
		final GBByteBuffer attributeName = new GBByteBuffer();
		final GBByteBuffer attributeValue = new GBByteBuffer();
		final GBByteBuffer entityName = new GBByteBuffer();
		final HashMap<GBByteBuffer,GBByteBuffer> strings = new HashMap<GBByteBuffer,GBByteBuffer>();
		final GBHtmlAttrMap attributes = new GBHtmlAttrMap();
		boolean scriptOpened = false;
		//boolean html = false;
		int bufferOffset = 0;
		int offset = 0;

		byte state = START_DOCUMENT;
		while (true) {
			final int count = stream.read(buffer);
			if (count <= 0) {
				return;
			}
			if (count < buffer.length) {
				buffer = ArrayUtils.createCopy(buffer, count, count);
			}
			int startPosition = 0;
			try {
				for (int i = -1;;) {
					mainSwitchLabel:
					switch (state) {
						case START_DOCUMENT:
							while (buffer[++i] != '<') {}
							state = LANGLE;
							break;
						case LANGLE:
							offset = bufferOffset + i;
							switch (buffer[++i]) {
								case '/':
									state = END_TAG;
									startPosition = i + 1;
									break;
								case '!':
								{
									switch (buffer[++i]) {
										case '-':
											state = COMMENT_MINUS;
											i--;
											break;
										default:
											state = COMMENT;
											break;
									}
								}
								case '?':
									state = COMMENT;
									break;
								default:
									state = START_TAG;
									startPosition = i;
									break;
							}
							break;
						case SCRIPT:
							while (true) {
								if (buffer[++i] == '<') {
									if (buffer[++i] == '/') {
										state = END_TAG;
										startPosition = i + 1;
										break mainSwitchLabel;
									}
								}
							}
						case COMMENT_MINUS:
						{
							int minusCounter = 0;
							while (minusCounter != 2) {
								switch (buffer[++i]) {
									case '-':
										minusCounter++;
										break;
									default :
										minusCounter = 0;
										break;
								}
							}
							switch (buffer[++i]) {
								case '>':
									state = TEXT;
									startPosition = i + 1;
									break mainSwitchLabel;
							}
						}

						case COMMENT :
							while (true) {
								switch (buffer[++i]) {
									case '>':
										state = TEXT;
										startPosition = i + 1;
										break mainSwitchLabel;
								}
							}
						case START_TAG:
							while (true) {
								switch (buffer[++i]) {
									case 0x0008:
									case 0x0009:
									case 0x000A:
									case 0x000B:
									case 0x000C:
									case 0x000D:
									case ' ':
										state = WS_AFTER_START_TAG_NAME;
										tagName.append(buffer, startPosition, i - startPosition);
										break mainSwitchLabel;
									case '>':
										state = TEXT;
										tagName.append(buffer, startPosition, i - startPosition);
									{
										GBByteBuffer stringTagName = unique(strings, tagName);
										processStartTag(htmlReader, stringTagName, offset, attributes);
										if (stringTagName.equalsToLCString("script")) {
											scriptOpened = true;
											state = SCRIPT;
											break mainSwitchLabel;
										}
											/*if (stringTagName.equalsToLCString("html")) {
												html = true;
											}*/
									}
									startPosition = i + 1;
									break mainSwitchLabel;
									case '/':
										state = SLASH;
										tagName.append(buffer, startPosition, i - startPosition);
										//processFullTag(htmlReader, unique(strings, tagName), attributes);
										break mainSwitchLabel;
								}
							}
						case END_TAG:
							while (true) {
								switch (buffer[++i]) {
									case 0x0008:
									case 0x0009:
									case 0x000A:
									case 0x000B:
									case 0x000C:
									case 0x000D:
									case ' ':
										state = WS_AFTER_END_TAG_NAME;
										tagName.append(buffer, startPosition, i - startPosition);
										break mainSwitchLabel;
									case '>':

										tagName.append(buffer, startPosition, i - startPosition);
									{
										GBByteBuffer stringTagName = unique(strings, tagName);
										processEndTag(htmlReader, stringTagName);
										if (scriptOpened) {
										}
										if (stringTagName.equalsToLCString("script")) {
											scriptOpened = false;
										}
									}
									if (scriptOpened) {
										state = SCRIPT;
									} else {
										state = TEXT;
										startPosition = i + 1;
									}
									break mainSwitchLabel;
								}
							}
						case WS_AFTER_START_TAG_NAME:
							switch (buffer[++i]) {
								case '>':
								{
									GBByteBuffer stringTagName = unique(strings, tagName);
									processStartTag(htmlReader, stringTagName, offset, attributes);
									if (stringTagName.equalsToLCString("script")) {
										scriptOpened = true;
										state = SCRIPT;
										break mainSwitchLabel;
									}
								}
								state = TEXT;
								startPosition = i + 1;
								break;
								case '/':
									state = SLASH;
									break;
								case 0x0008:
								case 0x0009:
								case 0x000A:
								case 0x000B:
								case 0x000C:
								case 0x000D:
								case ' ':
									break;
								default:
									state = ATTRIBUTE_NAME;
									startPosition = i;
									break;
							}
							break;

						case WS_AFTER_END_TAG_NAME:
							switch (buffer[++i]) {
								case '>':
								{
									GBByteBuffer stringTagName = unique(strings, tagName);
									processEndTag(htmlReader, stringTagName);
									if (stringTagName.equalsToLCString("script")) {
										scriptOpened = false;
									}
								}
								if (scriptOpened) {
									state = SCRIPT;
								} else {
									state = TEXT;
									startPosition = i + 1;
								}
								break;
							}
							break;

						case ATTRIBUTE_NAME:
							while (true) {
								switch (buffer[++i]) {
									case '=':
										attributeName.append(buffer, startPosition, i - startPosition);
										state = WAIT_ATTRIBUTE_VALUE;
										break mainSwitchLabel;
									case 0x0008:
									case 0x0009:
									case 0x000A:
									case 0x000B:
									case 0x000C:
									case 0x000D:
									case ' ':
										attributeName.append(buffer, startPosition, i - startPosition);
										state = WAIT_EQUALS;
										break mainSwitchLabel;
								}
							}
						case WAIT_EQUALS:
							while (true) {
								switch (buffer[++i]) {
									case '=':
										state = WAIT_ATTRIBUTE_VALUE;
										break mainSwitchLabel;
								}
							}
						case WAIT_ATTRIBUTE_VALUE:
							while (true) {
								switch (buffer[++i]) {
									case ' ' :
										break;
									case '\t' :
										break;
									case '\n' :
										break;
									case '\'':
										state = S_ATTRIBUTE_VALUE;
										startPosition = i + 1;
										break mainSwitchLabel;
									case '"' :
										state = D_ATTRIBUTE_VALUE;
										startPosition = i + 1;
										break mainSwitchLabel;
									default :
										state = DEFAULT_ATTRIBUTE_VALUE;
										startPosition = i;
										break mainSwitchLabel;
								}
							}
						case DEFAULT_ATTRIBUTE_VALUE:
							while (true) {
								i++;
								if ((buffer[i] == ' ') || (buffer[i] == '\'')
										|| (buffer[i] == '"') || (buffer[i] == '>')) {
									attributeValue.append(buffer, startPosition, i - startPosition);
									attributes.put(unique(strings, attributeName), new GBByteBuffer(attributeValue));
									attributeValue.clear();
								}
								switch (buffer[i]) {
									case ' ':
									case '\'':
									case '"':
										state = WS_AFTER_START_TAG_NAME;
										break mainSwitchLabel;
									case '/':
										state = SLASH;
										break mainSwitchLabel;
									case '>':
										GBByteBuffer stringTagName = unique(strings, tagName);

										processStartTag(htmlReader, stringTagName, offset, attributes);
										if (stringTagName.equalsToLCString("script")) {
											scriptOpened = true;
											state = SCRIPT;
											break mainSwitchLabel;
										}
										state = TEXT;
										startPosition = i + 1;
										break mainSwitchLabel;
								}
							}
						case D_ATTRIBUTE_VALUE:
							while (true) {
								switch (buffer[++i]) {
									case '"':
										attributeValue.append(buffer, startPosition, i - startPosition);
										state = WS_AFTER_START_TAG_NAME;
										attributes.put(unique(strings, attributeName), new GBByteBuffer(attributeValue));
										attributeValue.clear();
										break mainSwitchLabel;
								}
							}

						case S_ATTRIBUTE_VALUE:
							while (true) {
								switch (buffer[++i]) {
									case '\'':
										attributeValue.append(buffer, startPosition, i - startPosition);
										state = WS_AFTER_START_TAG_NAME;
										attributes.put(unique(strings, attributeName), new GBByteBuffer(attributeValue));
										attributeValue.clear();
										break mainSwitchLabel;
								}
							}
						case SLASH:
							while (true) {
								switch (buffer[++i]) {
									case ' ':
										break;
									case '>':
										processFullTag(htmlReader, unique(strings, tagName), offset, attributes);
										state = TEXT;
										startPosition = i + 1;
										break mainSwitchLabel;
									default :
										state = DEFAULT_ATTRIBUTE_VALUE;
										break mainSwitchLabel;
								}
							}
						case TEXT:
							while (true) {
								switch (buffer[++i]) {
									case '<':
										if (i > startPosition) {
											htmlReader.byteDataHandler(buffer, startPosition, i - startPosition);
										}
										state = LANGLE;
										break mainSwitchLabel;
									case '&':
										if (i > startPosition) {
											htmlReader.byteDataHandler(buffer, startPosition, i - startPosition);
										}
										state = ENTITY_REF;
										startPosition = i + 1;
										break mainSwitchLabel;
								}
							}
						case ENTITY_REF:
							while (true) {
								byte sym = buffer[++i];
								if (sym == ';') {
									entityName.append(buffer, startPosition, i - startPosition);
									state = TEXT;
									startPosition = i + 1;
									htmlReader.entityDataHandler(unique(strings, entityName).toString());
									entityName.clear();
									break mainSwitchLabel;
								} else if ((sym != '#') && !Character.isLetterOrDigit(sym)) {
									entityName.append(buffer, startPosition, i - startPosition);
									state = TEXT;
									startPosition = i;
									htmlReader.byteDataHandler(new byte[] { '&' }, 0, 1);
									htmlReader.byteDataHandler(entityName.getmData(), 0, entityName.size());
									entityName.clear();
									break mainSwitchLabel;
								}
							}
					}
				}
			} catch (ArrayIndexOutOfBoundsException e) {
				switch (state) {
					case START_TAG:
					case END_TAG:
						tagName.append(buffer, startPosition, count - startPosition);
						break;
					case ATTRIBUTE_NAME:
						attributeName.append(buffer, startPosition, count - startPosition);
						break;
					case S_ATTRIBUTE_VALUE:
					case D_ATTRIBUTE_VALUE:
						attributeValue.append(buffer, startPosition, count - startPosition);
						break;
					case TEXT:
						htmlReader.byteDataHandler(buffer, startPosition, count - startPosition);
						break;
					case ENTITY_REF:
						entityName.append(buffer, startPosition, count - startPosition);
						break;
				}
			}
			bufferOffset += count;
		}
	}
	/**
	 * 功能描述： 解析完一个完整的结束标签(<a href="#" />)执行回调<br>
	 * 创建者： jack<br>
	 * 创建日期：2013-4-9<br>
	 * @param htmlReader html解析器实现类
	 * @param tagName 标签名称
	 * @param offset 标签的名称的开始位置
	 * @param attributes 标签属性集合
	 */
	private static void processFullTag(GBHtmlReader htmlReader, GBByteBuffer tagName, int offset, GBHtmlAttrMap attributes) {
		String stringTagName = tagName.toString();
		htmlReader.startElementHandler(stringTagName, offset, attributes);
		htmlReader.endElementHandler(stringTagName);
		attributes.clear();
	}
	/**
	 * 功能描述： 解析完一个开始标签(<p>)的结束位置时执行的回调<br>
	 * 创建者： jack<br>
	 * 创建日期：2013-4-9<br>
	 * @param htmlReader html解析器实现类
	 * @param tagName 标签名称
	 * @param offset 标签名称的开始位置
	 * @param attributes 标签包含属性的集合
	 */
	private static void processStartTag(GBHtmlReader htmlReader, GBByteBuffer tagName, int offset, GBHtmlAttrMap attributes) {
		htmlReader.startElementHandler(tagName.toString(), offset, attributes);
		attributes.clear();
	}
	/**
	 * 功能描述： 解析到一个结束标签（</p>）执行的回调<br>
	 * 创建者： jack<br>
	 * 创建日期：2013-4-9<br>
	 * @param htmlReader html解析器实现类
	 * @param tagName 标签名称
	 */
	private static void processEndTag(GBHtmlReader htmlReader, GBByteBuffer tagName) {
		htmlReader.endElementHandler(tagName.toString());
	}
}
