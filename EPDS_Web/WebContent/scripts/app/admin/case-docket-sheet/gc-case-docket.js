epdsApp
/* @ngInject */
		.controller(
				'gcDocketSheetController',
				['localStorageService','$location','$routeParams','base64', function(localStorageService,$location,$routeParams, base64) {
					localStorageService.set("gc_A_no",$routeParams.a_No);
					localStorageService.set("gc_role",$routeParams.role);
					$location.path("/admin-case-docketsheet/"+ base64.urlencode($routeParams.a_No));
					
				}]);
