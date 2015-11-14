package com.unuuu.opus.util;

/**
 * 文字列のUtilityクラス
 */
public final class StringUtil {
    private StringUtil() {

    }

    /**
     * 文字列がnullか空文字か
     * @param string 文字列
     * @return nullもしくは空文字だったらtrueを返す
     */
    public static boolean isNullOrEmpty(String string) {
        return string == null || string.length() < 1;
    }

    /**
     * 文字列がnullの場合に空文字に変換する
     * @param string 文字列
     * @return nullの場合は空文字を返す。そうじゃない場合はなにもせずに返す
     */
    public static String nullToEmpty(String string) {
        if (string == null) {
            return "";
        }
        return string;
    }

    /**
     * 文字列の中に1つでもnullがあるか空文字があるか
     * @param strings 複数の文字列
     * @return 1つでもnullもしくは空文字だったらtrueを返す
     * */
    public static boolean hasNullOrEmpty(String... strings) {
        if (strings == null) {
            return true;
        }

        for (String string : strings) {
            if (isNullOrEmpty(string)) {
                return true;
            }
        }

        return false;
    }
}
