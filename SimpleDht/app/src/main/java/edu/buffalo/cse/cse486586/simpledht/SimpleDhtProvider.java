package edu.buffalo.cse.cse486586.simpledht;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.PriorityQueue;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.TextView;

public class SimpleDhtProvider extends ContentProvider {

    ArrayList<String> keysorginserted = new ArrayList<String>();
    static final int SERVER_PORT = 10000;
    static final String REMOTE_PORT0 = "11108";
    static final String REMOTE_PORT1 = "11112";
    static final String REMOTE_PORT2 = "11116";
    static final String REMOTE_PORT3 = "11120";
    static final String REMOTE_PORT4 = "11124";
    static int flagfirst = 0;
    public Uri mUri;

    ArrayList<String> porthashlish = new ArrayList<String>();

    ArrayList<String> activelist = new ArrayList<String>();
    ArrayList<String> activelistnonhash = new ArrayList<String>();
    HashMap<String, String> porthashwithorg = new HashMap<String, String>();

    MatrixCursor curglobal;
    MatrixCursor starcursor;

    static boolean waitforstar = false;
    static boolean waitingforservertask = false;
    static boolean waitforpreviousquery = false;
    static int waitforstarcount;

    static String myPort;
    static String myhash;
    static String myprehash;
    static String mysuccesshash;

    @Override

    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // TODO Auto-generated method stub

        try {
            keysorginserted.remove(selection);
            String hashedstring = genHash(selection);
            selection = hashedstring;
            Log.v("deletekeyhash", selection);
        } catch (NoSuchAlgorithmException e) {
            Log.v("exception5", "exception5");
        }

        getContext().deleteFile(selection);

