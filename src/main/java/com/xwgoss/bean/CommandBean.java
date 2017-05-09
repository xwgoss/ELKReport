package com.xwgoss.bean;

public class CommandBean {
private String seqId;
private String serviceId;
private String serviceName;
private String inTime;
private Long timeSpent;
public String getSeqId() {
	return seqId;
}
public void setSeqId(String seqId) {
	this.seqId = seqId;
}
public String getServiceId() {
	return serviceId;
}
public void setServiceId(String serviceId) {
	this.serviceId = serviceId;
}
public String getServiceName() {
	return serviceName;
}
public void setServiceName(String serviceName) {
	this.serviceName = serviceName;
}
public String getInTime() {
	return inTime;
}
public void setInTime(String inTime) {
	this.inTime = inTime;
}
public Long getTimeSpent() {
	return timeSpent;
}
public void setTimeSpent(Long timeSpent) {
	this.timeSpent = timeSpent;
}
@Override
public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((seqId == null) ? 0 : seqId.hashCode());
	return result;
}
@Override
public boolean equals(Object obj) {
	if (this == obj)
		return true;
	if (obj == null)
		return false;
	if (getClass() != obj.getClass())
		return false;
	CommandBean other = (CommandBean) obj;
	if (seqId == null) {
		if (other.seqId != null)
			return false;
	} else if (!seqId.equals(other.seqId))
		return false;
	return true;
}
@Override
public String toString() {
	return "CommandBean [seqId=" + seqId + ", serviceId=" + serviceId + ", serviceName=" + serviceName + ", inTime="
			+ inTime + ", timeSpent=" + timeSpent + "]";
}


}
