package ohilko.test4.models;

public class Product {
	private String name = "";
	private String um = "";
	private String price = "";
	private String amount = "";
	private long id;

	public Product(String name, String um, String price, String amount, long id) {
		super();
		this.amount = amount;
		this.name = name;
		this.price = price;
		this.um = um;
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUm() {
		return um;
	}

	public void setUm(String um) {
		this.um = um;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

}
