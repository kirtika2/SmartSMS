package com.example.vaibhavgupta.smartsms;

import com.example.vaibhavgupta.smartsms.R;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;
import java.util.regex.Pattern;

public class MainActivity extends Activity implements OnClickListener{

    //  GUI Widget
    Button btnSent, btnInbox, btnDraft;
    protected static final int RESULT_SPEECH = 1;
    TextView lblMsg, lblNo;
    TextView gestureEvent;
    ListView lvMsg;
    float x1,x2;
    float y1, y2;
    TextToSpeech ttobj;
    String stt;
    int i=0;
    int number;



    // Cursor Adapter
    SimpleCursorAdapter adapter;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        // Init GUI Widget
        btnInbox = (Button) findViewById(R.id.btnInbox);
        btnInbox.setOnClickListener(this);

        btnSent = (Button)findViewById(R.id.btnSentBox);
        btnSent.setOnClickListener(this);


        lvMsg = (ListView) findViewById(R.id.lvMsg);
        gestureEvent = (TextView)findViewById(R.id.GestureEvent);

        ttobj=new TextToSpeech(getApplicationContext(),
                new TextToSpeech.OnInitListener() {
                    @Override
                    public void onInit(int status) {
                        if(status != TextToSpeech.ERROR){
                            ttobj.setLanguage(Locale.US);
                        }
                    }
                });

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // TODO Auto-generated method stub
        return gestureDetector.onTouchEvent(event);
    }

    GestureDetector.SimpleOnGestureListener simpleOnGestureListener
            = new GestureDetector.SimpleOnGestureListener(){


        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                               float velocityY) {
            String swipe = "";

            float x1=e1.getX();
            float x2=e2.getX();
            float y1=e1.getY();
            float y2=e2.getY();

            Double angle = Math.toDegrees(Math.atan2(y1 - y2, x2 - x1));

            if (angle > 45 && angle <= 135)
                swipe = "Swipe Up\n";

            if (angle >= 135 && angle < 180 || angle < -135 && angle > -180) {
                swipe = "Swipe Left\n";
                onClick(btnInbox);
            }
            if (angle < -45 && angle>= -135) {
                swipe = "Swipe Down\n";
            }
            if (angle > -45 && angle <= 45) {
                swipe = "Swipe Right\n";
                onClick(btnSent);
            }

            gestureEvent.setText(swipe);

            return super.onFling(e1, e2, velocityX, velocityY);
        }

        @Override
        public boolean onDoubleTapEvent(MotionEvent e)
        {
            gestureEvent.setText("DoubleTap");
            Intent intent=new Intent(getApplicationContext(),writing.class);
            startActivity(intent);
            return true;
        }


        @Override
        public void onLongPress(MotionEvent e) {
            gestureEvent.setText("Longpress detected");
            lvMsg = (ListView) findViewById(R.id.lvMsg);
            number= lvMsg.getFirstVisiblePosition();
            Object obj = adapter.getItem(number);
            Cursor cursor = (Cursor) adapter.getItem(number);
            String no=cursor.getString(1);
            String msg = cursor.getString(2);

            if (no!=null && msg!=null)
            {

                if (Pattern.matches("[a-zA-Z]+", no) == false && no.length() > 2){
                    no=no.replaceAll("\\B", " ");
                }

                speakText("Message sent from" + no + "is as follows,,  " + msg);
                try {
                    Thread.sleep(6000);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
                speakText("Do you want to read next message");
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
                i=1;
                textspeak();

            }

        }

    };
    GestureDetector gestureDetector
            = new GestureDetector(simpleOnGestureListener);

    @Override
    public boolean dispatchTouchEvent(MotionEvent e)
    {
        super.dispatchTouchEvent(e);
        return gestureDetector.onTouchEvent(e);
    }

    @Override
    public void onStop() {
        if (ttobj != null) {
            ttobj.stop();
        }
        super.onStop();
    }

    @Override
    public void onDestroy() {
        if (ttobj != null) {
            ttobj.shutdown();
        }
        super.onDestroy();
    }

    public void speakText(String toSpeak){
        //String toSpeak = speak6;

        Toast.makeText(getApplicationContext(), toSpeak,
                Toast.LENGTH_SHORT).show();
        ttobj.speak(toSpeak, TextToSpeech.QUEUE_ADD, null);
        Log.v("hdj","dj");
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case RESULT_SPEECH: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> text = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    stt = text.get(0);
                    if (i==1)
                    {
                        if(stt.equals("yes"))
                        {
                            number=number+1;
                            Object obj = adapter.getItem(number);
                            Cursor cursor = (Cursor) adapter.getItem(number);
                            String no=cursor.getString(1);
                            String msg = cursor.getString(2);

                            if (no!=null && msg!=null)
                            {
                                if (Pattern.matches("[a-zA-Z]+", no) == false && no.length() > 2){
                                    no=no.replaceAll("\\B", " ");
                                }
                                speakText("Message sent by" + no + "is" + msg);
                                try {
                                    Thread.sleep(6000);
                                } catch (InterruptedException e1) {
                                    e1.printStackTrace();
                                }
                                speakText("Do you want to read next message");
                                try {
                                    Thread.sleep(10000);
                                } catch (InterruptedException e1) {
                                    e1.printStackTrace();
                                }
                                i=1;
                                textspeak();

                            }


                        }
                    }
                }

                break;

            }

        }
    }


    protected void textspeak(){

        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "en-US");

        try {
            startActivityForResult(intent, RESULT_SPEECH);

        } catch (ActivityNotFoundException a) {
            Toast t = Toast.makeText(getApplicationContext(),
                    "Ops! Your device doesn't support Speech to Text",
                    Toast.LENGTH_SHORT);
            t.show();

        }

    }


    @Override
    public void onClick(View v) {

        if (v == btnInbox) {

            // Create Inbox box URI
            Uri inboxURI = Uri.parse("content://sms/inbox");

            // List required columns
            String[] reqCols = new String[] { "_id", "address", "body" };

            // Get Content Resolver object, which will deal with Content Provider
            ContentResolver cr = getContentResolver();

            // Fetch Inbox SMS Message from Built-in Content Provider
            Cursor c = cr.query(inboxURI, reqCols, null, null, null);

            // Attached Cursor with adapter and display in listview
            adapter = new SimpleCursorAdapter(this, R.layout.row, c,
                    new String[] { "body", "address" }, new int[] {
                    R.id.lblMsg, R.id.lblNumber });
            lvMsg.setAdapter(adapter);

        }

        if(v==btnSent)
        {

            // Create Sent box URI
            Uri sentURI = Uri.parse("content://sms/sent");

            // List required columns
            String[] reqCols = new String[] { "_id", "address", "body" };

            // Get Content Resolver object, which will deal with Content Provider
            ContentResolver cr = getContentResolver();

            // Fetch Sent SMS Message from Built-in Content Provider
            Cursor c = cr.query(sentURI, reqCols, null, null, null);

            // Attached Cursor with adapter and display in listview
            adapter = new SimpleCursorAdapter(this, R.layout.row, c,
                    new String[] { "body", "address" }, new int[] {
                    R.id.lblMsg, R.id.lblNumber });
            lvMsg.setAdapter(adapter);

        }



    }
}