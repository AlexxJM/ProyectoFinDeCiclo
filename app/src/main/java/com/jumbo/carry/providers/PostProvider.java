package com.jumbo.carry.providers;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.jumbo.carry.models.Publicacion;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PostProvider {
    CollectionReference mCollection;
    public PostProvider(){
        mCollection= FirebaseFirestore.getInstance().collection("Publicaciones");
    }
    public Task<Void> subir (Publicacion publicacion){
        DocumentReference documentReference = mCollection.document();
        publicacion.setId(documentReference.getId());
        return documentReference.set(publicacion);
    }

    public Query getAll(){
       return mCollection.orderBy("fechaCreacion",Query.Direction.DESCENDING);
    }
    public Query getPublicacionPorTitulo(String titulo){
        return mCollection.orderBy("titulo").startAt(titulo).endAt(titulo+'\uf8ff');
    }
    public Query getPostTotalesUsuario(String idUsuario){
        return mCollection.whereEqualTo("idUsuario",idUsuario);
    }
    public Task <Void> delete (String id){
        return mCollection.document(id).delete();

    }
    public Task<DocumentSnapshot> getPostById(String id){
        return mCollection.document(id).get();
    }

}
