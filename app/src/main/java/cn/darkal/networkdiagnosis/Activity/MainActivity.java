package cn.darkal.networkdiagnosis.Activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.security.KeyChain;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.OvershootInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.zxing.QrCodeScanActivity;
import com.tencent.bugly.crashreport.CrashReport;

import net.gotev.uploadservice.ServerResponse;
import net.gotev.uploadservice.UploadInfo;
import net.gotev.uploadservice.UploadStatusDelegate;
import net.lightbody.bmp.BrowserMobProxy;
import net.lightbody.bmp.core.har.Har;
import net.lightbody.bmp.core.har.HarPage;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.darkal.networkdiagnosis.Adapter.FilterAdpter;
import cn.darkal.networkdiagnosis.Bean.PageBean;
import cn.darkal.networkdiagnosis.Fragment.BaseFragment;
import cn.darkal.networkdiagnosis.Fragment.BackHandledInterface;
import cn.darkal.networkdiagnosis.Fragment.NetworkFragment;
import cn.darkal.networkdiagnosis.Fragment.PreviewFragment;
import cn.darkal.networkdiagnosis.Fragment.PreviewFragment_ViewBinder;
import cn.darkal.networkdiagnosis.Fragment.WebViewFragment;
import cn.darkal.networkdiagnosis.R;
import cn.darkal.networkdiagnosis.SysApplication;
import cn.darkal.networkdiagnosis.Utils.DeviceUtils;
import cn.darkal.networkdiagnosis.Utils.FileUtils;
import cn.darkal.networkdiagnosis.Utils.SharedPreferenceUtils;
import cn.darkal.networkdiagnosis.Utils.ZipUtils;
import cn.darkal.networkdiagnosis.View.LoadingDialog;

/**
 * Created by xuzhou on 2016/8/10.
 * MainActivity
 */
public class MainActivity extends AppCompatActivity implements BackHandledInterface {
    public final static String CODE_URL = "#";
    public final static String UPLOAD_URL = "#";
    public final static String HOME_URL = "http://www.qq.com";
    public final static String GUIDE_URL = "http://h5.darkal.cn/har/guide/widget.guide.html";

    public final static int TYPE_NONE = 0;
    public final static int TYPE_SHARE = 1;
    public final static int TYPE_UPLOAD = 2;

    private int mLastHeightOfContainer; // 记录容器上一次的高度,用于检测高度变化
    private int mHeightOfVisibility;
    Boolean isKeyboardOpen = false;
    Boolean shouldExitSearchView = false;

    private BaseFragment mBackHandedFragment;
    private long exitTime = 0;

    @BindView(R.id.fl_contain)
    public View rootView;

    @BindView(R.id.nav_view)
    public NavigationView navigationView;

    @BindView(R.id.fab)
    public FloatingActionMenu fam;

    @BindView(R.id.fab_share)
    public FloatingActionButton shareFab;

    @BindView(R.id.fab_upload)
    public FloatingActionButton uploadFab;

    @BindView(R.id.fab_preview)
    public FloatingActionButton previewFab;

    @BindView(R.id.fab_clear)
    public FloatingActionButton clearFab;

//    int lastX, lastY;
//    Boolean isMove = false;

    public SearchView searchView;
    public MenuItem homeItem;
    public MenuItem searchItem;
    public MenuItem filterMenuItem;

    public Set<String> disablePages = new HashSet<>();
    public StringBuffer consoleLog = new StringBuffer();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        installCert();

        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ButterKnife.bind(this);
        initFloatingActionMenu();


        OnGlobalLayoutListener globalLayoutListener = new OnGlobalLayoutListener(rootView);
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(globalLayoutListener);

        getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

