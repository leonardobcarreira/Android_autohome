package sel373android.leonardocarreira.com.br.voice;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class main_activity extends AppCompatActivity {

    ListView lv;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference mDatabase;
    private DatabaseReference mDatabaseUsers; //path to users in database
    private DatabaseReference mDatabasePersonalInfo;

    private Object returnedCode;

    private String activationCode;

    private  String user_id;

    private ProgressDialog mProgress;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        mAuth = FirebaseAuth.getInstance();

        mDatabaseUsers = FirebaseDatabase.getInstance().getReference().child("Reg_Boards");
        mDatabase = FirebaseDatabase.getInstance().getReference();

        mDatabasePersonalInfo = FirebaseDatabase.getInstance().getReference().child("Reg_Boards").child("Personal_Info");

        mProgress = new ProgressDialog(this);

        mDatabaseUsers.keepSynced(true);

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() == null) {
                    //user state changed to loged out
                    Intent loginIntent = new Intent(main_activity.this, login_activity.class);
                    loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); //impede usuario de voltar usando back
                    startActivity(loginIntent);
                }
            }
        };

        FirebaseUser teste = mAuth.getCurrentUser();

        if(mAuth.getCurrentUser() != null)
        checkUserExist();
        else
        {
            Intent loginIntent = new Intent(main_activity.this, login_activity.class);
            loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); //impede usuario de voltar usando back
            startActivity(loginIntent);
        }

        //Value event listener for realtime data update for especific board logged in
        if (user_id != null) {
            mDatabasePersonalInfo.child(user_id).addChildEventListener(new ChildEventListener() {

                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {


                    activationCode = dataSnapshot.getValue(String.class);


                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    activationCode = dataSnapshot.getValue(String.class);

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
    }

    @Override
    protected void onStart() {
        super.onStart();


        mAuth.addAuthStateListener(mAuthListener);


        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(main_activity.this,
                        android.R.layout.simple_list_item_1,
                        getResources().getStringArray(R.array.screens));
        lv = (ListView) findViewById(R.id.main_listview);
        lv.setAdapter(adapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> lv, View view, int position, long id) {


                switch (position) {
                    case 0:
                        Intent light_intent = new Intent(main_activity.this, light_activity.class);

                        if(activationCode == null)
                        {
                            mProgress.setMessage("Carregando informações do banco de dados");
                            mProgress.show();
                        }
                        else
                        {
                        mProgress.dismiss();
                        light_intent.putExtra("activationCode",activationCode);
                        startActivity(light_intent);}
                        break;
                    case 1:
                        Intent coffe_intent = new Intent(main_activity.this, coffee_machine_activity.class);
                        if(activationCode == null)
                        {
                            mProgress.setMessage("Carregando informações do banco de dados");
                            mProgress.show();
                        }
                        else
                        {
                            mProgress.dismiss();
                            coffe_intent.putExtra("activationCode", activationCode);
                            startActivity(coffe_intent);
                            break;
                        }
                    case 2:
                        Intent status_intent = new Intent(main_activity.this, status_activity.class);
                        if (activationCode == null)
                        {
                            mProgress.setMessage("Carregando informações do banco de dados");
                            mProgress.show();
                        }
                        else
                        {
                            mProgress.dismiss();
                            status_intent.putExtra("activationCode", activationCode);
                            startActivity(status_intent);
                            break;
                        }
                    case 3:
                        Intent log_intent = new Intent(main_activity.this, log_activity.class);
                        if (activationCode == null)
                        {
                            mProgress.setMessage("Carregando informações do banco de dados");
                            mProgress.show();
                        }
                        else
                        {
                            mProgress.dismiss();
                            log_intent.putExtra("activationCode", activationCode);
                            startActivity(log_intent);
                            break;
                        }

                }
            }
        });

    }

   private void checkUserExist()
    {
        if (mAuth.getCurrentUser() != null)
        user_id = mAuth.getCurrentUser().getUid();

        mDatabasePersonalInfo.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(!dataSnapshot.hasChild(user_id))
                {
                   Toast.makeText(main_activity.this,"Você está registrado, porém deve configurar o usuario antes de iniciar sua sessão",Toast.LENGTH_LONG).show();
                   Intent loginIntent = new Intent(main_activity.this, login_activity.class);
                   loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); //impede usuario de voltar usando back
                   startActivity(loginIntent);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if(item.getItemId() == R.id.action_logout)
        {
            logout();
        }
        return super.onOptionsItemSelected(item);
    }

    private void logout() {
        mAuth.signOut();
    }
}


