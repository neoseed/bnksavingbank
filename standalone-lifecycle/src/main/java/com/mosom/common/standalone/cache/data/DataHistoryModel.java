/*
 * ���α׷��� : DataHistoryModel
 * �����衡�� : Thomas Parker(�ӿ���) - (2023.04.17)
 * �ۡ������� : Thomas Parker(�ӿ���) - (2023.04.17)
 * ���������� : ���� Cache ������ History Model
 */
package com.mosom.common.standalone.cache.data;

import com.mosom.common.standalone.DatabaseCommands;

import java.math.BigInteger;

public class DataHistoryModel<T> {

    private final BigInteger timestamp;

    private final DatabaseCommands command;

    private final T model;

    public DataHistoryModel(BigInteger timestamp, DatabaseCommands command, T model) {
        this.timestamp = timestamp;
        this.command = command;
        this.model = model;
    }

    public BigInteger getTimestamp() {
        return timestamp;
    }

    public DatabaseCommands getCommand() {
        return command;
    }

    public T getModel() {
        return model;
    }

}
