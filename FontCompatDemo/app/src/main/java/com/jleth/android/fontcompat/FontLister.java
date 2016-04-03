package com.jleth.android.fontcompat;

import android.util.Log;

import com.jleth.android.fontcompat.FontListParser.Alias;
import com.jleth.android.fontcompat.FontListParser.Config;
import com.jleth.android.fontcompat.FontListParser.Family;
import com.jleth.android.fontcompat.FontListParser.Font;
import com.jleth.android.fontcompat.TypefaceInfo.Style;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.jleth.android.fontcompat.FontListParser.parse;

/**
 * Helper class for listing embedded native fonts for current device / OS version
 *
 * @author JeppeLeth on 4/3/2016.
 */
public class FontLister {

    private static final String TAG = "FontLister";

    private static final int WEIGHT_NORMAL = 400;
    private static final int WEIGHT_BOLD = WEIGHT_NORMAL + 300; // 700

    private static final String FONTS_FILE_PATH = "/system/fonts";

    private static final File FONTS_XML = new File("/system/etc/fonts.xml");

    private static final File SYSTEM_FONTS_XML = new File("/system/etc/system_fonts.xml");

    public static List<TypefaceInfo> safelyGetTypefaceInfos() {
        try {
            return getSystemTypefaces();
        } catch (Exception e) {
            Log.e(TAG, "Could not parse system fonts in " + FONTS_FILE_PATH + ", using fallback instead", e);
            String[][] defaultSystemFonts = {
                    {"cursive", "400", null, "DancingScript-Regular.ttf"},
                    {"cursive", "700", null, "DancingScript-Bold.ttf"},
                    {"monospace", "400", null, "DroidSansMono.ttf"},
                    {"sans-serif", "400", null, "Roboto-Regular.ttf"},
                    {"sans-serif", "400", "", "Roboto-Italic.ttf"},
                    {"sans-serif-light", "300", null, "Roboto-Light.ttf"},
                    {"sans-serif-light", "300", "", "Roboto-LightItalic.ttf"},
                    {"sans-serif-medium", "500", null, "Roboto-Medium.ttf"},
                    {"sans-serif-medium", "500", "", "Roboto-MediumItalic.ttf"},
                    {"sans-serif-black", "900", null, "Roboto-Black.ttf"},
                    {"sans-serif-black", "900", "", "Roboto-BlackItalic.ttf"},
                    {"sans-serif-condensed", "400", null, "RobotoCondensed-Regular.ttf"},
                    {"sans-serif-thin", "100", null, "Roboto-Thin.ttf"},
                    {"sans-serif-thin", "100", "", "Roboto-ThinItalic.ttf"},
                    {"sans-serif", "700", null, "Roboto-Bold.ttf"},
                    {"sans-serif", "700", "", "Roboto-BoldItalic.ttf"},
                    {"serif", "400", null, "NotoSerif-Regular.ttf"}};
            List<TypefaceInfo> fonts = new ArrayList<>();
            for (String[] names : defaultSystemFonts) {
                File file = new File(FONTS_FILE_PATH, names[3]);
                if (file.exists()) {
                    fonts.add(new TypefaceInfo(names[0], styleFromFontMetrics(Integer.valueOf(names[1]), names[2] != null), file.getAbsolutePath()));
                }
            }
            return fonts;
        }
    }

    public static List<TypefaceInfo> getSystemTypefaces() throws Exception {
        String fontsXml;
        if (FONTS_XML.exists()) {
            fontsXml = FONTS_XML.getAbsolutePath();
        } else if (SYSTEM_FONTS_XML.exists()) {
            fontsXml = SYSTEM_FONTS_XML.getAbsolutePath();
        } else {
            throw new RuntimeException("fonts.xml does not exist on this system");
        }
        Config parser = parse(new FileInputStream(fontsXml));
        List<TypefaceInfo> fonts = new ArrayList<>();
        List<TypefaceInfo> aliasOnly = new ArrayList<>();

        for (Family family : parser.families) {
            if (family.name != null) {
                Font font;
                for (Font f : family.fonts) {
                    font = f;
                    if (f.weight == WEIGHT_NORMAL || f.weight == WEIGHT_BOLD) {
                        TypefaceInfo fontInfo = new TypefaceInfo(family.name, styleFromFont(font), font.fontName);
                        fonts.add(fontInfo);
                    } else {
                        TypefaceInfo fontInfo = new TypefaceInfo(family.name, styleFromFont(font), font.fontName);
                        aliasOnly.add(fontInfo);
                    }
                }
            }
        }

        for (Alias alias : parser.aliases) {
            if (alias.name == null || alias.toName == null || alias.weight == 0) {
                continue;
            }
            for (Family family : parser.families) {
                if (family.name == null || !family.name.equals(alias.toName)) {
                    continue;
                }
                for (Font font : family.fonts) {
                    if (font.weight == alias.weight) {
                        fonts.add(new TypefaceInfo(alias.name, styleFromFont(font), font.fontName));
                    }
                }
            }
        }

        if (fonts.isEmpty()) {
            throw new Exception("No system fonts found.");
        }

        Collections.sort(fonts, new Comparator<TypefaceInfo>() {

            @Override
            public int compare(TypefaceInfo font1, TypefaceInfo font2) {
                int comp = font1.familyName.compareToIgnoreCase(font2.familyName);
                return comp == 0 ? font1.style.ordinal() - font2.style.ordinal() : comp;
            }

        });

        return fonts;
    }

    private static Style styleFromFont(Font font) {
        return styleFromFontMetrics(font.weight, font.isItalic);
    }

    private static Style styleFromFontMetrics(int weight, boolean isItalic) {
        Style style;
        if (weight == WEIGHT_BOLD) {
            style = isItalic ? Style.BOLD_ITALIC : Style.BOLD;
        } else {
            style = isItalic ? Style.ITALIC : Style.NORMAL;
        }
        return style;
    }

}
