package com.jumbo.carry.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.jumbo.carry.R;
import com.jumbo.carry.models.Usuario;
import com.jumbo.carry.providers.AuthProvider;
import com.jumbo.carry.providers.UsersProvider;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import dmax.dialog.SpotsDialog;

public class RegisterActivity extends AppCompatActivity {
    //--------elementos para fondo de video
    VideoView fondoAnimado;
    MediaPlayer mediaPlayerr;
    int posicion;
    //--------elementos para el botón atras
    Button btnAtras;
    //--------elementos para registrarse
    TextInputEditText mTxtUsuarioRegistro;
    TextInputEditText mTxtEmailRegistro;
    TextInputEditText mTxtContraRegistro;
    TextInputEditText mTxtContra2Registro;
    TextInputEditText mTxtTelefono;
    Button mBtnRegistrar;
    AlertDialog mDialog;
    AuthProvider mAuthProvider;
    UsersProvider mUsersProvider;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

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
        btnAtras = findViewById(R.id.btnAtras);
        mTxtEmailRegistro=findViewById(R.id.txtCorreoRegistro);
        mTxtUsuarioRegistro=findViewById(R.id.txtUsuarioRegistro);
        mTxtContraRegistro=findViewById(R.id.txtContraRegistro);
        mTxtContra2Registro=findViewById(R.id.txtContra2Registro);
        mTxtTelefono=findViewById(R.id.txtNumeroRegistro);
        mBtnRegistrar=findViewById(R.id.btnRegistrarse);
        mAuthProvider = new AuthProvider();
        mUsersProvider = new UsersProvider();
        mDialog= new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Iniciando sesión...").setTheme(R.style.Custom)
                .setCancelable(false).build();

        btnAtras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegisterActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });

        mBtnRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                register();
            }
        });
    }

    private void register() {
        String numbreUsuario = mTxtUsuarioRegistro.getText().toString();
        String email = mTxtEmailRegistro.getText().toString();
        String contra = mTxtContraRegistro.getText().toString();
        String contra2 = mTxtContra2Registro.getText().toString();
        String numero = mTxtTelefono.getText().toString();
        if(!numbreUsuario.isEmpty()&&!email.isEmpty()&&!contra.isEmpty()&&!contra2.isEmpty()&&!numero.isEmpty()){
            if(!correoValido(email)){
                Toast.makeText(this, "El correo no es válido", Toast.LENGTH_LONG).show();
            }else{
                if(!numeroValido(numero)){
                    Toast.makeText(this, "El número no es válido", Toast.LENGTH_LONG).show();
                }else{
                    if(!contra.equals(contra2)){
                        Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_LONG).show();
                    }
                    else{
                        if(contra.length()>=6){
                            crearUsuario(email,contra,numbreUsuario,numero);
                        }
                        else{
                            Toast.makeText(this, "Las contraseña debe tener al menos 6 caracteres", Toast.LENGTH_LONG).show();
                        }
                    }
                }
            }
        }else{
            Toast.makeText(this, "Rellena todos los campos", Toast.LENGTH_LONG).show();
        }
    }

    private void crearUsuario(final String email,final String contra,final String usuario,final String numero) {
        mDialog.show();
        mAuthProvider.register(email,contra).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    String id=mAuthProvider.getUid();
                    Usuario usuario1 = new Usuario();
                    usuario1.setId(id);
                    usuario1.setEmail(email);
                    usuario1.setUsuario(usuario);
                    usuario1.setNumero(numero);
                    usuario1.setFechaCreacion(new Date().getTime());
                    mUsersProvider.crear(usuario1).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                mDialog.dismiss();
                                Toast.makeText(RegisterActivity.this, "Usuario regsitrado correctamente", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(RegisterActivity.this,HomeActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        }
                    });
                }
                else{
                    mDialog.dismiss();
                    Toast.makeText(RegisterActivity.this, "No se ha podido registrar el usuario", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public boolean correoValido(String email) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
    public boolean numeroValido(String numero) {
        String expression = "^[6-9]\\d{8}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(numero);
        return matcher.matches();
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