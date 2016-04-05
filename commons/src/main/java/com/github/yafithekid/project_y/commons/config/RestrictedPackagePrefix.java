package com.github.yafithekid.project_y.commons.config;

import java.util.ArrayList;
import java.util.List;

public class RestrictedPackagePrefix {
    private static List<String> prefixes;

    public static boolean isRestricted(String className){
        if (prefixes == null){
            prefixes = new ArrayList<String>();
            prefixes.add("javax.");
            prefixes.add("java.");
            prefixes.add("sun.");
            prefixes.add("org.ietf.");
            prefixes.add("org.w3c.");
            prefixes.add("org.omg.");
        }
        boolean ret = false;
        for(String prefix:prefixes){
            if (className.startsWith(prefix)){
                ret = true;
            }
        }
        return ret;
    }
}
