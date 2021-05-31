package com.example.lock;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class QrcodeActivity extends AppCompatActivity {
    ImageView imageView;
    String qrCodeName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode);
        imageView=findViewById(R.id.imageView);

        Intent intent=getIntent();
        qrCodeName=intent.getStringExtra("bicycleName");
        getDownloadURL(new DownloadCompleted() {
            @Override
            public void onDownloadComplete() {
                DocumentReference documentReference=FirebaseFirestore.getInstance().collection("LockStatus").
                        document("7p1xGymo3PhtIaMSxz1z");
                documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                        Map<String,Object> map=value.getData();
                        boolean locked=Boolean.parseBoolean(map.get("locked").toString());
                        if(!locked){
                            Toast.makeText(QrcodeActivity.this, "Enjoy the ride", Toast.LENGTH_SHORT).show();
                            imageView.setVisibility(View.INVISIBLE);
                        }
                    }
                });
            }
        });


    }
    void getDownloadURL(DownloadCompleted downloadCompleted){
        StorageReference storageReference= FirebaseStorage.getInstance().getReference("qrcode/"+qrCodeName+".png");

        storageReference.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                String imageURL=task.getResult().toString();
                Picasso.get().load(imageURL).into(imageView);
                downloadCompleted.onDownloadComplete();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(QrcodeActivity.this, "Failed to load qr code", Toast.LENGTH_SHORT).show();
            }
        });
    }
    void updateValue(Updated updated){
        FirebaseFirestore firebaseFirestore=FirebaseFirestore.getInstance();
        Map<String,Boolean> map=new HashMap<>();
        map.put("locked",true);
        firebaseFirestore.collection("LockStatus").document("7p1xGymo3PhtIaMSxz1z").set(map, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(QrcodeActivity.this, "Value updated in the database", Toast.LENGTH_SHORT).show();
                    updated.onValueUpdated();
                }
            }
        });
    }



    public interface Updated{
        void onValueUpdated();
    }
    public interface DownloadCompleted{
        void onDownloadComplete();
    }
}