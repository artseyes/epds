

angular.module('epdsApp.registration').controller('accountSelectionCtrl', roleSelectionCtrl);

roleSelectionCtrl.$inject = [ '$scope', '$uibModal' ]

function roleSelectionCtrl($scope, $uibModal) {

	$uibModal.open({
		templateUrl : 'scripts/app/registration/acctSelection.tpl.html',
		controller : roleSelectionModalCtrl,
		animation : true,
		size : 'md',
		keyboard : false,
		backdrop : false
	}).result.catch(angular.noop);

}

angular.module('epdsApp.registration').controller('accountSelectionCtrlModalCtrl', roleSelectionModalCtrl);
roleSelectionModalCtrl.$inject = [ '$http', '$scope', '$uibModalStack',
		'modalService', '$uibModal', '$uibModalInstance', '$location', 'ModalSvc','authFeedbackMessagesSvc' ]


function roleSelectionModalCtrl($http, $scope, $uibModalStack, modalService,
		$uibModal, $uibModalInstance, $location, ModalSvc,authFeedbackMessagesSvc) {

	$scope.role = [ {
		id : 1,
		name : 'Non-Agency Party Representative'
	}, {
		id : 6,
		name : 'AGENCY REPRESENTATIVE'
	} ];
	
	
	
$scope.selectedAcctType = $scope.role[0];


$scope.$watch('selectedAcctType',function() {
	
				var retVal = "";
				switch ($scope.selectedAcctType.id) {
				case 6:
					retVal = "agency";
					break;
			
				default:
					retVal = "vendor";
					break;
				
					return retVal;
				}
				$scope.message = retVal;
		})
		
	$scope.OK = function(selectedAcctType) {
			
				$uibModalInstance.close('cancel');
				var obj = {};
				switch (selectedAcctType.id) {
				
				case 6:
					authFeedbackMessagesSvc.getFeedbackMessages("agencyRegWarning").then(function(response){
						obj.data = response.data;
					})
					ModalSvc.showModal({
						  templateUrl: "scripts/app/authentication/authFeedbackMessages.tpl.htm",
						  controller: "authFeedbackCtrl",
						  inputs: obj
						}).then(function(modal) {
							 modal.element.modal({backdrop: false});
						      modal.close.then(function(result) {
						        switch (result) {
								case "registrationPage":
									$location.path("/register/" + selectedAcctType.id).replace();
									break;

								default:
									$location.path("/").replace();
									break;
								}
						      });
								
						}).catch(function(error) {
							
						});
					break;
				
				case 1:
					$location.path("/register/" + selectedAcctType.id).replace();
					break;
				default:
					$location.path("/").replace();
					break;
				
				}
	
	}
	
	$scope.cancel = function(){
		$uibModalInstance.close('cancel');
		$location.path("/").replace();
	}

}


