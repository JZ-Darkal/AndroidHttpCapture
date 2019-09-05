package cn.darkal.networkdiagnosis.Task;

import android.widget.TextView;

import cn.darkal.networkdiagnosis.Utils.NetInfo.NetBasicInfo;
import cn.darkal.networkdiagnosis.Utils.NetInfo.SystemBasicInfo;

/**
 * Created by xuzhou on 2016/8/1.
 */
public class InfoTask extends BaseTask {
    String url;

    TextView resultTextView;
    public Runnable execRunnable = new Runnable() {
        @Override
        public void run() {
            NetBasicInfo mNetBasicInfo = NetBasicInfo.getInstance(resultTextView.getContext());

            resultTextView.post(new updateResultRunnable(mNetBasicInfo.getApnInfo()
                    + "\r\nMac address : \r\n"
                    + "wlan0 :\t" + mNetBasicInfo.getMacAddress("wlan0")
                    + "\np2p0 :\t " + mNetBasicInfo.getMacAddress("p2p0")
                    + "\n\n" + SystemBasicInfo.getBuildInfo()
                    + "\n"));
        }
    };

    public InfoTask(String url, TextView resultTextView) {
        super(url, resultTextView);
        this.url = url;
        this.resultTextView = resultTextView;
    }

    @Override
    public Runnable getExecRunnable() {
        return execRunnable;
    }
}
