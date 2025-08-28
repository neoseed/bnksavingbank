/*
 * 프로그램명 : BaseLifecycle
 * 설　계　자 : Thomas Parker(임예준) - (2023.04.13)
 * 작　성　자 : Thomas Parker(임예준) - (2023.04.13)
 * 적　　　요 : 독립 서비스의 기본 구현체
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
                //INITIALIZE만 허용
                return next == INITIALIZE;
            case INITIALIZE:
                //SHUTDOWN 또는 STARTING 또는 RUNNING 허용
                return next == SHUTDOWN || next == STARTING || next == RUNNING;
            case STARTING:
            case RELOADING:
                //RUNNING만 허용
                return next == RUNNING;
            case RUNNING:
                //RELOADING 또는 SHUTDOWN 허용
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
