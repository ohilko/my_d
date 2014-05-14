package ohilko.test4.models;

public class Request {
	private long id;
	private String providerName;
	private String date;
	private String allCost;
	private int image;

	public Request(String date, String allCost, String providerName, long id,
			int image) {
		super();
		this.allCost = allCost;
		this.date = date;
		this.id = id;
		this.providerName = providerName;
		this.setImage(image);
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getProviderName() {
		return providerName;
	}

	public void setProviderName(String providerName) {
		this.providerName = providerName;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getAllCost() {
		return allCost;
	}

	public void setAllCost(String allCost) {
		this.allCost = allCost;
	}

	public int getImage() {
		return image;
	}

	public void setImage(int image) {
		this.image = image;
	}

}
