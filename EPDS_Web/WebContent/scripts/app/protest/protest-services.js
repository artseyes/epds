/*
 * Protest Data service is used for 
 * 1) loadProtestFilingForm 
 * 2) redirecting to pay.gov
 * 3) registerProtest
 * 4) showInvalidFormMessages : 
 * 			Basically, this is in place mainly to validate if protest document is attached and 
 * 					if the user selects yes to associated document and if there are no document attached then it will throw error.
*/


(function() {
	'use strict';

	var serviceId = 'protestDataSvc';

	
	angular.module('epdsApp.dashboard').factory(
			serviceId,
			[ '$rootScope', '$http', '$filter', '$uibModal','$uibModalStack',
				'modalService', '$location', '$timeout','userInfoService','$httpParamSerializerJQLike','$window','localStorageService','$q','navigationSvc','base64','regEx', protestDataSvc ]);

	/* @ngInject */
	function protestDataSvc($rootScope, $http, $filter, $uibModal,$uibModalStack,
			modalService, $location, $timeout,userInfoService,$httpParamSerializerJQLike,$window,localStorageService,$q,navigationSvc,base64,regEx) {

		var service = {
				loadProtestFilingForm : loadProtestFilingForm,
				startPayDotGovOnlineTransaction : startPayDotGovOnlineTransaction,
				registerProtest : registerProtest,
				validateProtestInfo : validateProtestInfo,
				registerOtherProtests : registerOtherProtests,
				showInvalidFormMessages : showInvalidFormMessages,
				testPayDotGov : testPayDotGov,
				getUserInfoByEmail :getUserInfoByEmail
		};

		return service;

		function loadProtestFilingForm(params) {
			
			
			return $http({
						url : '/epds/protest-request',
						method : 'GET',
						headers: {
							'Content-Type': 'application/json'
								},
					    params: params,
					})
					.then(
							function(data) {

								$rootScope.authenticated = true;
								userInfoService.setUserInfo(data.data.user_Info);
								if (params.typeOfProtest === "protest"){
		
									userInfoService.getRoleId(data.data.user_Role).then(function(roleId){
										var navigationObj = {
												navigationType : "dashboard",
												caseStatus : "N/A",
												roleId :  roleId
													
										} 
										navigationSvc.setListOfRoutesBasedOnRole(navigationObj);
									});
								}
								
								
									data.data.form = _protestInfoFormData(data.data);
								return data.data;
							},
							function(error) {
								
								return error;
							});

		}
		
		
		function _getParamsForRegisterProtest(form){
			
			var params = {
			        a_No : form.a_No,
					typeOfProtest : "protest",
					agency_tier_1 : form.tier1Id.agency_Id,
					firstname :  form.firstname,
					lastname :  form.lastname,
					email :  form.email,
					phonenumber :  form.phonenumber,
					faxnumber :  form.faxnumber,
					address1 :  form.address1,
					address2 :  form.address2,
					zipcode :  form.zipcode,
					city :  form.city,
					state :  form.state,
					country :  form.country,
					company_status :  form.company_status,
					isDocConfidential :  form.isDocConfidential,
					company_city :  form.company_city,
					company_country :  form.company_country,
					company_state :  form.company_state,
					company_name :  form.company_name,
					company_address1 :  form.company_address1,
					company_address2 :  form.company_address2,
					company_zipcode :  form.company_zipcode,
					solicitationNumber :  form.solicitationNumber,
					comments : form.comments,
						
				}
				  if (typeof form.tier2Id != 'undefined'  && form.tier2Id != null) {
					  params.agency_tier_2 =  form.tier2Id.agency_Id
				  }
			
			return params;
		}
		
		
		function registerProtest(form){
			
			var form = form || localStorageService.get("protestInfoForm");
			
			var params =_getParamsForRegisterProtest(form);
			
			
		return 	$http({
				method : 'POST',
				url : '/epds/register-protest',
				headers : {
					'Content-Type' : 'application/x-www-form-urlencoded'
				},
				data: $httpParamSerializerJQLike(_.omitBy(params, _.isNil)),
			})
			.then(
					function(data) {
						localStorageService.set("protestRegistered", "Y");
						return data.data;
					},
					function(error) {
						
						return error;
					});
		}
		
		function validateProtestInfo(form){
			
			var params =_getParamsForRegisterProtest(form);
				
			
			return 	$http({
					method : 'POST',
					url : '/epds/validate-protest',
					headers : {
						'Content-Type' : 'application/x-www-form-urlencoded'
					},
					data: $httpParamSerializerJQLike(_.omitBy(params, _.isNil)),
					
				}).then(
					function(data) {
						
						return data.data;
					},
					function(error) {
						
						return error;
					});
			
		}
		
		function registerOtherProtests(form,a_No,typeOfProtest,filerId){
			if (!filerId){
				filerId = 0;
			}
			var params = {
				typeOfProtest : typeOfProtest,
				firstname :  form.firstname,
				lastname :  form.lastname,
				email :  form.email,
				phonenumber :  form.phonenumber,
				faxnumber :  form.faxnumber,
				street :  form.street,
				zipcode :  form.zipcode,
				city :  form.city,
				state :  form.state,
				country :  form.country,
				company_status :  form.company_status,
				isDocConfidential :  form.isDocConfidential,
				company_city :  form.company_city,
				company_country :  form.company_country,
				company_state :  form.company_state,
				company_name :  form.company_name,
				company_street :  form.company_street,
				company_zipcode :  form.company_zipcode,
				solicitationNumber :  form.solicitationNumber,
				comments : form.comments,
				protestId : a_No,
				filerId : filerId.id
					
			}
			
			
		return 	$http({
				method : 'POST',
				url : '/epds/register-protest',
				headers : {
					'Content-Type' : 'application/x-www-form-urlencoded'
				},
				data: $httpParamSerializerJQLike(_.omitBy(params, _.isNil)),
			})
			.then(
					function(data) {
						
						return data.data;
					},
					function(error) {
						
						return error;
					});
		}
		
		function startPayDotGovOnlineTransaction(a_No){
			
		    var params = { a_No : a_No }
		    
			return $http({
						method : 'POST',
						url : '/epds/startPayDotGovOnlineCollection',
						headers : {
							'Content-Type' : 'application/x-www-form-urlencoded'
						},
						data: $httpParamSerializerJQLike(_.omitBy(params, _.isNil)),
					})
			.then(
					function(data) {
						
						var response = data.data;
						
						
						/*if (response.payDotGovToken == null 
								|| typeof response.payDotGovToken == 'undefined'){
							localStorageService.set("payDotGovTokenError",response);
							$location.path("/payDotGovTokenError")
						}else {
						$window.location.href = 'https://qa.pay.gov/tcsonline/payment.do?token='
								+ response.payDotGovToken
								+ '&tcsAppID=TCSGAOEPDS';
						}*/
					
						return data.data;
					},
					function(error) {
						
						return error;
					});
		}
		
		
		function testPayDotGov(){
					
					return $http({
								method : 'POST',
								url : '/epds/testPayDotGov',
							})
					.then(
							function(data) {
							
								return data.data;
							},
							function(error) {
								
								return error;
							});
				}
		


		function getUserInfoByEmail(email) {

			var def = $q.defer();
			var emailREGEX = regEx.email;
				var checkIfThisIsAValidEmailAddress = emailREGEX.exec(email)

				if (checkIfThisIsAValidEmailAddress) {
					var params = {
							email : base64.urlencode(email && email.toLowerCase())
					}
					return $http({
						url : '/epds/userInfoByEmail/',
						method : 'POST',
						headers : {
							'Accept' : 'application/json',
							'Content-Type' : 'application/json'
						},
						data : angular.toJson(params),
						ignoreLoadingBar : true
					}).then(function(data) {
						data.data.userInfoObj = _populateUserInfoObject(data.data);
						def.resolve(data.data);
						return data.data;
					}, function(error) {
						def.reject(error);
						return error;
					});

				}
		}
		
		
		function showInvalidFormMessages(isFormValid,attachAssociatedDocs,numberOfPrimaryDocumentsAdded,numberOfAssociatedDocumentsAdded){
			
			
			var obj = {};
			obj.attachAssociatedDocs = attachAssociatedDocs;
			obj.isFormValid = isFormValid;
			obj.isAssocatedDocumentsAttached = numberOfAssociatedDocumentsAdded;
			obj.isProtestDocumentAttached = numberOfPrimaryDocumentsAdded;

			if (isFormValid) {
				if (numberOfPrimaryDocumentsAdded != 1) {
					var customModalOptions = {
						headerText : 'Error',
						bodyText : 'This type of filing requires that you upload a document.  Please upload your document to continue.  Information regarding appropriate file types is included in the EPDS user guides.',
						closeButtonText : 'OK',
						messageType : "error"
					};

					modalService.showModal({}, customModalOptions).then(
							function(result) {
							});
					obj.isFormValid = false;

				} else if (attachAssociatedDocs === 'Y'
						&& numberOfAssociatedDocumentsAdded <= 0) {

					var customModalOptions = {
						headerText : 'Error',
						bodyText : 'Please attach associated document(s) or select NO.',
						closeButtonText : 'OK',
						messageType : "error"
					};

					modalService.showModal({}, customModalOptions);
					obj.isFormValid = false;
				}else{
					obj.isPrimaryDocumentAttached = true;
					obj.isAssocatedDocumentsAttached = true;
					obj.isFormValid = true;
				}

			} else {
				var customModalOptions = {
					headerText : 'Error',
					bodyText : 'Please review the form. You have not entered some required fields.',
					closeButtonText : 'OK',
					messageType : "error"
				};

				modalService.showModal({}, customModalOptions);
			}

			return $q.when(obj);
		}
		
		function _protestInfoFormData(response){
			
			var form = {
			
					company_status : null,
					isDocConfidential : null,
					company_city : null,
					company_country : null,
					company_state : null,
					attachAssociatedDoc : null,
					protestInfo : (typeof response.protestInfo != 'undefined' 
												|| response.protestInfo != null) ? response.protestInfo : null
				}
			
			if (response.user_Info.role_id != "7"){
				
				var userInfoObj = _populateUserInfoObject(response);
				
				angular.extend(form,userInfoObj)
				
			}
			
			return form;
		}
		
		
		function _populateUserInfoObject(response){
			
			
			if (response && !response.user_Info){
				return
			}
			var userInfoObj = {
					
					firstname : response.user_Info.first_Name,
					lastname : response.user_Info.last_Name,
					email : response.user_Info.email,
					phonenumber : response.user_Info.phone_No,
					faxnumber : response.user_Info.fax_No,
					address1 : response.user_Info.address1,
					address2 : response.user_Info.address2,
					zipcode : response.user_Info.zip_Code,
					city : response.user_Info.city,
					state : response.user_Info.state,
					country : response.user_Info.country,
			} 
			
			return userInfoObj;
		}
		

	}
})();