        if (getIntent().getStringExtra("url") != null && getIntent().getStringExtra("url").length() > 0) {
            WebViewFragment webViewFragment = WebViewFragment.getInstance();
            webViewFragment.loadUrl(getIntent().getStringExtra("url"));
            switchContent(webViewFragment);
        } else {
            switchContent(WebViewFragment.getInstance());
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(navigationItemListener);
        navigationView.getMenu().getItem(0).setChecked(true);

//        if (savedInstanceState != null && savedInstanceState.getInt("tab") != 0) {
//            switch (savedInstanceState.getInt("tab")) {
//                case 1:
//                    switchContent(WebViewFragment.getInstance());
//                    break;
//                case 2:
//                    switchContent(NetworkFragment.getInstance());
//                    break;
//                case 3:
//                    switchContent(PreviewFragment.getInstance());
//                    break;
//            }
//        }

//        fab.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View view, MotionEvent motionEvent) {
//                //获取到手指处的横坐标和纵坐标
//                int x = (int) motionEvent.getX();
//                int y = (int) motionEvent.getY();
//                switch (motionEvent.getAction()) {
//                    case MotionEvent.ACTION_DOWN:
//                        lastX = x;
//                        lastY = y;
//                        isMove = false;
//                        break;
//                    case MotionEvent.ACTION_MOVE:
//                        //计算移动的距离
//                        int offX = x - lastX;
//                        int offY = y - lastY;
//                        if (offX * offX + offY * offY < 400 && !isMove) {
//                            break;
//                        }
//                        view.offsetLeftAndRight(offX);
//                        view.offsetTopAndBottom(offY);
//                        isMove = true;
//                        break;
//                    case MotionEvent.ACTION_UP:
//                        if (!isMove) {
//                            createPage();
//                        }
//                        break;
//                }
//                return true;
//            }
//        });
    }

    public void createPage() {
        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA)
                .format(new Date(System.currentTimeMillis()));

        ((SysApplication) getApplication()).proxy.newPage(time);

