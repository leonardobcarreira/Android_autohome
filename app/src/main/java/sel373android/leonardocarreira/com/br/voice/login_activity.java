package sel373android.leonardocarreira.com.br.voice;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class login_activity extends AppCompatActivity {

    private EditText mEmailField;
    private EditText mPasswordField;
    private EditText mActivationCodeField;

    private Button mLoginButton;
    private Button mRegisterButton;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    private ProgressDialog mProgress;

    public static final String TAG = "MyActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_activity);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Reg_Boards"); //store users codes in a private child on firebase
        mDatabase.keepSynced(true); //stores data offline

        mProgress = new ProgressDialog(this);

        mEmailField = (EditText) findViewById(R.id.usuario);
        mPasswordField = (EditText) findViewById(R.id.senha);
        mActivationCodeField = (EditText) findViewById(R.id.codigo);

        mRegisterButton = (Button) findViewById(R.id.register);
        mLoginButton = (Button) findViewById(R.id.login);

        mRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startRegister();
            }
        });

        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkLogin();
            }

        });
    }

    private void checkLogin(){
        String email = mEmailField.getText().toString().trim();
        String password = mPasswordField.getText().toString().trim();

        if(!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password))
        {   mProgress.setMessage("Processando...");
            mProgress.show();
            mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful())
                    {
                       // checkUserExist();
                        mProgress.dismiss();
                        if (mAuth.getCurrentUser().isEmailVerified())
                        {
                            //starts main activity //temporario
                            Intent mainIntent = new Intent(login_activity.this, main_activity.class);
                            mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(mainIntent);
                        }
                        else
                            Toast.makeText(login_activity.this, "Verifique email ",Toast.LENGTH_LONG).show();

                    }
                    else
                    {
                        Toast.makeText(login_activity.this, "Erro login",Toast.LENGTH_LONG).show();
                        mProgress.dismiss();
                    }

                }
            });
        }
    }
    private void startRegister() {
        final String email = mEmailField.getText().toString().trim();
        String password = mPasswordField.getText().toString().trim();
        final String ActivationCode = mActivationCodeField.getText().toString().trim();

        //check if there is not empty fields
        if(!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password) && !TextUtils.isEmpty(ActivationCode))
        {   mProgress.setMessage("Registrando ...");
            mProgress.show();
            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if(task.isSuccessful())
                    {
                        String user_id = mAuth.getCurrentUser().getUid();

                        DatabaseReference current_user_db = mDatabase.child(ActivationCode); //acessa o conteudo p/ aquela placa daquele usuario
                        DatabaseReference user_information = mDatabase.child("Personal_Info");

                        current_user_db.child("users").child(user_id).child("User email").setValue(email);
                        current_user_db.child("users").child(user_id).child("Profile Image").setValue("default");
                        user_information.child(user_id).child("Code").setValue(ActivationCode);

                        mAuth.getCurrentUser().sendEmailVerification()
                                .addOnCompleteListener(login_activity.this, new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(login_activity.this,
                                                    "Verification email sent to " + mAuth.getCurrentUser().getEmail(),
                                                    Toast.LENGTH_SHORT).show();
                                        } else {
                                            Log.e(TAG, "sendEmailVerification", task.getException());
                                            Toast.makeText(login_activity.this,
                                                    "Falha ao enviar email de verificação",
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

                        // envia verificação de email
                        mProgress.dismiss();

                    }
                }
            });
        }
        else
        {
            if(TextUtils.isEmpty(email))
            {

            Toast.makeText(login_activity.this,"Email em branco",Toast.LENGTH_LONG).show();

                mEmailField.setHintTextColor(ContextCompat.getColor(this, R.color.HintTextHighlight));
            }
            if(TextUtils.isEmpty(password))
            {

                Toast.makeText(login_activity.this,"Senha em branco",Toast.LENGTH_LONG).show();
                mPasswordField.setHintTextColor(ContextCompat.getColor(this, R.color.HintTextHighlight));
            }
            if(TextUtils.isEmpty(ActivationCode))
            {

                Toast.makeText(login_activity.this,"Coódigo de ativação em branco",Toast.LENGTH_LONG).show();
                mActivationCodeField.setHintTextColor(ContextCompat.getColor(this, R.color.HintTextHighlight));
            }


        }
    }

/*    private void checkUserExist()
    {
        final String user_id = mAuth.getCurrentUser().getUid();
        final String ActivationCode = mActivationCodeField.getText().toString().trim();

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.child(ActivationCode).hasChild(user_id))
                {
                    Intent mainIntent = new Intent(login_activity.this, main_activity.class);
                    mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); //impede usuario de voltar usando back
                    startActivity(mainIntent);
                }
    *//*            else
                {
                    Intent setupIntent = new Intent(login_activity.this, setupActivity.class);
                    setupIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); //impede usuario de voltar usando back
                    startActivity(setupIntent);
                    Toast.makeText(login_activity.this,"Você deve configurar sua conta",Toast.LENGTH_LONG);
                }*//*

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }*/

}
