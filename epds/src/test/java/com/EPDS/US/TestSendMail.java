package com.EPDS.US;

import gov.gao.epds.service.EmailService;
import gov.gao.epds.utils.SpringApplicationContext;

public class TestSendMail {

	public static void sendEmail() throws Exception {
		/*ProtestInfoService protestInfoService = (ProtestInfoService) SpringApplicationContext
				.getBean("protestInfoService");*/
		EmailService emailService = (EmailService) SpringApplicationContext
				.getBean("emailSevice");
		//emailService.sendMailtoAgency("sendtora@gmail.com");

	}
}
