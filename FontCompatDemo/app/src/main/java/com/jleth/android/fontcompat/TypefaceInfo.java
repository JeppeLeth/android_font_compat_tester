package com.jleth.android.fontcompat;

import android.graphics.Typeface;

/**
 * Info holder for a font accepted in {@link android.graphics.Typeface#create(String, int)}
 */
public class TypefaceInfo {
    public enum Style {
        NORMAL(Typeface.NORMAL),
        ITALIC(Typeface.ITALIC),
        BOLD(Typeface.BOLD),
        BOLD_ITALIC(Typeface.BOLD_ITALIC);

        public final int value;

        Style(int value) {
            this.value = value;
        }
    }

    public final Style style;
    public final String familyName;
    public final String fontFileName;

    public TypefaceInfo(String familyName, Style style, String fontFileName) {
        this.style = style;
        this.familyName = familyName;
        this.fontFileName = fontFileName != null ? fontFileName.substring(fontFileName.lastIndexOf("/") + 1) : null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TypefaceInfo that = (TypefaceInfo) o;

        if (style != that.style) return false;
        if (!familyName.equals(that.familyName)) return false;
        return fontFileName.equals(that.fontFileName);

    }

    @Override
    public int hashCode() {
        int result = style.ordinal();
        result = 31 * result + familyName.hashCode();
        result = 31 * result + fontFileName.hashCode();
        return result;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("TypefaceInfo{");
        sb.append("style=").append(style);
        sb.append(", familyName='").append(familyName).append('\'');
        sb.append(", fontFileName='").append(fontFileName).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
