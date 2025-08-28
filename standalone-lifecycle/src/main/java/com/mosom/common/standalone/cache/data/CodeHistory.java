/*
 * 프로그램명 : CodeHistory
 * 설　계　자 : Thomas Parker(임예준) - (2023.04.17)
 * 작　성　자 : Thomas Parker(임예준) - (2023.04.17)
 * 적　　　요 : 독립 Cache 서비스의 History Model - Code
 */
package com.mosom.common.standalone.cache.data;

import com.mosom.common.standalone.DatabaseCommands;

import java.math.BigInteger;

public class CodeHistory extends DataHistoryModel<ImmutableCode> {

    public CodeHistory(BigInteger timestamp, DatabaseCommands command, ImmutableCode code) {
        super(timestamp, command, code);
    }

}
