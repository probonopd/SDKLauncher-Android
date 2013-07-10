package com.readium;

import com.readium.model.epub3.Container;

public class EPubAPI {
    static {
        System.loadLibrary("epub3");
    }
    
    private EPubAPI() {}

    // jni call
    public static final native Container openBook(final String path);
}
