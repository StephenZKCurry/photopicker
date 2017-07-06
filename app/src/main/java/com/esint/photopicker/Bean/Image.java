package com.esint.photopicker.Bean;

/**
 * 图片实体类
 */

public class Image {
    private int imgId;
    private String imgUri;

    public int getImgId() {
        return imgId;
    }

    public String getImgUri() {
        return imgUri;
    }

    public void setImgUri(String imgUri) {
        this.imgUri = imgUri;
    }

    @Override
    public String toString() {
        return "Image{" +
                "imgId=" + imgId +
                ", imgUri='" + imgUri + '\'' +
                '}';
    }
}
