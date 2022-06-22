package gov.gao.epds.utils;

import java.io.FileInputStream;
import java.net.URL;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

import org.apache.commons.lang.StringUtils;
import org.javers.common.collections.Lists;
import org.springframework.transaction.annotation.Transactional;

import gov.gao.epds.dao.File_Info_DAO;
import gov.gao.epds.dao.Protest_Info_DAO;
import gov.gao.epds.dao.User_Info_DAO;
import gov.gao.epds.enums.UserRoles;
import static gov.gao.epds.enums.UserRoles.*;
import gov.gao.epds.persistence.entity.File_Info;
import gov.gao.epds.persistence.entity.Protest_Info;
import gov.gao.epds.persistence.entity.User_Protest_Role_Bridge;

public class Protest_info_util {

    /**
	 * Usually PLCG joins two or more protest to a case if they are somewhat similar in the nature
	 * this basically helps the GAO users to manage multiple protest of same nature. 
	 * When two cases are joined The agency representatives of one case is added to other and vice versa.
	 *  The other parties to the cases are added as consolidated users.
	 * @param protest_Info_DAO
	 * @param user_Info_DAO
	 * @param listOfCasesToBeJoined
	 * @param parentProtestInfo
	 * @param file_Info_DAO 
	 * @throws Exception
	 */
//	@Transactional
	public static void joinCases(Protest_Info_DAO protest_Info_DAO,
								 User_Info_DAO user_Info_DAO,
								 List<Protest_Info> listOfCasesToBeJoined,
								 Protest_Info parentProtestInfo, File_Info_DAO file_Info_DAO) throws Exception {

		joinCases(protest_Info_DAO, user_Info_DAO, listOfCasesToBeJoined, parentProtestInfo, file_Info_DAO, false);
	}

	public static void joinCases(Protest_Info_DAO protest_Info_DAO,
			User_Info_DAO user_Info_DAO,
			List<Protest_Info> listOfCasesToBeJoined,
			Protest_Info parentProtestInfo, File_Info_DAO file_Info_DAO, boolean isCalledForGcService) throws Exception {
		
		List<Protest_Info> listOfAllJoinedCases = new ArrayList<Protest_Info>(listOfCasesToBeJoined);
		listOfAllJoinedCases.add(parentProtestInfo);
		
		assignParentAndSupplementalRelationBasedOnPastHistory(parentProtestInfo, listOfAllJoinedCases, protest_Info_DAO);
		
		
		List<User_Protest_Role_Bridge> parent_User_Protest_Role_BridgeList = user_Info_DAO
				.getUser_Protest_Role_Bridge_List_NonConsolidated(parentProtestInfo.getA_No());
		
		// applies only when case is joined from dashboard. For gc-track, we don't need to worry because parent case will always be the one which is top on hierarchy
		if (parentProtestInfo.getParent_A_No() != null){
			if (isCalledForGcService) {
				// Not sure if from bug or human error in GCTrack, but found problem where GCTrack will do a
				// push-case-data of a child with no parent listed and the parent listed as a child Bno,
				// inverting the parent/child relationship. This results in the A# of the child still pointing to the
				// parent and the parent listing itself as it's own parent. Need to throw error to prevent
				throw new IllegalArgumentException(parentProtestInfo.getB_No() + "/" + parentProtestInfo.getA_No() +
						" has children specified, but itself has a parent A# of " + parentProtestInfo.getParent_A_No() +
						". Possible inversion of parent/child.");
			} else {
				parentProtestInfo = protest_Info_DAO.getProtestByA_no(parentProtestInfo.getParent_A_No());
			}
		}

		List<Protest_Info> listOfConsolidatedAndJoinedCases = new ArrayList<Protest_Info>(listOfCasesToBeJoined);
		if (parentProtestInfo.getListOf_ConsolidatedProtest_Info() != null) {
            listOfConsolidatedAndJoinedCases.addAll(parentProtestInfo.getListOf_ConsolidatedProtest_Info());
        }

		for (Protest_Info eachCaseToBeJoined : listOfCasesToBeJoined) {
			
			if (eachCaseToBeJoined.getCase_Type().toUpperCase(Locale.ENGLISH).equals("SUPPLEMENTAL")){
				continue;
			}/*else if(wasThisCaseEverSupplementalOfParent(parentProtestInfo.getA_No(), eachCaseToBeJoined.getLast_parent_a_no())){
				eachCaseToBeJoined.setCase_Type("SUPPLEMENTAL");
				eachCaseToBeJoined.setParent_A_No(parentProtestInfo.getA_No());
				protest_Info_DAO.updateProtest_Info(eachCaseToBeJoined);
			}*/
			else {
				eachCaseToBeJoined.setParent_A_No(parentProtestInfo.getA_No());
				protest_Info_DAO.updateProtest_Info(eachCaseToBeJoined);

				// child->parent
				allowAllUsersOfChildProtestsTheAccessToParentProtest(user_Info_DAO,
						parentProtestInfo, eachCaseToBeJoined.getA_No());

				// parent->child
				allowUsersOfParentProtestTheAccessToChildProtest(user_Info_DAO, parent_User_Protest_Role_BridgeList, eachCaseToBeJoined);

				// add bi-directional access between children
				for (Protest_Info otherCaseToBeJoined : listOfConsolidatedAndJoinedCases) {
					if (eachCaseToBeJoined.getA_No().equals(otherCaseToBeJoined.getA_No()) )
						continue;

					allowAllUsersOfProtestAToProtestB(user_Info_DAO, eachCaseToBeJoined.getA_No(), otherCaseToBeJoined.getA_No());
					allowAllUsersOfProtestAToProtestB(user_Info_DAO, otherCaseToBeJoined.getA_No(), eachCaseToBeJoined.getA_No());
				}
			}
		}
		
		
		//When two or more cases are joined the agency representative of one case is added to other cases and vice versa.
		Protest_Info eachProtestInfo;
		
		for (int i =0; i < listOfAllJoinedCases.size(); i++){
			
          eachProtestInfo = listOfAllJoinedCases.get(i);
          
          List<Protest_Info> allChildrenProtestInfoList = new ArrayList<Protest_Info>(listOfAllJoinedCases);
			allChildrenProtestInfoList.remove(eachProtestInfo);

			List<User_Protest_Role_Bridge> agencyUprb = user_Info_DAO
					.getAgencyUserProtestRoleBridge(eachProtestInfo.getA_No());
			
			addAgencyUsers(allChildrenProtestInfoList,agencyUprb,user_Info_DAO);
			
		}

	}
	
	
	

