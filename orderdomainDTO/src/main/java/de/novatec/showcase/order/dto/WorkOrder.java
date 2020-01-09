package de.novatec.showcase.order.dto;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonFormat;

import de.novatec.showcase.order.GlobalConstants;

public class WorkOrder implements Serializable {

	private static final long serialVersionUID = 1L;

	private Integer id;
	private int location;
	private int salesId;
	private int orderLineId;
	private WorkOrderStatus status;
	private int originalQuantity;
	private int completedQuantity;

	@JsonFormat(pattern = GlobalConstants.DATE_FORMAT, locale = "de_DE")
	private Calendar dueDate;
	@JsonFormat(pattern = GlobalConstants.DATE_FORMAT, locale = "de_DE")
	private Calendar startDate;
	private String assemblyId;
	private int version;

	public WorkOrder() {
		super();
	}
	
	public WorkOrder(int location, int salesId, int orderLineId, int originalQuantity, Calendar dueDate,
			String assemblyId) {
		super();
		this.location = location;
		this.salesId = salesId;
		this.orderLineId = orderLineId;
		this.status = WorkOrderStatus.OPEN;
		this.originalQuantity = originalQuantity;
		this.completedQuantity = -1;
		this.dueDate = dueDate;
		this.assemblyId = assemblyId;
		this.version = 0;
	}


	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getAssemblyId() {
		return assemblyId;
	}

	public int getCompletedQuantity() {
		return completedQuantity;
	}

	public void setCompletedQuantity(int completedQuantity) {
		this.completedQuantity = completedQuantity;
	}

	public int getOrderLineId() {
		return orderLineId;
	}

	public Calendar getDueDate() {
		return dueDate;
	}

	public int getSalesId() {
		return salesId;
	}

	public int getOriginalQuantity() {
		return originalQuantity;
	}

	public int getLocation() {
		return location;
	}

	public Calendar getStartDate() {
		return startDate;
	}

	public WorkOrderStatus getStatus() {
		return status;
	}

	public void setStatus(WorkOrderStatus status) {
		this.status = status;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public int getVersion() {
		return version;
	}

	public void setLocation(int location) {
		this.location = location;
	}

	public void setStartDate(Calendar startDate) {
		this.startDate = startDate;
	}

	public void setDueDate(Calendar dueDate) {
		this.dueDate = dueDate;
	}

	public void setAssemblyId(String assemblyId) {
		this.assemblyId = assemblyId;
	}

	public void setSalesId(int salesId) {
		this.salesId = salesId;
	}

	public void setOrderLineId(int orderLineId) {
		this.orderLineId = orderLineId;
	}

	public void setOriginalQuantity(int originalQuantity) {
		this.originalQuantity = originalQuantity;
	}

	@Override
	public int hashCode() {
		return Objects.hash(assemblyId, completedQuantity, dueDate, id, location, orderLineId, originalQuantity,
				salesId, startDate, status, version);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof WorkOrder)) {
			return false;
		}
		WorkOrder other = (WorkOrder) obj;
		return Objects.equals(assemblyId, other.assemblyId) && completedQuantity == other.completedQuantity
				&& Objects.equals(dueDate, other.dueDate) && id == other.id && location == other.location
				&& orderLineId == other.orderLineId && originalQuantity == other.originalQuantity
				&& salesId == other.salesId && Objects.equals(startDate, other.startDate) && status == other.status
				&& version == other.version;
	}

	@Override
	public String toString() {
		return "WorkOrder [id=" + id + ", location=" + location + ", salesId=" + salesId + ", orderLineId="
				+ orderLineId + ", status=" + status + ", originalQuantity=" + originalQuantity + ", completedQuantity="
				+ completedQuantity + ", dueDate=" + dueDate + ", startDate=" + startDate + ", assemblyId=" + assemblyId
				+ ", version=" + version + "]";
	}
}
