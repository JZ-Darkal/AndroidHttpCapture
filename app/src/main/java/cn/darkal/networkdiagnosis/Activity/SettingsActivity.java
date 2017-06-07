package cn.darkal.networkdiagnosis.Activity;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Environment;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.provider.Settings;
import android.security.KeyChain;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;
import android.widget.Toast;

import com.tencent.bugly.beta.Beta;

import net.lightbody.bmp.BrowserMobProxy;
import net.lightbody.bmp.proxy.dns.AdvancedHostResolver;

import org.apache.commons.io.IOUtils;

import java.io.FileInputStream;

import cn.darkal.networkdiagnosis.R;
import cn.darkal.networkdiagnosis.SysApplication;
import cn.darkal.networkdiagnosis.Utils.DeviceUtils;
import cn.darkal.networkdiagnosis.Utils.SharedPreferenceUtils;
import cn.darkal.networkdiagnosis.View.LoadingDialog;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p/>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class SettingsActivity extends AppCompatPreferenceActivity implements Preference.OnPreferenceChangeListener {

    ListPreference lp;//创建一个ListPreference对象
    Preference hostPreference;

    /**
     * Helper method to determine if the device has an extra-large screen. For
     * example, 10" tablets are extra-large.
     */
    private static boolean isXLargeTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupActionBar();

        addPreferencesFromResource(R.xml.pref_data_sync);

        lp = (ListPreference) findPreference("select_ua");
        //设置获取ListPreference中发生的变化
        lp.setOnPreferenceChangeListener(this);
        lp.setSummary(lp.getEntry());

        findPreference("system_host").setOnPreferenceChangeListener(this);

        findPreference("enable_filter").setOnPreferenceChangeListener(this);

        findPreference("install_cert").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                changeSystemProxy();
                return false;
            }
        });

        findPreference("system_proxy").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                return false;
            }
        });

        findPreference("app_version").setSummary(DeviceUtils.getVersion(this));

        findPreference("app_version").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                try {
                    Beta.checkUpgrade();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return false;
            }

        });

        hostPreference = findPreference("app_host");
        hostPreference.setSummary(getHost());
    }

    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onIsMultiPane() {
        return isXLargeTablet(this);
    }


    //让所选择的项显示出来,获取变化并显示出来
    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference instanceof ListPreference) {
            //把preference这个Preference强制转化为ListPreference类型
            ListPreference listPreference = (ListPreference) preference;
            //获取ListPreference中的实体内容
            CharSequence[] entries = listPreference.getEntries();
            //获取ListPreference中的实体内容的下标值
            int index = listPreference.findIndexOfValue((String) newValue);
            //把listPreference中的摘要显示为当前ListPreference的实体内容中选择的那个项目
            listPreference.setSummary(entries[index]);
        }

        // 设置hosts
        if (preference.getKey().equals("system_host")) {
            DeviceUtils.changeHost(((SysApplication)getApplication()).proxy,newValue.toString());
            hostPreference.setSummary(getHost());
        }

        // 重启抓包进程
        if (preference.getKey().equals("enable_filter")) {
            Toast.makeText(this, "重启程序后生效", Toast.LENGTH_SHORT).show();
//            ((SysApplication)getApplication()).stopProxy();
//            ((SysApplication)getApplication()).startProxy();
//            Toast.makeText(this, "抓包进程重启完成", Toast.LENGTH_SHORT).show();
        }
        return true;
    }


    public void installCert() {
        final String CERTIFICATE_RESOURCE = Environment.getExternalStorageDirectory() + "/har/littleproxy-mitm.pem";
        Toast.makeText(this, "必须安装证书才可实现HTTPS抓包", Toast.LENGTH_LONG).show();
        try {
            byte[] keychainBytes;
            FileInputStream is = null;
            try {
                is = new FileInputStream(CERTIFICATE_RESOURCE);
                keychainBytes = new byte[is.available()];
                is.read(keychainBytes);
            } finally {
                IOUtils.closeQuietly(is);
            }

            Intent intent = KeyChain.createInstallIntent();
            intent.putExtra(KeyChain.EXTRA_CERTIFICATE, keychainBytes);
            intent.putExtra(KeyChain.EXTRA_NAME, "NetworkDiagnosis CA Certificate");
            startActivityForResult(intent, 3);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private LoadingDialog loadingDialog;

    public void showLoading(final String text) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (loadingDialog == null) {
                        loadingDialog = new LoadingDialog(SettingsActivity.this, text);
                    }
                    loadingDialog.show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void dismissLoading() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (loadingDialog != null) {
                    loadingDialog.dismiss();
                    loadingDialog = null;
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 3) {
            if (resultCode == Activity.RESULT_OK) {
                SharedPreferenceUtils.putBoolean(this, "isInstallNewCert", true);
                Toast.makeText(this, "安装成功", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "安装失败", Toast.LENGTH_LONG).show();
            }
        }
    }

    public void changeSystemProxy() {
        installCert();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    public String getHost() {
        String result = "";
        BrowserMobProxy browserMobProxy = ((SysApplication) getApplication()).proxy;
        AdvancedHostResolver advancedHostResolver = browserMobProxy.getHostNameResolver();
        for (String key : advancedHostResolver.getHostRemappings().keySet()) {
            result += key + " " + advancedHostResolver.getHostRemappings().get(key) + "\n";
        }
        return result.length() > 1 ? result.substring(0, result.length() - 1) : "无";
    }
}
