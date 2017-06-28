package sel373android.leonardocarreira.com.br.voice;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ExpandableListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class log_activity extends AppCompatActivity {
    private ProgressDialog mProgress;
    private lista_log_adapter adapter;
    private ExpandableListView luz_log, cafe_log;
    private Map<String,ArrayList<String>> mapChild;
    private DatabaseReference mDatabaseCurrentUser;

    private String activationCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);

        activationCode = getIntent().getExtras().getString("activationCode");

        mDatabaseCurrentUser = FirebaseDatabase.getInstance().getReference();

        mProgress = new ProgressDialog(this);

        luz_log = (ExpandableListView) findViewById(R.id.luz_list);
        cafe_log =(ExpandableListView) findViewById(R.id.cafe_list);
        mapChild = new HashMap<>();

    }

    @Override
    protected void onStart() {
        super.onStart();
        mProgress.setMessage("Carregando informações do banco de dados");
        mProgress.show();

        mDatabaseCurrentUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                ArrayList<String> luz_logs = new ArrayList<String>();
                ArrayList<String> cafe_logs = new ArrayList<String>();
                ArrayList<String> device = new ArrayList<String>();


                for (DataSnapshot logLuzSnapshot: dataSnapshot.child("Reg_Boards").child(activationCode).child("Log").child("Log_luz").getChildren()) {
                    String log_luz_value = logLuzSnapshot.getValue(String.class);
                    String log_time = logLuzSnapshot.getKey();
                    luz_logs.add(log_time +":"+log_luz_value);
                }

                for (DataSnapshot logCafeSnapshot: dataSnapshot.child("Reg_Boards").child(activationCode).child("Log").child("Log_cafeteira").getChildren()) {
                    String log_cafe_value = logCafeSnapshot.getValue(String.class);
                    String log_time = logCafeSnapshot.getKey();
                    cafe_logs.add(log_time +":"+log_cafe_value);
                }

                device.add("Luz");
                device.add("Cafeteira");

                loadData(device,luz_logs,cafe_logs, luz_log);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private  void loadData(ArrayList<String> device, ArrayList<String> logs_luz,ArrayList<String> logs_cafe,ExpandableListView explv )
    {   mapChild.put(device.get(0),logs_luz);
        mapChild.put(device.get(1),logs_cafe);
        adapter = new lista_log_adapter(this, device, mapChild);
        explv.setAdapter(adapter);
        mProgress.dismiss();

    }
}
