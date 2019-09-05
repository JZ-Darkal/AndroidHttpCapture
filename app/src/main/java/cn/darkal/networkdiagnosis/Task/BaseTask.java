package cn.darkal.networkdiagnosis.Task;

import android.widget.TextView;

/**
 * Created by xuzhou on 2016/8/1.
 */
public abstract class BaseTask {

    String url;
    TextView resultTextView;

    String tag;

    public BaseTask(String url, TextView resultTextView) {
        this.url = url;
        this.resultTextView = resultTextView;
    }

    public void doTask() {
        resultTextView.setText("");
        tag = System.currentTimeMillis() + "";
        resultTextView.setTag(tag);
        // TraceTask运行于主线程
        if (this instanceof TraceTask) {
            getExecRunnable().run();
        } else {
            new Thread(getExecRunnable()).start();
        }
    }

    public abstract Runnable getExecRunnable();

    public class updateResultRunnable implements Runnable {
        String resultString;

        public updateResultRunnable(String resultString) {
            this.resultString = resultString;
        }

        @Override
        public void run() {
            if (resultTextView != null && resultTextView.getTag().equals(tag)) {
                resultTextView.append(resultString);
                resultTextView.requestFocus();
            }
        }
    }
}
