package de.novatec.showcase.order.ejb.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
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

@Entity
@Table(name = "O_CUSTOMER")
@NamedQueries(value = { @NamedQuery(name = Customer.ALL_CUSTOMERS, query = Customer.ALL_CUSTOMERS_QUERY),
		@NamedQuery(name = Customer.COUNT_CUSTOMERS, query = Customer.COUNT_CUSTOMERS_QUERY),
		@NamedQuery(name = Customer.CUSTOMERS_BY_CREDIT, query = Customer.CUSOMERS_BY_CREDIT_QUERY),
		@NamedQuery(name = Customer.BAD_CREDIT_CUSTOMERS, query = Customer.BAD_CREDIT_CUSTOMERS_QUERY) })
public class Customer {
	
	public static final String ALL_CUSTOMERS = "QUERY_ALL";
	public static final String COUNT_CUSTOMERS = "QUERY_COUNT";
	public static final String CUSTOMERS_BY_CREDIT = "QUERY_BY_CREDIT";
	public static final String BAD_CREDIT_CUSTOMERS = "BAD_CREDIT";

	public static final String ALL_CUSTOMERS_QUERY = "SELECT c FROM Customer c";
	public static final String COUNT_CUSTOMERS_QUERY = "SELECT COUNT(c) FROM Customer c";
	public static final String CUSOMERS_BY_CREDIT_QUERY = "SELECT c FROM Customer c WHERE c.credit = :credit";
	public static final String BAD_CREDIT_CUSTOMERS_QUERY = "SELECT c FROM Customer c WHERE c.credit = 'BC'";

	@Id
	@Column(name = "C_ID", nullable = false)
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "C_ID_GEN")
	@TableGenerator(name = "C_ID_GEN", table = "U_SEQUENCES", pkColumnName = "S_ID", valueColumnName = "S_NEXTNUM", pkColumnValue = "C_SEQ", allocationSize = 1)
	private Integer id;

	@Column(name = "C_FIRST", length = 16)
	private String firstName;

	@Column(name = "C_LAST", length = 16)
	private String lastName;

	@Column(name = "C_CONTACT", length = 25)
	private String contact;

	@Column(name = "C_CREDIT", length = 2)
	private String credit;

	@Column(name = "C_CREDIT_LIMIT", precision = 12, scale = 2)
	private BigDecimal creditLimit;

	@Column(name = "C_SINCE")
//	@Temporal(value = TemporalType.DATE)
	private LocalDate since;

	@Column(name = "C_BALANCE", precision = 12, scale = 2)
	private BigDecimal balance;

	@Column(name = "C_YTD_PAYMENT", precision = 12, scale = 2)
	private BigDecimal ytdPayment;

	@OneToMany(mappedBy = "customer", fetch = FetchType.EAGER, cascade = { CascadeType.REMOVE, CascadeType.PERSIST })
	private List<CustomerInventory> customerInventories;

	@Embedded
	@AttributeOverrides({ @AttributeOverride(name = "street1", column = @Column(name = "C_STREET1", length = 20)),
			@AttributeOverride(name = "street2", column = @Column(name = "C_STREET2", length = 20)),
			@AttributeOverride(name = "city", column = @Column(name = "C_CITY", length = 20)),
			@AttributeOverride(name = "state", column = @Column(name = "C_STATE", length = 2)),
			@AttributeOverride(name = "country", column = @Column(name = "C_COUNTRY", length = 10)),
			@AttributeOverride(name = "zip", column = @Column(name = "C_ZIP", length = 9)),
			@AttributeOverride(name = "phone", column = @Column(name = "C_PHONE", length = 16)) })
	private Address address;

	@Version
	@Column(name = "C_VERSION")
	private Integer version;

	public Customer() {
		super();
	}

	public Customer(String firstName, String lastName, String contact, String credit, BigDecimal creditLimit,
			LocalDate since, BigDecimal balance, BigDecimal ytdPayment,
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
		this.customerInventories = new ArrayList<CustomerInventory>();
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

	public LocalDate getSince() {
		return since;
	}

	public void setSince(LocalDate since) {
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

	public void setVersion(Integer version) {
		this.version = version;
	}

	public Integer getVersion() {
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