	private static void assignParentAndSupplementalRelationBasedOnPastHistory(Protest_Info parentProtestInfo,
			List<Protest_Info> listOfCasesToBeJoined, Protest_Info_DAO protest_Info_DAO) throws Exception {
		Protest_Info eachCaseToBeJoined;
		
		for(int i=0; i< listOfCasesToBeJoined.size(); i++){
			
			eachCaseToBeJoined = listOfCasesToBeJoined.get(i);
			
			// find if any of rest of cases in the list can be made supplemental of this case
			Protest_Info potentialSupplementalCase;
			for(int j=0; j < listOfCasesToBeJoined.size(); j++){
				
				potentialSupplementalCase = listOfCasesToBeJoined.get(j);

				if(eachCaseToBeJoined.equals(potentialSupplementalCase)) continue;
				
				if(wasThisCaseEverSupplementalOfParent(eachCaseToBeJoined.getA_No(), potentialSupplementalCase.getLast_parent_a_no())){
					
					makeThisProtestSupplementalProtest(protest_Info_DAO, eachCaseToBeJoined,potentialSupplementalCase);
					
					List<Protest_Info> suppProtestInfoListOfPotentialSuppCase = protest_Info_DAO.get_list_of_supplementalProtest(potentialSupplementalCase.getA_No());					
					
					if (!suppProtestInfoListOfPotentialSuppCase.isEmpty()){
						
						for (Protest_Info eachSuppProtest :  suppProtestInfoListOfPotentialSuppCase){
							makeThisProtestSupplementalProtest(protest_Info_DAO, eachCaseToBeJoined,
									eachSuppProtest);
						}
					}
					
					listOfCasesToBeJoined.remove(j--);
				}
				
				
			}
		
			
		}
		
		
		
	}




	/**
	 * @param protest_Info_DAO
	 * @param eachCaseToBeJoined
	 * @param potentialSupplementalCase
	 * @throws Exception
	 */
	private static void makeThisProtestSupplementalProtest(Protest_Info_DAO protest_Info_DAO,
			Protest_Info eachCaseToBeJoined, Protest_Info potentialSupplementalCase) throws Exception {
		potentialSupplementalCase.setCase_Type("SUPPLEMENTAL");
		potentialSupplementalCase.setParent_A_No(eachCaseToBeJoined.getA_No());
		protest_Info_DAO.updateProtest_Info(potentialSupplementalCase);
	}




