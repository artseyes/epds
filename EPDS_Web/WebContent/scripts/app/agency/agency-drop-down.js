
(function() {
	'use strict';

	
	var serviceId = 'agencyDropDownService';
	var directiveId = 'agencyDropDown';
	/* @ngInject */
	angular.module('epdsApp').factory(serviceId,
			['$http','$q','$timeout','$filter','actionMessageSvc', agencyDropDownService]);

	/* @ngInject */
	function agencyDropDownService ($http,$q,$timeout,$filter,actionMessageSvc) {

		var service = {
				getListOfTier1Agencies : getListOfTier1Agencies,
				getListOfTier2Agencies : getListOfTier2Agencies,
				validateAgencyInfo : validateAgencyInfo
		};

		return service;


		
		function validateAgencyInfo(tier1SelectedOption, tier2SelectedOption){
			
			var retVal = true;
			if (tier1SelectedOption && tier1SelectedOption.agency_Id == 0){
				
				var customAttr = {
						headerText : "Error"	,
						bodyText : "Please select the Agency",
						modalType : "error",
						actionType : "",
					    cancelBtnReq : "N",
					    cancelBtnActionType : ""
					}
				
				actionMessageSvc.showModal(customAttr);
				
				retVal = false;
			}
			
			if (tier2SelectedOption && (tier2SelectedOption.agency_Id == 0 || tier2SelectedOption === "")){
				
				var customAttr = {
						headerText : "Error"	,
						bodyText : "Please select the Tier 2 Agency",
						modalType : "error",
						actionType : "",
					    cancelBtnReq : "N",
					    cancelBtnActionType : ""
					}
				
				actionMessageSvc.showModal(customAttr);
				
				retVal = false;
			}
			
			return retVal;
		}
		
		function getListOfTier1Agencies() {
			var obj = {};
			
			
			
			
			
			return $http({
				url : '/epds/get-tier_1-agency-list',
				method : 'GET',
				headers: {
					'Accept': 'application/json',
					'Content-Type': 'application/json'
						},
			    ignoreLoadingBar: true

			    
			}).then(
					function(data) {
						var agencyList  = data.data.AgencyTier1List;
						var defaultSelectedOption = {
							agency_Id : 0,
							agency_Name : "Please Select Agency",
							phone_No : "xxx-xxx-xxx",
						}
						
						function arraymove(arr, fromIndex, toIndex) {
					        var element = arr[fromIndex];
					        arr.splice(fromIndex, 1);
					        arr.splice(toIndex, 0, element);
					    }
						
						
						agencyList = $filter('orderBy')(agencyList, 'agency_Name');
						
					    var foundItem = $filter('filter')(agencyList, { "agency_Name": "Other "  }, true)[0];

					    //get the index
					    var index = agencyList.indexOf(foundItem );
					    arraymove(agencyList,index,agencyList.length -1)
						obj.tier1SelectedOption = defaultSelectedOption;
						
						if (agencyList){
							agencyList.splice(0,0,defaultSelectedOption);	
						}
						obj.tier1AgencyList = agencyList;

						return	obj;	

					},
					function(error) {

						return error;
					});
		}

		function getListOfTier2Agencies(tier1SelectedOption) {

			
			
			return $http({
							url : '/epds/tier2/'+ tier1SelectedOption.agency_Id,
							method : 'GET',
							headers: {
								'Accept': 'application/json',
								'Content-Type': 'application/json'
									},
						    
						    ignoreLoadingBar: true
						}).then(
					function(data) {
						var obj = {};
						var tier2AgencyList= obj.tier2AgencyList = data.data.tier2AgencyList;
						var defaultSelectedOption = {
								agency_Id : 0,
								agency_Name : "Please Select Tier 2 Agency",
								phone_No : "xxx-xxx-xxx",
								tier_1_Agency_Id : 0
							}
						
						if (tier2AgencyList != "empty"){
							
							tier2AgencyList.splice(0,0,defaultSelectedOption);
							obj.tier2AgencyList = tier2AgencyList;
							obj.tier2AgencySelectedOption =  defaultSelectedOption;
						
						}
						
						return	obj	

					},
					function(error) {
						
						return error;
					});
		}

	}
	
	angular.module('epdsApp').directive(directiveId,['agencyDropDownService','localStorageService','$rootScope', agencyDropDownDirective]);
	/* @ngInject */
	function agencyDropDownDirective (agencyDropDownService,localStorageService,$rootScope) {
		return {
			restrict : "EA",
			scope : {
				tooltip : '@',
				agencylist : '=',
				tier1SelectedOption : '=',
				tier2SelectedOption : '=',
				source : '@?',
				protestWarning: '@?'
			},
			templateUrl: function(elem,attrs){
				return "scripts/app/agency/agencyDropDown.tpl.html";
			},
			
			link: function($scope, $element, $attribute) {

				$scope.loadTier2Agencies= function (tier1SelectedOption){
					
				    if (tier1SelectedOption){
				        agencyDropDownService.getListOfTier2Agencies(tier1SelectedOption).then(
	                            function(data) {
	                                $scope.tier2AgencyList = data.tier2AgencyList;

	                                if ($scope.tier2AgencyList === "empty") {
	                                    $('#t2id').hide();
	                                } else {
	                                	var lsTier2Option = localStorageService.get("tier2SelectedOption");
										var length = lsTier2Option && Object.keys(lsTier2Option).length || 0;
	                                    $scope.tier2SelectedOption = length ? lsTier2Option : data.tier2AgencySelectedOption;
	                                    $('#t2id').show();
	                                }
	                            }); 
				    }
					
				}

			},
		};
	};
})();
