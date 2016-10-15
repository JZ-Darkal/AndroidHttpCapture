package com.netease.LDNetDiagnoService;

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

import android.content.Context;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.netease.LDNetDiagnoService.LDNetPing.LDNetPingListener;
import com.netease.LDNetDiagnoService.LDNetSocket.LDNetSocketListener;
import com.netease.LDNetDiagnoService.LDNetTraceRoute.LDNetTraceRouteListener;
import com.netease.LDNetDiagnoUtils.LDNetUtil;

/**
 * 网络诊断服务 通过对制定域名进行ping诊断和traceroute诊断收集诊断日志
 *
 * @author panghui
 *
 */
public class LDNetDiagnoService extends
    LDNetAsyncTaskEx<String, String, String> implements LDNetPingListener,
    LDNetTraceRouteListener, LDNetSocketListener {
  private String _appCode; // 客户端标记
  private String _appName;
  private String _appVersion;
  private String _UID; // 用户ID
  private String _deviceID; // 客户端机器ID，如果不传入会默认取API提供的机器ID
  private String _dormain; // 接口域名
  private String _carrierName;
  private String _ISOCountryCode;
  private String _MobileCountryCode;
  private String _MobileNetCode;

  private boolean _isNetConnected;// 当前是否联网
  private boolean _isDomainParseOk;// 域名解析是否成功
  private boolean _isSocketConnected;// conected是否成功
  private Context _context;
  private String _netType;
  private String _localIp;
  private String _gateWay;
  private String _dns1;
  private String _dns2;
  private InetAddress[] _remoteInet;
  private List<String> _remoteIpList;
  private final StringBuilder _logInfo = new StringBuilder(256);
  private LDNetSocket _netSocker;// 监控socket的连接时间
  private LDNetPing _netPinger; // 监控ping命令的执行时间
  private LDNetTraceRoute _traceRouter; // 监控ping模拟traceroute的执行过程
  private boolean _isRunning;

  private LDNetDiagnoListener _netDiagnolistener; // 将监控日志上报到前段页面
  private boolean _isUseJNICConn = false;
  private boolean _isUseJNICTrace = true;
  private TelephonyManager _telManager = null; // 用于获取网络基本信息

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
    this._context = context;
    this._appCode = theAppCode;
    this._appName = theAppName;
    this._appVersion = theAppVersion;
    this._UID = theUID;
    this._deviceID = theDeviceID;
    this._dormain = theDormain;
    this._carrierName = theCarrierName;
    this._ISOCountryCode = theISOCountryCode;
    this._MobileCountryCode = theMobileCountryCode;
    this._MobileNetCode = theMobileNetCode;
    this._netDiagnolistener = theListener;
    //
    this._isRunning = false;
    _remoteIpList = new ArrayList<String>();
    _telManager = (TelephonyManager) context
        .getSystemService(Context.TELEPHONY_SERVICE);
    sExecutor = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE,
        KEEP_ALIVE, TimeUnit.SECONDS, sWorkQueue, sThreadFactory);

  }

  @Override
  protected String doInBackground(String... params) {
    if (this.isCancelled())
      return null;
    // TODO Auto-generated method stub
    return this.startNetDiagnosis();
  }

  @Override
  protected void onPostExecute(String result) {
    if (this.isCancelled())
      return;
    super.onPostExecute(result);
    // 线程执行结束
    recordStepInfo("\n网络诊断结束\n");
    this.stopNetDialogsis();
    if (_netDiagnolistener != null) {
      _netDiagnolistener.OnNetDiagnoFinished(_logInfo.toString());
    }
  }

  @Override
  protected void onProgressUpdate(String... values) {
    if (this.isCancelled())
      return;
    // TODO Auto-generated method stub
    super.onProgressUpdate(values);
    if (_netDiagnolistener != null) {
      _netDiagnolistener.OnNetDiagnoUpdated(values[0]);
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
    if (TextUtils.isEmpty(this._dormain))
      return "";
    this._isRunning = true;
    this._logInfo.setLength(0);
    recordStepInfo("开始诊断...\n");
    recordCurrentAppVersion();
    recordLocalNetEnvironmentInfo();

    if (_isNetConnected) {
      // 获取运营商信息
      //recordStepInfo("\n开始获取运营商信息...");
      //String operatorInfo = requestOperatorInfo();
      //if (operatorInfo != null) {
        //recordStepInfo(operatorInfo);
      //}

      // TCP三次握手时间测试
      recordStepInfo("\n开始TCP连接测试...");
      _netSocker = LDNetSocket.getInstance();
      _netSocker._remoteInet = _remoteInet;
      _netSocker._remoteIpList = _remoteIpList;
      _netSocker.initListener(this);
      _netSocker.isCConn = this._isUseJNICConn;// 设置是否启用C进行connected
      _isSocketConnected = _netSocker.exec(_dormain);

      // 诊断ping信息, 同步过程

      if (!(_isNetConnected && _isDomainParseOk && _isSocketConnected)) {// 联网&&DNS解析成功&&connect测试成功
        recordStepInfo("\n开始ping...");
        _netPinger = new LDNetPing(this, 4);
        recordStepInfo("ping...127.0.0.1");
        _netPinger.exec("127.0.0.1", false);
        recordStepInfo("ping本机IP..." + _localIp);
        _netPinger.exec(_localIp, false);
        if (LDNetUtil.NETWORKTYPE_WIFI.equals(_netType)) {// 在wifi下ping网关
          recordStepInfo("ping本地网关..." + _gateWay);
          _netPinger.exec(_gateWay, false);
        }
        recordStepInfo("ping本地DNS1..." + _dns1);
        _netPinger.exec(_dns1, false);
        recordStepInfo("ping本地DNS2..." + _dns2);
        _netPinger.exec(_dns2, false);
      }
      
      if (_netPinger == null) {
        _netPinger = new LDNetPing(this, 4);
      }
      if (_netPinger != null) {
        //recordStepInfo("ping..." + LDNetUtil.OPEN_IP);
        //_netPinger.exec(LDNetUtil.OPEN_IP, true);
      }

      // 开始诊断traceRoute
      recordStepInfo("\n开始traceroute...");
      _traceRouter = LDNetTraceRoute.getInstance();
      _traceRouter.initListenter(this);
      _traceRouter.isCTrace = this._isUseJNICTrace;
      _traceRouter.startTraceRoute(_dormain);
      return _logInfo.toString();
    } else {
      recordStepInfo("\n\n当前主机未联网,请检查网络！");
      return _logInfo.toString();
    }
  }

  /**
   * 停止诊断网络
   */
  public void stopNetDialogsis() {
    if (_isRunning) {
      if (_netSocker != null) {
        _netSocker.resetInstance();
        _netSocker = null;
      }

      if (_netPinger != null) {
        _netPinger = null;
      }
      if (_traceRouter != null) {
        _traceRouter.resetInstance();
        _traceRouter = null;
      }
      cancel(true);// 尝试去取消线程的执行
      if (sExecutor != null && !sExecutor.isShutdown()) {
        sExecutor.shutdown();
        sExecutor = null;
      }

      _isRunning = false;
    }
  }

  /**
   * 设置是否需要JNICTraceRoute
   *
   * @param use
   */
  public void setIfUseJNICConn(boolean use) {
    this._isUseJNICConn = use;
  }

  /**
   * 设置是否需要JNICTraceRoute
   *
   * @param use
   */
  public void setIfUseJNICTrace(boolean use) {
    this._isUseJNICTrace = use;
  }

  /**
   * 打印整体loginInfo；
   */
  public void printLogInfo() {
    System.out.print(_logInfo);
  }

  /**
   * 如果调用者实现了stepInfo接口，输出信息
   *
   * @param stepInfo
   */
  private void recordStepInfo(String stepInfo) {
    _logInfo.append(stepInfo + "\n");
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
    if (this._traceRouter != null && this._traceRouter.isCTrace) {
      if (log.contains("ms") || log.contains("***")) {
        log += "\n";
      }
      _logInfo.append(log);
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
    _logInfo.append(log);
    publishProgress(log);
  }

  /**
   * socket更新跟踪
   */
  @Override
  public void OnNetSocketUpdated(String log) {
    _logInfo.append(log);
    publishProgress(log);
  }

  /**
   * 输出关于应用、机器、网络诊断的基本信息
   */
  private void recordCurrentAppVersion() {
    // 输出应用版本信息和用户ID
    recordStepInfo("应用code:\t" + _appCode);
    recordStepInfo("应用名称:\t" + this._appName);
    recordStepInfo("应用版本:\t" + this._appVersion);
//    recordStepInfo("用户id:\t" + _UID);

    // 输出机器信息
    recordStepInfo("机器类型:\t" + android.os.Build.MANUFACTURER + ":"
        + android.os.Build.BRAND + ":" + android.os.Build.MODEL);
    recordStepInfo("系统版本:\t" + android.os.Build.VERSION.RELEASE);
    if (_telManager != null && TextUtils.isEmpty(_deviceID)) {
      _deviceID = _telManager.getDeviceId();
    }
    recordStepInfo("机器ID:\t" + _deviceID);

    // 运营商信息
    if (TextUtils.isEmpty(_carrierName)) {
      _carrierName = LDNetUtil.getMobileOperator(_context);
    }
    recordStepInfo("运营商:\t" + _carrierName);

    if (_telManager != null && TextUtils.isEmpty(_ISOCountryCode)) {
      _ISOCountryCode = _telManager.getNetworkCountryIso();
    }
    recordStepInfo("ISOCountryCode:\t" + _ISOCountryCode);

    if (_telManager != null && TextUtils.isEmpty(_MobileCountryCode)) {
      String tmp = _telManager.getNetworkOperator();
      if (tmp.length() >= 3) {
        _MobileCountryCode = tmp.substring(0, 3);
      }
      if (tmp.length() >= 5) {
        _MobileNetCode = tmp.substring(3, 5);
      }
    }
    recordStepInfo("MobileCountryCode:\t" + _MobileCountryCode);
    recordStepInfo("MobileNetworkCode:\t" + _MobileNetCode+"\n");
  }

  /**
   * 输出本地网络环境信息
   */
  private void recordLocalNetEnvironmentInfo() {
    recordStepInfo("诊断域名 " + _dormain + "...");

    // 网络状态
    if (LDNetUtil.isNetworkConnected(_context)) {
      _isNetConnected = true;
      recordStepInfo("当前是否联网:\t" + "已联网");
    } else {
      _isNetConnected = false;
      recordStepInfo("当前是否联网:\t" + "未联网");
    }

    // 获取当前网络类型
    _netType = LDNetUtil.getNetWorkType(_context);
    recordStepInfo("当前联网类型:\t" + _netType);
    if (_isNetConnected) {
      if (LDNetUtil.NETWORKTYPE_WIFI.equals(_netType)) { // wifi：获取本地ip和网关，其他类型：只获取ip
        _localIp = LDNetUtil.getLocalIpByWifi(_context);
        _gateWay = LDNetUtil.pingGateWayInWifi(_context);
      } else {
        _localIp = LDNetUtil.getLocalIpBy3G();
      }
      recordStepInfo("本地IP:\t" + _localIp);
    } else {
      recordStepInfo("本地IP:\t" + "127.0.0.1");
    }
    if (_gateWay != null) {
      recordStepInfo("本地网关:\t" + this._gateWay);
    }

    // 获取本地DNS地址
    if (_isNetConnected) {
      _dns1 = LDNetUtil.getLocalDns("dns1");
      _dns2 = LDNetUtil.getLocalDns("dns2");
      recordStepInfo("本地DNS:\t" + this._dns1 + "," + this._dns2);
    } else {
      recordStepInfo("本地DNS:\t" + "0.0.0.0" + "," + "0.0.0.0");
    }

    // 获取远端域名的DNS解析地址
    if (_isNetConnected) {
      recordStepInfo("远端域名:\t" + this._dormain);
      _isDomainParseOk = parseDomain(this._dormain);// 域名解析
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
    _remoteInet = (InetAddress[]) map.get("remoteInet");
    String timeShow = null;
    if (Integer.parseInt(useTime) > 5000) {// 如果大于1000ms，则换用s来显示
      timeShow = " (" + Integer.parseInt(useTime) / 1000 + "s)";
    } else {
      timeShow = " (" + useTime + "ms)";
    }
    if (_remoteInet != null) {// 解析正确
      len = _remoteInet.length;
      for (int i = 0; i < len; i++) {
        _remoteIpList.add(_remoteInet[i].getHostAddress());
        ipString += _remoteInet[i].getHostAddress() + ",";
      }
      ipString = ipString.substring(0, ipString.length() - 1);
      recordStepInfo("DNS解析结果:\t" + ipString + timeShow);
      flag = true;
    } else {// 解析不到，判断第一次解析耗时，如果大于10s进行第二次解析
      if (Integer.parseInt(useTime) > 10000) {
        map = LDNetUtil.getDomainIp(_dormain);
        useTime = (String) map.get("useTime");
        _remoteInet = (InetAddress[]) map.get("remoteInet");
        if (Integer.parseInt(useTime) > 5000) {// 如果大于1000ms，则换用s来显示
          timeShow = " (" + Integer.parseInt(useTime) / 1000 + "s)";
        } else {
          timeShow = " (" + useTime + "ms)";
        }
        if (_remoteInet != null) {
          len = _remoteInet.length;
          for (int i = 0; i < len; i++) {
            _remoteIpList.add(_remoteInet[i].getHostAddress());
            ipString += _remoteInet[i].getHostAddress() + ",";
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

  @Override
  protected ThreadPoolExecutor getThreadPoolExecutor() {
    // TODO Auto-generated method stub
    return sExecutor;
  }

}
