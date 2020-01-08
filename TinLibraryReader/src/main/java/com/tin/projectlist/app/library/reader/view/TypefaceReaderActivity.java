package com.tin.projectlist.app.library.reader.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ListView;

import com.core.file.GBPaths;
import com.core.option.GBStringOption;
import com.core.text.style.GBTextStyleCollection;
import com.geeboo.R;
import com.geeboo.read.model.book.TypeFace;
import com.geeboo.read.view.widget.AndroidFontUtil;
import com.geeboo.utils.GeeBookLoader;
import com.geeboo.utils.UIUtil;

import java.io.File;
import java.util.List;

/**
 * 类名： TypefaceReaderActivity.java<br>
 * 描述： 字体设置<br>
 * 创建者： 周波<br>
 * 创建日期：2014-7-10<br>
 * 版本： <br>
 * 修改者： <br>
 * 修改日期：<br>
 */
public class TypefaceReaderActivity extends BaseReaderActivity {

    final String TAG = "TypefaceReaderActivity";
    private ListView mListView;
    private TypefaceAdapter mAdapter;
    private GBStringOption mFontOption;
    public String currentFont;

    public static void actionView(Activity context) {
        Intent intent = new Intent(context, TypefaceReaderActivity.class);
        context.startActivityForResult(intent, 2);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.setContentView(R.layout.typeface_activity);
        this.findViewById(R.id.iv_back).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        mFontOption = GBTextStyleCollection.Instance().getBaseStyle().FontFamilyOption;
        currentFont = mFontOption.getValue();
        fontUrl = null;

        mListView = (ListView) this.findViewById(R.id.lv_font_list);
        mAdapter = new TypefaceAdapter(this);
        mListView.setAdapter(mAdapter);
        //加载数据
        getData();
    }

    public void getData(){
        mAdapter.clearData();
        //载入本地字体
        final List<TypeFace> localFamiliesList = AndroidFontUtil.getLocalFamiliesList();
        mAdapter.addLocalData(localFamiliesList);
        mAdapter.notifyDataSetChanged();

        if (GeeBookLoader.getBookMgr() == null) {
            return;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                // 填充服务端字体
                List<TypeFace> netFamiliesList = GeeBookLoader.getBookMgr().fillFamiliesList(localFamiliesList);
                Message msg = new Message();
                msg.obj = netFamiliesList;
                setFontHander.sendMessage(msg);
            }
        }).start();
    }

    public int fontProgress;
    public String fontUrl;

    Handler setFontHander = new Handler() {
        public void handleMessage(Message message) {
            List<TypeFace> netFamiliesList = (List<TypeFace>) message.obj;
            mAdapter.addNetData(netFamiliesList);
            mAdapter.notifyDataSetChanged();
        }
    };

    Handler progressHander = new Handler() {
        public void handleMessage(android.os.Message mesg) {
            if(fontProgress == 0){//开始
                mAdapter.updateBtnState(fontUrl, fontProgress);
            } else if(fontProgress > 0 && fontProgress < 100){//下载中
                mAdapter.updateBtnState(fontUrl, fontProgress);
            } else if(fontProgress==100){//下载完成
                mAdapter.updateBtnState(fontUrl, fontProgress);
                UIUtil.showMessageText(TypefaceReaderActivity.this, "下载成功");
                fontUrl = null;
                AndroidFontUtil.clearFontCache();
            } else if(fontProgress < 0){//失败
                mAdapter.updateBtnState(fontUrl, fontProgress);
                UIUtil.showMessageText(TypefaceReaderActivity.this, "字体下载失败，请稍后再试！");
                fontUrl = null;
            }
        }
    };

    public void setFont(final String fontFileName){
        String realFontName = null;
        if("".equals(fontFileName)){//默认字体
            realFontName = "serif";
        } else {
            File fontFile = new File(GBPaths.getFontsPathOption().getValue(),fontFileName);
            if(!fontFile.exists()){
                UIUtil.showMessageText(TypefaceReaderActivity.this, "字体文件不存在");
                return;
            }
            realFontName = AndroidFontUtil.getRealFontName(fontFile);
        }
        if (mFontOption.getValue().equals(realFontName)) {
            UIUtil.showMessageText(TypefaceReaderActivity.this, "已经是该字体了");
            return;
        }
        //设置字体
        mFontOption.setValue(realFontName);
        currentFont = mFontOption.getValue();
        mAdapter.notifyDataSetChanged();
//        UIUtil.dialogTask(TypefaceReaderActivity.this, "正在设置字体,请稍候...", new UIUtil.ICallback<Boolean>() {
//            @Override
//            public Boolean execTask() {
//                
//                return true;
//            }
//            @Override
//            public void handUI(Boolean result) {
//                mAdapter.notifyDataSetChanged();
//            }
//        });
    }
}
