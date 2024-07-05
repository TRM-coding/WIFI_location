package com.example.imgmarker;

import android.graphics.PostProcessor;
import android.widget.ImageView;

public class Position {
    private float scaleX;//x坐标比例，用比例值来自适应缩放的地图
    private float scaleY;//y坐标比例
    private ImageView markerView;//标记图标
    private int imgSrcId;//标记图标资源id

    public Position() {
    }

    public Position(float scaleX, float scaleY, int imgSrcId) {
        this.scaleX = scaleX;
        this.scaleY = scaleY;
        this.imgSrcId = imgSrcId;
    }

    public float getScaleX() {
        return scaleX;
    }

    public void setScaleX(float scaleX) {
        this.scaleX = scaleX;
    }

    public float getScaleY() {
        return scaleY;
    }

    public void setScaleY(float scaleY) {
        this.scaleY = scaleY;
    }

    public void setMarkerView(ImageView markerView) {
        this.markerView = markerView;
    }

    public int getImgSrcId() {
        return imgSrcId;
    }

    public void setImgSrcId(int imgSrcId) {
        this.imgSrcId = imgSrcId;
    }

    public ImageView getMarkerView() {
        return markerView;
    }
}
