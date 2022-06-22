angular.module(
		'epdsApp.adminDashboard').controller('agencyAccountResetCtrl',agencyAccountResetCtrl);

agencyAccountResetCtrl.$inject = ['$scope','$uibModal', '$rootScope','$location']

function agencyAccountResetCtrl($scope,$uibModal,$rootScope,$location){
	// if not GAO admin, return and don't show modal
	if (!!$rootScope.userProfileInfo === false || $rootScope.userProfileInfo.role_id != "7") {
		$location.path("/dashboard").replace();
		return;
	}

	$uibModal.open({
		templateUrl : 'scripts/app/admin/reset-accounts/agencyAccountReset.htm',
		controller : agencyAccountResetCtrlModalCtrl,
		animation : true,
		size : 'md',
		resolve : {
			formInfo : function() {
				return null;
			}
		},
		keyboard :true,
		backdrop :'static'
	}).result.catch(function() {
		// catch rejection (ESC key) and redirect same as if cancel had been clicked
		$location.path("/admin-dashboard/unassigned").replace();
	});
	
}

angular.module(
'epdsApp.adminDashboard').controller('agencyAccountResetCtrlModalCtrl', agencyAccountResetCtrlModalCtrl);
agencyAccountResetCtrlModalCtrl.$inject = [ '$http', '$scope','$rootScope', '$uibModalStack',
		'modalService', '$uibModal', '$uibModalInstance', '$location',
		'accountResetService','formInfo','Idle','regEx','toolTip','agencyDropDownService']

/* @ngInject */
function agencyAccountResetCtrlModalCtrl($http, $scope, $rootScope,$uibModalStack, modalService,
		$uibModal, $uibModalInstance, $location, accountResetService,formInfo,Idle,regEx,toolTip, agencyDropDownService) {
	agencyDropDownService.getListOfTier1Agencies().then(
			function(data) {
				
				
				$scope.tier1SelectedOption = data.tier1SelectedOption;
				$scope.tier1AgencyList = data.tier1AgencyList;
			});

	var dto = $scope.userInfo = formInfo;
	
	$scope.agencyRoles = [{
		label : "AGENCY POC",
		id :5
			
	},{
		label : "AGENCY REP",
		id :6
			
	}]
	
	
	$scope.reset = function(form) {
		// var form = form;
		// var email = form.email;
		
		if (!agencyDropDownService.validateAgencyInfo($scope.tier1SelectedOption,$scope.tier2SelectedOption)){
			return;
		}
		
		var dto = {}
		accountResetService.getUserInfo(form.email).then(function(response){
			$scope.userInfo = response.data;
			var dto  = response.data || {}
			
			dto.tier1_agency_id = $scope.tier1SelectedOption && $scope.tier1SelectedOption.agency_Id || 0;
			dto.tier2_agency_id = $scope.tier2SelectedOption && $scope.tier2SelectedOption.agency_Id || 0;
			dto.typeOfUpdate = (form.selectRole == "Y" ? "roleUpdate" : "N");
			dto.epds_role_id = form.selectedAgencyRole && form.selectedAgencyRole.id;
			dto.email = form.email;
			
			$uibModal.open({
				templateUrl : 'scripts/app/admin/reset-accounts/info-confirmation.tpl.htm',
				controller : agencyAccountResetCtrlModalCtrl,
				animation : true,
				size : 'md',
				resolve : {
					formInfo : function() {
						return dto;
					}
				},
				keyboard :false,
				backdrop :true
			}).result.catch(angular.noop);
		})
		
	}
	
	$scope.ok = function(email) {
		$uibModalStack.dismissAll();
		$location.path("/admin-dashboard/unassigned").replace();
	}
	
	
	$scope.confirm = function(userId) {
		
		accountResetService.resetAgencyAccount(dto).then(function(response){

			$uibModalInstance.dismiss('cancel');
			$uibModal.open({
				templateUrl : 'scripts/app/admin/reset-accounts/feedbackMessages.tpl.htm',
				controller : agencyAccountResetCtrlModalCtrl,
				animation : true,
				size : 'md',
				resolve : {
					formInfo : function() {
						return null;
					}
				},
				keyboard :false,
				backdrop :true
			}).result.catch(angular.noop);
		
		})
	}
	
	
	$scope.cancel = function(val) {
		$uibModalInstance.dismiss('cancel');
		if (val !== 'confirmInfo') {
			$location.path("/admin-dashboard/unassigned").replace();
		}
	}
	
}



