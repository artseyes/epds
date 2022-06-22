epdsApp.controller('resetPasswordCtrl',
		resetPasswordCtrl);

resetPasswordCtrl.$inject = ['$scope','$uibModal']

function resetPasswordCtrl($scope,$uibModal){
	
	$uibModal.open({
		templateUrl : 'scripts/app/authentication/resetPassword.tpl.html',
		controller : resetPasswordCtrlModalCtrl,
		animation : true,
		size : 'sm',
		keyboard :false,
		backdrop :false
	}).result.catch(angular.noop);
	
}


epdsApp.controller('resetPasswordCtrlModalCtrl', resetPasswordCtrlModalCtrl);
resetPasswordCtrlModalCtrl.$inject = [ '$http', '$scope', '$uibModalStack',
		'modalService', '$uibModal', '$uibModalInstance', '$location',
		'resetPasswordService', 'authFeedbackMessagesSvc' ,'actionMessageSvc','regEx','toolTip']

function resetPasswordCtrlModalCtrl($http, $scope, $uibModalStack, modalService,
		$uibModal, $uibModalInstance, $location, resetPasswordService,
		authFeedbackMessagesSvc,actionMessageSvc,regEx,toolTip) {
	
	$scope.regEx = regEx;
	$scope.toolTip = toolTip;
	
	$scope.OK = function(email) {
		
		
		resetPasswordService.checkIfThisEmailIsValid(email).then(
				function(response) {
					 
					 if (response.data != null){
						 response.data.role_id = "8"
					 }
					 
					if (response.message === "Email doesn't exist"){
						
						var customModalOptions = {
								headerText : 'Error',
								bodyText : 'A user account for the provided email does not exist.  Please check if the email is correct.',
								closeButtonText : 'OK',
								messageType : "error"
							};

							modalService.showModal({}, customModalOptions).then(
									function(result) {

									});
					}else if (response.message === "ROLE: VENDOR"){

							var obj = {};
							
							authFeedbackMessagesSvc.getFeedbackMessages("passwordReset").then(function(response){
							obj.data = response.data;
							
							actionMessageSvc.showModal(obj.data).then(function(result){
								$location.path("/").replace();
							})
						})
					
					
					}else if (response.message === "ROLE: NON-VENDOR"){
						
						if (response.data && response.data.account_status_id !== 4){
						
							$uibModal.open({
								templateUrl : 'scripts/app/authentication/passwordResetSecQues.tpl.html',
								controller : secQuesCtrl,
								animation : true,
								size : 'md',
								resolve : {
										data : function() {
											return response.data
										}
									},
								keyboard : false,
								backdrop : true
							}).result.catch(angular.noop);
						}else if (response.isNonVendorNewAcct){
							
							var obj = {};
							
							authFeedbackMessagesSvc.getFeedbackMessages("nonVendorSecurityQuesNotSet").then(function(response){
									obj.data = response.data;
									actionMessageSvc.showModal(obj.data).then(function(result){
										$location.path("/").replace();
									})
							})
						}else{
						
							var obj = {};
							
							authFeedbackMessagesSvc.getFeedbackMessages("nonVendorLockedMessage").then(function(response){
									obj.data = response.data;
									actionMessageSvc.showModal(obj.data).then(function(result){
										$location.path("/").replace();
									})
							})
						}
						
					}else {

						var obj = {};
						
						authFeedbackMessagesSvc.getFeedbackMessages("serverError").then(function(response){
								obj.data = response.data;
								actionMessageSvc.showModal(obj.data).then(function(result){
									$location.path("/").replace();
								})
						})
				
				
				
						
					}
					
					
					
				});
	
	}
	
	$scope.cancel = function() {
		$uibModalInstance.dismiss('cancel');
		$location.path("/login").replace();
	}
	
}



(function() {
	'use strict';

	var serviceId = 'resetPasswordService';

	angular.module('epdsApp.core').factory(serviceId,
			[ '$http', '$rootScope', '$uibModal','$httpParamSerializerJQLike','base64', resetPasswordService ]);

	/* @ngInject */
	function resetPasswordService($http,$rootScope,$uibModal,$httpParamSerializerJQLike,base64) {

		var service = {
			checkIfThisEmailIsValid : checkIfThisEmailIsValid,
			checkIfthisAnswerIsCorrect : checkIfthisAnswerIsCorrect
		};

		return service;

		function checkIfThisEmailIsValid(email) {

			var form = {
					email : base64.urlencode(email)
			} 
			
			
			return $http({
				url : '/epds/user/forgot-password/submit-email',
				method : 'POST',
				headers : {
					'Content-Type' : 'application/x-www-form-urlencoded'
				},
				data: $httpParamSerializerJQLike(_.omitBy(form, _.isNil)),
			}).then(
					function(data) {
						
						return	data.data;

					},
					function(error) {
						
						return error;
					});

		}

		function checkIfthisAnswerIsCorrect(form) {
			
			return $http(
					{
						url : '/epds/user/forgot-password/check-security-answer/',
						method : 'POST',
						headers : {
							'Content-Type' : 'application/x-www-form-urlencoded'
						},
						data: $httpParamSerializerJQLike(_.omitBy(form, _.isNil)),
					})
					.then(
							function(data) {
								return data.data;
							},
							function(error) {
								
								return error;
							});
		}
		
		function transformRequest(obj){

			var str = [];
			for ( var key in obj) {
				if (obj[key] instanceof Array) {
					for ( var idx in obj[key]) {
						var subObj = obj[key][idx];
						for ( var subKey in subObj) {
							str
									.push(encodeURIComponent(key)
											+ "["
											+ idx
											+ "]["
											+ encodeURIComponent(subKey)
											+ "]="
											+ encodeURIComponent(subObj[subKey]));
						}
					}
				} else {
					str.push(encodeURIComponent(key) + "="
							+ encodeURIComponent(obj[key]));
				}
			}
			return str.join("&");
		
		
		}

	}
})();
