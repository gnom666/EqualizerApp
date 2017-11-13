package com.example.myapplication;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    public Person person;
    long id = 1;
    TextView textView;
    Button checkButton;
    EditText editText;
    PersonServices personServices;
    EditText multiLineTextView;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        personServices = new PersonServices();
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
        personServices.userById(1, this, textView);

    }
}
