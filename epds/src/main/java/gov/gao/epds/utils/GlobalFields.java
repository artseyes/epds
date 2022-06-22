package gov.gao.epds.utils;

import gov.gao.epds.dto.UploadedFileIdentifier;
import gov.gao.epds.persistence.entity.Agency_Info;
import gov.gao.epds.persistence.entity.Doc_Info;
import gov.gao.epds.persistence.entity.Tier_1_Agency;
import gov.gao.epds.persistence.entity.Tier_2_Agency;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author MHussaini
 *@Todo : It is better to do this using hibernate caching just storing all the information will create problem because sometime when we are need agencies in the database it wont get reflected without application restart
 *the better way to implement this is through hibernate caching like ehcache..
 */
public class GlobalFields {
	public static List<UploadedFileIdentifier> protestFileList = new ArrayList<UploadedFileIdentifier>();
	public static List<UploadedFileIdentifier> otherDocList = new ArrayList<UploadedFileIdentifier>();
	public static Map<Integer, Doc_Info> docId_To_Doc_Info_Map = null;
	static Map<Integer, Tier_1_Agency> tier_1_AgencyMap = null;
	static Map<Integer, Tier_2_Agency> tier_2_AgencyMap = null;
	static Map<Integer, Agency_Info> agency_InfoMap = null;
}
