(function() {
	'use strict';

	var serviceId = 'authLoginActionsSvc';
	
	
	angular.module('epdsApp.auth').factory(serviceId,
			['$q','$location','ModalSvc','authFeedbackMessagesSvc','$uibModal',
			 'localStorageService','Idle','actionMessageSvc','authenticationService','base64','$cookies',authLoginActionsSvc ]);

	/* @ngInject */
	function authLoginActionsSvc($q,$location,ModalSvc,authFeedbackMessagesSvc,
			$uibModal,localStorageService,Idle,actionMessageSvc,authenticationService,base64,$cookies) {

		var service = {
				redirectUser : redirectUser,
		};
		
		var modalOptions = {};
		return service;

		function redirectUser(response) {
			
			if (typeof response.inputErrors !== 'undefined'){
				
				var customAttr = {
						headerText : "Error"	,
						bodyText : "",
						modalType : "error",
						actionType : "",
					    cancelBtnReq : "N",
					    cancelBtnActionType : "",
					    inputErrorMessages : response.inputErrors
					}
				
				actionMessageSvc.showModal(customAttr);
			}/*else if (response.data == null){
				authenticationMessages("acctDoesNotExist",response)
			}*/else if ('undefined' !== typeof response.data 
				&& null !== response.data
				&& response.data.isROBRequired === true
									&& response.isLoginSuccess){
				
				authenticationService.rulesOfBehavior(response.data.email).then(function(rulesOfBehavior){
					if (rulesOfBehavior.isSuccess){
						redirectUserBasedOnAccountStatus(response);
					}
				});
				
			}else if (response.message === "Incorrect Password" 
				|| response.message === "User Id / Password doesn't match"){
				authenticationMessages("acctDoesNotExist",response)
			}else if (response.message === "Account Locked : Status Not changed"){
				authenticationMessages("accountLockedStatusNotChanged",response)
			}else if (response.message === "Temp. Password Expired"){
				
				authenticationMessages("tempPwdExpired",response);
				
			}else if (response.message === "Your account is locked. Please check in your email for temporary password"){
				
				if (response.isLoginSuccess){
					
					authenticationService.rulesOfBehavior(response.data.email).then(function(rulesOfBehavior){
						if (rulesOfBehavior.isSuccess){
							//need to take to the password reset page
							authenticationMessages("vendorAcctReset",response)
						}
					});
				}else if (!response.isLoginSuccess){
					authenticationMessages("vendorLockedMessage",response)
				}
			}else if(response.data !== null){
				
				redirectUserBasedOnAccountStatus(response);
			}/*else if (response.message === "User Id / Password doesn't match" || response.isLoginSuccess === false){
				authenticationMessages("acctDoesNotExist",response);
			}*/
			
			
			
		}
		
		
		
		function redirectUserBasedOnAccountStatus(response){
			
			
			
			switch (response.data.account_status_id) {
						case 1:
							Idle.watch();
							setLoginSuccess(response);
							firstTimeUser(response.data.auth_role_id, response);
							break;
						case 2:
							Idle.watch();
							setLoginSuccess(response);
							activeUser(response);
							break;
						case 3:
							authenticationMessages("nonVendorTempLocked",response);
							break;
						case 4:
							accountLocked(response.data.auth_role_id,response.isLoginSuccess,response);
							break;
						case 5:
							if (response.isLoginSuccess){
								//need to take to the password reset page
								authenticationMessages("accountExpired",response);
							}else if (response.message === "Incorrect Password"){
								authenticationMessages("acctDoesNotExist",response);
							}
							break;
						case 6:
							if (response.message === "Account Deactivated"){
								accountDeactivated(response.data.auth_role_id,response);
							}else if (response.isLoginSuccess){
								
								if (response.data.auth_role_id && response.data.auth_role_id === 1){
									authenticationMessages("vendorAcctReset",response);	
								}else if (response.data.auth_role_id && response.data.auth_role_id !== 1){
									authenticationMessages("nonVendorAcctReset",response);
								}
							}
							break;
						case 7:
							if (response.data.auth_role_id && response.data.auth_role_id == 1){
								authenticationMessages("vendorAcctReset",response);	
							}else{
								authenticationMessages("nonVendorAcctReset",response);
							}
							break;
						case 8:
							passwordReset(response);
							break;
						default:
							if (!response.isLoginSuccess){
								authenticationMessages("acctDoesNotExist",response);	
							}
							break;
						}
		}
		function passwordReset(response){
			
			if (response.isLoginSuccess){
				
				authenticationMessages("nonVendorSelfReset",response)
				
			}else if (!response.isLoginSuccess){
				authenticationMessages("acctDoesNotExist",response)
			}
		}
		function activeUser(response){
			
			if (response.message === "Incorrect Password"){
				authenticationMessages("acctDoesNotExist",response)
			}else {
				
				localStorageService.set("userLoggedIn", new Date());
				
				switch (response.data.role) {

				case "7":
				
					if (localStorageService.get("gc_A_no") != null 
							&& localStorageService.get("gc_role") == "GAO ADMIN"){
						
						$location.path("/admin-case-docketsheet/" + base64.urlencode(localStorageService.get("gc_A_no")));
						
					}else{
						
						$location.path("/admin-dashboard/unassigned").replace();
					}
					
					break;

				default:
					
					if (localStorageService.get("caseDocketa_No") != null){
						
						$location.path("/case-docketsheet/" + base64.urlencode(localStorageService.get("caseDocketa_No")));
					}else{
						
						$location.path("/dashboard").replace();
					}
					
				break;
				}
			}
		}
		
		
		function firstTimeUser(roleId,response){
			
			
			switch (roleId) {
			
			case 1:
				authenticationMessages("firstTimeVendor",response)
				break;

			default:
				authenticationMessages("firstTimeNonVendor",response)
				break;
			}
		}
		function accountLocked(roleId,isLoginSuccess,response){
			
			switch (roleId) {
			
			case 1:
				if (response.isLoginSuccess){
					//need to take to the password reset page
					authenticationMessages("vendorAcctReset",response)
				}else if (!response.isLoginSuccess){
					authenticationMessages("vendorLockedMessage",response)
				}
				
				break;

			default:
				authenticationMessages("nonVendorLockedMessage",response)
				break;
			}
		}
		
		function accountDeactivated(roleId,response){
					
					switch (roleId) {
					
					case 1:
						authenticationMessages("vendorAcctDeactiveMessage",response)
						
						break;
		
					default:
						authenticationMessages("nonVendorAcctDeactiveMessage",response)
						break;
					}
				}
		
		function exceptionalLoginCondition(){
			
			if (data.passwordExpired){
				
			}
		}
		
		function  modalService(modalOptions){
			
			$uibModal.open({
				templateUrl : modalOptions.templateUrl,
				controller : modalOptions.controller,
				animation : true,
				size : 'md',
				resolve : {
					data : function() {
						return modalOptions.inputs.data
					}
				},
				keyboard :false,
				backdrop: 'static'
			}).result.catch(angular.noop);
		}
		
		
		function authenticationMessages(feedBackMessageType,response){
			var obj = {};
			
			authFeedbackMessagesSvc.getFeedbackMessages(feedBackMessageType).then(function(feedbackMessageAttr){
				obj.data = feedbackMessageAttr.data;
				
				actionMessageSvc.showModal(obj.data).then(function(result){
					if (result.cancelBtnClicked === "Y"){
						result = result.cancelBtnActionType;
					}else{
						result = result.actionType;	
					}
					redirectUserAfterUserHasReadMessage(result,response)
				})
			})
			
		}
		
		function  redirectUserAfterUserHasReadMessage(result,response){
			
			switch (result) {
			
			case "PSA":
				modalOptions = {
					feedBackMessageType :"",
					templateUrl : "scripts/app/authentication/tempUserConfirmation.html",
					controller : "tempUserCtrl",
					inputs : response
				}
				
				modalService(modalOptions)
				break;
				
			case "PR":
				modalOptions = {
					feedBackMessageType :"",
					templateUrl : "scripts/app/authentication/tempUserConfirmation.html",
					controller : "tempUserCtrl",
					inputs : response
				}
				modalService(modalOptions)
				break;
				
			case "SQR":
				modalOptions = {
					feedBackMessageType :"",
					templateUrl : "scripts/app/authentication/passwordResetSecQues.tpl.html",
					controller : "secQuesCtrl",
					inputs : response
				}
				modalService(modalOptions)
				break;
			
			case "NPR":
				modalOptions = {
					feedBackMessageType :"",
					templateUrl : "scripts/app/authentication/nonVendorPasswordUpdate.html",
					controller : "tempUserCtrl",
					inputs : response
				}
				modalService(modalOptions)
				break;
			case "REG":
				$location.path("/register").replace()
				break;
			default:
				/*$location.path("/").replace()*/
				break;
			}
		}

		function setLoginSuccess(response){
			if (response.isLoginSuccess){
				localStorageService.set("userLoggedIn", new Date());
				if(localStorageService.cookie.isSupported) {
					var secure=false;
					if (location.protocol === 'https:') {
						secure=true;
					}
					localStorageService.cookie.set("userLoggedIn",base64.urlencode(response.isLoginSuccess ? "Y" : "N"), 0, secure);
					localStorageService.cookie.set("id", base64.urlencode(response.data.role), 0, secure);
				}
			}
		}

	}
})();
