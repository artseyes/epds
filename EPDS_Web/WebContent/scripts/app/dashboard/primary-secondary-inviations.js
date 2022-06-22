
angular.module('epdsApp.dashboard').controller('RespondToPrimaryOrSecondaryRepsRequestCtrl',
		RespondToPrimaryOrSecondaryRepsRequestCtrl);


RespondToPrimaryOrSecondaryRepsRequestCtrl.$inject = ['$scope', '$http','items',
                                                      'modalService', '$uibModalInstance',
                                                      '$uibModalStack','$uibModal','localStorageService',
                                                      'protestInformation','actionMessageSvc','$route','$timeout','$httpParamSerializerJQLike']

function RespondToPrimaryOrSecondaryRepsRequestCtrl($scope, $http, items,
		modalService, $uibModalInstance, $uibModalStack, $uibModal, 
		localStorageService,protestInformation,actionMessageSvc,$route,$timeout,$httpParamSerializerJQLike) {

	$scope.protestInformation = protestInformation;
	$scope.secondaryRepApprovalList = items;

	$scope.primaryRepInvitationList = items;

	
	//Requirement is no longer valid
	
	/*$scope.respondToSecondaryRepReq = function(index, response, invitee_Id,
			a_No) {

		// need to be consistent -----> one place we are using reject and other
		// No
		if (response == "N") {

			if (localStorageService.isSupported) {
				localStorageService.set("inviteeId", invitee_Id);
			} else if (localStorageService.cookie.isSupported) {
				localStorageService.cookie.set("inviteeId", invitee_Id);
			}

			rejectRequestsConfirmationModal($uibModal, $scope,
					$scope.secondaryRepApprovalList);
		} else {
			
			
			var customModalOptions = {
					headerText : 'success',
					bodyText : 'You have successfully approved',
					closeButtonText : 'OK',
					messageType : "success"
				};
				modalService.showModal({},
						customModalOptions).then(
						function(result) {
						});
				
				
			respondToPrimaryOrSecondaryRequests($http, $scope, $uibModalStack,localStorageService,
					items, response, index, a_No, "primary", invitee_Id)
		}
	};*/

	$scope.respondToPrimaryInvites = function(index, response, a_No,companyName) {

		var thisProtestInfo = {
				a_No : a_No,
				companyName :companyName,
				index : index,
				response : response
		}
		if (response === "N") {

			/*if (localStorageService.isSupported) {
				localStorageService.set("currentIndex", index);
				localStorageService.set("currentA_No", a_No);
			} else if (localStorageService.cookie.isSupported) {
				localStorageService.cookie.set("currentIndex", index);
				localStorageService.cookie.set("currentA_No", a_No);
			}*/

			rejectRequestsConfirmationModal($uibModal, $scope,
					$scope.primaryRepInvitationList,thisProtestInfo);

		} else {
			
			/*var customModalOptions = {
					headerText : 'success',
					bodyText : 'You have successfully accepted all the invitations',
					closeButtonText : 'OK',
					messageType : "success"
				};
				modalService.showModal({},
						customModalOptions).then(
						function(result) {
							$location.path("/dashboard").replace();
						});*/
			
				
			respondToPrimaryOrSecondaryRequests($http,$scope,$uibModalStack,localStorageService,
					actionMessageSvc,$route,$timeout,
					items, response, index, a_No, "secondary",$httpParamSerializerJQLike);

		}

	};

	/*On the confirmation dialog page if the user selects 'Yes' means reject the invitation*/
	$scope.yes = function(response) {

		
		$uibModalInstance.dismiss('cancel');

		/*var a_No = null;
		var index = null;
		var inviteeId = null;
		var type = null;

		if (localStorageService.isSupported) {
			a_No = localStorageService.get("currentA_No");
			index = localStorageService.get("currentIndex");
			inviteeId = localStorageService.get("inviteeId");
		} else if (localStorageService.cookie.isSupported) {
			a_No = localStorageService.cookie.get("currentA_No");
			index = localStorageService.cookie.get("currentIndex");
			inviteeId = localStorageService.cookie.get("inviteeId");
		}
		if (inviteeId == null) {
			type = "secondary";
		} else {
			type = "primary";
		}*/
		respondToPrimaryOrSecondaryRequests($http,$scope,$uibModalStack,localStorageService,
				actionMessageSvc,$route,$timeout, items,
				'reject', response.index, response.a_No, "secondary",$httpParamSerializerJQLike)
	}

	$scope.cancel = function() {
		$uibModalInstance.dismiss('cancel');
	}

}

