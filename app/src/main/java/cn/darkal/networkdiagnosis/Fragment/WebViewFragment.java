package cn.darkal.networkdiagnosis.Fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.ConsoleMessage;
import android.webkit.DownloadListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.darkal.networkdiagnosis.Activity.MainActivity;
import cn.darkal.networkdiagnosis.R;
import cn.darkal.networkdiagnosis.SysApplication;
import cn.darkal.networkdiagnosis.Utils.DeviceUtils;
import cn.darkal.networkdiagnosis.Utils.ProxyUtils;
import cn.darkal.networkdiagnosis.Utils.SharedPreferenceUtils;

public class WebViewFragment extends BaseFragment {
    private final static WebViewFragment webViewFragment = new WebViewFragment();
    public Boolean isSetProxy = false;
    public String baseUserAgentString = "Mozilla/5.0 (Linux; Android 5.0.2) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/37.0.0.0";
    public String userAgentString = baseUserAgentString;
    @BindView(R.id.fl_webview)
    WebView webView;
    @BindView(R.id.bt_jump)
    Button jumpButton;
    @BindView(R.id.et_url)
    EditText urlText;
    @BindView(R.id.pb_progress)
    ProgressBar progressBar;
    @BindView(R.id.swipe_container)
    SwipeRefreshLayout swipeRefreshLayout;
    Receiver receiver;

    public static WebViewFragment getInstance() {
        return webViewFragment;
    }

    @Override
    public boolean onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_webview, container, false);
        ButterKnife.bind(this, view);

        isSetProxy = false;

        urlText.setText(MainActivity.HOME_URL);

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setDatabaseEnabled(true);
        String dir = getActivity().getApplicationContext().getDir("database", Context.MODE_PRIVATE).getPath();
        webSettings.setDatabasePath(dir);
        webSettings.setDomStorageEnabled(true);
        webSettings.setGeolocationEnabled(true);

        baseUserAgentString = webSettings.getUserAgentString() + " jdhttpmonitor/" + DeviceUtils.getVersion(getContext());
        webSettings.setUserAgentString(userAgentString);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }
        webView.setDownloadListener(new MyWebViewDownLoadListener());
        webView.setWebViewClient(new WebViewClient() {

//            @Override
//            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
//                if(request.getUrl().toString().startsWith("jdhttpmonitor://webview")) {
//                    Intent intent = new Intent("android.intent.action.VIEW");
//                    intent.setData(Uri.parse(request.getUrl().toString()));
//                    startActivity(intent);
//                    return true;
//                }
//                return false;
//            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.startsWith("jdhttpmonitor://webview")) {
                    Intent intent = new Intent("android.intent.action.VIEW");
                    intent.setData(Uri.parse(url));
                    startActivity(intent);
                    return true;
                }
                return false;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                urlText.setText(url);
            }
        });

        webView.setWebChromeClient(new WebChromeClient() {
//            @Override
//            public void onConsoleMessage(String message, int lineNumber, String sourceID) {
//                super.onConsoleMessage(message, lineNumber, sourceID);
//                ((MainActivity)getActivity()).consoleLog.append(message).append("\n").append("\n");
//            }

            @Override
            public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                ((MainActivity) getActivity()).consoleLog.append(consoleMessage.message()).append("\n").append("\n");
                return super.onConsoleMessage(consoleMessage);
            }

            @Override
            public void onProgressChanged(WebView view, int progress) {
                progressBar.setProgress(progress);
                if (progress == 100) {
                    progressBar.setVisibility(View.GONE);
                    swipeRefreshLayout.setRefreshing(false);
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                }
            }
        });

        jumpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (urlText.getText().length() > 0) {
                    loadUrl(urlText.getText() + "");
                }
            }
        });

        urlText.setImeOptions(EditorInfo.IME_ACTION_GO);
        urlText.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                //当actionId == XX_SEND 或者 XX_DONE时都触发
                //或者event.getKeyCode == ENTER 且 event.getAction == ACTION_DOWN时也触发
                //注意，这是一定要判断event != null。因为在某些输入法上会返回null。
                if (actionId == EditorInfo.IME_ACTION_GO
                        || actionId == EditorInfo.IME_ACTION_DONE
                        || (event != null && KeyEvent.KEYCODE_ENTER == event.getKeyCode()
                        && KeyEvent.ACTION_DOWN == event.getAction())) {
                    if (urlText.getText().length() > 0) {
                        loadUrl(v.getText() + "");
                        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        if (imm != null) {
                            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                        }
                    }
                    //处理事件
                }
                return false;
            }
        });

        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccentDark, R.color.colorAccent);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //重新刷新页面
                webView.loadUrl(webView.getUrl());
            }
        });

        initProxyWebView();

        return view;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
//            initProxyWebView();
            ((MainActivity) getActivity()).navigationView.getMenu().getItem(0).setChecked(true);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        receiver = new Receiver();
        getActivity().registerReceiver(receiver, new IntentFilter("proxyfinished"));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getActivity().unregisterReceiver(receiver);
    }

    public void initProxyWebView() {
        if (getActivity() != null && SysApplication.isInitProxy && !isSetProxy) {
            webView.post(new Runnable() {
                @Override
                public void run() {
                    if (ProxyUtils.setProxy(webView, "127.0.0.1", SysApplication.proxyPort)) {
                        Log.e("~~~~", "initProxyWebView()");
                        webView.loadUrl(urlText.getText() + "");
                        isSetProxy = true;
                    } else {
                        Toast.makeText(webView.getContext(), "Set proxy fail!", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

    public void loadUrl(String url) {
        if (webView != null) {
            if (!isSetProxy) {
                ProxyUtils.setProxy(webView, "127.0.0.1", SysApplication.proxyPort);
                Log.e("~~~~", "initProxyWebView()");
                isSetProxy = true;
            }

            if (!url.startsWith("http://") && !url.startsWith("https://")) {
                url = "http://" + url;
            }
            urlText.setText(url);
            webView.loadUrl(url);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        setUserAgent();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

    }

    public void setUserAgent() {
        String originUA = userAgentString;

        switch (SharedPreferenceUtils.getString(getContext(), "select_ua", "0")) {
            case "0":
                userAgentString = baseUserAgentString;
                break;
            case "1":
                userAgentString = baseUserAgentString + " MQQBrowser/6.2 TBS/036524 MicroMessenger/6.3.18.800 NetType/WIFI Language/zh_CN";
                break;
            case "2":
                userAgentString = baseUserAgentString + " MQQBrowser/6.2 TBS/036524 V1_AND_SQ_6.0.0_300_YYB_D QQ/6.0.0.2605 NetType/WIFI WebP/0.3.0 Pixel/1440";
                break;
            default:
                break;
        }
        WebSettings webSettings = webView.getSettings();
        webSettings.setUserAgentString(userAgentString);

        if (!originUA.equals(userAgentString) && webView != null) {
            reload();
        }
    }

    public void reload() {
        if (webView != null && webView.getUrl() != null) {
            webView.reload();
        }
    }

    public class Receiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            initProxyWebView();
            Log.i("~~~~", "Receiver initProxyWebView");
        }
    }

    private class MyWebViewDownLoadListener implements DownloadListener {
        @Override
        public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype,
                                    long contentLength) {
            Uri uri = Uri.parse(url);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        }
    }
}
