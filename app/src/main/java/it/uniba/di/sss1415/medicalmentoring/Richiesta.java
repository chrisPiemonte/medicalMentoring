package it.uniba.di.sss1415.medicalmentoring;

import android.app.Dialog;
import android.app.ListActivity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import org.json.JSONObject;
import org.w3c.dom.Text;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class Richiesta extends AppCompatActivity {

    String from,to,data;
    ListView list;

    String param ;
    final String TIPO_ELEMENTO = "dateDisp";
    final String ACCESSO = "read";
    ArrayList<HashMap<String,String>> listaApp = new ArrayList<HashMap<String,String>>();

    public ListAdapter adapter;


    //variabili per l utilizzo del dialog
    final Dialog dialog = new Dialog(this);
    Button ok = (Button) dialog.findViewById(R.id.okBTN);
    Button annulla = (Button) dialog.findViewById(R.id.annullaBTN);
    TextView message = (TextView) findViewById(R.id.richiestaTV);
    final String TEXT = "Nessun risultato trovato. Inviare la richiesta al server per la ricerca automatica ?";





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_richiesta);

        //per il dialog
        dialog.setContentView(R.layout.dialog_richiesta);

        Intent ricevuto = getIntent();
        from = ricevuto.getStringExtra("oraInizio");
        to = ricevuto.getStringExtra("oraFine");
        data = ricevuto.getStringExtra("data");
        list = (ListView) findViewById(R.id.list);

        Parametri diz = new Parametri("dateDisp");
        diz.value = new String[]{data,from,to,"","",""};
        //diz.toJsonObj().toString()
        param = Parametri.generaParametri(TIPO_ELEMENTO, ACCESSO, "");
        String serverAnswer = ServerManager.sendRequest("POST",param);
        listaApp = JSONManager.toListOfMap(serverAnswer, diz.chiaveAccesso);
        listaApp = controllaJson(listaApp);
        //se non sono stati trovati interventi relativi alla ricerca, mandiamo un dialog
        if (listaApp == null){
            message.setText(TEXT);
            ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    tornaHome();
                    dialog.dismiss();

                }
            });
            annulla.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    tornaHome();
                    dialog.dismiss();

                }
            });
            dialog.show();


        }
        else {
            adapter = new SimpleAdapter(Richiesta.this,
                    listaApp,
                    R.layout.item_richiesta,
                    new String[]{"data", "oraInizio", "oraFine", "nomeT", "cognomeT", "scoreT"},
                    new int[]{R.id.dataTV, R.id.oraInizioTV, R.id.oraFineTV,
                            R.id.nomeTTV, R.id.cognomeTTV, R.id.scoreTTV});
            list.setAdapter(adapter);

        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_richiesta, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public  ArrayList<HashMap<String,String>> controllaJson(ArrayList<HashMap<String,String>> s){
        SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date checkData ;
        Date dataJson;
        ArrayList<HashMap<String,String>> temp = new ArrayList<HashMap<String,String>>();
        for ( int i = 0 ; i < s.size();i++) {

            try {

                checkData = timeFormat.parse(data);
                dataJson = timeFormat.parse(s.get(i).get("data"));
                if (dataJson.compareTo(checkData) == 0) {
                    temp.add(s.get(i));
                }
                ;
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        if(!temp.isEmpty()){
            return temp;
        }
        return null;






    }

    public void tornaHome(){
        Intent back = new Intent(Richiesta.this, Home.class);
        startActivity(back);
    }


}
