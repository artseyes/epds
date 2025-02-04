angular.module('epdsApp.adminDashboard').controller(
		'removeCaseCtrlModalCtrl', removeCaseCtrlModalCtrl);
removeCaseCtrlModalCtrl.$inject = [ '$scope', '$rootScope',
		'$uibModalStack', 'actionMessageSvc', '$uibModal', '$uibModalInstance',
		'$location', 'removeCaseService', 'userInfo', 'Idle', 'regEx',
		'toolTip' ]


function removeCaseCtrlModalCtrl($scope, $rootScope, $uibModalStack,
		actionMessageSvc, $uibModal, $uibModalInstance, $location,
		removeCaseService, userInfo, Idle, regEx, toolTip) {

	
	/* Idle.watch(); */
	$scope.userInfo = userInfo;

	if (!!$rootScope.userProfileInfo === false) {
		return;
	}

	if ($rootScope.userProfileInfo.role_id != "7") {
		$location.path("/dashboard").replace();
	}
	
	
	$scope.doRemoveCase = function(form) {

		if (!form.reasonForDeletion){
			
			var bodyText="";
			bodyText += "Please enter comments";

			
			var customAttr = {
					headerText : "",
					bodyText : bodyText,
					modalType : "error",
					actionType : "samepage",
					cancelBtnReq : "N",
					okBtnText : "OK",
				}

				actionMessageSvc
						.showModal(customAttr);
			
			return;
		}
		
		
		removeCaseService
				.getProtestInfoByANum(form.aNum)
				.then(
						function(response) {

							if (response.protestInfo){
								$scope.protestInfo = response.protestInfo;

								var bodyText="";
								bodyText += "<p>You are about to delete the case  ";
								bodyText += "		<strong>" + $scope.protestInfo.a_No +"<\/strong>";
								bodyText += "		- ";
                bodyText += "		<strong>" + ($scope.protestInfo.b_No || "CBCA#") +"<\/strong>";
                bodyText += "		- ";
                bodyText += "		<strong>" + $scope.protestInfo.company_status +"<\/strong>";
                bodyText += "		- ";
                bodyText += "		<strong>" + $scope.protestInfo.company_Name + "<\/strong>&nbsp;.";
								bodyText += "		&nbsp;&nbsp;";
								bodyText += "		<\/p>";
                bodyText += "   <p>Please note that 'A-XXX' and 'A-XXX.1' are not the same. Be sure to enter the exact ANum.</p>";
								bodyText += "		<p>Selecting OK will permanently delete this case and its filings from EDS. Do you want to proceed?<\/p>";

								var customAttr = {
										headerText : "Warning",
										bodyText : bodyText,
										modalType : "warning",
										actionType : "samepage",
										cancelBtnReq : "Y",
										cancelBtnActionType : "samepage",
										okAndCancelText : "Y",
										okBtnText : "OK",
										cancelBtnText : "Cancel"
									}

									actionMessageSvc
											.showModal(customAttr)
											.then(function(result) {
												
														if (result.cancelBtnClicked != "Y") {
															removeCaseService.deleteCase(form).then(function(){
																$uibModalStack.dismissAll();
																$location.path("/admin-dashboard/unassigned");
															});
														}else{
															$uibModalStack.dismissAll();
														}
													})
							}else{
								
								var bodyText="";
								bodyText += "A# doesn't exist";

								
								var customAttr = {
										headerText : "Warning",
										bodyText : bodyText,
										modalType : "warning",
										actionType : "samepage",
										cancelBtnReq : "N",
										okBtnText : "OK",
									}

									actionMessageSvc
											.showModal(customAttr);
							}
							
						})

	}

	$scope.ok = function(email) {
		$uibModalStack.dismissAll();
	}

	$scope.cancel = function(val) {
		$uibModalInstance.dismiss('cancel');
	}

}

(function() {
	'use strict';

	var serviceId = 'removeCaseService';
	
	angular.module('epdsApp.adminDashboard')
			.factory(
					serviceId,
					[ '$http', 'base64', '$uibModal', '$rootScope','$httpParamSerializerJQLike',
							accountResetService ]);
	
	function accountResetService($http, base64, $uibModal, $rootScope,$httpParamSerializerJQLike) {

		var service = {
			deleteCase : deleteCase,
			getProtestInfoByANum : getProtestInfoByANum
		};

		return service;

		function deleteCase(params) {
			return $http({
				url : '/epds/remove-case',
				method : 'POST',
				headers : {
					'Content-Type' : 'application/x-www-form-urlencoded'
				},
				data : $httpParamSerializerJQLike(_.omitBy(params, _.isNil)),
			}).then(function(data) {

				return data.data;

			}, function(error) {

				return error;
			});
		}
		
		
		function getProtestInfoByANum(aNum) {
			return $http({
				url : '/epds/protestInfo/' + aNum,
				method : 'GET',
				headers : {
					'Content-Type' : 'application/json',
					'skipInterceptor' : true  // skip the 500 error if A# doesn't exist
				},
			}).then(function(data) {

				return data.data;

			}, function(error) {

				return error;
			});
		}

	}
})();
