package com.core.text.model;

import com.core.text.model.style.GBTextStyleEntry;

/**
 * html5批注
 *
 * @author yangn
 *
 */
public class GBNoteEntry extends GBTextStyleEntry{

    protected static final char VALUE = 1;

    public String Value;

    public GBNoteEntry(String value) {
        Value = value;
    }

    public GBNoteEntry() {
    }

    @Override
    public char[] toChars() {
        StringBuilder sb = new StringBuilder();
        if (null != Value) {
            sb.append(VALUE);
            sb.append((char) Value.length());
            sb.append(Value);
        }

        return sb.toString().toCharArray();
    }

    public String getValue() {
        return Value;
    }

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
                case VALUE :
                    Value = tempVal;
                    break;
            }
            offset += kindLen;
        }
    }
}
