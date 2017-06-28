package sel373android.leonardocarreira.com.br.voice;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by leonardoboscocarreira on 27/05/17.
 */
public class MappApplication extends Application
{
    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
