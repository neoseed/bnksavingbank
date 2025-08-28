/*
 * 프로그램명 : DataSourceTxProvider
 * 설　계　자 : Thomas Parker(임예준) - (2023.04.13)
 * 작　성　자 : Thomas Parker(임예준) - (2023.04.13)
 * 적　　　요 : Transactional DataSource Provider
 */
package com.mosom.common.standalone;

import com.cv.jff.common.util.EJBUtil;
import com.mosom.consparam.ProgCons;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class DataSourceTxProvider extends DataSourceProvider {

    @Override
    protected DataSource getDataSource() {
        try {
            return (DataSource) EJBUtil.lookupLocalObject(ProgCons.DB_TX_CONNECTION);
        } catch (Exception e) {
            try {
                return (DataSource) EJBUtil.lookupLocalObject(ProgCons.DB_TX_CONNECTION + "_TEMP");
            } catch (Exception unrecoverable) {
                throw new RuntimeException(unrecoverable);
            }
        }
    }

    @Override
    protected Connection createConnection() {
        try {
            Connection con = getDataSource().getConnection();
            con.setAutoCommit(false);

            if (con.getTransactionIsolation() == Connection.TRANSACTION_NONE) {
                con.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
            }

            return con;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Connection getConnection() {
        return createConnection();
    }

    public void commit(Connection con) {
        try {
            if (con != null) {
                con.commit();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void rollback(Connection con) {
        try {
            if (con != null) {
                con.rollback();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
