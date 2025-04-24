package com.jumbo.carry.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.UploadTask;
import com.jumbo.carry.R;
import com.jumbo.carry.models.Ciudad;
import com.jumbo.carry.models.Publicacion;
import com.jumbo.carry.providers.AuthProvider;
import com.jumbo.carry.providers.ImageProvider;
import com.jumbo.carry.providers.PostProvider;
import com.jumbo.carry.providers.UsersProvider;
import com.jumbo.carry.utils.FileUtil;
import com.jumbo.carry.utils.MensajeVistoHelper;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import dmax.dialog.SpotsDialog;

public class PublicacionActivity extends AppCompatActivity {
    AutoCompleteTextView autoCompletar;
    static Ciudad ciudad= new Ciudad();
    static ArrayList <String> ciudadesList=ciudad.getCiudades();
    static Button btnPublicar;
    static Button btnAtras;
    static ImageView fotoPublicacion1;
    static ImageView fotoPublicacion2;
    ImageProvider mImageProvider;
    PostProvider mPostProvider;
    AuthProvider mAuthProvider;
    EditText txtTitulo;
    EditText txtDescripcion;
    private final int PETICION_GALERÍA=1;
    private final int PETICION_GALERÍA2=2;
    private final int PETICION_FOTO=3;
    private final int PETICION_FOTO2=4;

    AlertDialog mDialog;
    AlertDialog.Builder mBuilderElegir;
    CharSequence []opciones;

    File mImagen;
    File mImagen2;

