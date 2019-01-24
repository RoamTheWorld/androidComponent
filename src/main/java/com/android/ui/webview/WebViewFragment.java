package com.android.ui.webview;

import java.io.File;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Message;
import android.view.View;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

import com.android.master.R;
import com.android.ui.BaseFragment;
import com.android.ui.widget.LoadingDialog;
import com.android.ui.widget.Toast;
import com.android.utils.Constants;
import com.android.utils.FileUtil;
import com.android.utils.Log;

@SuppressLint({ "SetJavaScriptEnabled", "JavascriptInterface", "ValidFragment" })
public class WebViewFragment extends BaseFragment{
	
	protected WebView webView, openWebView;
	private WebViewClient client;
	private WebChromeClient chromeClient;
	protected Button emptyBtn, loadBtn;
	protected String url;
	protected boolean isLoadFail;
	protected int pageNum = 1;
	
	public WebViewFragment(String url) {
		super();
		this.url = url;
	}
	
	public WebViewFragment() {
		super();
	}

	@Override
	protected int getContentViewId() {
		return R.layout.webview_activity;
	}

	@Override
	public void startActivity(Class clazz, boolean isfinsh) {

	}

	@Override
	protected void findViewById(View view) {
		webView = (WebView) view.findViewById(R.id.webview_detail);
		emptyBtn = (Button) view.findViewById(R.id.empty);
		loadBtn = (Button) view.findViewById(R.id.btn_load);
		loadBtn.setVisibility(View.GONE);
	}
	
	@Override
	protected void setListeners() {
		emptyBtn.setOnClickListener(this);
	}
	
	public void clearCache(String cacheDir){
		try {
			getActivity().deleteDatabase("webview.db");
			getActivity().deleteDatabase("webviewCache.db");
		} catch (Exception e) {
			e.printStackTrace();
		}
		FileUtil.deleteFile(new File(getActivity().getCacheDir(),"webviewCaheChromium"));
		FileUtil.deleteFile(new File(getActivity().getCacheDir(),Constants.FILE_WEBVIEW));
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void init() {
		
		webView.setWebViewClient(getWebViewClient());
		webView.setWebChromeClient(getWebChromeClient());		
		webView.getSettings().setPluginState(PluginState.ON);
		webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
		webView.setWebViewClient(client);
		webView.setWebChromeClient(chromeClient);
		
		String cacheDirPath = getActivity().getFilesDir().getAbsolutePath()+Constants.FILE_WEBVIEW; 
		webView.getSettings().setAppCacheEnabled(true);
		webView.getSettings().setDatabaseEnabled(true);
		webView.getSettings().setDomStorageEnabled(true);
		webView.getSettings().setDatabasePath(cacheDirPath);
		webView.getSettings().setAppCachePath(cacheDirPath);
		
		if (com.android.utils.NetWorkUtil.isConnectInternet(getActivity()))
			webView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
		else
			webView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
		webView.getSettings().setAppCacheMaxSize(8 * 1024 * 1024);
		webView.getSettings().setJavaScriptEnabled(true);
		getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_UNCHANGED);
	
		webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
		webView.getSettings().setSupportMultipleWindows(true);
		
		registerJavasriptInterface();
		
		Log.d("webview url :"+url);
		webView.loadUrl(url);
	}
	
	protected void registerJavasriptInterface(){
		webView.addJavascriptInterface(this, Constants.JS_OBJECT_ANDROID);
	}
	
	private WebViewClient getWebViewClient(){
		if(client == null)
			client = initWebViewClient();
		return client;
	}
	
	private WebChromeClient getWebChromeClient(){
		if(chromeClient == null)
			chromeClient = initWebChromeClient();
		return chromeClient;
	}
	
	protected WebChromeClient initWebChromeClient(){
		return new Up72ChromeClient();
	}
	
	protected WebViewClient initWebViewClient(){
		return new Up72Client();
	}
	
	@JavascriptInterface
	public void close() {
	}
	
	@JavascriptInterface
	public void goHistory() {
		pageNum--;
	}

	@Override
	public void onClick(View v) {
		webView.loadUrl(url);
	}
	
	private class Up72ChromeClient extends WebChromeClient {
		@Override
		public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {
			openWebView = new WebView(view.getContext());
			webView.addView(openWebView);
			WebSettings settings = openWebView.getSettings();
			settings.setJavaScriptEnabled(true);
			openWebView.setWebViewClient(new WebViewClient());
			openWebView.setWebChromeClient(this);

			WebView.WebViewTransport transport = (WebView.WebViewTransport) resultMsg.obj;
			transport.setWebView(openWebView);
			resultMsg.sendToTarget();
			pageNum++;
			return true;
		}

		@Override
		public void onCloseWindow(WebView window) {
			 if (openWebView != null) {
                 openWebView.setVisibility(View.GONE);
                 webView.removeView(openWebView);
        }
		}
	}

	private class Up72Client extends WebViewClient {
		
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			view.loadUrl(url);
			if (!WebViewFragment.this.url.equals(url))
				pageNum++;
			WebViewFragment.this.url = url;
			return true;
		}
		
		@Override
		public void onPageFinished(WebView view, String url) {
			LoadingDialog.dismissLoadingDialog();
			if (isLoadFail)
				emptyBtn.setVisibility(View.VISIBLE);
			else
				webView.setVisibility(View.VISIBLE);
		}

		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			LoadingDialog.showLoadingDialog(getActivity(), "正在加载...");
		}

		@Override
		public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
			isLoadFail = true;
			Toast.show(getActivity(), "加载失败");
		}
	}

}
