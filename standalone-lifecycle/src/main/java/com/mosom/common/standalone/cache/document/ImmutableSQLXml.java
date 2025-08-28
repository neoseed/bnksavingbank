/*
 * ���α׷��� : ImmutableSQLXml
 * �����衡�� : Thomas Parker(�ӿ���) - (2023.04.17)
 * �ۡ������� : Thomas Parker(�ӿ���) - (2023.04.17)
 * ���������� : ���� Cache ������ Model Interface
 */
package com.mosom.common.standalone.cache.document;

import com.mosom.common.standalone.cache.ImmutableModel;

public interface ImmutableSQLXml extends ImmutableModel {

    String getGroup();

    String getName();

    String getComments();

    String getProgramCode();

    String getStatement();

    long getTime();

}
