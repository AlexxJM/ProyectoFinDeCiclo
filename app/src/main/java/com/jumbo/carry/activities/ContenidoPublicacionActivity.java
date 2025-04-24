package com.jumbo.carry.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.ListenerRegistration;
import com.jumbo.carry.R;
import com.jumbo.carry.adapters.AdaptadorSlider;
import com.jumbo.carry.models.SliderItem;
import com.jumbo.carry.providers.AuthProvider;
import com.jumbo.carry.providers.PostProvider;
import com.jumbo.carry.providers.UsersProvider;
import com.jumbo.carry.utils.RelativeTime;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ContenidoPublicacionActivity extends AppCompatActivity {

    SliderView mSliderView;
    AdaptadorSlider mAdaptadorSlider;
    List<SliderItem>mSliderItems= new ArrayList<>();
    PostProvider mPostProvider;
    UsersProvider mUsersProvider;
    AuthProvider mAuthProvider;
    CircleImageView mFotoPerfilDetalles;
    TextView mUsuarioDetalles;
    TextView mDescripcionDetalles;
    TextView mTituloDetalles;
    TextView mUbicacionDetalles;
    TextView mFechaDetalles;
    Button mBtnIrPerfil;
    Button mBtnAtras;
    Button mBtnChatear;
    String mExtraPostId;
    String mIdUsuarioPost="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contenido_publicacion);
        mSliderView=findViewById(R.id.imageSlider);
        mFotoPerfilDetalles=findViewById(R.id.fotoPerfilDetalles);
        mUsuarioDetalles=findViewById(R.id.usuarioDetalles);
        mBtnIrPerfil=findViewById(R.id.btnIrAlPerfil);
        mTituloDetalles=findViewById(R.id.tituloPublicacionDetalles);
        mDescripcionDetalles=findViewById(R.id.descripcionPublicacionDetalles);
        mUbicacionDetalles=findViewById(R.id.ubicacionPublicacionDetalles);
        mFechaDetalles = findViewById(R.id.fechaPublicacionDetalles);
        mBtnChatear=findViewById(R.id.btnChatear);
        mBtnAtras=findViewById(R.id.btnAtras);
        mPostProvider= new PostProvider();
        mUsersProvider= new UsersProvider();
        mAuthProvider = new AuthProvider();
        mBtnAtras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        mBtnIrPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                irAlPerfil();
            }
        });
        mBtnChatear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                irAlChat();
            }
        });
        mExtraPostId=getIntent().getStringExtra("id");
        getPost();
    }
    private void irAlChat() {
        Intent intent = new Intent(ContenidoPublicacionActivity.this,ChatActivity.class);
        intent.putExtra("idUsuarioEmisor",mAuthProvider.getUid());
        intent.putExtra("idUsuarioReceptor",mIdUsuarioPost);
        startActivity(intent);
    }

    private void irAlPerfil() {
        if(!mIdUsuarioPost.isEmpty()){
            Intent intent= new Intent(ContenidoPublicacionActivity.this,PerfilPublicacionActivity.class);
            intent.putExtra("idUsuario",mIdUsuarioPost);
            startActivity(intent);
        }
        else{
            Toast.makeText(this,"Vuelve a intentarlo",Toast.LENGTH_LONG);
        }
    }
    public void getPost(){
        mPostProvider.getPostById(mExtraPostId).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()){
                    if(documentSnapshot.contains("imagen1")){
                        String imagen1 = documentSnapshot.getString("imagen1");
                        SliderItem sliderItem = new SliderItem();
                        sliderItem.setImagenUrl(imagen1);
                        mSliderItems.add(sliderItem);
                    }
                    if(documentSnapshot.contains("imagen2")){
                        String imagen2 = documentSnapshot.getString("imagen2");
                        SliderItem sliderItem = new SliderItem();
                        sliderItem.setImagenUrl(imagen2);
                        mSliderItems.add(sliderItem);
                    }
                    if(documentSnapshot.contains("titulo")){
                        String titulo = documentSnapshot.getString("titulo");
                        mTituloDetalles.setText(titulo.toUpperCase());
                    }
                    if(documentSnapshot.contains("descripcion")){
                        String descripcion = documentSnapshot.getString("descripcion");
                        mDescripcionDetalles.setText(descripcion);
                    }
                    if(documentSnapshot.contains("ubicacion")){
                        String ubicacion = documentSnapshot.getString("ubicacion");
                        mUbicacionDetalles.setText(ubicacion);
                    }
                    if(documentSnapshot.contains("idUsuario")){
                        mIdUsuarioPost = documentSnapshot.getString("idUsuario");
                        getInfoUsuario(mIdUsuarioPost);}
                    if(documentSnapshot.contains("fechaCreacion")){
                        Long fechaCreacion = documentSnapshot.getLong("fechaCreacion");
                        String fechaCreacionPublicacion= RelativeTime.getTimeAgo(fechaCreacion,ContenidoPublicacionActivity.this);
                        mFechaDetalles.setText(fechaCreacionPublicacion);
                    }
                }
                mAdaptadorSlider= new AdaptadorSlider(ContenidoPublicacionActivity.this,mSliderItems);
                mSliderView.setSliderAdapter(mAdaptadorSlider);
                mSliderView.setIndicatorAnimation(IndicatorAnimationType.DROP);
                mSliderView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
                mSliderView.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_RIGHT);
                mSliderView.setIndicatorSelectedColor(Color.WHITE);
                mSliderView.setIndicatorUnselectedColor(Color.GRAY);
                mSliderView.setScrollTimeInSec(3);
                mSliderView.setAutoCycle(true);
                mSliderView.startAutoCycle();
                if(mAuthProvider.getUid().equals(mIdUsuarioPost)){
                    mBtnChatear.setVisibility(View.GONE);
                }
            }
        });
    }
    private void getInfoUsuario(String idUsuario) {
        mUsersProvider.getUsuario(idUsuario).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()){
                    if(documentSnapshot.contains("usuario")){
                        String usuario = documentSnapshot.getString("usuario");
                        mUsuarioDetalles.setText(usuario);
                    }
                    if(documentSnapshot.contains("foto_perfil")){
                        String fotoPerfil = documentSnapshot.getString("foto_perfil");
                        Picasso.with(ContenidoPublicacionActivity.this).load(fotoPerfil).into(mFotoPerfilDetalles);
                    }
                }
            }
        });
    }
}