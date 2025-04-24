package com.jumbo.carry.providers;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.jumbo.carry.models.Mensaje;

import java.util.HashMap;
import java.util.Map;

public class MensajeProvider {
    CollectionReference mCollectionReference;
    public MensajeProvider(){
        mCollectionReference= FirebaseFirestore.getInstance().collection("Mensajes");
    }
    public Task<Void> create (Mensaje mensaje){
        DocumentReference documentReference = mCollectionReference.document();
        mensaje.setIdMensaje(documentReference.getId());
        return documentReference.set(mensaje);
    }
    public Query getMensajesChat(String idChat){
        return mCollectionReference.whereEqualTo("idChat",idChat).orderBy("fechaEnviador",Query.Direction.ASCENDING);
    }
    public Query getMensajesChatYEmisor(String idChat,String idEmisor){
        return mCollectionReference.whereEqualTo("idChat",idChat).whereEqualTo("idEmisor",idEmisor).whereEqualTo("visto",false);
    }
    public Query getUltimoMensaje(String idChat){
        return mCollectionReference.whereEqualTo("idChat",idChat).orderBy("fechaEnviador",Query.Direction.DESCENDING).limit(1);
    }
    public Task<Void>actualizarVisto(String idDocumento,boolean estado){
        Map<String,Object> map= new HashMap<>();
        map.put("visto",estado);
        return mCollectionReference.document(idDocumento).update(map);
    }
}
