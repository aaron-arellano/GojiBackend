package goji.data;

/** Class representation of the json db config
 *
 *  @author Aaron
 *  @version 2020.10.26
 */
public class DatabaseConfigWrapper {

    private String username;
    private String password;
    private String mysqlUrl;
    private String jdbcDriver;
    private String databaseName;

    @SuppressWarnings("javadoc")
    public String getUsername() {
        return username;
    }

    @SuppressWarnings("javadoc")
    public void setUsername(String username) {
        this.username = username;
    }

    @SuppressWarnings("javadoc")
    public String getPassword() {
        return password;
    }

    @SuppressWarnings("javadoc")
    public void setPassword(String password) {
        this.password = password;
    }

    @SuppressWarnings("javadoc")
    public String getMysqlUrl() {
        return mysqlUrl;
    }

    @SuppressWarnings("javadoc")
    public void setMysqlUrl(String mysqlUrl) {
        this.mysqlUrl = mysqlUrl;
    }

    @SuppressWarnings("javadoc")
    public String getJdbcDriver() {
        return jdbcDriver;
    }

    @SuppressWarnings("javadoc")
    public void setJdbcDriver(String jdbcDriver) {
        this.jdbcDriver = jdbcDriver;
    }

    @SuppressWarnings("javadoc")
    public String getDatabaseName() {
        return databaseName;
    }

    @SuppressWarnings("javadoc")
    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

}
