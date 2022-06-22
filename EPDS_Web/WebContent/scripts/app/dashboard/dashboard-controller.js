angular.module(
		'epdsApp.dashboard')

.controller('DashboardCtrl', DashboardCtrl);

DashboardCtrl.$inject = [ '$scope', '$location', '$http', '$filter',
		'DTOptionsBuilder', 'DTColumnDefBuilder', '$resource', '$timeout',
		'$route', '$rootScope', '$routeParams', '$uibModal', 'modalService',
		'$window', 'localStorageService', 'dashboardDataService',
		'dashboardSettingService', 'primaryOrSecondaryRepsRequestDataService','DashboardData','userProfileViewSvc',
		'actionMessageSvc','localStorageService','$injector','$ocLazyLoad','Idle']

function DashboardCtrl($scope, $location, $http, $filter, DTOptionsBuilder,
		DTColumnDefBuilder, $resource, $timeout, $route, $rootScope,
		$routeParams, $uibModal, modalService, $window, localStorageService,
		dashboardDataService, dashboardSettingService,
		primaryOrSecondaryRepsRequestDataService,DashboardData,userProfileViewSvc,actionMessageSvc,localStorageService, $injector, $ocLazyLoad,Idle) {

	if (!!DashboardData === false) {
		return;
	}

	$timeout(function() {
		$('#focus_start').focus();
	}, 0);

	var vm = this;
	
	$scope.popup = {
			  options: {
			    title: null,
			    placement: 'top', 
			    delay: { show: 800, hide: 100 }
			  }
			}; 
	
	
	$scope.toggleEmailPreferences = function(yOrN){
		dashboardDataService.toggleGlobalEmailPreferences(yOrN).then(function(response){
			if (response && response.userInfo){
				$rootScope.userProfileInfo = response.userInfo;
			}
		})
		
	}

	vm = angular.extend(vm,DashboardData);
	vm.viewType = null;
	$scope.hideOtherOptions = "S";


	vm.group_No = DashboardData.group_No;
	vm.protest_Info_List = DashboardData.protest_Info_List;
	vm.role = $scope.role = DashboardData.role;
	localStorageService.set("role",vm.role);
	vm.hideOtherOptions = DashboardData.hideOtherOptions;

	if (DashboardData.userProfileInfo.role_id == "7") {
		$location.path("admin-dashboard/unassigned").replace();
	} else {
		$scope.path = "case-docketsheet"
		dashboardUtils($scope, $filter, $routeParams, dashboardDataService,
				dashboardSettingService, $location, DashboardData, $timeout,
				userProfileViewSvc, actionMessageSvc, localStorageService, Idle, vm, $http);
		primaryOrSecondaryRepsRequestDataService.getListOfSecondaryRepInvitations()
		.then(
				function(data) {
				
					if (data.invitedUserList && data.invitedUserList.length > 0) {

						var modalInstance =	$uibModal.open({
									templateUrl : 'views/dialogue-box-html-templates/secondary-rep-invites-dialogue-template.html?bust=' + Math.random().toString(36).slice(2),
									controller : 'RespondToPrimaryOrSecondaryRepsRequestCtrl',
									scope : $scope,
									resolve : {
										items : function() {
											return data.invitedUserList;
										},
										protestInformation : function() {
											return null;
										},
									},
									size : 'lg',
									backdrop : 'static'
								})
						modalInstance.result.then(function(){
							$route.reload();
						}).catch(angular.noop);
					}else{
						$scope.showPasswordExpiryWarning();
					}

				});
		$scope.setDashboardSettings()
		
	}

	
	$scope.fileNewProtest = function() {
		$rootScope.$broadcast('fileNewProtestEvent');
	}
	

}


