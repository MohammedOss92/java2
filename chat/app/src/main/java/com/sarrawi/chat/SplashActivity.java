package com.sarrawi.chat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.sarrawi.chat.model.UserModel;
import com.sarrawi.chat.utils.AndroidUtil;
import com.sarrawi.chat.utils.FirebaseUtil;


public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);


            new Handler().postDelayed(() -> {
                if(FirebaseUtil.isLoggedIn()){
                    Log.d("SplashActivity", "User is logged in, going to MainActivity");
                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                } else {
                    Log.d("SplashActivity", "User is not logged in, going to LoginPhoneNumberActivity");
                    startActivity(new Intent(SplashActivity.this, LoginPhoneNumberActivity.class));
                }
                finish();
            }, 1000);
        }

    }

    //        if(getIntent().getExtras() != null){
//            // من الإشعار
//            String userId = getIntent().getExtras().getString("userId");
//            Log.d("SplashActivity", "userId from notification: " + userId);
//
//            FirebaseUtil.allUserCollectionReference().document(userId).get()
//                    .addOnCompleteListener(task -> {
//                        if(task.isSuccessful()){
//                            UserModel model = task.getResult().toObject(UserModel.class);
//                            Log.d("SplashActivity", "User found: " + model.getUsername());
//
//                            Intent mainIntent = new Intent(this, MainActivity.class);
//                            mainIntent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
//                            startActivity(mainIntent);
//
//                            Intent intent = new Intent(this, ChatActivity.class);
//                            AndroidUtil.passUserModelAsIntent(intent, model);
//                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                            startActivity(intent);
//                            finish();
//                        } else {
//                            Log.e("SplashActivity", "Error getting user data", task.getException());
//                        }
//                    });
//        }else{