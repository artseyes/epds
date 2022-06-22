package gov.gao.epds.persistence.entity;

import gov.gao.epds.gctrack.EPDS_event;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.envers.Audited;

@Entity
@Table
@Audited
public class GC_Track_Service_Event implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 416410718223147643L;

	@Id
	@Column
	@GenericGenerator(
	        name = "gctrackServiceEventSeqGen", 
	        strategy = "enhanced-sequence",
	        parameters = {
	        	@org.hibernate.annotations.Parameter(name="prefer_sequence_per_entity", value="true"),	
	        		
	            @org.hibernate.annotations.Parameter(
	                name = "optimizer",
	                value = "pooled-lo"
	            ),
	            @org.hibernate.annotations.Parameter(
	                name = "initial_value", 
	                value = "1"
	            ),
	            @org.hibernate.annotations.Parameter(
	                name = "increment_size", 
	                value = "1"
	            )
	        }
	    )
	    @GeneratedValue(
	        strategy = GenerationType.SEQUENCE, 
	        generator = "gctrackServiceEventSeqGen"
	    )
	
	/*@Id
	@Column
	@GeneratedValue(strategy = GenerationType.SEQUENCE)*/
	private Integer event_Id;

	@Column
	private String a_No;
	@Column
	private String b_No;
	@Column
	private Integer event_type_id;
	@Column
	private String event_Type;
	@Column
	private String event_description;
	@Column
	private String status;
	@Column
	private String event_Date;
	@Column
	private String representative_User_Id;
	@Column
	private String case_type;
	@Column
	private String filed_date;
	@Column
	private String protester;
	@Column
	private String solicitation_no;
	@Column
	private String info;
	@Column
	private String agency;
	@Transient
	private List<EPDS_event> list_of_EPDS_event;

	public Integer getEvent_type_id() {
		return event_type_id;
	}

	public void setEvent_type_id(Integer event_type_id) {
		this.event_type_id = event_type_id;
	}

	public String getEvent_description() {
		return event_description;
	}

	public void setEvent_description(String event_description) {
		this.event_description = event_description;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public String getCase_type() {
		return case_type;
	}

	public void setCase_type(String case_type) {
		this.case_type = case_type;
	}

	public String getFiled_date() {
		return filed_date;
	}

	public void setFiled_date(String filed_date) {
		this.filed_date = filed_date;
	}

	public String getProtester() {
		return protester;
	}

	public void setProtester(String protester) {
		this.protester = protester;
	}

	public String getSolicitation_no() {
		return solicitation_no;
	}

	public void setSolicitation_no(String solicitation_no) {
		this.solicitation_no = solicitation_no;
	}

	public String getAgency() {
		return agency;
	}

	public void setAgency(String agency) {
		this.agency = agency;
	}

	public List<EPDS_event> getList_of_EPDS_event() {
		return list_of_EPDS_event;
	}

	public void setList_of_EPDS_event(List<EPDS_event> list_of_EPDS_event) {
		this.list_of_EPDS_event = list_of_EPDS_event;
	}

	public String getRepresentative_User_Id() {
		return representative_User_Id;
	}

	public void setRepresentative_User_Id(String representative_User_Id) {
		this.representative_User_Id = representative_User_Id;
	}

	public Integer getEvent_Id() {
		return event_Id;
	}

	public void setEvent_Id(Integer event_Id) {
		this.event_Id = event_Id;
	}

	public String getA_No() {
		return a_No;
	}

	public void setA_No(String a_No) {
		this.a_No = a_No;
	}

	public String getB_No() {
		return b_No;
	}

	public void setB_No(String b_No) {
		this.b_No = b_No;
	}

	public String getEvent_Type() {
		return event_Type;
	}

	public void setEvent_Type(String event_Type) {
		this.event_Type = event_Type;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getEvent_Date() {
		return event_Date;
	}

	public void setEvent_Date(String event_Date) {
		this.event_Date = event_Date;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((a_No == null) ? 0 : a_No.hashCode());
		result = prime * result + ((agency == null) ? 0 : agency.hashCode());
		result = prime * result + ((b_No == null) ? 0 : b_No.hashCode());
		result = prime * result + ((case_type == null) ? 0 : case_type.hashCode());
		result = prime * result + ((event_Date == null) ? 0 : event_Date.hashCode());
		result = prime * result + ((event_Id == null) ? 0 : event_Id.hashCode());
		result = prime * result + ((event_Type == null) ? 0 : event_Type.hashCode());
		result = prime * result + ((event_description == null) ? 0 : event_description.hashCode());
		result = prime * result + ((event_type_id == null) ? 0 : event_type_id.hashCode());
		result = prime * result + ((filed_date == null) ? 0 : filed_date.hashCode());
		result = prime * result + ((info == null) ? 0 : info.hashCode());
		result = prime * result + ((list_of_EPDS_event == null) ? 0 : list_of_EPDS_event.hashCode());
		result = prime * result + ((protester == null) ? 0 : protester.hashCode());
		result = prime * result + ((representative_User_Id == null) ? 0 : representative_User_Id.hashCode());
		result = prime * result + ((solicitation_no == null) ? 0 : solicitation_no.hashCode());
		result = prime * result + ((status == null) ? 0 : status.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof GC_Track_Service_Event))
			return false;
		GC_Track_Service_Event other = (GC_Track_Service_Event) obj;
		if (a_No == null) {
			if (other.a_No != null)
				return false;
		} else if (!a_No.equals(other.a_No))
			return false;
		if (agency == null) {
			if (other.agency != null)
				return false;
		} else if (!agency.equals(other.agency))
			return false;
		if (b_No == null) {
			if (other.b_No != null)
				return false;
		} else if (!b_No.equals(other.b_No))
			return false;
		if (case_type == null) {
			if (other.case_type != null)
				return false;
		} else if (!case_type.equals(other.case_type))
			return false;
		if (event_Date == null) {
			if (other.event_Date != null)
				return false;
		} else if (!event_Date.equals(other.event_Date))
			return false;
		if (event_Id == null) {
			if (other.event_Id != null)
				return false;
		} else if (!event_Id.equals(other.event_Id))
			return false;
		if (event_Type == null) {
			if (other.event_Type != null)
				return false;
		} else if (!event_Type.equals(other.event_Type))
			return false;
		if (event_description == null) {
			if (other.event_description != null)
				return false;
		} else if (!event_description.equals(other.event_description))
			return false;
		if (event_type_id == null) {
			if (other.event_type_id != null)
				return false;
		} else if (!event_type_id.equals(other.event_type_id))
			return false;
		if (filed_date == null) {
			if (other.filed_date != null)
				return false;
		} else if (!filed_date.equals(other.filed_date))
			return false;
		if (info == null) {
			if (other.info != null)
				return false;
		} else if (!info.equals(other.info))
			return false;
		if (list_of_EPDS_event == null) {
			if (other.list_of_EPDS_event != null)
				return false;
		} else if (!list_of_EPDS_event.equals(other.list_of_EPDS_event))
			return false;
		if (protester == null) {
			if (other.protester != null)
				return false;
		} else if (!protester.equals(other.protester))
			return false;
		if (representative_User_Id == null) {
			if (other.representative_User_Id != null)
				return false;
		} else if (!representative_User_Id.equals(other.representative_User_Id))
			return false;
		if (solicitation_no == null) {
			if (other.solicitation_no != null)
				return false;
		} else if (!solicitation_no.equals(other.solicitation_no))
			return false;
		if (status == null) {
			if (other.status != null)
				return false;
		} else if (!status.equals(other.status))
			return false;
		return true;
	}
	
	

}
