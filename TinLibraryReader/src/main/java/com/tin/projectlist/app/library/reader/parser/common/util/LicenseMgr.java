package com.tin.projectlist.app.library.reader.parser.common.util;

import com.geeboo.entity.SecretKey;

/**
 * 阅读器许可管理
 *
 * @author yangn
 *
 */
public class LicenseMgr {

    /**
     * 获取简帛加解密密匙
     *
     * @return
     */
    public static SecretKey getGeebooSecretKey() {
        return mKey;
    }

    private static SecretKey mKey = null /*
                                          * new SecretKey("1", "google",
                                          * "Nexus One",
                                          * "35495703424504673e52c02da1abb86")
                                          */;

    /**
     * 设置图书许可密钥
     */
    public static void settingLicenseKey(SecretKey key) {
        mKey = key;
    }

    /**
     * 是否是免费许可（即没有license key 的图书 一般为用户本地图书）
     *
     * @author yangn
     * @return
     */
    public static boolean isFreeLicense() {
        return null == mKey;
    }
}
