'use strict';

angular.module(
		'epdsApp.dashboard')

.controller('userProfileInfoController', UserProfileCtrl);

UserProfileCtrl.$inject = ['$scope','userProfileViewSvc','userInfoService','actionMessageSvc','$route','regEx','toolTip','agencyDropDownService','navigationSvc', '$timeout']

function UserProfileCtrl($scope,userProfileViewSvc,userInfoService,actionMessageSvc,$route,regEx,toolTip,agencyDropDownService,navigationSvc,$timeout) {
	$scope.regex = regEx;

	userProfileViewSvc.loadUserProfileView().then(function(response){
		$timeout(function() {
			$('#focus_start').focus();
		}, 0);

		navigationSvc.setListOfRoutesBasedOnRole({navigationType : "profile"});

		$scope.user_Role = response.user_Role;
		$scope.userProfileInfo = response.user_Info;
		$scope.role = response.user_Info.role_id;
		$scope.representativeAddressDetails = '';
		$scope.showUpdateUserForm = false;
		$scope.edit = function() {
			$scope.showUpdateUserForm = true;
		}
		$scope.disableEmail = true;
		
		$scope.form = {
			prefix : $scope.userProfileInfo.prefix,
			first_Name : $scope.userProfileInfo.first_Name,
			middle_initial : $scope.userProfileInfo.middle_initial,
			last_Name : $scope.userProfileInfo.last_Name,
			suffix : $scope.userProfileInfo.suffix,
			email : $scope.userProfileInfo.email,
			firm_Id : $scope.userProfileInfo.firm_id,
			firm_Name : $scope.userProfileInfo.firm_Name,
			phone_No : $scope.userProfileInfo.phone_No,
			fax_No : $scope.userProfileInfo.fax_No,
			address1 : $scope.userProfileInfo.address1,
			address2 : $scope.userProfileInfo.address2,
			zip_Code : $scope.userProfileInfo.zip_Code,
			city : $scope.userProfileInfo.city,
			state : $scope.userProfileInfo.state,
			country : $scope.userProfileInfo.country,
		}

		$scope
				.$watch(
						'representativeAddressDetails',
						function() {

							if (typeof $scope.representativeAddressDetails.address_components != 'undefined') {

								$scope.representativeAddressDetails = retrieveAddressDetailsFromUserSelection(
										$scope,
										$scope.representativeAddressDetails);
										$scope.form.address1 = $scope.representativeAddressDetails.streetAddr,
										$scope.form.zip_Code = $scope.representativeAddressDetails.zipcode
										$scope.form.country = $scope.representativeAddressDetails.country,
										$scope.form.state = $scope.representativeAddressDetails.state,
										$scope.form.city = $scope.representativeAddressDetails.city
							}
						});
		
		$scope.tier2SelectedOption = {};
		agencyDropDownService.getListOfTier1Agencies().then(
				function(data) {
					$scope.tier1SelectedOption = data.tier1SelectedOption;
					$scope.tier1AgencyList = data.tier1AgencyList;
				});

	})
	

		$scope.cancel = function() {
			$scope.showUpdateUserForm = false;
			$scope.form = {
				prefix : 	$scope.userProfileInfo.prefix,
				first_Name : $scope.userProfileInfo.first_Name,
				middle_initial : $scope.userProfileInfo.middle_initial,
				last_Name : $scope.userProfileInfo.last_Name,
				email : $scope.userProfileInfo.email,
				firm_Name : $scope.userProfileInfo.firm_Name,
				phone_No : $scope.userProfileInfo.phone_No,
				fax_No : $scope.userProfileInfo.fax_No,
				address1 : $scope.userProfileInfo.address1,
				address2 : $scope.userProfileInfo.address2,
				zip_Code : $scope.userProfileInfo.zip_Code,
				city : $scope.userProfileInfo.city,
				state : $scope.userProfileInfo.state,
				country : $scope.userProfileInfo.country,
				firm_Id : $scope.userProfileInfo.firm_id,
			}
			
		}
	$scope.registerUserInfo = function(form) {
		

		if($scope.role == 6 && $scope.updateAgencyName && $scope.showUpdateUserForm
			&& !agencyDropDownService.validateAgencyInfo($scope.tier1SelectedOption,$scope.tier2SelectedOption)){
			return;
		}
		
		var  form = {
				prefix : $scope.form.prefix,
				lastName : $scope.form.last_Name,
				firstName : $scope.form.first_Name,
				middle_initial : $scope.form.middle_initial,
				suffix : $scope.form.suffix,
				email : $scope.form.email,
				phoneNo : $scope.form.phone_No ? $scope.phonenumberCountryCode + $scope.form.phone_No : $scope.form.phone_No,
				faxNo : $scope.form.fax_No ? $scope.faxnumberCountryCode + $scope.form.fax_No : $scope.form.fax_No,
				epds_role_id : $scope.role,
				address1 : $scope.form.address1,
				address2 : $scope.form.address2,
				city : $scope.form.city,
				state : $scope.form.state,
				country : $scope.form.country,
				zipCode : $scope.form.zip_Code,
				nameOfFirm : $scope.form.firm_Name,
				isGAO_User : "N",
				firm_id : $scope.userProfileInfo.firm_Id,
				}
		
		if ($scope.role == "6" && $scope.updateAgencyName && $scope.showUpdateUserForm){
			form.tier1_agency_id = $scope.tier1SelectedOption && $scope.tier1SelectedOption.agency_Id;
			form.tier2_agency_id = $scope.tier2SelectedOption && $scope.tier2SelectedOption.agency_Id;
		}
		
		userProfileViewSvc.updateUserInfo(form).then(function(data){
			
			if (data.success){
				
				var customAttr = {
						headerText : "Success"	,
						bodyText : "You have successfully updated your profile.",
						modalType : "success",
						actionType : "",
					    cancelBtnReq : "N",
					    cancelBtnActionType : ""
					}
				
				actionMessageSvc.showModal(customAttr).then(function(){
					$route.reload();
				});
			}
				 
		})
	}
	
	$scope.changePassword = function(){
		
		userProfileViewSvc.changePasswordModal($scope.userProfileInfo);
	}
	

	$scope.changeEmailAddress = function(){
		
		userProfileViewSvc.changeEmailAddressModal($scope.userProfileInfo);
	}
	
	$scope.changeSecurityQuestions = function (){
		userProfileViewSvc.changeSecQuestionsModal($scope.userProfileInfo);
	}


}


