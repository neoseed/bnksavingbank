/*
 * 프로그램명 : DataSourceProvider
 * 설　계　자 : Thomas Parker(임예준) - (2023.04.13)
 * 작　성　자 : Thomas Parker(임예준) - (2023.04.13)
 * 적　　　요 : Non Transaction DataSource Provider
 */
package com.mosom.common.standalone;

import com.cv.jff.common.util.EJBUtil;
import com.mosom.consparam.ProgCons;

import javax.sql.DataSource;
import java.sql.*;

public class DataSourceProvider {

    private Connection con;

    protected DataSource getDataSource() {
        try {
            return (DataSource) EJBUtil.lookupLocalObject(ProgCons.DB_CONNECTION);
        } catch (Exception e) {
            try {
                return (DataSource) EJBUtil.lookupLocalObject(ProgCons.DB_CONNECTION + "_TEMP");
            } catch (Exception unexpected) {
                throw new RuntimeException(unexpected);
            }
        }
    }

    protected Connection createConnection() {
        try {
            Connection con = getDataSource().getConnection();
            con.setAutoCommit(true);

            return con;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void maintainConnection() {
        try {
            if (con == null || con.isClosed()) {
                con = createConnection();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Connection getConnection(boolean isMaintain) {
        if (isMaintain) {
            maintainConnection();
            return con;
        }

        return createConnection();
    }

    public void close(ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void close(Statement stmt) {
        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void close(PreparedStatement pstmt) {
        if (pstmt != null) {
            try {
                pstmt.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void close(Connection con) {
        try {
            if (con != null) {
                con.close();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
