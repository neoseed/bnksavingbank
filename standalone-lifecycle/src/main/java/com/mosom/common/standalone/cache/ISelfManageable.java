/*
 * 프로그램명 : ISelfManageable
 * 설　계　자 : Thomas Parker(임예준) - (2024.12.11)
 * 작　성　자 : Thomas Parker(임예준) - (2024.12.11)
 * 적　　　요 : 독립 Cache 서비스 자체 관리를 위한 Interface
 */
package com.mosom.common.standalone.cache;

public interface ISelfManageable<T> {

    void storage(T key);

    boolean isStorage(T key);

    void unstorage(T key);

}
