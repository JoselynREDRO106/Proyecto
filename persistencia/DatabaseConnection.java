package persistencia;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public final class DatabaseConnection {
    private static final String DEFAULT_HOST = "db.gjqrqfxgzewbrhovwtyi.supabase.co";
    private static final String DEFAULT_PORT = "5432";
    private static final String DEFAULT_DB = "postgres";

    private DatabaseConnection() {
    }

    public static Connection getConnection() throws SQLException {
        String host = env("SUPABASE_DB_HOST", DEFAULT_HOST);
        String port = env("SUPABASE_DB_PORT", DEFAULT_PORT);
        String database = env("SUPABASE_DB_NAME", DEFAULT_DB);
        String user = env("SUPABASE_DB_USER", "postgres");
        String password = env("SUPABASE_DB_PASSWORD", "");

        String url = "jdbc:postgresql://" + host + ":" + port + "/" + database + "?sslmode=require";
        Properties properties = new Properties();
        properties.setProperty("user", user);
        properties.setProperty("password", password);
        return DriverManager.getConnection(url, properties);
    }

    private static String env(String name, String fallback) {
        String value = System.getenv(name);
        return value == null || value.isBlank() ? fallback : value;
    }
}
