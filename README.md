Launcher-Android
---------------------
A small Android application to serve as a launcher/testbed for the Readium SDK. 

How to get source from github
-------------------------------
 git clone --recursive https://github.com/readium/Launcher-Android.git
 
Warning
--------
 There is an issue on older version of Android. The reading activity currently works for API level 11 (HoneyComb) and higher.
 The issue happens when loading book resources on devices with API level 10 and lower. The method WebViewClient.onLoadResource(WebView view, String url) gets called when a resource is loaded. If we call WebView.loadData, the current page is discarded and a new page is loaded. The readium-shared-js files become out of scope.

How to build
------------
`cd readium-sdk/Platform/Android`

`./ndk-compile.sh build <your-ndk-path>`

Note: In theory, one should be able to define ANDROID_NDK (as I have) and skip the path part, but that doesn't work.

How to open eclipse project

- Launch ADT (the branded version of Eclipse, Build: v22.0.1-685705) or Eclipse with ADT plugin(s) installed
- Import the project in the SDKLauncher-Android folder
- Created a new run configuration with:
    - The launch action: `org.readium.sdklauncher_android.MainActivity`
    - Target: Nexus Emulator, Android 4.2.2 API Level 17 armeabi-v7a (or other emulator or device you have)
- Ensure that there is something for the app to open
    - Open the emulator in the Android Virtual Device manager (AVD) (in Eclipse it is in Window/Android Virtual Device Manager)
    - Edit the emulator 
        - If there isn't one then add one with the New… button
    - Check that the sdcard has some storage (e.g. 512 MB).  By default there isn't any
    - Open an Android shell:
        - `adb shell`
        - `mkdir epubtest`
        - Copy one or more files there:
        - `adb push <local folder/file> <device folder>`, e.g.
            - `adb push epub30-spec-20121128.epub /sdcard/epubtest`
- Run the new run configuration (above)
- This should bring up the app and show you your file(s) to open.
