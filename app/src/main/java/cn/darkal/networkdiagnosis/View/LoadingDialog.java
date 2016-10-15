package cn.darkal.networkdiagnosis.View;


import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import cn.darkal.networkdiagnosis.R;


/**
 * Created by zhongshengkun on 2015/4/23.
 */
public class LoadingDialog extends ProgressDialog {

    private String mText;
    private boolean mCanBack = true;
    private boolean mSingleLine;
    private Context mContext;
    private ProgressWheel mProgressWheel;
    public LoadingDialog(Context context) {
        super(context, R.style.JzAlertDialogWhite);
    }

    public LoadingDialog(Context context, String text) {
        super(context, R.style.JzAlertDialogWhite);
        this.mText = text;
    }

    public LoadingDialog(Context context, String text, boolean canBack) {
        super(context, R.style.JzAlertDialogWhite);
        this.mText = text;
        mContext = context;
        mCanBack = canBack;
    }

    public LoadingDialog(Context context, String text, boolean canBack, boolean singleLine) {
        super(context, R.style.JzAlertDialogWhite);
        this.mText = text;
        mContext = context;
        mCanBack = canBack;
        mSingleLine = singleLine;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_loading2_dialog);
        TextView mMessage = (TextView) findViewById(R.id.loading_dialog_message);
        if ((mText == null) || (mText.equals(""))) {
            mMessage.setVisibility(View.GONE);
        } else {
            mMessage.setVisibility(View.VISIBLE);
            mMessage.setSingleLine(mSingleLine);
            mMessage.setEllipsize(TextUtils.TruncateAt.END);
            mMessage.setText(mText);
        }
        mProgressWheel = (ProgressWheel) findViewById(R.id.progressBar);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (!mCanBack) {
            Toast.makeText(mContext, "操作正在进行中，请稍后", Toast.LENGTH_SHORT).show();
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }

    @Override
    public void setOnDismissListener(OnDismissListener listener) {
        super.setOnDismissListener(listener);
    }

    /*
     * 设置单行显示
     */
    public void setSingleLine(boolean singleLine) {
        TextView mMessage = (TextView) findViewById(R.id.loading_dialog_message);
    }

    @Override
    public void show() {
        try {
            super.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        if(mProgressWheel != null) {
            mProgressWheel.spin();
        }
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if(mProgressWheel != null) {
            mProgressWheel.stopSpinning();
        }
    }

    @Override
    public void dismiss() {
        try {
            super.dismiss();
            if (mProgressWheel != null) {
                mProgressWheel.stopSpinning();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
