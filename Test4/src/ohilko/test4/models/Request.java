package ohilko.test4.models;

public class Request {
	private long id;
	private long providerId;
	private String date;
	private String allCost;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getProviderId() {
		return providerId;
	}

	public void setProviderId(long providerId) {
		this.providerId = providerId;
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

}
