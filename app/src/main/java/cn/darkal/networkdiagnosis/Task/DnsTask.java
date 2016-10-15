package cn.darkal.networkdiagnosis.Task;

import android.widget.TextView;
import java.net.InetAddress;

/**
 * Created by xuzhou on 2016/8/1.
 */
public class DnsTask extends BaseTask {
    String url;
    TextView resultTextView;

    public DnsTask(String url, TextView resultTextView) {
        super(url, resultTextView);
        this.url = url;
        this.resultTextView = resultTextView;
    }

    @Override
    public Runnable getExecRunnable() {
        return execRunnable;
    }

    public Runnable execRunnable = new Runnable() {
        @Override
        public void run() {
            StringBuilder sb = new StringBuilder();
            try{
                InetAddress aaa = InetAddress.getByName(url);
                InetAddress[] addrs = InetAddress.getAllByName(url);
                sb.append("Begin: \n" + aaa.toString() + "\nEnd\n");
                for (InetAddress adr : addrs)
                {
                    sb.append(adr.toString() + "\n");
                    resultTextView.post(new updateResultRunnable(adr.toString() + "\n"));
                }
            }
            catch (Exception e){
                resultTextView.post(new updateResultRunnable(e.toString() + "\n"));
            }
        }
    };
}
