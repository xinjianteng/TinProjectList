package com.tin.projectlist.app.library.reader.view;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.geeboo.R;
import com.geeboo.utils.GeeBookLoader;

/**
 * Created by Vae
 * on 16-10-28.
 */
public class SharetoStyleReaderActivity extends BaseReaderActivity implements View.OnClickListener{

    private LinearLayout scrollView_1;
    private LinearLayout scrollView_2;
    private LinearLayout scrollView_3;
    private ImageView igm_1;
    private ImageView igm_3;
    private ImageView igm_2;
    private String strshare = null;
    private String text = null;
    private String bookname = null;
    private int shareFlag = 1;
    private Bitmap b = null;
    private LinearLayout share_ll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gb_layout_share_qq_wb_wx);

        strshare = this.getIntent().getStringExtra("sharestyle");
        text = this.getIntent().getStringExtra("text");
        bookname = this.getIntent().getStringExtra("bookname");

        share_ll = (LinearLayout) findViewById(R.id.share_ll);
        scrollView_1 = (LinearLayout)findViewById(R.id.gb_layout_style_1);
        scrollView_2 = (LinearLayout)findViewById(R.id.gb_layout_style_2);
        scrollView_3 = (LinearLayout)findViewById(R.id.gb_layout_style_3);
        //三种样式
        igm_1 = (ImageView)findViewById(R.id.gb_img_share_style_1);
        igm_1.setOnClickListener(this);
        igm_2 = (ImageView)findViewById(R.id.gb_img_share_style_2);
        igm_2.setOnClickListener(this);
        igm_3 = (ImageView)findViewById(R.id.gb_img_share_style_3);
        igm_3.setOnClickListener(this);
        igm_1.setBackgroundResource(R.drawable.gb_bg_share_style_1_pre);
        igm_2.setBackgroundResource(R.drawable.gb_bg_share_style_2_def);
        igm_3.setBackgroundResource(R.drawable.gb_bg_share_style_3_def);
        //分享按钮
        findViewById(R.id.gb_share_qq_wb_wx).setOnClickListener(this);
        findViewById(R.id.gb_share_canel).setOnClickListener(this);
        //书名
        TextView booknameTv = (TextView) findViewById(R.id.gb_share_book_name);
        TextView booknameTv_1 = (TextView) findViewById(R.id.gb_share_book_name_1);
        TextView booknameTv_2 = (TextView) findViewById(R.id.gb_share_book_name_2);
        if (bookname != null){
            booknameTv.setText("《" + bookname+ "》");
            booknameTv_1.setText("《" + bookname+ "》");
            booknameTv_2.setText("《" + bookname+ "》");
        }
        //作者
        TextView bookauthor = (TextView) findViewById(R.id.gb_share_book_author);
        TextView bookauthor_1 = (TextView) findViewById(R.id.gb_share_book_author_1);
        TextView bookauthor_2 = (TextView) findViewById(R.id.gb_share_book_author_2);
        if(GeeBookLoader.getBookMgr() != null){
            bookauthor.setText(GeeBookLoader.getBookMgr().bookauthor());
            bookauthor_1.setText(GeeBookLoader.getBookMgr().bookauthor());
            bookauthor_2.setText(GeeBookLoader.getBookMgr().bookauthor());
        }
        //内容
        TextView bookContext = (TextView) findViewById(R.id.gb_share_context);
        TextView bookContext_1 = (TextView) findViewById(R.id.gb_share_context_1);
        TextView bookContext_2 = (TextView) findViewById(R.id.gb_share_context_2);
        bookContext.setText(text);
        bookContext_1.setText(text);
        bookContext_2.setText(text);

        scrollView_1.setVisibility(View.VISIBLE);
        scrollView_2.setVisibility(View.GONE);
        scrollView_3.setVisibility(View.GONE);
        igm_1.setBackgroundResource(R.drawable.gb_bg_share_style_1_pre);
        igm_2.setBackgroundResource(R.drawable.gb_bg_share_style_2_def);
        igm_3.setBackgroundResource(R.drawable.gb_bg_share_style_3_def);


    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if(i == R.id.gb_share_canel){
            finish();
        }else if (i == R.id.gb_img_share_style_1) {
            shareFlag = 1;
            scrollView_1.setVisibility(View.VISIBLE);
            scrollView_2.setVisibility(View.GONE);
            scrollView_3.setVisibility(View.GONE);
            igm_1.setBackgroundResource(R.drawable.gb_bg_share_style_1_pre);
            igm_2.setBackgroundResource(R.drawable.gb_bg_share_style_2_def);
            igm_3.setBackgroundResource(R.drawable.gb_bg_share_style_3_def);

        }else if(i == R.id.gb_img_share_style_2){
            shareFlag = 2;
            scrollView_1.setVisibility(View.GONE);
            scrollView_2.setVisibility(View.VISIBLE);
            scrollView_3.setVisibility(View.GONE);
            igm_1.setBackgroundResource(R.drawable.gb_bg_share_style_1_def);
            igm_2.setBackgroundResource(R.drawable.gb_bg_share_style_2_pre);
            igm_3.setBackgroundResource(R.drawable.gb_bg_share_style_3_def);

        }else if(i == R.id.gb_img_share_style_3){
            shareFlag = 3;
            scrollView_1.setVisibility(View.GONE);
            scrollView_2.setVisibility(View.GONE);
            scrollView_3.setVisibility(View.VISIBLE);
            igm_1.setBackgroundResource(R.drawable.gb_bg_share_style_1_def);
            igm_2.setBackgroundResource(R.drawable.gb_bg_share_style_2_def);
            igm_3.setBackgroundResource(R.drawable.gb_bg_share_style_3_pre);
        }else if (i == R.id.gb_share_qq_wb_wx){
            interceptImg();
        }
    }

    private void interceptImg() {

        if(strshare != null && "wx".equals(strshare)){//微信分享
            if(shareFlag ==1 && GeeBookLoader.getBookMgr() != null){
                GeeBookLoader.getBookMgr().sharetoWXBook(0,this,share_ll);
            }else if(shareFlag ==2 && GeeBookLoader.getBookMgr() != null){
                GeeBookLoader.getBookMgr().sharetoWXBook(0,this,share_ll);
            }else if(shareFlag ==3 && GeeBookLoader.getBookMgr() != null){
                GeeBookLoader.getBookMgr().sharetoWXBook(0,this,share_ll);
            }
        }else if(strshare != null && "qq".equals(strshare)){//QQ好友
            if(shareFlag ==1 && GeeBookLoader.getBookMgr() != null){
                GeeBookLoader.getBookMgr().sharetoQQBook(this,share_ll);
            }else if(shareFlag ==2 && GeeBookLoader.getBookMgr() != null){
                GeeBookLoader.getBookMgr().sharetoQQBook(this,share_ll);
            }else if(shareFlag ==3 && GeeBookLoader.getBookMgr() != null){
                GeeBookLoader.getBookMgr().sharetoQQBook(this,share_ll);
            }
        }else if(strshare != null && "wb".equals(strshare)){//微博
            if(shareFlag ==1 && GeeBookLoader.getBookMgr() != null){
                GeeBookLoader.getBookMgr().sharetoWbBook(this,share_ll);
            }else if(shareFlag ==2 && GeeBookLoader.getBookMgr() != null){
                GeeBookLoader.getBookMgr().sharetoWbBook(this,share_ll);
            }else if(shareFlag ==3 && GeeBookLoader.getBookMgr() != null){
                GeeBookLoader.getBookMgr().sharetoWbBook(this,share_ll);
            }
        }else if(strshare != null && "wxfriend".equals(strshare)){//朋友有圈(WX)
            if(shareFlag ==1 && GeeBookLoader.getBookMgr() != null){
                GeeBookLoader.getBookMgr().sharetoWXBook(1,this,share_ll);
            }else if(shareFlag ==2 && GeeBookLoader.getBookMgr() != null){
                GeeBookLoader.getBookMgr().sharetoWXBook(1,this,share_ll);
            }else if(shareFlag ==3 && GeeBookLoader.getBookMgr() != null){
                GeeBookLoader.getBookMgr().sharetoWXBook(1,this,share_ll);
            }

        }
    }
}