    String titulo;
    String descripcion;
    String fechaInicio;
    String fechaFin;
    String ubicacion;
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
        setContentView(R.layout.activity_publicacion);
        btnPublicar=findViewById(R.id.btnPublicar);
        btnAtras=findViewById(R.id.btnAtras);
        fotoPublicacion1=findViewById(R.id.imgPublicacion1);
        fotoPublicacion2=findViewById(R.id.imgPublicacion2);
        mImageProvider= new ImageProvider();
        mPostProvider=new PostProvider();
        mAuthProvider= new AuthProvider();
        txtTitulo=findViewById(R.id.txtTituloPublicacion);
        txtDescripcion=findViewById(R.id.txtDescripcionPublicacion);
        opciones= new CharSequence[]{"Abrir galería","Tomar foto"};
        mDialog=new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Publicando...").setTheme(R.style.Custom)
                .setCancelable(false).build();
        mBuilderElegir= new AlertDialog.Builder(this).setTitle("Elige una opción");
        //-----------------------Autocompletar ubicación---------------------
        autoCompletar = findViewById(R.id.txtUbicacionPubli);
        ArrayAdapter<String>adaptadorCiudades= new ArrayAdapter<>
                (this, android.R.layout.simple_dropdown_item_1line,ciudadesList);
        autoCompletar.setThreshold(0);
        autoCompletar.setAdapter(adaptadorCiudades);
        //---------------------------volver atrás---------------------
        btnAtras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        //-----------------------agregar imagen-------------------------------------
        fotoPublicacion1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                elegirOpcionFoto(1);
            }
        });
        fotoPublicacion2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                elegirOpcionFoto(2);
            }
        });
        //--------------------publicar publicacion
        btnPublicar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                publicar();
            }
        });
    }

    private void elegirOpcionFoto(final int numeroImagen) {
        mBuilderElegir.setItems(opciones, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(i==0){
                    if(numeroImagen==1){
                        abrirGalería(PETICION_GALERÍA);
                    }
                    else if(numeroImagen==2){
                        abrirGalería(PETICION_GALERÍA2);
                    }
                }else if (i==1){
                    if(numeroImagen==1){
                        tomarFoto(PETICION_FOTO);
                    }
                    else if(numeroImagen==2){
                        tomarFoto(PETICION_FOTO2);
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
                Toast.makeText(this, "Error al abrir el archivo, "+e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            if(ficheroFoto!=null){
                Uri uriFoto= FileProvider.getUriForFile(PublicacionActivity.this,"com.jumbo.carry",ficheroFoto);
                abrirCamara.putExtra(MediaStore.EXTRA_OUTPUT,uriFoto);
                startActivityForResult(abrirCamara,PETICION_FOTO);
            }
        }
    }

    private File crearFicheroFoto(int codigoPeticion) throws IOException {
        File direccionAlmacenamiento= getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File ficheroFoto= File.createTempFile(
          new Date()+"_photo",".jpg",direccionAlmacenamiento
        );
        if(codigoPeticion==PETICION_FOTO){
            mRutaFoto ="file:"+ficheroFoto.getAbsolutePath();
            mRutaAbsolutaFoto=ficheroFoto.getAbsolutePath();
        }else if(codigoPeticion==PETICION_FOTO2){
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
    private void publicar() {
        titulo = txtTitulo.getText().toString();
        descripcion = txtDescripcion.getText().toString();
        ubicacion = autoCompletar.getText().toString();
        if(titulo.isEmpty()||descripcion.isEmpty()||ubicacion.isEmpty()){
            Toast.makeText(this, "Rellena todos los campos", Toast.LENGTH_SHORT).show();
        }
        else{
            //------------ambas imagenes son de la galería
            if(mImagen!=null && mImagen2!=null){
                guardarImagen(mImagen,mImagen2);
            }
            //------------ambas imagenes son de la camara
            else if(mFotoFile!=null && mFotoFile2!=null){
                guardarImagen(mFotoFile,mFotoFile2);
            }
            //------------la primera desde la galería y la segunda desde la cámara
            else if(mImagen!=null && mFotoFile2!=null){
                guardarImagen(mImagen,mFotoFile2);
            }
            //------------la primera desde la cámara y la segunda desde la galería
            else if(mFotoFile!=null && mImagen2!=null){
                guardarImagen(mFotoFile,mImagen2);
            }
            else{
                Toast.makeText(this, "Debes subir las dos imágenes", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //------------------------------subir imagen a firestore-----------------------------------------------
    private void guardarImagen(File imageFile1, File imageFile2) {
        mDialog.show();
        mImageProvider.guardar(PublicacionActivity.this,imageFile1)
                .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if(task.isSuccessful()){
                    mImageProvider.getStorgae().getDownloadUrl()
                            .addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            final String url = uri.toString();
                            mImageProvider.guardar(PublicacionActivity.this,imageFile2)
                                    .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> taskImagen2) {
                                    if(taskImagen2.isSuccessful()){
                                        mImageProvider.getStorgae().getDownloadUrl()
                                                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri2) {
                                                String url2 = uri2.toString();
                                                Publicacion publicacion= new Publicacion();
                                                publicacion.setImagen1(url);
                                                publicacion.setImagen2(url2);
                                                publicacion.setTitulo(titulo.toLowerCase());
                                                publicacion.setDescripcion(descripcion);
                                                publicacion.setUbicacion(ubicacion);
                                                publicacion.setIdUsuario(mAuthProvider.getUid());
                                                publicacion.setFechaCreacion(new Date().getTime());
                                                mPostProvider.subir(publicacion)
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> taskSave) {
                                                        mDialog.dismiss();
                                                        if(taskSave.isSuccessful()){
                                                            Intent intent= new Intent(PublicacionActivity.this,HomeActivity.class);
                                                            startActivity(intent);
                                                        }else{
                                                            Toast.makeText(PublicacionActivity.this, "No se pudo publicar", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                            }
                                        });
                                    }
                                    else{
                                        mDialog.dismiss();
                                        Toast.makeText(PublicacionActivity.this, "No se pudo subir la segunda imagen", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                        }
                    });
                }
                else {
                    mDialog.dismiss();
                    Toast.makeText(PublicacionActivity.this, "No se pudo subir la imagen", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //-------------------------IMAGENES DESDE GALERÍA
        if(requestCode==PETICION_GALERÍA&&resultCode==RESULT_OK){
            try {
                mFotoFile=null;
                mImagen= FileUtil.from(this,data.getData());
                fotoPublicacion1.setImageBitmap(BitmapFactory.decodeFile(mImagen.getAbsolutePath()));

            }catch (Exception e){
                Toast.makeText(this, "No se pudo abrir la imagen", Toast.LENGTH_LONG).show();
            }
        }
        if(requestCode==PETICION_GALERÍA2&&resultCode==RESULT_OK){
            try {
                mFotoFile2=null;
                mImagen2= FileUtil.from(this,data.getData());
                fotoPublicacion2.setImageBitmap(BitmapFactory.decodeFile(mImagen2.getAbsolutePath()));

            }catch (Exception e){
                Toast.makeText(this, "No se pudo abrir la imagen", Toast.LENGTH_LONG).show();
            }
        }
        //--------------------------IMAGEN DESDE CAMARA

        if(requestCode==PETICION_FOTO&&resultCode==RESULT_OK){
            mImagen=null;
            mFotoFile= new File(mRutaAbsolutaFoto);
            Picasso.with(PublicacionActivity.this).load(mRutaFoto).into(fotoPublicacion1);
        }
        if(requestCode==PETICION_FOTO2&&resultCode==RESULT_OK){
            mImagen2=null;
            mFotoFile2= new File(mRutaAbsolutaFoto2);
            Picasso.with(PublicacionActivity.this).load(mRutaFoto2).into(fotoPublicacion2);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        MensajeVistoHelper.actualizarOnline(true,PublicacionActivity.this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MensajeVistoHelper.actualizarOnline(false,PublicacionActivity.this);
    }
}