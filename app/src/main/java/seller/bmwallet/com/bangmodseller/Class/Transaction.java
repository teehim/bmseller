package seller.bmwallet.com.bangmodseller.Class;

import java.io.Serializable;

public class Transaction implements Serializable {

	String transProd;
	double transPrice;
    String transId;

	public String getTransProd() {
		return transProd;
	}
	public void setTransProd(String transProd) {
		this.transProd = transProd;
	}
	public double getTransPrice() {
		return transPrice;
	}
	public void setTransPrice(double transPrice) {
		this.transPrice = transPrice;
	}
	
	
}
