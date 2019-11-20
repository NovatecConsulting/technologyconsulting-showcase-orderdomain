package de.novatec.showcase.ejb.orders.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

import com.fasterxml.jackson.annotation.JsonFormat;

@Entity
@Table(name = "O_CUSTOMER")
@NamedQueries(value = { @NamedQuery(name = "QUERY_ALL", query = Customer.QUERY_ALL),
		@NamedQuery(name = "QUERY_COUNT", query = Customer.QUERY_COUNT),
		@NamedQuery(name = "QUERY_BY_CREDIT", query = Customer.QUERY_BY_CREDIT),
		@NamedQuery(name = "BAD_CREDIT", query = Customer.BAD_CREDIT)

})
public class Customer implements Serializable {
	public static final String DATE_FORMAT = "yyyy-MM-dd";

	private static final long serialVersionUID = 3961431086357095469L;

	public static final String QUERY_ALL = "SELECT c FROM Customer c";
	public static final String QUERY_COUNT = "SELECT COUNT(c) FROM Customer c";
	public static final String QUERY_BY_CREDIT = "SELECT c FROM Customer c WHERE c.credit = :credit";
	public static final String BAD_CREDIT = "SELECT c FROM Customer c WHERE c.credit = 'BC'";

	@Id
	@Column(name = "C_ID")
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "C_ID_GEN")
	@TableGenerator(name = "C_ID_GEN", table = "U_SEQUENCES", pkColumnName = "S_ID", valueColumnName = "S_NEXTNUM", pkColumnValue = "C_SEQ", allocationSize = 1)
	private Integer id;

	@Column(name = "C_FIRST")
	private String firstName;

	@Column(name = "C_LAST")
	private String lastName;

	@Column(name = "C_CONTACT")
	private String contact;

	@Column(name = "C_CREDIT")
	private String credit;

	@Column(name = "C_CREDIT_LIMIT")
	private BigDecimal creditLimit;

	@Column(name = "C_SINCE")
	@Temporal(value = TemporalType.DATE)
	@JsonFormat(pattern = DATE_FORMAT, locale = "de_DE")
	private Calendar since;

	@Column(name = "C_BALANCE")
	private BigDecimal balance;

	@Column(name = "C_YTD_PAYMENT")
	private BigDecimal ytdPayment;

	@OneToMany(mappedBy = "customer", fetch = FetchType.EAGER, cascade = CascadeType.REFRESH)
	private List<CustomerInventory> customerInventories;

	@Embedded
	@AttributeOverrides({ @AttributeOverride(name = "street1", column = @Column(name = "C_STREET1")),
			@AttributeOverride(name = "street2", column = @Column(name = "C_STREET2")),
			@AttributeOverride(name = "city", column = @Column(name = "C_CITY")),
			@AttributeOverride(name = "state", column = @Column(name = "C_STATE")),
			@AttributeOverride(name = "country", column = @Column(name = "C_COUNTRY")),
			@AttributeOverride(name = "zip", column = @Column(name = "C_ZIP")),
			@AttributeOverride(name = "phone", column = @Column(name = "C_PHONE")) })
	private Address address;

	@Version
	@Column(name = "C_VERSION")
	private int version;

	protected Customer() {
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

	public void addBalance(BigDecimal money) {
		this.balance = this.balance.add(money);
	}

	public boolean reduceBalance(BigDecimal costs) {
		if (this.hasSufficientCredit(costs)) {
			this.balance.subtract(costs);
			return true;
		} else {
			return false;
		}
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

	public boolean hasSufficientCredit(BigDecimal cost) {
		return this.creditLimit.add(this.balance).compareTo(cost) == 1;
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
