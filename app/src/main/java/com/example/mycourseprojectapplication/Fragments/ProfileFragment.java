package com.example.mycourseprojectapplication.Fragments;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.mycourseprojectapplication.Activities.RewardsActivity;
import com.example.mycourseprojectapplication.Broadcasts.MyRestarter;
import com.example.mycourseprojectapplication.BuildConfig;
import com.example.mycourseprojectapplication.Models.Users;
import com.example.mycourseprojectapplication.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

public class ProfileFragment extends Fragment {
    private final int PICK_IMAGE = 1;
    private FirebaseAuth mAuth;
    private Users user;
    private TextView textViewUsername,textViewEmail,textViewMemberSince,textViewLastSuccessfulLogin;
    private ProgressBar progressBar;
    private ImageView profileImageView;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.profile_layout_v2, container, false);
        mAuth = FirebaseAuth.getInstance();
        CardView cardViewRewards = root.findViewById(R.id.cardViewRewards);
        CardView cardViewCards = root.findViewById(R.id.cardViewProfileCards);
        textViewUsername = root.findViewById(R.id.textViewProfileUsername);
        textViewEmail = root.findViewById(R.id.textViewProfileEmail);
        textViewMemberSince = root.findViewById(R.id.textViewProfileMemberSince);
        textViewLastSuccessfulLogin = root.findViewById(R.id.textViewLastSuccessfulLogin);
        progressBar = root.findViewById(R.id.progressBarProfile);
        profileImageView = root.findViewById(R.id.profile_image);



        cardViewRewards.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), RewardsActivity.class);
                startActivity(intent);
            }
        });

        cardViewCards.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewCardsBottomFragment viewCardsBottomFragment = ViewCardsBottomFragment.newInstance();
                viewCardsBottomFragment.show(getFragmentManager(),viewCardsBottomFragment.TAG);
            }
        });
        Button uploadImg = root.findViewById(R.id.buttonUploadProfileImage);
        uploadImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(checkPermissions()) {
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("image/*");                                         // open a window to select multiple product images
                    startActivityForResult(intent, PICK_IMAGE);
                }
                else
                {
                    requestPermission();
                }
            }
        });
        getUserDetails();

        return root;
    }

    private void getUserDetails() {


            progressBar.setVisibility(View.VISIBLE);
            FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
            DatabaseReference myRef = firebaseDatabase.getReference("Users");
            myRef.child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    try {
                        user = snapshot.getValue(Users.class);
                        textViewUsername.setText(user.getUsername());
                        textViewEmail.setText(user.getEmail());
                        String simpleDateFormat = new SimpleDateFormat("dd MMMM yyyy").format(mAuth.getCurrentUser().getMetadata().getCreationTimestamp());
                        textViewMemberSince.setText(simpleDateFormat);
                        String simpleDateFormat1 = new SimpleDateFormat("dd MMMM yyyy hh:mm").format(mAuth.getCurrentUser().getMetadata().getLastSignInTimestamp());
                        textViewLastSuccessfulLogin.setText(simpleDateFormat1);
                        if (!user.getUserPhotoUrl().equals("default")) {
                            Glide.with(getActivity()).load(user.getUserPhotoUrl()).dontAnimate().into(profileImageView);
                            progressBar.setVisibility(View.INVISIBLE);
                        }
                        progressBar.setVisibility(View.INVISIBLE);

                    }catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }



    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE)
        {
            if(resultCode == Activity.RESULT_OK)
            {
                    Uri uri = data.getData();
                    if(uri != null)
                    {
                        profileImageView.setImageURI(uri);
                        upload(uri);
                    }
            }
        }
    }


    private void upload(Uri uri) { // method to upload images to the cloud firestorage

        progressBar.setVisibility(View.VISIBLE);
        StorageReference ImageFolder = FirebaseStorage.getInstance().getReference().child("profileImages"); // create a storage reference
        final StorageReference ImageName = ImageFolder.child("Images" + uri.getLastPathSegment());// create a reference to each image

            ImageName.putFile(uri).addOnSuccessListener( // upload image to firebase
                    new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            ImageName.getDownloadUrl().addOnSuccessListener( // get download url for each image after a successful upload
                                    new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                        storeLink(uri);
                                        }
                                    }
                            );
                        }
                    }
            );

    }

    private void storeLink(Uri uri) {


        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                Map<String, Object> values = new HashMap();
                for (DataSnapshot user : snapshot.getChildren()) {
                    values.put(user.getKey(),user.getValue());
                }
                values.put("userPhotoUrl",uri.toString());
                databaseReference.child(mAuth.getCurrentUser().getUid()).updateChildren(values).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(getContext(), "Profile Image Updated Successfully", Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    @Override
    public void onResume() {
        super.onResume();
        cancelBackgroundNotification();
    }

    private void cancelBackgroundNotification()
    {
        int alarmId = 0; /* Dynamically assign alarm ids for multiple alarms */
        Intent intent = new Intent(getContext(), MyRestarter.class); /* your Intent localIntent = new Intent("com.test.sample");*/
        intent.putExtra("alarmId", alarmId); /* So we can catch the id on BroadcastReceiver */
        PendingIntent alarmIntent;
        alarmIntent = PendingIntent.getBroadcast(getContext(),
                alarmId, intent,
                PendingIntent.FLAG_CANCEL_CURRENT);
        long futureInMillis = SystemClock.elapsedRealtime()  ; // 2min        // 3600000 Hour  // 7200000 2 Hours // 1800000 30 min
        AlarmManager alarmManager = (AlarmManager)getActivity().getSystemService(Context.ALARM_SERVICE);
        alarmManager.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP, futureInMillis, alarmIntent);
    }

    private boolean checkPermissions()
    {
        if(ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
        {
            return true;
        }
        return false;
    }
    private void requestPermission()
    {
        requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 100);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");                                         // open a window to select multiple product images
                startActivityForResult(intent, PICK_IMAGE);
            }
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                // user rejected the permission

                if (!ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    // if user clicked deny permanently

                    new MaterialAlertDialogBuilder(getContext())                           // create an alert before logging out
                            .setTitle("Alert")
                            .setMessage("This feature is disable due to lack of permission! please give permission first!")
                            .setCancelable(true)
                            .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    startActivity(new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + BuildConfig.APPLICATION_ID))); // go to application settings
                                }
                            }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            dialogInterface.dismiss();

                        }
                    }).show();
                } else {
                    requestPermission();
                }


            }
        }
    }
}