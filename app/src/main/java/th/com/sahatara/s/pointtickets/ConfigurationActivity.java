package th.com.sahatara.s.pointtickets;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.sql.Connection;

import th.com.sahatara.s.pointtickets.utility.ConfigurationModel;
import th.com.sahatara.s.pointtickets.utility.ConnectionHelper;
import th.com.sahatara.s.pointtickets.utility.DBHelper;


public class ConfigurationActivity extends AppCompatActivity {

    private EditText ipEditText,dbNameEditText, usernameEditText, passwordEditText;
    private Button testConnectButton, saveConfButton;
    private DBHelper dbHelper;
    private ConfigurationModel configurationModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuration);

        //Bind Widget
        bindWidget();

        //Get Configuration
        getConfiguration();

        //SaveButton Controller
        SaveButtonController();

        //Connection Controller
        testConnectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConnectionHelper connectionHelper = new ConnectionHelper();
                Connection connection = connectionHelper.connection(ConfigurationActivity.this);
                if (connection == null) {
                    Toast.makeText(ConfigurationActivity.this, getString(R.string.conn_err1), Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(ConfigurationActivity.this, getString(R.string.connected), Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    private void SaveButtonController() {
        saveConfButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbHelper = new DBHelper(ConfigurationActivity.this);
                configurationModel = new ConfigurationModel(ipEditText.getText().toString(), usernameEditText.getText().toString(), passwordEditText.getText().toString(), dbNameEditText.getText().toString());
                dbHelper.addConfiguration(configurationModel);


                Toast.makeText(ConfigurationActivity.this, getString(R.string.save_success), Toast.LENGTH_LONG).show();

            }
        });
    }

    private void getConfiguration() {
        dbHelper = new DBHelper(this);
        configurationModel = dbHelper.getConfigurationDetail();
        if (configurationModel != null) {
            ipEditText.setText(configurationModel.getIp());
            dbNameEditText.setText(configurationModel.getDbname());
            usernameEditText.setText(configurationModel.getUsername());
            passwordEditText.setText(configurationModel.getPassword());
        }
    }

    private void bindWidget() {
        ipEditText = findViewById(R.id.edtIP);
        dbNameEditText = findViewById(R.id.edtDBName);
        usernameEditText = findViewById(R.id.edtUsername);
        passwordEditText = findViewById(R.id.edtPassword);
        saveConfButton = findViewById(R.id.btn_save_conf);
        testConnectButton = findViewById(R.id.btn_test_connect);
    }
}
