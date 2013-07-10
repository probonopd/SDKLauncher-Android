package org.readium.sdklauncher_android;

import java.io.ByteArrayInputStream;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.readium.model.epub3.Container;
import com.readium.model.epub3.Package;

public class WebViewActivity extends Activity {

	private static final String TAG = "WebViewActivity";
	private static final String ASSET_PREFIX = "file:///android_asset/";
	private static final String READER_SKELETON = "file:///android_asset/reader.html";
	
	private WebView webview;
	private Package pckg;
	private String href;
	private String baseUrl;
	private String idRef;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_web_view);
		
		webview = (WebView) findViewById(R.id.webview);
		initWebView();

        Intent intent = getIntent();
        if (intent.getFlags() == Intent.FLAG_ACTIVITY_NEW_TASK) {
            Bundle extras = intent.getExtras();
            if (extras != null) {
                Container container = ContainerHolder.getInstance().get(extras.getInt(Constants.CONTAINER_ID));
                if (container == null) {
                	finish();
                	return;
                }
                pckg = container.getDefaultPackage();
                //TODO Merge params into a single openPageRequestData JSON (cf reader_view.js)
                href = extras.getString(Constants.HREF);
                idRef = extras.getString(Constants.IDREF);
                baseUrl = extras.getString(Constants.BASE_URL, "");
            }
        }

        // Load the page skeleton
        webview.loadUrl(READER_SKELETON);
	}

	@SuppressLint("SetJavaScriptEnabled")
	private void initWebView() {
		webview.getSettings().setJavaScriptEnabled(true);
		webview.getSettings().setAllowUniversalAccessFromFileURLs(true);
		webview.getSettings().setLightTouchEnabled(true);
		webview.getSettings().setPluginState(WebSettings.PluginState.ON);
		webview.setWebViewClient(new EpubWebViewClient());
		webview.addJavascriptInterface(new EpubInterface(), "LauncherUI");
	}

	public boolean onMenuItemSelected(int featureId, MenuItem item) {
	    if (item.getItemId() == R.id.add_bookmark) {
			Log.i(TAG, "Add a bookmark");
			bookmarkCurrentPage();
			return true;
	    }
	    return false;
	}
	
	public void onClick(View v) {
		if (v.getId() == R.id.previous) {
			openPagePrevious();
		} else if (v.getId() == R.id.next) {
			openPageNext();
		}
	}
	
	private void bookmarkCurrentPage() {
		loadJS("window.LauncherUI.getBookmarkData(ReadiumSDK.reader.bookmarkCurrentPage());");
	}
	
	private void openPagePrevious() {
		loadJS("ReadiumSDK.reader.openPagePrev();");
	}
	
	private void openPageNext() {
		loadJS("ReadiumSDK.reader.openPageNext();");
	}
	
	private void openBook(String packageData, String openPageRequest) {
		loadJSOnReady("ReadiumSDK.reader.openBook("+packageData+", "+openPageRequest+");");
	}
	
	private void openContentUrl(String href, String baseUrl) {
		loadJSOnReady("ReadiumSDK.reader.openContentUrl(\""+href+"\", \""+baseUrl+"\");");
	}
	
	private void openSpineItemPage(String idRef, int page) {
		loadJSOnReady("ReadiumSDK.reader.openSpineItemPage(\""+idRef+"\", "+page+");");
	}

	private void openSpineItemElementCfi(String idRef, String elementCfi) {
		loadJSOnReady("ReadiumSDK.reader.openSpineItemElementCfi(\""+idRef+"\",\""+elementCfi+"\");");
	}

    private void loadJSOnReady(String jScript) {
        loadJS("$(document).ready(function () {" + jScript + "});");
    }

    private void loadJS(String jScript) {
        webview.loadUrl("javascript:(function(){" + jScript + "})()");
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.web_view, menu);
		return true;
	}

    public final class EpubWebViewClient extends WebViewClient {
    	
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
        	Log.i(TAG, "onPageStarted: "+url);
        }
        
        @Override
        public void onPageFinished(WebView view, String url) {
        	Log.i(TAG, "onPageFinished: "+url);
        	if (url.equals(READER_SKELETON)) {
        		JSONObject openPageRequest = new JSONObject();
        		try {
	        		if (idRef != null) {
	        			openPageRequest.put("idref", idRef).put("spineItemPageIndex", 0);
	        		} else if (href != null) {
	        			openPageRequest.put("contentRefUrl", href).put("sourceFileHref", baseUrl);
	        		}
        		} catch (JSONException e) {
        			Log.e(TAG, ""+e.getMessage(), e);
        		}
        		openBook(pckg.toJSON(), openPageRequest.toString());
        	}
        }
        
        @Override
        public void onLoadResource(WebView view, String url) {
        	Log.i(TAG, "onLoadResource: "+url);
        }

        @Override
        public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
        	Log.i(TAG, "shouldInterceptRequest ? "+url);

            byte[] data = pckg.getContent(cleanResourceUrl(url));
        	//Log.i(TAG, "data : "+new String(data));
            // TODO Pass the correct mimetype
        	return new WebResourceResponse(null, "utf-8", new ByteArrayInputStream(data));
        }
        
        private String cleanResourceUrl(String url) {
        	String cleanUrl = url.replace(ASSET_PREFIX, "");
        	return (cleanUrl.startsWith(pckg.getBasePath())) ? 
        			cleanUrl.replaceFirst(pckg.getBasePath(), "") : cleanUrl;
        }
    }
    
	public class EpubInterface {

		@JavascriptInterface
		public void onOpenPage(String currentPagesInfo) {
			Log.i(TAG, "currentPagesInfo: "+currentPagesInfo);
		}
		
		@JavascriptInterface
		public void getBookmarkData(String bookmarkData) {
			Log.i(TAG, "bookmarkData: "+bookmarkData);
		}
	}

}
