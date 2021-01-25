package th.com.sahatara.s.pointtickets;

import android.content.Context;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import th.com.sahatara.s.pointtickets.utility.ConnectionHelper;

public class GetData {
    Connection connection;
    String connectionResult = "";
    Boolean isSuccessABoolean = false;
    String query,op;
    Context context;

    public GetData(String query, String op, Context context) {
        this.query = query;
        this.op = op;
        this.context = context;
    }

    public List<Map<String,String>> doInBackground() {
        List<Map<String, String>> dataMapsList = null;

        dataMapsList = new ArrayList<Map<String, String>>();

        try {
            ConnectionHelper connectionHelper = new ConnectionHelper();
            connection = connectionHelper.connection(context);
            if (connection == null) {
                connectionResult = context.getResources().getString(R.string.conn_err1);
            } else {
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(query);
                Map<String, String> resultMap;
                if (op.equals("Detail")) {
                    while (resultSet.next()) {
                        resultMap = new HashMap<String, String>();
                        resultMap.put("BARCODE", resultSet.getString("BARCODE"));
                        resultMap.put("CLAMOUNT", resultSet.getString("CLAMOUNT"));
                        resultMap.put("tripname", resultSet.getString("tripname"));
                        resultMap.put("typename", resultSet.getString("typename"));
                        dataMapsList.add(resultMap);
                    }
                } else if (op.equals("Stations")) {
                    while (resultSet.next()) {
                        resultMap = new HashMap<String, String>();
                        resultMap.put("stname", resultSet.getString("stname"));
                        resultMap.put("stno", resultSet.getString("stno"));
                        dataMapsList.add(resultMap);

                    }
                }


                connectionResult = context.getResources().getString(R.string.conn_success);
                isSuccessABoolean=true;
                connection.close();

            }

        } catch (SQLException e) {
            e.printStackTrace();
            isSuccessABoolean = false;
        }

        return dataMapsList;
    }
}
