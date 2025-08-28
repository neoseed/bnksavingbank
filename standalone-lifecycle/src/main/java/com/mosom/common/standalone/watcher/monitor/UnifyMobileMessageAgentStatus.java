/*
 * ���α׷��� : UnifyMobileMessageAgentStatus
 * �����衡�� : Thomas Parker(�ӿ���) - (2025.08.04)
 * �ۡ������� : Thomas Parker(�ӿ���) - (2025.08.04)
 * ���������� : UnifyMobileMessage Agent Status
 */
package com.mosom.common.standalone.watcher.monitor;

import com.mosom.common.standalone.watcher.OverallStatusCode;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static com.mosom.common.standalone.watcher.OverallStatusCode.ABNORMAL;
import static com.mosom.common.standalone.watcher.OverallStatusCode.NORMAL;

public class UnifyMobileMessageAgentStatus extends UnifyMobileMessageStatus implements Serializable {

    private static final long serialVersionUID = 1L;

    private String name;

    private boolean triggerSwitch;

    private boolean triggerWatcherSwitch;

    //�����ڵ�:NORMAL(����), ABNORMAL(������)
    private OverallStatusCode overallStatusCode;

    //�߼� �������� Queue ����
    private int waitingQueueCount;

    //Agent Error �ڵ尡 ��ϵ� Log ����
    private int agentErrorLogCount;

    //���� SMS�� �����ϴ� �����
    private final List<String> recipients;


    public UnifyMobileMessageAgentStatus() {
        overallStatusCode = NORMAL;
        recipients = new ArrayList<String>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isTriggerSwitch() {
        return triggerSwitch;
    }

    public void setTriggerSwitch(boolean triggerSwitch) {
        this.triggerSwitch = triggerSwitch;
    }

    public boolean isTriggerWatcherSwitch() {
        return triggerWatcherSwitch;
    }

    public void setTriggerWatcherSwitch(boolean triggerWatcherSwitch) {
        this.triggerWatcherSwitch = triggerWatcherSwitch;
    }

    void setOverallStatusCode(String overallStatusCode) {
        this.overallStatusCode = OverallStatusCode.valueOf(overallStatusCode);
    }

    void setWaitingQueueCount(int waitingQueueCount) {
        this.waitingQueueCount = waitingQueueCount;
    }

    void setAgentErrorLogCount(int agentErrorLogCount) {
        this.agentErrorLogCount = agentErrorLogCount;
    }

    void addRecipient(String mobileNumber) {
        recipients.add(mobileNumber);
    }

    void clearRecipients() {
        recipients.clear();
    }

    public List<String> getRecipients() {
        return recipients;
    }

    public boolean isNotification() {
        return overallStatusCode == ABNORMAL;
    }

    public String getStatusMessage() {
        return "Agent:" + name + "\nStatus Code:" + overallStatusCode.name() + "\nWaiting Queue:" + waitingQueueCount + "\nError Log:" + agentErrorLogCount;
    }

}
