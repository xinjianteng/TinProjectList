package com.tin.projectlist.app.library.base.utils;


import com.chad.library.adapter.base.BaseQuickAdapter;

import java.util.List;

public class AdapterUtil {



    public static void setAdapterData(boolean refresh, boolean success, List list, BaseQuickAdapter baseAdapter) {
        if (success) {
            if (refresh) {
                baseAdapter.setNewData(list);
            } else {
                List data = baseAdapter.getData();
                if (!ListUtil.isListNull(data)) {
                    for (Object o : list) {
                        if (!data.contains(o)) {
                            baseAdapter.addData(o);
                        }
                    }
                } else {
                    baseAdapter.addData(list);
                }
            }
        }
    }

}
