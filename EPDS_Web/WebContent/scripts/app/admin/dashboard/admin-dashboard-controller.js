'use strict';

angular.module('epdsApp.adminDashboard').controller('AdminDashboardCtrl', AdminDashboardCtrl);

AdminDashboardCtrl.$inject = [ '$scope', '$filter', '$routeParams',
                               'dashboardDataService', 'dashboardSettingService', '$location',
                               'DashboardData', '$timeout', 'userProfileViewSvc', 'actionMessageSvc',
                               'localStorageService', 'Idle', '$http']
/* @ngInject */
function AdminDashboardCtrl($scope, $filter, $routeParams,
		dashboardDataService, dashboardSettingService, $location,
		DashboardData, $timeout, userProfileViewSvc, actionMessageSvc,
		localStorageService, Idle, $http) {

	if (!!DashboardData === false) {
		return;
	}

	$timeout(function() {
		$('#focus_start').focus();
	}, 0);

	var vm = this;

	vm.viewType = $routeParams.viewType
	var title = "EDS: ";
	if(vm.viewType  == 'unassigned') {
		title += " Unassigned";
	}
	if(vm.viewType  == 'assigned') {
		title += " Assigned";
	}
	document.title = title;
	vm = angular.extend(vm,DashboardData);
	
	$scope.clearEmailNotifications = function(yOrN){
		dashboardDataService.toggleGlobalEmailPreferences(yOrN).then(function(response){
			if (response && response.userInfo){
				$scope.userProfileInfo = response.userInfo;
			}
		})
		
	}

	$scope.popup = {
			  options: {
			    title: null,
			    placement: 'right', 
			    delay: { show: 800, hide: 100 }
			  }
			}; 

	if (DashboardData.userProfileInfo.role_id != "7") {
		$location.path("/dashboard").replace();
	} else if (DashboardData.userProfileInfo.role_id == "7") {
		$scope.path = "admin-case-docketsheet"
		Idle.setIdle(60 * 60); // 60 mins
		Idle.setTimeout(60 * 2);// 2 mins  : how much time the session timeout warning needs to be displayed
		dashboardUtils($scope, $filter, $routeParams, dashboardDataService,
				dashboardSettingService, $location, DashboardData, $timeout,
				userProfileViewSvc, actionMessageSvc, localStorageService, Idle,vm, $http);
		$scope.showPasswordExpiryWarning();
		$scope.setDashboardSettings()
	}

	

}