(function() {
	'use strict';

	var serviceId = 'primaryOrSecondaryRepsRequestDataService';

	angular.module('epdsApp.dashboard').factory(serviceId,
			['$http', '$rootScope', '$uibModal', primaryOrSecondaryRepsRequestDataService]);
	/* @ngInject */
	function primaryOrSecondaryRepsRequestDataService ($http, $rootScope, $uibModal) {

		var service = {
				getListOfPrimaryRepApprovals : getListOfPrimaryRepApprovals,
				getListOfSecondaryRepInvitations : getListOfSecondaryRepInvitations
		};

		return service;

		function getListOfPrimaryRepApprovals() {

			return $http({
				url : '/epds/primary-rep-approvals',
				method : 'GET',
				headers: {
					'Accept': 'application/json',
					'Content-Type': 'application/json'
						},
			    
			})
			.then(
					function(data) {
						$rootScope.accepted_SecondaryProtesterList = data.data.accepted_SecondaryProtesterList;
						
						return	(data.data.accepted_SecondaryProtesterList.length > 0) ? data.data : null	

					},
					function(error) {
						
						return error;
					});

		}

		function getListOfSecondaryRepInvitations() {

			return $http({
				url : '/epds/secondary-rep-invites',
				method : 'GET',
				headers: {
					'Accept': 'application/json',
					'Content-Type': 'application/json'
						},
			    
			})
			.then(
					function(data) {
						return data.data
					},
					function(error) {
						console
						.log("Error occured when retrieving list of secondary rep invitations "
								+ JSON.stringify(error));
						return error;
					});

		}
	}
})();




/*
 * This directive is used make the modal dialog draggable by default we cannot drag modal dialog boxes
 * need to make some adjustment when nested modals are displayed at the same time only the one at the top needs
 * to be dragged ....as of now all the modals boxes are dragged at the same time
*/

/* @ngInject */
angular.module('epdsApp.dashboard').directive('modaldraggable', function ($document) {
	  "use strict";
	  return function (scope, element) {
	    var startX = 0,
	      startY = 0,
	      x = 0,
	      y = 0;
	     element = angular.element(document.getElementsByClassName("modal-dialog"));
	     
	    /*element.css({
	      position: 'fixed',
	      cursor: 'move'
	    });*/
	    
	    element.on('mousedown', function (event) {
	      // Prevent default dragging of selected content
	      event.preventDefault();
	      startX = event.screenX - x;
	      startY = event.screenY - y;
	      $document.on('mousemove', mousemove);
	      $document.on('mouseup', mouseup);
	    });

	    function mousemove(event) {
	      y = event.screenY - startY;
	      x = event.screenX - startX;
	      element.css({
	        top: y + 'px',
	        left: x + 'px'
	      });
	    }

	    function mouseup() {
	      $document.unbind('mousemove', mousemove);
	      $document.unbind('mouseup', mouseup);
	    }
	  };
	});




/*
 * respond to primary or secondary requests....
 *I need to refactor this code based on what roshan says about 
 *if we can use the same servlet mapping for responding to requests 
 *need to check if clearing localstorage/cookie storage 
 * will have any effect elsewhere in the application.
 *
 */


