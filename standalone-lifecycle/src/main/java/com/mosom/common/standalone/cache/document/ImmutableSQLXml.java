/*
 * 프로그램명 : ImmutableSQLXml
 * 설　계　자 : Thomas Parker(임예준) - (2023.04.17)
 * 작　성　자 : Thomas Parker(임예준) - (2023.04.17)
 * 적　　　요 : 독립 Cache 서비스의 Model Interface
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
