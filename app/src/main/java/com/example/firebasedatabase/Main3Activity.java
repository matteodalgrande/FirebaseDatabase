package com.example.firebasedatabase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Main3Activity extends AppCompatActivity {

    Button button ;

    String playerName = "";
    String roomName = "";
    String role = "";
    String message = "";

    FirebaseDatabase database;
    DatabaseReference messageRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);

        button = (Button) findViewById(R.id.buttonPoke);
        button.setEnabled(false);

        database = FirebaseDatabase.getInstance();

        SharedPreferences preferences = getSharedPreferences("PREFS", 0);
        playerName = preferences.getString("playerName","");

        Bundle extras = getIntent().getExtras();
        if(extras != null){
            roomName = extras.getString("roomName");
            if(roomName.equals(playerName)){
                role = "host";
            } else {
                role = "guest";
            }
        }

        /*setto il valore dei bottoni con false*/
        button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                //send message
                button.setEnabled(false);
                message = role + ":Poke!";
                messageRef.setValue(message);
            }
        });

        //listen for incoming message
        messageRef = database.getReference("rooms/" + roomName + "/message");
        message = role + ":Poke!";
        messageRef.setValue(message);
        addRoomEventListener();
    }

    //se cambia lo stato del database allora succede che parte un poke[quindi anche quando due utenti sono online e uno entra nella partita dell'altro, anche l'altro verrà richiamato a giocare]
    private void addRoomEventListener(){
        messageRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //message received
                if(role.equals("host")){//se sono l'host
                    if(dataSnapshot.getValue(String.class).contains("guest:")){
                        button.setEnabled(true);
                        //stampa poke
                        Toast.makeText(Main3Activity.this, "" + dataSnapshot.getValue(String.class).replace("guest:", ""), Toast.LENGTH_SHORT).show();
                    }
                }else{//se sono il guest
                    if(dataSnapshot.getValue(String.class).contains("host:")){
                        button.setEnabled(true);
                        //stampa poke
                        Toast.makeText(Main3Activity.this, "" + dataSnapshot.getValue(String.class).replace("host:", ""), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}