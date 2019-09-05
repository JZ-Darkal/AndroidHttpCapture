package com.netease.LDNetDiagnoService;

import android.util.Log;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.List;

public class LDNetSocket {
    private static final int PORT = 80;
    private static final int CONN_TIMES = 4;
    private static final String TIMEOUT = "DNS解析正常,连接超时,TCP建立失败";
    private static final String IOERR = "DNS解析正常,IO异常,TCP建立失败";
    private static final String HOSTERR = "DNS解析失败,主机地址不可达";
    static boolean loaded;
    private static LDNetSocket instance = null;

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

    private final long[] RttTimes = new long[CONN_TIMES];// 用于存储三次测试中每次的RTT值
    public InetAddress[] remoteInet;
    public List<String> remoteIpList;
    public boolean isCConn = true;
    private LDNetSocketListener listener;
    private int timeOut = 6000;// 设置每次连接的timeout时间
    private boolean[] isConnnected;

    private LDNetSocket() {

    }

    public static LDNetSocket getInstance() {
        if (instance == null) {
            instance = new LDNetSocket();
        }
        return instance;
    }

    public void initListener(LDNetSocketListener listener) {
        this.listener = listener;
    }

    /**
     * 通过connect函数测试TCP的RTT时延
     */
    public boolean exec(String host) {
        if (isCConn && loaded) {
            try {
                startJNITelnet(host, "80"); //默认80端口
                return true;
            } catch (UnsatisfiedLinkError e) {
                e.printStackTrace();
                Log.i("LDNetSocket", "call jni failed, call execUseJava");
                return execUseJava(host);
            }
        } else {
            return execUseJava(host);
        }
    }

    /**
     * 使用java执行connected
     */
    private boolean execUseJava(String host) {
        if (remoteInet != null && remoteIpList != null) {
            int len = remoteInet.length;
            isConnnected = new boolean[len];
            for (int i = 0; i < len; i++) {
                if (i != 0) {
                    this.listener.OnNetSocketUpdated("\n");
                }
                isConnnected[i] = execIP(remoteInet[i], remoteIpList.get(i));
            }
            for (Boolean i : isConnnected) {
                if (i == true) {// 一个连接成功即认为成功
                    this.listener.OnNetSocketFinished("\n");
                    return true;
                }
            }

        } else {
            this.listener.OnNetSocketFinished(HOSTERR);
        }
        this.listener.OnNetSocketFinished("\n");
        return false;
    }

    /**
     * 返回某个IP进行5次connect的最终结果
     */
    private boolean execIP(InetAddress inetAddress, String ip) {
        boolean isConnected = true;
        StringBuilder log = new StringBuilder();
        InetSocketAddress socketAddress = null;
        if (inetAddress != null && ip != null) {
            socketAddress = new InetSocketAddress(inetAddress, PORT);
            int flag = 0;
            this.listener.OnNetSocketUpdated("Connect to host: " + ip + "..." + "\n");
            for (int i = 0; i < CONN_TIMES; i++) {
                execSocket(socketAddress, timeOut, i);
                if (RttTimes[i] == -1) {// 一旦发生timeOut,则尝试加长连接时间
                    this.listener.OnNetSocketUpdated((i + 1) + "'s time=" + "TimeOut"
                            + ",  ");
                    timeOut += 4000;
                    if (i > 0 && RttTimes[i - 1] == -1) {// 连续两次连接超时,停止后续测试
                        flag = -1;
                        break;
                    }
                } else if (RttTimes[i] == -2) {
                    this.listener
                            .OnNetSocketUpdated((i + 1) + "'s time=" + "IOException");
                    if (i > 0 && RttTimes[i - 1] == -2) {// 连续两次出现IO异常,停止后续测试
                        flag = -2;
                        break;
                    }
                } else {
                    this.listener.OnNetSocketUpdated((i + 1) + "'s time=" + RttTimes[i]
                            + "ms,  ");
                }
            }
            long time = 0;
            int count = 0;
            if (flag == -1) {
                // log.append(TIMEOUT);
                isConnected = false;
            } else if (flag == -2) {
                // log.append(IOERR);
                isConnected = false;
            } else {
                for (int i = 0; i < CONN_TIMES; i++) {
                    if (RttTimes[i] > 0) {
                        time += RttTimes[i];
                        count++;
                    }
                }
                if (count > 0) {
                    time = time / count;
                    log.append("average=" + time + "ms");
                }
            }
        } else {
            isConnected = false;
        }
        this.listener.OnNetSocketUpdated(log.toString());
        return isConnected;
    }

    /**
     * 针对某个IP第index次connect
     */
    private void execSocket(InetSocketAddress socketAddress, int timeOut,
                            int index) {
        Socket socket = null;
        long start = 0;
        long end = 0;
        try {
            socket = new Socket();
            start = System.currentTimeMillis();
            socket.connect(socketAddress, timeOut);
            end = System.currentTimeMillis();
            RttTimes[index] = end - start;
        } catch (SocketTimeoutException e) {
            RttTimes[index] = -1;// 作为TIMEOUT标识
            e.printStackTrace();
        } catch (IOException e) {
            RttTimes[index] = -2;// 作为IO异常标识
            e.printStackTrace();
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void resetInstance() {
        if (instance != null) {
            instance = null;
        }
    }

    /*
     * 调用jni中native方法
     */
    public native void startJNITelnet(String host, String port);

    public void printSocketInfo(String log) {
        listener.OnNetSocketUpdated(log);
    }

    public interface LDNetSocketListener {
        public void OnNetSocketFinished(String log);

        public void OnNetSocketUpdated(String log);
    }

}