changePasswordModalCtrl.$inject = ['$scope', '$rootScope', '$uibModalInstance',
                              		'userInfo','localStorageService','userProfileViewSvc',
                              		'actionMessageSvc','userInfoService','authenticationService'];

 function changePasswordModalCtrl ($scope, $rootScope, $uibModalInstance,
		 userInfo,localStorageService,userProfileViewSvc,actionMessageSvc,userInfoService,authenticationService) {
	 
	 localStorageService.set("userId",userInfo.user_Id)
	 $scope.customFormOptions = {
			validationsTemplate : 'scripts/app/account-update/register-form-validations.html',
			preventInvalidSubmit : true,
			preventDoubleSubmit : true,
			setFormDirtyOnSubmit : true,
			scrollToAndFocusFirstErrorOnSubmit : true,
			scrollAnimationTime : 900,
			scrollOffset : -100,
	};
	 $scope.listOfErrorMessages = null;
	 
	 
	 
 	$scope.changePassword = function(form) {
		 $uibModalInstance.close();
		 var credentials  = {};
		 
		 credentials.userPwd = form.password;
		 
		 var newPwd = authenticationService.encodePassword(credentials);
		 
		 credentials.userPwd = form.oldPassword;
		 
		 var oldPwd =  authenticationService.encodePassword(credentials);
		 
		 var form  = {
			    user_id : userInfo.user_Id,
				prefix : userInfo.prefix,
				lastName : userInfo.last_Name,
				firstName : userInfo.first_Name,
				middle_initial : userInfo.middle_initial,
				suffix : userInfo.suffix,
				email : userInfo.email,
				phoneNo : userInfo.phone_No,
				faxNo : userInfo.fax_No,
				epds_role_id : userInfo.role_id,
				address1 : userInfo.address1,
				address2 : userInfo.address2,
				city : userInfo.city,
				state : userInfo.state,
				country : userInfo.country,
				zipCode : userInfo.zip_Code,
				nameOfFirm : userInfo.firm_Name,
				newPassword : String(newPwd),
				oldPassword : String(oldPwd)
		 }
		 userProfileViewSvc.changePassword(form).then(function(){
			
			 var customAttr = {
						headerText : "Success"	,
						bodyText : "You have successfully updated your password.",
						modalType : "success",
						actionType : "",
					    cancelBtnReq : "N",
					    cancelBtnActionType : ""
					}
				
				actionMessageSvc.showModal(customAttr);
			 
			 localStorageService.remove("userId");
		 })
 		$uibModalInstance.close();
 	};

 	$scope.cancel = function(htmlContent) {
 		$uibModalInstance.close();
 	};

 }
 
 
 changeEmailAddressModalCtrl.$inject = ['$scope', '$rootScope', '$uibModalInstance',
                              		'userInfo','localStorageService','userProfileViewSvc',
                              		'actionMessageSvc','userInfoService','authenticationService','$route'];

 function changeEmailAddressModalCtrl ($scope, $rootScope, $uibModalInstance,
		 userInfo,localStorageService,userProfileViewSvc,actionMessageSvc,userInfoService,authenticationService,$route) {
	 
	 localStorageService.set("userId",userInfo.user_Id)
	 $scope.customFormOptions = {
			validationsTemplate : 'scripts/app/account-update/register-form-validations.html',
			preventInvalidSubmit : true,
			preventDoubleSubmit : true,
			setFormDirtyOnSubmit : true,
			scrollToAndFocusFirstErrorOnSubmit : true,
			scrollAnimationTime : 900,
			scrollOffset : -100,
	};
	 $scope.listOfErrorMessages = null;
	 
	 
 	$scope.changeEmail = function(form) {
		 $uibModalInstance.close();
		 var credentials  = {};
		 credentials.userPwd = form.oldPassword;
		 
		 var oldPwd =  authenticationService.encodePassword(credentials);
		 
		 var form  = {
				email : form.email,
				old_email : userInfo.email,
				oldPassword : String(oldPwd)
		 }
		 
		 
		 userProfileViewSvc.changeEmailAddress(form).then(function(response){
			
			 localStorageService.remove("userId");
			 
			 if (response.isSuccess){
				
				 var customAttr = {
							headerText : "Success"	,
							bodyText : "You have successfully updated your user name.",
							modalType : "success",
							actionType : "",
						    cancelBtnReq : "N",
						    cancelBtnActionType : ""
						}
					
					actionMessageSvc.showModal(customAttr).then(function(){
						$route.reload();
					});
				 
			 }else{
				 
				 
				 var bodyText = "There was some internal error processing your request. "
						+ "Please try again after some time. If the issue persists please contact admin at cbca.it@gsa.gov. ";
				
				var customAttr = {
							headerText : "Error",
							bodyText : bodyText,
							modalType : "error",
							actionType : "",
						    cancelBtnReq : "N",
						    cancelBtnActionType : ""
						}
					
					actionMessageSvc.showModal(customAttr);
			 }
			 
		 })
 		$uibModalInstance.close();
 	};

 	$scope.cancel = function(htmlContent) {
 		$uibModalInstance.close();
 	};

 }
 
 
 changeSecQuesModalCtrl.$inject = ['$scope', '$rootScope', '$uibModalInstance',
                              		'userInfo','localStorageService','userProfileViewSvc','actionMessageSvc','userInfoService','authFeedbackMessagesSvc','authenticationService'];

 function changeSecQuesModalCtrl ($scope, $rootScope, $uibModalInstance,
		 userInfo,localStorageService,userProfileViewSvc,actionMessageSvc,userInfoService,authFeedbackMessagesSvc,authenticationService ) {
	 
	 userProfileViewSvc.getListOfSecurityQuestions().then(function(response){
		 
		var selectedOption = {
				 security_q_id : 0,
				 security_question : "Please Select Security Question. "
		 } 
		
		 response.list.push(selectedOption);
		$scope.listOfSecurityQuestions = response.list
		$scope.selectedOption1 = selectedOption
		$scope.selectedOption2 = selectedOption
		$scope.selectedOption3 = selectedOption
	 })
	 
	 $scope.securityQuestionHelp = function(){
			var obj = {};
			authFeedbackMessagesSvc.getFeedbackMessages("securityQuestionHelp").then(function(response){
				obj.data = response.data;
				actionMessageSvc.showModal(obj.data);
			})
			
		
		}
		
		
		$scope.onQuestion1Selected  = function(selectoption1,selectoption2,selectoption3){
			
			if(selectoption1.security_q_id === selectoption2.security_q_id){
				$scope.selectedOption2 = $scope.listOfSecurityQuestions[$scope.listOfSecurityQuestions.length -1]
			}
		
			if(selectoption1.security_q_id === selectoption3.security_q_id){
				$scope.selectedOption3 = $scope.listOfSecurityQuestions[$scope.listOfSecurityQuestions.length -1]
			}
			
		}
		
		
		$scope.onQuestion2Selected = function(selectoption1,selectoption2,selectoption3){
			
			
			if(selectoption2.security_q_id === selectoption1.security_q_id){
				$scope.selectedOption1 = $scope.listOfSecurityQuestions[$scope.listOfSecurityQuestions.length -1]
			}
		
			if(selectoption2.security_q_id === selectoption3.security_q_id){
				$scope.selectedOption3 = $scope.listOfSecurityQuestions[$scope.listOfSecurityQuestions.length -1]
			}
			
		}
		
		$scope.onQuestion3Selected = function(selectoption1,selectoption2,selectoption3){
				
				if(selectoption3.security_q_id === selectoption2.security_q_id){
					$scope.selectedOption2 = $scope.listOfSecurityQuestions[$scope.listOfSecurityQuestions.length -1]
				}
			
				if(selectoption3.security_q_id === selectoption1.security_q_id){
					$scope.selectedOption1 = $scope.listOfSecurityQuestions[$scope.listOfSecurityQuestions.length -1]
				}
				
			}
				
 	$scope.changeSecQuestions = function(form, selectoption1, selectoption2,
					selectoption3) {
 		
 		
		 		if (selectoption1.security_q_id == "0" 
		 			|| selectoption2.security_q_id == "0" 
		 				|| selectoption3.security_q_id == "0"){
		 			
		 			var customAttr = {
							headerText : "Error"	,
							bodyText : "Please make sure you have selected a security question. ",
							modalType : "error",
							actionType : "",
						    cancelBtnReq : "N",
						    cancelBtnActionType : ""
						}
					
					actionMessageSvc.showModal(customAttr);
		 			
		 			return;
		 			
		 		}
 		
				form.seqQue1Id = selectoption1.security_q_id
				form.seqQue2Id = selectoption2.security_q_id
				form.seqQue3Id = selectoption3.security_q_id
				
				 $uibModalInstance.close();
				 var form = {
							user_id : userInfo.user_id,
							answer1: form.answer1,
							answer2: form.answer2,
							answer3: form.answer3,
							seqQue1Id: form.seqQue1Id,
							seqQue2Id:form.seqQue2Id,
							seqQue3Id: form.seqQue3Id,	
					}
				 
				 userProfileViewSvc.changeSecurityQuestions(form).then(function(response){
					
					 
					 if (response && !response.status){
					
						 var customAttr = {
									headerText : "Success"	,
									bodyText : "You have successfully updated your security questions.",
									modalType : "success",
									actionType : "",
								    cancelBtnReq : "N",
								    cancelBtnActionType : ""
								}
							
							actionMessageSvc.showModal(customAttr);
					 }
					 
					 
				 })
 		};

 	$scope.cancel = function(htmlContent) {
 		$uibModalInstance.close();
 	};

 }

