package seller.bmwallet.com.bangmodseller.Class;

import java.io.Serializable;

/**
 * Created by Thanatkorn on 9/24/2014.
 */
public class Reward implements Serializable {
    String productImage;
    String productId;
    String productName;
    String amount;
    String price;

    public String getProductImage() {
        return productImage;
    }

    public void setProductImage(String productimage) {
        this.productImage = productimage;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}

