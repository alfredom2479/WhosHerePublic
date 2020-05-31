package com.example.whosherejava;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.icu.util.Calendar;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.ui.AppBarConfiguration;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;

public class SecondFragment extends Fragment {

    String classID="defaultVal";


    private FirebaseAuth auth;
    private TextView studentsDate;
    private ListView mainLV;
    public ImageView kidsFaceIV;
    private Button mainSwitchUserButt, mainAddClassPicButt, mainAddStudentButt, mainToMain2;

    private AppBarConfiguration appBarConfiguration;

    private Activity sendHelp;
    private Context daContext;

    public List<Student> studentList;

    private static final int PICK_IMAGE_REQUEST=69, PICK_CLASS_IMAGE_REQUEST=420;

    private Uri uriHolder;

    private DatabaseReference myDB, studentsDB, datesDB;
    private StorageReference picStorageReference;
    private DatabaseReference picDBReference, classPicDBRef;

    private String globalCurrentDate ="default value";
    private Student.Status loopStatus= Student.Status.OTHER;
    private Student thatNigga;

    public SecondFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getParentFragmentManager();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        classID = getArguments().getString("classID");
        globalCurrentDate = getArguments().getString("currDate");
        Log.d("second frag cid output", classID);
        return inflater.inflate(R.layout.fragment_second, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        studentList= new ArrayList<>();

        //Firebase References Set Up
        ////////////////////////////////////////////////////////////////////////////////////////////
        auth = FirebaseAuth.getInstance();
        if(auth.getCurrentUser()!=null) {
            myDB = FirebaseDatabase.getInstance().getReference
                    ("users/" + auth.getCurrentUser().getUid());
            myDB.child("email").setValue(auth.getCurrentUser().getEmail());
            studentsDB = myDB.child("students");
            datesDB = myDB.child("dates");
        }

        picStorageReference= FirebaseStorage.getInstance().getReference("pictures");
        picDBReference= FirebaseDatabase.getInstance().getReference("pictures");
        classPicDBRef = FirebaseDatabase.getInstance().getReference("ClassPicture");

        //Top Half of Main Activity Set Up
        ////////////////////////////////////////////////////////////////////////////////////////////

//        mainUsernameTV = view.findViewById(R.id.mainUsernameTV3);
//        mainSwitchUserButt = view.findViewById(R.id.mainSwitchUserButt);
//        mainAddClassPicButt = view.findViewById(R.id.mainAddClassPicButt3);
        mainAddStudentButt = view.findViewById(R.id.mainAddStudentButt3);


        //For facial recognition !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

//        mainAddClassPicButt.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                openFileChooser(PICK_CLASS_IMAGE_REQUEST);
//            }
//        });
        mainAddStudentButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddStudentDialog();
            }
        });


        //List In Main Activity Set Up
        ////////////////////////////////////////////////////////////////////////////////////////////
        mainLV = (ListView) view.findViewById(R.id.mainLV3);
        mainLV.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Student student= studentList.get(position);
                showEditStudentDialog(student.getSid(), student.getName(),student.getStatus(), student.getImageName());
                return false;
            }
        });

        studentsDate= view.findViewById(R.id.students_date);

        studentsDate.setText(globalCurrentDate);
        studentsDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initializeDateSelecting();
            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();

        //Update UI And Set Event Listener For Student Database
        ////////////////////////////////////////////////////////////////////////////////////////////

        if(auth.getCurrentUser() != null) {

            //New Way test code
            ////////////////////////////////////////////////////////////////////////////////////////
            FirebaseUser currentUser = auth.getCurrentUser();
//            updateUI(currentUser);

            myDB.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    studentList.clear();
                    Log.d("fragClassId",classID);
                    DataSnapshot studentsSnap=dataSnapshot.child("students");
                    DataSnapshot classesSnap= dataSnapshot.child("classes");
                    DataSnapshot datesSnap = dataSnapshot.child("dates");

                    for(DataSnapshot studentSnapshot: studentsSnap.getChildren()){

                        final Student student= studentSnapshot.getValue(Student.class);
                        Log.d("StudentCid",student.getName()+" cid: "+student.getCid());
                        if (student.getCid().equals(classID)){
                            String usableDate = globalCurrentDate.replace('/','-');

                            if(datesSnap.child(usableDate).child(student.getSid()).exists()){
                                Log.d("TestingDates",student.getName()+" is in date");
                                String statusFromDateSnap= ((String)datesSnap.child(usableDate).child(student.getSid()).getValue());
                                Log.d("statusVal",datesSnap.child(usableDate).child(student.getSid()).getValue().toString());
                                Student.Status statusVal = Student.Status.valueOf(statusFromDateSnap);
                                student.setStatus(statusVal);

                            }
                            else{
                                Log.d("TestingDates",student.getName()+" is NOT in date");
                                student.setStatus(Student.Status.OTHER);
                            }


//                            Log.d("noInfo?2", student.getName()+": "+student.getStatus().toString());
//                            studentList.add(thatNigga);
                            studentList.add(student);
                        }
                    }
//                    Log.d("current activity: ", (sendHelp).toString());

                    StudentList adapter = new StudentList(sendHelp,studentList);
                    mainLV.setAdapter(adapter);
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });



        }
        else{
            Log.d("authStatus","There is no current User");
//            goToSignIn();
        }




    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        sendHelp=getActivity();
        daContext=context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
