package seller.bmwallet.com.bangmodseller.Class;

import java.io.Serializable;
import android.graphics.Bitmap;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Thanatkorn on 8/14/2014.
 */
public class Promotion implements Serializable {

    String proIm;
    String proId;
    String title;
    String expDate;
    String strDate;
    String description;
    String usePoint;
    String storeId;
    String storeName;
    ArrayList<Reward> promoSet;

    public ArrayList<Reward> getPromoSet() {
        return promoSet;
    }

    public void setPromoSet(ArrayList<Reward> promoSet) {
        this.promoSet = promoSet;
    }

    public String getProId() {
        return proId;
    }

    public void setProId(String proId) {
        this.proId = proId;
    }

    public String getProIm() {
        return proIm;
    }

    public void setProIm(String proIm) {
        this.proIm = proIm;
    }

    public String getUsePoint() {
        return usePoint;
    }

    public void setUsePoint(String usePoint) {
        this.usePoint = usePoint;
    }

    public String getStoreId() {
        return storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    public String getStrDate() {
        return strDate;
    }

    public void setStrDate(String strDate) {
        this.strDate = strDate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getExpDate() {
        return expDate;
    }

    public void setExpDate(String expDate) {
        this.expDate = expDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }
}

