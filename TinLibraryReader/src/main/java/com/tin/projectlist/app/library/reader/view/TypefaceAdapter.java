package com.tin.projectlist.app.library.reader.view;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.core.common.util.IFunction;
import com.core.file.GBPaths;
import com.geeboo.R;
import com.geeboo.read.model.book.TypeFace;
import com.geeboo.read.view.widget.AndroidFontUtil;
import com.geeboo.utils.FileUtils;
import com.geeboo.utils.GeeBookLoader;
import com.geeboo.utils.UIUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 类名：TypefaceAdapter.java<br>
 * 描述： 字体列表适配器<br>
 * 创建者： 周波<br>
 * 创建日期：2014-8-14<br>
 * 版本： <br>
 * 修改者： <br>
 * 修改日期：<br>
 */
public class TypefaceAdapter extends BaseAdapter {
    private List<ViewHolder> mViewList = new ArrayList<ViewHolder>();

    private Activity mActivity;
    private List<TypeFace> mFamilies;

    public TypefaceAdapter(Activity activity) {
        this.mActivity = activity;
        mFamilies = new ArrayList<TypeFace>();
    }

    public void addLocalData(List<TypeFace> families){
        if (mFamilies.size() > 0) {
            this.mFamilies.clear();
        }
        if(families!=null && families.size()>0){
            this.mFamilies.addAll(families);
        }
        this.mFamilies.add(new TypeFace(null, "", false));
    }

    public void addNetData(List<TypeFace> families) {
        this.mFamilies.remove(this.mFamilies.size()-1);
        if(families!=null && families.size()>0){
            this.mFamilies.addAll(families);
        }
    }

    public void clearData(){
        this.mFamilies.clear();
    }

    @Override
    public int getCount() {
        return mFamilies.size();
    }

    @Override
    public Object getItem(int position) {
        return mFamilies.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final TypeFace data = mFamilies.get(position);
        final ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mActivity).inflate(R.layout.typeface_item, null);
            holder.rl_content = convertView.findViewById(R.id.rl_content);
            holder.ll_loading = convertView.findViewById(R.id.ll_loading);
            holder.ll_content = convertView.findViewById(R.id.ll_content);
            holder.txt_typeface_name = (TextView) convertView.findViewById(R.id.txt_typeface_name);
            holder.iv_icon_selected = (ImageView) convertView.findViewById(R.id.iv_icon_selected);
            holder.btn_download = convertView.findViewById(R.id.btn_download);
            holder.borrow_btn = (ProgressBar)convertView.findViewById(R.id.borrow_btn);
            holder.tv_borrow_title = (TextView) convertView.findViewById(R.id.tv_borrow_title);
            mViewList.add(holder);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if(data.fontName==null){
            holder.ll_loading.setVisibility(View.VISIBLE);
            holder.ll_content.setVisibility(View.GONE);
        } else {
            holder.ll_loading.setVisibility(View.GONE);
            holder.ll_content.setVisibility(View.VISIBLE);
            if(data.isExist){// 已下载字体
                String realFontName = null;
                if(data.fontName.equals("Droid Sans")){//默认字体
                    holder.txt_typeface_name.setText("默认字体");
                    realFontName = "serif";
                } else {
                    holder.txt_typeface_name.setText(data.fontName);
                    File fontFile = new File(GBPaths.getFontsPathOption().getValue(),data.downloadUrl);
                    realFontName = AndroidFontUtil.getRealFontName(fontFile);
                }
                if (realFontName.equals(((TypefaceReaderActivity)mActivity).currentFont)) {
                    holder.iv_icon_selected.setVisibility(View.VISIBLE);
                    holder.btn_download.setVisibility(View.GONE);
                } else {
                    holder.iv_icon_selected.setVisibility(View.GONE);
                    holder.btn_download.setVisibility(View.GONE);
                    holder.rl_content.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(((TypefaceReaderActivity)mActivity).fontUrl!=null){
                                UIUtil.showMessageText(mActivity, "当前有字体正在下载，请稍后...");
                                return;
                            }
                            ((TypefaceReaderActivity)mActivity).setFont(data.downloadUrl);
                        }
                    });
                }
            } else {//未下载的字体
                String fontFormat = data.downloadUrl.substring(data.downloadUrl.lastIndexOf("."));
                final File itemFontFile = new File(FileUtils.FONT + File.separator + data.fontName + fontFormat);
                holder.borrow_btn.setTag(data.downloadUrl);
                holder.tv_borrow_title.setTag(itemFontFile.getName());
                holder.tv_borrow_title.setText("下载");
                holder.borrow_btn.setProgress(0);
                holder.txt_typeface_name.setText(data.fontName);
                holder.iv_icon_selected.setVisibility(View.GONE);
                holder.btn_download.setVisibility(View.VISIBLE);
                holder.rl_content.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(((TypefaceReaderActivity)mActivity).fontUrl!=null){
                            UIUtil.showMessageText(mActivity, "当前有字体正在下载，请稍后...");
                            return;
                        }
                        GeeBookLoader.getBookMgr().downloadFile(data.downloadUrl,itemFontFile.getAbsolutePath(),false,"正在下载"+data.fontName+"字体", new IFunction<Integer>() {
                            @Override
                            public void callback(Integer nowProgress) {
                                ((TypefaceReaderActivity)mActivity).fontProgress = nowProgress;
                                ((TypefaceReaderActivity)mActivity).fontUrl = data.downloadUrl;
                                ((TypefaceReaderActivity)mActivity).progressHander.sendEmptyMessage(0);
                            }
                        });
                    }
                });
            }
        }
        return convertView;
    }

    //更新进度条进度，并防止ViewHolder重用导致下载紊乱
    public void updateBtnState(String url, int progress){
        if(url==null){
            return;
        }
        ViewHolder viewHolder = null;//
        for (ViewHolder vh : mViewList) {
            if (vh.borrow_btn.getTag() != null && vh.borrow_btn.getTag().toString().equals(url)) {
                viewHolder = vh;
                break;
            }
        }
        if(viewHolder != null){
            if(progress == 0){//开始
                viewHolder.borrow_btn.setClickable(false);
                viewHolder.borrow_btn.setProgress(0);
                viewHolder.tv_borrow_title.setText("开始下载");
            }else if(progress > 0 && progress < 100){//下载中
                viewHolder.borrow_btn.setProgress(progress);
                viewHolder.tv_borrow_title.setText("下载中...");
            }else if(progress == 100){//下载完成
                viewHolder.borrow_btn.setProgress(0);
                viewHolder.btn_download.setVisibility(View.GONE);
                ((TypefaceReaderActivity)mActivity).getData();
//                final String fontName = viewHolder.tv_borrow_title.getTag().toString();
//                viewHolder.rl_content.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                    	((TypefaceReaderActivity)mActivity).setFont(fontName);
//                    }
//            	});
            } else if(progress < 0){//失败
                viewHolder.borrow_btn.setClickable(true);
                viewHolder.borrow_btn.setProgress(0);
                viewHolder.tv_borrow_title.setText("下载");
            }
        }
    }

    class ViewHolder {
        View rl_content;
        View ll_loading;
        View ll_content;
        TextView txt_typeface_name;
        ImageView iv_icon_selected;
        View btn_download;
        ProgressBar borrow_btn;
        TextView tv_borrow_title;
    }
}
