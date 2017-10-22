package com.vacunasdomaincompany.vacunasapp;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.bumptech.glide.*;
import com.google.android.gms.common.api.Status;
import com.vacunasdomaincompany.vacunasapp.objetos.web.service.Usuario;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private ImageView photoImageView;
    private TextView nameTextView;
    private TextView emailTexView;
    private TextView idTextView;
    private Usuario usuario = null;

    private GoogleApiClient googleApiClient;

    public static final String NOT_LOG_OUT = "No se pudo cerrar sesión";
    public static final String NOT_REVOKE = "No se pudo revocar el acceso";
    public static final String ipServer = "192.168.43.3";
    public static final String portServer = "8080";
    public static final String dirWebServerUsuario = "/control_vacunas_web_service/webresources/pkg_entidad.usuario/usuario/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        photoImageView = (ImageView) findViewById(R.id.photoImageView);
        nameTextView = (TextView) findViewById(R.id.nameTextView);
        emailTexView = (TextView) findViewById(R.id.emailTextView);
        idTextView = (TextView) findViewById(R.id.idTextView);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }

    @Override
    protected void onStart() {
        super.onStart();

        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(googleApiClient);
        if (opr.isDone()) {
            GoogleSignInResult result = opr.get();
            if (validarUsuario(result)){
                handleSignInResult(result);
            }else{
                Toast.makeText(getApplicationContext(), "Usuario no registrado!", Toast.LENGTH_SHORT).show();
                goLogInScreen();
            }
        } else {
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(@NonNull GoogleSignInResult googleSignInResult) {
                    handleSignInResult(googleSignInResult);
                }
            });
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            GoogleSignInAccount account = result.getSignInAccount();
            nameTextView.setText(account.getDisplayName());
            emailTexView.setText(account.getEmail());
            Glide.with(this).load(account.getPhotoUrl()).into(photoImageView);
        } else {
            goLogInScreen();
        }
    }

    private void goLogInScreen() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    public void LogOut(View view) {
        Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {
                if (status.isSuccess()) {
                    goLogInScreen();
                } else {
                    Toast.makeText(getApplicationContext(), NOT_LOG_OUT, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    public boolean validarUsuario(GoogleSignInResult result){
        if (result.isSuccess()) {
            GoogleSignInAccount account = result.getSignInAccount();
            String email = account.getEmail();
            pedirDatos("http://"+ipServer+":"+portServer+dirWebServerUsuario+email);
            return true;
        } else {
            return false;
        }
    };

    public void pedirDatos(String uri){
        MyAsynTask task = new MyAsynTask();
        task.execute(uri);
        //Log.e("GET_USER:", task.getUser().toString());
    }

    private class MyAsynTask extends AsyncTask<String, String, String> {
        private Usuario user = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            String content = HttpManager.getData(params[0]);
            this.user = HttpManager.getUsuario(params[0]);
            //Log.e("GET_USER", this.user.toString());
            return content;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s == null){
                Toast.makeText(getApplicationContext(), "El usuario no esta registrado en la base de datos!", Toast.LENGTH_SHORT).show();
                goLogInScreen();
            }
        }

        public Usuario getUser(){
            return this.user;
        }
    }

    public void cargarDatos(String dato){
        idTextView.append(dato + "\n");
    }

}