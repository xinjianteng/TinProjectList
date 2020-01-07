package com.tin.projectlist.app.library.reader.parser.text.widget;

import com.tin.projectlist.app.library.reader.parser.view.GBPaint;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;
/**
 * 类名： GBTextHorizontalConvexHull.java<br>
 * 描述： 封装一行元素数组的在控件上绘制位置的矩阵信息<br>
 * 创建者： jack<br>
 * 创建日期：2013-8-30<br>
 * 版本： <br>
 * 修改者： <br>
 * 修改日期：<br>
 */
class GBTextHorizontalConvexHull {
    private final LinkedList<Rectangle> myRectangles = new LinkedList<Rectangle>();

    GBTextHorizontalConvexHull(GBTextElementArea[] textAreas) {
        for (GBTextElementArea area : textAreas) {
            addArea(area);
        }
        normalize();
    }

    private void addArea(GBTextElementArea area) {
        if (myRectangles.isEmpty()) {
            myRectangles.add(new Rectangle(area.XStart, area.XEnd, area.YStart, area.YEnd));
            return;
        }
        final int top = area.YStart;
        final int bottom = area.YEnd;
        for (ListIterator<Rectangle> iter = myRectangles.listIterator(); iter.hasNext();) {
            Rectangle r = iter.next();
            if (r.Bottom <= top) {
                continue;
            }
            if (r.Top >= bottom) {
                break;
            }
            if (r.Top < top) {
                final Rectangle before = new Rectangle(r);
                before.Bottom = top;
                r.Top = top;
                iter.previous();
                iter.add(before);
                iter.next();
            }
            if (r.Bottom > bottom) {
                final Rectangle after = new Rectangle(r);
                after.Top = bottom;
                r.Bottom = bottom;
                iter.add(after);
            }
            r.Left = Math.min(r.Left, area.XStart);
            r.Right = Math.max(r.Right, area.XEnd);
        }

        final Rectangle first = myRectangles.getFirst();
        if (top < first.Top) {
            myRectangles.add(0, new Rectangle(area.XStart, area.XEnd, top, Math.min(bottom, first.Top)));
        }

        final Rectangle last = myRectangles.getLast();
        if (bottom > last.Bottom) {
            myRectangles.add(new Rectangle(area.XStart, area.XEnd, Math.max(top, last.Bottom), bottom));
        }
    }
    /*
     * normalize 正常化
     */
    private void normalize() {
        Rectangle previous = null;
        for (ListIterator<Rectangle> iter = myRectangles.listIterator(); iter.hasNext();) {
            final Rectangle current = iter.next();
            if (previous != null) {
                if ((previous.Left == current.Left) && (previous.Right == current.Right)) {
                    previous.Bottom = current.Bottom;
                    iter.remove();
                    continue;
                }
                if ((previous.Bottom != current.Top) && (current.Left <= previous.Right)
                        && (previous.Left <= current.Right)) {
                    iter.previous();
                    iter.add(new Rectangle(Math.max(previous.Left, current.Left), Math.min(previous.Right,
                            current.Right), previous.Bottom, current.Top));
                    iter.next();
                }
            }
            previous = current;
        }
    }
    /**
     * 功能描述： 获取触点所在的矩阵最小距离<br>
     * 创建者： jack<br>
     * 创建日期：2013-8-30<br>
     *
     * @param x 触点x
     * @param y 触点y
     * @return
     */
    int distanceTo(int x, int y) {
        int distance = Integer.MAX_VALUE;
        for (Rectangle r : myRectangles) {
            final int xd = (r.Left > x) ? r.Left - x : ((r.Right < x) ? x - r.Right : 0);
            final int yd = (r.Top > y) ? r.Top - y : ((r.Bottom < y) ? y - r.Bottom : 0);
            distance = Math.min(distance, Math.max(xd, yd));
            if (distance == 0) {
                break;
            }
        }
        return distance;
    }

    public void draw(GBPaint context) {
        final LinkedList<Rectangle> rectangles = new LinkedList<Rectangle>(myRectangles);
        while (!rectangles.isEmpty()) {
            final LinkedList<Rectangle> connected = new LinkedList<Rectangle>();
            Rectangle previous = null;
            for (final Iterator<Rectangle> iter = rectangles.iterator(); iter.hasNext();) {
                final Rectangle current = iter.next();
                if ((previous != null) && ((previous.Left > current.Right) || (current.Left > previous.Right))) {
                    break;
                }
                iter.remove();
                connected.add(current);
                previous = current;
            }

            final LinkedList<Integer> xList = new LinkedList<Integer>();
            final LinkedList<Integer> yList = new LinkedList<Integer>();
            int x = 0, xPrev = 0;

            final ListIterator<Rectangle> iter = connected.listIterator();
            Rectangle r = iter.next();
            x = r.Right + 2;
            xList.add(x);
            yList.add(r.Top);
            while (iter.hasNext()) {
                xPrev = x;
                r = iter.next();
                x = r.Right + 2;
                if (x != xPrev) {
                    final int y = (x < xPrev) ? r.Top + 2 : r.Top;
                    xList.add(xPrev);
                    yList.add(y);
                    xList.add(x);
                    yList.add(y);
                }
            }
            xList.add(x);
            yList.add(r.Bottom + 2);

            r = iter.previous();
            x = r.Left - 2;
            xList.add(x);
            yList.add(r.Bottom + 2);
            while (iter.hasPrevious()) {
                xPrev = x;
                r = iter.previous();
                x = r.Left - 2;
                if (x != xPrev) {
                    final int y = (x > xPrev) ? r.Bottom : r.Bottom + 2;
                    xList.add(xPrev);
                    yList.add(y);
                    xList.add(x);
                    yList.add(y);
                }
            }
            xList.add(x);
            yList.add(r.Top);

            final int xs[] = new int[xList.size()];
            final int ys[] = new int[yList.size()];
            int count = 0;
            for (int xx : xList) {
                xs[count++] = xx;
            }
            count = 0;
            for (int yy : yList) {
                ys[count++] = yy;
            }
            context.drawOutline(xs, ys);
        }
    }
    /*
     * 矩形区域描述
     */
    private static final class Rectangle {
        int Left;
        int Right;
        int Top;
        int Bottom;

        Rectangle(int left, int right, int top, int bottom) {
            Left = left;
            Right = right;
            Top = top;
            Bottom = bottom;
        }

        Rectangle(Rectangle orig) {
            Left = orig.Left;
            Right = orig.Right;
            Top = orig.Top;
            Bottom = orig.Bottom;
        }
    }
}
