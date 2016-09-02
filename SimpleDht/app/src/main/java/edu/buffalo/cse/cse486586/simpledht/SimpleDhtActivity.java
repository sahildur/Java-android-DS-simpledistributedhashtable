package edu.buffalo.cse.cse486586.simpledht;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.telephony.TelephonyManager;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;

public class SimpleDhtActivity extends Activity {

    static String myPort;

    private static final String KEY_FIELD = "key";
    private static final String VALUE_FIELD = "value";
    private final Uri mUri = buildUri("content", "edu.buffalo.cse.cse486586.simpledht.provider");

    private Uri buildUri(String scheme, String authority) {
        Uri.Builder uriBuilder = new Uri.Builder();
        uriBuilder.authority(authority);
        uriBuilder.scheme(scheme);
        return uriBuilder.build();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.v("here101", "here101");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple_dht_main);

        TextView tv = (TextView) findViewById(R.id.textView1);
        tv.setMovementMethod(new ScrollingMovementMethod());
        TelephonyManager tel = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        String portStr = tel.getLine1Number().substring(tel.getLine1Number().length() - 4);
        myPort = String.valueOf((Integer.parseInt(portStr) * 2));
        Log.v("Myportno on create", myPort);

        findViewById(R.id.button3).setOnClickListener(
                new OnTestClickListener(tv, getContentResolver()));

        findViewById(R.id.button1).setOnClickListener(
                new OnButtonOneClickListener(tv, getContentResolver()));

        //test code commented
//        ContentValues cvtest = new ContentValues();
//
//
//        cvtest.put(KEY_FIELD, "key" + Integer.toString(1));
//        cvtest.put(VALUE_FIELD, "val" + Integer.toString(5));
//
//        getContentResolver().insert(mUri, cvtest);
//insert second val
//        cvtest.put(KEY_FIELD, "key" + Integer.toString(2));
//        cvtest.put(VALUE_FIELD, "val" + Integer.toString(6));
//
//        getContentResolver().insert(mUri, cvtest);
//
////delete working fine
//        getContentResolver().delete(mUri, "key2", null);
//        //querytest
//        try {
//            Cursor resultCursor = getContentResolver().query(mUri, null,
//                    "key2", null, null);
//            if (resultCursor == null) {
//                Log.e("TAG1", "Result null");
//                throw new Exception();
//            }
//
//            int keyIndex = resultCursor.getColumnIndex(KEY_FIELD);
//            int valueIndex = resultCursor.getColumnIndex(VALUE_FIELD);
//
//            Log.v("keyIndex"," "+keyIndex);
//            Log.v("valueIndex"," "+valueIndex);
//
//
//        }
//        catch(Exception e)
//        {
//            Log.v("INexception1","yo");
//        }
//
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_simple_dht_main, menu);
        return true;
    }

}
