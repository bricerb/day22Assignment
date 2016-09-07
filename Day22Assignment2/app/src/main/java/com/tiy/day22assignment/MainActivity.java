package com.tiy.day22assignment;

import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemLongClickListener {
    ListView list;
    EditText text;
    Button addButton;

    ArrayAdapter<String> items;
    ClientRunner myClient = new ClientRunner();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        list = (ListView) findViewById(R.id.listView);
        text = (EditText) findViewById(R.id.editText);
        addButton = (Button) findViewById(R.id.button);

        items = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        list.setAdapter(items);

        addButton.setOnClickListener(this);
        list.setOnItemLongClickListener(this);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }

    @Override
    public void onClick(View v) {
        String item = text.getText().toString();
        if (item.equals("history")) {
            item = "tx-hist-start";
        }
        System.out.println(item);

        myClient.sendMessage(item);

        items.add(item);
        text.setText("");
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        String item = items.getItem(position);
        items.remove(item);
        return true;
    }

    public class ClientRunner {

        public void sendMessage (String message) {

            try {
                Socket clientSocket = new Socket("10.0.0.129", 8005);

                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                out.println(message);

                String incomingMessage = in.readLine();

                if (message.equals("tx-hist-start")) {
                    while (incomingMessage != "tx-hist-done") {
                        items.add(incomingMessage);
                        incomingMessage = in.readLine();
                    }
                }

                clientSocket.close();

            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

}

