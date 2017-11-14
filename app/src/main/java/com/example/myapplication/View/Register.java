package com.example.myapplication.View;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.myapplication.R;

import java.util.ArrayList;
import java.util.List;

public class Register extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    String user;
    String firstName;
    String lastName;
    String password;
    String rpassword;
    String numpers;
    EditText userEditText;
    EditText passwordEditText;
    EditText repeatedPasswordEditText;
    EditText firstNameEditText;
    EditText lastNameEditText;
    Spinner numpersSpinner;

    public void okClick (View view) {
        user = userEditText.getText().toString();
        password = passwordEditText.getText().toString();
        rpassword = repeatedPasswordEditText.getText().toString();
        firstName = firstNameEditText.getText().toString();
        lastName = lastNameEditText.getText().toString();
        if (password.equals(rpassword)) {
            Intent me = getIntent();
            me.putExtra("user", user);
            me.putExtra("password", password);
            me.putExtra("firstName", firstName);
            me.putExtra("lastName", lastName);
            me.putExtra("numpers", numpers);
            setResult(RESULT_OK, me);
            finish();
        }   else {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
        }
    }

    public void cancelClick (View view) {
        Intent me = getIntent();
        me.putExtra("user", "");
        me.putExtra("password", "");
        me.putExtra("firstName", "");
        me.putExtra("lastName", "");
        me.putExtra("numpers", "");
        setResult(RESULT_CANCELED, me);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        numpers = "1";
        userEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        repeatedPasswordEditText = findViewById(R.id.repeatedPasswordEditText);
        firstNameEditText = findViewById(R.id.firstNameEditText);
        lastNameEditText = findViewById(R.id.lastNameEditText);
        numpersSpinner = findViewById(R.id.numpersSpinner);

        List<String> list = new ArrayList<String>();
        for (int i = 1; i < 10; i++) list.add(String.valueOf(i));
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        numpersSpinner.setAdapter(dataAdapter);
        numpersSpinner.setOnItemSelectedListener(this);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        numpers = adapterView.getItemAtPosition(i).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        numpers = "1";
    }
}
