package com.netease.LDNetDiagnoService;

import android.content.Context;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.netease.LDNetDiagnoService.LDNetPing.LDNetPingListener;
import com.netease.LDNetDiagnoService.LDNetSocket.LDNetSocketListener;
import com.netease.LDNetDiagnoService.LDNetTraceRoute.LDNetTraceRouteListener;
import com.netease.LDNetDiagnoUtils.LDNetUtil;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 网络诊断服务 通过对制定域名进行ping诊断和traceroute诊断收集诊断日志
 *
 * @author panghui
 */
public class LDNetDiagnoService extends
        AbstractLDNetAsyncTaskEx<String, String, String> implements LDNetPingListener,
        LDNetTraceRouteListener, LDNetSocketListener {
    private static final int CORE_POOL_SIZE = 1;// 4
    private static final int MAXIMUM_POOL_SIZE = 1;// 10
    private static final int KEEP_ALIVE = 10;// 10
    private static final BlockingQueue<Runnable> sWorkQueue = new LinkedBlockingQueue<Runnable>(
            2);// 2
    private static final ThreadFactory sThreadFactory = new ThreadFactory() {
        private final AtomicInteger mCount = new AtomicInteger(1);

        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(r, "Trace #" + mCount.getAndIncrement());
            t.setPriority(Thread.MIN_PRIORITY);
            return t;
        }
    };
    private static ThreadPoolExecutor sExecutor = null;
    private final StringBuilder logInfo = new StringBuilder(256);
    private String appCode; // 客户端标记
    private String appName;
    private String appVersion;
    private String UID; // 用户ID
    private String deviceID; // 客户端机器ID，如果不传入会默认取API提供的机器ID
    private String dormain; // 接口域名
    private String carrierName;
    private String ISOCountryCode;
    private String MobileCountryCode;
    private String MobileNetCode;
    private boolean isNetConnected;// 当前是否联网
    private boolean isDomainParseOk;// 域名解析是否成功
    private boolean isSocketConnected;// conected是否成功
    private Context context;
    private String netType;
    private String localIp;
    private String gateWay;
    private String dns1;
    private String dns2;
    private InetAddress[] remoteInet;
    private List<String> remoteIpList;
    private LDNetSocket netSocker;// 监控socket的连接时间
    private LDNetPing netPinger; // 监控ping命令的执行时间
    private LDNetTraceRoute traceRouter; // 监控ping模拟traceroute的执行过程
    private boolean isRunning;
    private LDNetDiagnoListener netDiagnolistener; // 将监控日志上报到前段页面
    private boolean isUseJNICConn = false;
    private boolean isUseJNICTrace = true;
    private TelephonyManager telManager = null; // 用于获取网络基本信息

    public LDNetDiagnoService() {
        super();
    }

    /**
     * 初始化网络诊断服务
     *
     * @param theAppCode
     * @param theDeviceID
     * @param theUID
     * @param theDormain
     */
    public LDNetDiagnoService(Context context, String theAppCode,
                              String theAppName, String theAppVersion, String theUID,
                              String theDeviceID, String theDormain, String theCarrierName,
                              String theISOCountryCode, String theMobileCountryCode,
                              String theMobileNetCode, LDNetDiagnoListener theListener) {
        super();
        this.context = context;
        this.appCode = theAppCode;
        this.appName = theAppName;
        this.appVersion = theAppVersion;
        this.UID = theUID;
        this.deviceID = theDeviceID;
        this.dormain = theDormain;
        this.carrierName = theCarrierName;
        this.ISOCountryCode = theISOCountryCode;
        this.MobileCountryCode = theMobileCountryCode;
        this.MobileNetCode = theMobileNetCode;
        this.netDiagnolistener = theListener;
        //
        this.isRunning = false;
        remoteIpList = new ArrayList<String>();
        telManager = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        sExecutor = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE,
                KEEP_ALIVE, TimeUnit.SECONDS, sWorkQueue, sThreadFactory);

    }

    @Override
    protected String doInBackground(String... params) {
        if (this.isCancelled()) {
            return null;
        }
        // TODO Auto-generated method stub
        return this.startNetDiagnosis();
    }

    @Override
    protected void onPostExecute(String result) {
        if (this.isCancelled()) {
            return;
        }
        super.onPostExecute(result);
        // 线程执行结束
        recordStepInfo("\n网络诊断结束\n");
        this.stopNetDialogsis();
        if (netDiagnolistener != null) {
            netDiagnolistener.OnNetDiagnoFinished(logInfo.toString());
        }
    }

    @Override
    protected void onProgressUpdate(String... values) {
        if (this.isCancelled()) {
            return;
        }
        // TODO Auto-generated method stub
        super.onProgressUpdate(values);
        if (netDiagnolistener != null) {
            netDiagnolistener.OnNetDiagnoUpdated(values[0]);
        }
    }

    @Override
    protected void onCancelled() {
        this.stopNetDialogsis();
    }

    /**
     * 开始诊断网络
     */
    public String startNetDiagnosis() {
        if (TextUtils.isEmpty(this.dormain)) {
            return "";
        }
        this.isRunning = true;
        this.logInfo.setLength(0);
        recordStepInfo("开始诊断...\n");
        recordCurrentAppVersion();
        recordLocalNetEnvironmentInfo();

        if (isNetConnected) {
            // 获取运营商信息
            //recordStepInfo("\n开始获取运营商信息...");
            //String operatorInfo = requestOperatorInfo();
            //if (operatorInfo != null) {
            //recordStepInfo(operatorInfo);
            //}

            // TCP三次握手时间测试
            recordStepInfo("\n开始TCP连接测试...");
            netSocker = LDNetSocket.getInstance();
            netSocker.remoteInet = remoteInet;
            netSocker.remoteIpList = remoteIpList;
            netSocker.initListener(this);
            netSocker.isCConn = this.isUseJNICConn;// 设置是否启用C进行connected
            isSocketConnected = netSocker.exec(dormain);

            // 诊断ping信息, 同步过程

            if (!(isNetConnected && isDomainParseOk && isSocketConnected)) {// 联网&&DNS解析成功&&connect测试成功
                recordStepInfo("\n开始ping...");
                netPinger = new LDNetPing(this, 4);
                recordStepInfo("ping...127.0.0.1");
                netPinger.exec("127.0.0.1", false);
                recordStepInfo("ping本机IP..." + localIp);
                netPinger.exec(localIp, false);
                if (LDNetUtil.NETWORKTYPE_WIFI.equals(netType)) {// 在wifi下ping网关
                    recordStepInfo("ping本地网关..." + gateWay);
                    netPinger.exec(gateWay, false);
                }
                recordStepInfo("ping本地DNS1..." + dns1);
                netPinger.exec(dns1, false);
                recordStepInfo("ping本地DNS2..." + dns2);
                netPinger.exec(dns2, false);
            }

            if (netPinger == null) {
                netPinger = new LDNetPing(this, 4);
            }
            if (netPinger != null) {
                //recordStepInfo("ping..." + LDNetUtil.OPEN_IP);
                //netPinger.exec(LDNetUtil.OPEN_IP, true);
            }

            // 开始诊断traceRoute
            recordStepInfo("\n开始traceroute...");
            traceRouter = LDNetTraceRoute.getInstance();
            traceRouter.initListenter(this);
            traceRouter.isCTrace = this.isUseJNICTrace;
            traceRouter.startTraceRoute(dormain);
            return logInfo.toString();
        } else {
            recordStepInfo("\n\n当前主机未联网,请检查网络！");
            return logInfo.toString();
        }
    }

    /**
     * 停止诊断网络
     */
    public void stopNetDialogsis() {
        if (isRunning) {
            if (netSocker != null) {
                netSocker.resetInstance();
                netSocker = null;
            }

            if (netPinger != null) {
                netPinger = null;
            }
            if (traceRouter != null) {
                traceRouter.resetInstance();
                traceRouter = null;
            }
            cancel(true);// 尝试去取消线程的执行
            if (sExecutor != null && !sExecutor.isShutdown()) {
                sExecutor.shutdown();
                sExecutor = null;
            }

            isRunning = false;
        }
    }

    /**
     * 设置是否需要JNICTraceRoute
     *
     * @param use
     */
    public void setIfUseJNICConn(boolean use) {
        this.isUseJNICConn = use;
    }

    /**
     * 设置是否需要JNICTraceRoute
     *
     * @param use
     */
    public void setIfUseJNICTrace(boolean use) {
        this.isUseJNICTrace = use;
    }

    /**
     * 打印整体loginInfo；
     */
    public void printLogInfo() {
        System.out.print(logInfo);
    }

    /**
     * 如果调用者实现了stepInfo接口，输出信息
     *
     * @param stepInfo
     */
    private void recordStepInfo(String stepInfo) {
        logInfo.append(stepInfo + "\n");
        publishProgress(stepInfo + "\n");
    }

    /**
     * traceroute 消息跟踪
     */
    @Override
    public void OnNetTraceFinished() {
    }

    @Override
    public void OnNetTraceUpdated(String log) {
        if (log == null) {
            return;
        }
        if (this.traceRouter != null && this.traceRouter.isCTrace) {
            if (log.contains("ms") || log.contains("***")) {
                log += "\n";
            }
            logInfo.append(log);
            publishProgress(log);
        } else {
            this.recordStepInfo(log);
        }
    }

    /**
     * socket完成跟踪
     */
    @Override
    public void OnNetSocketFinished(String log) {
        logInfo.append(log);
        publishProgress(log);
    }

    /**
     * socket更新跟踪
     */
    @Override
    public void OnNetSocketUpdated(String log) {
        logInfo.append(log);
        publishProgress(log);
    }

    /**
     * 输出关于应用、机器、网络诊断的基本信息
     */
    private void recordCurrentAppVersion() {
        // 输出应用版本信息和用户ID
        recordStepInfo("应用code:\t" + appCode);
        recordStepInfo("应用名称:\t" + this.appName);
        recordStepInfo("应用版本:\t" + this.appVersion);
//    recordStepInfo("用户id:\t" + UID);

        // 输出机器信息
        recordStepInfo("机器类型:\t" + android.os.Build.MANUFACTURER + ":"
                + android.os.Build.BRAND + ":" + android.os.Build.MODEL);
        recordStepInfo("系统版本:\t" + android.os.Build.VERSION.RELEASE);
        if (telManager != null && TextUtils.isEmpty(deviceID)) {
            deviceID = telManager.getDeviceId();
        }
        recordStepInfo("机器ID:\t" + deviceID);

        // 运营商信息
        if (TextUtils.isEmpty(carrierName)) {
            carrierName = LDNetUtil.getMobileOperator(context);
        }
        recordStepInfo("运营商:\t" + carrierName);

        if (telManager != null && TextUtils.isEmpty(ISOCountryCode)) {
            ISOCountryCode = telManager.getNetworkCountryIso();
        }
        recordStepInfo("ISOCountryCode:\t" + ISOCountryCode);

        if (telManager != null && TextUtils.isEmpty(MobileCountryCode)) {
            String tmp = telManager.getNetworkOperator();
            if (tmp.length() >= 3) {
                MobileCountryCode = tmp.substring(0, 3);
            }
            if (tmp.length() >= 5) {
                MobileNetCode = tmp.substring(3, 5);
            }
        }
        recordStepInfo("MobileCountryCode:\t" + MobileCountryCode);
        recordStepInfo("MobileNetworkCode:\t" + MobileNetCode + "\n");
    }

    /**
     * 输出本地网络环境信息
     */
    private void recordLocalNetEnvironmentInfo() {
        recordStepInfo("诊断域名 " + dormain + "...");

        // 网络状态
        if (LDNetUtil.isNetworkConnected(context)) {
            isNetConnected = true;
            recordStepInfo("当前是否联网:\t" + "已联网");
        } else {
            isNetConnected = false;
            recordStepInfo("当前是否联网:\t" + "未联网");
        }

        // 获取当前网络类型
        netType = LDNetUtil.getNetWorkType(context);
        recordStepInfo("当前联网类型:\t" + netType);
        if (isNetConnected) {
            if (LDNetUtil.NETWORKTYPE_WIFI.equals(netType)) { // wifi：获取本地ip和网关，其他类型：只获取ip
                localIp = LDNetUtil.getLocalIpByWifi(context);
                gateWay = LDNetUtil.pingGateWayInWifi(context);
            } else {
                localIp = LDNetUtil.getLocalIpBy3G();
            }
            recordStepInfo("本地IP:\t" + localIp);
        } else {
            recordStepInfo("本地IP:\t" + "127.0.0.1");
        }
        if (gateWay != null) {
            recordStepInfo("本地网关:\t" + this.gateWay);
        }

        // 获取本地DNS地址
        if (isNetConnected) {
            dns1 = LDNetUtil.getLocalDns("dns1");
            dns2 = LDNetUtil.getLocalDns("dns2");
            recordStepInfo("本地DNS:\t" + this.dns1 + "," + this.dns2);
        } else {
            recordStepInfo("本地DNS:\t" + "0.0.0.0" + "," + "0.0.0.0");
        }

        // 获取远端域名的DNS解析地址
        if (isNetConnected) {
            recordStepInfo("远端域名:\t" + this.dormain);
            isDomainParseOk = parseDomain(this.dormain);// 域名解析
        }
    }

    /**
     * 域名解析
     */
    private boolean parseDomain(String _dormain) {
        boolean flag = false;
        int len = 0;
        String ipString = "";
        Map<String, Object> map = LDNetUtil.getDomainIp(_dormain);
        String useTime = (String) map.get("useTime");
        remoteInet = (InetAddress[]) map.get("remoteInet");
        String timeShow = null;
        if (Integer.parseInt(useTime) > 5000) {// 如果大于1000ms，则换用s来显示
            timeShow = " (" + Integer.parseInt(useTime) / 1000 + "s)";
        } else {
            timeShow = " (" + useTime + "ms)";
        }
        if (remoteInet != null) {// 解析正确
            len = remoteInet.length;
            for (int i = 0; i < len; i++) {
                remoteIpList.add(remoteInet[i].getHostAddress());
                ipString += remoteInet[i].getHostAddress() + ",";
            }
            ipString = ipString.substring(0, ipString.length() - 1);
            recordStepInfo("DNS解析结果:\t" + ipString + timeShow);
            flag = true;
        } else {// 解析不到，判断第一次解析耗时，如果大于10s进行第二次解析
            if (Integer.parseInt(useTime) > 10000) {
                map = LDNetUtil.getDomainIp(_dormain);
                useTime = (String) map.get("useTime");
                remoteInet = (InetAddress[]) map.get("remoteInet");
                if (Integer.parseInt(useTime) > 5000) {// 如果大于1000ms，则换用s来显示
                    timeShow = " (" + Integer.parseInt(useTime) / 1000 + "s)";
                } else {
                    timeShow = " (" + useTime + "ms)";
                }
                if (remoteInet != null) {
                    len = remoteInet.length;
                    for (int i = 0; i < len; i++) {
                        remoteIpList.add(remoteInet[i].getHostAddress());
                        ipString += remoteInet[i].getHostAddress() + ",";
                    }
                    ipString = ipString.substring(0, ipString.length() - 1);
                    recordStepInfo("DNS解析结果:\t" + ipString + timeShow);
                    flag = true;
                } else {
                    recordStepInfo("DNS解析结果:\t" + "解析失败" + timeShow);
                }
            } else {
                recordStepInfo("DNS解析结果:\t" + "解析失败" + timeShow);
            }
        }
        return flag;
    }

    /**
     * 获取运营商信息
     */
    private String requestOperatorInfo() {
        String res = null;
        String url = LDNetUtil.OPERATOR_URL;
        HttpURLConnection conn = null;
        URL Operator_url;
        try {
            Operator_url = new URL(url);
            conn = (HttpURLConnection) Operator_url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(1000 * 10);
            conn.connect();
            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                res = LDNetUtil.getStringFromStream(conn.getInputStream());
                if (conn != null) {
                    conn.disconnect();
                }
            }
            return res;
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
        return res;
    }

    /**
     * ping 消息跟踪
     */
    @Override
    public void OnNetPingFinished(String log) {
        this.recordStepInfo(log);
    }

    @Override
    protected ThreadPoolExecutor getThreadPoolExecutor() {
        // TODO Auto-generated method stub
        return sExecutor;
    }

}
