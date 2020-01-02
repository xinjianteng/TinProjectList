package com.core.text.model;

import com.core.text.model.style.GBTextStyleEntryProxy;

/**
 *
 * 描述：段信息 创建者： 燕冠楠<br>
 * 创建日期：2013-3-26<br>
 */
public interface GBTextParagraph {
    /**
     *
     * 描述：段类型 创建者： 燕冠楠<br>
     * 创建日期：2013-3-26<br>
     */
    public static interface Entry {
        /**
         * 文本段
         */
        byte TEXT = 1;
        /**
         * 图片段
         */
        byte IMAGE = 2;
        /**
         * 控件段 isStart +=\n
         */
        byte CONTROL = 3;
        /**
         * 超链接段
         */
        byte HYPERLINK_CONTROL = 4;
        /**
         * 样式段
         */
        byte STYLE_CSS = 5;

        byte STYLE_OTHER = 6;
        byte STYLE_CLOSE = 7;
        byte FIXED_HSPACE = 8;
        byte RESET_BIDI = 9;

        byte HTML5_AUDIO_CONTROL = 10;
        byte HTML5_VIDEO_CONTROL = 11;
        byte HTML5_FILE_CTR=12;
        byte HTML5_NOTE_CONTROL = 13;
        // byte STYLE_LINK=10;//样式链接段
    }

    /**
     *
     * 描述： 段条目迭代器 <br>
     * 创建者： 燕冠楠<br>
     * 创建日期：2013-3-26<br>
     */
    public static interface EntryIterator {
        byte getType();

        char[] getTextData();

        int getTextOffset();

        int getTextLength();

        byte getControlKind();

        boolean getControlIsStart();

        byte getHyperlinkType();

        String getHyperlinkId();

        public int getHyperlinkChpFileIndex();

        GBImageEntry getImageEntry();
        GBVideoEntry getVideoEntry();
        GBNoteEntry getNoteEntry();
        GBAudioEntry getAudioEntry();
        GBFileCtrEntry getFileCtrEntry();

        // 原返回类型GBTextStyleEntry改为GBTextStyleEntryProxy modify by yangn
        GBTextStyleEntryProxy getStyleEntry();

        short getFixedHSpaceLength();

        boolean hasNext();

        /**
         * 返回下一段信息 包含 段类别 段偏移量 段长度 段数据char[] 功能描述： <br>
         * 创建者： yangn<br>
         * 创建日期：2013-4-11<br>
         *
         * @param
         */
        void next();
    }

    public EntryIterator iterator();

    /**
     *
     * 描述： 段 类别 创建者： 燕冠楠<br>
     * 创建日期：2013-3-26<br>
     */
    public static interface Kind {
        /**
         * 文本
         */
        byte TEXT_PARAGRAPH = 0;
        byte CSS_PARAGRAPH = 7;
        byte TREE_PARAGRAPH = 1; // 树型结构文本 <tr><td></td><td></td></tr>
        /**
         * 空行
         */
        byte EMPTY_LINE_PARAGRAPH = 2;
        byte BEFORE_SKIP_PARAGRAPH = 3;
        byte AFTER_SKIP_PARAGRAPH = 4;
        byte END_OF_SECTION_PARAGRAPH = 5;
        byte END_OF_TEXT_PARAGRAPH = 6;
    };

    byte getKind();
}
