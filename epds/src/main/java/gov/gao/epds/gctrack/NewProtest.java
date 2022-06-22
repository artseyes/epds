package gov.gao.epds.gctrack;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

/*

Amer,

This is last minute stuff but we have a change in code so our data needs from JSON files are going to be quite different. The biggest changes are to simplify list_of_new_protest but load all info up to individual interface /get-case/{A#}.

Here’s what we need now:

1.	in list_of_new_protest, keep only these five elements:
a.	"a_no"
b.	"case_type"
c.	"filed_date" (date&time)
d.	"protester"
e.	"solicitation_no"

2.	in …/get-case/ interface, keep what’s already there (add time for filed and due dates), and also include parties, organizations, and full repInfo/orgInfo, and add ID attribute wherever applicable.

3.	in list_of_EPDS_event, always populate all available organization data for all parties/agency/etc;

Let me know if the above makes sense, or if you have any question or concern

Thanks so much for being patient with me!


1.	in list_of_new_protest, keep only these five elements:
a.	"a_no"
b.	"case_type"
c.	"filed_date" (date&time)
d.	"protester"
e.	"solicitation_no"


*/
public class NewProtest {
	private String a_no;
	private String case_type;
	private String filed_date;
	private String protester;
	private String solicitation_no;
	private String transaction_status;

	private String b_no;

	@JsonIgnore
	private List<RepInfo> parties;
	@JsonIgnore
	private List<OrgInfo> organizations;

	public String getA_no() {
		return a_no;
	}

	public void setA_no(String a_no) {
		this.a_no = a_no;
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

	public String getTransaction_status() { return transaction_status; }

	public void setTransaction_status(String transaction_status) { this.transaction_status = transaction_status; }

	public String getB_no() {
		return b_no;
	}

	public void setB_no(String b_no) {
		this.b_no = b_no;
	}

	public List<RepInfo> getParties() {
		return parties;
	}

	public void setParties(List<RepInfo> parties) {
		this.parties = parties;
	}

	public List<OrgInfo> getOrganizations() {
		return organizations;
	}

	public void setOrganizations(List<OrgInfo> organizations) {
		this.organizations = organizations;
	}



}
