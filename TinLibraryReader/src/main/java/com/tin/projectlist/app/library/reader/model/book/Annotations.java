package com.tin.projectlist.app.library.reader.model.book;

import org.json.JSONArray;
import org.json.JSONException;

import com.core.log.L;
import com.core.object.GBColor;
import com.core.text.widget.GBTextAnnotation;
import com.core.text.widget.GBTextFixedPosition;
import com.core.text.widget.GBTextHighlighting;
import com.google.gson.Gson;

/**
 * 类名： Annatitions.java<br>
 * 描述： 图书笔记，批注和高亮业务实体<br>
 * 创建者： jack<br>
 * 创建日期：2013-8-14<br>
 * 版本： <br>
 * 修改者： <br>
 * 修改日期：<br>
 */
public class Annotations {

    public static String ANNOTATION = "ANNOTATION";
    public static String NOTE = "NOTE";
    public static String HIGHLIGHT = "HIGHLIGHT";

    public int _id;
    public int bookId;
    public int accountId;
    public String nickname;
    public long addedDate; // 添加时间
    public String annotationType; // 注释类型 ANNOTATION（批注），NOTE（档案），HIGHLIGHT（高亮）
    public String annotationRange; // 注释范围信息 json组织
    public String annotationContent; // 被注释的文本信息和颜色
    public String annotationText; // 注释的文本
    public String annotationUUid; // 注释唯一码
    public long modifiedDate; // 修改时间
    public int tag;
    private Gson gson = new Gson();

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public long getAddedDate() {
        return addedDate;
    }

    public void setAddedDate(long addedDate) {
        this.addedDate = addedDate;
    }

    public String getAnnotationContent() {
        return annotationContent;
    }

    public void setAnnotationContent(String annotationContent) {
        this.annotationContent = annotationContent;
    }

    public String getAnnotationRange() {
        return annotationRange;
    }

    public void setAnnotationRange(String annotationRange) {
        this.annotationRange = annotationRange;
    }

    public String getAnnotationText() {
        return annotationText;
    }

    public void setAnnotationText(String annotationText) {
        this.annotationText = annotationText;
    }

    public String getAnnotationType() {
        return annotationType;
    }

    public void setAnnotationType(String annotationType) {
        this.annotationType = annotationType;
    }

    public String getAnnotationUUid() {
        return annotationUUid;
    }

    public void setAnnotationUUid(String annotationUUid) {
        this.annotationUUid = annotationUUid;
    }

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public long getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(long modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public int getTag() {
        return tag;
    }

    public void setTag(int tag) {
        this.tag = tag;
    }

    public Annotations(int _id, int bookId, int accountId, String nickname, long addedDate, String annotationType, String annotationRange,
                       String annotationContent, String annotationText, String annotationUUid, long modifiedDate, int tag) {
        this._id = _id;
        this.bookId = bookId;
        this.accountId = accountId;
        this.nickname = nickname;
        this.addedDate = addedDate;
        this.annotationType = annotationType;
        this.annotationRange = annotationRange;
        this.annotationContent = annotationContent;
        this.annotationText = annotationText;
        this.annotationUUid = annotationUUid;
        this.modifiedDate = modifiedDate;
        this.tag = tag;
    }
    public Annotations() {

    }
    public Annotations(int _id, int bookId, int accountId, String nickname, long addedDate, String annotationType, String annotationRange,
                       String annotationContent, String annotationText, String annotationUUid, long modifiedDate) {
        this(_id, bookId,accountId,nickname, addedDate, annotationType, annotationRange, annotationContent, annotationText,
                annotationUUid, modifiedDate, 0);
    }

    public GBTextAnnotation getAnnotation() {
        GBTextFixedPosition[] p = parsePosition();
        AnnotationContent ac = (AnnotationContent) gson.fromJson(annotationContent, AnnotationContent.class);
        return new GBTextAnnotation(_id, p[0], p[1], annotationText, ac.mAnnoContent);
    }

    public GBTextHighlighting getHighlighting() {
        GBTextFixedPosition[] p = parsePosition();
        AnnotationContent ac = (AnnotationContent) gson.fromJson(annotationContent, AnnotationContent.class);
        return new GBTextHighlighting(_id, p[0], p[1], ac.mAnnoContent);
    }

    private GBTextFixedPosition[] parsePosition() {
        JSONArray arr;
        GBTextFixedPosition[] p = new GBTextFixedPosition[2];
        try {
            arr = new JSONArray(annotationRange);
            for (int i = 0; i < arr.length(); i++) {
                p[i] = (GBTextFixedPosition) gson.fromJson(arr.getString(i), GBTextFixedPosition.class);
                if (i == 1)
                    break;
            }
        } catch (JSONException e) {
            L.e("Annotations", annotationRange);
            e.printStackTrace();
        }
        return p;
    }

    public static class AnnotationContent {
        String mAnnoContent;
        GBColor mColor;

        public AnnotationContent(String content, GBColor color) {
            mAnnoContent = content;
            mColor = color;
        }

        public String getmAnnoContent() {
            return mAnnoContent;
        }

        public void setmAnnoContent(String mAnnoContent) {
            this.mAnnoContent = mAnnoContent;
        }

        public GBColor getmColor() {
            return mColor;
        }

        public void setmColor(GBColor mColor) {
            this.mColor = mColor;
        }
    }
}
