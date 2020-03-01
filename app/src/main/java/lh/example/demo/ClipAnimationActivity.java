package lh.example.demo;

import android.os.Bundle;
import android.view.View;


import androidx.appcompat.app.AppCompatActivity;
import view.ClipAnimationView;

public class ClipAnimationActivity extends AppCompatActivity {

    private ClipAnimationView clipAnimationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clip_animation);
        clipAnimationView = findViewById(R.id.cv_anim);
        clipAnimationView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clipAnimationView.setDuration(500);
                clipAnimationView.setZoomOut(true);
                clipAnimationView.startAnim(500,200,800,600);
            }
        });

    }

}
