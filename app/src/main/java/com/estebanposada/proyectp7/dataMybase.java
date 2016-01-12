package com.estebanposada.proyectp7;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class dataMybase extends AppCompatActivity {

    EditText nombrec, pass, email, phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_mybase);

        nombrec = (EditText) findViewById(R.id.eNombre);
        pass = (EditText) findViewById(R.id.ePass);
        email = (EditText) findViewById(R.id.eCorreo);
        phone = (EditText) findViewById(R.id.eTel);
    }

    public void back2main(View view) {
        Intent b2m = new Intent(this, MainActivity.class);
        b2m.putExtra("Xname", nombrec.getText().toString());
        b2m.putExtra("Xpass", pass.getText().toString());
        b2m.putExtra("Xemail", email.getText().toString());
        b2m.putExtra("Xtel", phone.getText().toString());
        setResult(1, b2m);
        finish();
    }
}
