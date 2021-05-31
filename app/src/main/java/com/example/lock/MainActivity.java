package com.example.lock;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class MainActivity extends AppCompatActivity implements BicycleList.CycleclickCallback{
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    ArrayList<String> bicycleNames;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView=findViewById(R.id.recyclerView);
        bicycleNames=new ArrayList<>();

        updateValue(new Updated() {
            @Override
            public void onValueUpdated() {
                getAllBicycleNames(new BicycleNamesCallback() {
                    @Override
                    public void onBicycleDataComplete(ArrayList<String> bicycleNames) {
                        for(String s:bicycleNames){
                            System.out.println(s);
                        }
                        BicycleList bicycleList=new BicycleList(bicycleNames,MainActivity.this::onBicycleClicked);
                        recyclerView.setHasFixedSize(true);
                        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
                        recyclerView.setAdapter(bicycleList);


                    }
                });
            }
        });


    }
    public void getAllBicycleNames(BicycleNamesCallback bicycleNamesCallback){
        CollectionReference collectionReference=db.collection("Bicycles");
        collectionReference.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for(DocumentSnapshot documentSnapshot:queryDocumentSnapshots.getDocuments()){
                    bicycleNames.add(String.valueOf(documentSnapshot.get("name")));

                }
                bicycleNamesCallback.onBicycleDataComplete(bicycleNames);
            }
        });
    }

    @Override
    public void onBicycleClicked(int position) {
        String intentString=bicycleNames.get(position);
        Intent intent=new Intent(MainActivity.this,QrcodeActivity.class);
        intent.putExtra("bicycleName",intentString);
        startActivity(intent);
    }

    public interface BicycleNamesCallback{
        void onBicycleDataComplete(ArrayList<String> bicycleNames);

    }


    @Override
    protected void onResume() {

        FirebaseFirestore firebaseFirestore=FirebaseFirestore.getInstance();
        Map<String,Boolean> map=new HashMap<>();
        map.put("locked",true);
        firebaseFirestore.collection("LockStatus").document("7p1xGymo3PhtIaMSxz1z").set(map, SetOptions.merge());
        super.onResume();
    }

    void updateValue(Updated updated){
        FirebaseFirestore firebaseFirestore=FirebaseFirestore.getInstance();
        Map<String,Boolean> map=new HashMap<>();
        map.put("locked",true);
        firebaseFirestore.collection("LockStatus").document("7p1xGymo3PhtIaMSxz1z").set(map, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(MainActivity.this, "Value updated in the database", Toast.LENGTH_SHORT).show();
                    updated.onValueUpdated();
                }
                else{
                    System.out.println("value not");
                }
            }
        });
    }



    public interface Updated{
        void onValueUpdated();
    }
}