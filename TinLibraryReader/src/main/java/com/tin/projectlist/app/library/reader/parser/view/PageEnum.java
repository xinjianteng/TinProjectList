package com.tin.projectlist.app.library.reader.parser.view;

/**
 * 类名： PageEnum.java#zlView,zlPaintContext<br>
 * 描述： 图书翻页业务相关枚举类<br>
 * 创建者： jack<br>
 * 创建日期：2013-3-28<br>
 * 版本： <br>
 * 修改者： <br>
 * 修改日期：<br>
 */
public class PageEnum {
    /**
     * 类名： PageIndex.java<br>
     * 描述： 页码索引枚举<br>
     * 创建者： jack<br>
     * 创建日期：2013-3-28<br>
     * 版本： <br>
     * 修改者： <br>
     * 修改日期：<br>
     */
    public static enum PageIndex {
        PREVIOUS, CURRENT, NEXT;
        /**
         * 功能描述： 获取下一页<br>
         * 创建者： jack<br>
         * 创建日期：2013-3-28<br>
         *
         * @param
         */
        public PageIndex getNext() {
            switch (this) {
                case PREVIOUS :
                    return CURRENT;
                case CURRENT :
                    return NEXT;
                default :
                    return null;
            }
        }
        /**
         *
         * 功能描述： 获取上一页<br>
         * 创建者： jack<br>
         * 创建日期：2013-3-28<br>
         *
         * @param
         */
        public PageIndex getPrevious() {
            switch (this) {
                case NEXT :
                    return CURRENT;
                case CURRENT :
                    return PREVIOUS;
                default :
                    return null;
            }
        }
        // 服务于双翻页
        public boolean isLeft = true;
    };
    /**
     * 类名： PageEnum.java<br>
     * 描述： 翻页方式<br>
     * 创建者： jack<br>
     * 创建日期：2013-3-28<br>
     * 版本： <br>
     * 修改者： <br>
     * 修改日期：<br>
     */
    public static enum DirectType {
        LTOR(true), // 左向右翻
        RTOL(true), // 右向左翻
        UP(false), // 向上翻页
        DOWN(false); // 向下翻页

        // 是否水平模式
        public final boolean mIsHorizontal;

        DirectType(boolean isHorizontal) {
            mIsHorizontal = isHorizontal;
        }
    };
    /**
     * 类名： Anim.java<br>
     * 描述： 翻页动画类型<br>
     * 创建者： jack<br>
     * 创建日期：2013-3-28<br>
     * 版本： <br>
     * 修改者： <br>
     * 修改日期：<br>
     */
    public static enum Anim {

        NONE, // 无
        CURL, // 翻页
        FLIP, // 平移滑动
        FLIP_FRAME // 带层次的平移滑动
    }

    public static Anim parseAnim(int anim) {

        if (anim == Anim.NONE.ordinal()) {
            return Anim.NONE;
        } else if (anim == Anim.FLIP.ordinal()) {
            return Anim.FLIP;
        } else if (anim == Anim.FLIP_FRAME.ordinal()) {
            return Anim.FLIP_FRAME;
        } else if (anim == Anim.CURL.ordinal()) {
            return Anim.CURL;
        } else {
            return Anim.FLIP;
        }

    }

    /**
     * 类名： PageEnum.java<br>
     * 描述： 阅读背景绘制模式<br>
     * 创建者： jack<br>
     * 创建日期：2013-3-28<br>
     * 版本： <br>
     * 修改者： <br>
     * 修改日期：<br>
     */
    public static enum PageBgMode {
        TILE, // 平铺
        STRETCH // 拉伸
    }
    /**
     * 类名： PageEnum.java<br>
     * 描述： 图片显示模式<br>
     * 创建者： jack<br>
     * 创建日期：2013-3-28<br>
     * 版本： <br>
     * 修改者： <br>
     * 修改日期：<br>
     */
    public static enum ImgFitType {
        ORIGINAL, // 原始尺寸显示
        AUTO_FIT, // 自适应填充
        MAX_FIT // 超出最大尺寸时自适应
    }
}
