package de.novatec.showcase.order.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonFormat;

import de.novatec.showcase.order.GlobalConstants;

public class Customer implements Serializable {

	private static final long serialVersionUID = 1L;

	private Integer id;

	private String firstName;

	private String lastName;

	private String contact;

	private String credit;

	private BigDecimal creditLimit;

	@JsonFormat(pattern = GlobalConstants.DATE_FORMAT, locale = "de_DE")
	private Calendar since;

	private BigDecimal balance;

	private BigDecimal ytdPayment;

	private List<CustomerInventory> customerInventories;

	private Address address;

	private int version;

	public Customer() {
		super();
	}

	public Customer(String firstName, String lastName, String contact, String credit, BigDecimal creditLimit,
			Calendar since, BigDecimal balance, BigDecimal ytdPayment, List<CustomerInventory> customerInventories,
			Address address) {
		super();
		this.firstName = firstName;
		this.lastName = lastName;
		this.contact = contact;
		this.credit = credit;
		this.creditLimit = creditLimit;
		this.since = since;
		this.balance = balance;
		this.ytdPayment = ytdPayment;
		this.customerInventories = customerInventories;
		this.address = address;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getContact() {
		return contact;
	}

	public void setContact(String contact) {
		this.contact = contact;
	}

	public String getCredit() {
		return credit;
	}

	public void setCredit(String credit) {
		this.credit = credit;
	}

	public BigDecimal getCreditLimit() {
		return creditLimit;
	}

	public void setCreditLimit(BigDecimal creditLimit) {
		this.creditLimit = creditLimit;
	}

	public Calendar getSince() {
		return since;
	}

	public void setSince(Calendar since) {
		this.since = since;
	}

	public BigDecimal getBalance() {
		return balance;
	}

	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}

	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}

	public BigDecimal getYtdPayment() {
		return ytdPayment;
	}

	public void setYtdPayment(BigDecimal ytdPayment) {
		this.ytdPayment = ytdPayment;
	}

	public List<CustomerInventory> getCustomerInventories() {
		return customerInventories;
	}

	public void addInventory(CustomerInventory inventory) {
		this.customerInventories.add(inventory);
	}

	public int getVersion() {
		return version;
	}

	@Override
	public int hashCode() {
		return Objects.hash(address, balance, contact, credit, creditLimit, customerInventories, firstName, id,
				lastName, since, version, ytdPayment);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof Customer)) {
			return false;
		}
		Customer other = (Customer) obj;
		return Objects.equals(address, other.address) && Objects.equals(balance, other.balance)
				&& Objects.equals(contact, other.contact) && Objects.equals(credit, other.credit)
				&& Objects.equals(creditLimit, other.creditLimit)
				&& Objects.equals(customerInventories, other.customerInventories)
				&& Objects.equals(firstName, other.firstName) && Objects.equals(id, other.id)
				&& Objects.equals(lastName, other.lastName) && Objects.equals(since, other.since)
				&& version == other.version && Objects.equals(ytdPayment, other.ytdPayment);
	}

	@Override
	public String toString() {
		return "Customer [id=" + id + ", firstName=" + firstName + ", lastName=" + lastName + ", contact=" + contact
				+ ", credit=" + credit + ", creditLimit=" + creditLimit + ", since=" + since + ", balance=" + balance
				+ ", ytdPayment=" + ytdPayment + ", customerInventories=" + customerInventories + ", address=" + address
				+ ", version=" + version + "]";
	}
}
