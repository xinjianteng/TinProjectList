package com.tin.projectlist.app.model.oldBook.core.read;

import android.support.v7.widget.ListPopupWindow;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.PopupWindow;

import com.tin.projectlist.app.model.oldBook.R;
import com.tin.projectlist.app.model.oldBook.core.read.epub.EpubCatalogAdapter;
import com.tin.projectlist.app.model.oldBook.mvp.MvpActivity;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import com.tin.projectlist.app.model.oldBook.readingTool.epub.view.DirectionalViewpager;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import nl.siegmann.epublib.domain.TOCReference;
import nl.siegmann.epublib.epub.EpubReader;


@ContentView(R.layout.activity_read)
public class ReadActivity extends MvpActivity<ReadPresenter> implements ReadContract.View{


    @ViewInject(R.id.epubViewPager)
    private DirectionalViewpager readPageView;


    private EPubReaderAdapter mAdapter;

    private EpubCatalogAdapter epubCatalogAdapter;


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

        epubCatalogAdapter = new EpubCatalogAdapter(this, mChapterList, "", 1);

    }


    private void loadBook() {

        try {
            // 打开书籍
            EpubReader reader = new EpubReader();
            InputStream is = new FileInputStream(mFilePath);
            mBook = reader.readEpub(is);

            mTocReferences = (ArrayList<TOCReference>) mBook.getTableOfContents().getTocReferences();
            mSpineReferences = mBook.getSpine().getSpineReferences();

            setSpineReferenceTitle();

            // 解压epub至缓存目录
            FileUtils.unzipFile(mFilePath, Constant.PATH_EPUB + "/" + mFileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



}
