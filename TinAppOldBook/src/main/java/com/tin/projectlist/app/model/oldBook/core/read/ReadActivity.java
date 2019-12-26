package com.tin.projectlist.app.model.oldBook.core.read;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.hjq.bar.TitleBar;
import com.tin.projectlist.app.model.oldBook.R;
import com.tin.projectlist.app.model.oldBook.core.read.EPubReaderAdapter;
import com.tin.projectlist.app.model.oldBook.core.read.ReadContract;
import com.tin.projectlist.app.model.oldBook.core.read.ReadPresenter;
import com.tin.projectlist.app.model.oldBook.core.read.epub.EpubCatalogAdapter;
import com.tin.projectlist.app.model.oldBook.mvp.MvpActivity;
import com.tin.projectlist.app.model.oldBook.readingTool.BookFileUtils;
import com.tin.projectlist.app.model.oldBook.readingTool.BookMixAToc;
import com.tin.projectlist.app.model.oldBook.readingTool.epub.view.DirectionalViewpager;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.List;

import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.domain.SpineReference;
import nl.siegmann.epublib.domain.TOCReference;


@ContentView(R.layout.activity_read)
public class ReadActivity extends MvpActivity<ReadPresenter> implements ReadContract.View {

    @ViewInject(R.id.tabBar)
    TitleBar mToolbar;

    @ViewInject(R.id.epubViewPager)
    private DirectionalViewpager readPageView;

    @ViewInject(R.id.rcv_catalog)
    private RecyclerView rcvCatalog;

    @ViewInject(R.id.book_index)
    private ImageView bookIndex;

    private EPubReaderAdapter mAdapter;

    private EpubCatalogAdapter epubCatalogAdapter;

    private Book mBook;
    private ArrayList<TOCReference> mTocReferences;
    private List<SpineReference> mSpineReferences;
    public boolean mIsSmilParsed = false;
    private String mFileName = "Redis入门指南（第2版）";
    private String mFilePath = "/sdcard/books/Redis入门指南（第2版）.epub";

    private List<BookMixAToc.mixToc.Chapters> mChapterList = new ArrayList<>();

    @Override
    protected ReadPresenter createPresenter() {
        return new ReadPresenter(this);
    }

    @Override
    protected View getTitleId() {
        return mToolbar;
    }


    @Event({R.id.book_index})
    private void onBtnClick(View view) {
        switch (view.getId()) {
            case R.id.book_index:
                rcvCatalog.setVisibility(rcvCatalog.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
                break;
        }
    }

    @Override
    protected void initData() {
        epubCatalogAdapter = new EpubCatalogAdapter(this);
        rcvCatalog.setAdapter(epubCatalogAdapter);
        getPresenter().loadBook(mFilePath);
        mToolbar.setTitle(mFileName);
    }

    @Override
    public void loadBookResult(Book book) {
        if (book == null) {

        } else {
            mBook = book;
            mTocReferences = (ArrayList<TOCReference>) mBook.getTableOfContents().getTocReferences();
            mSpineReferences = mBook.getSpine().getSpineReferences();
            readPageView.setOnPageChangeListener(new DirectionalViewpager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                }

                @Override
                public void onPageSelected(int position) {
                }

                @Override
                public void onPageScrollStateChanged(int state) {
                }
            });

            if (mBook != null && mSpineReferences != null && mTocReferences != null) {
                mAdapter = new EPubReaderAdapter(getSupportFragmentManager(),
                        mSpineReferences, mBook, mFileName, mIsSmilParsed);
                readPageView.setAdapter(mAdapter);
            }

            int srSize = mSpineReferences.size();
            int trSize = mTocReferences.size();
            for (int j = 0; j < srSize; j++) {
                String href = mSpineReferences.get(j).getResource().getHref();
                for (int i = 0; i < trSize; i++) {
                    if (mTocReferences.get(i).getResource().getHref().equalsIgnoreCase(href)) {
                        mSpineReferences.get(j).getResource().setTitle(mTocReferences.get(i).getTitle());
                        break;
                    } else {
                        mSpineReferences.get(j).getResource().setTitle("");
                    }
                }
            }

            for (int i = 0; i < trSize; i++) {
                Resource resource = mTocReferences.get(i).getResource();
                if (resource != null) {
                    mChapterList.add(new BookMixAToc.mixToc.Chapters(resource.getTitle(), resource.getHref()));
                }
            }
            epubCatalogAdapter.setData(mChapterList);

        }
    }


    public String getPageHref(int position) {
        String pageHref = mTocReferences.get(position).getResource().getHref();
        String opfpath = BookFileUtils.getPathOPF(BookFileUtils.getEpubFolderPath(mFileName));
        if (BookFileUtils.checkOPFInRootDirectory(BookFileUtils.getEpubFolderPath(mFileName))) {
            pageHref = BookFileUtils.getEpubFolderPath(mFileName) + "/" + pageHref;
        } else {
            pageHref = BookFileUtils.getEpubFolderPath(mFileName) + "/" + opfpath + "/" + pageHref;
        }
        return pageHref;
    }

}
