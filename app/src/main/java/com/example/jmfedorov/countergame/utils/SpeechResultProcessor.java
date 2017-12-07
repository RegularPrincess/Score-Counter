package com.example.jmfedorov.countergame.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by jmfedorov on 07.12.17.
 */

public class SpeechResultProcessor {
    public static final int NIL = 0;
    public static final int RED = 2;
    public static final int BLUE = 1;
    static final String RED_NAME = "красн";
    static final String BLUE_NAME = "син";

    static final String MINUS = "минус";
    static final String PLUS = "плюс";

    public static int getWho(String res) {
        res = res.toLowerCase();
        String[] res_arr = res.split(" ");
        for (String word : res_arr) {
            if (word.startsWith(RED_NAME)) {
                return RED;
            }
            if (word.startsWith(BLUE_NAME)) {
                return BLUE;
            }
        }
        return NIL;
    }

    public static int getHowmany(String res) {
        res = res.toLowerCase();
        String[] res_arr = res.split(" ");
        String recogniseRes = "";
        int index = 0;
        boolean foundSign = false;
        for (int i = 0; i < res_arr.length; i++) {
            String word = res_arr[i];
            if (word.startsWith(MINUS) || word.startsWith("-")) {
                recogniseRes += "-";
                index = i;
                foundSign = true;
                break;
            }
            if (word.startsWith(PLUS) || word.startsWith("+")) {
                recogniseRes += "+";
                index = i;
                foundSign = true;
                break;
            }
        }
        int intRes = 0;
        if (foundSign) {
            recogniseRes += res_arr[index + 1];
            intRes = Integer.decode(recogniseRes);
            return intRes;
        } else {
            Pattern pat = Pattern.compile("[-]?[0-9]+(.[0-9]+)?");
            Matcher matcher = pat.matcher(res);
            if (matcher.find(0)) {
                String value = res.substring(matcher.start(), matcher.end());
                intRes = Integer.parseInt(value);
                return intRes;
            }
            return intRes;
        }
    }
}
