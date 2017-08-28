package yun.homeguard.strayanimals;

import android.graphics.Bitmap;

/**
 * Created by 123 on 2017/8/24.
 */

public class Pets {
    public String getShelter() {
        return shelter;
    }

    public void setShelter(String shelter) {
        this.shelter = shelter;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public Bitmap getImg() {
        return img;
    }

    public void setImg(Bitmap img) {
        this.img = img;
    }

    String shelter;

    String kind;

    Bitmap img;
}
