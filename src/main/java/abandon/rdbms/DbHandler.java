package abandon.rdbms;

import java.io.IOException;
import java.sql.*;

public class DbHandler {

    @FunctionalInterface
    public interface Traversing{
        void traverse(final ResultSet resultSet) throws SQLException, IOException;
    }

    private Connection connection;



    public void open() throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.jdbc.Driver");
        connection = DriverManager.getConnection("jdbc:mysql://localhost/EAN?user=root&password=Bora1234");
    }

    public  void close() throws SQLException {
        if (connection!=null)
            connection.close();
    }


    public void executeSqlScript(String sqlSript, Traversing traversing) throws SQLException, IOException {
        PreparedStatement statement = connection.prepareStatement(sqlSript);
        ResultSet resultSet =  statement.executeQuery();
        while(resultSet.next()) {
            traversing.traverse(resultSet);
        }
    }

}
