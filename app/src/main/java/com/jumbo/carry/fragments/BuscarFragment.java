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
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.jumbo.carry.R;
import com.jumbo.carry.activities.EditarPerfil;
import com.jumbo.carry.activities.MainActivity;
import com.jumbo.carry.adapters.AdaptadorPublicacion;
import com.jumbo.carry.adapters.MiGuardadosAdapter;
import com.jumbo.carry.adapters.MiPublicacionAdapter;
import com.jumbo.carry.models.Guardado;
import com.jumbo.carry.models.Publicacion;
import com.jumbo.carry.providers.AuthProvider;
import com.jumbo.carry.providers.GuardadosProvider;
import com.jumbo.carry.providers.PostProvider;
import com.jumbo.carry.providers.UsersProvider;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BuscarFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BuscarFragment extends Fragment {
    TextView sinPublicaciones;
    RecyclerView mRecyclerView;
    MiGuardadosAdapter miGuardadosAdapter;
    AuthProvider mAuthProvider;
    UsersProvider mUsersProvider;
    PostProvider mPostProvider;
    GuardadosProvider mGuardadosProvider;

    View mView;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public BuscarFragment() {
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
    public static BuscarFragment newInstance(String param1, String param2) {
        BuscarFragment fragment = new BuscarFragment();
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
        mView=inflater.inflate(R.layout.fragment_buscar, container, false);
        mAuthProvider=new AuthProvider();
        mUsersProvider= new UsersProvider();
        mPostProvider=new PostProvider();
        sinPublicaciones=mView.findViewById(R.id.lblSinPublicaciones);
        mRecyclerView=mView.findViewById(R.id.recyclerViewMisPublicaciones);
        mGuardadosProvider= new GuardadosProvider();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(linearLayoutManager);
        return mView;
    }

    @Override
    public void onStart() {
        super.onStart();
        Query query = mGuardadosProvider.getGuardadoUsuario(mAuthProvider.getUid());
        FirestoreRecyclerOptions<Guardado> opciones =
                new FirestoreRecyclerOptions.Builder<Guardado>().setQuery(query, Guardado.class).build();
        miGuardadosAdapter = new MiGuardadosAdapter(opciones,getContext());
        mRecyclerView.setAdapter(miGuardadosAdapter);
        miGuardadosAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        miGuardadosAdapter.stopListening();
    }
}