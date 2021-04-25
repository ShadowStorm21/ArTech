package com.example.mycourseprojectapplication.Activities;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreference;

import com.example.mycourseprojectapplication.Fragments.AuthenticationFragment;
import com.example.mycourseprojectapplication.Fragments.EditProfileFragment;
import com.example.mycourseprojectapplication.Fragments.PasswordEditFragment;
import com.example.mycourseprojectapplication.Models.Users;
import com.example.mycourseprojectapplication.R;
import com.example.mycourseprojectapplication.Utilities.UserSession;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settings, new SettingsFragment())
                    .commit();
        }
        Toolbar toolbar = findViewById(R.id.toolbar5);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // setup the action toolbar with activity title and back icon functionality
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        private FirebaseAuth mAuth;
        private Users user;
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);
            UserSession userSession = new UserSession(getContext());
            mAuth = FirebaseAuth.getInstance();
            getUserDetails();
            Preference buttonRestHomeTips = (Preference)getPreferenceManager().findPreference("home_tips");
            if (buttonRestHomeTips != null) {
                buttonRestHomeTips.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(Preference arg0) {
                        arg0.setSummary("reset complete");
                        userSession.setIsFirstTimeLogin(true);
                        return true;
                    }
                });
            }

            Preference buttonClearCache = (Preference)getPreferenceManager().findPreference("clear_cache_button");
            if (buttonClearCache != null) {
                buttonClearCache.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(Preference arg0) {
                        deleteCache(getContext());
                        arg0.setSummary("Cache Cleared");
                        return true;
                    }
                });
            }

            Preference buttonDeleteAccount = (Preference)getPreferenceManager().findPreference("delete_account_button");
            if (buttonDeleteAccount  != null) {
                buttonDeleteAccount.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(Preference arg0) {

                        AuthenticationFragment authenticationFragment = new AuthenticationFragment();
                        authenticationFragment.show(getFragmentManager(),"ModalBottomFragment");
                        return true;
                    }
                });
            }
            Preference buttonEditAccount = (Preference)getPreferenceManager().findPreference("change_account_info_button");
            if (buttonEditAccount  != null) {
                buttonEditAccount.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(Preference arg0) {

                        Bundle bundle = new Bundle();
                        bundle.putString("username",user.getUsername());
                        bundle.putString("email",user.getEmail());
                        EditProfileFragment editProfileFragment = new EditProfileFragment();
                        editProfileFragment.setArguments(bundle);
                        editProfileFragment.show(getFragmentManager(),"ModalSheet");
                        return true;
                    }
                });
            }


            Preference buttonChangePasswordAccount = (Preference)getPreferenceManager().findPreference("change_account_password_button");
            if (buttonChangePasswordAccount  != null) {
                buttonChangePasswordAccount.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(Preference arg0) {

                        PasswordEditFragment passwordEditFragment = new PasswordEditFragment();
                        passwordEditFragment.show(getFragmentManager(),"ModalSheet");
                        return true;
                    }
                });
            }
            SwitchPreference ar_tips_switch = getPreferenceManager().findPreference("ar_tips");

            if (ar_tips_switch != null) {

                ar_tips_switch.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(Preference preference, Object newValue) {
                        if(newValue.toString().equals("true")) {
                           userSession.setUserPRFS_AR(true);
                        }
                        else {
                            userSession.setUserPRFS_AR(false);
                        }
                        return true;
                    }
                });

            }

            ListPreference theme_preference = getPreferenceManager().findPreference("theme");

            if (theme_preference != null) {

                theme_preference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(Preference preference, Object newValue) {
                        if(newValue.toString().equals("Light"))
                        {
                            userSession.setUserPerfMode("Light");
                            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

                        }
                        else if(newValue.toString().equals("Dark"))
                        {
                            userSession.setUserPerfMode("Dark");
                            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);

                        }
                        else
                        {
                            userSession.setUserPerfMode("System Default");
                            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                        }

                        return true;
                    }
                });



            }
        }

        private void getUserDetails() {

            FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
            DatabaseReference myRef = firebaseDatabase.getReference("Users");
            myRef.child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    try {
                        user = snapshot.getValue(Users.class);

                    }catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

        }





        public static void deleteCache(Context context) {
            try {
                File dir = context.getCacheDir();
                deleteDir(dir);
            } catch (Exception e) { e.printStackTrace();}
        }

        public static boolean deleteDir(File dir) {
            if (dir != null && dir.isDirectory()) {
                String[] children = dir.list();
                for (int i = 0; i < children.length; i++) {
                    boolean success = deleteDir(new File(dir, children[i]));
                    if (!success) {
                        return false;
                    }
                }
                return dir.delete();
            } else if(dir!= null && dir.isFile()) {
                return dir.delete();
            } else {
                return false;
            }
        }

}