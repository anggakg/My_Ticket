package com.example.tiketsaya;

import android.content.Intent;
import android.content.SharedPreferences;
import android.opengl.ETC1;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class RegisterOneAct extends AppCompatActivity {

    Button btn_continue;
    LinearLayout btn_back;
    EditText username, password, email_address;
    DatabaseReference reference, reference_username;

    String USERNAME_KEY = "usernamekey";
    String username_key = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_one);

        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        email_address = findViewById(R.id.email_address);

        btn_continue = findViewById(R.id.btn_continue);
        btn_back = findViewById(R.id.btn_back);

        btn_continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //ubah state menjadi loading...
                btn_continue.setEnabled(false);
                btn_continue.setText("Loading ...");

                //mengambil username pada firebase
                reference_username = FirebaseDatabase.getInstance().getReference().child("Users").child(username.getText().toString());
                reference_username.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        // jika username sudah tersedia
                        if(dataSnapshot.exists()){
                            Toast.makeText(getApplicationContext(), "Username sudah tersedia!", Toast.LENGTH_SHORT).show();

                            //ubah state menjadi active
                            btn_continue.setEnabled(true);
                            btn_continue.setText("CONTINUE");
                        }
                        else{
                            final String xusername = username.getText().toString();
                            final String xpassword = password.getText().toString();
                            final String xemail_address = email_address.getText().toString();

                            if(xusername.isEmpty()){
                                Toast.makeText(getApplicationContext(),"Username kosong!", Toast.LENGTH_SHORT).show();
                                //ketika user kosong, akan kembali menjadi CONTINUE
                                btn_continue.setEnabled(true);
                                btn_continue.setText("CONTINUE");
                            }
                            else{
                                if(xpassword.isEmpty()){
                                    Toast.makeText(getApplicationContext(),"Password kosong!", Toast.LENGTH_SHORT).show();
                                    //ketika password kosong, akan kembali menjadi CONTINUE
                                    btn_continue.setEnabled(true);
                                    btn_continue.setText("CONTINUE IN");
                                }
                                else if(xemail_address.isEmpty()){
                                    Toast.makeText(getApplicationContext(),"Email Address kosong!", Toast.LENGTH_SHORT).show();
                                    //ketika email kosong, akan kembali menjadi CONTINUE
                                    btn_continue.setEnabled(true);
                                    btn_continue.setText("CONTINUE");
                                }
                                else{
                                    //menyimpan data pada lokal storage
                                    SharedPreferences sharedPreferences = getSharedPreferences(USERNAME_KEY, MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putString(username_key, username.getText().toString());
                                    editor.apply();

                                    //simpan data ke database firebase
                                    reference = FirebaseDatabase.getInstance().getReference().child("Users").child(username.getText().toString());
                                    reference.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            dataSnapshot.getRef().child("username").setValue(username.getText().toString());
                                            dataSnapshot.getRef().child("password").setValue(password.getText().toString());
                                            dataSnapshot.getRef().child("email_address").setValue(email_address.getText().toString());
                                            dataSnapshot.getRef().child("user_balance").setValue(800);
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });

                                    Intent gotonextregister = new Intent(RegisterOneAct.this,RegisterTwoAct.class);
                                    startActivity(gotonextregister);
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
}
