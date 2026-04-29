package com.zenith.admin.util;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

public class PinyinUtil {

    /**
     * Extract first letters of pinyin for each Chinese character, in uppercase.
     * Non-Chinese letters are kept as-is (uppercased).
     */
    public static String getFirstLetters(String str) {
        if (str == null || str.isEmpty()) {
            return "";
        }

        HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
        format.setCaseType(HanyuPinyinCaseType.UPPERCASE);
        format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);

        StringBuilder sb = new StringBuilder();
        for (char c : str.toCharArray()) {
            if (isChinese(c)) {
                try {
                    String[] pinyinArray = PinyinHelper.toHanyuPinyinStringArray(c, format);
                    if (pinyinArray != null && pinyinArray.length > 0 && !pinyinArray[0].isEmpty()) {
                        sb.append(pinyinArray[0].charAt(0));
                    }
                } catch (BadHanyuPinyinOutputFormatCombination e) {
                    // skip characters that cannot be converted
                }
            } else if (Character.isLetter(c)) {
                sb.append(Character.toUpperCase(c));
            }
        }
        return sb.toString();
    }

    private static boolean isChinese(char c) {
        return Character.toString(c).matches("[\\u4E00-\\u9FA5]+");
    }
}
