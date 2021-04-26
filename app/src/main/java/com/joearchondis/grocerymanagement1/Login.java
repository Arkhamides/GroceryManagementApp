package com.joearchondis.grocerymanagement1;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.PersistableBundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.vishnusivadas.advanced_httpurlconnection.PutData;

public class Login extends AppCompatActivity {

    EditText edTxt_username, edTxt_pass;
    Button btn_login;
    ProgressBar progressBar;
    TextView signUp;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        edTxt_username = (EditText) findViewById(R.id.i_userName);
        edTxt_pass = (EditText) findViewById(R.id.i_password);
        btn_login = (Button) findViewById(R.id.buttonLogin);
        progressBar = findViewById(R.id.progress);
        signUp = findViewById(R.id.signUpText);

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LOGIN() ;
            }
        });

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SignUp.class);
                startActivity(intent);
            }
        });

    }
    
    public void LOGIN() {

        final String username, password;
        username = String.valueOf(edTxt_username.getText());
        password = String.valueOf(edTxt_pass.getText());

        if(!username.equals("") && !password.equals("") ) {

            progressBar.setVisibility(View.VISIBLE);

            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {

                    String[] field = new String[2];
                    field[0] = "username";
                    field[1] = "password";
                    //Creating array for data
                    String[] data = new String[2];
                    data[0] = username;
                    data[1] = password;

                    PutData putData = new PutData("http://192.168.0.106/GroceryManagementApp/login.php", "POST", field, data);
                    if (putData.startPut()) {
                        if (putData.onComplete()) {
                            progressBar.setVisibility(View.GONE);

                            String result = putData.getResult();
                            if(result.equals("Login Success")) {


                                Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
                                setUser();
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(intent);
                                finish();

                            } else {
                                Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }
            });


        } else {
            Toast.makeText(getApplicationContext(), "All Fields are required", Toast.LENGTH_LONG).show();
        }

    }

    public void setUser() {
        String UserID, username, email;
        username = String.valueOf(edTxt_username.getText());

        String[] field = new String[1];
        field[0] = "username";

        //Creating array for data
        String[] data = new String[1];
        data[0] = username;


        PutData putData = new PutData("http://192.168.0.106/GroceryManagementApp/getUser.php", "POST", field, data);
        if (putData.startPut()) {
            if (putData.onComplete()) {

                String result = putData.getResult();

                if(result != "-1") {
                    String ArrOfResult[] = result.split(",",-1);
                    String id = ArrOfResult[0];
                    String UserName = ArrOfResult[1];
                    String UserEmail = ArrOfResult[2];
                    User user = new User(id, UserName, UserEmail);

                    ((MyApplication) getApplication()).setSelectedUser(user);

                }

            }
        }

    }




}
