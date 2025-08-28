/*
 * ���α׷��� : ISelfManageable
 * �����衡�� : Thomas Parker(�ӿ���) - (2024.12.11)
 * �ۡ������� : Thomas Parker(�ӿ���) - (2024.12.11)
 * ���������� : ���� Cache ���� ��ü ������ ���� Interface
 */
package com.mosom.common.standalone.cache;

public interface ISelfManageable<T> {

    void storage(T key);

    boolean isStorage(T key);

    void unstorage(T key);

}
