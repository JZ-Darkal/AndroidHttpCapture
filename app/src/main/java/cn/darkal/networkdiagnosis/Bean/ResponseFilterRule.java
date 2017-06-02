package cn.darkal.networkdiagnosis.Bean;

import java.io.Serializable;

import static cn.darkal.networkdiagnosis.Bean.ResponseFilterRule.RULE_TYPE.STRING_REPLACE;

/**
 * Created by darkal on 2017/5/31.
 */

public class ResponseFilterRule implements Serializable{
    enum RULE_TYPE{
        STRING_REPLACE,
        BEGIN_INSERT,
        END_INSERT
    }

    private RULE_TYPE ruleType =  STRING_REPLACE;
    private String url;
    private String replaceRegex;
    private String replaceContent;
    private Boolean isEnable = true;

    public RULE_TYPE getRuleType() {
        return ruleType;
    }

    public void setRuleType(RULE_TYPE ruleType) {
        this.ruleType = ruleType;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getReplaceRegex() {
        return replaceRegex;
    }

    public void setReplaceRegex(String replaceRegex) {
        this.replaceRegex = replaceRegex;
    }

    public String getReplaceContent() {
        return replaceContent;
    }

    public void setReplaceContent(String replaceContent) {
        this.replaceContent = replaceContent;
    }

    public Boolean getEnable() {
        return isEnable;
    }

    public void setEnable(Boolean enable) {
        isEnable = enable;
    }
}
