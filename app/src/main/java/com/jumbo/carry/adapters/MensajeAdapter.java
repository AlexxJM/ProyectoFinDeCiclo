package com.jumbo.carry.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.jumbo.carry.R;
import com.jumbo.carry.activities.ChatActivity;
import com.jumbo.carry.models.Mensaje;
import com.jumbo.carry.providers.AuthProvider;
import com.jumbo.carry.providers.UsersProvider;
import com.jumbo.carry.utils.RelativeTime;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class MensajeAdapter extends FirestoreRecyclerAdapter<Mensaje, MensajeAdapter.ViewHolder> {

    Context context;
    UsersProvider mUsersProvider;
    AuthProvider mAuthProvider;

    public MensajeAdapter(FirestoreRecyclerOptions<Mensaje> options, Context context) {
        super(options);
        this.context = context;
        mUsersProvider = new UsersProvider();
        mAuthProvider= new AuthProvider();
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull Mensaje mensaje) {
        holder.textViewMensaje.setText(mensaje.getContenido());
        String realtiveTime= RelativeTime.timeFormatAMPM(mensaje.getFechaEnviador(),context);
        holder.textViewFecha.setText(realtiveTime);
        if(mensaje.getIdEmisor().equals(mAuthProvider.getUid())){
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT
            );
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            params.setMargins(150,0,0,0);
            holder.linearLayout.setLayoutParams(params);
            holder.linearLayout.setPadding(30,20,0,20);
            holder.linearLayout.setBackground(context.getResources().getDrawable(R.drawable.rounded_linear_layout_gris));
            holder.visto.setVisibility(View.VISIBLE);
        }else{
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT
            );
            params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            params.setMargins(0,0,150,0);
            holder.linearLayout.setLayoutParams(params);
            holder.linearLayout.setPadding(20,20,30,20);
            holder.linearLayout.setBackground(context.getResources().getDrawable(R.drawable.rounded_linear_layout));
            holder.visto.setVisibility(View.GONE);
        }
        if(mensaje.isVisto()){
            holder.visto.setImageResource(R.drawable.visto);
        }else{
            holder.visto.setImageResource(R.drawable.no_visto);
        }

    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_mensaje, parent, false);
        return new ViewHolder(view);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewMensaje;
        TextView textViewFecha;
        ImageView visto;
        LinearLayout linearLayout;
        View viewHolder;

        public ViewHolder(View view) {
            super(view);
            textViewMensaje = view.findViewById(R.id.contenidoMensaje);
            textViewFecha = view.findViewById(R.id.fechaMensaje);
            visto = view.findViewById(R.id.vistoMensaje);
            linearLayout=view.findViewById(R.id.linearLayoutMensajes);
            viewHolder = view;
        }
    }

}