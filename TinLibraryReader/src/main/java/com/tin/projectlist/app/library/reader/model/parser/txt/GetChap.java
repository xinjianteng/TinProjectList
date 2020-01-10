package com.tin.projectlist.app.library.reader.model.parser.txt;

import com.geeboo.read.model.parser.txt.catalog.TxtCatalogRegExpMatcher;

/**
 * 类名： GetChap.java<br>
 * 描述： 提取<br>
 * 创建者： jack<br>
 * 创建日期：2013-9-30<br>
 * 版本： <br>
 * 修改者： <br>
 * 修改日期：<br>
 */
public class GetChap extends Thread {

    private TxtParser mParser;
    private int mStart;
    private int mEnd;
    private int mIdx; // 多线程编号
    private OnAnalyzeLisener mLisener;
    public final static TxtCatalogRegExpMatcher mTcrem = new TxtCatalogRegExpMatcher();

    public GetChap(TxtParser parser, int start, int end, int index, OnAnalyzeLisener lisener) {
        this.mParser = parser;
        this.mStart = start;
        this.mEnd = end;
        this.mIdx = index;
        this.mLisener = lisener;
    }

    // List<String> mList = new ArrayList<String>();
    // public List<String> getmList() {
    // return mList;
    // }
    @Override
    public void run() {
        super.run();
        if (mLisener != null)
            mLisener.onStart();
        long i = mStart;
        String temp = "";
        while (i < mEnd) {
            try {
                byte[] bt = mParser.readParagrah(i);
                // String str1 = new String(bt, mParser.mEncode);
                if (mParser.checkChapPara(bt)) {
                    String str = new String(bt, mParser.mEncode).trim();
                    // str = str.replace("　", ""); // 全角空格为空格
                    if (mTcrem.isBodyCatalogElement(str)) {
                        if (!str.trim().equals(temp)) {
                            temp = str.trim();
                            // mParser.mChapters.put(temp, i);
                            // mList.add(temp)
                            if (mLisener != null) {
                                mLisener.onAnalyzeProgress((int) Math.abs(i * 100 / (mEnd - mStart)), temp, i);
                            }
                        }
                    }
                }
                i += bt.length;
                if (mLisener != null)
                    mLisener.onPragrahProgress(i, bt.length);
            } catch (Exception e) {
                e.printStackTrace();
                if (mLisener != null)
                    mLisener.onError();
                return;
            }
            // }
        }
        if (mLisener != null)
            mLisener.onFinish();
    }
    public interface OnAnalyzeLisener {
        // 文件分析开始
        public void onStart();
        // 分析段落
        public void onPragrahProgress(long readLen, int paragrahLen);
        // 分析进度
        public void onAnalyzeProgress(int progress, String chapterName, long index);
        // 顺序索引
        public void onFinish();
        // 异常
        public void onError();
    }

}
