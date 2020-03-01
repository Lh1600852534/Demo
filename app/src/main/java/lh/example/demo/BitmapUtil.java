package lh.example.demo;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.util.Log;

public class BitmapUtil {

    private static final String TAG = BitmapUtil.class.getSimpleName();
    public static ClipPhotoShowBean adapterShowView(ClipPhotoShowBean clipPhotoShowBean){

        //最小容忍值
        int tolerate = clipPhotoShowBean.getTolerate();
        Log.d(TAG, "最小容忍值: " + tolerate);
        //原始bitmap
        Bitmap originBitmap = clipPhotoShowBean.getBitmap();
        //原始坐标，识别区域
        float[] originCoordinate = clipPhotoShowBean.getCoordinate();
        Log.d(TAG, "原始坐标区域坐标: [" + originCoordinate[0] + ", " + originCoordinate[1] +
                ", " + originCoordinate[2] + ", " + originCoordinate[3] + "]");
        //原始四点坐标
        float[] originUpLeft = new float[]{originCoordinate[0],originCoordinate[1]};
        Log.d(TAG, "原始左上坐标: [" + originCoordinate[0] + ", " + originCoordinate[1] + "]");
        float[] originUpRight = new float[]{originCoordinate[2],originCoordinate[1]};
        Log.d(TAG, "原始右上坐标: [" + originCoordinate[2] + ", " + originCoordinate[1] + "]");
        float[] originBottomLeft = new float[]{originCoordinate[0],originCoordinate[3]};
        Log.d(TAG, "原始左下坐标: [" + originCoordinate[0] + ", " + originCoordinate[3] + "]");
        float[] originBottomRight = new float[]{originCoordinate[2],originCoordinate[3]};
        Log.d(TAG, "原始右下坐标: [" + originCoordinate[2] + ", " + originCoordinate[3] + "]");
        //原始坐标区域长宽
        int originDisplayWidth = (int) (originCoordinate[2] - originCoordinate[0]);
        Log.d(TAG, "原始坐标区域宽: " + originDisplayWidth);
        int originDisplayHeight = (int) (originCoordinate[3] - originCoordinate[1]);
        Log.d(TAG, "原始坐标区域高: " + originDisplayHeight);
        //原始bitmap 长宽
        int originBitmapWidth = originBitmap.getWidth();
        Log.d(TAG, "原始bitmap宽: " + originBitmapWidth);
        int originBitmapHeight = originBitmap.getHeight();
        Log.d(TAG, "原始bitmap高: " + originBitmapHeight);
        //最大可放大倍数
        float maxAcceptZoomIn = originBitmapWidth/originDisplayWidth > originBitmapHeight/originDisplayHeight ?
                (float) originBitmapHeight/originDisplayHeight : (float) originBitmapWidth/originDisplayWidth;
        Log.d(TAG, "最大可放大倍数: " + maxAcceptZoomIn);
        //最小需放大倍数
        float minNeedZoomIn = tolerate/originBitmapWidth > tolerate/originDisplayHeight ?
                (float) tolerate/originBitmapWidth : (float) tolerate/originDisplayHeight;
        Log.d(TAG, "最小需放大倍数: " + minNeedZoomIn);
        if(minNeedZoomIn <= 1){
            //当前识别区域可以识别，无需放大
            Log.d(TAG, "当前识别区域可以识别，无需放大");
            return clipPhotoShowBean;
        }
        if(minNeedZoomIn > maxAcceptZoomIn){
            //需放大倍数大于可放大倍数，无法放大
            Log.d(TAG, "需放大倍数大于可放大倍数，无法放大");
            return null;
        }

        Bitmap zoomBitmap = zoomBitmap(originBitmap, minNeedZoomIn);
        //缩放后坐标，识别区域
        float[] zoomCoordinate = new float[]{originCoordinate[0] * minNeedZoomIn,originCoordinate[1] * minNeedZoomIn,
                originCoordinate[2] * minNeedZoomIn,originCoordinate[3] * minNeedZoomIn};
        Log.d(TAG, "缩放坐标区域坐标: [" + zoomCoordinate[0] + ", " + zoomCoordinate[1] +
                ", " + zoomCoordinate[2] + ", " + zoomCoordinate[3] + "]");


        return cropBitmap(zoomBitmap, zoomCoordinate, originBitmapWidth, originBitmapHeight);

    }

