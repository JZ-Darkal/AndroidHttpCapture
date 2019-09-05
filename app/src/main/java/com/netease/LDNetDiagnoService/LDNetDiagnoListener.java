package com.netease.LDNetDiagnoService;

/**
 * 监控网络诊断的跟踪信息
 *
 * @author panghui
 */
public interface LDNetDiagnoListener {

    /**
     * 当结束之后返回日志
     *
     * @param log
     */
    public void OnNetDiagnoFinished(String log);


    /**
     * 跟踪过程中更新日志
     *
     * @param log
     */
    public void OnNetDiagnoUpdated(String log);
}
