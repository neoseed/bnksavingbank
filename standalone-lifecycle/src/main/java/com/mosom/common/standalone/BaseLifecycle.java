/*
 * ���α׷��� : BaseLifecycle
 * �����衡�� : Thomas Parker(�ӿ���) - (2023.04.13)
 * �ۡ������� : Thomas Parker(�ӿ���) - (2023.04.13)
 * ���������� : ���� ������ �⺻ ����ü
 */
package com.mosom.common.standalone;

import org.apache.log4j.Logger;

import static com.dmdlogging.DmdLog.getLog;
import static com.mosom.common.standalone.LifecycleSituations.*;
import static com.mosom.consparam.LogCons.MOSOM_COMMON;

abstract public class BaseLifecycle implements ILifecycle {

    protected ILifecycleOption option;

    protected LifecycleSituations situation = SHUTDOWN;

    @Override
    public void initialize(ILifecycleOption option) throws LifecycleException {
        this.option = option;
        situation(INITIALIZE);
    }

    @Override
    public void start() throws LifecycleException {
        situation(RUNNING);
    }

    @Override
    public void shutdown() {
        situation(SHUTDOWN);
    }

    @Override
    public LifecycleSituations situation() {
        return situation;
    }

    @Override
    public ILifecycleOption option() {
        return option;
    }

    protected void situation(LifecycleSituations situation) {
        if (!isValidTransition(situation)) {
            throw new IllegalStateException("Invalid state transition from '" + this.situation + "' to '" + situation + "'");
        }

        this.situation = situation;
    }

    protected boolean isValidTransition(LifecycleSituations next) {
        switch (situation) {
            case SHUTDOWN:
                //INITIALIZE�� ���
                return next == INITIALIZE;
            case INITIALIZE:
                //SHUTDOWN �Ǵ� STARTING �Ǵ� RUNNING ���
                return next == SHUTDOWN || next == STARTING || next == RUNNING;
            case STARTING:
            case RELOADING:
                //RUNNING�� ���
                return next == RUNNING;
            case RUNNING:
                //RELOADING �Ǵ� SHUTDOWN ���
                return next == RELOADING || next == SHUTDOWN;
            default:
                return false;
        }
    }

    protected boolean isSituation(LifecycleSituations... situations) {
        for (LifecycleSituations situation : situations) {
            if (this.situation == situation) {
                return true;
            }
        }

        return false;
    }

    protected Logger log() {
        return getLog(MOSOM_COMMON);
    }

}
