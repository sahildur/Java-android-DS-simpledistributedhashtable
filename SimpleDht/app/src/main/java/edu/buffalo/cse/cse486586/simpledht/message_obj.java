package edu.buffalo.cse.cse486586.simpledht;

import android.database.Cursor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by sahil on 2/27/16.
 */
public class message_obj implements Serializable {

    int unique_no = 0;
    String origin_port = "";
    String status = "";
    String key = "";
    String value = "";
    String message = "";
    HashMap<String, String> resultquery = new HashMap<String, String>();
    ArrayList<String> globalactive = new ArrayList<String>();
    ArrayList<String> globalactivewiithouthash = new ArrayList<String>();

    public message_obj(String org, String keyy, String msg, String stat) {
        origin_port = org;
        key = keyy;
        value = msg;
        status = stat;
    }

}
