package th.com.sahatara.s.pointtickets;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.Map;

import device.common.DecodeResult;
import device.common.ScanConst;
import device.sdk.ScanManager;
import th.com.sahatara.s.pointtickets.utility.ConfigurationModel;
import th.com.sahatara.s.pointtickets.utility.DBHelper;

public class MainActivity extends AppCompatActivity {

    private String TAG = this.getClass().getSimpleName();

    private static ScanManager mScanner;
    private static DecodeResult mDecodeResult;
    private static TextView qrResultTextView, stationResultTextView, programResultTextView, typeResultTextView, amountResultTextView;
    private static LinearLayout splashLinearLayout, detailLinearLayout;
    private DBHelper dbHelper;
    private ConfigurationModel configurationModel;
    private Button scanButton;
    private AlertDialog mDialog = null;
    private int mBackupResultType = ScanConst.ResultType.DCD_RESULT_COPYPASTE;
    private Boolean doubleBackToExitPressedOnce = false;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.scan_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent intent = new Intent(MainActivity.this, ConfigurationActivity.class);
            startActivity(intent);


            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, getResources().getString(R.string.press_back), Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }

    public static class ScanResultReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (mScanner != null) {
                if (splashLinearLayout.getVisibility() == View.VISIBLE) {
                    splashLinearLayout.setVisibility(View.GONE);
                }
                detailLinearLayout.setVisibility(View.VISIBLE);
                if (ScanConst.INTENT_USERMSG.equals(intent.getAction())) {

                    mScanner.aDecodeGetResult(mDecodeResult.recycle());
                    //Query Get Ticket Detail
                    String query1 = "select t.BARCODE, t.CLAMOUNT, ts.tripname, tt.typename from Tickets t inner join trips ts on ts.tripid = t.TripsId inner join typetourist tt on tt.typetouristid = t.TypeTourId  where t.BARCODE like '" + mDecodeResult.toString() + "%'";
                    //Query Get Ticket Station Detail
                    String query2 = "select stno,stname from tripdetail1 td1 inner join TripStations ts on ts.stid = td1.stid where td1.tripid = ( select top 1 TripsId from Tickets where BARCODE like '" + mDecodeResult.toString() + "%') order by td1.stid";

                    List<Map<String, String>> data = null;
                    GetData getData;

                    getData = new GetData(query1, "Detail",context);
                    data = getData.doInBackground();
                    if (data.size() > 0) {
                        typeResultTextView.setText(data.get(0).get("typename"));
                        qrResultTextView.setText(data.get(0).get("BARCODE"));
                        amountResultTextView.setText(data.get(0).get("CLAMOUNT"));
                        programResultTextView.setText(data.get(0).get("tripname"));
                    }

                    getData = new GetData(query2, "Stations",context);
                    data = getData.doInBackground();
                    String stationString = "";
                    for (int i = 0; i < data.size(); i++) {
                        stationString += data.get(i).get("stno");
                        if (i != data.size() - 1) {
                            stationString += ",";
                        }
                    }

                    stationResultTextView.setText(stationString);


                } else if (ScanConst.INTENT_EVENT.equals(intent.getAction())) {
                    boolean result = intent.getBooleanExtra(ScanConst.EXTRA_EVENT_DECODE_RESULT, false);
                    int decodeBytesLength = intent.getIntExtra(ScanConst.EXTRA_EVENT_DECODE_LENGTH, 0);
                    byte[] decodeBytesValue = intent.getByteArrayExtra(ScanConst.EXTRA_EVENT_DECODE_VALUE);
                    String decodeValue = new String(decodeBytesValue, 0, decodeBytesLength);
                    int decodeLength = decodeValue.length();
                    String symbolName = intent.getStringExtra(ScanConst.EXTRA_EVENT_SYMBOL_NAME);
                    byte symbolId = intent.getByteExtra(ScanConst.EXTRA_EVENT_SYMBOL_ID, (byte) 0);
                    int symbolType = intent.getIntExtra(ScanConst.EXTRA_EVENT_SYMBOL_TYPE, 0);
                    byte letter = intent.getByteExtra(ScanConst.EXTRA_EVENT_DECODE_LETTER, (byte) 0);
                    byte modifier = intent.getByteExtra(ScanConst.EXTRA_EVENT_DECODE_MODIFIER, (byte) 0);
                    int decodingTime = intent.getIntExtra(ScanConst.EXTRA_EVENT_DECODE_TIME, 0);
//                    Log.i(TAG, "1. result: " + result);
//                    Log.i(TAG, "2. bytes length: " + decodeBytesLength);
//                    Log.i(TAG, "3. bytes value: " + decodeBytesValue);
//                    Log.i(TAG, "4. decoding length: " + decodeLength);
//                    Log.i(TAG, "5. decoding value: " + decodeValue);
//                    Log.i(TAG, "6. symbol name: " + symbolName);
//                    Log.i(TAG, "7. symbol id: " + symbolId);
//                    Log.i(TAG, "8. symbol type: " + symbolType);
//                    Log.i(TAG, "9. decoding letter: " + letter);
//                    Log.i(TAG, "10.decoding modifier: " + modifier);
//                    Log.i(TAG, "11.decoding time: " + decodingTime);

                    mScanner.aDecodeGetResult(mDecodeResult.recycle());

                    //Query Get Ticket Detail
                    String query1 = "select t.BARCODE, t.CLAMOUNT, ts.tripname, tt.typename from Tickets t inner join trips ts on ts.tripid = t.TripsId inner join typetourist tt on tt.typetouristid = t.TypeTourId  where t.BARCODE like '" + decodeValue + "'";
                    //Query Get Ticket Station Detail
                    String query2 = "select stno,stname from tripdetail1 td1 inner join TripStations ts on ts.stid = td1.stid where td1.tripid = ( select top 1 TripsId from Tickets where BARCODE like '" + decodeValue + "') order by td1.stid";

                    List<Map<String, String>> data = null;
                    GetData getData;

                    getData = new GetData(query1, "Detail",context);
                    data = getData.doInBackground();
                    typeResultTextView.setText(data.get(0).get("typename"));
                    qrResultTextView.setText(data.get(0).get("BARCODE"));
                    amountResultTextView.setText(data.get(0).get("CLAMOUNT"));
                    programResultTextView.setText(data.get(0).get("tripname"));

                    getData = new GetData(query2, "Stations",context);
                    data = getData.doInBackground();
                    String stationString = "";
                    for (int i = 0; i < data.size(); i++) {
                        stationString += data.get(i).get("stno");
                        if (i != data.size() - 1) {
                            stationString += ",";
                        }
                    }

                    stationResultTextView.setText(stationString);
                }
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Bind Widget
        bindWidget();

        //Scan Controller
        scanController();


        //Barcode Scanner Off
//        ((Button) findViewById(R.id.button_scan_off)).setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                if (mScanner != null) {
//                    if (mAutoScanOption.isChecked()) {
//                        mScanner.aDecodeSetTriggerMode(ScanConst.TriggerMode.DCD_TRIGGER_MODE_ONESHOT);
//                    }
//
//                    mScanner.aDecodeSetTriggerOn(0);
//
//                    if (mAutoScanOption.isChecked()) {
//                        mScanner.aDecodeSetTriggerMode(ScanConst.TriggerMode.DCD_TRIGGER_MODE_AUTO);
//                    }
//                }
//            }
//        });

    }




    private void scanController() {
        scanButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (mScanner != null) {
                    mScanner.aDecodeSetTriggerOn(1);
                }
            }
        });
    }

    private void bindWidget() {
        mScanner = new ScanManager();
        mDecodeResult = new DecodeResult();
        mScanner.aDecodeSetBeepEnable(1);
        typeResultTextView = findViewById(R.id.txtTypeResult);
        amountResultTextView = findViewById(R.id.txtAmtResult);
        programResultTextView = findViewById(R.id.txtProgramResult);
        qrResultTextView = findViewById(R.id.txtQRResult);
        stationResultTextView = findViewById(R.id.txtStationResult);
        detailLinearLayout = findViewById(R.id.linDetail);
        splashLinearLayout = findViewById(R.id.linSplash);
        scanButton = findViewById(R.id.btn_fsq_scan_qr);

        splashLinearLayout.setVisibility(View.VISIBLE);
        detailLinearLayout.setVisibility(View.GONE);
    }

    private void initScanner() {
        if (mScanner != null) {
            mBackupResultType = mScanner.aDecodeGetResultType();
            mScanner.aDecodeSetResultType(ScanConst.ResultType.DCD_RESULT_USERMSG);
            mScanner.aDecodeSetTriggerMode(ScanConst.TriggerMode.DCD_TRIGGER_MODE_ONESHOT);
            mScanner.aDecodeSetBeepEnable(1);
        }
    }

    private AlertDialog getEnableDialog() {
        if (mDialog == null) {
            AlertDialog dialog = new AlertDialog.Builder(this).create();
            dialog.setTitle(R.string.app_name);
            dialog.setMessage(getResources().getString(R.string.scanner_disable_alert));

            dialog.setButton(AlertDialog.BUTTON_NEGATIVE, getString(android.R.string.cancel),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    });
            dialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(android.R.string.ok),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(ScanConst.LAUNCH_SCAN_SETTING_ACITON);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            dialog.dismiss();
                        }
                    });
            dialog.setCancelable(false);
            mDialog = dialog;
        }
        return mDialog;
    }

    @Override
    protected void onResume() {
        if (mScanner.aDecodeGetDecodeEnable() == 1) {
            if (getEnableDialog().isShowing()) {
                getEnableDialog().dismiss();
            }
            initScanner();
        } else {
            if (!getEnableDialog().isShowing()) {
                getEnableDialog().show();
            }
        }

        super.onResume();
    }

    @Override
    protected void onPause() {
        if (mScanner != null) {
            mScanner.aDecodeSetResultType(mBackupResultType);
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if (mScanner != null) {
            mScanner.aDecodeSetResultType(mBackupResultType);
        }
        mScanner = null;
        super.onDestroy();
    }
}
