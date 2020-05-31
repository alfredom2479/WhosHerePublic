package com.example.whosherejava;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


/**
 * A simple {@link Fragment} subclass.
 */
public class ThirdFragment extends Fragment {

    FirebaseAuth auth;

    DatabaseReference myDB, classDB;
    StorageReference pictureStorage;


    List<Class> classList;
    Context context;
    Activity activity;

    RecyclerView recyclerView;
    Button classesAddStudentButt;

    public ThirdFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment


        return inflater.inflate(R.layout.fragment_third, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        auth = FirebaseAuth.getInstance();

        if(auth.getCurrentUser() != null){
            myDB= FirebaseDatabase.getInstance().getReference
                    ("users/"+auth.getCurrentUser().getUid());
            classDB = myDB.child("classes");
            pictureStorage = FirebaseStorage.getInstance().getReference("pictures");

        }


        classList = new ArrayList<>();

        classesAddStudentButt = view.findViewById(R.id.butts);

//        classList.add(new Class("anus1","more anus",69));
//        classList.add(new Class("anus2","more anus",70));
//        classList.add(new Class("anus3","more anus",80));

        recyclerView= view.findViewById(R.id.classesRV);

        classesAddStudentButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddClassDialog();
//                RecyclerAdapter adapter = new RecyclerAdapter(context, classList);
//                recyclerView.setLayoutManager(new GridLayoutManager(context,2));
//                recyclerView.setAdapter(adapter);
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();

        if (auth.getCurrentUser() != null){

            classDB.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    classList.clear();
                    for(DataSnapshot  classSnap: dataSnapshot.getChildren()){
                        Class elClass = classSnap.getValue(Class.class);
                        classList.add(elClass);
                    }
                    RecyclerAdapter adapter = new RecyclerAdapter(context,activity, classList);
                    recyclerView.setLayoutManager(new GridLayoutManager(context,2));
                    recyclerView.setAdapter(adapter);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    private void showAddClassDialog(){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this.context);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.add_class_dialog,null);
        dialogBuilder.setView(dialogView);

        final EditText addClassET = dialogView.findViewById(R.id.addClassET);
        final Button addClassButt = dialogView.findViewById(R.id.addClassSaveButt);

        addClassET.setEnabled(true);
        addClassButt.setEnabled(true);

        dialogBuilder.setTitle("Add Class");
        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();

        addClassButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadClassToDB(addClassET.getText().toString());
                alertDialog.dismiss();
            }
        });


    }

    private void uploadClassToDB(String name) {

        final String entryKey = classDB.push().getKey();

        Random r = new Random();
        int color = Color.argb(255, r.nextInt(256),r.nextInt(256), r.nextInt(256));

        final Class elClass = new Class(entryKey, name, color, r.nextInt(101) );

        classDB.child(entryKey).setValue(elClass);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context=context;
        this.activity=getActivity();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        this.context=null;
        this.activity=activity;
    }
//    public void addClass(){
//        classList.add(new Class("Testing", "anal",));
//    }
}