	private static boolean wasThisCaseEverSupplementalOfParent(String aNoOfParent, String lastParentAnosSeparatedBySemiColonInThisCase) {
		boolean thisCaseWasSupplementalOfParent = false;
		
		if(lastParentAnosSeparatedBySemiColonInThisCase!=null && !lastParentAnosSeparatedBySemiColonInThisCase.equalsIgnoreCase("")){
			String[] arrayOfLastParentAno = lastParentAnosSeparatedBySemiColonInThisCase.split(";");
			List<String> listOfLastParentAno = Arrays.asList(arrayOfLastParentAno);
			
			for(String eachLastParentAno : listOfLastParentAno){
				if(eachLastParentAno.equalsIgnoreCase(aNoOfParent)){
					thisCaseWasSupplementalOfParent = true;
					break;
				}
			}
		}
		
		return thisCaseWasSupplementalOfParent;
	}




	private static String getParentA_no(Protest_Info protest_Info) {
		String parentAno = protest_Info.getParent_A_No();
		
		if(parentAno == null || parentAno.equalsIgnoreCase("")){
			parentAno = protest_Info.getA_No();
		}
		
		return parentAno;
	}

	/**
	 * When two cases are joined merge agency reps from one case to another and vice versa
	 * @param child_Protest_Info_List
	 * @param uprb
	 * @param user_Info_DAO
	 * @throws Exception
	 */
	private static void addAgencyUsers(List<Protest_Info> child_Protest_Info_List, List<User_Protest_Role_Bridge> uprb, User_Info_DAO user_Info_DAO)
			throws Exception {

		for (Protest_Info protestInfo : child_Protest_Info_List) {

			for (User_Protest_Role_Bridge eachUprb : uprb) {
				Boolean isUprbExists = user_Info_DAO
						.checkIfThisAgencyRepUserProtestRoleBridgeExists(
								protestInfo.getA_No(), eachUprb.getUser_Id(),
								eachUprb.getRole_Id());

				if (!isUprbExists) {
					User_Protest_Role_Bridge user_Protest_Role_Bridge = new User_Protest_Role_Bridge();

					user_Protest_Role_Bridge.setA_No(protestInfo.getA_No());
					user_Protest_Role_Bridge.setPo(protestInfo.getPo());
					user_Protest_Role_Bridge.setRole_Id(eachUprb.getRole_Id());
					user_Protest_Role_Bridge.setUser_Id(eachUprb.getUser_Id());

					user_Info_DAO.save(user_Protest_Role_Bridge);

				}
			}
		}

	}

	/**
	 * Allow Users Of Child Protest The Access To Parent Protest as a consolidated User
	 * @param user_Info_DAO
	 * @param A_A_No
	 * @param B_A_No
	 * @throws Exception
	 */
	private static void allowAllUsersOfProtestAToProtestB(
			User_Info_DAO user_Info_DAO, String A_A_No,
			String B_A_No) throws Exception {
		List<User_Protest_Role_Bridge> B_User_Protest_Role_BridgeList = user_Info_DAO
				.getUser_Protest_Role_Bridge_List_NonConsolidated(B_A_No);

		for (User_Protest_Role_Bridge each_B_User_Protest_Role_BridgeList : B_User_Protest_Role_BridgeList) {
			User_Protest_Role_Bridge isUprbExists = user_Info_DAO.getUser_Protest_Role_Bridge(A_A_No, each_B_User_Protest_Role_BridgeList.getUser_Id());

			if (isUprbExists != null){
				continue;
			}

			UserRoles role = UserRoles.getByCode(each_B_User_Protest_Role_BridgeList.getRole_Id());
			if (role != AGENCY_ADMIN && role != AGENCY_ATTORNEY) {
				User_Protest_Role_Bridge user_Protest_Role_Bridge = new User_Protest_Role_Bridge();
				user_Protest_Role_Bridge.setA_No(A_A_No);
				user_Protest_Role_Bridge.setPo(each_B_User_Protest_Role_BridgeList.getPo());
				user_Protest_Role_Bridge.setRole_Id(each_B_User_Protest_Role_BridgeList.getRole_Id());
				user_Protest_Role_Bridge.setUser_Id(each_B_User_Protest_Role_BridgeList.getUser_Id());
				user_Protest_Role_Bridge.setConsolidated_A_No(each_B_User_Protest_Role_BridgeList.getA_No());
				user_Info_DAO.save(user_Protest_Role_Bridge);
			}
		}
	}

