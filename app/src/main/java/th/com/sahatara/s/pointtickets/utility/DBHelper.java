package th.com.sahatara.s.pointtickets.utility;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper {

    SQLiteDatabase sqLiteDatabase;
    String TAG = "DBHelper";

    public DBHelper(Context context) {
        super(context, ConfigurationModel.DATABASE_NAME, null, ConfigurationModel.DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONFIGURATION_TABLE = String.format("CREATE TABLE %s ( %s TEXT, %s TEXT, %s TEXT, %s TEXT )", ConfigurationModel.TABLE, ConfigurationModel.Column.ip, ConfigurationModel.Column.dbname, ConfigurationModel.Column.username, ConfigurationModel.Column.password);
        db.execSQL(CREATE_CONFIGURATION_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String DROP_CONFIGURATION_TABLE = String.format("DROP TABLE IF EXISTS %s",ConfigurationModel.TABLE);
        db.execSQL(DROP_CONFIGURATION_TABLE);
        onCreate(db);
    }

    public ConfigurationModel getConfigurationDetail(){
        sqLiteDatabase = this.getWritableDatabase();

        ConfigurationModel configurationModel = new ConfigurationModel();

        Cursor cursor = sqLiteDatabase.query(ConfigurationModel.TABLE, null, null, null, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();
        }
        if (cursor.getCount() > 0) {
            configurationModel.setIp(cursor.getString(0));
            configurationModel.setDbname(cursor.getString(1));
            configurationModel.setUsername(cursor.getString(2));
            configurationModel.setPassword(cursor.getString(3));

            sqLiteDatabase.close();
            return configurationModel;
        } else {

            Log.i(TAG, "getConfigurationDetail: Not Have Data");
            sqLiteDatabase.close();
            return null;
        }




    }

    public void addConfiguration(ConfigurationModel configurationModel) {
        sqLiteDatabase = this.getWritableDatabase();

        Cursor cursor = sqLiteDatabase.query(ConfigurationModel.TABLE, null, null, null, null, null, null);
        if (cursor != null) {
            sqLiteDatabase.delete(ConfigurationModel.TABLE, "1", null);
        }

        ContentValues contentValues = new ContentValues();

        contentValues.put(ConfigurationModel.Column.ip, configurationModel.getIp());
        contentValues.put(ConfigurationModel.Column.dbname, configurationModel.getDbname());
        contentValues.put(ConfigurationModel.Column.username, configurationModel.getUsername());
        contentValues.put(ConfigurationModel.Column.password, configurationModel.getPassword());

        sqLiteDatabase.insert(ConfigurationModel.TABLE, null, contentValues);

        sqLiteDatabase.close();
    }

}
