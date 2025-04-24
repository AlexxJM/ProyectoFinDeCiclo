package com.jumbo.carry.providers;

import android.content.Context;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.jumbo.carry.utils.CompressorBitmapImage;

import java.io.File;
import java.util.Date;

public class ImageProvider {
    StorageReference mStorage;
    public ImageProvider(){
        mStorage= FirebaseStorage.getInstance().getReference();
    }

    public UploadTask guardar(Context context, File file){
        byte[] imagenBytes= CompressorBitmapImage.getImage(context, file.getPath(), 500,500);
        StorageReference storage = FirebaseStorage.getInstance().getReference().child(new Date()+".jpg");
        mStorage=storage;
        UploadTask task=storage.putBytes(imagenBytes);
        return task;
    }

    public StorageReference getStorgae(){
            return mStorage;
    }
}