	/** Allow Users Of Parent Protest The Access To Child Protest as a consolidated User
	 * @param user_Info_DAO
	 * @param parent_User_Protest_Role_BridgeList
	 * @param child_Protest_Info
	 * @throws Exception
	 */
	private static void allowUsersOfParentProtestTheAccessToChildProtest(
			User_Info_DAO user_Info_DAO,
			List<User_Protest_Role_Bridge> parent_User_Protest_Role_BridgeList,
			Protest_Info child_Protest_Info) throws Exception {

		for (User_Protest_Role_Bridge each_Parent_User_Protest_Role_Bridge : parent_User_Protest_Role_BridgeList) {
			User_Protest_Role_Bridge isUprbExists = user_Info_DAO.getUser_Protest_Role_Bridge(child_Protest_Info.getA_No(),each_Parent_User_Protest_Role_Bridge.getUser_Id());

			if (isUprbExists != null){
				continue;
			}

			UserRoles role = UserRoles.getByCode(each_Parent_User_Protest_Role_Bridge.getRole_Id());
			if (role != AGENCY_ADMIN && role != AGENCY_ATTORNEY) {
				User_Protest_Role_Bridge user_Protest_Role_Bridge = new User_Protest_Role_Bridge();
				user_Protest_Role_Bridge.setA_No(child_Protest_Info.getA_No());
				user_Protest_Role_Bridge.setPo(each_Parent_User_Protest_Role_Bridge.getPo());
				user_Protest_Role_Bridge.setRole_Id(each_Parent_User_Protest_Role_Bridge.getRole_Id());
				user_Protest_Role_Bridge.setUser_Id(each_Parent_User_Protest_Role_Bridge.getUser_Id());
				user_Protest_Role_Bridge.setConsolidated_A_No(each_Parent_User_Protest_Role_Bridge.getA_No());
				user_Info_DAO.save(user_Protest_Role_Bridge);
			}
		}
	}

	/**
	 * Allow Users Of Child Protest The Access To Parent Protest as a consolidated User
	 * @param user_Info_DAO
	 * @param parent_Protest_Info
	 * @param child_A_No
	 * @throws Exception
	 */
	private static void allowAllUsersOfChildProtestsTheAccessToParentProtest(
			User_Info_DAO user_Info_DAO, Protest_Info parent_Protest_Info,
			String child_A_No) throws Exception {
		List<User_Protest_Role_Bridge> child_User_Protest_Role_BridgeList = user_Info_DAO
				.getUser_Protest_Role_Bridge_List_NonConsolidated(child_A_No);

		for (User_Protest_Role_Bridge each_Child_User_Protest_Role_Bridge : child_User_Protest_Role_BridgeList) {
			User_Protest_Role_Bridge isUprbExists = user_Info_DAO.getUser_Protest_Role_Bridge(parent_Protest_Info.getA_No(),each_Child_User_Protest_Role_Bridge.getUser_Id());
			
			if (isUprbExists != null){
				continue;
			}

			UserRoles role = UserRoles.getByCode(each_Child_User_Protest_Role_Bridge.getRole_Id());
			if (role != AGENCY_ADMIN && role != AGENCY_ATTORNEY) {
				User_Protest_Role_Bridge user_Protest_Role_Bridge = new User_Protest_Role_Bridge();
				user_Protest_Role_Bridge.setA_No(parent_Protest_Info.getA_No());
				user_Protest_Role_Bridge.setPo(each_Child_User_Protest_Role_Bridge.getPo());
				user_Protest_Role_Bridge.setRole_Id(each_Child_User_Protest_Role_Bridge.getRole_Id());
				user_Protest_Role_Bridge.setUser_Id(each_Child_User_Protest_Role_Bridge.getUser_Id());
				user_Protest_Role_Bridge.setConsolidated_A_No(each_Child_User_Protest_Role_Bridge.getA_No());
				user_Info_DAO.save(user_Protest_Role_Bridge);
			}
		}
	}

