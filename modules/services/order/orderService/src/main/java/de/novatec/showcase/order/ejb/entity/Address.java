package de.novatec.showcase.order.ejb.entity;

import java.util.Objects;

import javax.persistence.Embeddable;

@Embeddable
public class Address{ 

	private String street1;
	private String street2;
	private String city;
	private String state;
	private String country;
	private String zip;
	private String phone;

	public Address() {
		super();
	}

	public Address(String street1, String street2, String city, String state, String country, String zip,
			String phone) {
		super();
		this.street1 = street1;
		this.street2 = street2;
		this.city = city;
		this.state = state;
		this.country = country;
		this.zip = zip;
		this.phone = phone;
	}

	public String getStreet1() {
		return street1;
	}

	public void setStreet1(String street1) {
		this.street1 = street1;
	}

	public String getStreet2() {
		return street2;
	}

	public void setStreet2(String street2) {
		this.street2 = street2;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getZip() {
		return zip;
	}

	public void setZip(String zip) {
		this.zip = zip;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	@Override
	public int hashCode() {
		return Objects.hash(city, country, phone, state, street1, street2, zip);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof Address)) {
			return false;
		}
		Address other = (Address) obj;
		return Objects.equals(city, other.city) && Objects.equals(country, other.country)
				&& Objects.equals(phone, other.phone) && Objects.equals(state, other.state)
				&& Objects.equals(street1, other.street1) && Objects.equals(street2, other.street2)
				&& Objects.equals(zip, other.zip);
	}

	@Override
	public String toString() {
		return "Address [street1=" + street1 + ", street2=" + street2 + ", city=" + city + ", state=" + state
				+ ", country=" + country + ", zip=" + zip + ", phone=" + phone + "]";
	}
}
