package lh.example.demo;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.LayoutTransition;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.BounceInterpolator;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private ImageView ivOriginView;
    private ImageView ivCropView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ivOriginView = findViewById(R.id.iv_origin_view);
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.test).copy(Bitmap.Config.ARGB_8888, true);
        float[] array = new float[]{400,300,700,500};
        Log.d(TAG, "onCreate: width: " + bitmap.getWidth() + ", height: " + bitmap.getHeight());
        Bitmap copy = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        Bitmap bitmap1 = BitmapUtil.drawLine(bitmap,array);
        ivOriginView.setImageBitmap(bitmap1);

        ClipPhotoShowBean clipPhotoShowBean = new ClipPhotoShowBean();
        clipPhotoShowBean.setBitmap(copy);
        clipPhotoShowBean.setCoordinate(array);
        clipPhotoShowBean.setTolerate(200);
        ClipPhotoShowBean newBean = BitmapUtil.adapterShowView(clipPhotoShowBean);
        Log.d(TAG, "onCreate: newBean:" + newBean.toString());
        Log.d(TAG, "onCreate: width:" + newBean.getBitmap().getWidth());
        Log.d(TAG, "onCreate: height:" + newBean.getBitmap().getHeight());
       // Bitmap bitmap2 = BitmapUtil.drawLine(newBean.getBitmap(), newBean.getCoordinate());
        if(newBean.getBitmap() == null){
            Log.d(TAG, "onCreate: bitmap == null");
        }
        ivCropView = findViewById(R.id.iv_crop_view);
        ivCropView.setImageBitmap(BitmapUtil.drawOther(newBean.getBitmap(),newBean.getCoordinate()));
       // clipPhotoShowBean

        LayoutTransition layoutTransition = new LayoutTransition();
        ObjectAnimator objectAnimator = ObjectAnimator.ofPropertyValuesHolder(ivCropView,
                PropertyValuesHolder.ofFloat("scaleX",0f,1f),
                PropertyValuesHolder.ofFloat("scaleY",0f,1f));
        objectAnimator.setInterpolator(new BounceInterpolator());
        layoutTransition.setAnimator(LayoutTransition.APPEARING,objectAnimator);
        layoutTransition.setDuration(LayoutTransition.APPEARING,layoutTransition.getDuration(LayoutTransition.APPEARING));
        layoutTransition.setStartDelay(LayoutTransition.APPEARING,layoutTransition.getStartDelay(LayoutTransition.APPEARING));


        ViewGroup viewGroup = (ViewGroup) new ViewGroup(this) {
            @Override
            protected void onLayout(boolean changed, int l, int t, int r, int b) {

            }
        };

        viewGroup.setLayoutTransition(layoutTransition);

        ValueAnimator valueAnimator = new ValueAnimator();
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {

            }
        });
    }
}