	/**
	 * Unjoin two cases
	 * @param protest_Info_DAO
	 * @param user_Info_DAO
	 * @param file_Info_DAO
	 * @param listOfCasesToBeUnjoined
	 * @param parentProtestInfo
	 * @param listOfToBeJoined_child_protest_info 
	 * @throws Exception
	 */
	
//	@Transactional
	public static void unJoinCases(Protest_Info_DAO protest_Info_DAO,
			User_Info_DAO user_Info_DAO, File_Info_DAO file_Info_DAO,
			List<Protest_Info> listOfCasesToBeUnjoined,
			Protest_Info parentProtestInfo, List<Protest_Info> listOfToBeJoined_child_protest_info, boolean isCalledForGcService) throws Exception {
	
		// if parent case is child of some other case, do not unjoin any cases (this is useful for child case (which is parent in this method) that has supplemental cases) 
		
		
		if(parentProtestInfo.getParent_A_No()!=null && !parentProtestInfo.getParent_A_No().equalsIgnoreCase("")) {
			if(isCalledForGcService) return;
			
			
			listOfCasesToBeUnjoined.add(parentProtestInfo);
			parentProtestInfo = protest_Info_DAO.getProtestByA_no(parentProtestInfo.getParent_A_No());
			
			int indexOfParentProtestInfo = listOfCasesToBeUnjoined.indexOf(parentProtestInfo);
			
			if (indexOfParentProtestInfo >= 0){
				listOfCasesToBeUnjoined.remove(indexOfParentProtestInfo);	
			}
			
		}

		List<Protest_Info> listOfConsolidatedCases = parentProtestInfo.getListOf_ConsolidatedProtest_Info();

		List<User_Protest_Role_Bridge> parent_User_Protest_Role_BridgeList = user_Info_DAO
				.getUser_Protest_Role_Bridge_List_BasedOnProtestId(parentProtestInfo.getA_No());

		for (Protest_Info eachCaseToBeUnjoined : listOfCasesToBeUnjoined) {
			
			if (eachCaseToBeUnjoined.getCase_Type().equalsIgnoreCase(
					"Supplemental")) {
				eachCaseToBeUnjoined.setCase_Type("PROTEST");
				setLastParentANoToSupplementalCase(eachCaseToBeUnjoined);
				eachCaseToBeUnjoined.setParent_A_No(null);

				List<String> otherCasesAnumberList = new ArrayList<String>();
				otherCasesAnumberList.add(eachCaseToBeUnjoined.getA_No());

				user_Info_DAO.removeUserProtestRoleBridgeRecordsForACase(eachCaseToBeUnjoined.getA_No());

				user_Info_DAO.allowAllUsersOfCaseATheAccessToCaseB(parentProtestInfo.getA_No(),
						eachCaseToBeUnjoined.getA_No(),"");

			} else {
				eachCaseToBeUnjoined.setParent_A_No(null);
				removeAccessOfAllUsersOfProtestAToProtestB(user_Info_DAO,
						parent_User_Protest_Role_BridgeList,
						eachCaseToBeUnjoined.getA_No());

				removeAccessOfAllUsersOfParentProtestToChildProtest(
						user_Info_DAO, eachCaseToBeUnjoined.getA_No(),
						parentProtestInfo.getA_No());

				// remove bi-directional between children
				if (null != listOfConsolidatedCases) {
					for (Protest_Info otherCaseToBeUnjoined : listOfConsolidatedCases) {
						if (eachCaseToBeUnjoined.getA_No().equals(otherCaseToBeUnjoined.getA_No()) )
							continue;

						removeAccessOfAllUsersOfParentProtestToChildProtest(user_Info_DAO, eachCaseToBeUnjoined.getA_No(), otherCaseToBeUnjoined.getA_No());
						removeAccessOfAllUsersOfParentProtestToChildProtest(user_Info_DAO, otherCaseToBeUnjoined.getA_No(), eachCaseToBeUnjoined.getA_No());
					}
				}

				associateParentCaseWithSupplementalCasesOfTheCaseToBeUnjoined(parentProtestInfo, eachCaseToBeUnjoined, listOfToBeJoined_child_protest_info, protest_Info_DAO, user_Info_DAO, file_Info_DAO, isCalledForGcService);
			}

			protest_Info_DAO.updateProtest_Info(eachCaseToBeUnjoined);
		}

	}

