package com.jumbo.carry.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.jumbo.carry.R;
import com.jumbo.carry.activities.ContenidoPublicacionActivity;
import com.jumbo.carry.models.Guardado;
import com.jumbo.carry.models.Publicacion;
import com.jumbo.carry.providers.AuthProvider;
import com.jumbo.carry.providers.GuardadosProvider;
import com.jumbo.carry.providers.PostProvider;
import com.jumbo.carry.providers.UsersProvider;
import com.jumbo.carry.utils.RelativeTime;
import com.squareup.picasso.Picasso;

import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class MiPublicacionAdapter extends FirestoreRecyclerAdapter<Publicacion, MiPublicacionAdapter.ViewHolder> {

    Context context;
    UsersProvider mUsersProvider;
    GuardadosProvider mGuardadosProvider;
    AuthProvider mAuthProvider;
    PostProvider mPostProvider;

    public MiPublicacionAdapter(FirestoreRecyclerOptions<Publicacion> opciones, Context context){
        super(opciones);
        this.context=context;
        mUsersProvider=new UsersProvider();
        mGuardadosProvider= new GuardadosProvider();
        mAuthProvider = new AuthProvider();
        mPostProvider = new PostProvider();
    }
    @Override
    protected void onBindViewHolder(@NonNull final ViewHolder holder, int position, @NonNull final Publicacion publicacion) {
        DocumentSnapshot document = getSnapshots().getSnapshot(position);
        final String postId = document.getId();
        String relativeTime= RelativeTime.getTimeAgo(publicacion.getFechaCreacion(),context);
        holder.lblFechaMiPost.setText(relativeTime);
        holder.lblTituloMiPost.setText(publicacion.getTitulo().toUpperCase());

        if(publicacion.getIdUsuario().equals(mAuthProvider.getUid())){
            holder.btnEliminarMiPost.setVisibility(View.VISIBLE);
        }else{
            holder.btnEliminarMiPost.setVisibility(View.GONE);
        }

        if(publicacion.getImagen1()!=null){
            if(!publicacion.getImagen1().isEmpty()){
                Picasso.with(context).load(publicacion.getImagen1()).into(holder.imagenMiPost);
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
        holder.btnEliminarMiPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmarBorrar(postId);
            }
        });

    }

    private void confirmarBorrar(String postId) {
        new AlertDialog.Builder(context).setIcon(android.R.drawable.ic_dialog_alert).setTitle("Eliminar Publicación").
                setMessage("¿Quieres eliminar la publicación?").setPositiveButton("Eliminar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        borraPublicacion(postId);
                    }
                }).setNegativeButton("Cancelar",null).show();

    }

    private void borraPublicacion(String postId) {
        mPostProvider.delete(postId).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(context,"Publicacion eliminada",Toast.LENGTH_LONG).show();
                }
                else{
                    Toast.makeText(context,"No se pudo eliminar la publicación",Toast.LENGTH_LONG).show();
                }
            }
        });
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cardview_mis_publicaciones,parent,false);
        return new ViewHolder(view);
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView lblTituloMiPost;
        private TextView lblFechaMiPost;
        private ImageView imagenMiPost;
        private ImageView btnEliminarMiPost;
        View viewHolder;
        public ViewHolder (View view){
            super(view);
            lblTituloMiPost=view.findViewById(R.id.tituloMiPublicacion);
            lblFechaMiPost=view.findViewById(R.id.fechaMiPublicacion);
            btnEliminarMiPost=view.findViewById(R.id.btnEliminarMiPublicacion);
            imagenMiPost=view.findViewById(R.id.fotoMiPubllicacion);
            viewHolder=view;
        }
    }
}