respondToPrimaryOrSecondaryRequests.$inject = ['$http','$scope','$uibModalStack','localStorageService','actionMessageSvc','$route','$timeout','$httpParamSerializerJQLike']

function respondToPrimaryOrSecondaryRequests($http,$scope,$uibModalStack,localStorageService,
												actionMessageSvc,$route,$timeout,
												items,response,index,a_No,type,$httpParamSerializerJQLike){
	

	var params = {
		response : response,
		invite_A_No : a_No
	}
	

	$http({
		url : '/epds/respond-to-invitation',
		method : 'POST',
		headers : {
			'Content-Type' : 'application/x-www-form-urlencoded'
		},
		data : $httpParamSerializerJQLike(_.omitBy(params, _.isNil)),
	    
	}).then(function(data) {

	});

	$scope.primaryRepInvitationList.splice(items, (index + 1));
	var totalRemainingInvitatons = $scope.primaryRepInvitationList.length

	
	if (totalRemainingInvitatons <= 0) {
		/*localStorageService.clearAll();
		localStorageService.cookie.clearAll();*/
		var customAttr = {
				headerText : "Success"	,
				bodyText : "You have successfully accepted all pending invitations.",
				modalType : "success",
				actionType : "",
			    cancelBtnReq : "N",
			    cancelBtnActionType : ""
			}
		
		$timeout(function() {
			actionMessageSvc.showModal(customAttr).then(function(){
				$route.reload()
			})
	        
	    }, 2000);
		
		$uibModalStack.dismissAll();
	}
	
	

	/*if (type == "primary") {
		
		$http({
			url : '/epds/approve-secondary-protester',
			method : 'GET',
			headers : {
				'Content-Type' : 'application/json'
			},
			params : {
				response : response,
				secondary_User_Id : invitee_Id,
				a_No : a_No
			}
		}).then(function(data) {
			
		});

		$scope.secondaryRepApprovalList.splice(items, (index + 1));
		var totalRemainingApprovals = $scope.secondaryRepApprovalList.length

		if (totalRemainingApprovals <= 0) {
			localStorageService.clearAll();
			localStorageService.cookie.clearAll();
			$uibModalStack.dismissAll();
		}
	}else {
	
			$http({
			url : '/epds/respond-to-invitation',
			method : 'GET',
			headers : {
				'Content-Type' : 'application/json'
			},
			params : {
				response : response,
				invite_A_No : a_No
			}
		}).then(function(data) {
			
		});
		
		$scope.primaryRepInvitationList.splice(items, (index + 1));
		var totalRemainingInvitatons = $scope.primaryRepInvitationList.length

		console.log("toal remaining requests" , totalRemainingInvitatons)
		if (totalRemainingInvitatons <= 0) {
			localStorageService.clearAll();
			localStorageService.cookie.clearAll();
			 $uibModalStack.dismissAll();
		}
			
			
	}*/
	
	
	
}

/*When primary or secondary Reps. reject the request. Request needs to be confirmed*/

rejectRequestsConfirmationModal.$inject = ['$uibModal','$scope']
/* @ngInject */
function rejectRequestsConfirmationModal($uibModal,$scope,listOfRequests,thisProtestInfo){
	
	$uibModal
		.open({
			templateUrl : 'views/dialogue-box-html-templates/representatives-requests-confirmation.html',
			controller : 'RespondToPrimaryOrSecondaryRepsRequestCtrl',
			scope : $scope,
			resolve : {
				items : function() {
					return listOfRequests;
				},
				protestInformation : function() {
					return thisProtestInfo;
				}
			},
			size : 'md',
			backdrop : 'static'
		}).result.catch(angular.noop);



}

//need to place this code in appropriate place
/* @ngInject */
angular.module('epdsApp.dashboard').config(function (localStorageServiceProvider) {
	  localStorageServiceProvider
	    .setPrefix('epdsApp.dashboard')
	    .setStorageType('sessionStorage')
	    .setNotify(true, true)
	});
