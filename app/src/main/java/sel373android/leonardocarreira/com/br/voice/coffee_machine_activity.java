    package sel373android.leonardocarreira.com.br.voice;

    import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
    import java.util.Arrays;
    import java.util.Calendar;
    import java.util.HashMap;
import java.util.Locale;
    import java.util.Map;
    import java.util.TimeZone;

    public class coffee_machine_activity extends AppCompatActivity implements View.OnClickListener {

        //Variables declaration
        //time
        TimeZone tz;
        Calendar c;
        String time;
        //widgets variables
        ListView lv;
        private ProgressDialog mProgress;

        //speech variables
        static final int check = 1234; //voice recog. request code
        private TextToSpeech tts;
        private static cafe tipo_cafe = new cafe();
        private static cafe pedido = new cafe();
        private Boolean busy_flag;
        private Boolean power_flag;


        //firebase variables
        private FirebaseAuth mAuth;
        private FirebaseUser mCurrentUser;
        private DatabaseReference mDatabaseCurrentUser;
        private String activationCode;
        private String user_email;
        private long nLogs;
        private String logKeys[] = {"", "", "", "", "", "", "", "", "", "", ""};


        //Creates the current activity
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_coffe_machine_activity);

            //get current board
            String code = getIntent().getStringExtra("activationCode");
            Firebase.setAndroidContext(this);
            activationCode = code;
            activationCode = getIntent().getExtras().getString("activationCode");

            //get firebase current user
            mAuth = FirebaseAuth.getInstance();
            mCurrentUser = mAuth.getCurrentUser();

            if (mCurrentUser != null)
            {
                user_email = mCurrentUser.getEmail();

            }

            mDatabaseCurrentUser = FirebaseDatabase.getInstance().getReference();
            mProgress = new ProgressDialog(this);


            lv = (ListView) findViewById(R.id.lvVoiceReturn);
            //button speak
            Button b = (Button) findViewById(R.id.order);
            b.setOnClickListener(this);
            //power button
            Button bpower = (Button) findViewById(R.id.bpower);
            bpower.setOnClickListener(this);

            //text to phone speech
            tts = new TextToSpeech(coffee_machine_activity.this, new TextToSpeech.OnInitListener()
            {
                public void onInit(int status) {
                    if (status == TextToSpeech.SUCCESS) {
                        int result = tts.setLanguage(new Locale ("pt", "BR"));
                        if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                            Log.e("TTS", "This Language is not supported");
                            Intent installIntent = new Intent();
                            installIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                            startActivity(installIntent);
                        }
                    }
                    else
                    {
                        Log.e("TTS", "Falha de Inicialização");
                    }
                }
            });

            busy_flag = false;
        }


        @Override
        protected void onStart() {
            super.onStart();

            final Button bPower = (Button) findViewById(R.id.bpower);
            mProgress.setMessage("Carregando informações do banco de dados");
            mProgress.show();
            //Values event listener for realtime data update for especific board logged in
            mDatabaseCurrentUser.child("Reg_Boards").child(activationCode).child("Device").child("Cafeteira").child("queue").addChildEventListener(new ChildEventListener() {

                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                    String type = dataSnapshot.getValue(String.class);

                        if (type != null)
                            busy_flag = true;
                        else
                            busy_flag = false;
                }
                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s)
                {

                }
                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {

                    busy_flag = false;

                }
                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }
                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            mDatabaseCurrentUser.child("Reg_Boards").child(activationCode).child("Device").child("Cafeteira").child("Power").addChildEventListener(new ChildEventListener() {

                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                    String power = dataSnapshot.getValue(String.class);
                    power_flag = power.equals("on");
                    if (power_flag)
                        bPower.setSelected(true);
                    else
                        bPower.setSelected(false);


                }
                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s)
                {
                    String power = dataSnapshot.getValue(String.class);
                    power_flag = power.equals("on");
                    if (power_flag)
                        bPower.setSelected(true);
                    else
                        bPower.setSelected(false);
                }
                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {

                }
                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }
                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });         mProgress.dismiss();

            mDatabaseCurrentUser.addChildEventListener(new ChildEventListener() {

                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                    nLogs = dataSnapshot.child(activationCode).child("Log").child("Log_cafeteira").getChildrenCount();
                    if(nLogs != 0)
                    {
                        Map<String, Object> logData = (Map<String, Object>) dataSnapshot.child(activationCode).child("Log").child("Log_cafeteira").getValue();
                        int i = 0;
                        for (Map.Entry<String, Object> entry : logData.entrySet()) {
                            logKeys[i] = entry.getKey();
                            i++;
                        }
                    }

                }
                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s)
                {
                    nLogs = dataSnapshot.child(activationCode).child("Log").child("Log_cafeteira").getChildrenCount();
                    if(nLogs != 0)
                    {
                        Map<String, Object> logData = (Map<String, Object>) dataSnapshot.child(activationCode).child("Log").child("Log_cafeteira").getValue();
                        int i = 0;
                        for (Map.Entry<String, Object> entry : logData.entrySet()) {
                            logKeys[i] = entry.getKey();
                            i++;
                        }
                    }

                }
                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                    nLogs = dataSnapshot.getChildrenCount();
                    if(nLogs != 0)
                    {
                        Map<String, Object> logData = (Map<String, Object>) dataSnapshot.child(activationCode).child("Log").child("Log_cafeteira").getValue();
                        int i = 0;
                        for (Map.Entry<String, Object> entry : logData.entrySet()) {
                            logKeys[i] = entry.getKey();
                            i++;
                        }
                    }

                }
                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }
                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });         mProgress.dismiss();

        }
       //Called to deal with all "clicks" during this activity


        public void onClick(View v)

        {   //time
           tz =TimeZone.getDefault();
           c = Calendar.getInstance(tz);
          time = String.format("%02d", c.get(Calendar.YEAR))+"-" +
                    String.format("%02d", c.get(Calendar.MONTH))+"-" +
                    String.format("%02d", c.get(Calendar.DAY_OF_MONTH))+"-" +
                    String.format("%02d" , c.get(Calendar.HOUR_OF_DAY))+":"+
                    String.format("%02d" , c.get(Calendar.MINUTE))+":"+
                    String.format("%02d" , c.get(Calendar.SECOND));
            Button power = (Button) findViewById(R.id.bpower);
            Intent i = new Intent (RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

            switch (v.getId())
            {
                case R.id.bpower:

                        if (v.isSelected()) {
                            if (!busy_flag)
                            {
                                mDatabaseCurrentUser.child("Reg_Boards").child(activationCode).child("Device").child("Cafeteira").child("Power").child("status").setValue("off");
                                if (nLogs > 10) {
                                    Arrays.sort(logKeys);
                                    mDatabaseCurrentUser.child("Reg_Boards").child(activationCode).child("Log").child("Log_cafeteira").child(logKeys[0]).removeValue();
                                }
                                mDatabaseCurrentUser.child("Reg_Boards").child(activationCode).child("Log").child("Log_cafeteira").child(time).setValue("Desligada : by " + user_email);
                            }
                            else
                                ttsUnder20("Café em execução, não posso desligar agora");
                        }
                        else
                        {
                            mDatabaseCurrentUser.child("Reg_Boards").child(activationCode).child("Device").child("Cafeteira").child("Power").child("status").setValue("on");
                            if (nLogs > 10) {
                                Arrays.sort(logKeys);
                                mDatabaseCurrentUser.child("Reg_Boards").child(activationCode).child("Log").child("Log_cafeteira").child(logKeys[0]).removeValue();
                            }
                            mDatabaseCurrentUser.child("Reg_Boards").child(activationCode).child("Log").child("Log_cafeteira").child(time).setValue("Ligada : by " + user_email);
                        }

                    break;
                case R.id.order:
                    if (power.isSelected())
                    {
                        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                        i.putExtra(RecognizerIntent.EXTRA_PROMPT,
                                "Expresso, Duplo,\n Cappuccino, Latte, \n Americano, Mocha ");
                        startActivityForResult(i, check);
                        break;
                    }
                    else
                    {
                        Toast.makeText(this,
                                "Cafeteira Desligada", Toast.LENGTH_LONG).show();
                        ttsGreater21("Cafeteira Desligada");
                    }

            }
        }

        //called to request the results from google voice recognition activity
        @Override
        protected void onActivityResult(int requestCode, int resultCode, Intent data) {


            if (requestCode == check && resultCode == RESULT_OK)
            {
                //filling our list view
                ArrayList<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

                lv.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, results));
                searchOnList(results);

            }
            super.onActivityResult(requestCode, resultCode, data);

        }

        //Search on our listView to find results that match with all kinds of coffe our machine can make
        public void searchOnList(ArrayList<String> voice_results)
        {
            int index =0;
            int error =1;
            TextView showResult = (TextView) findViewById( (R.id.VoiceText)) ;

            //Firebase setup
            FirebaseDatabase my_database = FirebaseDatabase.getInstance();
            DatabaseReference myRef = my_database.getReference("TipoCafe");

            for (int i=0; i < voice_results.size(); i++)
            {
                if ( voice_results.get(i).compareToIgnoreCase("Expresso") == 0 || voice_results.get(i).compareToIgnoreCase("Duplo") == 0
                        || voice_results.get(i).compareToIgnoreCase("Cappuccino") == 0  || voice_results.get(i).compareToIgnoreCase("Latte") == 0
                        ||voice_results.get(i).compareToIgnoreCase("Americano") == 0 || voice_results.get(i).compareToIgnoreCase("Mooca") == 0
                        )
                {
                    index = i;
                    error = 0;
                }
            }
            if (error == 0)
            {
                showResult.setText(voice_results.get(index));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                {   if (!busy_flag )
                    {
                    ttsGreater21("Saindo um" + voice_results.get(index) + "quentinho");
                    pedido.setCafe(voice_results.get(index));
                    mDatabaseCurrentUser.child("Reg_Boards").child(activationCode).child("Device").child("Cafeteira").child("queue").child("type").setValue(voice_results.get(index));
                    if (nLogs > 10)
                     {   Arrays.sort(logKeys);
                            mDatabaseCurrentUser.child("Reg_Boards").child(activationCode).child("Log").child("Log_cafeteira").child(logKeys[0]).removeValue();
                     }
                     mDatabaseCurrentUser.child("Reg_Boards").child(activationCode).child("Log").child("Log_cafeteira").child(time).setValue( voice_results.get(index) +" by "+ user_email);

                    }
                    else
                    {
                        ttsGreater21("Bandeja ocupada, peça mais tarde");
                    }
                } else
                {
                    if (!busy_flag) {
                        ttsUnder20("Saindo um" + voice_results.get(index) + "quentinho");
                        pedido.setCafe(voice_results.get(index));
                        mDatabaseCurrentUser.child("Reg_Boards").child(activationCode).child("Device").child("Cafeteira").child("queue").child("type").setValue(voice_results.get(index));
                        if (nLogs > 10)
                        {   Arrays.sort(logKeys);
                            mDatabaseCurrentUser.child("Reg_Boards").child(activationCode).child("Log").child("Log_cafeteira").child(logKeys[0]).removeValue();
                        }
                        mDatabaseCurrentUser.child("Reg_Boards").child(activationCode).child("Log").child("Log_cafeteira").child(time).setValue( voice_results.get(index) +" by "+ user_email);

                    }
                    else
                    {
                        ttsGreater21("Bandeja ocupada, peça mais tarde");
                    }
                }
            }
            else
            {
                showResult.setText("Desculpe, sem correspondencia");

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                {
                    ttsGreater21("Desculpe, sem correspondencia. Tente novamente");
                } else
                {
                    ttsUnder20("Desculpe, sem correspondencia. Tente novamente");
                }

            }

        }

        //Speaking functions (app to person) converts text to speak, using the @targetApi method to use
        // the most optimal function based on the current API
        @SuppressWarnings("deprecation")
        private void ttsUnder20(String text) {
            HashMap<String, String> map = new HashMap<>();
            map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "MessageId");
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, map);
        }

        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        private void ttsGreater21(String text) {
            String utteranceId=this.hashCode() + "";
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, utteranceId);
        }

        //pause speeching execution only when a new activity takes place, since we are calling
        //google voice recognition activity we need to kill the tts only during onStop not during onPause
        //because the google activity doesnt cover completely the current activity.
        @Override
        protected void onStop() {
            if (tts != null)
            {
                tts.stop();
                tts.shutdown();
            }
            super.onStop();
        }

    }
