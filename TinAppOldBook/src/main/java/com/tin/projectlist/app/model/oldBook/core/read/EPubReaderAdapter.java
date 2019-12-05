package com.tin.projectlist.app.model.oldBook.core.read;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;


import com.tin.projectlist.app.model.oldBook.core.read.epub.ReaderEpubFragment;

import java.util.List;

import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.SpineReference;

public class EPubReaderAdapter extends FragmentPagerAdapter {

    private List<SpineReference> mSpineReferences;
    private Book mBook;
    private String mEpubFileName;
    private boolean mIsSmilAvailable;

    public EPubReaderAdapter(FragmentManager fm, List<SpineReference> spineReferences,
                             Book book, String epubFilename, boolean isSmilAvilable) {
        super(fm);
        this.mSpineReferences = spineReferences;
        this.mBook = book;
        this.mEpubFileName = epubFilename;
        this.mIsSmilAvailable = isSmilAvilable;
    }

    @Override
    public Fragment getItem(int position) {
        return ReaderEpubFragment.newInstance(position, mBook, mEpubFileName, mIsSmilAvailable);
    }

    @Override
    public int getCount() {
        return mSpineReferences.size();
    }

}