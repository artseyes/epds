/*package gov.gao.epds.test;

import gov.gao.epds.dao.File_Info_DAO;
import gov.gao.epds.dao.GC_Track_Service_DAO;
import gov.gao.epds.dao.Protest_Info_DAO;
import gov.gao.epds.persistence.entity.File_Info;
import gov.gao.epds.persistence.entity.Protest_Info;
import gov.gao.epds.utils.Params;
import gov.gao.epds.utils.SpringApplicationContext;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class TestDataHandler {

	@RequestMapping(value = "/remove-case/{a_number}/{password}", method = RequestMethod.GET, produces = "application/json")
	private static void removeGC_webservice_data(
			@PathVariable("a_number") String a_number,
			@PathVariable("password") String password) throws Exception {
		if (password.equals("Testing123"Params.removeCasePassword)) {
			Protest_Info_DAO protest_info_dao = (Protest_Info_DAO) SpringApplicationContext
					.getBean("protest_Info_DAO");
			File_Info_DAO file_info_dao = (File_Info_DAO) SpringApplicationContext
					.getBean("file_Info_DAO");
			GC_Track_Service_DAO gc_Track_Service_DAO = (GC_Track_Service_DAO) SpringApplicationContext
					.getBean("GC_Track_Service_DAO");

			// remove user_protest_role_bridge
			protest_info_dao
					.removeAll_user_protest_role_bridge_recordsByA_no(a_number);

			// remove gc events
			gc_Track_Service_DAO.deleteEventsByA_no(a_number);

			List<File_Info> list_of_file_info = file_info_dao
					.getListOf_file_info_basedOnA_no(a_number);

			// remove all file_alert records
			if (list_of_file_info != null && list_of_file_info.size() > 0) {
				file_info_dao
						.removeFile_Alert_records_forGivenListOf_file_Info(list_of_file_info);
			}

			// remove all protest_file_bridge records
			file_info_dao
					.remove_all_protest_file_bridge_records_forGivenA_no(a_number);
			file_info_dao
					.remove_all_protest_file_bridge_records_forGivenListOf_file_Info(list_of_file_info);

			// remove file_info
			file_info_dao.removeAll_file_info_records(a_number);

			// remove protest_info
			Protest_Info protest_info = protest_info_dao
					.getProtestByA_no(a_number);
			protest_info_dao.delete(protest_info);

		} else {
			throw new IllegalArgumentException();
		}
	}
}
*/