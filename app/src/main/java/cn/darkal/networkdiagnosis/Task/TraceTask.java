package cn.darkal.networkdiagnosis.Task;

import android.content.Context;
import android.widget.TextView;

import com.netease.LDNetDiagnoService.LDNetDiagnoListener;
import com.netease.LDNetDiagnoService.LDNetDiagnoService;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.darkal.networkdiagnosis.Utils.DeviceUtils;


/**
 * Created by xuzhou on 2016/8/1.
 */
public class TraceTask extends BaseTask  implements LDNetDiagnoListener {
    String url;
    TextView resultTextView;
    Context context;
    String result;

    public TraceTask(Context context , String url, TextView resultTextView)  {
        super(url, resultTextView);
        this.context = context;
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
            try{
//                TraceRouteWithPing traceRouteWithPing = new TraceRouteWithPing(url, TraceTask.this);
//                traceRouteWithPing.executeTraceRoute();
                LDNetDiagnoService _netDiagnoService = new LDNetDiagnoService(context.getApplicationContext(),
                        "NetworkDiagnosis", "网络诊断应用", DeviceUtils.getVersion(context), "",
                        "", url, "", "",
                        "", "", TraceTask.this);
                // 设置是否使用JNIC 完成traceroute
                _netDiagnoService.setIfUseJNICTrace(true);
                _netDiagnoService.execute();
            }
            catch (Exception e){
                resultTextView.post(new updateResultRunnable(e.toString() + "\n"));
            }
        }
    };

    public void setResult(String result){
        Pattern pattern = Pattern.compile("(?<=rom )[\\w\\W]+(?=\\n\\n)");
        Matcher matcher = pattern.matcher(result);
        if(matcher.find()){
            resultTextView.post(new updateResultRunnable(matcher.group(0) + "\n"));
        }
    }

    @Override
    public void OnNetDiagnoFinished(String log) {

    }

    @Override
    public void OnNetDiagnoUpdated(String log) {
        resultTextView.post(new updateResultRunnable(log));
    }
}
