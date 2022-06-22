angular.module(
		'epdsApp.dashboard')

.controller('InterveneCtrl', InterveneCtrl);

InterveneCtrl.$inject = ['$scope', '$location', '$rootScope', '$http', '$filter',
                 		'$resource', '$timeout', '$route', '$routeParams',
                 		'$uibModal','modalService', 'localStorageService','interveneDataSvc',
                 		'actionMessageSvc','$window','regEx','toolTip']


/* @ngInject */
function InterveneCtrl ($scope, $location, $rootScope, $http, $filter,
		$resource, $timeout, $route, $routeParams, $uibModal,
		modalService, localStorageService,interveneDataSvc,actionMessageSvc,$window,regEx,toolTip) {

	
	$scope.showAgencyRepCases = false;
	
	
	$scope.resetScreen = function(){
		$route.reload();
	}
	interveneDataSvc.loadRequestToIntervenePage().then(function(data){
		// $window.scrollTo(0, 0);
		$timeout(function() {
			$('#searchProtestInfo').show();
			// $('#searchProtestInfo').animateCss('fadeInRightBig');
			$('#bnumber').focus();
			
			$scope.role = data.user_Role;
			
			if ($scope.role == "AGENCY ATTORNEY") {
				$scope.headerText = "Join a Case"
				$rootScope.requestToAccessMessage = "Do you want to enter an appearance in this case ?";	
				$scope.b_NoWildCardText = "B-* will match B-XXXXXX.X, etc. or B-12* will match B-12XXXX.XX"
			}else if ($scope.role == "INTERVENOR") {
				$rootScope.requestToAccessMessage = "Do you want to request to intervene in this case ?";
				$scope.b_NoWildCardText = "B-123456* will match B-123456.1, B-123456.2, B-123456.3, etc."
				$scope.headerText = "Request to Intervene"
					
			}else{
				$location.path("/dashboard")
			}
			
		    }, 500);
		
		
		
	});
	
	$rootScope.authenticated = true;
	$scope.showProtestInfo = false;
	$scope.submitRequestToIntervene = false;
	$scope.form = {
		attachAssociatedDoc : null,
		intervenorCompanyName : "",
		address1 : "",
		address2 : "",
		zipCode : "",
		city : "",
		state : "",
		country : "",
	}
	
	$scope.submitNoticeOfAppearance = function (protestInfo){
		
		$scope.showProtestInfo = true;
		$scope.showAgencyRepCases = true;
		$scope.showSubmitNewDoc = true;
		$scope.protestInfo = protestInfo;
		$scope.agencyName = protestInfo.agency_Name;
		$scope.requestType  = "agency-rep-request";
		
		$scope.docInfo = {
				docTypeId : "0",
				uploadText : "Upload Primary Document",
				headerText : "Notice Of Appearance",
				a_No : protestInfo.a_No
				
		}
	}
	
	$scope.redirect = function (){
		$location.path("/intervene")
	}
	$scope.b_No = "B-";
	$scope.onchange = function(value){};
	$scope.searchProtestInfo = function(b_No) {
		$("#protestInfo").show();
		$("#submitNewDocForm").show();
		$("#interveneRequestForm").show();
		$("#caseAccessList").show();
		
		var checkIfThisBNumberStartWithPrefix = $filter("startsWith")(b_No,"B-",true);
		var checkNumberOfPlacesBeforeBNumber;
		var lengthOfbNumberAfterRemovingPrefixAndSuffix;
		var bNumberAfterRemovingPrefixAndSuffix;
		
		if (!checkIfThisBNumberStartWithPrefix){
			$scope.b_No = "B-" + b_No;
		}
		if ($scope.b_No.indexOf(".") >= 0){
			var bNumBeforeDecimalIfPresent = $scope.b_No.split(".")[0];
			bNumberAfterRemovingPrefixAndSuffix = bNumBeforeDecimalIfPresent.split("-")[1];
			lengthOfbNumberAfterRemovingPrefixAndSuffix = bNumberAfterRemovingPrefixAndSuffix.length;
			
		}else if ($scope.b_No.indexOf(".") <= 0){
			bNumberAfterRemovingPrefixAndSuffix = $scope.b_No.split("-")[1];
			lengthOfbNumberAfterRemovingPrefixAndSuffix = bNumberAfterRemovingPrefixAndSuffix.length;
		}
		
		if ($scope.role == "AGENCY ATTORNEY") {
			interveneDataSvc.getListOfProtestsForAgencyRepAccess(b_No).then(
					function(data) {
						$scope.agencyProtestInfoList = data.protestInfoList;
						
						if ($scope.agencyProtestInfoList.length > 0){
							$scope.showAgencyRepCases = true;
						}else {
							
							var customAttr = {
									headerText : "Info"	,
									bodyText : "We cannot find the case that you are looking for." +
									"  This might be because you already have access to the case," +
									" OR you have a pending request to access the case," +
									" OR the case is closed.  Please check the B# (B-XXXXXX.XX) and try again.",
									modalType : "info",
									actionType : "",
								    cancelBtnReq : "N",
								    cancelBtnActionType : ""
								}
							
							actionMessageSvc.showModal(customAttr);
						}

					})
		}else if ($scope.role == "INTERVENOR") {
			if(lengthOfbNumberAfterRemovingPrefixAndSuffix != 6){
				var customAttr = {
						headerText : "Info"	,
						bodyText : "B# has to be at least 6 digits B-XXXXXX.",
						modalType : "info",
						actionType : "",
					    cancelBtnReq : "N",
					    cancelBtnActionType : ""
					}
				
				actionMessageSvc.showModal(customAttr);
			}else{
				
				interveneDataSvc.getListOfProtestsForIntervenorRepAccess(b_No).then(function(data){


					$scope.agencyProtestInfoList = data.protestInfoList;
					
					if ($scope.agencyProtestInfoList.length > 0){
						$scope.showAgencyRepCases = true;
					}else {
						var customAttr = {
								headerText : "Info"	,
								bodyText : "We cannot find the case that you are looking for." +
										"  This might be because you already have access to the case," +
										" you already have a pending request to access the case," +
										" or the case is closed.  Please check the B# and try again.",
								modalType : "info",
								actionType : "",
							    cancelBtnReq : "N",
							    cancelBtnActionType : ""
							}
						
						actionMessageSvc.showModal(customAttr);
					}
				
				})
			}
			
		}
		

	}
	
	$scope.submitRequestToInterveneForm = function(protestInfo){
		
		$scope.protestInformation = protestInfo;
		$scope.protestInfo = protestInfo;
		$scope.agencyName = protestInfo.agency_Name;
		$scope.showProtestInfo = true;
		$scope.showAgencyRepCases = false;
		$scope.showSubmitNewDoc = false;
		$scope.submitRequestToIntervene = true;
		
	}
	
	
	
	$scope.cancel = function(){
		$scope.showProtestInfo = false;
		$scope.showSubmitNewDoc = false;
		$scope.submitRequestToIntervene = false;
		$scope.showAgencyRepCases = true;
	}
	$scope.redirect = function() {
		$scope.showProtestInfo = false;
		$location.path("/intervene")
	}
	$scope.showInterveneRequestForm = function() {
		$scope.submitRequestToIntervene = true;
	}


}

