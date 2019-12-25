package com.tin.projectlist.app.model.oldBook.core.book;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.hjq.bar.TitleBar;
import com.tin.projectlist.app.library.base.utils.IntentUtils;
import com.tin.projectlist.app.library.base.widget.MultiStateView;
import com.tin.projectlist.app.model.oldBook.R;
import com.tin.projectlist.app.model.oldBook.constant.KeyConstant;
import com.tin.projectlist.app.model.oldBook.core.read.ReadActivity;
import com.tin.projectlist.app.model.oldBook.entity.Book;
import com.tin.projectlist.app.model.oldBook.entity.BookComment;
import com.tin.projectlist.app.model.oldBook.mvp.MvpActivity;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.List;


@ContentView(R.layout.activity_book_detail)
public class BookDetailActivity extends MvpActivity<BookDetailPresenter> implements BookDetailContract.View{


    @ViewInject(R.id.tb_title)
    TitleBar titleBar;

    @ViewInject(R.id.iv_bookCover)
    private ImageView bookCover;

    @ViewInject(R.id.tv_bookName)
    private TextView bookName;

    @ViewInject(R.id.tv_bookAuthor)
    private TextView bookAuthor;

    @ViewInject(R.id.tv_bookSummary)
    private TextView bookSummary;

    @ViewInject(R.id.tv_bookIntroduction)
    private TextView bookIntroduction;

    @ViewInject(R.id.recycler)
    private RecyclerView rcvList;

    @ViewInject(R.id.multi_state_view)
    private MultiStateView multiStateView;

    @ViewInject(R.id.tv_read)
    private TextView tvRead;


    Book book;

    BookCommentAdapter bookCommentAdapter;

    @Override
    protected BookDetailPresenter createPresenter() {
        return new BookDetailPresenter();
    }

    @Override
    protected View getTitleId() {
        return titleBar;
    }


    @Override
    protected void initData() {
        book = (Book) IntentUtils.getParcelableExtra(getIntent(), KeyConstant.ENTITY);
        titleBar.setTitle(book.getBook_name());
        x.image().bind(bookCover,book.getBook_cover());
        bookName.setText(book.getBook_name());
        bookAuthor.setText(book.getBook_author());
        bookSummary.setText(book.getBook_summary());
        bookIntroduction.setText(book.getBook_introduction());

        bookCommentAdapter=new BookCommentAdapter(this);
        rcvList.setAdapter(bookCommentAdapter);
        getPresenter().getBookCommentForBookId("PshVCCCH");
        multiStateView.showContent();
    }

    @Override
    public void getBookCommentResult(boolean result, List<BookComment> bookCommentList) {
        if(result){
            bookCommentAdapter.setData(bookCommentList);
        }
    }


    @Event(R.id.tv_read)
    private void onClickRead(View view) {
        startActivity(ReadActivity.class);
    }

}
