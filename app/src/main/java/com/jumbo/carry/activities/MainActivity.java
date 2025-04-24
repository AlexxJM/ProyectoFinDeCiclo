package com.jumbo.carry.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.auth.User;
import com.jumbo.carry.R;
import com.jumbo.carry.models.Usuario;
import com.jumbo.carry.providers.AuthProvider;
import com.jumbo.carry.providers.UsersProvider;

import java.util.HashMap;
import java.util.Map;

import dmax.dialog.SpotsDialog;

public class MainActivity extends AppCompatActivity {
    //--------elementos para fondo de video
    VideoView fondoAnimado;
    MediaPlayer mediaPlayerr;
    int posicion;
    //--------elementos para activar registrar
    TextView txtRegistrar;
    //-------elementos para iniciar sesión
    TextInputEditText mTextInputEmail;
    TextInputEditText mTextInputContrasena;
    Button mBotonLogin;
    AuthProvider mAuthProvider;
    AlertDialog mDialog;
    //-------elementos para iniciar sesión con Google
    private static final String TAG = "MainActivity";
    private static final int RC_SIGN_IN = 9001;
    private GoogleSignInClient mGoogleSignInClient;
    SignInButton mButtonGoogle;
    UsersProvider mUsersProvider;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //---------------------------------------------FONDO ANIMADO-------------------------------------
        //creamos un videoView
        fondoAnimado = findViewById(R.id.vv_fondo);
        //Escribimos la ruta del video
        Uri uri = Uri.parse("android.resource://"+getPackageName()+"/"+R.raw.fondoanimadocarryy);
        //Añadimos la ruta al videoView
        fondoAnimado.setVideoURI(uri);
        fondoAnimado.start();
        //para que el video se repita constantemente
        fondoAnimado.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                mediaPlayerr= mediaPlayer;
                mediaPlayerr.setLooping(true);
                if(posicion!=0){
                    mediaPlayerr.seekTo(posicion);
                    mediaPlayerr.start();
                }
            }
        });

        //-------------------------------------------REGISTRAR USUARIO-------------------------------------
        txtRegistrar = findViewById(R.id.txtRegistrar);
        txtRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,RegisterActivity.class);
                startActivity(intent);
            }
        });

        //------------------------------------------INICIAR SESIÓN-------------------------------------
        mTextInputEmail=findViewById(R.id.txtCorreoRegistro);
        mTextInputContrasena=findViewById(R.id.txtContraRegistro);
        mBotonLogin=findViewById(R.id.btnRegistrarse);
        mAuthProvider=new AuthProvider();
        mUsersProvider=new UsersProvider();
        mDialog= new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Iniciando sesión...").setTheme(R.style.Custom)
                .setCancelable(false).build();

        mBotonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });
        //------------------------------------INICIAR SESIÓN CON GOOGLE---------------------------------
        mButtonGoogle= findViewById(R.id.btnGoogle);
        mButtonGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
            }
        }
    }
    // [END onactivityresult]

    // [START auth_with_google]
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        mDialog.show();
        mAuthProvider.googleLogin(acct).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            String id = mAuthProvider.getUid();
                            // Sign in success, update UI with the signed-in user's information
                            usuarioExistente(id);
                        } else {
                            mDialog.dismiss();

                            // If sign in fails, display a message to the user.
                            Toast.makeText(MainActivity.this,"No se pudo almacenar el usuario",Toast.LENGTH_LONG).show();
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                        }
                    }
                });
    }

    private void usuarioExistente(final String id) {
        mUsersProvider.getUsuario(id).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()){
                    mDialog.dismiss();
                    Log.d(TAG, "signInWithCredential:success");
                    Intent intent= new Intent(MainActivity.this,HomeActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
                else{
                    String email= mAuthProvider.getEmail();
                    Map<String, Object> map= new HashMap<>();
                    map.put("email",email);
                    Usuario usuario = new Usuario();
                    usuario.setEmail(email);
                    usuario.setId(id);

                   mUsersProvider.crear(usuario).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            mDialog.dismiss();
                            if(task.isSuccessful()){
                                Intent intent= new Intent(MainActivity.this,CompletarPerfil.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                            else{
                                Toast.makeText(MainActivity.this,"No se pudo registrar al usuario",Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            }
        });
    }
    // [END auth_with_google]

    // [START signin]
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void login() {
        if(!mTextInputEmail.getText().toString().isEmpty()&&!mTextInputContrasena.getText().toString().isEmpty()){
            mDialog.show();
            mAuthProvider.login(mTextInputEmail.getText().toString(),mTextInputContrasena.getText().toString())
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            mDialog.dismiss();
                            if(task.isSuccessful()){
                                Intent intent= new Intent(MainActivity.this,HomeActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                            else{
                                Toast.makeText(MainActivity.this,"Correo o contraseña incorrecto",Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        }
        else Toast.makeText(MainActivity.this,"Rellena todos los campos",Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        posicion=mediaPlayerr.getCurrentPosition();
        fondoAnimado.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        fondoAnimado.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mediaPlayerr.release();
        mediaPlayerr=null;
    }
}