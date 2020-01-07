package com.tin.projectlist.app.library.reader.parser.text.model;

import com.tin.projectlist.app.library.reader.parser.text.model.style.GBTextStyleEntry;
import com.tin.projectlist.app.library.reader.parser.file.image.GBImage;

import java.util.Map;

public class GBFileCtrEntry extends GBTextStyleEntry {

    public GBFileCtrEntry() {
    }

    public GBFileCtrEntry(Map<String, GBImage> imageMap) {
        myImageMap = imageMap;
    }
    protected static final char PATH = 1;
    protected static final char PATH_TWO = 2;

    protected Map<String, GBImage> myImageMap;

    @Override
    public void loadData(char[] data, int offset, int len) {
        String tempVal;
        while (offset < len) {

            char kindType = data[offset++];
            int kindLen = data[offset++];
            try {
                tempVal = new String(data, offset, kindLen);
            } catch (StringIndexOutOfBoundsException ex) {
                return;
            }
            switch (kindType) {
                case PATH :
                    Path = tempVal;
                    break;
                case PATH_TWO :
                    PathTwo = tempVal;
                    break;

            }
            offset += kindLen;

        }

    }
    @Override
    public char[] toChars() {
        StringBuilder sb = new StringBuilder();
        if (null != Path) {
            sb.append(PATH);
            sb.append((char) Path.length());
            sb.append(Path);
        }

        if (null != PathTwo) {
            sb.append(PATH_TWO);
            sb.append((char) PathTwo.length());
            sb.append(PathTwo);
        }
        return sb.toString().toCharArray();
    }

    public String Path;
    public String PathTwo;

    public GBFileCtrEntry(String path, String pathTwo) {
        this.Path = path;
        this.PathTwo = pathTwo;
    }
    /**
     *
     * 功能描述： <br>
     * 创建者： yangn<br>
     * 创建日期：2013-12-2<br>
     *
     * @param path 或者pathTwo
     */
    public GBImage getPathImage() {
        if (null != Path && null != myImageMap) {
            return myImageMap.get(Path);
        } else {
            return null;
        }

    }

    public GBImage getPathTwoImage() {
        if (null != PathTwo && null != myImageMap) {
            return myImageMap.get(PathTwo);
        } else {
            return null;
        }
    }

}
