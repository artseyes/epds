
angular.module(
		'epdsApp.dashboard')

.controller('advanceSearchCtrl', advanceSearchCtrl);

advanceSearchCtrl.$inject = ['$scope', '$location', '$http', '$filter', 'DTOptionsBuilder',
                           		'DTColumnDefBuilder', 'DTColumnBuilder', '$resource',
                        		'$timeout', '$route', '$rootScope', '$routeParams', '$uibModal',
                        		'modalService', '$window', '$log', '$location',
                        		'$filter', 'localStorageService', '$uibModal','advanceSearchDataSvc','agencyDropDownService','$ocLazyLoad','$injector','regEx','toolTip','base64']

/* @ngInject */
function advanceSearchCtrl ($scope, $location, $http, $filter, DTOptionsBuilder,
		DTColumnDefBuilder, DTColumnBuilder, $resource,
		$timeout, $route, $rootScope, $routeParams, $uibModal,
		modalService, $window, $log, $location,
		$filter, localStorageService, $uibModal,advanceSearchDataSvc,agencyDropDownService,$ocLazyLoad,$injector, regEx, toolTip, base64) {

	// $timeout(function() {
	//
	// 	 $('#advanceSearchForm').show();
	// 	 $('#advanceSearchForm').animateCss('slideInRight');
	//     }, 1000);
	//
	// $window.scrollTo(0, 0);
	advanceSearchDataSvc.loadAdvanceSearchView().then(function(data){
		if (!data.user_Role || data.user_Role.indexOf("GAO") == -1){
			$location.path("/dashboard").replace();
		}

		$timeout(function() {
			$('#advanceSearchForm').show();
			$('#bNumber').focus();
		}, 0);

	    var b_No = localStorageService.get("b_No");
	    var a_No = localStorageService.get("a_No");
		//Changed Arthur 10-16-2023 "B-"
	    $scope.b_No = b_No || "";
	    $scope.a_No = a_No || "";
		//Changed Arthur 03-06-2024 "A-"
		$scope.dateOptions = {};
		$scope.selectedCaseStatus = localStorageService.get("selectedCaseStatus");
		$scope.selectedCaseType = localStorageService.get("selectedCaseType");
		$scope.company_Name = localStorageService.get("company_Name");
		$scope.solicitation_No = localStorageService.get("solicitation_No");
		$scope.selectedLawGroup = localStorageService.get("selectedLawGroup");
		$scope.selectedAttorney = localStorageService.get("selectedAttorney");
		$scope.role = data.user_Role;
		var startSubmissionDate = localStorageService.get("startSubmissionDate");
		var endSubmissionDate = localStorageService.get("endSubmissionDate");
		var startDueDate = localStorageService.get("startDueDate");
		var endDueDate = localStorageService.get("endDueDate");
		$scope.startSubmissionDate = startSubmissionDate && new Date(startSubmissionDate);
		$scope.endSubmissionDate = endSubmissionDate && new Date(endSubmissionDate);
		$scope.startDueDate = startDueDate && new Date(startDueDate);
		$scope.endDueDate = endDueDate && new Date(endDueDate);
		$scope.startSubmissionDatePickerIsOpen = false;
		$scope.endSubmissionDatePickerIsOpen = false;
		$scope.startDueDatePickerIsOpen = false;
		$scope.endDueDatePickerIsOpen = false;
		$scope.partyInfo = localStorageService.get("partyInfo");
		$scope.intervenorCompName = localStorageService.get("intervenorCompName");

        if ($scope.startSubmissionDate || $scope.endSubmissionDate || $scope.startDueDate || $scope.endDueDate){
            angular.element(".dateSection").trigger("click");
        }

		$scope.startSubmissionDatePickerOpen = function($event) {

			if ($event) {
				$event.preventDefault();
				$event.stopPropagation();
			}
			this.startSubmissionDatePickerIsOpen = true;
		};

		$scope.endSubmissionDatePickerOpen = function($event) {

			if ($event) {
				$event.preventDefault();
				$event.stopPropagation();
			}
			this.endSubmissionDatePickerIsOpen = true;
		};

		$scope.startDueDatePickerOpen = function($event) {

			if ($event) {
				$event.preventDefault();
				$event.stopPropagation();
			}
			this.startDueDatePickerIsOpen = true;
		};

		$scope.endDueDatePickerOpen = function($event) {

			if ($event) {
				$event.preventDefault();
				$event.stopPropagation();
			}
			this.endDueDatePickerIsOpen = true;
		};

		advanceSearchDataSvc.getCaseTypesAndCaseStatus().then(function(data){
			$scope.caseStatuses = data.caseStatuses;
			$scope.caseTypes = 	data.caseTypes;	
			
				})	

		$scope.resetFilters = function() {
			$timeout(function() {
				document.getElementById( 'advanceSearchForm' ).scrollIntoView();
				});
			
	         localStorageService.remove("b_No", "a_No", "selectedCaseStatus", "selectedCaseType", "company_Name",
	                 "solicitation_No", "selectedLawGroup", "selectedAttorney", "startSubmissionDate", "endSubmissionDate",
	                 "startDueDate", "endDueDate", "tier1SelectedOption", "tier2SelectedOption","partyInfo","intervenorCompName");

			// $scope.b_No = "B-";
			// $scope.a_No = "A-";
			$scope.b_No = null;
			$scope.a_No = null;
			$scope.selectedCaseStatus = null;
			$scope.selectedCaseType = null;
			$scope.company_Name = null;
			$scope.solicitation_No = null;
			$scope.selectedLawGroup = null;
			$scope.selectedAttorney = null;
			$scope.startSubmissionDate = null;
			$scope.endSubmissionDate = null;
			$scope.startDueDate = null;
			$scope.endDueDate = null;
			$scope.startSubmissionDatePickerIsOpen = false;
			$scope.endSubmissionDatePickerIsOpen = false;
			$scope.startDueDatePickerIsOpen = false;
			$scope.endDueDatePickerIsOpen = false;
			$scope.partyInfo,$scope.intervenorCompName = null;
			
			$scope.tier1SelectedOption = {
					agency_Id : 0,
					agency_Name : "Please Select Agency",
					phone_No : "xxx-xxx-xxx",
			};
			$scope.tier1AgencyList.push($scope.tier1SelectedOption);
			localStorageService.set("tier1SelectedOption", $scope.tier1SelectedOption);

			$scope.tier2SelectedOption = {};
			$('#t2id').hide();
			localStorageService.set("tier2SelectedOption", $scope.tier2SelectedOption);
			
			$scope.showProtestInfo = false;
		}
		
	})
	
	

	$scope.tier2SelectedOption = {};
	
	agencyDropDownService.getListOfTier1Agencies().then(
			function(data) {
                var tier1SelectedOption = localStorageService.get("tier1SelectedOption");
                if (tier1SelectedOption) {
                    $scope.tier1SelectedOption = tier1SelectedOption;
                } else {
                    $scope.tier1SelectedOption = {
                            agency_Id : 0,
                            agency_Name : "Please Select Agency",
                            phone_No : "xxx-xxx-xxx",
                    };
                }
				$scope.tier1AgencyList = data.tier1AgencyList;
				$scope.tier1AgencyList.push($scope.tier1SelectedOption);
				
                var tier2SelectedOption = localStorageService.get("tier2SelectedOption");
                if (tier2SelectedOption) {
                    $timeout(function(){
                        angular.element("#agency_tier_1").trigger("change");
                        angular.element(".agencySection").trigger("click");
                    },1000)
                }
                

			});
	
	$scope.assignedAttorneyUserId = [];
	
	
	$ocLazyLoad.load(['jquery-datatables','angular-datatables','dashboard','account-update','request-to-intervene',
                      'angular-xeditable','cds','file-info-view'],{serie: true, cache :false}).then(function() {
        
   	 var caseDocketDataSvc = $injector.get("caseDocketDataSvc");
       
			caseDocketDataSvc.getListOfAttorneys().then(function(data){
					
					$scope.attorneyInfoList = data.gao_User_Info_List
					
					if (typeof $scope.attorneyInfo === 'undefined'
							|| $scope.attorneyInfo.first_Name == "pending") {
						$scope.assignedAttorneyUserId = $scope.attorneyInfoList[0].user_id;
					}
					
				})
	});
	
	

	$scope.selectedSearchFilters = [];

	$scope.b_No = "B-";
	$scope.a_No = "A-";
	
	var vm = this;
	$scope.searchProtestInfo = function(b_No, a_No,
			selectedCaseType, company_Name, selectedLawGroup,
			selectedAttorney, solicitation_No,
			selectedCaseStatus, tier1AgencyId, tier2AgencyId,
			startSubmissionDate, endSubmissionDate,
			startDueDate, endDueDate,partyInfo,intervenorCompName, onlyPrimaryANos) {
		$scope.showProtestInfo = false;
		
		localStorageService.set("b_No", b_No);
		localStorageService.set("a_No", a_No);
		localStorageService.set("selectedCaseType", selectedCaseType);
		localStorageService.set("company_Name", company_Name);
		localStorageService.set("selectedLawGroup", selectedLawGroup);
		localStorageService.set("selectedAttorney", selectedAttorney);
		localStorageService.set("solicitation_No", solicitation_No);
		localStorageService.set("selectedCaseStatus", selectedCaseStatus);
		localStorageService.set("tier1SelectedOption", tier1AgencyId);
		localStorageService.set("tier2SelectedOption", tier2AgencyId);
		localStorageService.set("startSubmissionDate", startSubmissionDate);
		localStorageService.set("endSubmissionDate", endSubmissionDate);
		localStorageService.set("startDueDate", startDueDate);
		localStorageService.set("endDueDate", endDueDate);
		localStorageService.set("partyInfo", partyInfo);
		localStorageService.set("intervenorCompName", intervenorCompName);
		
		advanceSearchDataSvc.searchProtestInfo(b_No, a_No,
				selectedCaseType, company_Name, selectedLawGroup,
				selectedAttorney, solicitation_No,
				selectedCaseStatus, tier1AgencyId, tier2AgencyId,
				startSubmissionDate, endSubmissionDate,
				startDueDate, endDueDate,partyInfo,intervenorCompName, onlyPrimaryANos).then(function(data){
					
					
					/*if(
						b_No == "B-" &&
						a_No == "A-" &&
						selectedCaseType == null &&
						selectedCaseType == null &&
						company_Name == null &&
						solicitation_No == null &&
						selectedLawGroup == null &&
						selectedAttorney == null &&
						startSubmissionDate == null &&
						endSubmissionDate == null &&
						startDueDate == null &&
						endDueDate == null &&
						tier1AgencyId == {
								agency_Id : 0,
								agency_Name : "Please Select Agency",
								phone_No : "xxx-xxx-xxx",
						}){
						
					}*/

					$scope.protestInfoList = data.protestInfoList;
					$scope.role = data.role;
					vm.protestInfoList = data.protestInfoList;

					for (let protest of data.protestInfoList) {
						protest.searchChildBnos = protest.supplemental_B_Nos + "," + protest.children_Protest_InfoList.map( (o)=> o.b_No ).join() + protest.children_Protest_InfoList.map( (o)=> o.supplemental_B_Nos ).join();
					}

					if (data.role == "GAO SUPERVISOR"
							|| data.role == "GAO ATTORNEY") {
						$scope.path = "case-docketsheet"
					} else {
						$scope.path = "admin-case-docketsheet"
					}

					$scope.redirectToCaseDocket = function (a_No){
						localStorageService.set("caseDocketa_No",a_No);
						$location.path($scope.path +"/" + base64.urlencode(a_No));
						
		        		
		        	}
					if ($scope.protestInfoList && $scope.protestInfoList.length) {
						$scope.showProtestInfo = true;
						
						$timeout(function() {
							document.getElementById( 'protestTable' ).scrollIntoView();
					        });
					} else {
						
						var customModalOptions = {
							headerText : 'Error',
							bodyText : 'No search results were found.  Please try changing your search criteria.',
							closeButtonText : 'OK',
							messageType : "error"
						};

						modalService.showModal({}, customModalOptions)
								.then(function(result) {
								});
					}

				
				})
				
				
		
		
	}
	
	advanceSearchDataSvc.getDashboardSettings(vm).then(
			function(data) {
				
				data.dtOptions.initComplete = function() {
					advanceSearchDataSvc.initCompleteFunc(vm);
				}
				
				vm.dtOptions = data.dtOptions;
			});

}
/* @ngInject */
angular.module('epdsApp').directive('datepickerPopup', function() {
	return {
		restrict : 'EAC',
		require : 'ngModel',
		
		link : function(scope, element, attr, controller) {
			//remove the default formatter from the input directive to prevent conflict
			controller.$formatters.shift();
		}
	}
});


//This is used to modify the default behavior of bootstrap datepicker
/* @ngInject */
angular.module('epdsApp').config(['uibDatepickerConfig', function (uibDatepickerConfig) {
    uibDatepickerConfig.showWeeks = false;
    
}]);
