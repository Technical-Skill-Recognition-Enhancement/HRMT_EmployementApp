package com.example.android.hrm;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.Calendar;

public class Employee_job_opporunity extends AppCompatActivity {
    String need;
    ProgressBar s;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.employee_display_job_opportunity);
        s=findViewById(R.id.progressBar4);
        s.setVisibility(View.GONE);
        need = getIntent().getStringExtra("occ");
        FirebaseDatabase rootNode = FirebaseDatabase.getInstance();
        DatabaseReference reference = rootNode.getReference("job");
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        //assert user != null;
        //String userid = user.getUid();
        //Query checkUser = reference.child(userid);
        reference.child(need).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    s.setVisibility(View.VISIBLE);
                    final TextView[] tv = new TextView[(Integer.parseInt(String.valueOf(snapshot.getChildrenCount())))];
                    int k = 0;
                    int t = 1;
                    int flag=0;
                    final LinearLayout rl = findViewById(R.id.linear_layout2);
                    for (DataSnapshot data : snapshot.getChildren()) {
                        String date_posted = data.child("Dated").getValue(String.class);
                        Calendar currenttime=Calendar.getInstance();
                        String date= DateFormat.getDateInstance().format(currenttime.getTime());
                        if(date.equalsIgnoreCase(date_posted))
                        {
                            flag=1;
                            EmployerJOBDetailsHelper user = data.getValue(EmployerJOBDetailsHelper.class);
                        tv[k] = new TextView(getApplicationContext());
                        assert user != null;
                        tv[k].setText((t++) +". "+date+"\nनियोक्ता का फोननंबर: " + user.getPhn()+ "\nनियोक्ता का नाम: " + user.getName()+ "\nविवरण: " + user.getDesp() + "\nश्रमिकों की संख्या: " + user.getNlab() + "\nदिनों की संख्या: " + user.getNdays());
                        tv[k].setTextSize((float) 20);
                        tv[k].setBackgroundColor(Color.parseColor("#f8fcee"));
                        tv[k].setPadding(20, 20, 20, 20);
                        rl.addView(tv[k]);
                        TextView border = new TextView(getApplicationContext());
                        border.setText("\n");
                        border.setBackgroundColor(Color.parseColor("#4FB5E6"));
                        rl.addView(border);
                        k++;
                        s.setVisibility(View.GONE);}
                    }
                    if(flag==0)
                    {
                        s.setVisibility(View.GONE);
                        final LinearLayout rll = findViewById(R.id.linear_layout2);
                        TextView tvv = new TextView(getApplicationContext());
                        tvv.setText("\n" + "नया रोजगार अवसर खोजने में विफल रहा");
                        tvv.setTextSize((float) 20);
                        tvv.setPadding(20, 20, 20, 20);
                        rll.addView(tvv);
                    }
                } else {
                    s.setVisibility(View.GONE);
                    final LinearLayout rl = findViewById(R.id.linear_layout2);
                    TextView tv = new TextView(getApplicationContext());
                    tv.setText("\n" + "नया रोजगार अवसर खोजने में विफल रहा");
                    tv.setTextSize((float) 20);
                    tv.setPadding(20, 20, 20, 20);
                    rl.addView(tv);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        findViewById(R.id.button_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