//        sendHelp=null;

    }

    public void showAddStudentDialog(){
        uriHolder=null;

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_template,null);
        dialogBuilder.setView(dialogView);

        final EditText addStudentDialogNameET = (EditText) dialogView.findViewById(R.id.addStudentDialogNameET);
        final Spinner addStudentDialogStatusSpin= (Spinner) dialogView.findViewById(R.id.addStudentDialogStatusSpin);
        final Button addStudentDialogChoosePicButt=(Button) dialogView.findViewById(R.id.addStudentDialogChoosePicButt);
        final Button addStudentSaveButt = (Button) dialogView.findViewById(R.id.addStudentSaveButt);
        kidsFaceIV= (ImageView) dialogView.findViewById(R.id.addStudentDialogFaceIV);

        addStudentDialogNameET.setEnabled(true);
        addStudentDialogStatusSpin.setEnabled(true);
        addStudentDialogChoosePicButt.setEnabled(true);
        addStudentSaveButt.setEnabled(true);

        dialogBuilder.setTitle("Add Student");
        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();

        addStudentDialogChoosePicButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser(PICK_IMAGE_REQUEST);
            }
        });

        addStudentSaveButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(uriHolder!=null){
//                    String imageFileNameInStorage= uploadStudentPicture();
//                    Student.Status selectedStatus = Student.Status.valueOf(addStudentDialogStatusSpin.getSelectedItem().toString());

//                    Student student = new Student(entryKey,addStudentDialogNameET.getText().toString(), selectedStatus, imageFileNameInStorage);

                    uploadStudentPicture(addStudentDialogNameET.getText().toString(), Student.Status.valueOf(addStudentDialogStatusSpin.getSelectedItem().toString()));

                    alertDialog.dismiss();
                }
                else {
                    Toast.makeText(getContext(), "No image Selected!", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void showEditStudentDialog(final String studentID, String studentName, Student.Status studentStatus, final String imageFileName){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView=inflater.inflate(R.layout.edit_student_dialog,null);
        dialogBuilder.setView(dialogView);

        final EditText editDialogNameET = dialogView.findViewById(R.id.editDialogNameET);
        final Spinner editDialogStatusSpin = dialogView.findViewById(R.id.editDialogStatusSpin);
        final Button saveButt=  dialogView.findViewById(R.id.editDialogSaveButt);
        final Button murderButt= dialogView.findViewById(R.id.editDialogMurderButt);

        editDialogNameET.setEnabled(true);
        editDialogStatusSpin.setEnabled(true);
        saveButt.setEnabled(true);
        murderButt.setEnabled(true);

        editDialogNameET.setText(studentName);
        editDialogStatusSpin.setSelection(studentStatus.ordinal());

        dialogBuilder.setTitle(studentName);
        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();

        saveButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name=editDialogNameET.getText().toString();
                Student.Status status= Student.Status.valueOf(editDialogStatusSpin.getSelectedItem().toString());

                if(TextUtils.isEmpty(name)){
                    editDialogNameET.setError("Insert Name");
                }
                else {
                    updateStudent(studentID, name, status, imageFileName);
                    alertDialog.dismiss();
                }
            }
        });

        murderButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteStudent(studentID);
                alertDialog.dismiss();
            }
        });

    }

    private String uploadStudentPicture(String name, final Student.Status status){
        String newFileName="";

        if(uriHolder !=null){
            final String entryKey=studentsDB.push().getKey();

            newFileName=System.currentTimeMillis() +"."+ getFileExtension(uriHolder);
            StorageReference fileReference = picStorageReference.child(newFileName);

            final Student student = new Student(entryKey, classID, name, status, newFileName);

            fileReference.putFile(uriHolder).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Log.d("StudentPicUpload", "SUCCESSFUL student picture upload");
                    studentsDB.child(entryKey).setValue(student);
                    String usableDate = globalCurrentDate.replace('/','-');
                    datesDB.child(usableDate).child(entryKey).setValue(status);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getContext(),e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.d("StudentPicUpload", "FAILED student picture upload");
                }
            });
        }

        return newFileName;
    }

    private void uploadClassPic( Uri classPicUri){

        if(classPicUri !=null){
            final String newFileName = auth.getCurrentUser().getUid()+"class."+ getFileExtension(classPicUri);
            StorageReference fileReference = picStorageReference.child(newFileName);

            fileReference.putFile(classPicUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Upload upload = new Upload(newFileName, taskSnapshot.getUploadSessionUri().toString());
                    myDB.child("classPicture").setValue(upload);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void openFileChooser(int code){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,code);
    }

    private String getFileExtension(Uri uri){
        ContentResolver contentResolver = getContext().getContentResolver();
        MimeTypeMap mime =MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private boolean updateStudent(String id, String name, Student.Status status, String imageFileName){
        DatabaseReference studentItemDB=studentsDB.child(id);
        Student student = new Student(id,classID,name,status, imageFileName);
        studentItemDB.setValue(student);
        String usableDate = globalCurrentDate.replace('/','-');
        datesDB.child(usableDate).child(id).setValue(status);
        return true;
    }

    private void deleteStudent(String studentID){
        DatabaseReference studentItemDB = studentsDB.child(studentID);
        studentItemDB.removeValue();
    }

    private void initializeDateSelecting(){
        DatePickerDialog.OnDateSetListener dateSetListener= new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
//                CustomDate date= new CustomDate();
//                //Have a Global CurrentDate value
//                datesDB.child(year+"-"+month+"-"+dayOfMonth).setValue(date);
                MainActivity mainActivity= (MainActivity) sendHelp;

                globalCurrentDate = (month+1)+"/"+dayOfMonth+"/"+year;
                ((MainActivity) sendHelp).currentDate= globalCurrentDate;
                studentsDate.setText(globalCurrentDate);
                Log.d("dat OG date nigga", year+", "+month+", "+dayOfMonth);
                onStart();
            }
        };
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog= new DatePickerDialog(
                daContext,
                android.R.style.Theme_DeviceDefault_Dialog_MinWidth,
                dateSetListener,
                year,month,day);

//        datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.));
        datePickerDialog.show();
    }

    public void changeStatus(Student.Status pleaseEndMyLife){
        loopStatus= pleaseEndMyLife;
    }

    public void  addNewShitToDaList(Student student, Student.Status status){
        Student newStudent = new Student(student.getSid(),student.getCid(),student.getName(),status, student.getImageName());
        thatNigga= newStudent;
        //        studentList.add(newStudent);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==10){
            Toast.makeText(getContext(), "eat my ass", Toast.LENGTH_LONG).show();
        }
        if(requestCode==PICK_IMAGE_REQUEST && resultCode==-1 && data != null &&
                data.getData() != null){
            uriHolder=data.getData();
            kidsFaceIV.setImageURI(uriHolder);
        }
        if(requestCode==PICK_CLASS_IMAGE_REQUEST && resultCode==-1 && data !=null &&
                data.getData() != null){
            uploadClassPic((Uri) data.getData());
        }
    }
}
