package com.example.vaibhavgupta.smartsms;

/**
 * Created by vaibhav gupta on 31-03-2015.
 */
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class writing extends Activity{


    protected static final int RESULT_SPEECH = 1;

    TextToSpeech ttobj;
    TextView gestureEvent;

    String stt;

    int i;

    EditText rec;
    EditText msg;


    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout2);

        gestureEvent = (TextView)findViewById(R.id.GestureEvent);

        rec=(EditText) findViewById(R.id.recipient);
        msg=(EditText) findViewById(R.id.message);


        ttobj=new TextToSpeech(getApplicationContext(),
                new TextToSpeech.OnInitListener() {
                    @Override
                    public void onInit(int status) {
                        if(status != TextToSpeech.ERROR){
                            ttobj.setLanguage(Locale.US);
                        }
                    }
                });



        ImageButton send_button=(ImageButton) findViewById(R.id.send);
        send_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view){

                try {
                    sendSMS();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        });

        ImageButton b2=(ImageButton) findViewById(R.id.pick_contact);
        b2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg1) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                startActivityForResult(intent, 7);
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

            if (angle > 45 && angle <= 135) {
                swipe = "Swipe Up\n";
                speakText("Speak 1 for entering contact number and 2 for entering name from contact list");
                try {
                    Thread.sleep(5600);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                i=2;
                textspeak();
            }

            if (angle >= 135 && angle < 180 || angle < -135 && angle > -180) {
                swipe = "Swipe Left\n";
                Intent intent=new Intent(getApplicationContext(),MainActivity.class);
                startActivity(intent);


            }
            if (angle < -45 && angle>= -135) {
                swipe = "Swipe Down\n";
                speakText("enter your message");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //EditText msg=(EditText) findViewById(R.id.message);
                i=1;
                textspeak();


            }
            if (angle > -45 && angle <= 45) {
                swipe = "Swipe Right\n";
                rec.setText("");
                msg.setText("");

            }

            gestureEvent.setText(swipe);

            return super.onFling(e1, e2, velocityX, velocityY);
        }

        @Override
        public boolean onDoubleTapEvent(MotionEvent e)
        {
            gestureEvent.setText("DoubleTap");
            try {
                sendSMS();
            } catch (InterruptedException me) {
                me.printStackTrace();
            }

            return true;
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



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        TextView abc=(TextView) findViewById(R.id.recipient);

        switch (requestCode) {

            case (7) :

                if (resultCode == Activity.RESULT_OK) {

                    Uri contactData = data.getData();
                    Cursor contactCursor = getContentResolver().query(contactData,
                            new String[] { ContactsContract.Contacts._ID }, null, null,
                            null);
                    String id = null;
                    if (contactCursor.moveToFirst()) {
                        id = contactCursor.getString(contactCursor
                                .getColumnIndex(ContactsContract.Contacts._ID));
                    }
                    Log.e("d12", id);
                    contactCursor.close();
                    String phoneNumber = null;
                    Cursor phoneCursor = getContentResolver().query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER},
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "= ? ",
                            new String[]{id}, null);
                    if (phoneCursor.moveToFirst()) {
                        phoneNumber = phoneCursor
                                .getString(phoneCursor
                                        .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    }
                    Log.w("jfnejnj", phoneNumber);
                    phoneCursor.close();
                    abc.setText(phoneNumber);


                    break;
                }
            case RESULT_SPEECH: {

                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> text = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    stt = text.get(0);
                    if(i==1)
                    {
                        msg.setText(stt);
                    }
                    Log.v("stt",stt);
                    if(i==2)
                    {

                        Log.v("stt0",stt);
                        if(stt.equals("1"))
                        {
                            speakText("Speak Number");
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            i=4;
                            textspeak();


                        }
                        if(stt.equals("2"))
                        {

                            speakText("Speak Name");
                            Log.v("hdj","dj");
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            i=3;
                            Log.v("abdjsbcjsbcjdbekcjbkdcjcj",stt);
                            textspeak();

                        }

                    }
                    if(i==3)
                    {
                        text = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                        stt = text.get(0);
                        Log.v("ssssss",stt);
                        if(!stt.equals("2"))
                        {
                            input_contacts(stt);
                        }

                    }

                    if(i==4)
                    {
                        stt=stt.replaceAll("\\s+","");
                        rec.setText(stt);
                    }
                    if(i==5)
                    {
                        i=0;
                        if(stt.equals("yes"))
                        {
                            try {
                                sendSMS2();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                }
                break;
            }
        }
    }

    protected void input_contacts(String name)
    {

        if (name!=null) {
            String id_name = null;
            Uri resultUri = ContactsContract.Contacts.CONTENT_URI;
            Cursor cont = getContentResolver().query(resultUri, null, null, null, null);
            String whereName = ContactsContract.Data.MIMETYPE + " = ? AND " + ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME + " = ?";
            String[] whereNameParams = new String[]{ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE, name};
            Cursor nameCur = getContentResolver().query(ContactsContract.Data.CONTENT_URI, null, whereName, whereNameParams, ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME);

            Log.w("ejhnrjncjrc","rcjncjrbcjbcjb");

            if(nameCur!=null && nameCur.getCount()>0) {
                while (nameCur.moveToNext()) {
                    id_name = nameCur.getString(nameCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.CONTACT_ID));
                }
                nameCur.close();
                cont.close();
                nameCur.close();
                String phoneNumber = null;
                Cursor phoneCursor = getContentResolver().query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER},
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "= ? ",
                        new String[]{id_name}, null);
                if (phoneCursor.moveToFirst()) {
                    phoneNumber = phoneCursor
                            .getString(phoneCursor
                                    .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                }
                phoneCursor.close();
                rec.setText(phoneNumber);
            }



        }

    }




    protected void sendSMS() throws InterruptedException {

        if(rec.getText()!=null && msg.getText()!=null) {
            if (i != 5) {
                String abcd = String.valueOf(rec.getText());
                abcd = abcd.replaceAll("\\B", " ");
                speakText("Contact Entered" + abcd);
                Thread.sleep(5000);
                speakText("Message Entered" + String.valueOf(msg.getText()));
                Thread.sleep(4000);
                speakText("Do you want to send this message");
                Thread.sleep(1200);
                i = 5;
                textspeak();
            }
        }
    }

    protected void sendSMS2() throws InterruptedException {


        try {

            SmsManager smsManager = SmsManager.getDefault();

            smsManager.sendTextMessage(String.valueOf(rec.getText()), null,String.valueOf(msg.getText()), null, null);

            Toast.makeText(getApplicationContext(), "SMS Sent!",

                    Toast.LENGTH_LONG).show();

        } catch (Exception e) {

            Toast.makeText(getApplicationContext(),

                    "SMS failed, please try again later!",
                    Toast.LENGTH_LONG).show();

            e.printStackTrace();

        }

    }




    protected void textspeak(){

        Intent intent = new Intent(
                RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    //public static void main(String[] args) {
    // TODO Auto-generated method stub

    //}

}

