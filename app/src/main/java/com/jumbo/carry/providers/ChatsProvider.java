package com.jumbo.carry.providers;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.jumbo.carry.models.Chat;

import java.util.ArrayList;

public class ChatsProvider {
    CollectionReference mCollectionReference;
    public ChatsProvider(){
       mCollectionReference= FirebaseFirestore.getInstance().collection("Chats");
    }

    public void crear(Chat chat){
        mCollectionReference.document(chat.getIdUsuarioEmisor()+chat.getIdUsuarioDestino()).set(chat);
    }

    public Query getAll(String idUsuario){
        return mCollectionReference.whereArrayContains("ids",idUsuario);
    }
    public Query getChatUsuarioEmisorUsuarioReceptor(String idUsuarioEmisor,String idUsuarioReceptor){
        ArrayList<String>ids= new ArrayList<>();
        ids.add(idUsuarioEmisor+idUsuarioReceptor);
        ids.add(idUsuarioReceptor+idUsuarioEmisor);
        return mCollectionReference.whereIn("idChat",ids);
    }

}
