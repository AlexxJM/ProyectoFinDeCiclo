package com.jumbo.carry.providers;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.jumbo.carry.models.Guardado;

public class GuardadosProvider {
    CollectionReference mCollectionReference;
    public GuardadosProvider() {
        mCollectionReference= FirebaseFirestore.getInstance().collection("Guardados");

    }
    public Task<Void> create (Guardado guardado){
        DocumentReference document = mCollectionReference.document();
        String id =document.getId();
        guardado.setId(id);
        return document.set(guardado);
    }
    public Query getGuardadoPublicacion(String idPublicacion,String idUsuario){
        return mCollectionReference.whereEqualTo("idPublicacion", idPublicacion).whereEqualTo("idUsuario",idUsuario);
    }
    public Task<Void> eliminarGuardado (String id){
        return mCollectionReference.document(id).delete();
    }
    public Query getNumeroGuardados(String idPost){
        return mCollectionReference.whereEqualTo("idPublicacion",idPost);
    }
    public Query getGuardadoUsuario(String idUsuario){
        return mCollectionReference.whereEqualTo("idUsuario",idUsuario);
    }
}
