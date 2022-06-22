 /* @ngInject */
angular.module('epdsApp').service('modalService', ['$uibModal','$http','$routeParams','$location',
    function ($uibModal,$http,$routeParams,$location) {

        var modalDefaults = {
            backdrop: true,
            keyboard: true,
            modalFade: true,
            templateUrl: 'views/dialogue-box-html-templates/custom-messages-dialogue-box-template.html?bust=' + Math.random().toString(36).slice(2),
        };

        var modalOptions = {
            closeButtonText: 'Close',
            actionButtonText: 'OK',
            headerText: 'Proceed?',
            bodyText: 'Perform this action?'
        };

        this.showModal = function (customModalDefaults, customModalOptions) {
            if (!customModalDefaults) customModalDefaults = {};
            customModalDefaults.backdrop = 'static';
            return this.show(customModalDefaults, customModalOptions);
        };

        this.show = function (customModalDefaults, customModalOptions) {
            // Create temp objects to work with since we're in a singleton
			// service
            var tempModalDefaults = {};
            var tempModalOptions = {};

            // Map angular-ui modal custom defaults to modal defaults defined in
			// service
            angular.extend(tempModalDefaults, modalDefaults, customModalDefaults);

            // Map modal.html $scope custom properties to defaults defined in
			// service
            angular.extend(tempModalOptions, modalOptions, customModalOptions);

            if (!tempModalDefaults.controller) {
                tempModalDefaults.controller = ('modalController', ['$scope', '$uibModalInstance','$route','localStorageService', function ($scope, $uibModalInstance,$route,localStorageService) {
                    $scope.modalOptions = tempModalOptions;
                    
                    
                   
                    
                    $scope.modalOptions.reloadCaseDocetSheetInfo = function (result) {
                    
                	 var params = {
         					"a_No" : $routeParams.a_No,
         					"role" : $routeParams.role,
         				}
                	 
                    $http({
            				url:'/epds/casedocketsheet',
            				method : 'GET',
            				headers: {
            					'Accept': 'application/json',
            					'Content-Type': 'application/json'
            						},
            			    params: params,
            			}).success(function(response) {
            				
            			})
            			
                    	$uibModalInstance.close(result);
                    };
                    $scope.modalOptions.ok = function (result) {
                        /*window.location.reload()*/
                    	$route.reload();
                    	$uibModalInstance.close(result);
                    };
                    $scope.modalOptions.close = function (result) {
                        $uibModalInstance.close('cancel');
                    };
                    
                    $scope.redirectToDashboard = function(result){
                    	
                    	var form = localStorageService.get("protestInfoForm");
                    	
                    	if (form && form.user_Role == "GAO ADMIN" ){
							$location.path("/admin-dashboard/unassigned").replace();
						}else{
							$location.path("/dashboard").replace();
						}
                    	
                    	$uibModalInstance.close(result);
                    }
                    
                    $scope.modalOptions.paymentSuccess = function (result) {
                    	 $scope.redirectToDashboard(result);
                    };
                    
                    $scope.modalOptions.paymentFailure = function (result) {
                    	
                    	$scope.redirectToDashboard(result);
                    };
                }]);
            }

            return $uibModal.open(tempModalDefaults).result;
        };

    }]);
