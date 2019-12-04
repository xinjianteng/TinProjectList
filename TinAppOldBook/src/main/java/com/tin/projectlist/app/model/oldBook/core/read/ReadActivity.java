package com.tin.projectlist.app.model.oldBook.core.read;

import android.view.View;

import com.tin.projectlist.app.model.oldBook.R;
import com.tin.projectlist.app.model.oldBook.mvp.MvpActivity;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import com.tin.projectlist.app.model.oldBook.readingTool.epub.view.DirectionalViewpager;


@ContentView(R.layout.activity_read)
public class ReadActivity extends MvpActivity<ReadPresenter> implements ReadContract.View{


    @ViewInject(R.id.epubViewPager)
    private DirectionalViewpager readPageView;


    @Override
    protected ReadPresenter createPresenter() {
        return new ReadPresenter();
    }

    @Override
    protected View getTitleId() {
        return null;
    }

    @Override
    protected void initData() {
        String path = "file:///android_asset/test.epub";
        readPageView.setOnPageChangeListener(new DirectionalViewpager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                mTocListAdapter.setCurrentChapter(position + 1);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        if (mBook != null && mSpineReferences != null && mTocReferences != null) {

            mAdapter = new EPubReaderAdapter(getSupportFragmentManager(),
                    mSpineReferences, mBook, mFileName, mIsSmilParsed);
            viewpager.setAdapter(mAdapter);
        }


    }





}
