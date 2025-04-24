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

public class CompletarPerfil extends AppCompatActivity {
    //--------elementos para fondo de video
    VideoView fondoAnimado;
    MediaPlayer mediaPlayerr;
    int posicion;
    //--------elementos para registrarse
    TextInputEditText mTxtUsuarioRegistro;
    TextInputEditText mTxtNumeroRegistro;
    Button mBtnRegistrar;
    AuthProvider mAuthProvider;
    UsersProvider mUsersProvider;
    AlertDialog mDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_completar_perfil);

        //---------------------------------------------FONDO ANIMADO-------------------------------------
        //creamos un videoView
        fondoAnimado = findViewById(R.id.vv_fondo2);
        //Escribimos la ruta del video
        Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.fondoanimadocarryy);
        //Añadimos la ruta al videoView
        fondoAnimado.setVideoURI(uri);
        fondoAnimado.start();
        //para que el video se repita constantemente
        fondoAnimado.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                mediaPlayerr = mediaPlayer;
                mediaPlayerr.setLooping(true);
                if (posicion != 0) {
                    mediaPlayerr.seekTo(posicion);
                    mediaPlayerr.start();
                }
            }
        });
        //----------------------------------------REGISTRARSE--------------------------------
        mTxtUsuarioRegistro = findViewById(R.id.txtUsuarioRegistro2);
        mTxtNumeroRegistro=findViewById(R.id.txtNumeroRegistro);
        mBtnRegistrar = findViewById(R.id.btnCompletarPerfil);
        mAuthProvider = new AuthProvider();
        mUsersProvider = new UsersProvider();
        mDialog= new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Iniciando sesión...").setTheme(R.style.Custom)
                .setCancelable(false).build();


        mBtnRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                register();
            }
        });
    }

    private void register() {
        String numbreUsuario = mTxtUsuarioRegistro.getText().toString();
        String numero= mTxtNumeroRegistro.getText().toString();

        if (!numbreUsuario.isEmpty()) {
            if(numeroValido(numero)){
                actualizarUsuario(numbreUsuario,numero);
            }else{
                Toast.makeText(this, "El número no es válido", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(this, "Escribe un nombre de usuario", Toast.LENGTH_LONG).show();
        }
    }
    public boolean numeroValido(String numero) {
        String expression = "^[6-9]\\d{8}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(numero);
        return matcher.matches();
    }

    private void actualizarUsuario(final String usuario,final String numero) {
        mDialog.show();
        String id = mAuthProvider.getUid();
        Usuario usuario1 = new Usuario();
        usuario1.setUsuario(usuario);
        usuario1.setId(id);
        usuario1.setNumero(numero);
        usuario1.setFechaCreacion(new Date().getTime());
        mUsersProvider.actualizar(usuario1).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                mDialog.dismiss();
                if (task.isSuccessful()) {
                    Intent intent = new Intent(CompletarPerfil.this, HomeActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                } else {
                    Toast.makeText(CompletarPerfil.this, "No se pudo almacenar el usuario", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        posicion = mediaPlayerr.getCurrentPosition();
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
        mediaPlayerr = null;
    }
}
