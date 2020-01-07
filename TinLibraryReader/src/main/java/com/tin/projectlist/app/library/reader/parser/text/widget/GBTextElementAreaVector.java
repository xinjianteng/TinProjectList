package com.tin.projectlist.app.library.reader.parser.text.widget;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.tin.projectlist.app.library.reader.parser.text.model.GBTextHyperlink;
import com.tin.projectlist.app.library.reader.parser.view.PageEnum;

/**
 * 类名： GBTextElementAreaVector.java<br>
 * 描述： 文本域列表<br>
 * 创建者： yangn<br>
 * 创建日期：2013-4-18<br>
 * 版本： <br>
 * 修改者： <br>
 * 修改日期：<br>
 */
final class GBTextElementAreaVector {
    private final List<GBTextElementArea> myAreas = Collections.synchronizedList(new ArrayList<GBTextElementArea>());
    private final List<GBTextRegion> myElementRegions = Collections.synchronizedList(new ArrayList<GBTextRegion>());
    private GBTextRegion myCurrentElementRegion;

    void clear() {
        myElementRegions.clear();
        myCurrentElementRegion = null;
        myAreas.clear();
    }

    public int size() {
        return myAreas.size();
    }
    public int getRegionSize() {
        return myElementRegions.size();
    }

    // TODO: remove this unsafe method
    public GBTextElementArea get(int index) {
        return myAreas.get(index);
    }

    /**
     *
     * 功能描述：获取第一个域 创建者： yangn<br>
     * 创建日期：2013-4-17<br>
     *
     * @param
     */
    public GBTextElementArea getFirstArea() {
        synchronized (myAreas) {
            return myAreas.isEmpty() ? null : myAreas.get(0);
        }
    }

    /**
     *
     * 功能描述：获取最后一个域 创建者： yangn<br>
     * 创建日期：2013-4-17<br>
     *
     * @param
     */
    public GBTextElementArea getLastArea() {
        synchronized (myAreas) {
            return myAreas.isEmpty() ? null : myAreas.get(myAreas.size() - 1);
        }
    }

    /**
     *
     * 功能描述：添加一个文本域元素 创建者： yangn<br>
     * 创建日期：2013-4-17<br>
     *
     * @param
     */
    public boolean add(GBTextElementArea area) {
        synchronized (myAreas) {
            if (myCurrentElementRegion != null && myCurrentElementRegion.getSoul().accepts(area)) {
                myCurrentElementRegion.extend();
            } else {
                GBTextRegion.Soul soul = null;
                final GBTextHyperlink hyperlink = area.Style.Hyperlink;
                if (hyperlink.Id != null) {
                    soul = new GBTextHyperlinkRegionSoul(area, hyperlink);
                } else if (area.Element instanceof GBTextImageElement) {
                    soul = new GBTextImageRegionSoul(area, (GBTextImageElement) area.Element);
                } else if (area.Element instanceof GBAudioElement) {
                    soul = new GBAudioRegionSoul(area, (GBAudioElement) area.Element);
                } else if(area.Element instanceof GBNoteElement){
                    soul = new GBNoteRegionSoul(area, (GBNoteElement) area.Element);
                } else if (area.Element instanceof GBVideoElement) {
                    soul = new GBVideoRegionSoul(area, (GBVideoElement) area.Element);
                } else if (area.Element instanceof GBAnimObjElement) {
                    soul = new GBAnimObjRegionSoul(area, (GBAnimObjElement) area.Element);
                } else if (area.Element instanceof GBTextWord && !((GBTextWord) area.Element).isASpace()) {
                    soul = new GBTextWordRegionSoul(area, (GBTextWord) area.Element);
                }
                if (soul != null) {
                    myCurrentElementRegion = new GBTextRegion(soul, myAreas, myAreas.size());
                    myElementRegions.add(myCurrentElementRegion);
                } else {
                    myCurrentElementRegion = null;
                }
            }
            return myAreas.add(area);
        }
    }

    /**
     * 功能描述： 从开始位置获取当前页中指定元素所在的区域<br>
     * 创建者： jack<br>
     * 创建日期：2013-9-3<br>
     * 修改日期：2013-9-3<br>
     * 修改备注：支持双页模式，增加参数isLeft(是否左页)，offset(双页元素分页下标)
     *
     * @param position 位置
     * @param isLeft 是否左页
     * @param offset 分页下标
     * @return 所在区域
     */
    GBTextElementArea getFirstAfter(GBTextPosition position, boolean isLeft, int offset) {
        if (position == null) {
            return null;
        }
        int i = (isLeft ? 0 : offset);
        int end = ((isLeft && offset > 0) ? offset : myAreas.size());
        synchronized (myAreas) {
            for (; i < end; i++) {// GBTextElementArea area : myAreas) {
                final GBTextElementArea area = myAreas.get(i);
                if (position.compareTo(area) <= 0) {
                    return area;
                }
            }
        }
        return null;
    }
    /**
     * 功能描述： 从开始位置获取当前页中指定元素所在的区域<br>
     * 创建者： yangn<br>
     * 创建日期：2013-4-23<br>
     * 修改日期：2013-9-3<br>
     * 修改备注：支持双页模式，增加参数isLeft(是否左页)，offset(双页元素分页下标)
     *
     * @param positon 位置
     * @return 所在区域
     */
    GBTextElementArea getLastBefore(GBTextPosition position, boolean isLeft, int offset) {
        if (position == null) {
            return null;
        }
        int i = (isLeft ? 0 : offset);
        int end = (isLeft ? offset - 1 : myAreas.size() - 1);

        synchronized (myAreas) {
            for (; end > i; end--) {// int i = myAreas.size() - 1; i >= 0; --i)
                // {
                final GBTextElementArea area = myAreas.get(end);
                if (position.compareTo(area) >= 0) {
                    return area;
                }
            }
        }
        return null;
    }

