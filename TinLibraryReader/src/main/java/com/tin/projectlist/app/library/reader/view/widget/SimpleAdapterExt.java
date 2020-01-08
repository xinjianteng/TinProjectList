/*
 * Copyright (C) 2006 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.tin.projectlist.app.library.reader.view.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Checkable;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * An easy adapter to map static data to views defined in an XML file. You can
 * specify the data backing the list as an ArrayList of Maps. Each entry in the
 * ArrayList corresponds to one row in the list. The Maps contain the data for
 * each row. You also specify an XML file that defines the views used to display
 * the row, and a mapping from keys in the Map to specific views.
 *
 * Binding data to views occurs in two phases. First, if a
 * {@link android.widget.SimpleAdapter.ViewBinder} is available,
 * {@link ViewBinder#setViewValue(android.view.View, Object, String)} is
 * invoked. If the returned value is true, binding has occurred. If the returned
 * value is false, the following views are then tried in order:
 * <ul>
 * <li>A view that implements Checkable (e.g. CheckBox). The expected bind value
 * is a boolean.
 * <li>TextView. The expected bind value is a string and
 * {@link #setViewText(TextView, String)} is invoked.
 * <li>ImageView. The expected bind value is a resource id or a string and
 * {@link #setViewImage(ImageView, int)} or
 * {@link #setViewImage(ImageView, String)} is invoked.
 * </ul>
 * If no appropriate binding can be found, an {@link IllegalStateException} is
 * thrown.
 */
public class SimpleAdapterExt extends BaseAdapter implements Filterable {
    private int[] mTo;
    private String[] mFrom;
    private ViewBinder mViewBinder;

    private List<? extends Map<String, ?>> mData;

    private int mResource;
    private int mDropDownResource;
    private LayoutInflater mInflater;

    private SimpleFilter mFilter;
    private ArrayList<Map<String, ?>> mUnfilteredData;
    private Drawable mBackRes;
    private HashMap<Integer, OnClickListener> mListeners;