        Snackbar.make(rootView, "HAR文件已创建新的分页:" + time, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (fam.isOpened()) {
            fam.close(true);
        } else if (mBackHandedFragment == null || !(mBackHandedFragment instanceof WebViewFragment)) {
            switchContent(WebViewFragment.getInstance());
        } else if (!mBackHandedFragment.onBackPressed()) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                finish();
                System.exit(0);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

//        MenuItem pageButton = menu.findItem(R.id.action_page);
//        pageButton.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
//            @Override
//            public boolean onMenuItemClick(MenuItem item) {
//                createPage();
//                return true;
//            }
//        });

        filterMenuItem = menu.findItem(R.id.action_filter);
        homeItem = menu.findItem(R.id.action_home);
        searchItem = menu.findItem(R.id.search);
//        uaMenuItem = menu.findItem(R.id.ua);
//        logMenuItem = menu.findItem(R.id.log);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) searchItem.getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setFocusable(false);
        searchView.setIconifiedByDefault(true);
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setQueryHint("请输入URL关键字...");

        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchContent(PreviewFragment.getInstance());
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                PreviewFragment.getInstance().filterItem(query);
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                PreviewFragment.getInstance().filterItem(newText);
                shouldExitSearchView = newText.length() == 0;
                return false;
            }
        });
        return true;
    }

    public void changeStateBar(Fragment fragment) {
        if (filterMenuItem != null && searchItem != null && homeItem != null) {
            if (fragment instanceof WebViewFragment) {
                homeItem.setVisible(true);
            } else {
                homeItem.setVisible(false);
            }
            if (fragment instanceof PreviewFragment) {
                filterMenuItem.setVisible(true);
                searchItem.setVisible(true);
                clearFab.setVisibility(View.VISIBLE);
            } else {
                filterMenuItem.setVisible(false);
                searchItem.setVisible(false);
                clearFab.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.action_home){
            WebViewFragment webViewFragment = WebViewFragment.getInstance();
            webViewFragment.loadUrl(HOME_URL);
            switchContent(webViewFragment);
            return true;
        }
        if(id == R.id.action_guide){
            WebViewFragment webViewFragment = WebViewFragment.getInstance();
            webViewFragment.loadUrl(GUIDE_URL);
            switchContent(webViewFragment);
            return true;
        }

        if (id == R.id.action_filter) {
            showFilter(this, TYPE_NONE);
            return true;
        }
        if (id == R.id.action_exit) {
            finish();
            System.exit(0);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public NavigationView.OnNavigationItemSelectedListener navigationItemListener = new NavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(MenuItem item) {
            // Handle navigation view item clicks here.
            int id = item.getItemId();

            if (!SysApplication.isInitProxy) {
                Toast.makeText(MainActivity.this, "请等待程序初始化完成", Toast.LENGTH_LONG).show();
                return true;
            }
            if (id == R.id.nav_camera) {
                Intent intent = new Intent(MainActivity.this, QrCodeScanActivity.class);
                startActivity(intent);
            } else if (id == R.id.nav_gallery) {
                switchContent(WebViewFragment.getInstance());
            }
//            else if (id == R.id.nav_preview) {
//                switchContent(PreviewFragment.getInstance());
//            }
            else if (id == R.id.nav_slideshow) {
                switchContent(NetworkFragment.getInstance());
            } else if (id == R.id.nav_manage) {
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
            } else if (id == R.id.nav_ua) {
                showUaDialog();
            } else if (id == R.id.nav_cosole) {
                showLogDialog();
            } else if (id == R.id.nav_host) {
                showHostDialog();
            } else if (id == R.id.nav_page) {
                createPage();
            }

            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
            return true;
        }
    };

    /**
     * 修改显示的内容 不会重新加载
     **/
    public void switchContent(Fragment to) {
        Boolean isAdded = false;
        try {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            if (getSupportFragmentManager().getFragments() != null) {
                for (Fragment f : getSupportFragmentManager().getFragments()) {
                    if (to.getClass().isAssignableFrom(f.getClass())) {
                        if (!f.isAdded()) {
                            transaction.add(R.id.fl_contain, f, f.getClass().getName());
                        } else {
                            transaction.show(f);
                        }
                        isAdded = true;
                    } else {
                        transaction.hide(f);
                        f.setUserVisibleHint(false);
                    }
                }
            }
            if (!isAdded) {
                if (!to.isAdded()) { // 先判断是否被add过
                    transaction.add(R.id.fl_contain, to, to.getClass().getName()).commitNow();
                } else {
                    transaction.show(to).commitNow(); // 隐藏当前的fragment，显示下一个
                }
            } else {
                transaction.commitNow();
            }
            if (getSupportFragmentManager().findFragmentByTag(to.getClass().getName()) != null) {
                getSupportFragmentManager().findFragmentByTag(to.getClass().getName()).setUserVisibleHint(true);
            }
//            setSelectedFragment((BaseFragment) to);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setSelectedFragment(BaseFragment selectedFragment) {
        this.mBackHandedFragment = selectedFragment;
    }

    public void installCert() {
        final String CERTIFICATE_RESOURCE = "/sslSupport/ca-certificate-rsa.cer";
        Boolean isInstallCert = SharedPreferenceUtils.getBoolean(this, "isInstallCert", false);

        if (!isInstallCert) {
            Toast.makeText(this, "必须安装证书才可实现HTTPS抓包", Toast.LENGTH_LONG).show();
            try {
                byte[] keychainBytes;
                InputStream bis = MainActivity.class.getResourceAsStream(CERTIFICATE_RESOURCE);
                keychainBytes = new byte[bis.available()];
                bis.read(keychainBytes);

                Intent intent = KeyChain.createInstallIntent();
                intent.putExtra(KeyChain.EXTRA_CERTIFICATE, keychainBytes);
                intent.putExtra(KeyChain.EXTRA_NAME, "NetworkDiagnosis CA Certificate");
                startActivityForResult(intent, 3);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 3) {
            if (resultCode == Activity.RESULT_OK) {
                SharedPreferenceUtils.putBoolean(this,"isInstallCert", true);
                Toast.makeText(this, "安装成功", Toast.LENGTH_LONG).show();

            } else {
                Toast.makeText(this, "安装失败", Toast.LENGTH_LONG).show();
            }
        }
    }

    /**
     * 点击屏幕关闭键盘
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        Boolean shouldDispatchTouchEvent = false;
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (isShouldHideInput(v, ev)) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null && v != null) {
                    if (isKeyboardOpen) {
                        shouldDispatchTouchEvent = true;
                    }
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
            return shouldDispatchTouchEvent || super.dispatchTouchEvent(ev);
        }
        // 必不可少，否则所有的组件都不会有TouchEvent了
        return getWindow().superDispatchTouchEvent(ev) || onTouchEvent(ev);
    }

    public boolean isShouldHideInput(View view, MotionEvent event) {
        if (view != null && (view instanceof EditText)) {
            int[] leftTop = {0, 0};
            view.getLocationInWindow(leftTop);
            //获取输入框当前的location位置
            int left = leftTop[0];
            int top = leftTop[1];
            int bottom = top + view.getHeight();
            int right = left + view.getWidth();
            if (event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom) {
                return false;
            }
        }

        return true;
    }

    private class OnGlobalLayoutListener implements ViewTreeObserver.OnGlobalLayoutListener {
        private View mView;

        public OnGlobalLayoutListener(View view) {
            mView = view;
        }

        @Override
        public void onGlobalLayout() {
            int currentHeight = mView.getHeight();
            if (currentHeight < mLastHeightOfContainer) { // 软键盘打开
                if (mHeightOfVisibility == 0) {
                    mHeightOfVisibility = currentHeight;
                }
                isKeyboardOpen = true;
            } else if (currentHeight > mLastHeightOfContainer && mLastHeightOfContainer != 0) { // 软键盘关闭
                isKeyboardOpen = false;
                // 隐藏搜索框
                if (shouldExitSearchView) {
                    searchItem.collapseActionView();
                }
            }
            mLastHeightOfContainer = currentHeight;
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handleUriStartupParams();
        if (intent.getAction().equals("android.intent.action.SEARCH")) {
            switchContent(PreviewFragment.getInstance());
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        handleUriStartupParams();
    }

    /**
     * 启动的时候根据bundle参数决定切换到哪个tab
     */
    private void handleUriStartupParams() {
        Intent intent = getIntent();
        if (intent == null) {
            return;
        }

        Uri uri = intent.getData();
        if (uri == null) {
            return;
        }
        // 这里要把data置空，否则每次进来锁屏解锁，都会触发这些逻辑
        intent.setData(null);

        String query = uri.getQuery();

        // "param="这个字符串已经占了6个字符了，所以query的长度至少要有8（加上花括号)
        if (query == null || query.length() < 8) {
            return;
        }

        try {
            // 通过uri.getQuery()得到的query已经是解码过的字符串了，不需要再decode
            String jsonString = query.substring(6);
            JSONObject json = new JSONObject(jsonString);

            WebViewFragment webViewFragment = WebViewFragment.getInstance();
            webViewFragment.loadUrl(json.getString("url"));

            switchContent(webViewFragment);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void createZip(final Runnable callback) {
        showLoading("打包中");
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final Har har = getFiltedHar();
                    final File saveHarFile = new File(Environment.getExternalStorageDirectory() + "/har/test.har");
                    har.writeTo(saveHarFile);

                    ZipUtils.zip(Environment.getExternalStorageDirectory() + "/har",
                            Environment.getExternalStorageDirectory() + "/test.zip");

                    rootView.post(new Runnable() {
                        @Override
                        public void run() {
                            Snackbar.make(rootView, "HAR文件已保存至" + saveHarFile.getPath() + " 共计："
                                    + har.getLog().getEntries().size() + "个请求", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                        }
                    });

                    MainActivity.this.runOnUiThread(callback);
                } catch (Exception e) {
                    rootView.post(new Runnable() {
                        @Override
                        public void run() {
                            Snackbar.make(rootView, "HAR文件保存失败", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                        }
                    });
                    CrashReport.postCatchedException(e);
                    e.printStackTrace();
                } finally {
                    dismissLoading();
                }
            }
        }).start();
    }

    public void shareZip() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("application/octet-stream");
                intent.putExtra(Intent.EXTRA_SUBJECT, "分享HAR文件");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(Environment.getExternalStorageDirectory() + "/test.zip")));
                startActivity(Intent.createChooser(intent, "share"));
            }
        };

        createZip(runnable);
    }

    public void uploadZip() {
        showUploadDialog(this);
    }

    public class MyUploadDelegate implements UploadStatusDelegate {
        @Override
        public void onProgress(UploadInfo uploadInfo) {
            Log.e("~~~~", uploadInfo.getProgressPercent() + "");
        }

        @Override
        public void onError(UploadInfo uploadInfo, Exception exception) {
            dismissLoading();
            Snackbar.make(rootView, "上传失败！", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            exception.printStackTrace();
            CrashReport.postCatchedException(exception);
        }

        @Override
        public void onCompleted(UploadInfo uploadInfo, ServerResponse serverResponse) {
            try {
                JSONObject jsonObject = new JSONObject(serverResponse.getBodyAsString());
                if (jsonObject.getInt("errId") == 0) {
                    Snackbar.make(rootView, "上传成功！", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                } else if (jsonObject.getInt("errId") == 2 || jsonObject.getInt("errId") == 11004) {
                    Snackbar.make(rootView, "验证码错误！", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                    showUploadDialog(MainActivity.this);
                } else {
                    Snackbar.make(rootView, "上传失败！", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                }
            } catch (Exception e) {
                Snackbar.make(rootView, "上传失败！", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
            dismissLoading();
        }

        @Override
        public void onCancelled(UploadInfo uploadInfo) {
            dismissLoading();
        }
    }

    private LoadingDialog loadingDialog;

    public void showLoading(String text) {
        try {
            if (loadingDialog == null) {
                loadingDialog = new LoadingDialog(this, text);
            }
            loadingDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void dismissLoading() {
        if (loadingDialog != null) {
            loadingDialog.dismiss();
            loadingDialog = null;
        }
    }

    //显示基于Layout的AlertDialog
    private void showUploadDialog(Context context) {
        LayoutInflater inflater = LayoutInflater.from(this);
        View textEntryView = inflater.inflate(R.layout.alert_code, null);
        final EditText edtInput = (EditText) textEntryView.findViewById(R.id.et_code);
        final ImageView imageView = (ImageView) textEntryView.findViewById(R.id.iv_code);
//        final String uuid = SystemBasicInfo.getUUID(this);

        final String key = Math.random() + "";
        Glide.with(this).load(CODE_URL + "?key=" + key + "&scene=2&t=" + Math.random()).into(imageView);

        // 点击刷新验证码
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edtInput.setText("");
                Glide.with(MainActivity.this).load(CODE_URL + "?key=" + key + "&scene=2&t=" + Math.random()).into(imageView);
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(true);
        builder.setTitle("请输入验证码");
        builder.setView(textEntryView);
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(edtInput.getWindowToken(), 0);
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        String serverUrl = UPLOAD_URL + "?code=" + edtInput.getText() + "&os=Android&module=" + Build.MODEL.replace(" ", "") + "&key=" + key;
                        showLoading("上传中");
                        FileUtils.uploadFiles(MainActivity.this, new MyUploadDelegate(), serverUrl, "upload", Environment.getExternalStorageDirectory() + "/test.zip");
                    }
                };
                createZip(runnable);
            }
        });

        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(edtInput.getWindowToken(), 0);
            }
        });
        builder.show();
    }

    public void showFilter(final Context context, final int type) {
        BrowserMobProxy proxy = ((SysApplication) getApplication()).proxy;

        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.alert_filter, null);
        ListView listView = (ListView) view.findViewById(R.id.list);
        List<HarPage> harPageList = proxy.getHar().getLog().getPages();
        final List<PageBean> pageBeenList = new ArrayList<>();

        for (HarPage harPage : harPageList) {
            PageBean pageBean = new PageBean();
            if (disablePages.contains(harPage.getId())) {
                pageBean.setSelected(false);
            }
            pageBean.setName(harPage.getTitle());
            pageBean.setCount(proxy.getHar(harPage.getId()).getLog().getEntries().size() + "");
            pageBeenList.add(pageBean);
        }

        listView.setAdapter(new FilterAdpter(pageBeenList));

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(true);
        builder.setTitle("请选择分页");
        builder.setView(view);

        builder.setPositiveButton("确认", null);
        builder.setNegativeButton("取消", null);

        class ConfirmListener implements View.OnClickListener {
            private AlertDialog alertDialog;

            public ConfirmListener(AlertDialog alertDialog) {
                this.alertDialog = alertDialog;
            }

            @Override
            public void onClick(View v) {
                disablePages.clear();

                int entryCount = 0;
                int selectedCount = 0;
                for (PageBean pageBean : pageBeenList) {
                    if (!pageBean.getSelected()) {
                        disablePages.add(pageBean.getName());
                    } else {
                        entryCount += pageBean.getCountInt();
                        selectedCount++;
                    }
                }

                if (selectedCount > 0) {
                    PreviewFragment.getInstance().notifyHarChange();

                    if (type == TYPE_SHARE) {
                        shareZip();
                    }
                    if (type == TYPE_UPLOAD) {
                        if (selectedCount > 1 && entryCount > 1000) {
                            Toast.makeText(context, "选择的请求总数过多,建议使用分享功能或减少选择",
                                    Toast.LENGTH_LONG).show();
                        }
                        uploadZip();
                    }
                    alertDialog.dismiss();
                } else {
                    Toast.makeText(context, "请至少选择一个分页", Toast.LENGTH_LONG).show();
                }
            }
        }

        final AlertDialog alertDialog = builder.create();
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface d) {
                alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(
                        new ConfirmListener(alertDialog));
            }
        });

        alertDialog.show();
    }

    // 获取已选中的page
    public Set<String> getPageSet() {
        BrowserMobProxy proxy = ((SysApplication) getApplication()).proxy;

        Set<String> pageSet = new HashSet<>();
        for (HarPage harPage : proxy.getHar().getLog().getPages()) {
            if (!disablePages.contains(harPage.getId())) {
                pageSet.add(harPage.getId());
            }
        }

        return pageSet;
    }

    public Har getFiltedHar() {
        BrowserMobProxy proxy = ((SysApplication) getApplication()).proxy;
        return proxy.getHar(getPageSet());
    }

