(function() {
	'use strict';

	var serviceId = 'registrationService';

	angular.module('epdsApp.registration').factory(serviceId,
			['$http', '$rootScope', '$uibModal','$q','$location','base64','actionMessageSvc','regEx', registrationService]);

	/* @ngInject */
	function registrationService ($http, $rootScope, $uibModal,$q,$location,base64,actionMessageSvc,regEx) {

		var service = {
				registerUser : registerUser,
				checkEmailValidity : checkEmailValidity,
				getRegistrationFieldsBasedOnRole : getRegistrationFieldsBasedOnRole,
				getFormInputRegexPatterns : getFormInputRegexPatterns,
				validateRegisterationForm : validateRegisterationForm
		};

		return service;
		
		
		function validateRegisterationForm(form) {
			
			var nameOfFirm ;
			
			if (form.nameoffirm){
				nameOfFirm = form.nameoffirm;
			
			}else if (form.tier2Id &&  form.tier2Id.agency_Name){
				
				nameOfFirm = form.tier2Id.agency_Name;
			}else {
				nameOfFirm = form.tier1Id.agency_Name;
			}
			
			
			
			var form  = {
					prefix : form.prefix,
					lastName : form.lastname,
					firstName : form.firstname,
					middle_initial : form.mi,
					suffix : form.suffix,
					email : base64.urlencode(form.email),
					phoneNo : form.phonenumber,
					faxNo : form.faxnumber,
					address1 : form.address1,
					address2 : form.address2,
					city : form.city,
					state : form.state,
					country : form.country,
					zipCode : form.zipcode,
					nameOfFirm : nameOfFirm,
					epds_role_id : form.role,
					tier1_agency_id : (form.tier1Id ? form.tier1Id.agency_Id : null) ,
					tier2_agency_id : (form.tier2Id ? form.tier2Id.agency_Id : null),
					}
			
			
			return $http({
				url : '/epds/user/validate-register-form',
				method : 'POST',
				headers: {
					'Accept': 'application/json',
					'Content-Type': 'application/json'
						},
			    data: angular.toJson(form),
			}).then(
					function(data) {
						
						return	data.data;

					},
					function(error) {
						
						return error;
					});

		}

		function registerUser(form) {
			
			var nameOfFirm ;
			
			if (form.nameoffirm != null 
					|| typeof form.nameoffirm != 'undefined' ){
				nameOfFirm = form.nameoffirm;
			
			}else if (form.tier2Id && typeof form.tier2Id.agency_Id !='undefined'	){
				
				nameOfFirm = form.tier2Id.agency_Name;
			}else {
				nameOfFirm = form.tier1Id.agency_Name;
			}
			
			
			var form  = {
					prefix : form.prefix,
					lastName : form.lastname,
					firstName : form.firstname,
					middle_initial : form.mi,
					suffix : form.suffix,
					email : base64.urlencode(form.email),
					phoneNo : form.phonenumber,
					faxNo : form.faxnumber,
					address1 : form.address1,
					address2 : form.address2,
					city : form.city,
					state : form.state,
					country : form.country,
					zipCode : form.zipcode,
					nameOfFirm : nameOfFirm,
					epds_role_id : form.role,
					tier1_agency_id : (typeof form.tier1Id != 'undefined' ? form.tier1Id.agency_Id : null) ,
					tier2_agency_id : (form.tier2Id && typeof form.tier2Id != 'undefined' ? form.tier2Id.agency_Id : null),
					}
			
			
			return $http({
				url : '/epds/user/register',
				method : 'POST',
				headers: {
					'Accept': 'application/json',
					'Content-Type': 'application/json'
						},
			    data: angular.toJson(form),
			}).then(
					function(data) {
						
						return	data.data;

					},
					function(error) {
						
						return error;
					});

		}


		function checkEmailValidity(email, role) {
			return checkIfEmailContainsTrustedDomain(email,role);
		}
		
		function checkIfEmailContainsTrustedDomain(repEmail,role) {
			
			var response =  {};
			var def = $q.defer();
			if (repEmail.length > 0) {

				var trustedDomainList = ['gov','mil']
				for (var i = 0; i < trustedDomainList.length; i++) {
					trustedDomainList[i] == trustedDomainList[i].toLowerCase()
				}

				try {
					var emailREGEX = /^[a-zA-Z0-9._+'-]+@([a-zA-Z0-9.-]+\.[a-zA-Z]{2,4})$/
					var checkIfThisIsAValidEmailAddress = emailREGEX.exec(repEmail)

					if (!checkIfThisIsAValidEmailAddress) {
						response.message = repEmail + ' is not a valid email';
						return $q.when(response);
					}

					var isValidEmailExtension = false;

					
					var emailFullyQualifiedDomain = checkIfThisIsAValidEmailAddress[1].split(".")
					var getEmailAddExtension = emailFullyQualifiedDomain[emailFullyQualifiedDomain.length -1].toLowerCase();
					
					for (var i = 0; i < trustedDomainList.length; i++) {
						if (getEmailAddExtension == trustedDomainList[i]) {
							isValidEmailExtension = true;
							break;
						}
					}

					if (!isValidEmailExtension && role == "6") {
						response.message = checkIfThisIsAValidEmailAddress[1] + ' is not allowed.  Please enter an email address with an authorized domain. Ex : .gov or .mil';
						return $q.when(response);
					}
					return checkIfEmailExists(email);

				} catch (err) {
					
					return def.reject();
				}
			}

		}
		
		
		function checkIfEmailExists(email){
			

			return $http({
				url : '/epds/user/check-if-user-exists/' + base64.urlencode(email),
				method : 'GET',
				headers: {
					'Accept': 'application/json',
					'Content-Type': 'application/json'
						},
			    
			})
			.then(
					function(data) {
						
						return (data.data.message.length > 0) ? data.data: null

					},
					function(error) {
						
						return error;
					});

		
		}
		
		
		function getRegistrationFieldsBasedOnRole(role){
			
			var retFunc;
			switch (role) {
			
			case "1": 
				retFunc =  vendorFields();
				break;
			case "6": 
				retFunc = agencyRepFields();
				break;

			default: ;
				break;
			}
			
			return $q.when(retFunc)
		}
		
		function vendorFields(){
			var obj = {};
			var fieldText = {}
			fieldText.headerText = "Register as  Non-Agency Party Representative Representative";
			fieldText.contactInfo = "Non-Agency Party Representative Info";
			fieldText.nameOfFirm = "Name of Company/Firm";
			fieldText.companyAddress = "Mailing Address";
			fieldText.emailToolTip = "Enter an valid email address. It will be used as your User ID";
			fieldText.prefixToolTip = "(ex: Mr., Mrs., Dr., Professor, etc.)";
			fieldText.suffixToolTip = " (e.g., Jr., Sr., III).";
			fieldText.companyAddToolTip = "Please enter the Business address";
			obj.fieldText = fieldText
			return obj;
			
		}
		
		function agencyRepFields(){
			var obj = {};
			var fieldText = {}
			fieldText.headerText = "Register as an Agency Representative";
			fieldText.contactInfo = "Agency Representative Info";
			fieldText.nameOfFirm = "Please Select the Government Agency you are representing";
			fieldText.companyAddress = "Business Address";
			fieldText.emailToolTip = "Enter an email address with a valid extension i.e; .gov or .mil";
			fieldText.companyAddToolTip = "Please enter the Agency address";
			obj.fieldText = fieldText
			return obj;
			
		}
		
		//not being used ..it was used in the registration form but now it is not used
		function getFormInputRegexPatterns(){
			var regEx = {
					email : /^[a-zA-Z0-9._+'-]+@([a-zA-Z0-9.-]+\.[a-zA-Z]{2,4})$/,
					prefix : /^[a-zA-Z0-9. ]{1,10}$/,
					firstName : /^[a-zA-Z0-9 ]{2,25}$/,
					middleInitial : /^[a-zA-Z0-9]{1}$/,
					lastName : /^[a-zA-Z0-9 ]{2,25}$/,
					suffix :/^[a-zA-Z0-9. ]{1,10}$/,
					nameOfFirm : /^[a-zA-Z0-9 !,@#$&.)(/\\-]{2,200}$/,
					address : /^[a-zA-Z0-9 #&,;:'.)(/\\-]{2,100}$/,
					zipcode : /^[a-zA-Z0-9- ]{2,20}$/,
					city : /^[a-zA-Z0-9 &.)(/\\':;-]{2,100}$/,
					state : /^[a-zA-Z0-9 .)(/\\-]{2,20}$/,	
					country : /^[a-zA-Z ]{2,20}$/,	
					}
			
			
			return $q.when(regEx);
		}
	}
})();
