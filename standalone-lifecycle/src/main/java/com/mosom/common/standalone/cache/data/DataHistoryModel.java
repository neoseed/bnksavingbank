/*
 * 프로그램명 : DataHistoryModel
 * 설　계　자 : Thomas Parker(임예준) - (2023.04.17)
 * 작　성　자 : Thomas Parker(임예준) - (2023.04.17)
 * 적　　　요 : 독립 Cache 서비스의 History Model
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
