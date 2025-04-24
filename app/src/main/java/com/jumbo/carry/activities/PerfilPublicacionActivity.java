package com.jumbo.carry.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.jumbo.carry.R;
import com.jumbo.carry.adapters.MiPublicacionAdapter;
import com.jumbo.carry.models.Publicacion;
import com.jumbo.carry.providers.AuthProvider;
import com.jumbo.carry.providers.PostProvider;
import com.jumbo.carry.providers.UsersProvider;
import com.jumbo.carry.utils.MensajeVistoHelper;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class PerfilPublicacionActivity extends AppCompatActivity {

    ImageView mFotoPortada;
    CircleImageView mFotoPerfil;
    TextView usuarioNombre;
    TextView numeroPublicaciones;
    TextView sinPublicaciones;
    RecyclerView mRecyclerView;
    AuthProvider mAuthProvider;
    UsersProvider mUsersProvider;
    PostProvider mPostProvider;
    String mIdUsuarioPublicacion="";
    Button mBtnAtras;
    Button mBtnChatear;
    ListenerRegistration mListenerRegistration;
    MiPublicacionAdapter miPublicacionAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_publicacion);
        mFotoPerfil=findViewById(R.id.fotoPerfilFragment);
        mFotoPortada=findViewById(R.id.fotoPortadaFragment);
        usuarioNombre=findViewById(R.id.lblUsuarioPerfil);
        sinPublicaciones=findViewById(R.id.lblSinPublicaciones);
        numeroPublicaciones=findViewById(R.id.lblNumeroPublicaciones);
        mRecyclerView=findViewById(R.id.recyclerViewMisPublicaciones);
        mAuthProvider=new AuthProvider();
        mUsersProvider= new UsersProvider();
        mPostProvider=new PostProvider();
        mIdUsuarioPublicacion=getIntent().getStringExtra("idUsuario");
        mBtnAtras=findViewById(R.id.btnAtras);
        mBtnChatear=findViewById(R.id.btnChatear);
        mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL));
        if(mAuthProvider.getUid().equals(mIdUsuarioPublicacion)){
            mBtnChatear.setVisibility(View.GONE);
        }

        mBtnAtras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        mBtnChatear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                irAlChat();
            }
        });
        getUser();
        publicacionesExistentes();
    }
    private void irAlChat() {
        Intent intent = new Intent(PerfilPublicacionActivity.this,ChatActivity.class);
        intent.putExtra("idUsuarioEmisor",mAuthProvider.getUid());
        intent.putExtra("idUsuarioReceptor",mIdUsuarioPublicacion);
        startActivity(intent);
    }

    private void publicacionesExistentes() {
        mListenerRegistration=mPostProvider.getPostTotalesUsuario(mIdUsuarioPublicacion)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if(value!=null){
                    int numeroDePublicaciones= value.size();
                    if(numeroDePublicaciones>0){
                        numeroPublicaciones.setText(String.valueOf(numeroDePublicaciones));
                        sinPublicaciones.setText("Publicaciones");
                        sinPublicaciones.setTextColor(Color.BLACK);
                    }
                    else{
                        sinPublicaciones.setText("Nada por aqui...");
                        sinPublicaciones.setTextColor(Color.LTGRAY);
                    }
                }
            }
        });
    }
    private  void getUser(){
        mUsersProvider.getUsuario(mIdUsuarioPublicacion).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()){
                    if (documentSnapshot.contains("usuario")){
                        String usuario = documentSnapshot.getString("usuario");
                        usuarioNombre.setText(usuario);
                    }
                    if (documentSnapshot.contains("foto_perfil")){
                        String fotoPerfil = documentSnapshot.getString("foto_perfil");
                        if(fotoPerfil!=null&&!fotoPerfil.isEmpty()){
                            Picasso.with(PerfilPublicacionActivity.this).load(fotoPerfil).into(mFotoPerfil);
                        }
                    }
                    if (documentSnapshot.contains("foto_portada")){
                        String fotoPortada = documentSnapshot.getString("foto_portada");
                        if(fotoPortada!=null&&!fotoPortada.isEmpty()){
                            Picasso.with(PerfilPublicacionActivity.this).load(fotoPortada).into(mFotoPortada);
                        }
                    }
                }
            }
        });
    }
    @Override
    public void onStart() {
        super.onStart();
        Query query = mPostProvider.getPostTotalesUsuario(mIdUsuarioPublicacion);
        FirestoreRecyclerOptions<Publicacion> opciones =
                new FirestoreRecyclerOptions.Builder<Publicacion>().setQuery(query,Publicacion.class).build();
        miPublicacionAdapter = new MiPublicacionAdapter(opciones,PerfilPublicacionActivity.this);
        mRecyclerView.setAdapter(miPublicacionAdapter);
        miPublicacionAdapter.startListening();
        MensajeVistoHelper.actualizarOnline(true,PerfilPublicacionActivity.this);
    }

    @Override
    public void onStop() {
        super.onStop();
        miPublicacionAdapter.stopListening();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mListenerRegistration!=null){
            mListenerRegistration.remove();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        MensajeVistoHelper.actualizarOnline(false,PerfilPublicacionActivity.this);

    }
}