//    @Override
//    protected void onSaveInstanceState(Bundle outState) {
//        int tab;
//        if (mBackHandedFragment instanceof PreviewFragment) {
//            tab = 3;
//        } else if (mBackHandedFragment instanceof NetworkFragment) {
//            tab = 2;
//        } else {
//            tab = 1;
//        }
//        outState.putInt("tab", tab);
//        super.onSaveInstanceState(outState);
//    }

    public void initFloatingActionMenu() {
        fam.setClosedOnTouchOutside(true);
        AnimatorSet set = new AnimatorSet();

        ObjectAnimator scaleOutX = ObjectAnimator.ofFloat(fam.getMenuIconView(), "scaleX", 1.0f, 0.2f);
        ObjectAnimator scaleOutY = ObjectAnimator.ofFloat(fam.getMenuIconView(), "scaleY", 1.0f, 0.2f);

        ObjectAnimator scaleInX = ObjectAnimator.ofFloat(fam.getMenuIconView(), "scaleX", 0.2f, 1.0f);
        ObjectAnimator scaleInY = ObjectAnimator.ofFloat(fam.getMenuIconView(), "scaleY", 0.2f, 1.0f);

        scaleOutX.setDuration(50);
        scaleOutY.setDuration(50);

        scaleInX.setDuration(150);
        scaleInY.setDuration(150);

        scaleInX.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                fam.getMenuIconView().setImageResource(fam.isOpened()
                        ? R.drawable.ic_file_upload_white_24dp : R.drawable.ic_close_white_24dp);
                if(mBackHandedFragment instanceof PreviewFragment){
                    if(fam.isOpened()){
                        clearFab.show(true);
                    }else {
                        clearFab.hide(true);
                    }
                }
            }
        });

        set.play(scaleOutX).with(scaleOutY);
        set.play(scaleInX).with(scaleInY).after(scaleOutX);
        set.setInterpolator(new OvershootInterpolator(2));

        fam.setIconToggleAnimatorSet(set);

        shareFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFilter(MainActivity.this,TYPE_SHARE);
                fam.close(true);
            }
        });

        uploadFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFilter(MainActivity.this,TYPE_UPLOAD);
                fam.close(true);
            }
        });

        previewFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchContent(PreviewFragment.getInstance());
                fam.close(true);
            }
        });

        clearFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("请确认是否清除所有请求?");
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        ((SysApplication)getApplication()).proxy.getHar().getLog().clearAllEntries();
                        PreviewFragment.getInstance().notifyHarChange();
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                });
                builder.create().show();
            }
        });
    }

    private String[] uaItem = new String[]{"手机浏览器", "微信环境", "手Q环境"};

    public void showUaDialog() {
        DialogInterface.OnClickListener buttonListener = new ButtonOnClick();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        String selected = SharedPreferenceUtils.getString(this, "select_ua", "0");
        int pos;
        try {
            pos = Integer.parseInt(selected);
        } catch (NumberFormatException e) {
            pos = -1;
        }
        builder.setTitle("环境切换");
        builder.setSingleChoiceItems(uaItem, pos, buttonListener);
        builder.setPositiveButton("确认", buttonListener);
        builder.setNegativeButton("取消", buttonListener);
        builder.create().show();
    }

    private class ButtonOnClick implements DialogInterface.OnClickListener {

        private int index = -1; // 表示选项的索引

        @Override
        public void onClick(DialogInterface dialog, int which) {
            if (which >= 0) {
                index = which;
            } else {
                //用户单击的是【确定】按钮
                if (which == DialogInterface.BUTTON_POSITIVE) {
                    SharedPreferenceUtils.putString(MainActivity.this, "select_ua", index + "");
                    WebViewFragment.getInstance().setUserAgent();
                }
            }
        }
    }

    public void showLogDialog(){
        View textEntryView = LayoutInflater.from(this).inflate(R.layout.alert_textview, null);
        TextView edtInput = (TextView) textEntryView.findViewById(R.id.tv_content);
        edtInput.setText(consoleLog);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Console.log");
        builder.setCancelable(true);
        builder.setView(textEntryView);
        builder.setPositiveButton("确认", null);
        builder.setNegativeButton("清空", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                consoleLog.setLength(0);
            }
        });
        builder.show();
    }

    public void showHostDialog(){
        View textEntryView = LayoutInflater.from(this).inflate(R.layout.alert_edittext, null);
        final EditText editText = (EditText) textEntryView.findViewById(R.id.et_content);

        String host = SharedPreferenceUtils.getString(this, "system_host", "");
        editText.setText(host);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setView(textEntryView);
        builder.setTitle("HOST配置");
        builder.setMessage("配置hosts，以空格分隔，每行一个");
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
                SharedPreferenceUtils.putString(MainActivity.this, "system_host", editText.getText()+"");
                DeviceUtils.changeHost(((SysApplication)getApplication()).proxy,editText.getText()+"");
            }
        });
        builder.setNegativeButton("清空", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
                SharedPreferenceUtils.putString(MainActivity.this, "system_host", "");
                DeviceUtils.changeHost(((SysApplication)getApplication()).proxy,editText.getText()+"");
            }
        });
        builder.show();
    }
}
