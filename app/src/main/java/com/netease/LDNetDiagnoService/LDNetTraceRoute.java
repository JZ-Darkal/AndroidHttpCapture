package com.netease.LDNetDiagnoService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.util.Log;

/**
 * 通过ping模拟traceroute过程
 * 
 * @author panghui
 * 
 */
public class LDNetTraceRoute {
  private final String LOG_TAG = "LDNetTraceRoute";
  private static LDNetTraceRoute instance;

  private LDNetTraceRoute() {
  }

  public static LDNetTraceRoute getInstance() {
    if (instance == null) {
      instance = new LDNetTraceRoute();
    }
    return instance;
  }

  LDNetTraceRouteListener listener;
  public boolean isCTrace = true;

  public void initListenter(LDNetTraceRouteListener listener) {
    this.listener = listener;
  }

  /**
   * 监控NetPing的日志输出到Service
   * 
   * @author panghui
   * 
   */
  public interface LDNetTraceRouteListener {
    public void OnNetTraceUpdated(String log);

    public void OnNetTraceFinished();
  }

  /**
   * 执行指定host的traceroute
   * 
   * @param host
   * @return
   */
  public void startTraceRoute(String host) {
    if (isCTrace && loaded) {
      try {
        startJNICTraceRoute(host);
      } catch (UnsatisfiedLinkError e) {
        e.printStackTrace();
        // 如果c调用失败改调JAVA代码
        Log.i("LDNetTraceRoute", "调用java模拟traceRoute");
        TraceTask trace = new TraceTask(host, 1);
        execTrace(trace);
      }
    } else {
      TraceTask trace = new TraceTask(host, 1);
      execTrace(trace);
    }
  }

  public void resetInstance() {
    if (instance != null) {
      instance = null;
    }
  }

  /**
   * 调用jni c函数执行traceroute过程
   */
  public native void startJNICTraceRoute(String traceCommand);

  static boolean loaded;
  static {
    try {
      System.loadLibrary("tracepath");
      loaded = true;
    } catch (UnsatisfiedLinkError e) {
      e.printStackTrace();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * 供jni c函数回调
   * 
   * @param log
   */
  public void printTraceInfo(String log) {
    // Log.i(LOG_TAG, log);
    listener.OnNetTraceUpdated(log);
  }

  private static final String MATCH_TRACE_IP = "(?<=From )(?:[0-9]{1,3}\\.){3}[0-9]{1,3}";
  private static final String MATCH_PING_IP = "(?<=from ).*(?=: icmp_seq=1 ttl=)";
  private static final String MATCH_PING_TIME = "(?<=time=).*?ms";

  /**
   * 执行ping命令，返回ping命令的全部控制台输出
   * 
   * @param ping
   * @return
   */
  private String execPing(PingTask ping) {
    Process process = null;
    String str = "";
    BufferedReader reader = null;
    try {
      process = Runtime.getRuntime().exec("ping -c 1 " + ping.getHost());
      reader = new BufferedReader(new InputStreamReader(
          process.getInputStream()));
      String line = null;
      while ((line = reader.readLine()) != null) {
        str += line;
      }
      reader.close();
      process.waitFor();

    } catch (IOException e) {
      e.printStackTrace();
    } catch (InterruptedException e) {
      e.printStackTrace();
    } finally {
      try {
        if (reader != null) {
          reader.close();
        }
        process.destroy();
      } catch (Exception e) {
      }
    }
    return str;
  }

  /**
   * 通过ping命令模拟执行traceroute的过程
   * 
   * @param trace
   * @return
   */
  private void execTrace(TraceTask trace) {
    Pattern patternTrace = Pattern.compile(MATCH_TRACE_IP);
    Pattern patternIp = Pattern.compile(MATCH_PING_IP);
    Pattern patternTime = Pattern.compile(MATCH_PING_TIME);

    Process process = null;
    BufferedReader reader = null;
    boolean finish = false;
    try {
      // 通过ping的跳数控制，取得相应跳输的ip地址，然后再次执行ping命令读取时间
      while (!finish && trace.getHop() < 30) {
        // 先发出ping命令获得某个跳数的ip地址
        String str = "";
        // -c 1 同时发送消息次数 －t是指跳数
        String command = "ping -c 1 -t " + trace.getHop() + " "
            + trace.getHost();

        process = Runtime.getRuntime().exec(command);
        reader = new BufferedReader(new InputStreamReader(
            process.getInputStream()));
        String line = null;
        while ((line = reader.readLine()) != null) {
          str += line;
        }
        reader.close();
        process.waitFor();

        Matcher m = patternTrace.matcher(str);

        // 如果成功获得trace:IP，则再次发送ping命令获取ping的时间
        StringBuilder log = new StringBuilder(256);
        if (m.find()) {
          String pingIp = m.group();
          PingTask pingTask = new PingTask(pingIp);

          String status = execPing(pingTask);
          if (status.length() == 0) {
            log.append("unknown host or network error\n");
            finish = true;
          } else {
            Matcher matcherTime = patternTime.matcher(status);
            if (matcherTime.find()) {
              String time = matcherTime.group();
              log.append(trace.getHop());
              log.append("\t\t");
              log.append(pingIp);
              log.append("\t\t");
              log.append(time);
              log.append("\t");
            } else {
              log.append(trace.getHop());
              log.append("\t\t");
              log.append(pingIp);
              log.append("\t\t timeout \t");
            }
            listener.OnNetTraceUpdated(log.toString());
            trace.setHop(trace.getHop() + 1);
          }

        }

        // 否则：what
        else {
          Matcher matchPingIp = patternIp.matcher(str);
          if (matchPingIp.find()) {
            String pingIp = matchPingIp.group();
            Matcher matcherTime = patternTime.matcher(str);
            if (matcherTime.find()) {
              String time = matcherTime.group();
              log.append(trace.getHop());
              log.append("\t\t");
              log.append(pingIp);
              log.append("\t\t");
              log.append(time);
              log.append("\t");
              listener.OnNetTraceUpdated(log.toString());
            }
            finish = true;
          } else {
            if (str.length() == 0) {
              log.append("unknown host or network error\t");
              finish = true;
            } else {
              log.append(trace.getHop());
              log.append("\t\t timeout \t");
              trace.setHop(trace.getHop() + 1);
            }
            listener.OnNetTraceUpdated(log.toString());
          }
        }// else no match traceIPPattern
      }// while
    } catch (IOException e) {
      e.printStackTrace();
    } catch (InterruptedException e) {
      e.printStackTrace();
    } finally {
      try {
        if (reader != null) {
          reader.close();
        }
        process.destroy();
      } catch (Exception e) {
      }
    }

    listener.OnNetTraceFinished();
  }

  /**
   * Ping任务
   * 
   * @author panghui
   * 
   */
  private class PingTask {

    private String host;
    private static final String MATCH_PING_HOST_IP = "(?<=\\().*?(?=\\))";

    public String getHost() {
      return host;
    }

    public PingTask(String host) {
      super();
      this.host = host;
      Pattern p = Pattern.compile(MATCH_PING_HOST_IP);
      Matcher m = p.matcher(host);
      if (m.find()) {
        this.host = m.group();
      }

    }
  }

  /**
   * 生成trace任务
   * 
   * @author panghui
   * 
   */
  private class TraceTask {
    private final String host;
    private int hop;

    public TraceTask(String host, int hop) {
      super();
      this.host = host;
      this.hop = hop;
    }

    public String getHost() {
      return host;
    }

    public int getHop() {
      return hop;
    }

    public void setHop(int hop) {
      this.hop = hop;
    }
  }
}
