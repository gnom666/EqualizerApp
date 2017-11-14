package com.example.myapplication.View;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.myapplication.Controller.PersonServices;
import com.example.myapplication.Model.Person;
import com.example.myapplication.R;

public class MainActivity extends AppCompatActivity {

    public Person person;
    long id = 1;
    TextView textView;
    Button checkButton;
    EditText editText;
    PersonServices personServices;
    EditText multiLineTextView;
    int rCode = 10;
    String token;

    public void onCheckClick (View view) {
        try {
            id = Long.decode(editText.getText().toString());
        }   catch (Exception e) {
            id = 1;
        }
        if (id < 1 || id > 4) id = 1;
        personServices.userById(id, this, textView, multiLineTextView);
        editText.setText("");
    }

    public void onAllClick (View view) {
        Log.i("info" , "pasa por aqui");
        personServices.usersList(this, multiLineTextView);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        token = "";

        Intent loginIntent = new Intent(getApplicationContext(), LogIn.class);
        startActivityForResult(loginIntent, rCode);

/*        personServices = new PersonServices();
        textView = findViewById(R.id.textView);
        editText = findViewById(R.id.idEditText);
        checkButton = findViewById(R.id.checkButton);
        multiLineTextView = findViewById(R.id.multiLineTextView);
        checkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onCheckClick(view);
            }
        });
        personServices.userById(1, this, textView);*/

    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent loginIntent) {
        super.onActivityResult(requestCode, resultCode, loginIntent);
        if(requestCode == rCode && resultCode == RESULT_OK) {
            Log.i("u:p", loginIntent.getStringExtra("user") + ":" + loginIntent.getStringExtra("password"));
            finish();
        }
    }
}
