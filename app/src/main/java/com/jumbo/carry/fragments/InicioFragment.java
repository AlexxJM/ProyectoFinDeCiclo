package com.jumbo.carry.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.jumbo.carry.R;
import com.jumbo.carry.activities.PublicacionActivity;
import com.jumbo.carry.adapters.AdaptadorPublicacion;
import com.jumbo.carry.models.Publicacion;
import com.jumbo.carry.providers.AuthProvider;
import com.jumbo.carry.providers.PostProvider;
import com.jumbo.carry.providers.UsersProvider;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.squareup.picasso.Picasso;

import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link InicioFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class InicioFragment extends Fragment implements MaterialSearchBar.OnSearchActionListener{
    View mView;
    FloatingActionButton mFab;
    MaterialSearchBar mMaterialSearchBar;

    AuthProvider mAuthProvider;
    RecyclerView mRecyclerView;
    PostProvider mPostProvider;
    AdaptadorPublicacion mAdaptadorPublicacion;
    AdaptadorPublicacion mAdaptadorPublicacionBusqueda;

    TextView mLblBienvenida;
    UsersProvider mUsersProvider;
    CircleImageView mFotoPerfil;




    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public InicioFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment InicioFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static InicioFragment newInstance(String param1, String param2) {
        InicioFragment fragment = new InicioFragment();
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
        mView= inflater.inflate(R.layout.fragment_inicio, container, false);
        mFab=mView.findViewById(R.id.botonFlotanteCrear);
        mRecyclerView=mView.findViewById(R.id.recyclerViewPublicaciones);
        mLblBienvenida= mView.findViewById(R.id.lblBienvenida);
        mFotoPerfil=mView.findViewById(R.id.fotoPerfilInicio);
        mMaterialSearchBar = mView.findViewById(R.id.searchBar);
        mPostProvider = new PostProvider();
        mUsersProvider= new UsersProvider();
        mAuthProvider = new AuthProvider();

        mMaterialSearchBar.setOnSearchActionListener(this);
        mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL));

        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                publicarPost();
            }
        });

        mUsersProvider.getUsuario(mAuthProvider.getUid())
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()){
                    if(documentSnapshot.contains("usuario")){
                        String usuarioActual=documentSnapshot.getString("usuario");
                        mLblBienvenida.setText("Bienvenido de nuevo, \n"+usuarioActual);

                    }
                }
            }
        });
        mUsersProvider.getUsuario(mAuthProvider.getUid())
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()){
                    if (documentSnapshot.contains("foto_perfil")){
                        String fotoPerfil = documentSnapshot.getString("foto_perfil");
                        if(fotoPerfil!=null&&!fotoPerfil.isEmpty()){
                            Picasso.with(getContext()).load(fotoPerfil).into(mFotoPerfil);
                        }
                    }
                }
            }
        });
        return mView;
    }
    private void buscarPorTitulo(String titulo){
        super.onStart();
        Query query = mPostProvider.getPublicacionPorTitulo(titulo);
        FirestoreRecyclerOptions<Publicacion> opciones =
                new FirestoreRecyclerOptions.Builder<Publicacion>().setQuery(query,Publicacion.class).build();
        mAdaptadorPublicacionBusqueda = new AdaptadorPublicacion(opciones,getContext());
        mAdaptadorPublicacionBusqueda.notifyDataSetChanged();
        mRecyclerView.setAdapter(mAdaptadorPublicacionBusqueda);
        mAdaptadorPublicacionBusqueda.startListening();
    }
    private void getTodasPublicaciones(){
        super.onStart();
        Query query = mPostProvider.getAll();
        FirestoreRecyclerOptions<Publicacion> opciones =
                new FirestoreRecyclerOptions.Builder<Publicacion>().
                        setQuery(query,Publicacion.class).build();
        mAdaptadorPublicacion = new AdaptadorPublicacion(opciones,getContext());
        mAdaptadorPublicacion.notifyDataSetChanged();
        mRecyclerView.setAdapter(mAdaptadorPublicacion);
        mAdaptadorPublicacion.startListening();
    }
    private void publicarPost() {
        Intent intent = new Intent(getContext(), PublicacionActivity.class);
        startActivity(intent);
    }
    @Override
    public void onStart() {
        super.onStart();
        Query query = mPostProvider.getAll();
        FirestoreRecyclerOptions<Publicacion> opciones =
                new FirestoreRecyclerOptions.Builder<Publicacion>().setQuery(query,Publicacion.class).build();
        mAdaptadorPublicacion = new AdaptadorPublicacion(opciones,getContext());
        mRecyclerView.setAdapter(mAdaptadorPublicacion);
        mAdaptadorPublicacion.startListening();
    }


    @Override
    public void onStop() {
        super.onStop();
        if(mAdaptadorPublicacionBusqueda!=null){
            mAdaptadorPublicacionBusqueda.stopListening();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mAdaptadorPublicacion.getmListenerRegistration()!=null){
            mAdaptadorPublicacion.getmListenerRegistration().remove();
        }
    }

    @Override
    public void onSearchStateChanged(boolean enabled) {
        if(!enabled){
            getTodasPublicaciones();
        }
    }

    @Override
    public void onSearchConfirmed(CharSequence text) {
        buscarPorTitulo(String.valueOf(text).toLowerCase(Locale.ROOT));
    }

    @Override
    public void onButtonClicked(int buttonCode) {

    }
}