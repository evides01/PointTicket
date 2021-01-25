package th.com.sahatara.s.pointtickets.utility;

public class ConfigurationModel {
    public static final String DATABASE_NAME = "dbpointtickets.db";
    public static final int DATABASE_VERSION = 1;
    public static final String TABLE = "tm_conf";
    private String ip;
    private String username;
    private String password;
    private String dbname;

    public ConfigurationModel() {
    }

    public ConfigurationModel(String ip, String username, String password, String dbname) {
        this.ip = ip;
        this.username = username;
        this.password = password;
        this.dbname = dbname;
    }

    public class Column {
        public static final String ip = "IP";
        public static final String username = "Username";
        public static final String password = "Password";
        public static final String dbname = "DBName";
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDbname() {
        return dbname;
    }

    public void setDbname(String dbname) {
        this.dbname = dbname;
    }
}
