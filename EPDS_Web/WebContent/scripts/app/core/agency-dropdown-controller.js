epdsApp
		.controller(
				'agencyDropDownController',
				['$scope', '$http', '$location','agencyDropDownService',function($scope, $http, $location,agencyDropDownService) {

					if ($location.path() === "/manage-agency-contacts") {
						$scope.buttonText = "Update Agency Contacts"
					} else {
						$scope.buttonText = "Update"
					}
					
					agencyDropDownService.getListOfTier1Agencies().then(function(response){
					
					$scope.tier_1_AgencySelectedOption = response.tier1SelectedOption
					
					$scope.AgencyTier1List = response  &&  response.tier1AgencyList;
				})
					
					$scope
							.$watch(
									'tier_1_AgencySelectedOption',
									function() {
										if ($scope.tier_1_AgencySelectedOption) {
											
											
											agencyDropDownService.getListOfTier2Agencies($scope.tier_1_AgencySelectedOption).then(function(response){

												$scope.AgencyTier2List = response.tier2AgencyList;
												$scope.tier_2_AgencySelectedOption = response.tier2AgencySelectedOption;
												
												if (response && !response.tier2AgencySelectedOption) {
													$('#t2id').hide();
												} else {
													$('#t2id').show();
												}
											
											})
											
										}
									})
				}]);