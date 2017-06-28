package sel373android.leonardocarreira.com.br.voice;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.firebase.client.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Map;
import java.util.TimeZone;


public class light_activity extends AppCompatActivity implements View.OnClickListener {

    public static luz current_status = new luz();

    private ProgressDialog mProgress;


    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;
    private DatabaseReference mDatabaseCurrentUser;

    private String activationCode;
    private long nLogs;
    private String user_email;

    private String logKeys[] = {"", "", "", "", "", "", "", "", "", "", ""};


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_light_activity);
        String code = getIntent().getStringExtra("activationCode");
        Firebase.setAndroidContext(this);

        activationCode = code;

        activationCode = getIntent().getExtras().getString("activationCode");

        mAuth = FirebaseAuth.getInstance();

        mCurrentUser = mAuth.getCurrentUser();

        if (mCurrentUser != null)
        {
            user_email = mCurrentUser.getEmail();

        }
        mDatabaseCurrentUser = FirebaseDatabase.getInstance().getReference();

        mProgress = new ProgressDialog(this);

    }

    @Override
    protected void onStart() {
        super.onStart();

        mProgress.setMessage("Carregando informações do banco de dados");
        mProgress.show();

        //Value event listener for realtime data update for especific board logged in
        mDatabaseCurrentUser.addChildEventListener(new ChildEventListener() {

            ImageView bulb = (ImageView) findViewById(R.id.bulb);
            Button blight = (Button) findViewById(R.id.blight);

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                current_status = dataSnapshot.child(activationCode).child("Device").child("Luz").getValue(luz.class);
               nLogs = dataSnapshot.child(activationCode).child("Log").child("Log_luz").getChildrenCount();

                blight.setOnClickListener(light_activity.this);

                //set correct image to app activity
                if(current_status != null)
                {
                    if (current_status.getStatus().equals("acesa")) {
                        bulb.setImageResource(R.drawable.bulb_on);
                        blight.setSelected(true);
                        mProgress.dismiss();

                    } else {
                        bulb.setImageResource(R.drawable.bulb_off);
                        blight.setSelected(false);
                        mProgress.dismiss();
                    }
                }
                else
                {
                    bulb.setImageResource(R.drawable.bulb_off);
                    blight.setSelected(false);
                    mProgress.dismiss();
                }

                if(nLogs != 0)
                {
                    Map<String, Object> logData = (Map<String, Object>) dataSnapshot.child(activationCode).child("Log").child("Log_luz").getValue();
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


                current_status = dataSnapshot.child(activationCode).child("Device").child("Luz").getValue(luz.class);
                nLogs = dataSnapshot.child(activationCode).child("Log").child("Log_luz").getChildrenCount();
                blight.setOnClickListener(light_activity.this);

                if(current_status != null)
                {
                    if (current_status.getStatus().equals("acesa")) {
                        bulb.setImageResource(R.drawable.bulb_on);
                        blight.setSelected(true);
                        mProgress.dismiss();

                    } else {
                        bulb.setImageResource(R.drawable.bulb_off);
                        blight.setSelected(false);
                        mProgress.dismiss();

                    }
                }
                else
                {
                    bulb.setImageResource(R.drawable.bulb_off);
                    blight.setSelected(false);
                    mProgress.dismiss();
                }
                if(nLogs != 0)
                {
                    Map<String, Object> logData = (Map<String, Object>) dataSnapshot.child(activationCode).child("Log").child("Log_luz").getValue();
                    int i = 0;
                    for (Map.Entry<String, Object> entry : logData.entrySet()) {
                        logKeys[i] = entry.getKey();
                        i++;
                    }
                }
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

        });

    }

    @Override
    public void onClick(View v) {

        Button blight = (Button) findViewById(R.id.blight);

        luz status_luz = new luz();
        TimeZone tz = TimeZone.getDefault();
        Calendar c = Calendar.getInstance(tz);
        String time = String.format("%02d", c.get(Calendar.YEAR))+"-" +
                String.format("%02d", c.get(Calendar.MONTH))+"-" +
                String.format("%02d", c.get(Calendar.DAY_OF_MONTH))+"-" +
                String.format("%02d" , c.get(Calendar.HOUR_OF_DAY))+":"+
                String.format("%02d" , c.get(Calendar.MINUTE))+":"+
                String.format("%02d" , c.get(Calendar.SECOND));

        switch (v.getId())
        {

            case R.id.blight:
                if (blight.isSelected())
                {
                    status_luz.setStatus("apagada");
                    mDatabaseCurrentUser.child("Reg_Boards").child(activationCode).child("Device").child("Luz").removeValue();
                    mDatabaseCurrentUser.child("Reg_Boards").child(activationCode).child("Device").child("Luz").setValue(status_luz);
                    //check if log count didn't pass over 10 positions, if it did remove the oldest post
                    if (nLogs > 10)
                    {   Arrays.sort(logKeys);
                        mDatabaseCurrentUser.child("Reg_Boards").child(activationCode).child("Log").child("Log_luz").child(logKeys[0]).removeValue();
                    }
                    mDatabaseCurrentUser.child("Reg_Boards").child(activationCode).child("Log").child("Log_luz").child(time).setValue("apagada : by "+ user_email);
                }
                else {
                    status_luz.setStatus("acesa");
                    mDatabaseCurrentUser.child("Reg_Boards").child(activationCode).child("Device").child("Luz").removeValue();
                    mDatabaseCurrentUser.child("Reg_Boards").child(activationCode).child("Device").child("Luz").setValue(status_luz);
                    //check if log count didn't pass over 10 positions, if it did remove the oldest post
                    if (nLogs > 10)
                    {   Arrays.sort(logKeys);
                        mDatabaseCurrentUser.child("Reg_Boards").child(activationCode).child("Log").child("Log_luz").child(logKeys[0]).removeValue();
                    }
                    mDatabaseCurrentUser.child("Reg_Boards").child(activationCode).child("Log").child("Log_luz").child(time).setValue("acesa : by "+ user_email);
                    break;
                }
        }
    }

}
