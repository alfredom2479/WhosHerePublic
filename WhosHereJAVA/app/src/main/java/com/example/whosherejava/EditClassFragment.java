package com.example.whosherejava;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import petrov.kristiyan.colorpicker.ColorPicker;


public class EditClassFragment extends Fragment {

    FirebaseAuth auth;

    DatabaseReference myDB, classDB;

    Button editClassChooseColor, editClassDeleteClass;
    Activity activity;
    Color currColor;
    String classID="Default";
    int currColor2;

    public EditClassFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        classID = getArguments().getString("classID");
        Log.d("second frag cid output", classID);
        return inflater.inflate(R.layout.fragment_edit_class, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        auth= FirebaseAuth.getInstance();

        if(auth.getCurrentUser() != null){
            myDB= FirebaseDatabase.getInstance().getReference
                    ("users/"+auth.getCurrentUser().getUid());
            classDB = myDB.child("classes");
        }

        editClassChooseColor= view.findViewById(R.id.editClass_changeColorButt);
        editClassDeleteClass= view.findViewById(R.id.editClass_deleteClass);

        currColor=Color.valueOf(Color.parseColor("#f84c44"));
        currColor2= Color.parseColor("#f84c44");

        editClassChooseColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ColorPicker colorPicker= new ColorPicker(activity);

                colorPicker.setOnFastChooseColorListener(new ColorPicker.OnFastChooseColorListener() {
                    @Override
                    public void setOnFastChooseColorListener(int position, int color) {
                        currColor2=color;
                        classDB.child(classID).child("color").setValue(currColor2);
                    }

                    @Override
                    public void onCancel() {

                    }
                })
                .setDefaultColorButton(currColor2)
                .setColumns(5)
                .show();
            }
        });
    editClassDeleteClass.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            MainActivity mainActivity= (MainActivity) activity;
            ((MainActivity) activity).goBackToStartFrag(classID);
        }
    });
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.activity=getActivity();
    }
}
