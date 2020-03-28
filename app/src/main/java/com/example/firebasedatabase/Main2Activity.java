package com.example.firebasedatabase;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Main2Activity extends AppCompatActivity {

    ListView listView;
    Button button;

    List<String> roomsList;

    String playerName = "";
    String roomName = "";

    FirebaseDatabase database;
    DatabaseReference roomRef;
    DatabaseReference roomsRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        database = FirebaseDatabase.getInstance();

        //get the player name and assign his room name to the player name
        SharedPreferences preferences = getSharedPreferences("PREFS",0);
        playerName = preferences.getString("playerName","");
        roomName = playerName;

        listView = (ListView) findViewById(R.id.listViewLobby);
        button = (Button) findViewById(R.id.buttonLobby);

        //all existing available rooms
        roomsList = new ArrayList<>();

        button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                //create room and add yourself as player1
                button.setText("CREATING ROOM");
                button.setEnabled(false);
                roomName = playerName;
                roomRef = database.getReference("rooms/" + roomName + "/player1");
                addRoomEventListener();
                roomRef.setValue(playerName);
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override               //ArrayAdapter adapter = new ArrayAdapter<String>(this,R.layout.ListView,R.id.textView,StringArray);
            public void onItemClick(AdapterView<?> parent, View view, int position, long id){//position è un textView
                //join an existing room and add yourself as player2
                roomName = roomsList.get(position);
                roomRef = database.getReference("rooms/" + roomName + "/player2");//SE IL GIOCATORE RIENTRA E SCEGLIE LA SUA ROOM ALLORA ENTRA COME GIOCATORE 2, ERRORE!
                addRoomEventListener();
                roomRef.setValue(playerName);
            }
        });
        //show if new room is available
        addRoomsEventListener();
    }
        /*passa alla nuova schermata*/
        private void addRoomEventListener(){
        roomRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //join the room
                button.setText("CREATE ROOM");
                button.setEnabled(true);
                Intent intent = new Intent(getApplicationContext(), Main3Activity.class);
                intent.putExtra("roomName", roomName);
                startActivity(intent);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //error
                button.setText("CREATE ROOM");
                button.setEnabled(true);
                Toast.makeText(Main2Activity.this, "Erros!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /*mostra la nuova lista*/
    private void addRoomsEventListener(){
        roomsRef = database.getReference("rooms");
        roomsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //show list of rooms
                roomsList.clear();
                Iterable<DataSnapshot> rooms = dataSnapshot.getChildren();
                for(DataSnapshot snapshot : rooms){
                    roomsList.add(snapshot.getKey());

                    //ArrayAdapter adapter = new ArrayAdapter<String>(this, R.layout.ListView, R.id.textView, StringArray);
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(Main2Activity.this, android.R.layout.simple_list_item_1, roomsList);
                    listView.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //error - nothing
            }
        });
    }
}
