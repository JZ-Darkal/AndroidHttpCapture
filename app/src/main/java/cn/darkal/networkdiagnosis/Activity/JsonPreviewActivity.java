package cn.darkal.networkdiagnosis.Activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import net.lightbody.bmp.core.har.HarEntry;
import net.lightbody.bmp.core.har.HarLog;

import java.nio.charset.Charset;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.darkal.networkdiagnosis.R;
import cn.darkal.networkdiagnosis.SysApplication;

/**
 * Created by Darkal on 2016/9/20.
 * 格式化Json
 */

public class JsonPreviewActivity extends AppCompatActivity {

    @BindView(R.id.tv_detailLayout)
    TextView textView;

    private Handler mHandler = new Handler();
    private String content;
    private int selectedEncode = 0;
    private String[] encodeItem = new String[]{"UTF-8", "ISO-8859-1", "GBK"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_json);
        ButterKnife.bind(this);
//
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

        setupActionBar();

        try {
            int pos = getIntent().getIntExtra("pos", -1);
            if (pos > -1) {
                HarLog harLog = ((SysApplication) getApplication()).proxy.getHar().getLog();
                HarEntry harEntry = harLog.getEntries().get(pos);
                content = harEntry.getResponse().getContent().getText();
                initViewDelay(content);
            } else {
                finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
            finish();
        }
    }

    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    private void setupActionBar() {
        setTitle("内容详情");
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void initViewDelay(final String content) {
        getWindow().getDecorView().post(
                new Runnable() {
                    @Override
                    public void run() {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                formatContent(content);
                            }
                        });
                    }
                }
        );

    }

    public void formatContent(String content) {
        try {
            textView.setText(jsonFormatter(content));
        } catch (Exception e) {
            textView.setText(content);
        }
    }

    public String jsonFormatter(String uglyJSONString) throws Exception {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonParser jp = new JsonParser();
        JsonElement je = jp.parse(uglyJSONString);
        return gson.toJson(je);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.json_menu, menu);
        MenuItem encodeButton = menu.findItem(R.id.encode);
        encodeButton.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                DialogInterface.OnClickListener listener = new ButtonOnClick();
                AlertDialog.Builder builder = new AlertDialog.Builder(JsonPreviewActivity.this);
                builder.setNegativeButton("取消", null);
                builder.setPositiveButton("确认", listener);
                builder.setSingleChoiceItems(encodeItem, selectedEncode, listener);
                builder.create().show();
                return true;
            }
        });


        return super.onCreateOptionsMenu(menu);
    }

    public void changeEncode(int pos) {
        switch (pos) {
            case 0:
                initViewDelay(content);
                break;
            case 1:
                initViewDelay(new String(content.getBytes(Charset.forName("ISO-8859-1")), Charset.forName("UTF-8")));
                break;
            case 2:
                initViewDelay(new String(content.getBytes(Charset.forName("GBK")), Charset.forName("UTF-8")));
                break;
            default:
                initViewDelay(content);
                break;
        }

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
                    selectedEncode = index;
                    changeEncode(index);
                }
            }
        }
    }
}
