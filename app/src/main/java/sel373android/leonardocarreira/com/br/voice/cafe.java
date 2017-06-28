package sel373android.leonardocarreira.com.br.voice;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Created by leonardoboscocarreira on 22/03/17.
 * Declara Clase café com seu construtor, getter e setter
 * a ser utilizada tanto na obtenção dos dados vindos da Rasp
 * quanto enviados do app para a Rasp
 */

public class cafe {

    private String type;
    @JsonIgnore
    private String fbKey;
  //  @JsonIgnore
  //  private String power;

    public cafe()
{

}
    //constructor
    public cafe (String type)
    {
        this.type = type;
    }


    //Setter
    public  void  setCafe(String type)
    {
        this.type = type;
    }
   /* public void setPower(String power) {
        this.power = power;
    }*/
   public void setFbKey(String fbKey) {
       this.fbKey = fbKey;
   }

    //getter
    public String getCafe()
    {
        return type;
    }

    public String getFbKey() {
        return fbKey;
    }

  /*  public String getPower() {
        return power;
    } */
}
