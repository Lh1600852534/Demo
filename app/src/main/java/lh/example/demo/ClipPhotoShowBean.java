package lh.example.demo;

import android.graphics.Bitmap;

import java.util.Arrays;

public class ClipPhotoShowBean {

    /**
     * 要显示的图片
     */
    private Bitmap bitmap;
    /**
     * 需要显示的坐标
     * eg：[0,0,182,320]
     * 四个点分别是  [0,0],[182,0],[0,320],[182,320]
     */
    private float[] coordinate;
    /**
     * 坐标区域显示，所需的最小值
     */
    private int tolerate;

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public float[] getCoordinate() {
        return coordinate;
    }

    public void setCoordinate(float[] coordinate) {
        this.coordinate = coordinate;
    }

    public int getTolerate() {
        return tolerate;
    }

    public void setTolerate(int tolerate) {
        this.tolerate = tolerate;
    }

    @Override
    public String toString() {
        return "ClipPhotoShowBean{" +
                "bitmap=" + bitmap +
                ", coordinate=" + Arrays.toString(coordinate) +
                ", tolerate=" + tolerate +
                '}';
    }
}
