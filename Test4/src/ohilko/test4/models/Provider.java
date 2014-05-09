package ohilko.test4.models;

public class Provider {
	private long id;
	private String name;
	private String address;
	private String phone;

	public Provider(String name, String address, String phone, long id) {
		super();
		this.address = address;
		this.name = name;
		this.phone = phone;
		this.id = id;
	}
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

}
