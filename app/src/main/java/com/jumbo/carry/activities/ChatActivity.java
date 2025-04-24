package com.jumbo.carry.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.jumbo.carry.R;
import com.jumbo.carry.adapters.MensajeAdapter;
import com.jumbo.carry.adapters.MiPublicacionAdapter;
import com.jumbo.carry.models.Chat;
import com.jumbo.carry.models.FCMBody;
import com.jumbo.carry.models.FCMResponse;
import com.jumbo.carry.models.Mensaje;
import com.jumbo.carry.models.Publicacion;
import com.jumbo.carry.providers.AuthProvider;
import com.jumbo.carry.providers.ChatsProvider;
import com.jumbo.carry.providers.MensajeProvider;
import com.jumbo.carry.providers.NotificacionesProvider;
import com.jumbo.carry.providers.TokenProvider;
import com.jumbo.carry.providers.UsersProvider;
import com.jumbo.carry.utils.MensajeVistoHelper;
import com.jumbo.carry.utils.RelativeTime;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatActivity extends AppCompatActivity {
    String mIdUsuarioReceptor;
    String mIdUsuarioEmisor;
    String mIdChat;
    Long idNotificacion;
    ChatsProvider mChatsProvider;
    MensajeProvider mensajeProvider;
    AuthProvider mAuthProvider;
    UsersProvider mUsersProvider;
    NotificacionesProvider mNotificacionesProvider;
    TokenProvider mTokenProvider;
    EditText mTxtMensaje;
    CircleImageView mBtnEnviar;
    CircleImageView mFotoRemitente;
    CircleImageView mBtnAtras;
    MensajeAdapter mensajeAdapter;
    RecyclerView mRecyclerView;
    TextView mUsuarioRemitente;
    TextView mUltimaConexion;
    View mActionBarView;
    LinearLayoutManager linearLayoutManager;
    ListenerRegistration mListenerRegistration;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        mIdUsuarioReceptor=getIntent().getStringExtra("idUsuarioReceptor");
        mIdUsuarioEmisor=getIntent().getStringExtra("idUsuarioEmisor");
        mIdChat=getIntent().getStringExtra("idChat");
        mChatsProvider= new ChatsProvider();
        mensajeProvider= new MensajeProvider();
        mAuthProvider= new AuthProvider();
        mUsersProvider= new UsersProvider();
        mTokenProvider= new TokenProvider();
        mNotificacionesProvider= new NotificacionesProvider();
        mRecyclerView=findViewById(R.id.recyclerViewMensajes);
        linearLayoutManager = new LinearLayoutManager(ChatActivity.this);
        linearLayoutManager.setStackFromEnd(true);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mTxtMensaje=findViewById(R.id.txtMensaje);
        mBtnEnviar=findViewById(R.id.btnEnviar);
        mostrarToolBar(R.layout.custom_chat_toolbar);

        mBtnEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EnviarMensaje();
            }
        });
        chatExistente();
    }

    private void obtenerMensajes(){
        Query query = mensajeProvider.getMensajesChat(mIdChat);
        FirestoreRecyclerOptions<Mensaje> opciones =
                new FirestoreRecyclerOptions.Builder<Mensaje>().setQuery(query,Mensaje.class).build();
        mensajeAdapter = new MensajeAdapter(opciones,ChatActivity.this);
        mensajeAdapter.startListening();
        mensajeAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                actualizarVisto();
                int numeroMensajes = mensajeAdapter.getItemCount();
                int posicionUltimoMensaje=linearLayoutManager.findLastCompletelyVisibleItemPosition();
                if(posicionUltimoMensaje==-1||(positionStart>=(numeroMensajes-1)&&posicionUltimoMensaje==(positionStart-1))){
                    mRecyclerView.scrollToPosition(positionStart);
                }
            }
        });
        mRecyclerView.setAdapter(mensajeAdapter);

    }
    private void enviarNotificacion(final String mensaje) {
        String idUsuario="";
        if(mAuthProvider.getUid().equals(mIdUsuarioEmisor)){
            idUsuario=mIdUsuarioReceptor;
        }
        else{
            idUsuario=mIdUsuarioEmisor;
        }
        mTokenProvider.getToken(idUsuario)
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()&&documentSnapshot.contains("token")){

                    mUsersProvider.getUsuario(mAuthProvider.getUid())
                            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot2) {
                            if(documentSnapshot2.exists()){
                                if(documentSnapshot2.contains("usuario")){
                                    String usuarioActual=documentSnapshot2.getString("usuario");
                                    String token =documentSnapshot.getString("token");
                                    Map<String,String> data = new HashMap<>();
                                    data.put("title",usuarioActual);
                                    data.put("body",mensaje);
                                    data.put("idNotificacion",idNotificacion.toString());
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
    private void EnviarMensaje() {
        String mensaje;
       mensaje= mTxtMensaje.getText().toString();
       if(!mensaje.isEmpty()){
           final Mensaje mensaje1 = new Mensaje();
           mensaje1.setIdChat(mIdChat);
           if(mAuthProvider.getUid().equals(mIdUsuarioEmisor)){
               mensaje1.setIdEmisor(mIdUsuarioEmisor);
               mensaje1.setIdReceptor(mIdUsuarioReceptor);
           }else{
               mensaje1.setIdEmisor(mIdUsuarioReceptor);
               mensaje1.setIdReceptor(mIdUsuarioEmisor);
           }
           mensaje1.setFechaEnviador(new Date().getTime());
           mensaje1.setVisto(false);
           mensaje1.setContenido(mensaje);
           mensajeProvider.create(mensaje1).addOnCompleteListener(new OnCompleteListener<Void>() {
               @Override
               public void onComplete(@NonNull Task<Void> task) {
                   if(task.isSuccessful()){
                       mTxtMensaje.setText("");
                       mensajeAdapter.notifyDataSetChanged();
                       enviarNotificacion(mensaje1.getContenido());
                   }
               }
           });
       }
    }

    private void mostrarToolBar(int recurso) {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("");
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        LayoutInflater inflater =(LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mActionBarView=inflater.inflate(recurso,null);
        actionBar.setCustomView(mActionBarView);
        mFotoRemitente = mActionBarView.findViewById(R.id.fotoUsuarioRemitente);
        mUsuarioRemitente = mActionBarView.findViewById(R.id.usuarioChatRemitente);
        mUltimaConexion = mActionBarView.findViewById(R.id.ultimaConexion);
        mBtnAtras= mActionBarView.findViewById(R.id.btnAtras);
        mBtnAtras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        getInformacionUsuario();
    }

    private void getInformacionUsuario() {
        String idUsuarioRemitente="";
        if(mAuthProvider.getUid().equals(mIdUsuarioEmisor)){
            idUsuarioRemitente=mIdUsuarioReceptor;
        }else{
            idUsuarioRemitente=mIdUsuarioEmisor;
        }
        mListenerRegistration=mUsersProvider.getUsuarioTiempoReal(idUsuarioRemitente)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if(value.exists()){
                    if(value.contains("usuario")){
                        String usuario = value.getString("usuario");
                        mUsuarioRemitente.setText(usuario);
                    }
                    if(value.contains("foto_perfil")){
                        String fotoPerfil = value.getString("foto_perfil");
                        if(fotoPerfil!=null&&!fotoPerfil.equals("")){
                            Picasso.with(ChatActivity.this).load(fotoPerfil).into(mFotoRemitente);
                        }
                    }
                    if(value.contains("online")&&value.contains("ultima_conexion")){
                        boolean online = value.getBoolean("online");
                        if(online){
                            mUltimaConexion.setText("En línea");
                        }else{
                            Long ultimaConexion=value.getLong("ultima_conexion");
                            String relativeTime = RelativeTime.getTimeAgo(ultimaConexion,ChatActivity.this);
                            mUltimaConexion.setText(relativeTime);
                        }
                    }
                }
            }
        });
    }

    private void chatExistente(){
        mChatsProvider.getChatUsuarioEmisorUsuarioReceptor(mIdUsuarioEmisor,mIdUsuarioReceptor)
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                int size= queryDocumentSnapshots.size();
                if(size==0){
                    crearChat();
                }
                else{
                    mIdChat=queryDocumentSnapshots.getDocuments().get(0).getId();
                    idNotificacion=queryDocumentSnapshots.getDocuments().get(0)
                            .getLong("idNotificacion");
                    obtenerMensajes();
                    actualizarVisto();
                }
            }
        });
    }

    private void actualizarVisto() {
        String idSender="";
        if(mAuthProvider.getUid().equals(mIdUsuarioEmisor)){
            idSender=mIdUsuarioReceptor;
        }else{
            idSender=mIdUsuarioEmisor;
        }
        mensajeProvider.getMensajesChatYEmisor(mIdChat,idSender).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for(DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()){
                    mensajeProvider.actualizarVisto(documentSnapshot.getId(),true);
                }
            }
        });
    }

    private void crearChat() {
        Chat chat = new Chat();
        chat.setIdUsuarioDestino(mIdUsuarioReceptor);
        chat.setIdUsuarioEmisor(mIdUsuarioEmisor);
        chat.setEscribiendo(false);
        chat.setFechaCreacion(new Date().getTime());
        chat.setIdChat(mIdUsuarioEmisor+mIdUsuarioReceptor);
        ArrayList<String>ids= new ArrayList<>();
        ids.add(mIdUsuarioEmisor);
        ids.add(mIdUsuarioReceptor);
        chat.setIds(ids);
        Random r = new Random();
        int n=r.nextInt(1000000);
        chat.setIdNotificacion(n);
        idNotificacion= Long.valueOf(n);
        mChatsProvider.crear(chat);
        mIdChat=chat.getIdChat();
        obtenerMensajes();
    }
    @Override
    public void onStart() {
        super.onStart();
        if(mensajeAdapter!=null){
            mensajeAdapter.startListening();
        }
        MensajeVistoHelper.actualizarOnline(true,ChatActivity.this);

    }

    @Override
    public void onStop() {
        super.onStop();
        if(mensajeAdapter!=null){
            mensajeAdapter.stopListening();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        MensajeVistoHelper.actualizarOnline(false,ChatActivity.this);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mListenerRegistration!=null){
            mListenerRegistration.remove();
        }
    }

}