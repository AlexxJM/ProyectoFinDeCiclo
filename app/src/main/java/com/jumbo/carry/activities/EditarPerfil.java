package com.jumbo.carry.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.storage.UploadTask;
import com.jumbo.carry.R;
import com.jumbo.carry.fragments.PerfilFragment;
import com.jumbo.carry.models.Publicacion;
import com.jumbo.carry.models.Usuario;
import com.jumbo.carry.providers.AuthProvider;
import com.jumbo.carry.providers.ImageProvider;
import com.jumbo.carry.providers.UsersProvider;
import com.jumbo.carry.utils.FileUtil;
import com.jumbo.carry.utils.MensajeVistoHelper;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;
import dmax.dialog.SpotsDialog;

public class EditarPerfil extends AppCompatActivity {
    Button btnAtras;
    Button btnGuardarCambios;
    CircleImageView fotoPerfil;
    ImageView fotoFondo;
    TextInputEditText usuarioAntiguo;
    TextInputEditText telefonoAntiguo;
    File mImagen;
    File mImagen2;
    String usuarioNuevo="";
    String telefonoNuevo="";
    String mImagenPerfil="";
    String mImagenPortada="";
    String fotoPortada2;
    String fotoPerfil2;
    AlertDialog mDialog;
    ImageProvider mImageProvider;
    UsersProvider mUsersProvider;
    AuthProvider mAuthProvider;
    //---------------peticiones
    private final int PETICION_GALERÍA_PERFIL=1;
    private final int PETICION_GALERÍA_PORTADA=2;
    private final int PETICION_FOTO_PERFIL=3;
    private final int PETICION_FOTO_PORTADA=4;
    AlertDialog.Builder mBuilderElegir;
    CharSequence []opciones;
    //-----------foto 1
    String mRutaAbsolutaFoto;
    String mRutaFoto;
    File mFotoFile;
    //-----------foto2
    String mRutaAbsolutaFoto2;
    String mRutaFoto2;
    File mFotoFile2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_perfil);
        fotoPerfil= findViewById(R.id.fotoPerfil);
        fotoFondo=findViewById(R.id.fotoPortada);
        usuarioAntiguo=findViewById(R.id.txtUsuarioEditar);
        telefonoAntiguo=findViewById(R.id.txtNumeroEditar);
        btnAtras= findViewById(R.id.btnAtras);
        btnGuardarCambios=findViewById(R.id.btnActualizarPerfil);
        mBuilderElegir= new AlertDialog.Builder(this).setTitle("Elige una opción");
        opciones= new CharSequence[]{"Abrir galería","Tomar foto"};
        mDialog=new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Actualizando...").setTheme(R.style.Custom)
                .setCancelable(false).build();
        mImageProvider= new ImageProvider();
        mUsersProvider= new UsersProvider();
        mAuthProvider = new AuthProvider();
        fotoPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                elegirOpcionFoto(1);
            }
        });
        fotoFondo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                elegirOpcionFoto(2);
            }
        });

        btnAtras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        btnGuardarCambios.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {guardarCambios();}
        });
        getUsuario();
    }

    private void elegirOpcionFoto(int numeroImagen) {
            mBuilderElegir.setItems(opciones, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    if(i==0){
                        if(numeroImagen==1){
                            abrirGalería(PETICION_GALERÍA_PERFIL);
                        }
                        else if(numeroImagen==2){
                            abrirGalería(PETICION_GALERÍA_PORTADA);
                        }
                    }else if (i==1){
                        if(numeroImagen==1){
                            tomarFoto(PETICION_FOTO_PERFIL);
                        }
                        else if(numeroImagen==2){
                            tomarFoto(PETICION_FOTO_PORTADA);
                        }
                    }
                }
            });
            mBuilderElegir.show();
        }

        private void tomarFoto(int codigoPeticion) {
            Intent abrirCamara = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if(abrirCamara.resolveActivity(getPackageManager())!=null){
                File ficheroFoto=null;
                try {
                    ficheroFoto=crearFicheroFoto(codigoPeticion);
                }catch (Exception e){
                    Toast.makeText(this, "Error al abrir el archivo, "+e.getMessage(), Toast.LENGTH_LONG).show();
                }

                if(ficheroFoto!=null){
                    Uri uriFoto= FileProvider.getUriForFile(EditarPerfil.this,"com.jumbo.carry",ficheroFoto);
                    abrirCamara.putExtra(MediaStore.EXTRA_OUTPUT,uriFoto);
                    startActivityForResult(abrirCamara,PETICION_FOTO_PERFIL);
                }
            }
        }

        private File crearFicheroFoto(int codigoPeticion) throws IOException {
            File direccionAlmacenamiento= getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            File ficheroFoto= File.createTempFile(
                    new Date()+"_photo",".jpg",direccionAlmacenamiento
            );
            if(codigoPeticion==PETICION_FOTO_PERFIL){
                mRutaFoto ="file:"+ficheroFoto.getAbsolutePath();
                mRutaAbsolutaFoto=ficheroFoto.getAbsolutePath();
            }else if(codigoPeticion==PETICION_FOTO_PORTADA){
                mRutaFoto2 ="file:"+ficheroFoto.getAbsolutePath();
                mRutaAbsolutaFoto2=ficheroFoto.getAbsolutePath();
            }
            return ficheroFoto;

        }
        private void abrirGalería(int peticion) {
            Intent galeria = new Intent(Intent.ACTION_GET_CONTENT);
            galeria.setType("image/*");
            startActivityForResult(galeria,peticion);
        }

        @Override
        protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            //-------------------------IMAGENES DESDE GALERÍA
            if(requestCode==PETICION_GALERÍA_PERFIL&&resultCode==RESULT_OK){
                try {
                    mFotoFile=null;
                    mImagen= FileUtil.from(this,data.getData());
                    fotoPerfil.setImageBitmap(BitmapFactory.decodeFile(mImagen.getAbsolutePath()));

                }catch (Exception e){
                    Toast.makeText(this, "No se pudo abrir la imagen", Toast.LENGTH_LONG).show();
                }
            }
            if(requestCode==PETICION_GALERÍA_PORTADA&&resultCode==RESULT_OK){
                try {
                    mFotoFile2=null;
                    mImagen2= FileUtil.from(this,data.getData());
                    fotoFondo.setImageBitmap(BitmapFactory.decodeFile(mImagen2.getAbsolutePath()));

                }catch (Exception e){
                    Toast.makeText(this, "No se pudo abrir la imagen", Toast.LENGTH_LONG).show();
                }
            }
            //--------------------------IMAGEN DESDE CAMARA

            if(requestCode==PETICION_FOTO_PERFIL&&resultCode==RESULT_OK){
                mImagen=null;
                mFotoFile= new File(mRutaAbsolutaFoto);
                Picasso.with(EditarPerfil.this).load(mRutaFoto).into(fotoPerfil);
            }
            if(requestCode==PETICION_FOTO_PORTADA&&resultCode==RESULT_OK){
                mImagen2=null;
                mFotoFile2= new File(mRutaAbsolutaFoto2);
                Picasso.with(EditarPerfil.this).load(mRutaFoto2).into(fotoFondo);
            }
        }
        private void getUsuario(){
            mUsersProvider.getUsuario(mAuthProvider.getUid()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if(documentSnapshot.exists()){
                        if(documentSnapshot.contains("usuario")){
                            usuarioAntiguo.setText(documentSnapshot.getString("usuario"));
                        }
                        if(documentSnapshot.contains("numero")){
                            telefonoAntiguo.setText(documentSnapshot.getString("numero"));
                        }
                        if(documentSnapshot.contains("foto_perfil")){
                            mImagenPerfil=documentSnapshot.getString("foto_perfil");
                            fotoPerfil2 = documentSnapshot.getString("foto_perfil");
                            if(mImagenPerfil!=null&&!mImagenPerfil.isEmpty()){
                                Picasso.with(EditarPerfil.this).load(fotoPerfil2).into(fotoPerfil);
                            }
                        }
                        if(documentSnapshot.contains("foto_portada")) {
                            mImagenPortada = documentSnapshot.getString("foto_portada");
                            fotoPortada2 = documentSnapshot.getString("foto_portada");
                            if(mImagenPortada!=null&&!mImagenPortada.isEmpty()){
                                Picasso.with(EditarPerfil.this).load(fotoPortada2).into(fotoFondo);
                            }
                        }
                    }
                }
            });
        }

    private void guardarCambios() {
        usuarioNuevo= usuarioAntiguo.getText().toString();
        telefonoNuevo= telefonoAntiguo.getText().toString();
        if(!telefonoNuevo.isEmpty()&&!usuarioNuevo.isEmpty()){
            //------------ambas imagenes son de la galería
            if(mImagen!=null && mImagen2!=null){
                guardarImagen(mImagen,mImagen2);
            }
            //------------ambas imagenes son de la camra
            else if(mFotoFile!=null && mFotoFile2!=null){
                guardarImagen(mFotoFile,mFotoFile2);
            }
            else if(mImagen!=null && mFotoFile2!=null){
                guardarImagen(mImagen,mFotoFile2);
            }
            else if(mFotoFile!=null && mImagen2!=null){
                guardarImagen(mFotoFile,mImagen2);
            }else if(mFotoFile !=null){
                guardarImagen2(mFotoFile,true);
            }else if(mFotoFile2 !=null){
                guardarImagen2(mFotoFile2,false);
            }
            else if(mImagen !=null){
                guardarImagen2(mImagen,true);
            }
            else if(mImagen2 !=null){
                guardarImagen2(mImagen2,false);
            }
            else{
                Usuario user = new Usuario();
                user.setUsuario(usuarioNuevo);
                user.setNumero(telefonoNuevo);
                user.setFoto_perfil(fotoPerfil2);
                user.setFoto_portada(fotoPortada2);
                user.setId(mAuthProvider.getUid());
                actualizarInformacion(user);
            }
        }else{
            Toast.makeText(this,"Rellena todos los campos",Toast.LENGTH_LONG).show();
        }
    }
    //------------------------------subir imagen a firestore-----------------------------------------------
    private void guardarImagen(File imageFile1, final File imageFile2) {
        mDialog.show();
        mImageProvider.guardar(EditarPerfil.this, imageFile1).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {
                    mImageProvider.getStorgae().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            final String urlPerfil = uri.toString();

                            mImageProvider.guardar(EditarPerfil.this, imageFile2).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> taskImage2) {
                                    if (taskImage2.isSuccessful()) {
                                        mImageProvider.getStorgae().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri2) {
                                                String urlportada = uri2.toString();
                                                Usuario user = new Usuario();
                                                user.setUsuario(usuarioNuevo);
                                                user.setNumero(telefonoNuevo);
                                                user.setFoto_perfil(urlPerfil);
                                                user.setFoto_portada(urlportada);
                                                user.setId(mAuthProvider.getUid());
                                                actualizarInformacion(user);
                                            }
                                        });
                                    }
                                    else {
                                        mDialog.dismiss();
                                        Toast.makeText(EditarPerfil.this, "La imagen numero 2 no se pudo guardar", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    });
                }
                else {
                    mDialog.dismiss();
                    Toast.makeText(EditarPerfil.this, "No se pudo subir la imagen", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
    private void guardarImagen2(File imagen, boolean perfil){
        mDialog.show();
        mImageProvider.guardar(EditarPerfil.this, imagen).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {
                    mImageProvider.getStorgae().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            final String url = uri.toString();
                            Usuario user = new Usuario();
                            user.setUsuario(usuarioNuevo);
                            user.setNumero(telefonoNuevo);
                            if(perfil){
                                user.setFoto_perfil(url);
                                user.setFoto_portada(mImagenPortada);
                            }else{
                                user.setFoto_portada(url);
                                user.setFoto_perfil(mImagenPerfil);
                            }
                            user.setId(mAuthProvider.getUid());
                            actualizarInformacion(user);
                        }
                    });
                }
                else {
                    mDialog.dismiss();
                    Toast.makeText(EditarPerfil.this, "No se pudo subir la imagen", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
    private void actualizarInformacion(Usuario user){
        if(mDialog.isShowing()){
            mDialog.show();
        }
        mUsersProvider.actualizar(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                mDialog.dismiss();
                if (task.isSuccessful()) {
                    finish();
                }
                else {
                    Toast.makeText(EditarPerfil.this, "La informacion no se pudo actualizar", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        MensajeVistoHelper.actualizarOnline(true,EditarPerfil.this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MensajeVistoHelper.actualizarOnline(false,EditarPerfil.this);

    }
}