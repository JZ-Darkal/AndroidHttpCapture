package cn.darkal.networkdiagnosis.Task;

import android.util.Log;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by xuzhou on 2016/8/1.
 */
public class PingTask extends BaseTask {
    String url;
    TextView resultTextView;

    public PingTask(String url, TextView resultTextView) {
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
            BufferedReader in = null;
            Runtime rt = Runtime.getRuntime();
            boolean FoundMatch = false;
            String pingCommand = "/system/bin/ping -c 10 " + url;
            Log.e("TAG", "ping thread is running");
            try {
                Process pro = rt.exec(pingCommand);
                in = new BufferedReader(new InputStreamReader(pro.getInputStream()));

                final StringBuilder sb = new StringBuilder();
                String line = in.readLine();

                while (line != null) {
                    sb.append(line + "\n");
                    resultTextView.post(new updateResultRunnable(line + "\n"));
                    line = in.readLine();
                }
            } catch (IOException e) {
                resultTextView.post(new updateResultRunnable(e.toString() + "\n"));
            } finally {
                try{
                    if (in != null) {
                        in.close();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    };
}