    /**
     * 等比例缩放bitmap
     * @param bitmap 原始bitmap
     * @param zoom 缩放倍数
     * @return 缩放后bitmap
     */
    private static Bitmap zoomBitmap(Bitmap bitmap, float zoom) {
        Matrix matrix = new Matrix();
        //长和宽放大缩小的比例
        matrix.postScale(zoom,zoom);
        return Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),matrix,true);
   }

   private static ClipPhotoShowBean cropBitmap(Bitmap zoomBitmap, float[] zoomCoordinate, float bitmapWidth, float bitmapHeight){
       ClipPhotoShowBean resultBean = new ClipPhotoShowBean();

        //缩放后四点坐标
       float[] zoomUpLeft = new float[]{zoomCoordinate[0],zoomCoordinate[1]};
       Log.d(TAG, "缩放左上坐标: [" + zoomCoordinate[0] + ", " + zoomCoordinate[1] + "]");
       float[] zoomUpRight = new float[]{zoomCoordinate[2],zoomCoordinate[1]};
       Log.d(TAG, "缩放右上坐标: [" + zoomCoordinate[2] + ", " + zoomCoordinate[1] + "]");
       float[] zoomBottomLeft = new float[]{zoomCoordinate[0],zoomCoordinate[3]};
       Log.d(TAG, "缩放左下坐标: [" + zoomCoordinate[0] + ", " + zoomCoordinate[3] + "]");
       float[] zoomBottomRight = new float[]{zoomCoordinate[2],zoomCoordinate[3]};
       Log.d(TAG, "缩放右下坐标: [" + zoomCoordinate[2] + ", " + zoomCoordinate[3] + "]");
       //缩放后中点坐标
       float[] zoomMid = new float[]{(zoomCoordinate[0] + zoomCoordinate[2])/2,(zoomCoordinate[1] + zoomCoordinate[3])/2};
       //缩放后长宽
       float zoomBitmapWidth = zoomBitmap.getWidth();
       float zoomBitmapHeight = zoomBitmap.getHeight();
       //缩放后中点到各边距离
       float toTop = zoomMid[1];
       float toLeft = zoomMid[0];
       float toBottom = zoomBitmapHeight - zoomMid[1];
       float toRight = zoomBitmapWidth - zoomMid[0];

       float[] finalCoordinate;
       Bitmap finalBitmap;
       if(toTop < bitmapHeight/2 && toLeft < bitmapWidth/2){
           //左上角
           Log.d(TAG, "cropBitmap: 左上角");
           finalBitmap = Bitmap.createBitmap(zoomBitmap, 0, 0, (int) bitmapWidth, (int) bitmapHeight);
           finalCoordinate = new float[]{zoomCoordinate[0],zoomCoordinate[1],zoomCoordinate[2],zoomCoordinate[3]};
       }else if (toTop < bitmapHeight/2 && toRight < bitmapWidth/2){
           //右上角
           Log.d(TAG, "cropBitmap: 右上角");
           finalBitmap = Bitmap.createBitmap(zoomBitmap, (int) (zoomBitmapWidth - bitmapWidth), 0, (int) bitmapWidth, (int) bitmapHeight);
           finalCoordinate = new float[]{zoomCoordinate[0] - zoomBitmapWidth + bitmapWidth,zoomCoordinate[1],
           zoomCoordinate[2] - zoomBitmapWidth + bitmapWidth,zoomCoordinate[3]};
       }else if(toBottom < bitmapHeight/2 && toRight < bitmapWidth/2){
           //右下角
           Log.d(TAG, "cropBitmap: 右下角");
           finalBitmap = Bitmap.createBitmap(zoomBitmap, (int) (zoomBitmapWidth - bitmapWidth),(int) (zoomBitmapHeight - bitmapHeight), (int) bitmapWidth, (int) bitmapHeight);
           finalCoordinate = new float[]{zoomCoordinate[0] - zoomBitmapWidth + bitmapWidth,zoomCoordinate[1] - zoomBitmapHeight + bitmapHeight,
                   zoomCoordinate[2] - zoomBitmapWidth + bitmapWidth,zoomCoordinate[3] - zoomBitmapHeight + bitmapHeight};
       }else if(toBottom < bitmapHeight/2 && toLeft < bitmapWidth/2){
           //左下角
           Log.d(TAG, "cropBitmap: 左下角");
           finalBitmap = Bitmap.createBitmap(zoomBitmap, 0,(int) (zoomBitmapHeight - bitmapHeight), (int) bitmapWidth, (int) bitmapHeight);
           finalCoordinate = new float[]{zoomCoordinate[0],zoomCoordinate[1] - zoomBitmapHeight + bitmapHeight,
                   zoomCoordinate[2],zoomCoordinate[3] - zoomBitmapHeight + bitmapHeight};
       }else if(toTop < bitmapHeight/2){
           //贴上边
           Log.d(TAG, "cropBitmap: 贴上边");
           finalBitmap = Bitmap.createBitmap(zoomBitmap,(int) (zoomMid[0] - bitmapWidth / 2),0, (int) bitmapWidth, (int) bitmapHeight);
           finalCoordinate = new float[]{zoomCoordinate[0] - zoomMid[0] + bitmapWidth / 2,zoomCoordinate[1],
                   zoomCoordinate[2] - zoomMid[0] + bitmapWidth / 2,zoomCoordinate[3]};
       }else if(toRight < bitmapWidth/2){
           //贴右边
           Log.d(TAG, "cropBitmap: 贴右边");
           finalBitmap = Bitmap.createBitmap(zoomBitmap,(int) (zoomBitmapWidth - bitmapWidth),(int) (zoomMid[1] - bitmapHeight / 2), (int) bitmapWidth, (int) bitmapHeight);
           finalCoordinate = new float[]{zoomCoordinate[0] - zoomBitmapWidth + bitmapWidth,zoomCoordinate[1] - zoomMid[1] + bitmapHeight / 2,
                   zoomCoordinate[2] - zoomBitmapWidth + bitmapWidth,zoomCoordinate[3] - zoomMid[1] + bitmapHeight / 2};
       }else if(toBottom < bitmapHeight / 2){
           //贴下边
           Log.d(TAG, "cropBitmap: 贴下边");
           finalBitmap = Bitmap.createBitmap(zoomBitmap,(int) (zoomMid[0] - bitmapWidth / 2),(int) (zoomBitmapHeight - bitmapHeight), (int) bitmapWidth, (int) bitmapHeight);
           finalCoordinate = new float[]{zoomCoordinate[0] - zoomMid[0] + bitmapWidth / 2,zoomCoordinate[1] - zoomBitmapHeight + bitmapHeight,
                   zoomCoordinate[2] - zoomMid[0] + bitmapWidth / 2,zoomCoordinate[3] - zoomBitmapHeight + bitmapHeight};
       }else if(toLeft < bitmapWidth / 2){
           //贴左边
           Log.d(TAG, "cropBitmap: 贴左边");
           finalBitmap = Bitmap.createBitmap(zoomBitmap,0,(int) (zoomMid[1] - bitmapHeight / 2), (int) bitmapWidth, (int) bitmapHeight);
           finalCoordinate = new float[]{zoomCoordinate[0],zoomCoordinate[1] - zoomMid[1] + bitmapHeight / 2,
                   zoomCoordinate[2],zoomCoordinate[3] - zoomMid[1] + bitmapHeight / 2};
       }else {
           //中间
           Log.d(TAG, "cropBitmap: 中间");
           finalBitmap = Bitmap.createBitmap(zoomBitmap,(int) (zoomMid[0] - bitmapWidth / 2),(int) (zoomMid[1] - bitmapHeight / 2), (int) bitmapWidth, (int) bitmapHeight);
           finalCoordinate = new float[]{zoomCoordinate[0] - zoomMid[0] + bitmapWidth / 2,zoomCoordinate[1] - zoomMid[1] + bitmapHeight / 2,
                   zoomCoordinate[2] - zoomMid[0] + bitmapWidth / 2,zoomCoordinate[3] - zoomMid[1] + bitmapHeight / 2};
       }
       resultBean.setBitmap(finalBitmap);
       resultBean.setCoordinate(finalCoordinate);
       return resultBean;
   }

    /**
     * 绘制识别区域红线
     *
     * @param sourceBitmap
     * @param coordinateArray
     * @return
     */
    public static Bitmap drawLine(Bitmap sourceBitmap, float[] coordinateArray) {
        Canvas canvas = new Canvas(sourceBitmap);
        Paint paint = new Paint();
        paint.setStrokeWidth(5);
        paint.setAntiAlias(true);
        paint.setColor(Color.RED);
        canvas.drawBitmap(sourceBitmap, new Matrix(), paint);
        // 第一条横线
        canvas.drawLine(coordinateArray[0], coordinateArray[1], coordinateArray[2], coordinateArray[1], paint);
        // 第一条竖线
        canvas.drawLine(coordinateArray[0], coordinateArray[1], coordinateArray[0], coordinateArray[3], paint);
        // 第二条横线
        canvas.drawLine(coordinateArray[0], coordinateArray[3], coordinateArray[2], coordinateArray[3], paint);
        // 第二条竖线
        canvas.drawLine(coordinateArray[2], coordinateArray[1], coordinateArray[2], coordinateArray[3], paint);
        return sourceBitmap;
    }

    /**
     * 绘制识别区域红线
     *
     * @param sourceBitmap
     * @param coordinateArray
     * @return
     */
    public static Bitmap drawOther(Bitmap sourceBitmap, float[] coordinateArray) {
        Canvas canvas = new Canvas(sourceBitmap);
        Paint paint = new Paint();

        paint.setStyle(Paint.Style.FILL);
        paint.setFilterBitmap(true);
        paint.setColor(Color.parseColor("#AA1C1C1C"));
        Path path = new Path();

        Log.d(TAG, "drawOther: current file type: " + path.getFillType());
        path.addRect(new RectF(0,0,sourceBitmap.getWidth(),sourceBitmap.getHeight()), Path.Direction.CW);
        path.addRect(new RectF(coordinateArray[0],coordinateArray[1],coordinateArray[2],coordinateArray[3]), Path.Direction.CCW);
        canvas.drawPath(path, paint);

        paint.setStrokeWidth(20);
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setColor(Color.WHITE);
        /*canvas.drawBitmap(sourceBitmap, new Matrix(), paint);
        // 第一条横线
        canvas.drawLine(coordinateArray[0], coordinateArray[1], coordinateArray[2], coordinateArray[1], paint);
        // 第一条竖线
        canvas.drawLine(coordinateArray[0], coordinateArray[1], coordinateArray[0], coordinateArray[3], paint);
        // 第二条横线
        canvas.drawLine(coordinateArray[0], coordinateArray[3], coordinateArray[2], coordinateArray[3], paint);
        // 第二条竖线
        canvas.drawLine(coordinateArray[2], coordinateArray[1], coordinateArray[2], coordinateArray[3], paint);*/
        RectF rectLeftTop = new RectF(coordinateArray[0],coordinateArray[1],coordinateArray[0] + 80, coordinateArray[1] + 80);
        canvas.drawArc(rectLeftTop,180,90 ,false, paint);
        RectF rectRightTop = new RectF(coordinateArray[2] - 80,coordinateArray[1],coordinateArray[2], coordinateArray[1] + 80);
        canvas.drawArc(rectRightTop,270,90 ,false, paint);
        RectF rectRightBottom = new RectF(coordinateArray[2] - 80,coordinateArray[3] - 80,coordinateArray[2], coordinateArray[3]);
        canvas.drawArc(rectRightBottom,0,90 ,false, paint);
        RectF rectLeftBottom = new RectF(coordinateArray[0],coordinateArray[3] - 80,coordinateArray[0] + 80, coordinateArray[3]);
        canvas.drawArc(rectLeftBottom,90,90 ,false, paint);
        canvas.drawLine(coordinateArray[0] + 40, coordinateArray[1],coordinateArray[0] + 80, coordinateArray[1], paint);
        canvas.drawLine(coordinateArray[2] - 80, coordinateArray[1],coordinateArray[2] - 40, coordinateArray[1], paint);
        canvas.drawLine(coordinateArray[0] + 40, coordinateArray[3],coordinateArray[0] + 80, coordinateArray[3], paint);
        canvas.drawLine(coordinateArray[2] - 80, coordinateArray[3],coordinateArray[2] - 40, coordinateArray[3], paint);
        canvas.drawLine(coordinateArray[0], coordinateArray[1] + 40,coordinateArray[0], coordinateArray[1] + 80, paint);
        canvas.drawLine(coordinateArray[2], coordinateArray[1] + 40,coordinateArray[2], coordinateArray[1] + 80, paint);
        canvas.drawLine(coordinateArray[0], coordinateArray[3] - 80,coordinateArray[0], coordinateArray[3] - 40, paint);
        canvas.drawLine(coordinateArray[2], coordinateArray[3] - 80,coordinateArray[2], coordinateArray[3] - 40, paint);
        return sourceBitmap;
    }

}
