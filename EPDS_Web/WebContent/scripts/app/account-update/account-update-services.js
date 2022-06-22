(function() {
	'use strict';

	var serviceId = 'userProfileViewSvc';

	angular.module('epdsApp.dashboard').factory(serviceId,
			[ '$http', '$location', '$window', '$rootScope',
			  'localStorageService',
			  'actionMessageSvc','$httpParamSerializerJQLike',
			  '$q','userInfoService','$uibModal','$ocLazyLoad','authenticationService','$injector', userProfileViewSvc ]);
	
	/* @ngInject */
	function userProfileViewSvc($http, $location, $window, $rootScope,
			localStorageService,actionMessageSvc,$httpParamSerializerJQLike,
			$q,userInfoService,$uibModal,$ocLazyLoad,authenticationService,$injector) {

		var service = {
			loadUserProfileView : loadUserProfileView,
			registerUserInfo : registerUserInfo,
			changePasswordModal : changePasswordModal,
			changeEmailAddressModal :changeEmailAddressModal,
			changeEmailAddress :changeEmailAddress,
			updateUserInfo : updateUserInfo,
			getListOfSecurityQuestions : getListOfSecurityQuestions,
			changePassword :changePassword,
			changeSecQuestionsModal : changeSecQuestionsModal,
			changeSecurityQuestions : changeSecurityQuestions
		};

		return service;

		
		function loadUserProfileView() {
			
			return $http({
						url : '/epds/user-profile-view',
						method : 'GET',
						headers : {
							'Content-Type' : 'application/json'
						},
					}).then(
							function(data) {
								
								$rootScope.authenticated = true;
								userInfoService
										.setUserInfo(data.data.user_Info);
								
								return data.data;

							
							},
							function(error) {
							
								return error;
							});

		}
		
     function registerUserInfo(params) {
			
			return $http({
						url : '/epds/user-profile-view',
						method : 'POST',
						headers : {
							'Content-Type' : 'application/x-www-form-urlencoded'
						},
						data: $httpParamSerializerJQLike(_.omitBy(params, _.isNil)),
					}).then(
							function(data) {
								$location.path("/profile");
								return data.data;
							},
							function(error) {
								
								return error;
							});

		}
     
     function changePassword(form) {
			
			return $http({
						url : '/epds/change-password',
						method : 'POST',
						headers : {
							'Content-Type' : 'application/x-www-form-urlencoded'
						},
						data: $httpParamSerializerJQLike(_.omitBy(form, _.isNil)),
					}).then(
							function(data) {
								/*$location.path("/profile");*/
								return data.data;
							},
							function(error) {
								
								return error;
							});

		}
     
     function changeEmailAddress(form) {
			
			return $http({
						url : '/epds/update-username',
						method : 'POST',
						headers : {
							'Content-Type' : 'application/x-www-form-urlencoded'
						},
						data: $httpParamSerializerJQLike(_.omitBy(form, _.isNil)),
					}).then(
							function(data) {

								return data.data;
							},
							function(error) {
								
								return error;
							});

		}
     
     function changeSecurityQuestions(form) {
			
			return $http({
						url : '/epds/change-sec-ques',
						method : 'POST',
						headers : {
							'Content-Type' : 'application/x-www-form-urlencoded'
						},
						data: $httpParamSerializerJQLike(_.omitBy(form, _.isNil)),
					}).then(
							function(data) {
								$location.path("/profile");
								return data.data;
							},
							function(error) {
								
								return error;
							});

		}
     function changePasswordModal (userInfo){
    	 
    	  $uibModal.open({
 				templateUrl : 'scripts/app/account-update/changePassword.tpl.html',
 				controller : changePasswordModalCtrl,
 				size : 'lg',
 				backdrop : 'static',
 				resolve : {
 					userInfo : function() {
 						return userInfo;
 					},
 				}
 			}).result.catch(angular.noop);
    	  
     }
     
     function changeEmailAddressModal (userInfo){
    	 
   	  $uibModal.open({
				templateUrl : 'scripts/app/account-update/changeEmailAddress.tpl.html',
				controller : changeEmailAddressModalCtrl,
				size : 'lg',
				backdrop : 'static',
				resolve : {
					userInfo : function() {
						return userInfo;
					},
				}
			}).result.catch(angular.noop);
   	  
    }
     
     function changeSecQuestionsModal (userInfo){
    	 
   	  $uibModal.open({
				templateUrl : 'scripts/app/account-update/changeSecurityQues.tpl.htm',
				controller : changeSecQuesModalCtrl,
				size : 'lg',
				backdrop : 'static',
				resolve : {
					userInfo : function() {
						return userInfo;
					},
				}
			}).result.catch(angular.noop);
   	  
    }
     
     function getListOfSecurityQuestions(){
    	 
    	 return $http({
    			url : '/epds/user/get-security-questions',
    			method : 'GET',
    			headers : {
    				'Content-Type' : 'application/json'
    			}
    		}).then(
					function(data) {
						$location.path("/profile");
						return data.data;
					},
					function(error) {
						console
								.log("Error occured when registering user Info "
										+ JSON.stringify(error));
						return error;
					});
     }
     function updateUserInfo(form){
    	 
    	 return $ocLazyLoad.load(['jquery-datatables','angular-datatables','dashboard','account-update',
    	                   'admin-dashboard','angular-xeditable','cds','file-info-view','agency','registeration','parties','manage-gao'],{serie: true, cache :false}).then(function() {
		        
	    	 var manageAttorneyInfoSvc = $injector.get("manageAttorneyInfoSvc");
	        
	    	 return manageAttorneyInfoSvc.updateUserInfo(form).then(function(data){
	    		 
	    		return data; 
	    	 });
	      });
    	 
     }
	}
		
})();



