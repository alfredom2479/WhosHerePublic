package com.example.whosherejava;

import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.List;


public class StudentList extends ArrayAdapter {

    private Activity context;
    private Context context2;
    private List<Student> studentList;

    StorageReference picStorageReference=FirebaseStorage.getInstance().getReference("pictures");

//    ImageView faceTV;
    Uri tempUri=null;

    public StudentList(@NonNull Activity context, List<Student> studentList) {
        super(context, R.layout.student_row, studentList);
        this.context=context;
        this.studentList=studentList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        LayoutInflater inflater = context.getLayoutInflater();

        final CardView cardViewItem = (CardView) inflater.inflate(R.layout.student_row,null, true);

        TextView nameTV = (TextView) cardViewItem.getChildAt(0).findViewById(R.id.rowNameTV);
        TextView statusTV = (TextView) cardViewItem.getChildAt(0).findViewById(R.id.rowAttendanceTV);
        final ImageView faceTV = (ImageView) cardViewItem.getChildAt(0).findViewById(R.id.rowFaceIV);

        final Student student = studentList.get(position);

        nameTV.setText(student.getName());
        statusTV.setText(student.getStatus().toString());
        Log.d("student name", student.getName());
        Log.d("kill me",student.getImageName());
        picStorageReference.child(student.getImageName()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                tempUri=uri;

                Picasso.with(context).load(uri).into(faceTV);
            }

        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, student.getName()+": Failed to get Uri",Toast.LENGTH_LONG).show();
            }
        });
//        Picasso.with(context).load(tempUri).into(faceTV);
        Log.d("statusInAdapter",student.getName()+": "+student.getStatus().toString());
        Student.Status stat = student.getStatus();
        switch (stat){
            case PRESENT:
                cardViewItem.setCardBackgroundColor(Color.parseColor("#bbdbb5"));
                break;
            case ABSENT:
                cardViewItem.setCardBackgroundColor(Color.parseColor("#f4bdbd"));
                break;
            case OTHER:
                cardViewItem.setCardBackgroundColor(Color.parseColor("#f1ebc3"));
                break;
            default:
                cardViewItem.setCardBackgroundColor(Color.parseColor("#FF00FF"));
                Log.d("case", "default");
        }


        return cardViewItem;
    }
}