	private static void setLastParentANoToSupplementalCase(Protest_Info protestInfo) {
		
		String currentLastParentANosSeparatedBySemiColonInOneString = protestInfo.getLast_parent_a_no();
		String newLastParentANo = protestInfo.getParent_A_No();
		String newLastParentANosSeparatedBySemiColonInOneString = "";
		
		if(currentLastParentANosSeparatedBySemiColonInOneString !=null 
				&& !currentLastParentANosSeparatedBySemiColonInOneString.equalsIgnoreCase("")){
			String[] arrayOfLastParentANos = currentLastParentANosSeparatedBySemiColonInOneString.split(";");

			// to make sure there is not repetition of a_No
			Set<String> setOfLastParentAnos = new HashSet<String>(Arrays.asList(arrayOfLastParentANos));
			setOfLastParentAnos.add(newLastParentANo);
			
			newLastParentANosSeparatedBySemiColonInOneString =  StringUtils.join(setOfLastParentAnos, ";");
			
			/*List<String> listOfAllLastParentAnos = new ArrayList<String>(setOfLastParentAnos);
			String eachLastParentAno;
			
			for(int i=0; i<listOfAllLastParentAnos.size(); i++){
				 eachLastParentAno = listOfAllLastParentAnos.get(i);
				 
				 if(i==0){
					 newLastParentANosSeparatedBySemiColonInOneString = eachLastParentAno;
				 }else{
					 newLastParentANosSeparatedBySemiColonInOneString +=";" + eachLastParentAno;
				 }
				 
			}*/
		}else{
			newLastParentANosSeparatedBySemiColonInOneString = newLastParentANo;
		}
		
		
		protestInfo.setLast_parent_a_no(newLastParentANosSeparatedBySemiColonInOneString);
		
	}

	private static void associateParentCaseWithSupplementalCasesOfTheCaseToBeUnjoined(Protest_Info parentProtestInfo,
			Protest_Info eachCaseToBeUnjoined, List<Protest_Info> listOfToBeJoined_child_protest_info, Protest_Info_DAO protest_Info_DAO, User_Info_DAO user_Info_DAO, File_Info_DAO file_Info_DAO, boolean isCalledForGcService) throws Exception {
		List<Protest_Info> listOfSupplementalCaseOfCaseToBeJoined = protest_Info_DAO.get_list_of_supplementalProtest(eachCaseToBeUnjoined.getA_No());
		Collections.sort(listOfSupplementalCaseOfCaseToBeJoined, Protest_Info.SubmissionDateComparatorOldToNew);
		
		Protest_Info eachCase = null;
		
		for(int i=0; i < listOfSupplementalCaseOfCaseToBeJoined.size(); i++){
			
			eachCase = listOfSupplementalCaseOfCaseToBeJoined.get(i);
			
			if(findIfSupplementProtestInfoInListOfCasesToBeJoined(eachCase, listOfToBeJoined_child_protest_info)){
				
				List<Protest_Info> listOfCasesToBeUnjoined = new ArrayList<Protest_Info>();
				listOfCasesToBeUnjoined.add(eachCase);
				
				unJoinCases(protest_Info_DAO, user_Info_DAO, file_Info_DAO, listOfCasesToBeUnjoined, parentProtestInfo, listOfToBeJoined_child_protest_info, isCalledForGcService);
				
				List<Protest_Info> listOfCasesToBeJoined = new ArrayList<Protest_Info>();
				listOfCasesToBeJoined.add(eachCase);
				
				
				joinCases(protest_Info_DAO, user_Info_DAO, listOfCasesToBeJoined, parentProtestInfo, file_Info_DAO);
				
				makeRestOfTheSupplementalCasesInTheListAsSupplementalOfThisCase(protest_Info_DAO, eachCase, listOfSupplementalCaseOfCaseToBeJoined, i+1);
				
				break;
			}
		}
		
	}




	private static void makeRestOfTheSupplementalCasesInTheListAsSupplementalOfThisCase(
			Protest_Info_DAO protest_Info_DAO, Protest_Info eachCase,
			List<Protest_Info> listOfSupplementalCaseOfCaseToBeJoined, int indexOfFirstSupplementalCase) throws Exception {
		Protest_Info eachSupplementalProtestInfo;
		
		if(listOfSupplementalCaseOfCaseToBeJoined.size() >= indexOfFirstSupplementalCase){
			for(int i = indexOfFirstSupplementalCase; i<listOfSupplementalCaseOfCaseToBeJoined.size(); i++){
				eachSupplementalProtestInfo = listOfSupplementalCaseOfCaseToBeJoined.get(i);
				setLastParentANoToSupplementalCase(eachSupplementalProtestInfo);
				eachSupplementalProtestInfo.setParent_A_No(eachCase.getA_No());
				
				protest_Info_DAO.updateProtest_Info(eachSupplementalProtestInfo);
			}
		}
	}




