package com.example.firebasedatabase;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    EditText editText;
    Button button;

    String playerName = "";

    FirebaseDatabase database;
    DatabaseReference playerRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText = (EditText) findViewById(R.id.editTextMultiplayer);
        button = (Button) findViewById(R.id.buttonLoginMultiplayer);

        database = FirebaseDatabase.getInstance();

        //check if the player exists and get reference
        SharedPreferences preferences = getSharedPreferences("PREFS", 0);
        playerName = preferences.getString("playerName","");
        if(!playerName.equals("")){
            playerRef = database.getReference("players/" + playerName);
            addEventListener();
            playerRef.setValue("");
        }

        button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                //logging the player in
                playerName = editText.getText().toString();
                editText.setText("");
                if(!playerName.equals("")){
                    button.setText("LOGGING IN");
                    button.setEnabled(false);
                    playerRef = database.getReference("players/" + playerName);
                    addEventListener();
                    playerRef.setValue("");
                }
            }
        });

    }

    private void addEventListener(){
        //READ FROM DATABASE //addValueEventListener è un evento di firebase e si attiva ogni volta che viene fatta una query su playerRef
        playerRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                /*Un'istanza DataSnapshot contiene dati da un percorso del database Firebase. Ogni volta che leggi i dati del database, ricevi i dati come DataSnapshot.
                  DataSnapshots sono passati ai metodi di ascoltatori che si collega con addValueEventListener(ValueEventListener), addChildEventListener(ChildEventListener)o
                  addListenerForSingleValueEvent(ValueEventListener).
                  Sono copie immutabili dei dati generate in modo efficiente in una posizione del database Firebase. Non possono essere modificati e non cambieranno mai.
                  Per modificare i dati in una posizione, utilizzare un DatabaseReference riferimento (ad es. Con setValue(Object)).
                */
                //success - continue to the next screen after saving the player name
                if(!playerName.equals("")) {
                    SharedPreferences preferences = getSharedPreferences("PREFS", 0);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("playerName", playerName);
                    editor.apply();
                    /*Ho notato che è importante scrivere la differenza tra commit() e anche apply()qui.
                        -commit()ritorna truese il valore è stato salvato con successo altrimenti false.
                            Salva i valori in SharedPreferences in modo sincrono .
                        -apply()è stato aggiunto in 2.3 e non restituisce alcun valore in caso di successo o fallimento.
                            Salva immediatamente i valori in SharedPreferences ma avvia un commit asincrono
                     */

                    startActivity(new Intent(getApplicationContext(), Main2Activity.class));
                    /*Un intento fornisce una funzione per eseguire l'associazione in fase di runtime tra il codice in diverse applicazioni.
                    Il suo uso più significativo è nel lancio di attività, dove può essere considerata la colla tra le attività.
                    È fondamentalmente una struttura di dati passivi che contiene una descrizione astratta di un'azione da eseguire
                    * */

                    /*se si vuole far terminare l'app quando si ritorna dal contesto di MAIN 2 ACTIVITY*/
                    //finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //error
                button.setText("LOG IN");
                button.setEnabled(true);
                Toast.makeText(MainActivity.this, "Error!", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
