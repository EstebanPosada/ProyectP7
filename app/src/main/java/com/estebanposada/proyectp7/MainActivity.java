package com.estebanposada.proyectp7;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, View.OnClickListener{

    //Facebook
    CallbackManager callbackManager;
    TextView details;
    LoginButton loginButton;

    //Google+
    private static final String TAG = "SignInActivity";
    private static final int RC_SIGN_IN = 9001;

    private GoogleApiClient mGoogleApiClient;
    private TextView mStatusUser, mStatusEmail;
    private ProgressDialog mProgressDialog;

    //DataBase
    SQLiteDatabase mydatabase;
    EditText user;
    EditText passw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_main);


        //Google+
        mStatusUser = (TextView) findViewById(R.id.id_tvStatusUser);
        mStatusEmail = (TextView) findViewById(R.id.id_tvStatusEmail);

        findViewById(R.id.id_sign_in_button).setOnClickListener(MainActivity.this);
        findViewById(R.id.id_sign_out_button).setOnClickListener(this);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        //DataBase

//Facebook
        loginButton = (LoginButton) findViewById(R.id.btnlogin);
        details = (TextView) findViewById(R.id.dataStatus);

        callbackManager = CallbackManager.Factory.create();
        loginButton.setReadPermissions("public_profile email");
        details.setVisibility(View.INVISIBLE);

        loginButton.setReadPermissions("user_status");
        //loginButton.registerCallback(callbackManager, callback);
        if (AccessToken.getCurrentAccessToken() != null){
            details.setVisibility(View.VISIBLE);
            RequestData();
        }
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AccessToken.getCurrentAccessToken() != null) {
                    details.setVisibility(View.INVISIBLE);
                }
            }
        });
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                if (AccessToken.getCurrentAccessToken() != null){
                    details.setVisibility(View.VISIBLE);
                    RequestData();

                }
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });

        //Data Base
        fromstart();
        user = (EditText) findViewById(R.id.idname);
        passw = (EditText) findViewById(R.id.idpass);

    }

    private void fromstart(){
        DatabaseCreatorHelper myhelp = new DatabaseCreatorHelper(this, "baseDatos2", null,1);
        mydatabase = myhelp.getWritableDatabase();
        Toast.makeText(this, "Nombre: " + myhelp.getDatabaseName().toString(), Toast.LENGTH_SHORT).show();
    }

    public void log_in(View v) {
        String tabla = "baseDatos2";
        String [] columnas = {"nombreCom"};     //, "pass", "correo", "tel"
        String [] selectionAps = {user.getText().toString(), passw.getText().toString()};
        String selection = "nombreCom = ?" + "and" + " pass = ?";
        Cursor c = mydatabase.query(tabla, columnas, selection, selectionAps, null, null, null);
        String resul = "";

        if (c.moveToFirst()){
            resul = "Login succeded: ";
            do{
                for (int i = 0; i<c.getColumnCount(); i++){

                    resul = resul +" "+ c.getString(i);
                }
            } while (c.moveToNext());
        } else
            resul = "No existe dato";
        Toast.makeText(this, resul.toString(), Toast.LENGTH_SHORT).show();
        user.setText("");
        passw.setText("");

        //t.setText(resul.toString());
        //e.setText("");
    }


    public void formu (View v) {
        Intent in = new Intent(this, dataMybase.class);
        startActivityForResult(in, 1);
        //   startActivity(new Intent(getApplicationContext(), DataBase.class));
    }

    public void RequestData(){
        GraphRequest request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
                JSONObject json = response.getJSONObject();
                try {
                    if (json != null){
                        String text = "<b>Name :</b> "+json.getString("name");
                        details.setText(Html.fromHtml(text));
                    }
                } catch (JSONException e){
                    e.printStackTrace();
                }
            }
        });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "name");
        request.setParameters(parameters);
        request.executeAsync();
    }


//Google+

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.id_sign_in_button:
                signIn();
                break;
            case R.id.id_sign_out_button:
                signOut();
                break;
        }

    }
    private void signOut(){
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        updateUI(false);
                    }
                }
        );
    }
    private void signIn(){
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Database
        if (requestCode == 1){
            if (resultCode == 1) {
                String nameIn = data.getStringExtra("Xname");
                String passIn = data.getStringExtra("Xpass");
                String emailIn = data.getStringExtra("Xemail");
                String phoneIn = data.getStringExtra("Xtel");
                String tabla = "baseDatos2";
                ContentValues value = new ContentValues();
                value.put("nombreCom",nameIn);
                value.put("pass",passIn);
                value.put("correo",emailIn);
                value.put("tel",phoneIn);
                if (mydatabase.insert(tabla,null,value) == -1){
                    Toast.makeText(getApplicationContext().getApplicationContext(), "Error, valor insertado erroneo", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext().getApplicationContext(), "Éxito, nuevo usuario ingresado", Toast.LENGTH_SHORT).show();
                }
            }
        }

        //Google+
        callbackManager.onActivityResult(requestCode, resultCode, data);


        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult: " + result.isSuccess());
        if (result.isSuccess()) {
            GoogleSignInAccount acct = result.getSignInAccount();
            mStatusUser.setText(getString(R.string.signed_in1, acct.getDisplayName()));
            mStatusEmail.setText(getString(R.string.signed_in2, acct.getEmail()));
            updateUI(true);
        } else {
            updateUI(false);
        }
    }
    private void updateUI(boolean signedIn) {
        if (signedIn) {
            Toast.makeText(MainActivity.this, "In", Toast.LENGTH_SHORT).show();
            findViewById(R.id.id_sign_in_button).setVisibility(View.GONE);
            findViewById(R.id.id_sign_out_button).setVisibility(View.VISIBLE);
        } else {
            Toast.makeText(MainActivity.this, "Out", Toast.LENGTH_SHORT).show();
            mStatusUser.setText(R.string.signed_out);
            mStatusEmail.setText(R.string.signed_out2);
            findViewById(R.id.id_sign_in_button).setVisibility(View.VISIBLE);
            findViewById(R.id.id_sign_out_button).setVisibility(View.GONE);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
        if (opr.isDone()){
            Log.d(TAG, "Got cached ssign-in");
            GoogleSignInResult result = opr.get();
            handleSignInResult(result);
        } else {
            showProgressDialog();
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(GoogleSignInResult result) {
                    hideProgressDialog();
                    handleSignInResult(result);
                }
            });
        }
    }
    private void showProgressDialog(){
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(getString(R.string.loading));
            mProgressDialog.setIndeterminate(true);
        }
        mProgressDialog.show();
    }
    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.hide();
        }
    }
}
