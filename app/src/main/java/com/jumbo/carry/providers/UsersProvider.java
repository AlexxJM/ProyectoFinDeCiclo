package com.jumbo.carry.providers;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.jumbo.carry.models.Usuario;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class UsersProvider {
    private CollectionReference mCollection;
    public UsersProvider(){
        mCollection = FirebaseFirestore.getInstance().collection("Usuarios");
    }

    public Task<DocumentSnapshot> getUsuario(String id){
        return mCollection.document(id).get();
    }
    public DocumentReference getUsuarioTiempoReal(String id){
        return mCollection.document(id);
    }

    public Task <Void> crear(Usuario usuario){
       return mCollection.document(usuario.getId()).set(usuario);
    }

    public Task <Void> actualizar(Usuario usuario){
        Map<String, Object> map = new HashMap<>();
        map.put("usuario",usuario.getUsuario());
        map.put("numero",usuario.getNumero());
        map.put("fechaCreacion",new Date().getTime());
        map.put("foto_perfil",usuario.getFoto_perfil());
        map.put("foto_portada",usuario.getFoto_portada());


        return  mCollection.document(usuario.getId()).update(map);
    }
    public Task <Void> actualizarEstado(String  idUsuario,boolean estado){
        Map<String, Object> map = new HashMap<>();
        map.put("online",estado);
        map.put("ultima_conexion",new Date().getTime());
        return  mCollection.document(idUsuario).update(map);
    }
}
