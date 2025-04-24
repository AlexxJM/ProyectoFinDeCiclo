package com.jumbo.carry.adapters;

import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;
import com.jumbo.carry.R;
import com.jumbo.carry.activities.ContenidoPublicacionActivity;
import com.jumbo.carry.activities.HomeActivity;
import com.jumbo.carry.models.FCMBody;
import com.jumbo.carry.models.FCMResponse;
import com.jumbo.carry.models.Guardado;
import com.jumbo.carry.models.Publicacion;
import com.jumbo.carry.providers.AuthProvider;
import com.jumbo.carry.providers.GuardadosProvider;
import com.jumbo.carry.providers.NotificacionesProvider;
import com.jumbo.carry.providers.PostProvider;
import com.jumbo.carry.providers.TokenProvider;
import com.jumbo.carry.providers.UsersProvider;
import com.squareup.picasso.Picasso;

import java.lang.annotation.Documented;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdaptadorPublicacion extends FirestoreRecyclerAdapter<Publicacion,AdaptadorPublicacion.ViewHolder> {

    Context context;
    UsersProvider mUsersProvider;
    GuardadosProvider mGuardadosProvider;
    AuthProvider mAuthProvider;
    NotificacionesProvider mNotificacionesProvider;
    TokenProvider mTokenProvider;
    ListenerRegistration mListenerRegistration;

    public AdaptadorPublicacion(FirestoreRecyclerOptions<Publicacion> opciones,Context context){
        super(opciones);
        this.context=context;
        mUsersProvider=new UsersProvider();
        mAuthProvider= new AuthProvider();
        mGuardadosProvider= new GuardadosProvider();
        mTokenProvider= new TokenProvider();
        mNotificacionesProvider= new NotificacionesProvider();
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_publicacion,parent,false);
        return new ViewHolder(view);
    }
    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView lblTitulo;
        private TextView lblDescripcion;
        private TextView lblUsuario;
        private TextView numeroGuardados;
        private ImageView imagenPost;
        private ImageView imagenGuardado;
        View viewHolder;
        public ViewHolder (View view){
            super(view);
            lblTitulo=view.findViewById(R.id.tituloPublicacionInicio);
            lblDescripcion=view.findViewById(R.id.descripcionPublicacionInicio);
            lblUsuario=view.findViewById(R.id.usuarioPublicacionInicio);
            imagenPost=view.findViewById(R.id.imagenPublicacionInicio);
            imagenGuardado=view.findViewById(R.id.imagenGuardado);
            numeroGuardados=view.findViewById(R.id.numeroGuardados);
            viewHolder=view;
        }
    }
    @Override
    protected void onBindViewHolder(@NonNull final ViewHolder holder, int position, @NonNull final Publicacion publicacion) {
        DocumentSnapshot document = getSnapshots().getSnapshot(position);
        final String postId = document.getId();
        holder.lblTitulo.setText(publicacion.getTitulo().toUpperCase());
        holder.lblDescripcion.setText(publicacion.getDescripcion());
        if(publicacion.getImagen1()!=null){
            if(!publicacion.getImagen1().isEmpty()){
                Picasso.with(context).load(publicacion.getImagen1()).into(holder.imagenPost);
            }
        }
        holder.viewHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ContenidoPublicacionActivity.class);
                intent.putExtra("id",postId);
                context.startActivity(intent);
            }
        });
        holder.imagenGuardado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Guardado guardado= new Guardado();
                guardado.setIdUsuario(mAuthProvider.getUid());
                guardado.setIdPublicacion(postId);
                guardado.setFechaGuardado(new Date().getTime());
                guardar(guardado,holder,publicacion);
            }
        });
        getInformacionUsuario(publicacion.getIdUsuario(),holder);
        getNumeroGuardadosPublicacion(postId,holder);
        guardadosExistentes(postId,mAuthProvider.getUid(),holder);
    }
    public ListenerRegistration getmListenerRegistration(){
        return mListenerRegistration;
    }

    private void getNumeroGuardadosPublicacion(String idPost,final ViewHolder holder){
        mListenerRegistration=mGuardadosProvider.getNumeroGuardados(idPost).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if(value!=null){
                    int numeroGuardaos= value.size();
                    holder.numeroGuardados.setText(String.valueOf(numeroGuardaos)+" Guardados");
                }
            }
        });
    }



    private void guardar(final Guardado guardado, ViewHolder holder,Publicacion publicacion) {
        mGuardadosProvider.getGuardadoPublicacion(guardado.getIdPublicacion(),mAuthProvider.getUid())
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                int numeroDocumentos = queryDocumentSnapshots.size();
                if(numeroDocumentos<=0){
                    holder.imagenGuardado.setImageResource(R.drawable.huella_naranja);
                    mGuardadosProvider.create(guardado);
                    enviarNotificacion(publicacion);
                }else{
                    String idGuardado = queryDocumentSnapshots.getDocuments().get(0).getId();
                    holder.imagenGuardado.setImageResource(R.drawable.huella_gris);
                    mGuardadosProvider.eliminarGuardado(idGuardado);
                }
            }
        });
    }

    private void enviarNotificacion(Publicacion publicacion) {
        if(publicacion.getIdUsuario()==null){
            return;
        }
        mTokenProvider.getToken(publicacion.getIdUsuario())
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()&&documentSnapshot.contains("token")){

                    mUsersProvider.getUsuario(mAuthProvider.getUid())
                            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot2) {
                            if(documentSnapshot2.exists()){
                                if(documentSnapshot2.contains("usuario")&&
                                        !Objects.equals(mAuthProvider.getUid(), publicacion.getIdUsuario())){
                                    String usuarioActual=documentSnapshot2.getString("usuario");
                                    String token =documentSnapshot.getString("token");
                                    Map<String,String> data = new HashMap<>();
                                    data.put("title","¡Nuevo cuidador!");
                                    data.put("body",usuarioActual+", ha guardado tu publicación");
                                    FCMBody body = new FCMBody(token,"high","4500s",data);
                                    mNotificacionesProvider.sendNotificayion(body).enqueue(new Callback<>() {
                                        @Override
                                        public void onResponse(Call<FCMResponse> call, Response<FCMResponse> response) {
                                        }
                                        @Override
                                        public void onFailure(Call<FCMResponse> call, Throwable t) {
                                        }
                                    });
                                }
                            }
                        }
                    });
                }
            }
        });
    }

    private void guardadosExistentes(String idPublicacion,String idUsuario, ViewHolder holder) {
        mGuardadosProvider.getGuardadoPublicacion(idPublicacion,idUsuario).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                int numeroDocumentos = queryDocumentSnapshots.size();
                if(numeroDocumentos<=0){
                    holder.imagenGuardado.setImageResource(R.drawable.huella_gris);
                }else{
                    holder.imagenGuardado.setImageResource(R.drawable.huella_naranja);
                }
            }
        });
    }

    private void getInformacionUsuario(String idUsuario, final ViewHolder holder) {
        mUsersProvider.getUsuario(idUsuario).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()){
                    if(documentSnapshot.contains("usuario")){
                        String usuario=documentSnapshot.getString("usuario");
                        holder.lblUsuario.setText(usuario);
                    }
                }
            }
        });
    }

}
