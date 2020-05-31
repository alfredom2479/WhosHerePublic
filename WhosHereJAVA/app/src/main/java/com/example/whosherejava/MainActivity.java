package com.example.whosherejava;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.icu.util.Calendar;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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

import androidx.appcompat.app.WindowDecorActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.NavigatorProvider;
import androidx.navigation.fragment.FragmentNavigator;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

//import com.example.whosherejava.ui.login.LoginActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.navigation.NavigationView;
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
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private FirebaseAuth auth;

    private Menu topRightMenu;

    private Toolbar mainToolbar;
    private DrawerLayout mainDrawerLayout;
    public NavController mainNavController;
    private NavigationView mainNavigationView;

    private ListView mainLV;
    public ImageView kidsFaceIV;
    private Button mainSwitchUserButt, mainAddClassPicButt, mainAddStudentButt, mainToMain2;

    private AppBarConfiguration appBarConfiguration;


    private List<Student> studentList;

    private static final int PICK_IMAGE_REQUEST=69, PICK_CLASS_IMAGE_REQUEST=420;

    private Uri uriHolder;

    private DatabaseReference myDB, studentsDB, classesDB, datesDB;
    private StorageReference picStorageReference;
    private DatabaseReference picDBReference, classPicDBRef;

    public String currClassId, deletionID;
    public String currentDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        studentList= new ArrayList<>();
        
