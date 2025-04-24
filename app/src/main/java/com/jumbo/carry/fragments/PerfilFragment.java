package com.jumbo.carry.fragments;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
import com.jumbo.carry.activities.EditarPerfil;
import com.jumbo.carry.activities.MainActivity;
import com.jumbo.carry.adapters.AdaptadorPublicacion;
import com.jumbo.carry.adapters.MiPublicacionAdapter;
import com.jumbo.carry.models.Publicacion;
import com.jumbo.carry.providers.AuthProvider;
import com.jumbo.carry.providers.PostProvider;
import com.jumbo.carry.providers.UsersProvider;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PerfilFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PerfilFragment extends Fragment {
    ImageView mEditarPerfil;
    ImageView mCerrarSesion;
    ImageView mFotoPortada;
    CircleImageView mFotoPerfil;
    TextView usuarioNombre;
    TextView numeroPublicaciones;
    TextView sinPublicaciones;
    RecyclerView mRecyclerView;
    MiPublicacionAdapter miPublicacionAdapter;
    AuthProvider mAuthProvider;
    UsersProvider mUsersProvider;
    PostProvider mPostProvider;
    ListenerRegistration mListenerRegistration;
    View mView;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public PerfilFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PerfilFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PerfilFragment newInstance(String param1, String param2) {
        PerfilFragment fragment = new PerfilFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        mView=inflater.inflate(R.layout.fragment_perfil, container, false);
        mAuthProvider=new AuthProvider();
        mUsersProvider= new UsersProvider();
        mPostProvider=new PostProvider();
        mEditarPerfil=mView.findViewById(R.id.imgEditarPerfil);
        mCerrarSesion=mView.findViewById(R.id.imgCerrarSesion);
        mFotoPerfil=mView.findViewById(R.id.fotoPerfilFragment);
        mFotoPortada=mView.findViewById(R.id.fotoPortadaFragment);
        usuarioNombre=mView.findViewById(R.id.lblUsuarioPerfil);
        sinPublicaciones=mView.findViewById(R.id.lblSinPublicaciones);
        mRecyclerView=mView.findViewById(R.id.recyclerViewMisPublicaciones);
        numeroPublicaciones=mView.findViewById(R.id.lblNumeroPublicaciones);
        mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL));
        mEditarPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editarPerfil();
            }
        });
        mCerrarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cerrarSesion();
            }
        });
        getUser();
        getNumeroPost();
        publicacionesExistentes();
        return mView;
    }

    private void publicacionesExistentes() {
        mListenerRegistration=mPostProvider.getPostTotalesUsuario(mAuthProvider.getUid())
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if(value!=null){
                    int numeroDePublicaciones= value.size();
                    if(numeroDePublicaciones>0){
                        sinPublicaciones.setText("Mis publicaciones");
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

    @Override
    public void onStart() {
        super.onStart();
        Query query = mPostProvider.getPostTotalesUsuario(mAuthProvider.getUid());
        FirestoreRecyclerOptions<Publicacion> opciones =
                new FirestoreRecyclerOptions.Builder<Publicacion>().setQuery(query,Publicacion.class).build();
        miPublicacionAdapter = new MiPublicacionAdapter(opciones,getContext());
        mRecyclerView.setAdapter(miPublicacionAdapter);
        miPublicacionAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        miPublicacionAdapter.stopListening();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mListenerRegistration!=null){
            mListenerRegistration.remove();
        }
    }

    private void cerrarSesion() {

        mAuthProvider.logout();
        Intent intent= new Intent(getContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void editarPerfil() {
        Intent intent = new Intent(getContext(), EditarPerfil.class);
        startActivity(intent);
    }

    private void getNumeroPost(){
        mPostProvider.getPostTotalesUsuario(mAuthProvider.getUid())
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                int total=queryDocumentSnapshots.size();
                numeroPublicaciones.setText(String.valueOf(total));
            }
        });
    }

    private  void getUser(){
        mUsersProvider.getUsuario(mAuthProvider.getUid())
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
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
                         Picasso.with(getContext()).load(fotoPerfil).into(mFotoPerfil);
                     }
                 }
                    if (documentSnapshot.contains("foto_portada")){
                        String fotoPortada = documentSnapshot.getString("foto_portada");
                        if(fotoPortada!=null&&!fotoPortada.isEmpty()){
                            Picasso.with(getContext()).load(fotoPortada).into(mFotoPortada);
                        }
                    }
                }
            }
        });
    }
}