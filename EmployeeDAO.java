import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EmployeeDAO {

    /**
     * Fetches all unique department labels currently present in your XAMPP instance.
     */
    public List<String> getAllDepartments() throws SQLException {
        List<String> depts = new ArrayList<>();
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) return depts;

        String query = "SELECT Dname FROM DEPARTMENT ORDER BY Dname;";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                depts.add(rs.getString("Dname"));
            }
        }
        return depts;
    }

    /**
     * Fetches all unique project tracking titles from your XAMPP instance.
     */
    public List<String> getAllProjects() throws SQLException {
        List<String> projs = new ArrayList<>();
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) return projs;

        String query = "SELECT Pname FROM PROJECT ORDER BY Pname;";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                projs.add(rs.getString("Pname"));
            }
        }
        return projs;
    }

    /**
     * Dynamically builds the multi-selection query logic, supporting exclusion flags.
     */
    public List<String> searchEmployees(List<String> depts, List<String> projs, boolean notDept, boolean notProj) throws SQLException {
        List<String> employees = new ArrayList<>();
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) return employees;

        StringBuilder sql = new StringBuilder("SELECT DISTINCT e.Fname, e.Lname FROM EMPLOYEE e ");
        sql.append("LEFT JOIN DEPARTMENT d ON e.Dno = d.Dnumber WHERE 1=1 ");

        // Build runtime parameters for Department lookups
        if (!depts.isEmpty()) {
            sql.append(" AND d.Dname ");
            if (notDept) sql.append("NOT ");
            sql.append("IN (");
            for (int i = 0; i < depts.size(); i++) {
                sql.append("?").append(i == depts.size() - 1 ? "" : ",");
            }
            sql.append(")");
        }

        // Build runtime subqueries for Project lookups to preserve correct mathematical sets
        if (!projs.isEmpty()) {
            if (notProj) {
                sql.append(" AND e.Ssn NOT IN (");
                sql.append("SELECT w.Essn FROM WORKS_ON w ");
                sql.append("JOIN PROJECT p ON w.Pno = p.Pnumber WHERE p.Pname IN (");
                for (int i = 0; i < projs.size(); i++) {
                    sql.append("?").append(i == projs.size() - 1 ? "" : ",");
                }
                sql.append("))");
            } else {
                sql.append(" AND e.Ssn IN (");
                sql.append("SELECT w.Essn FROM WORKS_ON w ");
                sql.append("JOIN PROJECT p ON w.Pno = p.Pnumber WHERE p.Pname IN (");
                for (int i = 0; i < projs.size(); i++) {
                    sql.append("?").append(i == projs.size() - 1 ? "" : ",");
                }
                sql.append("))");
            }
        }

        sql.append(" ORDER BY e.Lname, e.Fname;");

        // Map data arrays securely to prevent malicious payloads
        try (PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {
            int paramIndex = 1;
            
            for (String dName : depts) {
                pstmt.setString(paramIndex++, dName);
            }
            for (String pName : projs) {
                pstmt.setString(paramIndex++, pName);
            }

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    employees.add(rs.getString("Fname") + " " + rs.getString("Lname"));
                }
            }
        }
        return employees;
    }
}
