package com.netease.LDNetDiagnoService;

import android.util.Log;

import com.netease.LDNetDiagnoUtils.LDPingParse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 直接通过ping命令监测网络
 */
public class LDNetPing {
    private static final String MATCH_PING_IP = "(?<=from ).*(?=: icmp_seq=1 ttl=)";
    private final int sendCount; // 每次ping发送数据包的个数
    LDNetPingListener listener; // 回传ping的结果

    public LDNetPing(LDNetPingListener listener, int theSendCount) {
        super();
        this.listener = listener;
        this.sendCount = theSendCount;
    }

    /**
     * 执行ping命令，返回ping命令的全部控制台输出
     *
     * @param ping
     * @return
     */
    private String execPing(PingTask ping, boolean isNeedL) {
        String cmd = "ping -c ";
        if (isNeedL) {
            cmd = "ping -s 8185 -c  ";
        }
        Process process = null;
        String str = "";
        BufferedReader reader = null;
        try {
            process = Runtime.getRuntime().exec(
                    cmd + this.sendCount + " " + ping.getHost());
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
     * 执行指定host的traceroute
     *
     * @param host
     * @return
     */
    public void exec(String host, boolean isNeedL) {
        PingTask pingTask = new PingTask(host);
        StringBuilder log = new StringBuilder(256);
        String status = execPing(pingTask, isNeedL);
        if (Pattern.compile(MATCH_PING_IP).matcher(status).find()) {
            Log.i("LDNetPing", "status" + status);
            log.append("\t" + status);
        } else {
            if (status.length() == 0) {
                log.append("unknown host or network error");
            } else {

                log.append("timeout");
            }
        }
        String logStr = LDPingParse.getFormattingStr(host, log.toString());
        this.listener.OnNetPingFinished(logStr);
    }

    /**
     * 监控NetPing的日志输出到Service
     *
     * @author panghui
     */
    public interface LDNetPingListener {
        public void OnNetPingFinished(String log);
    }

    /**
     * Ping任务
     *
     * @author panghui
     */
    private class PingTask {

        private static final String MATCH_PING_HOST_IP = "(?<=\\().*?(?=\\))";
        private String host;

        public PingTask(String host) {
            super();
            this.host = host;
            Pattern p = Pattern.compile(MATCH_PING_HOST_IP);
            Matcher m = p.matcher(host);
            if (m.find()) {
                this.host = m.group();
            }

        }

        public String getHost() {
            return host;
        }
    }
}
