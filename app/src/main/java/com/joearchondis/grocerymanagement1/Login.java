package com.joearchondis.grocerymanagement1;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class Login extends AppCompatActivity {

    EditText pass, user;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        user = (EditText) findViewById(R.id.i_userName);
        pass = (EditText) findViewById(R.id.i_password);

    }

    public void loginBtn (View view) {
        String userName = user.getText().toString();
        String password = pass.getText().toString();

    }


}