//        //Firebase References Set Up
//        ////////////////////////////////////////////////////////////////////////////////////////////
        auth = FirebaseAuth.getInstance();
        if(auth.getCurrentUser()!=null) {
            myDB = FirebaseDatabase.getInstance().getReference
                    ("users/" + auth.getCurrentUser().getUid());
            myDB.child("email").setValue(auth.getCurrentUser().getEmail());
            studentsDB = myDB.child("students");
            classesDB= myDB.child("classes");
            datesDB= myDB.child("dates");
        }
        setupNavigation();

        Calendar calendar = Calendar.getInstance();

        Date date = new Date(calendar.getTimeInMillis());

        currentDate= date.toString();

        Log.d("onCreateDate","Y: "+calendar.get(calendar.YEAR)+" M: "+ calendar.get(calendar.MONTH)+" D: "+calendar.get(calendar.DAY_OF_MONTH));
        currentDate=(calendar.get(calendar.MONTH)+1)+"/"+calendar.get(calendar.DAY_OF_MONTH)+"/"+calendar.get(calendar.YEAR);
        Log.d("onCreateDate", date.toString());


    }

    public void onStart(){
        super.onStart();

        //Update UI And Set Event Listener For Student Database
        ////////////////////////////////////////////////////////////////////////////////////////////
        if(auth.getCurrentUser() != null) {
            studentsDB.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    studentList.clear();
                    for(DataSnapshot studentSnapshot: dataSnapshot.getChildren()){
                        Student student= studentSnapshot.getValue(Student.class);
                            studentList.add(student);

                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        }
        else{
            Log.d("authStatus","There is no current User");
            goToSignIn();
        }

    }

    private void setupNavigation() {
        mainToolbar= findViewById(R.id.mainToolbar);
        setSupportActionBar(mainToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
       getSupportActionBar().setTitle("Who's Here?");

        mainDrawerLayout =  findViewById(R.id.parentLayout);
        mainNavigationView = findViewById(R.id.mainNavigationView);
        mainNavController = Navigation.findNavController(this, R.id.nav_host_fragment);

        NavigationUI.setupActionBarWithNavController(this, mainNavController, mainDrawerLayout);
        NavigationUI.setupWithNavController(mainNavigationView, mainNavController);
        mainNavigationView.setNavigationItemSelectedListener(this);

    }


    private void topRightMenuVisibility(boolean isVisible){
        topRightMenu.getItem(0).setVisible(isVisible);
//        topRightMenu.getItem(1).setVisible(isVisible);

    }

    @Override
    public boolean onSupportNavigateUp() {
        return NavigationUI.navigateUp(Navigation.findNavController(this, R.id.nav_host_fragment), mainDrawerLayout);
    }

    @Override
    public void onBackPressed() {
        if(mainDrawerLayout.isDrawerOpen(GravityCompat.START)){
            mainDrawerLayout.closeDrawer(GravityCompat.START);
        }
        else{
            super.onBackPressed();
            if(mainNavController.getCurrentDestination().getId()==R.id.startFragment){
                topRightMenuVisibility(false);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        topRightMenu= menu;

        mainNavController.addOnDestinationChangedListener(new NavController.OnDestinationChangedListener() {
            @Override
            public void onDestinationChanged(@NonNull NavController controller, @NonNull NavDestination destination, @Nullable Bundle arguments) {
                if(destination.getId()== R.id.startFragment){
                    topRightMenuVisibility(false);
                }
            }
        });
        if(mainNavController.getCurrentDestination().getId()==R.id.startFragment){
            topRightMenuVisibility(false);
        }
//        menu.getItem(0).setVisible(false);
        return true;
    }

    public void goToSignIn(){
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }
    private void goToMain2() {
        Intent intent = new Intent(this, MainTestActivity.class);
        startActivity(intent);
    }

    public void updateUI(FirebaseUser user){
        if(user.getDisplayName() == null){
//            mainUsernameTV.setText(user.getEmail());
        }
        else{
//            mainUsernameTV.setText(user.getDisplayName());
        }

    }

    public void goToClassPage(String classID){
        Bundle bundle = new Bundle();
        bundle.putString("classID",classID);
        bundle.putString("currDate", currentDate);

        mainNavController.navigate(R.id.action_startFragment_to_secondFragment, bundle);
        topRightMenuVisibility(true);
    }
    public void goToEditClass(String classID){
        Bundle bundle= new Bundle();
        bundle.putString("classID",classID);
        mainNavController.navigate(R.id.action_secondFragment_to_editClassFragment, bundle);

        topRightMenuVisibility(false);
    }

    public void goBackToStartFrag(String id){
        mainNavController.popBackStack();
        mainNavController.popBackStack();
        classesDB.child(id).removeValue();

        for(int i=0;i< studentList.size();i++){
            if (studentList.get(i).getCid().equals(id)){
                studentsDB.child(studentList.get(i).getSid()).removeValue();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==10){
            Toast.makeText(this, "eat my ass", Toast.LENGTH_LONG).show();
        }
        if(requestCode==PICK_IMAGE_REQUEST && resultCode==RESULT_OK && data != null &&
        data.getData() != null){
            uriHolder=data.getData();
            kidsFaceIV.setImageURI(uriHolder);
        }
        if(requestCode==PICK_CLASS_IMAGE_REQUEST && resultCode==RESULT_OK && data !=null &&
        data.getData() != null){
//            uploadClassPic((Uri) data.getData());
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id==R.id.rightMenuEditClass){ goToEditClass(currClassId); }
//        if(id ==R.id.addStudent){
//            showAddStudentDialog();
//        }
//        if(id==R.id.optionSwitchUser && mainNavController.getCurrentDestination().getId()==R.id.startFragment){
//            StartFragment.
//        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        menuItem.setChecked(true);

        int id = menuItem.getItemId();

        switch (id){

//            case R.id.drawerSecond:
//                mainNavController.navigate(R.id.action_startFragment_to_secondFragment);
//                break;
//            case R.id.drawerThird:
//                mainNavController.navigate(R.id.action_startFragment_to_thirdFragment);
//                break;
            case R.id.drawerSignOut:
                 goToSignIn();
                 break;
        }
        mainDrawerLayout= findViewById(R.id.parentLayout);
        mainDrawerLayout.closeDrawer(GravityCompat.START);
//        mainDrawerLayout.closeDrawer(GravityCompat.START,true);
//        Log.d("destination", mainNavController.getCurrentDestination().toString());
        Log.d("destination IDs", "method: "+mainNavController.getCurrentDestination().getId()+
                "manual"+R.id.thirdFragment);
        return true;
    }



}
