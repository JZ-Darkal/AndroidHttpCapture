package com.netease.LDNetDiagnoUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LDPingParse {
  public static String getFormattingStr(String host, String log) {
    StringBuilder logRes = new StringBuilder();
    if (log.contains("timeout")) {
      logRes.append("ping: cannot resolve " + host + ": Timeout");
    } else if (log.contains("unknown")) {
      logRes.append("ping: cannot resolve " + host + ": Unknown host");
    } else {
      makePingResponse(log, logRes);
    }
    return logRes.toString();
  }

  public static void makePingResponse(String log, StringBuilder logRes) {
    String hostIp = getIP(log);
    List<String> bytesList = getSumBytes(log);
    List<String> ttlList = getTTL(log);
    List<String> timeList = getTime(log);
    List<String> icmpList = getIcmp_seq(log);
    int len = timeList.size();
    for (int i = 0; i < len - 1; i++) {
      logRes.append(bytesList.get(i) + "bytes from " + hostIp + ": icmp_seq=#"
          + icmpList.get(i) + " ttl=" + ttlList.get(i) + " time="
          + timeList.get(i) + "ms" + "\n");
    }
    logRes.append(bytesList.get(len - 1) + "bytes from " + hostIp
        + ": icmp_seq=#" + icmpList.get(len - 1) + " ttl="
        + ttlList.get(len - 1) + " time=" + timeList.get(len - 1) + "ms");
  }

  private static List<String> getTime(String log) {
    List<String> timeList = new ArrayList<String>();
    String regex = "(?<==)([\\.0-9\\s]+)(?=ms)";
    Pattern p = Pattern.compile(regex);
    Matcher m = p.matcher(log);
    while (m.find()) {
      timeList.add(m.group().toString().trim());
    }
    return timeList;
  }

  private static List<String> getSumBytes(String log) {
    List<String> bytesList = new ArrayList<String>();
    String regex = "(?<=\\D)([\\s0-9]+)(?=bytes)";
    Pattern p = Pattern.compile(regex);
    Matcher m = p.matcher(log);
    while (m.find()) {
      String string = m.group().toString().trim();
      if (m.group().toString().trim().matches("\\d+")) {
        bytesList.add(string);
      }
    }
    return bytesList;
  }

  private static List<String> getTTL(String log) {
    List<String> ttlList = new ArrayList<String>();
    String regex = "(?<=ttl=)([0-9]+)(?=\\s)";
    Pattern p = Pattern.compile(regex);
    Matcher m = p.matcher(log);
    while (m.find()) {
      String tmp = m.group().toString().trim();
      ttlList.add(tmp);
    }
    return ttlList;
  }

  private static String getIP(String log) {
    String hostIp = null;
    String regex = "(?<=\\()([\\d]+\\.)+[\\d]+(?=\\))";
    Pattern p = Pattern.compile(regex);
    Matcher m = p.matcher(log);
    while (m.find()) {
      hostIp = m.group().toString().trim();
    }
    return hostIp;
  }

  private static List<String> getIcmp_seq(String log) {
    List<String> icmpList = new ArrayList<String>();
    String regex = "(?<=icmp_seq=)([0-9]+)(?=\\s)";
    Pattern p = Pattern.compile(regex);
    Matcher m = p.matcher(log);
    while (m.find()) {
      String tmp = m.group().toString().trim();
      icmpList.add(tmp);
    }
    return icmpList;
  }
}