	private static boolean findIfSupplementProtestInfoInListOfCasesToBeJoined(Protest_Info supplementalProtestInfo,
			List<Protest_Info> listOfToBeJoined_child_protest_info) {
		for(Protest_Info eachProtestInfoToBeJoined: listOfToBeJoined_child_protest_info){
			if(supplementalProtestInfo.getA_No().equalsIgnoreCase(eachProtestInfoToBeJoined.getA_No())) return true;
		}
		
		return false;
	}




	/**
	 * @param file_InfoList
	 * @return
	 */
	private static List<Integer> getFileInfoIdList(List<File_Info> file_InfoList) {
		List<Integer> fileInfoIdList = new ArrayList<Integer>();

		if (file_InfoList != null) {
			for (File_Info eachFileInfo : file_InfoList) {
				fileInfoIdList.add(eachFileInfo.getFile_Id());
			}
		}

		return fileInfoIdList;
	}

	/**
	 * @param user_Info_DAO
	 * @param child_A_No
	 * @param parent_A_No
	 * @throws Exception
	 */
	private static void removeAccessOfAllUsersOfParentProtestToChildProtest(
			User_Info_DAO user_Info_DAO, String child_A_No, String parent_A_No)
			throws Exception {
		List<User_Protest_Role_Bridge> child_User_Protest_Role_BridgeList = user_Info_DAO
				.getUser_Protest_Role_Bridge_List_BasedOnProtestId(child_A_No);

		removeAccessOfAllUsersOfProtestAToProtestB(user_Info_DAO,
				child_User_Protest_Role_BridgeList, parent_A_No);
	}

	/**
	 * @param user_Info_DAO
	 * @param protestB_User_Protest_Role_BridgeList
	 * @param protestA_A_No
	 * @throws Exception
	 */
	private static void removeAccessOfAllUsersOfProtestAToProtestB(
			User_Info_DAO user_Info_DAO,
			List<User_Protest_Role_Bridge> protestB_User_Protest_Role_BridgeList,
			String protestA_A_No) throws Exception {
		for (User_Protest_Role_Bridge each_ProtestB_User_Protest_Role_Bridge : protestB_User_Protest_Role_BridgeList) {
			String consolidated_A_No = each_ProtestB_User_Protest_Role_Bridge
					.getConsolidated_A_No();
			if (consolidated_A_No != null
					&& consolidated_A_No.equals(protestA_A_No)) {
				user_Info_DAO.delete(each_ProtestB_User_Protest_Role_Bridge);
			}
		}

	}

	/**
	 * @param certPath
	 * @return
	 * @throws Exception
	 */
    public static HttpsURLConnection getPayDotGovURL_connection(String certPath)
            throws Exception {
        System.out.println(certPath);
        System.setProperty("weblogic.StdoutDebugEnabled", "true");


        System.out.println(System.getProperty("https.cipherSuites"));

        KeyStore clientStore = KeyStore.getInstance("PKCS12");

        System.out.println(certPath);

        clientStore.load(new FileInputStream(certPath),
                Params.payDotGovCertPassword.toCharArray());

        KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory
                .getDefaultAlgorithm());
        kmf.init(clientStore, Params.payDotGovCertPassword.toCharArray());
        KeyManager[] kms = kmf.getKeyManagers();

        KeyStore trustStore = KeyStore.getInstance("JKS");
        trustStore.load(new FileInputStream(Params.javaTrustStoreLocation),
                "changeit".toCharArray());

        TrustManagerFactory tmf = TrustManagerFactory
                .getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmf.init(trustStore);
        TrustManager[] tms = tmf.getTrustManagers();

		/*SSLContext sslContext = null;
		sslContext = SSLContext.getInstance("TLS");
		sslContext.init(kms, tms, new SecureRandom());*/

	/*	HttpsURLConnection.setDefaultSSLSocketFactory(sslContext
				.getSocketFactory());*/
        SSLSocketFactoryEx factory = new SSLSocketFactoryEx(kms, tms, new SecureRandom());

        HttpsURLConnection.setDefaultSSLSocketFactory(factory);
        URL url = new URL(Params.payDotGovURL);

        HttpsURLConnection urlConn = (HttpsURLConnection) url.openConnection();

