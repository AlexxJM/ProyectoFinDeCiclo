package com.jumbo.carry.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;
import com.jumbo.carry.R;
import com.jumbo.carry.activities.ChatActivity;
import com.jumbo.carry.models.Chat;
import com.jumbo.carry.providers.AuthProvider;
import com.jumbo.carry.providers.ChatsProvider;
import com.jumbo.carry.providers.MensajeProvider;
import com.jumbo.carry.providers.UsersProvider;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatsAdapter extends FirestoreRecyclerAdapter<Chat, ChatsAdapter.ViewHolder> {
    Context context;
    UsersProvider mUsersProvider;
    AuthProvider mAuthProvider;
    MensajeProvider mensajeProvider;
    ListenerRegistration mListenerRegistration;
    public ChatsAdapter(FirestoreRecyclerOptions<Chat> options, Context context) {
        super(options);
        this.context = context;
        mUsersProvider = new UsersProvider();
        mAuthProvider= new AuthProvider();
        mensajeProvider= new MensajeProvider();
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull Chat chat) {

        DocumentSnapshot document = getSnapshots().getSnapshot(position);
        final String chatId = document.getId();
        if(mAuthProvider.getUid().equals(chat.getIdUsuarioEmisor())){
            getUserInfo(chat.getIdUsuarioDestino(), holder);

        }else{
            getUserInfo(chat.getIdUsuarioEmisor(), holder);
        }
        obtenerUltimoMensaje(chatId,holder.textViewLastMessage);

        String idSender="";
        if(mAuthProvider.getUid().equals(chat.getIdUsuarioEmisor())){
            idSender=chat.getIdUsuarioDestino();
        }else{
            idSender=chat.getIdUsuarioEmisor();
        }
        obtenerMensajesNoLeidos(chatId,idSender,holder.textViewNumeroMensajes,holder.mFrameLayout);
        holder.viewHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                irAlChat(chatId,chat.getIdUsuarioEmisor(),chat.getIdUsuarioDestino());
            }
        });

    }

    private void obtenerMensajesNoLeidos(String chatId, String idSender, TextView textViewNumeroMensajes, FrameLayout mFrameLayout) {
       mListenerRegistration=mensajeProvider.getMensajesChatYEmisor(chatId,idSender).addSnapshotListener(new EventListener<QuerySnapshot>() {
           @Override
           public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
               if(value!=null){
                   int size=value.size();
                   if(size>0){
                       mFrameLayout.setVisibility(View.VISIBLE);
                       textViewNumeroMensajes.setText(String.valueOf(size));
                   }else{
                       mFrameLayout.setVisibility(View.GONE);
                   }
               }
           }
       });
    }

    public ListenerRegistration getmListenerRegistration(){
        return mListenerRegistration;
    }

    private void obtenerUltimoMensaje(String chatId, TextView textViewLastMessage) {
        mensajeProvider.getUltimoMensaje(chatId).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                int size= queryDocumentSnapshots.size();
                if(size>0){
                    textViewLastMessage.setText(queryDocumentSnapshots.getDocuments().get(0).getString("contenido"));
                }
            }
        });
    }

    private void irAlChat(String chatId,String idUsuarioEmisor,String idUsuarioReceptor) {
        Intent intent = new Intent(context, ChatActivity.class);
        intent.putExtra("idChat",chatId);
        intent.putExtra("idUsuarioEmisor",idUsuarioEmisor);
        intent.putExtra("idUsuarioReceptor",idUsuarioReceptor);
        context.startActivity(intent);
    }

    private void getUserInfo(String idUser, final ViewHolder holder) {
        mUsersProvider.getUsuario(idUser).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    if (documentSnapshot.contains("usuario")) {
                        String username = documentSnapshot.getString("usuario");
                        holder.textViewUsername.setText(username);
                    }
                    if (documentSnapshot.contains("foto_perfil")) {
                        String imageProfile = documentSnapshot.getString("foto_perfil");
                        if (imageProfile != null) {
                            if (!imageProfile.isEmpty()) {
                                Picasso.with(context).load(imageProfile).into(holder.circleImageChat);
                            }
                        }
                    }
                }
            }
        });
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_chat, parent, false);
        return new ViewHolder(view);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewUsername;
        TextView textViewLastMessage;
        TextView textViewNumeroMensajes;
        CircleImageView circleImageChat;
        FrameLayout mFrameLayout;
        View viewHolder;
        public ViewHolder(View view) {
            super(view);
            textViewUsername = view.findViewById(R.id.usuarioChat);
            textViewLastMessage = view.findViewById(R.id.ultimoMensajeChat);
            textViewNumeroMensajes = view.findViewById(R.id.numeroMensajes);
            circleImageChat = view.findViewById(R.id.fotoUsuarioChat);
            mFrameLayout=view.findViewById(R.id.fondoNumeroMensajes);
            viewHolder = view;
        }
    }

}