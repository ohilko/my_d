package ohilko.test4;

import java.util.ArrayList;

public class Directory {
	private String name;
	private String allCost;
	private ArrayList<Product> productList = new ArrayList<Product>();

	public Directory(String name, String allCost, ArrayList<Product> productList) {
		super();
		this.name = name;
		this.allCost = allCost;
		this.productList = productList;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAllCost() {
		return allCost;
	}

	public void setAllCost(String allCost) {
		this.allCost = allCost;
	}

	public ArrayList<Product> getProductList() {
		return productList;
	}

	public void setProductList(ArrayList<Product> productList) {
		this.productList = productList;
	}

}
