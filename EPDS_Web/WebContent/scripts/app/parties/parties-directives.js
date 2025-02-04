(function() {
	'use strict';

	var directiveId = 'headersInfo';
	/* @ngInject */
	angular.module('epdsApp.parties').directive(directiveId,['RecursionHelper', headersInfoDirective]);

	function headersInfoDirective (RecursionHelper) {
		return {
			
			restrict: "EA",
	        scope: {
	        	protestInfo: '=', 
	        	attorneyInfo :'='
	        	},
	        templateUrl: function(elem,attrs){
	            return "scripts/app/parties/caseHeaders.tpl.html";
	        },
	        compile: function(element) {
	            return RecursionHelper.compile(element);
	        },
	        link: function(scope, element, attrs){
	          }
		};
	};
})();

(function() {
	'use strict';

	var directiveId = 'partiesInfo';
	angular.module('epdsApp.parties').directive(directiveId,['RecursionHelper','partiesDataSvc','$uibModal','$filter', partiesInfoDirective]);
	/* @ngInject */
	function partiesInfoDirective (RecursionHelper,partiesDataSvc,$uibModal,$filter) {
		return {
			
			restrict: "EA",
	        scope : {
	        	partiesList: '=?', 
	        	isOrphanIntervenor : '@',
	        	partyType : '@?',
	        	userRole :'=',
	        	isViewOnly :'=',
	        	protestInfo: '=', 
	        	loggedInUserInfo: '=', 
	        	intervenorCompanyName: '=?', 
	        	intervenorCompanyAddr: '=?', 
	        	},
	        templateUrl: function(elem,attrs){
	        	
	        	return attrs.templateUrl;
	        },

	        link: function($scope, $element, $attribute) {
	        	
	        	$scope.partiesList = $scope.partiesList && $filter('orderBy')($scope.partiesList,['invitationStatus']);
//	        	$scope.isFirmIdSame = $filter('contains')($scope.protestInfo.primaryAgencyInfoIds,$scope.protestInfo.agency_Info_Id);//($scope.loggedInUserInfo.firm_id == $scope.protestInfo.agency_Info_Id) ;
	        	$scope.isFirmIdSame = ($scope.loggedInUserInfo.firm_id == $scope.protestInfo.agency_Info_Id)  || $filter('contains')($scope.protestInfo.primaryAgencyInfoIds,$scope.protestInfo.agency_Info_Id);;
	        	$scope.isGAOUserSupervisorOrAttorney = ($scope.loggedInUserInfo.role_id == '8' || $scope.loggedInUserInfo.role_id == '3')
	        	$scope.isGAOUserWithFullAccess = ($scope.loggedInUserInfo.role_id == '7' || ($scope.isGAOUserSupervisorOrAttorney && !$scope.protestInfo.viewOnly))
	        	$scope.canAgencyRepBeAdded = ($scope.partiesList && $scope.partiesList.length <= 9 && $scope.partiesList.length > 0)
	        	$scope.agencyUser = ($scope.loggedInUserInfo.role_id == '5' || $scope.loggedInUserInfo.role_id == '6');
	        	$scope.isAgencyPOCUser = ($scope.loggedInUserInfo.role_id == '5');
	        	$scope.isPrimaryProtesterAssigned = ($scope.partyType == "P") && ($scope.partiesList && $scope.partiesList[0] && $scope.partiesList[0].role == 'PROTESTER');
	        	$scope.isPrimaryIntervenorAssigned = ($scope.partyType == "I") && ($scope.partiesList && $scope.partiesList[0] && $scope.partiesList[0].role == 'INTERVENOR');
	        	$scope.isPartiesListConsistOfPrimaryProtesterOrIntervenor = ($scope.isPrimaryProtesterAssigned ||  $scope.isPrimaryIntervenorAssigned);
	        	$scope.isGAOAdminAndPrimaryRepNotAssigned = $scope.loggedInUserInfo.role_id == '7'  && !$scope.isPartiesListConsistOfPrimaryProtesterOrIntervenor;
	        	$scope.isGAOAdminAndAllRepDeleted = $scope.loggedInUserInfo.role_id == '7'  && $scope.isOrphanIntervenor === "Y";
	        	
	        	console.log($scope.partyType)
	        	
	        	$scope.isAssignPrimaryRepBtnDisplayed = false;
	        	
	        	//is Primary Protester/Intervenor Btn can it be displayed?
	        	
	        	if ($scope.isGAOAdminAndPrimaryRepNotAssigned || $scope.isGAOAdminAndAllRepDeleted){
	        		$scope.isAssignPrimaryRepBtnDisplayed = true;
	        	}
	        	
	        	$scope.canAddAgencyBtnBeDisplayed = false;
	        	$scope.canDeleteAgencyBtnBeDisplayed = false;
	        	
	        
	        	
	        	//secondary agency
	        	if ($scope.partyType === "SA"){
	        		
	        		if ($scope.canAgencyRepBeAdded 
	        				&& ($scope.isGAOUserWithFullAccess || ($scope.agencyUser && !$scope.isFirmIdSame))){
	        			$scope.canAddAgencyBtnBeDisplayed = true;
	        		}
	        		
	        		if ($scope.isGAOUserWithFullAccess || ($scope.isAgencyPOCUser && !$scope.isFirmIdSame)){
	        			$scope.canDeleteAgencyBtnBeDisplayed = true;
	        		}
	        		
	        		//primary agency
	        	}else if ($scope.partyType === "A"){
	        		
	        		if ($scope.canAgencyRepBeAdded 
	        				&& ($scope.isGAOUserWithFullAccess || ($scope.agencyUser && $scope.isFirmIdSame))){
	        			$scope.canAddAgencyBtnBeDisplayed = true;
	        		}
	        		
	        		if ($scope.isGAOUserWithFullAccess || ($scope.isAgencyPOCUser && $scope.isFirmIdSame)){
	        			$scope.canDeleteAgencyBtnBeDisplayed = true;
	        		}
	        		
	        	}
	        	
	        
	    
	        	/* Updating the protective order */

	        	$scope.updateProtectiveOrder = function(userId,
	        			updateStatus) {
	        		
	        		
	        		if (updateStatus === "Y"){
	        			updateStatus = "N"
	        		}else if (updateStatus === "N"){
	        			updateStatus = "Y"
	        		}
	        		partiesDataSvc.updateProtectiveOrder(userId,updateStatus,$scope.protestInfo.a_No);
	        	}

	        	/* Adding parties to the case */

	        	$scope.addPartiesToTheCase = function(partyType,inviterRole,companyName,companyAddr) {
	        		
	        		partiesDataSvc.addPartiesToTheCase(partyType,inviterRole,companyName,companyAddr,$scope.protestInfo);
	        	}

	        	/* delete parties from the case */
	        	$scope.deletePartiesFromTheCase = function(userId,
	    				firstName, lastName, partyType) {
	        		partiesDataSvc.deletePartiesFromTheCase(userId,
	        				firstName, lastName, partyType,$scope.protestInfo.a_No)
	        	}
	          }
		};
	};
})();


