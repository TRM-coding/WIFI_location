package com.example.wifilocation.locate;

import android.widget.ImageView;

public class Marker {
    private float scaleX;//x坐标比例，用比例值来自适应缩放的地图
    private float scaleY;//y坐标比例
    private float floorZ;//z坐标楼层
    private String name;
    private ImageView markerView;//标记图标
    private int imgSrcId;//标记图标资源id

    public Marker() {
    }

    public Marker(float scaleX, float scaleY, float floorZ, String name, int imgSrcId) {
        this.scaleX = scaleX;
        this.scaleY = scaleY;
        this.floorZ = floorZ;
        this.name = name;
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

    public float getFloorZ() {
        return floorZ;
    }

    public void setFloorZ(float floorZ) {
        this.floorZ = floorZ;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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