        return 0;
    }

    @Override
    public String getType(Uri uri) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        // TODO Auto-generated method stub
        //return null;

        try {
            String vall = (String) values.get("value");
            Log.v("insertval", vall);
            //vall="55";
            String keyyyy = (String) values.get("key");
            Log.v("insertkey", keyyyy);

            String findkeyhash = "";
            try {
                findkeyhash = genHash(keyyyy);
            } catch (NoSuchAlgorithmException e) {
                Log.v("exception4", "exception4");
            }

            Log.v("findkeyhash", findkeyhash);

            ArrayList<String> porthashlishtesting = new ArrayList<String>();
            porthashlishtesting = activelist;
            porthashlishtesting.add(findkeyhash);

            Collections.sort(porthashlishtesting);
            Log.v("indexofkey", porthashlishtesting.indexOf(findkeyhash) + "");

            int flag = 0;
            if (activelist.size() == 1) {

            } else if (activelist.size() == 2) {

            } else {
                if (myhash.compareTo(myprehash) > 0) {
                    Log.v("ahaan1", "ahaan1");
                    if (findkeyhash.compareTo(myprehash) > 0 && findkeyhash.compareTo(myhash) <= 0) {
                        Log.v("ahaan2", "ahaan2");
                        flag = 1;
                    } else {
                        Log.v("ahaan3", "ahaan3");
                        new ClientTask().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, keyyyy, vall, "insert", "");
                        return uri;
                    }
                } else {
                    Log.v("ahaan4", "ahaan4");
                    if (findkeyhash.compareTo(myprehash) > 0 || findkeyhash.compareTo(myhash) <= 0) {
                        Log.v("ahaan5", "ahaan5");
                        flag = 1;
                    } else {
                        Log.v("ahaan6", "ahaan6");
                        new ClientTask().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, keyyyy, vall, "insert", "");
                        return uri;
                    }
                }
            }

            Log.v("ahaan7", "ahaan7");

            ////
            keysorginserted.add(keyyyy);

            try {
                String hashedstring = genHash(keyyyy);
                keyyyy = hashedstring;
                Log.v("insertkeyhash", keyyyy);
            } catch (NoSuchAlgorithmException e) {
                Log.v("exception4", "exception4");
            }

            String filename = keyyyy;//"SimpleMessengerOutput";
            //String string = strReceived + "\n";
            FileOutputStream outputStream;

            outputStream = getContext().openFileOutput(filename, Context.MODE_PRIVATE);
            outputStream.write(vall.getBytes());
            outputStream.close();
        } catch (Exception e) {
            Log.e("Failed", "File write failed");
        }

        return uri;
    }

    private Uri buildUri(String scheme, String authority) {
        Uri.Builder uriBuilder = new Uri.Builder();
        uriBuilder.authority(authority);
        uriBuilder.scheme(scheme);
        return uriBuilder.build();
    }

    @Override
    public boolean onCreate() {
        // TODO Auto-generated method stub

        TelephonyManager tel = (TelephonyManager) getContext().getSystemService(
                Context.TELEPHONY_SERVICE);
        String portStr = tel.getLine1Number().substring(tel.getLine1Number().length() - 4);
        myPort = String.valueOf((Integer.parseInt(portStr) * 2));

        Log.v("portStr_c", portStr);
        Log.v("myPort_c", myPort);

        try {

            ServerSocket serverSocket = new ServerSocket(SERVER_PORT);
            new ServerTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, serverSocket);
            //return true;
        } catch (IOException e) {
            Log.e("Create Server", "Can't create a ServerSocket");
            Log.e("Create Server", e.getMessage());
            //return false;
        }
        Log.v("flow5", "flow5");

        mUri = buildUri("content", "edu.buffalo.cse.cse486586.simpledht.provider");

        try {
            porthashwithorg.put(genHash("5554"), "5554");
            porthashwithorg.put(genHash("5556"), "5556");
            porthashwithorg.put(genHash("5558"), "5558");
            porthashwithorg.put(genHash("5560"), "5560");
            porthashwithorg.put(genHash("5562"), "5562");
            // porthashlish.add(genHash("key1"));

            myhash = genHash(portStr);
            myprehash = myhash;
            mysuccesshash = myhash;

            Log.v("myhash01", myhash);
            Log.v("myprehash01", myprehash);
            Log.v("mysuccesshash01", mysuccesshash);

        } catch (NoSuchAlgorithmException e) {
            Log.v("exception6", "exception7");
        }

        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
            String sortOrder) {
        // TODO Auto-generated method stub
        //return null;
        Log.v("queryselection", selection);

        String orgkey = selection;
        try {
            String hashedstring = genHash(selection);
            selection = hashedstring;
            Log.v("queryhash", selection);
        } catch (NoSuchAlgorithmException e) {
            Log.v("exception3", "exception3");
        }

//@//9a78211436f6d425ec38f5c4e02270801f3524f8
        //*//df58248c414f342c81e056b40bee12d17a08bf61
        ////all files list
        if (selection.equals("df58248c414f342c81e056b40bee12d17a08bf61")) {
            Log.v("hereiam1", "hereiam1");
            try {
                waitforstarcount = activelistnonhash.size();
                for (String starsend : activelistnonhash) {
                    Socket socketj = new Socket(InetAddress.getByAddress(new byte[]{10, 0, 2, 2}),
                            Integer.parseInt(starsend));

                    Log.v("myPortquery0j", "yae");
                    Log.v("myPortqueryj", this.myPort);
                    message_obj msg_obj = new message_obj(myPort, "", "", "querystar");

                    OutputStream outj = socketj.getOutputStream();
                    ObjectOutputStream ooutj = new ObjectOutputStream(outj);

                    ooutj.writeObject(msg_obj);

                    Log.v("out2", outj.toString());
                    /*
                     * TODO: Fill in your client code that sends out a message.
                     */

                    socketj.close();
                }
            } catch (Exception e) {
                Log.v("Exception22", "exception22");
            }
            waitforstar = true;

            Log.v("activelistnonhashsize", waitforstarcount + "");
            while (waitforstar) {

            }
            Log.v("starcursorreturn", "yes");
            return starcursor;

        }

        int star = 1;
        if (selection.equals("9a78211436f6d425ec38f5c4e02270801f3524f8")) {
            Log.v("allfilesname1", "hey");
            //all files in dir
//        File mydir = getContext().getFilesDir();
//        File lister = mydir.getAbsoluteFile();
            String[] s = new String[]{"key", "value"};
            MatrixCursor cur = new MatrixCursor(s);
            for (String list : keysorginserted) {
                //

                try {
                    String hashedstring = genHash(list);
                    selection = hashedstring;
                    Log.v("queryhash6", selection);
                } catch (NoSuchAlgorithmException e) {
                    Log.v("exception6", "exception3");
                }

                File file = new File(getContext().getFilesDir() + "/" + selection);

                Log.v("querycheck11", file.toString());

            //File file = "";
                InputStream in = null;
                try {
                    // in = new BufferedInputStream(new FileInputStream(file));
                    InputStreamReader isr = new InputStreamReader(new FileInputStream(file));
                    BufferedReader bufferedReader = new BufferedReader(isr);

                    String temp = bufferedReader.readLine();
                    Log.v("fileeadcheck11", temp);

                    cur.addRow(new String[]{list, temp});

                //finally {
                    //  if (in != null) {
                    //     in.close();
                    // }
                    //}
                } catch (Exception e) {
                    Log.v("query exception", "exception1");
                }

                ///
                Log.v("allfilesname", list);
            }
            Log.v("allfilesname2", "heya");
            return cur;

        }
        //global query dynamic

        if (!keysorginserted.contains(orgkey)) {
            try {

                while (waitforpreviousquery) {

                }
                waitforpreviousquery = true;

                String psuccesstosend = porthashwithorg.get(mysuccesshash);
                int tempi = Integer.parseInt(psuccesstosend);
                tempi = tempi * 2;
                String mysuccesshashport = "" + tempi;
                Socket socket = new Socket(InetAddress.getByAddress(new byte[]{10, 0, 2, 2}),
                        Integer.parseInt(mysuccesshashport));

                Log.v("myPortquery0", "yae");
                Log.v("myPortquery", this.myPort);
                message_obj msg_obj = new message_obj(myPort, orgkey, "", "query");

                OutputStream out = socket.getOutputStream();
                ObjectOutputStream oout = new ObjectOutputStream(out);

                oout.writeObject(msg_obj);

                Log.v("out2", out.toString());
                /*
                 * TODO: Fill in your client code that sends out a message.
                 */

                socket.close();
                Log.v("close12", "close12");
            } catch (Exception e) {
                Log.v("Exception12", "exception12");
            }

            waitingforservertask = true;
            while (waitingforservertask) {

            }
            waitforpreviousquery = false;

            return curglobal;

        }

//
        File file = new File(getContext().getFilesDir() + "/" + selection);

        Log.v("querycheck1", file.toString());

        //File file = "";
        InputStream in = null;
        try {
            // in = new BufferedInputStream(new FileInputStream(file));
            InputStreamReader isr = new InputStreamReader(new FileInputStream(file));
            BufferedReader bufferedReader = new BufferedReader(isr);

            String temp = bufferedReader.readLine();
            Log.v("fileeadcheck1", temp);
            String[] s = new String[]{"key", "value"};
            MatrixCursor cur = new MatrixCursor(s);
            cur.addRow(new String[]{orgkey, temp});
            return cur;
      } catch (Exception e) {
            Log.v("query exception", "exception1");
        }

        /*
         * TODO: You need to implement this method. Note that you need to return a Cursor object
         * with the right format. If the formatting is not correct, then it is not going to work.
         *
         * If you use SQLite, whatever is returned from SQLite is a Cursor object. However, you
         * still need to be careful because the formatting might still be incorrect.
         *
         * If you use a file storage option, then it is your job to build a Cursor * object. I
         * recommend building a MatrixCursor described at:
         * http://developer.android.com/reference/android/database/MatrixCursor.html
         */
        Log.v("query", selection);
        return null;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        // TODO Auto-generated method stub
        return 0;
    }

    private String genHash(String input) throws NoSuchAlgorithmException {
        MessageDigest sha1 = MessageDigest.getInstance("SHA-1");
        byte[] sha1Hash = sha1.digest(input.getBytes());
        Formatter formatter = new Formatter();
        for (byte b : sha1Hash) {
            formatter.format("%02x", b);
        }
        return formatter.toString();
    }

    class ServerTask extends AsyncTask<ServerSocket, String, Void> {

        private static final String TAG = "oye";

        //private final ContentResolver serverContentResolver=SimpleDhtActivity.getContentResolver();
        private int count = 0;

        @Override

        protected Void doInBackground(ServerSocket... sockets) {
            ServerSocket serverSocket = sockets[0];

            /*
             * TODO: Fill in your server code that receives messages and passes them
             * to onProgressUpdate().
             */
            Log.v("server", "Hiii");
            //publishProgress("msg");
            Socket socket = null;
            //count=-1;
            while (!serverSocket.isClosed()) {
                try {
                    //firsttime
                    if (flagfirst == 0) {
                        flagfirst = 1;
                        try {

                            int halft = Integer.parseInt(myPort) / 2;
                            activelist.add(genHash(halft + ""));
                            activelistnonhash.add(myPort);
                        } catch (NoSuchAlgorithmException e) {
                            Log.v("Exception19", "exception18");
                        }
                        if (!myPort.equals("11108")) {
        //inform 11108 that I have joined
                            //activelist.add(myPort);
                            message_obj msg = new message_obj(myPort, "", "", "joined");

                            ///
                            try {

                                Log.v("here57", "here57");
                                Socket socketf = new Socket(InetAddress.getByAddress(new byte[]{10, 0, 2, 2}),
                                        Integer.parseInt("11108"));
                                Log.v("here58", "here58");

                                OutputStream outf = socketf.getOutputStream();
                                ObjectOutputStream ooutf = new ObjectOutputStream(outf);

                                ooutf.writeObject(msg);

                                Log.v("outf17", outf.toString());
                                /*
                                 * TODO: Fill in your client code that sends out a message.
                                 */

                                socketf.close();
                            } catch (Exception e) {
                                Log.v("Exception 17", "couldnotinform");

                            }

                            ////
                        }

                    }

                    //
                    Log.v("BEFOREACCCEPT", "BEFOREACCCET");
                    socket = serverSocket.accept();
                    ObjectInputStream inp = new ObjectInputStream(socket.getInputStream());
                    //try {
                    message_obj msg_received = (message_obj) inp.readObject();

                    Log.v("msg_stat", msg_received.status);

                    if (msg_received.status.equals("querystarcomplete")) {
                        Log.v("qcompletereceived2", "yo");
                        Log.v("msg_received_org2", msg_received.origin_port + "");
                        if (waitforstarcount == activelistnonhash.size()) {
                            String[] sglo = new String[]{"key", "value"};
                            starcursor = new MatrixCursor(sglo);
                        }
                        waitforstarcount--;
                        for (String ststs : msg_received.resultquery.keySet()) {

                            Log.v("here27", "here27");
                            Log.v("qcompletekey", ststs + "");
                            Log.v("qcompleteval", msg_received.resultquery.get(ststs) + "");

                            starcursor.addRow(new String[]{ststs, msg_received.resultquery.get(ststs)});

                        }
                        Log.v("waitforstarcountans", "" + waitforstarcount);
                        if (waitforstarcount == 0) {
                            Log.v("waitforstarcount0", "waitforstarcount0");
                            waitforstar = false;
                        }

                    }

                    if (msg_received.status.equals("querystar")) {
                        try {
                            Log.v("yahanq1", "yahanq1");
                            Cursor resultCursor = getContext().getContentResolver().query(mUri, null, "@", null, null);
                            message_obj msgobj_n = new message_obj(myPort, "", "", "querystarcomplete");
                            while (resultCursor.moveToNext()) {
                                int keyIndex = resultCursor.getColumnIndex("key");
                                int valueIndex = resultCursor.getColumnIndex("value");
                                String listkeyy = resultCursor.getString(keyIndex);
                                String listvaly = resultCursor.getString(valueIndex);

                                msgobj_n.resultquery.put(listkeyy, listvaly);
                                Log.v("@@@@@listkey", listkeyy + "");
                                Log.v("@@@@@listval", listvaly + "");

                            }

                            //msg_received.status="querystarcomplete";
                            Log.v("yahanq22", "yahanq22");
                            Log.v("yahanq22org", msg_received.origin_port);
                            Socket sockett = new Socket(InetAddress.getByAddress(new byte[]{10, 0, 2, 2}),
                                    Integer.parseInt(msg_received.origin_port));

                            OutputStream outt = sockett.getOutputStream();
                            Log.v("yahanq33", "yahanq33");
                            ObjectOutputStream ooutt = new ObjectOutputStream(outt);

                            ooutt.writeObject(msgobj_n);

                            Log.v("out3", outt.toString());
                            /*
                             * TODO: Fill in your client code that sends out a message.
                             */

                            sockett.close();
                            Log.v("close1234", "close12");

                        } catch (Exception e) {
                            Log.v("Exception23", "exception23");
                        }
                    }

                    if (msg_received.status.equals("updatejoin")) {

                       //
                        activelist = msg_received.globalactive;
                        activelistnonhash = msg_received.globalactivewiithouthash;
                        ArrayList<String> freshactive = new ArrayList<String>();
                        for (String yup : activelist) {
                            freshactive.add(yup);
                        }

                        Collections.sort(freshactive);

                        for (String sts : freshactive) {
                            Log.v("sortedv", sts);
                        }

                        int index = freshactive.indexOf(myhash);
                        Log.v("myindex", index + "");
                        Log.v("myhash", myhash + "");

                        int lasttind = freshactive.size() - 1;
                        if (index == 0) {
                            myprehash = freshactive.get(lasttind);
                            mysuccesshash = freshactive.get(1);
                        } else if (index == lasttind) {
                            myprehash = freshactive.get(lasttind - 1);
                            mysuccesshash = freshactive.get(0);
                        } else {
                            myprehash = freshactive.get(index - 1);
                            mysuccesshash = freshactive.get(index + 1);
                        }

                        Log.v("myprehash", myprehash + "");
                        Log.v("mysuccesshash", mysuccesshash + "");

                        ////
                    }
                    Log.v("myPortagain", myPort);
                    if (msg_received.status.equals("joined") && myPort.equals("11108")) {

                        activelistnonhash.add(msg_received.origin_port);
                        Log.v("myPortagainenter", myPort);
                        try {

                            int temphalfport = Integer.parseInt(msg_received.origin_port) / 2;
                            activelist.add(genHash(temphalfport + ""));
                            Log.v("printactive", "yee");
                            for (String sa : activelist) {
                                Log.v("pprint1", sa);
                            }
                        } catch (NoSuchAlgorithmException e) {
                            Log.v("Exception 18", "exception 18");
                        }
                        for (String eachporta : activelistnonhash) {
                            Log.v("eachporta", eachporta);
                            Socket socketa = new Socket(InetAddress.getByAddress(new byte[]{10, 0, 2, 2}),
                                    Integer.parseInt(eachporta));

                            message_obj msg_obja = new message_obj(msg_received.origin_port, "", "", "updatejoin");
                            msg_obja.globalactive = activelist;
                            msg_obja.globalactivewiithouthash = activelistnonhash;

                            OutputStream outa = socketa.getOutputStream();
                            ObjectOutputStream oouta = new ObjectOutputStream(outa);

                            oouta.writeObject(msg_obja);

                            Log.v("out", outa.toString());
                            /*
                             * TODO: Fill in your client code that sends out a message.
                             */

                            socketa.close();

                        }
                    }

                    if (msg_received.status.equals("querycomplete")) {
                        Log.v("qcompletereceived", "yo");
                        Log.v("msg_received_org", msg_received.origin_port + "");
                        Log.v("msg_received_key", msg_received.key + "");
                        String[] sglo = new String[]{"key", "value"};
                        curglobal = new MatrixCursor(sglo);
                        for (String ststs : msg_received.resultquery.keySet()) {

                            Log.v("qcompletekey", ststs + "");
                            Log.v("qcompleteval", msg_received.resultquery.get(ststs) + "");

                            curglobal.addRow(new String[]{ststs, msg_received.resultquery.get(ststs)});

                        }
                        waitingforservertask = false;
                    }

                    if (msg_received.status.equals("insert")) {
                        ContentValues cvtest = new ContentValues();

                        cvtest.put("key", "" + msg_received.key);
                        cvtest.put("value", "" + msg_received.value);

                        getContext().getContentResolver().insert(mUri, cvtest);

                    } else if (msg_received.status.equals("query")) {
                        if (!keysorginserted.contains(msg_received.key)) {
                            try {
                                String psuccesstosend = porthashwithorg.get(mysuccesshash);
                                int tempi = Integer.parseInt(psuccesstosend);
                                tempi = tempi * 2;
                                String mysuccesshashport = "" + tempi;
                                Socket sockett = new Socket(InetAddress.getByAddress(new byte[]{10, 0, 2, 2}),
                                        Integer.parseInt(mysuccesshashport));

                                Log.v("oya22", "oya22");
                                Log.v("queryport", msg_received.origin_port);
                                message_obj msg_obj = new message_obj(msg_received.origin_port, msg_received.key, "", "query");

                                OutputStream outt = sockett.getOutputStream();
                                ObjectOutputStream ooutt = new ObjectOutputStream(outt);

                                ooutt.writeObject(msg_obj);

                                Log.v("out", outt.toString());
                                /*
                                 * TODO: Fill in your client code that sends out a message.
                                 */

                                sockett.close();
                                Log.v("close123", "close12");
                            } catch (Exception e) {
                                Log.v("Exception14", "exception14");
                            }

                        } else {
                            try {
                                Log.v("yahan1", "yahan1");
                                Cursor resultCursor = getContext().getContentResolver().query(mUri, null, msg_received.key, null, null);
                                while (resultCursor.moveToNext()) {
                                    int keyIndex = resultCursor.getColumnIndex("key");
                                    int valueIndex = resultCursor.getColumnIndex("value");
                                    String listkeyy = resultCursor.getString(keyIndex);
                                    String listvaly = resultCursor.getString(valueIndex);
                                    msg_received.resultquery.put(listkeyy, listvaly);
                                    Log.v("@@@@listkey", listkeyy + "");
                                    Log.v("@@@@listval", listvaly + "");

                                }

                                msg_received.status = "querycomplete";
                                Log.v("yahan2", "yahan2");
                                Log.v("yahan2org", msg_received.origin_port);
                                Socket sockett = new Socket(InetAddress.getByAddress(new byte[]{10, 0, 2, 2}),
                                        Integer.parseInt(msg_received.origin_port));

                                OutputStream outt = sockett.getOutputStream();
                                Log.v("yahan3", "yahan3");
                                ObjectOutputStream ooutt = new ObjectOutputStream(outt);

                                ooutt.writeObject(msg_received);

                                Log.v("out", outt.toString());
                                /*
                                 * TODO: Fill in your client code that sends out a message.
                                 */

                                sockett.close();
                                Log.v("close1234", "close12");

                            } catch (Exception e) {
                                Log.v("Exception15", "exception15");
                            }
                        }
                    }

                    //publishProgress(temps);
                } catch (Exception e) {
                    Log.e("Sah", e.getMessage());
                }
            }
            Log.v("CHECKreturnv", "CHECKreturnv");
            return null;
        }

        protected void onProgressUpdate(String... strings) {
            /*
             * The following code displays what is received in doInBackground().
             */

            Log.v("ser_check_prog-1", "tep");

            try {
                //Log.v("ser_check_prog1","trystatt");

            } catch (Exception e) {
                Log.e("serveron progress", "sometemp");
                Log.e("serveron progress", e.getMessage());
            }
        }

        private Uri buildUri(String scheme, String authority) {
            Uri.Builder uriBuilder = new Uri.Builder();
            uriBuilder.authority(authority);
            uriBuilder.scheme(scheme);
            return uriBuilder.build();
        }
    }

    class ClientTask extends AsyncTask<String, Void, Void> {

        static final String REMOTE_PORT0 = "11108";
        static final String REMOTE_PORT1 = "11112";
        static final String REMOTE_PORT2 = "11116";
        static final String REMOTE_PORT3 = "11120";
        static final String REMOTE_PORT4 = "11124";

        @Override
        protected Void doInBackground(String... msgs) {
            try {
                Log.v("clienttask", "clientask");
                String remotePort = REMOTE_PORT0;
                //     if (msgs[1].equals(REMOTE_PORT0))
                //       remotePort = REMOTE_PORT1;

                String psuccesstosend = porthashwithorg.get(mysuccesshash);
                int tempi = Integer.parseInt(psuccesstosend);
                tempi = tempi * 2;

                String tstat = msgs[2];
                Log.v("tstat", tstat);
                if (tstat.equals("insert")) {
                    Log.v("tstat1", tstat);
                    String successorport = tempi + "";
                    String[] portnos = new String[]{successorport};
                    for (String eachport : portnos) {
                        Socket socket = new Socket(InetAddress.getByAddress(new byte[]{10, 0, 2, 2}),
                                Integer.parseInt(eachport));

                        String msgToSend = msgs[0];
                        String tkey = msgs[0];
                        String tval = msgs[1];

                        Log.v("tkey", tkey);
                        Log.v("tval", tval);

                        tstat = msgs[2];
                        message_obj msg_obj = new message_obj(myPort, tkey, tval, tstat);

                        OutputStream out = socket.getOutputStream();
                        ObjectOutputStream oout = new ObjectOutputStream(out);

                        oout.writeObject(msg_obj);

                        Log.v("out", out.toString());
                        /*
                         * TODO: Fill in your client code that sends out a message.
                         */

                        socket.close();

                    }
                }

                Log.v("ClientTask", "Loop end send to all including self");
            } catch (UnknownHostException e) {
                Log.e("ClientTask", "ClientTask UnknownHostException");
            } catch (IOException e) {
                Log.e("ClientTASK", "ClientTask socket IOException");
                Log.e("ClientTASK", e.getMessage());
            }

            return null;
        }
    }

}
