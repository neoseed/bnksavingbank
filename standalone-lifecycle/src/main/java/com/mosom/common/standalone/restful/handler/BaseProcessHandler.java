/*
 * ���α׷��� : BaseProcessHandler
 * �����衡�� : Thomas Parker(�ӿ���) - (2023.03.08)
 * �ۡ������� : Thomas Parker(�ӿ���) - (2023.03.08)
 * ���������� : �������� �ý��� REST API Service - ���������� �⺻ ����ü
 */
package com.mosom.common.standalone.restful.handler;

import com.mosom.common.standalone.ILifecycleOption;
import com.mosom.common.standalone.LifecycleException;
import com.mosom.common.standalone.restful.RequestStructure;
import com.mosom.common.standalone.restful.ResponseModel;
import org.apache.log4j.Logger;

import static com.dmdlogging.DmdLog.getLog;
import static com.mosom.common.standalone.IOperationalOption.CLEAR_AT_START;
import static com.mosom.common.standalone.IScheduleOption.*;
import static com.mosom.common.standalone.OptionGenerator.operational;
import static com.mosom.common.standalone.OptionGenerator.schedule;
import static com.mosom.consparam.LogCons.MOSOM_COMMON;

abstract public class BaseProcessHandler implements IProcessHandler<ResponseModel> {

    protected ResponseModel model;

    protected void setResponseModel(ResponseModel model) {
        this.model = model;
    }

    protected Logger log() {
        return getLog(MOSOM_COMMON);
    }

    protected ILifecycleOption generateOption(RequestStructure request) throws LifecycleException {
        String clearAtStart = null;

        if (request.isParameterContainsKey(CLEAR_AT_START)) {
            clearAtStart = request.get(CLEAR_AT_START);
        }

        if (request.isGroupParameterContainsKey(TIME_SCALE, INITIAL_DELAY, SCHEDULE_DELAY)) {
            String timeScale = request.get(TIME_SCALE);
            String initialDelay = request.get(INITIAL_DELAY);
            String scheduleDelay = request.get(SCHEDULE_DELAY);

            return schedule(clearAtStart, timeScale, initialDelay, scheduleDelay);
        }

        return operational(clearAtStart);
    }

}