    /**
     * Constructor
     *
     * @param context The context where the View associated with this
     *        SimpleAdapter is running
     * @param data A List of Maps. Each entry in the List corresponds to one row
     *        in the list. The Maps contain the data for each row, and should
     *        include all the entries specified in "from"
     * @param resource Resource identifier of a view layout that defines the
     *        views for this list item. The layout file should include at least
     *        those named views defined in "to"
     * @param from A list of column names that will be added to the Map
     *        associated with each item.
     * @param to The views that should display column in the "from" parameter.
     *        These should all be TextViews. The first N views in this list are
     *        given the values of the first N columns in the from parameter.
     */
    public SimpleAdapterExt(Context context, List<? extends Map<String, ?>> data, int resource, String[] from, int[] to) {
        mData = data;
        mResource = mDropDownResource = resource;
        mFrom = from;
        mTo = to;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public SimpleAdapterExt(Context context, List<? extends Map<String, ?>> data, int resource, String[] from,
                            int[] to, HashMap<Integer, OnClickListener> listener) {
        mData = data;
        mResource = mDropDownResource = resource;
        mFrom = from;
        mTo = to;
        mListeners = listener;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public SimpleAdapterExt(Context context, List<? extends Map<String, ?>> data, int resource, String[] from,
                            int[] to, Drawable dra) {
        mData = data;
        mResource = mDropDownResource = resource;
        mFrom = from;
        mTo = to;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mBackRes = dra;
    }

    /**
     * @see android.widget.Adapter#getCount()
     */
    public int getCount() {
        return mData.size();
    }

    /**
     * @see android.widget.Adapter#getItem(int)
     */
    public Object getItem(int position) {
        return mData.get(position);
    }

    /**
     * @see android.widget.Adapter#getItemId(int)
     */
    public long getItemId(int position) {
        return position;
    }

    /**
     * @see android.widget.Adapter#getView(int, View, ViewGroup)
     */
    public View getView(int position, View convertView, ViewGroup parent) {
        return createViewFromResource(position, convertView, parent, mResource);
    }

    HashMap<Integer, View> mViewCache = new HashMap<Integer, View>();

    public void cacheClear() {
        if (null != mViewCache) {
            mViewCache.clear();
        }
    }

    private View createViewFromResource(int position, View convertView, ViewGroup parent, int resource) {
        View v;
        if (mViewCache.get(position) == null) {

            v = mInflater.inflate(resource, parent, false);
            mViewCache.put(position, v);
        } else {
            // v = convertView;
            v = mViewCache.get(position);
        }

        bindView(position, v);
        if (mBackRes != null) {
            v.setBackgroundDrawable(mBackRes);
        }
        return v;
    }

    /**
     * <p>
     * Sets the layout resource to create the drop down views.
     * </p>
     *
     * @param resource the layout resource defining the drop down views
     * @see #getDropDownView(int, android.view.View, android.view.ViewGroup)
     */
    public void setDropDownViewResource(int resource) {
        this.mDropDownResource = resource;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return createViewFromResource(position, convertView, parent, mDropDownResource);
    }

    private void bindView(int position, View view) {
        final Map dataSet = mData.get(position);
        if (dataSet == null) {
            return;
        }

        final ViewBinder binder = mViewBinder;
        final String[] from = mFrom;
        final int[] to = mTo;
        final int count = to.length;

        for (int i = 0; i < count; i++) {
            final View v = view.findViewById(to[i]);
            if (v != null) {
                final Object data = dataSet.get(from[i]);

                String text = data == null ? "" : data.toString();
                if (text == null) {
                    text = "";
                }
                if (text.equalsIgnoreCase("gone")) {
                    v.setVisibility(View.GONE);
                }

                if (null != mListeners) {
                    View.OnClickListener listener = mListeners.get(to[i]);
                    if (null != listener) {
                        // L.e("SimpleAdapter", "bind listener");
                        v.setOnClickListener(listener);
                    } else {
                        // L.e("SimpleAdapter", " listener is null");
                    }

                }

                boolean bound = false;
                if (binder != null) {
                    bound = binder.setViewValue(v, data, text);
                }

                if (!bound) {
                    /*
                     * if (v instanceof CheckBoxExt) { if (data instanceof
                     * Boolean) { ((CheckBoxExt) v).setChecked((Boolean) data);
                     * } else if (data instanceof String) { JSONObject ckeObj;
                     * try { ckeObj = new JSONObject(data.toString());
                     * ((CheckBoxExt) v).setName(ckeObj .getString("name"));
                     * ((CheckBoxExt) v).setValue(ckeObj .getString("val"));
                     * ((CheckBoxExt) v).setChecked(ckeObj
                     * .getBoolean("checked")); } catch (JSONException e) {
                     * Log.e("SimpleAdapterExt", "JSON Exception");
                     * e.printStackTrace(); } } } else
                     */if (v instanceof Checkable) {
                        if (data instanceof Boolean) {
                            ((Checkable) v).setChecked((Boolean) data);
                        } else if (v instanceof TextView) {
                            // Note: keep the instanceof TextView check at the
                            // bottom of these
                            // ifs since a lot of views are TextViews (e.g.
                            // CheckBoxes).
                            /*
                             * else if(text.equalsIgnoreCase("visible")){
                             * v.setVisibility(View.VISIBLE) }
                             */

                            setViewText((TextView) v, text);
                        } else {
                            throw new IllegalStateException(v.getClass().getName()
                                    + " should be bound to a Boolean, not a "
                                    + (data == null ? "<unknown type>" : data.getClass()));
                        }
                    } else if (v instanceof TextView) {
                        // Note: keep the instanceof TextView check at the
                        // bottom of these
                        // ifs since a lot of views are TextViews (e.g.
                        // CheckBoxes).
                        if (data instanceof CharSequence) {
                            ((TextView) v).setText((CharSequence) data);
                        } else {
                            setViewText((TextView) v, text);
                        }
                    } else if (v instanceof ImageView) {
                        if (data instanceof Integer) {
                            setViewImage((ImageView) v, (Integer) data);
                        } else if (data instanceof Bitmap) {
                            setViewImage((ImageView) v, (Bitmap) data);
                        } else {
                            setViewImage((ImageView) v, text);
                        }
                    } else if (v instanceof ViewGroup) {
                        setViewBackgroud((ViewGroup) v, text);
                    } else {
                        throw new IllegalStateException(v.getClass().getName() + " is not a "
                                + " view that can be bounds by this SimpleAdapter");
                    }
                }
            }
        }
    }

    /**
     * Returns the {@link ViewBinder} used to bind data to views.
     *
     * @return a ViewBinder or null if the binder does not exist
     *
     * @see # setViewBinder(android.widget.SimpleAdapter.ViewBinder)
     */
    public ViewBinder getViewBinder() {
        return mViewBinder;
    }

    /**
     * Sets the binder used to bind data to views.
     *
     * @param viewBinder the binder used to bind data to views, can be null to
     *        remove the existing binder
     *
     * @see #getViewBinder()
     */
    public void setViewBinder(ViewBinder viewBinder) {
        mViewBinder = viewBinder;
    }

    /**
     * Called by bindView() to set the image for an ImageView but only if there
     * is no existing ViewBinder or if the existing ViewBinder cannot handle
     * binding to an ImageView.
     *
     * This method is called instead of {@link #setViewImage(ImageView, String)}
     * if the supplied data is an int or Integer.
     *
     * @param v ImageView to receive an image
     * @param value the value retrieved from the data set
     *
     * @see #setViewImage(ImageView, String)
     */
    public void setViewImage(ImageView v, int value) {
        v.setImageResource(value);
    }

    /**
     *
     * @param v
     * @param bitmap
     * @author GuannanYan
     */
    public void setViewImage(ImageView v, Bitmap bitmap) {
        v.setImageBitmap(bitmap);
    }

    /**
     * Called by bindView() to set the image for an ImageView but only if there
     * is no existing ViewBinder or if the existing ViewBinder cannot handle
     * binding to an ImageView.
     *
     * By default, the value will be treated as an image resource. If the value
     * cannot be used as an image resource, the value is used as an image Uri.
     *
     * This method is called instead of {@link #setViewImage(ImageView, int)} if
     * the supplied data is not an int or Integer.
     *
     * @param v ImageView to receive an image
     * @param value the value retrieved from the data set
     *
     * @see #setViewImage(ImageView, int)
     */
    public void setViewImage(ImageView v, String value) {
        try {
            v.setImageResource(Integer.parseInt(value));
        } catch (NumberFormatException nfe) {
            v.setImageURI(Uri.parse(value));

        }
    }

    public void setViewBackgroud(ViewGroup vg, String value) {
        try {
            vg.setBackgroundResource(Integer.parseInt(value));
        } catch (NumberFormatException nfe) {
            // vg.setb.setImageURI(Uri.parse(value));
        }
    }

    /**
     * Called by bindView() to set the text for a TextView but only if there is
     * no existing ViewBinder or if the existing ViewBinder cannot handle
     * binding to an TextView.
     *
     * @param v TextView to receive text
     * @param text the text to be set for the TextView
     */
    public void setViewText(TextView v, String text) {
        if (text.equalsIgnoreCase("gone")) {
            v.setVisibility(View.GONE);
        } else {
            v.setText(text);
        }

    }

    public void setViewTextVisible(View view) {
        view.setVisibility(View.VISIBLE);

    }

    public Filter getFilter() {
        if (mFilter == null) {
            mFilter = new SimpleFilter();
        }
        return mFilter;
    }

    /**
     * This class can be used by external clients of SimpleAdapter to bind
     * values to views.
     *
     * You should use this class to bind values to views that are not directly
     * supported by SimpleAdapter or to change the way binding occurs for views
     * supported by SimpleAdapter.
     *
     * @see SimpleAdapterExt#setViewImage(ImageView, int)
     * @see SimpleAdapterExt#setViewImage(ImageView, String)
     * @see SimpleAdapterExt#setViewText(TextView, String)
     */
    public static interface ViewBinder {
        /**
         * Binds the specified data to the specified view.
         *
         * When binding is handled by this ViewBinder, this method must return
         * true. If this method returns false, SimpleAdapter will attempts to
         * handle the binding on its own.
         *
         * @param view the view to bind the data to
         * @param data the data to bind to the view
         * @param textRepresentation a safe String representation of the
         *        supplied data: it is either the result of data.toString() or
         *        an empty String but it is never null
         *
         * @return true if the data was bound to the view, false otherwise
         */
        boolean setViewValue(View view, Object data, String textRepresentation);
    }

    @Override
    public void notifyDataSetChanged() {
        this.cacheClear();
        super.notifyDataSetChanged();
    }

    /**
     * <p>
     * An array filters constrains the content of the array adapter with a
     * prefix. Each item that does not start with the supplied prefix is removed
     * from the list.
     * </p>
     */
    private class SimpleFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence prefix) {
            FilterResults results = new FilterResults();

            if (mUnfilteredData == null) {
                mUnfilteredData = new ArrayList<Map<String, ?>>(mData);
            }

            if (prefix == null || prefix.length() == 0) {
                ArrayList<Map<String, ?>> list = mUnfilteredData;
                results.values = list;
                results.count = list.size();
            } else {
                String prefixString = prefix.toString().toLowerCase();

                ArrayList<Map<String, ?>> unfilteredValues = mUnfilteredData;
                int count = unfilteredValues.size();

                ArrayList<Map<String, ?>> newValues = new ArrayList<Map<String, ?>>(count);

                for (int i = 0; i < count; i++) {
                    Map<String, ?> h = unfilteredValues.get(i);
                    if (h != null) {

                        int len = mTo.length;

                        for (int j = 0; j < len; j++) {
                            String str = (String) h.get(mFrom[j]);

                            String[] words = str.split(" ");
                            int wordCount = words.length;

                            for (int k = 0; k < wordCount; k++) {
                                String word = words[k];

                                if (word.toLowerCase().startsWith(prefixString)) {
                                    newValues.add(h);
                                    break;
                                }
                            }
                        }
                    }
                }

                results.values = newValues;
                results.count = newValues.size();
            }

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            // noinspection unchecked
            mData = (List<Map<String, ?>>) results.values;
            if (results.count > 0) {
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }
        }
    }
}
