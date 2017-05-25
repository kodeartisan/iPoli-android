package io.ipoli.android.store.viewmodels;

import android.support.annotation.DrawableRes;

/**
 * Created by Venelin Valkov <venelin@curiousily.com>
 * on 8/26/16.
 */
public class PetViewModel {
    private String name;

    private int price;

    @DrawableRes
    private int picture;

    @DrawableRes
    private int pictureState;

    private String pictureName;

    public PetViewModel(String name, int price, @DrawableRes int picture, @DrawableRes int pictureState, String pictureName) {
        this.name = name;
        this.price = price;
        this.picture = picture;
        this.pictureState = pictureState;
        this.pictureName = pictureName;
    }

    public String getName() {
        return name;
    }

    public int getPrice() {
        return price;
    }

    public int getPicture() {
        return picture;
    }

    public int getPictureState() {
        return pictureState;
    }

    public String getPictureName() {
        return pictureName;
    }

}
