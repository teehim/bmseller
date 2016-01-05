package seller.bmwallet.com.bangmodseller.Class;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class LineItem {
	Product product;
	int amount;

	public LineItem(Product i, int amt) {
		this.product = i;
		this.amount = amt;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product i) {
		this.product = i;
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	public static ArrayList<LineItem> addLine(ArrayList<LineItem> ar,
			Product trans) {
		int chk = 0;
		if (ar.size() == 0) {
			ar.add(new LineItem(trans, 1));
		} else {
			for (int i = 0; i < ar.size(); i++) {
				LineItem line = ar.get(i);

				if (line.product.getId()
						.equalsIgnoreCase(trans.id)) {
					line.setAmount(line.amount + 1);
					chk++;
				}

			}
			if (chk == 0) {
				ar.add(new LineItem(trans, 1));
			}
		}
		return ar;
	}

	public static ArrayList<LineItem> removeLine(ArrayList<LineItem> ar,
			int position) {
		LineItem line = ar.get(position);
		
		if (line.amount != 1) {
			line.setAmount(line.amount - 1);
		} else {
			ar.remove(position);
		}

		return ar;
	}

	public static String calTotal(ArrayList<LineItem> ar){
		DecimalFormat d = new DecimalFormat("0.00");
		double total = 0.0;
		if(ar.size()!=0){
			for(int i = 0;i<ar.size();i++){
				total += ar.get(i).product.getPrice() * ar.get(i).amount;
			}
			return d.format(total);
		}
		return d.format(total);
	}
}
