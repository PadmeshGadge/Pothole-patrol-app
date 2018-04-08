package com.example.cleo.camera;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class RegActivity extends AppCompatActivity {

    Button buttonSubmit;
    EditText Name,Username,Email,Password;
    String SERVER_URL = "http://192.168.43.182/Projects/potholefinal/phone/update_info.php";
    //String SERVER_URL = "http://192.168.43.60/app_db/update_info.php";
    AlertDialog.Builder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg);
        buttonSubmit = (Button) findViewById(R.id.buttonSubmit);
        Name = (EditText) findViewById(R.id.editTextName);
        Username = (EditText) findViewById(R.id.editTextUsername);
        Email = (EditText) findViewById(R.id.editTextEmail);
        Password = (EditText) findViewById(R.id.editTextPassword);
        builder = new AlertDialog.Builder(RegActivity.this);
        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String name,username,email,password;
                name = Name.getText().toString();
                username = Username.getText().toString();
                email = Email.getText().toString();
                password = Password.getText().toString();
                StringRequest stringRequest = new StringRequest(Request.Method.POST, SERVER_URL,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                builder.setTitle("Registration successful!");
                                builder.setMessage("You may login with the following "+response);
                                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        /*Name.setText("");
                                        Username.setText("");
                                        Email.setText("");
                                        Password.setText("");*/
                                        Intent intent=new Intent(RegActivity.this,LoginActivity.class);
                                        startActivity(intent);
                                    }
                                });
                                AlertDialog alertDialog=builder.create();
                                alertDialog.show();
                            }
                        }
                        , new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(RegActivity.this,error.toString(),Toast.LENGTH_LONG).show();
                        error.printStackTrace();

                    }
                }){
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String,String> params = new HashMap<String, String>();
                        params.put("name",name);
                        params.put("uname",username);
                        params.put("email",email);
                        params.put("password",password);
                        return params;
                    }
                };
                MySingleton.getInstance(RegActivity.this).addTorequestque(stringRequest);
            }
        });
    }
}