        System.out.println("Response Code : " + urlConn.getURL() + "    " + urlConn.getResponseCode());
        System.out.println("Cipher Suite : " + urlConn.getCipherSuite());
        System.out.println("\n");

       
		return urlConn;
    }

	public static String getNonDecimalPart(String text) {
		String returnValue = "";
		if (text != null) {
			String[] dotSeparatedValues = text.split("\\.");
			if (dotSeparatedValues == null || dotSeparatedValues.length == 0) {
				returnValue = text;
			} else {
				returnValue = dotSeparatedValues[0];
			}
		}

		return returnValue;
	}

	public static void populateSupplementalNumbersForEachCaseInDashboard(Protest_Info eachProtest_Info,
			Protest_Info parentProtest_Info) {
		
		if (eachProtest_Info.getB_No() != null && !eachProtest_Info.getB_No().equals("")) {
			if (parentProtest_Info.getSupplemental_B_Nos() == null) {
				parentProtest_Info.setSupplemental_B_Nos("; " + eachProtest_Info.getB_No());
				parentProtest_Info.setSupplemental_A_Nos("; " + eachProtest_Info.getA_No());
			} else {
				List<String> suppBList = new java.util.ArrayList<>(Arrays.asList(parentProtest_Info.getSupplemental_B_Nos().split("(?=;)")));
				List<String> suppAList = new java.util.ArrayList<>(Arrays.asList(parentProtest_Info.getSupplemental_A_Nos().split("(?=;)")));

				String suppBComp = "; " + eachProtest_Info.getB_No();
				int i = 0;
				for (; i < suppBList.size(); i++) {
					if (suppBComp.compareTo(suppBList.get(i)) < 0) {
						// if new B# is less than existing, insert
						suppBList.add(i, suppBComp);
						suppAList.add(i, "; " + eachProtest_Info.getA_No());
						break;
					}
				}
				if (i == suppBList.size()) {
					// reached end, just add
					suppBList.add(suppBComp);
					suppAList.add("; " + eachProtest_Info.getA_No());
				}
				String suppB = String.join("", suppBList);
				String suppA = String.join("", suppAList);
				parentProtest_Info.setSupplemental_B_Nos(suppB);
				parentProtest_Info.setSupplemental_A_Nos(suppA);
			}
		}
	}


	/**
	 * check for the supplemental protest characteristics in child protest wrt parent protest and return true/false 
	 * @param protestInfo = PARENT PROTEST INFO
	 * @param supplementalProtestInfo
	 * @return boolean 
	 */
	public static Boolean checkIfThisProtestIsSupplemental(Protest_Info protestInfo, Protest_Info supplementalProtestInfo){
		
		Boolean isChildProtestSupplemental = false;
		Boolean checkIfBNumPrefixMatches = false;
		Boolean isProtestCasetypeQualifiesAsSupp = false;
		
		if ("SUPPLEMENTAL".equalsIgnoreCase(supplementalProtestInfo.getCase_Type())){
			isProtestCasetypeQualifiesAsSupp = true;
		}else {
			isProtestCasetypeQualifiesAsSupp = protestInfo.getCase_Type()
					.equalsIgnoreCase(supplementalProtestInfo.getCase_Type());
		}
		
		if (protestInfo.getB_No() != null 
				&& supplementalProtestInfo.getB_No() != null){
			String[] parentBNum = protestInfo.getB_No().split("\\.");
			String[] suppBNum = supplementalProtestInfo.getB_No().split("\\.");
			
			if (parentBNum.length == 2 && suppBNum.length == 2) {
				checkIfBNumPrefixMatches = parentBNum[0].toUpperCase(Locale.ENGLISH).contains(suppBNum[0].toUpperCase(Locale.ENGLISH));
			}else if (parentBNum.length != 2 || suppBNum.length != 2){
				checkIfBNumPrefixMatches = protestInfo.getB_No().equalsIgnoreCase(supplementalProtestInfo.getB_No());
			}
			
		}
		
		if (checkIfBNumPrefixMatches
				&& protestInfo.getCompany_Name()
				.equalsIgnoreCase(supplementalProtestInfo.getCompany_Name())
				&& protestInfo.getSolicitation_No()
				.equalsIgnoreCase(supplementalProtestInfo.getSolicitation_No())
			   && protestInfo.getA_No()
				.equalsIgnoreCase(supplementalProtestInfo.getParent_A_No())
				&& isProtestCasetypeQualifiesAsSupp){
			isChildProtestSupplemental = true;
		}
		
		
		return isChildProtestSupplemental;
	}

}
