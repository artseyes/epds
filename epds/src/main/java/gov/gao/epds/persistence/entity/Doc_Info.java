package gov.gao.epds.persistence.entity;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;

@Entity
@Table
/*@Cacheable
@org.hibernate.annotations.Cache(usage = org.hibernate.annotations.CacheConcurrencyStrategy.READ_WRITE)*/
@Cacheable
@Cache(usage = org.hibernate.annotations.CacheConcurrencyStrategy.READ_WRITE, region="docInfo")
public class Doc_Info {
	
	@Id
	@Column
	private int doc_Type_Id;

	@Column
	private String doc_Type_Desc;
	@Column
	private String role;
	
	@Column
	private String case_Type;
	
	@Column
	private Integer filing_Order;
	


	public Integer getFiling_Order() {
		return filing_Order;
	}

	public void setFiling_Order(Integer filing_Order) {
		this.filing_Order = filing_Order;
	}

	public int getDoc_Type_Id() {
		return doc_Type_Id;
	}

	public void setDoc_Type_Id(int doc_Type_Id) {
		this.doc_Type_Id = doc_Type_Id;
	}

	public String getDoc_Type_Desc() {
		return doc_Type_Desc;
	}

	public void setDoc_Type_Desc(String doc_Type_Desc) {
		this.doc_Type_Desc = doc_Type_Desc;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getCase_Type() {
		return case_Type;
	}

	public void setCase_Type(String case_Type) {
		this.case_Type = case_Type;
	}
}
