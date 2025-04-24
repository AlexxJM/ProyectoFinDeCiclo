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
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
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

public class MiGuardadosAdapter extends FirestoreRecyclerAdapter<Guardado, MiGuardadosAdapter.ViewHolder> {

    Context context;
    GuardadosProvider mGuardadosProvider;
    PostProvider mPostProvider;
    AuthProvider mAuthProvider;

    public MiGuardadosAdapter(FirestoreRecyclerOptions<Guardado> opciones, Context context){
        super(opciones);
        this.context=context;
        mGuardadosProvider= new GuardadosProvider();
        mAuthProvider = new AuthProvider();
        mPostProvider= new PostProvider();
    }
    @Override
    protected void onBindViewHolder(@NonNull final ViewHolder holder, int position, @NonNull final Guardado guardado) {
        final String postId=guardado.getIdPublicacion();
        mPostProvider.getPostById(postId).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                holder.lblTituloMiPost.setText(documentSnapshot.getString("titulo").toUpperCase());
                String relativeTime= RelativeTime.getTimeAgo(documentSnapshot.getLong("fechaCreacion"),context);
                holder.lblFechaMiPost.setText(relativeTime);
                if(documentSnapshot.getString("imagen1")!=null){
                    if(!documentSnapshot.getString("imagen1").isEmpty()){
                        Picasso.with(context).load(documentSnapshot.getString("imagen1")).into(holder.imagenMiPost);
                    }
                }
            }
        });

        holder.viewHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ContenidoPublicacionActivity.class);
                intent.putExtra("id",postId);
                context.startActivity(intent);
            }
        });

    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_mis_guardados,parent,false);
        return new ViewHolder(view);
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView lblTituloMiPost;
        private TextView lblFechaMiPost;
        private ImageView imagenMiPost;
        View viewHolder;
        public ViewHolder (View view){
            super(view);
            lblTituloMiPost=view.findViewById(R.id.tituloMiPublicacion);
            lblFechaMiPost=view.findViewById(R.id.fechaMiPublicacion);
            imagenMiPost=view.findViewById(R.id.fotoMiPubllicacion);
            viewHolder=view;
        }
    }
}
