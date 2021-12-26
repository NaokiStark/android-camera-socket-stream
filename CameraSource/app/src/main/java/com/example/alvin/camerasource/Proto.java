package com.example.alvin.camerasource;

import org.json.JSONObject;
import org.json.JSONStringer;

import java.util.LinkedHashMap;

public class Proto {
    public static String envelop(String s){

        String o = "";

        LinkedHashMap<String, String> toEnvelop = new LinkedHashMap<>();

        toEnvelop.put("type", "string");
        toEnvelop.put("data", s);

        o = (new JSONObject(toEnvelop)).toString();

        return  o;
    }

    public static String envelop(String t, String s){
        String o = "";

        LinkedHashMap<String, String> toEnvelop = new LinkedHashMap<>();

        toEnvelop.put("type", "string");
        toEnvelop.put("data", s);

        o = (new JSONObject(toEnvelop)).toString();

        return  o;
    }


}