    /**
     * 功能描述： 二分查找 x y位置的文本域 若没有返回null <br>
     * 创建者： yangn<br>
     * 创建日期：2013-4-23<br>
     *
     * @param
     */
    GBTextElementArea binarySearch(int x, int y) {
        synchronized (myAreas) {
            int left = 0;
            int right = myAreas.size();
            while (left < right) {
                final int middle = (left + right) / 2;
                final GBTextElementArea candidate = myAreas.get(middle);
                if (candidate.YStart > y) {
                    right = middle;
                } else if (candidate.YEnd < y) {
                    left = middle + 1;
                } else if (candidate.XStart > x) {
                    right = middle;
                } else if (candidate.XEnd < x) {
                    left = middle + 1;
                } else {
                    return candidate;
                }

            }
            return null;
        }
    }
    /**
     * 功能描述： 获取选中的文字区域信息<br>
     * 创建者： jack<br>
     * 创建日期：2013-4-25<br>
     *
     * @param soul
     * @return
     */
    GBTextRegion getRegion(GBTextRegion.Soul soul) {
        if (soul == null) {
            return null;
        }
        synchronized (myElementRegions) {
            for (GBTextRegion region : myElementRegions) {
                if (soul.equals(region.getSoul())) {
                    return region;
                }
            }
        }
        return null;
    }

    /**
     * 功能描述： 获取触点所在的页面元素区域<br>
     * 创建者： jack<br>
     * 创建日期：2013-9-2<br>
     * 修改日期：2013-9-3<br>
     * 修改备注：增加双翻页支持，增加参数：isLeft(是否左页),offset（元素分页下标）,
     *
     * @param x 触点x
     * @param y 触点y
     * @param maxDistance 触点幅射范围
     * @param filter 选择对象过滤器
     * @return
     */
    GBTextRegion findRegion(int x, int y, int maxDistance, GBTextRegion.Filter filter, boolean isLeft, int offset) {
        GBTextRegion bestRegion = null;
        int distance = maxDistance + 1;
        int i = (isLeft ? 0 : offset);
        int end = ((isLeft && offset > 0) ? offset : myElementRegions.size());
        synchronized (myElementRegions) {
            for (; i < end; i++) {// GBTextRegion region : myElementRegions) {
                final GBTextRegion region = myElementRegions.get(i);
                if (filter.accepts(region)) {
                    final int d = region.distanceTo(x, y);
                    if (d < distance) {
                        bestRegion = region;
                        distance = d;
                    }
                }
            }
        }
        return bestRegion;
    }

    protected GBTextRegion nextRegion(GBTextRegion currentRegion, PageEnum.DirectType direction,
                                      GBTextRegion.Filter filter) {
        synchronized (myElementRegions) {
            if (myElementRegions.isEmpty()) {
                return null;
            }

            int index = currentRegion != null ? myElementRegions.indexOf(currentRegion) : -1;

            switch (direction) {
                case RTOL :
                case UP :
                    if (index == -1) {
                        index = myElementRegions.size() - 1;
                    } else if (index == 0) {
                        return null;
                    } else {
                        --index;
                    }
                    break;
                case LTOR :
                case DOWN :
                    if (index == myElementRegions.size() - 1) {
                        return null;
                    } else {
                        ++index;
                    }
                    break;
            }

            switch (direction) {
                case RTOL :
                    for (; index >= 0; --index) {
                        final GBTextRegion candidate = myElementRegions.get(index);
                        if (filter.accepts(candidate) && candidate.isAtLeftOf(currentRegion)) {
                            return candidate;
                        }
                    }
                    break;
                case LTOR :
                    for (; index < myElementRegions.size(); ++index) {
                        final GBTextRegion candidate = myElementRegions.get(index);
                        if (filter.accepts(candidate) && candidate.isAtRightOf(currentRegion)) {
                            return candidate;
                        }
                    }
                    break;
                case DOWN : {
                    GBTextRegion firstCandidate = null;
                    for (; index < myElementRegions.size(); ++index) {
                        final GBTextRegion candidate = myElementRegions.get(index);
                        if (!filter.accepts(candidate)) {
                            continue;
                        }
                        if (candidate.isExactlyUnder(currentRegion)) {
                            return candidate;
                        }
                        if (firstCandidate == null && candidate.isUnder(currentRegion)) {
                            firstCandidate = candidate;
                        }
                    }
                    if (firstCandidate != null) {
                        return firstCandidate;
                    }
                    break;
                }
                case UP :
                    GBTextRegion firstCandidate = null;
                    for (; index >= 0; --index) {
                        final GBTextRegion candidate = myElementRegions.get(index);
                        if (!filter.accepts(candidate)) {
                            continue;
                        }
                        if (candidate.isExactlyOver(currentRegion)) {
                            return candidate;
                        }
                        if (firstCandidate == null && candidate.isOver(currentRegion)) {
                            firstCandidate = candidate;
                        }
                    }
                    if (firstCandidate != null) {
                        return firstCandidate;
                    }
                    break;
            }
        }
        return null;
    }
}
