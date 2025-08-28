/*
 * ���α׷��� : CodeHistory
 * �����衡�� : Thomas Parker(�ӿ���) - (2023.04.17)
 * �ۡ������� : Thomas Parker(�ӿ���) - (2023.04.17)
 * ���������� : ���� Cache ������ History Model - Code
 */
package com.mosom.common.standalone.cache.data;

import com.mosom.common.standalone.DatabaseCommands;

import java.math.BigInteger;

public class CodeHistory extends DataHistoryModel<ImmutableCode> {

    public CodeHistory(BigInteger timestamp, DatabaseCommands command, ImmutableCode code) {
        super(timestamp, command, code);
    }

}
