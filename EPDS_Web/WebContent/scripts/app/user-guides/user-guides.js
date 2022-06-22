(function() {
	'use strict';

	angular.module('epdsApp')

	.controller('UserGuideCtrl', UserGuideCtrl);

	UserGuideCtrl.$inject = [ '$scope', '$location', '$http', '$filter',
			'$route', '$rootScope', '$routeParams', '$window','Idle' ]

	function UserGuideCtrl($scope, $location, $http, $filter, $route,
			$rootScope, $routeParams, $window,Idle) {

		
		var roleId = $rootScope.userProfileInfo.role_id;
		

		if (roleId == 1 
				|| roleId == 2 
				|| roleId == 4 
				|| roleId == 9) {
			$scope.showProtesterGuide = true;
		} else if (roleId == 3 
				|| roleId == 8) {
			$scope.showGAOGuide = true;
		}else if (roleId == 5 
				|| roleId == 6) {
			$scope.showAgencyGuide = true;
		}else if (roleId == 7) {
			$scope.showPLCGGuide = true;
			
			//Temporary
		}else{
			$scope.showProtesterGuide = true;
		}
		
		console.log($rootScope.userProfileInfo.role_id)
		
	}

})();
