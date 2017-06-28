package sel373android.leonardocarreira.com.br.voice;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class status_activity extends AppCompatActivity

{

    private RecyclerView mStatusList;
    private RecyclerView mStatusList2;
    private DatabaseReference mDatabase;
    private String activationCode;



    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);

        String code = getIntent().getStringExtra("activationCode");

        activationCode = code;

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.keepSynced(true);

        mStatusList = (RecyclerView) findViewById(R.id.status_list);
        mStatusList.setHasFixedSize(true);
        mStatusList.setLayoutManager(new LinearLayoutManager(this));

    }

    @Override
    protected void onStart()
    {   super.onStart();


        FirebaseRecyclerAdapter<Object, statusViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Object, statusViewHolder>(
                Object.class,
                R.layout.status_row,
                statusViewHolder.class,
                mDatabase.child("Reg_Boards").child(activationCode).child("Device").child("Luz")
               // mDatabase

        ) {
            @Override
            protected void populateViewHolder(statusViewHolder viewHolder, Object model, int position) {

                viewHolder.setItem("Luz");
                viewHolder.setStatus(model.toString());

            }
        };
        mStatusList.setAdapter(firebaseRecyclerAdapter);


    }

    public static class statusViewHolder extends RecyclerView.ViewHolder
    {
        View mView;

        public statusViewHolder(View itemView) {
            super(itemView);
             mView =itemView;
        }

        public void setItem (String name)
        {
            TextView item_name = (TextView) mView.findViewById(R.id.Item_text);
            item_name.setText(name);
        }

        public void setStatus (String status)
        {
            TextView status_status = (TextView) mView.findViewById(R.id.Status_text);
            status_status.setText(status);
        }

    }

}


