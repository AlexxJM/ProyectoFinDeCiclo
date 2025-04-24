package com.jumbo.carry.providers;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceIdReceiver;
import com.google.firebase.iid.internal.FirebaseInstanceIdInternal;
import com.google.firebase.messaging.FirebaseMessaging;
import com.jumbo.carry.models.Token;

public class TokenProvider {
    CollectionReference mCollectionReference;
    public TokenProvider(){
        mCollectionReference= FirebaseFirestore.getInstance().collection("Tokens");
    }
    public void crear (String idUsuario){
        if(idUsuario==null){
            return;
        }
        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(new OnSuccessListener<String>() {
            @Override
            public void onSuccess(String s) {
                Token token = new Token (s);
                mCollectionReference.document(idUsuario).set(token);
            }
        });
    }
    public Task<DocumentSnapshot> getToken(String idUsuario){
        return mCollectionReference.document(idUsuario).get();
    }
}
