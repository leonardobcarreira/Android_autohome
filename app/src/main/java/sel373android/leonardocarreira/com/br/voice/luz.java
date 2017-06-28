package sel373android.leonardocarreira.com.br.voice;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.firebase.database.Exclude;

/**
 * Created by leonardoboscocarreira on 03/05/17.
 */
public class luz {

    private String status;
    @JsonIgnore
    private String fbKey;



    public luz()
    {

    }

    public luz(String status)
    {
     this.status = status;
    }

    //getters
    public String getStatus() {
        return status;
    }

    @Exclude
    public String getFbKey() {
        return fbKey;
    }

    //setters

    public void setStatus(String status) {
        this.status = status;
    }

    public void setFbKey(String fbKey) {
        this.fbKey = fbKey;
    }


}
