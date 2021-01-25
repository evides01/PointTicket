package th.com.sahatara.s.pointtickets.utility;

import android.content.Context;
import android.os.StrictMode;
import android.util.Log;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import th.com.sahatara.s.pointtickets.R;


public class ConnectionHelper {
    String TAG = this.getClass().getSimpleName();
    DBHelper dbHelper;
    ConfigurationModel configurationModel;
    public Connection connection(Context context) {
        dbHelper = new DBHelper(context);
        configurationModel = dbHelper.getConfigurationDetail();
        StrictMode.ThreadPolicy threadPolicy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(threadPolicy);
        Connection connection = null;
        String connectionURL = null;


        try {
            Class.forName("net.sourceforge.jtds.jdbc.Driver");
            connectionURL = "jdbc:jtds:sqlserver://" + configurationModel.getIp() + ";databaseName=" + configurationModel.getDbname() + ";user=" + configurationModel.getUsername() + ";password=" + configurationModel.getPassword() + ";";
            connection = DriverManager.getConnection(connectionURL);
        } catch (SQLException se) {
            Log.e("error here 1 : ", se.getMessage());
            Toast.makeText(context, context.getResources().getString(R.string.pls_check_conn), Toast.LENGTH_LONG).show();
            return null;
        } catch (ClassNotFoundException e) {
            Log.e("error here 2 : ", e.getMessage());
            Toast.makeText(context, context.getResources().getString(R.string.pls_check_conn), Toast.LENGTH_LONG).show();
            return null;
        } catch (Exception e) {
            Log.e("error here 3 : ", e.getMessage());
            Toast.makeText(context, context.getResources().getString(R.string.pls_check_conn), Toast.LENGTH_LONG).show();
            return null;
        }

        return connection;
    }
}
