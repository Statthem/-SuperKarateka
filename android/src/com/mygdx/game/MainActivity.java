package com.mygdx.game;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.mygdx.game.UDP.Pinger;
import com.mygdx.game.UDP.UDP_Client;
import com.mygdx.game.UDP.UDP_Server;

import static android.provider.AlarmClock.EXTRA_MESSAGE;


public class MainActivity extends AppCompatActivity {
    public static final String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";

    public static Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getApplicationContext();
        setContentView(R.layout.activity_main);

        Intent intent = new Intent(this, SecondActivity.class);
        UDP_Server server = new UDP_Server();
        UDP_Server.intent = intent;
        UDP_Server.activity = this;
        server.runUdpServer();
    }

    /** Called when the user taps the Send button */
    public void sendMessage(View view) {
        EditText editText = (EditText) findViewById(R.id.editText);
        String message = editText.getText().toString();

        UDP_Client client = new UDP_Client();
        client.Message = message;
        client.NachrichtSenden();
    }

    public void startGame(View view) {
        Intent intent = new Intent(this, AndroidLauncher.class);
        startActivity(intent);
    }

    public void ping(View view){
        Pinger pinger = new Pinger(getApplicationContext());
        pinger.start();
    }

}
