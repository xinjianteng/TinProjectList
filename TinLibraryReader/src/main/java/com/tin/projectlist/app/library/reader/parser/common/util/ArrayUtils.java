package com.tin.projectlist.app.library.reader.parser.common.util;

/**
 * 描述：数组操作工具类 <br>
 * 根据传入数组，创建该数组的一个子集数组并返回<br>
 * 创建者： T_xin<br>
 * 创建日期：2020-01-02<br>
 */
public abstract class ArrayUtils {

    /**
     * 创建一个新数组，该数组的部分内容已被源数组填充<br>
     *
     * @param array     源数组
     * @param dataSize  拷贝源数组的长度
     * @param newLength 新数组的长度
     */
    public static boolean[] createCopy(boolean[] array, int dataSize, int newLength) {
        boolean[] newArray = new boolean[newLength];
        if (dataSize > 0) {
            System.arraycopy(array, 0, newArray, 0, dataSize);
        }
        return newArray;
    }

    /**
     * 创建一个新数组，该数组的部分内容已被源数组填充<br>
     *
     * @param array     源数组
     * @param dataSize  拷贝源数据的长度
     * @param newLength 新数组的长度
     */
    public static byte[] createCopy(byte[] array, int dataSize, int newLength) {
        byte[] newArray = new byte[newLength];
        if (dataSize > 0) {
            System.arraycopy(array, 0, newArray, 0, dataSize);
        }
        return newArray;
    }

    /**
     * 创建一个新数组，该数组的部分内容已被源数组填充<br>
     *
     * @param array     源数组
     * @param dataSize  拷贝源数组的长度
     * @param newLength 新数组的长度
     */
    public static char[] createCopy(char[] array, int dataSize, int newLength) {
        char[] newArray = new char[newLength];
        if (dataSize > 0) {
            System.arraycopy(array, 0, newArray, 0, dataSize);
        }
        return newArray;
    }

    /**
     * 创建一个新数组，该数组的部分内容已被源数组填充<br>
     *
     * @param array     源数组
     * @param dataSize  拷贝源数组的长度
     * @param newLength 新数组的长度
     */
    public static int[] createCopy(int[] array, int dataSize, int newLength) {
        int[] newArray = new int[newLength];
        if (dataSize > 0) {
            System.arraycopy(array, 0, newArray, 0, dataSize);
        }
        return newArray;
    }


    // 改为native method
    public static int[][] createCopy(int[][] array, int dataSize, int newLength) {
        int[][] newArray = new int[newLength][0];
        if (dataSize > 0) {
            // System.arraycopy(array, 0, newArray, 0, dataSize);
            for (int i = 0; i < dataSize; i++) {
                newArray[i] = array[i];
            }
        }
        return newArray;
    }

    public static int[][][] createCopy(int[][][] array, int dataSize, int newLength) {
        int[][][] newArray = new int[newLength][0][0];
        if (dataSize > 0) {
            // System.arraycopy(array, 0, newArray, 0, dataSize);
            for (int i = 0; i < dataSize; i++) {
                newArray[i] = array[i];
            }
        }
        return newArray;
    }

    public static byte[][] createCopy(byte[][] array, int dataSize, int newLength) {
        byte[][] newArray = new byte[newLength][0];
        if (dataSize > 0) {
            // System.arraycopy(array, 0, newArray, 0, dataSize);
            for (int i = 0; i < dataSize; i++) {
                newArray[i] = array[i];
            }
        }
        return newArray;
    }

    /**
     * 创建一个新数组，该数组的部分内容已被源数组填充<br>
     *
     * @param array     源数组
     * @param dataSize  拷贝源数据的长度
     * @param newLength 新数组的长度
     */
    public static String[] createCopy(String[] array, int dataSize, int newLength) {
        String[] newArray = new String[newLength];
        if (dataSize > 0) {
            System.arraycopy(array, 0, newArray, 0, dataSize);
        }
        return newArray;
    }

    final static String SEPARATE = "@_@";

    /**
     * 功能描述：将空格分割的字符串转换为数组 返回的数组元素不包含空格元素 创建者： yangn<br>
     * 创建日期：2013-6-8<br>
     *
     * @param
     */
    public static String[] splitDeleteSpace(String str) {

        StringBuilder sb = new StringBuilder();
        String[] strArr = str.split(" ".intern());
        for (String s : strArr) {
            if (s.trim().equals("".intern())) {
                sb.append(SEPARATE);
            } else {
                sb.append(s);
            }
        }
        if (sb.toString().contains(SEPARATE)) {
            return sb.toString().split(SEPARATE);
        } else {
            return strArr;
        }

    }
}
