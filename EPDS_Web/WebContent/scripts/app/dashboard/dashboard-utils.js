
function dashboardUtils($scope, $filter,$routeParams,dashboardDataService, 
		dashboardSettingService,$location,DashboardData,$timeout,userProfileViewSvc,actionMessageSvc,localStorageService,Idle,vm, $http) {
	$scope.groupList = [];
	$scope.selectedGroup =[];
	vm.attorneyGroupIds = [];
	vm.attorneyNameArray = [];
	vm.protest_Info_List = DashboardData.protest_Info_List;

//	for (let protest of DashboardData.protest_Info_List) {
//		protest.searchChildBnos = protest.supplemental_B_Nos + "," + protest.children_Protest_InfoList.map((o) => o.b_No).join() + protest.children_Protest_InfoList.map((o) => o.supplemental_B_Nos).join();
//	}
	if (DashboardData.protest_Info_List && DashboardData.protest_Info_List.length) {
		for (let protest of DashboardData.protest_Info_List) {
			protest.searchChildBnos = protest.supplemental_B_Nos + "," + protest.children_Protest_InfoList.map((o) =>
				o.b_No).join() + protest.children_Protest_InfoList.map((o) => o.supplemental_B_Nos).join();
		}
	}

	//var selectedCaseStatusObj = localStorage.getItem("caseStatus") && JSON.parse(localStorage.getItem("caseStatus"));
	$scope.attorneyGrpIds= [];
	var filterProtestInfoListByUniqueAttorneyGrpId, filterProtestInfoListByUniqueAttorneyName;

	$scope.caseStatusList = [];
	if (vm.protest_Info_List && vm.protest_Info_List.length) {
		vm.displayDashboard = true;
		vm.fullProtestInfoList = DashboardData.unjoinedProtestInfoList || DashboardData.protest_Info_List || [];
		vm.fullJoinedProtestInfoList = DashboardData.protest_Info_List || [];
	}
	
	vm.caseStatusFilterList  = $scope.selectedCaseStatus = [{
		id : "OPEN",
		label : "OPEN"
	} ];

	if (DashboardData.userProfileInfo.role_id == "1"){
		vm.caseStatusFilterList  = $scope.selectedCaseStatus = [{
			id : "OPEN",
			label : "OPEN"
		},{
			id : "CLOSED",
			label : "CLOSED"
		} ];
	}

	vm.caseStatusFilterList  = $scope.selectedCaseStatus = localStorageService.get("dboardSelectedCaseStatus") || vm.caseStatusFilterList;
	$scope.buttonText = {
			buttonDefaultText : "Filter by Group "
	};
	
	$scope.$on('Keepalive', function() {
		
		if ($http.pendingRequests && $http.pendingRequests.length > 0){
			onItemSelectionDone();	
		}
		
	});

	$scope.caseStatusBtnText = {
			buttonDefaultText : "Filter by Case Status "
	};

	$scope.gaoUserList = DashboardData.gaoUserList;
	

	filterProtestInfoListByUniqueAttorneyGrpId = _.uniqBy($scope.gaoUserList, function(item){
		return item.group_No
	});

	filterProtestInfoListByUniqueAttorneyName = _.uniqBy($scope.gaoUserList, function(item){
		return (item.last_Name + ", " + item.first_Name)
	});
	

	
	angular.forEach(filterProtestInfoListByUniqueAttorneyGrpId, function(value,
			key) {
		if (value.group_No) {
			$scope.attorneyGrpIds.push(value.group_No)
		}
	});

	angular.forEach(filterProtestInfoListByUniqueAttorneyName, function(value,
			key) {
		
		var attorneyName = value.last_Name + ", " + value.first_Name
		if (value.group_No != 0) {
			$scope.groupList.push({
				id : value.group_No + "|||" + attorneyName + "|||" + value.user_Id ,
				label : attorneyName,
				groupId: value.group_No,
				attorneyName: attorneyName,
				attorneyId: value.user_Id
			})
		}
	});

	
	$scope.selectByGroupSettings = {
			selectByGroups : $filter('orderBy')($scope.attorneyGrpIds),

			groupByTextProvider : function(groupValue) {

				return "Group " + groupValue;
			},
			groupBy : 'groupId',
			smartButtonMaxItems : 8,
			buttonClasses : 'btn btn-success',
			scrollableHeight: '300px', 
			scrollable: true,
			enableSearch : true
	};


	$scope.selectByCaseStatusSettings  = {
			smartButtonMaxItems : 8,
			buttonClasses : 'btn btn-success',
	}

	angular.forEach(DashboardData.caseStatusList, function(value,
			key) {
		$scope.caseStatusList.push({
			id : value,
			label : value,
			caseStatusFilter : true
		})

	});
	

	$scope.groupList = $filter("orderBy")($scope.groupList, 'id');
	vm.orignalGrpInfoList = angular.extend([],$scope.groupList)
	$scope.caseStatusList = $filter("orderBy")($scope.caseStatusList, 'id');

	if (DashboardData.userProfileInfo.role_id == "8") {

		if (vm.group_No != null) {
			$scope.selectedGroup = $filter('filter')($scope.groupList,function(value){
				return value.groupId == vm.group_No;
			});

			vm.selectedGroup = $scope.selectedGroup;
			
			vm.attorneyGroupIds = _.map($scope.selectedGroup,'groupId')
			vm.attorneyNameArray = _.map($scope.selectedGroup,'attorneyName');
		}

	}
	
	//vm.attorneyGroupIds = localStorageService.get("grpIds")  || vm.attorneyGroupIds;
	
	$scope.caseStatusFilterEvents = {

			onItemSelect : function(item) {
				vm.caseStatusFilterList = $scope.selectedCaseStatus;
			
				onItemSelectionDone();
			},

			onItemDeselect : function(item) {
				
				vm.caseStatusFilterList = _.reject(vm.caseStatusFilterList, function(eachCaseStatus){
					
					return item.id.toLowerCase().trim() == eachCaseStatus.id.toLowerCase().trim();
				})
				
				if (vm.caseStatusFilterList && !vm.caseStatusFilterList.length){
					vm.caseStatusFilterList = $scope.caseStatusList;
				}
				
				onItemSelectionDone();
			},

			onSelectAll : function() {
				onItemSelectionDone();
			},

			onDeselectAll : function() {
				vm.caseStatusFilterList = $scope.caseStatusList;
				onItemSelectionDone();
			},

			
	}
	
	function onItemSelectionDone(){
		
		
		$http.pendingRequests.forEach(function(request) {
			if (request.cancel) {
				request.cancel.resolve();
			}
		});
		
		getDashboardData(vm.viewType || null, 0,false);
		
	}
	
	function getGroupIdAndAttorneyId (id){
		var groupIdAndAttorneyId;
		if (!angular.isObject(id)){
			groupIdAndAttorneyId  = id.split("|||")	
		}else{
			groupIdAndAttorneyId = id && id["id"].split("|||");
		}

		return groupIdAndAttorneyId;
	}


	function filterByGroupId(group_No){
		var groups = $scope.groupList;
		
		if (groups && groups.length <=0){
			groups = vm.orignalGrpInfoList;
			
		}
		if (group_No && groups.length) {
			vm.selectedGroup = groups = $filter('filter')(vm.orignalGrpInfoList,function(value){
				return value.groupId == group_No;
			});
		}
		$scope.selectedGroup = groups;
		$scope.groupList =  angular.extend([],vm.orignalGrpInfoList);
		return groups;

	}
	$scope.plcgGroupFilterEvents = {

			onItemSelect : function(item) {
				
				var groupIdAndAttorneyId = getGroupIdAndAttorneyId(item);
				
				if (groupIdAndAttorneyId && groupIdAndAttorneyId[1]){
					vm.attorneyNameArray.push(groupIdAndAttorneyId[1])
				}
				
				if (groupIdAndAttorneyId && groupIdAndAttorneyId[0]){
					vm.attorneyGroupIds.push(groupIdAndAttorneyId[0])
				}
				
				onItemSelectionDone();
			
			},

			onItemDeselect : function(item) {
				
				var groupIdAndAttorneyId = getGroupIdAndAttorneyId (item);
				
				if (groupIdAndAttorneyId && groupIdAndAttorneyId[0]){
					vm.attorneyGroupIds = _.without(vm.attorneyGroupIds, groupIdAndAttorneyId[0]);
				}
				
				if (groupIdAndAttorneyId && groupIdAndAttorneyId[1]){
					vm.attorneyNameArray = _.without(vm.attorneyNameArray, groupIdAndAttorneyId[1]);
				}
				
				onItemSelectionDone();
				
			},

			onSelectAll : function() {
				var groups = filterByGroupId();
				vm.attorneyGroupIds = _.map(groups,'groupId')
				vm.attorneyNameArray = _.map(groups,'attorneyName');
				onItemSelectionDone();
			},

			onDeselectAll : function() {
				var groups = [];
				if (DashboardData.userProfileInfo.role_id == "8") {
					groups = filterByGroupId(vm.group_No)
					vm.attorneyGroupIds = _.map(groups,'groupId');
					vm.attorneyNameArray = _.map(groups,'attorneyName');
				}else{
					vm.attorneyGroupIds = [];
					vm.attorneyNameArray = [];
				}
				
				
				
				onItemSelectionDone();
				
			},

			onSelectAllGroupSelected : function(groupNum) {
				vm.onItemSelectionInProgress = true;
				var groups = filterByGroupId(groupNum);
				vm.attorneyGroupIds = [groupNum]
				vm.attorneyNameArray = _.map(groups,'attorneyName');
				
				onItemSelectionDone();
					
			}
			
			
	}

	
	$scope.setDashboardSettings = function(){
		dashboardSettingService.getDashboardSettingsBasedOnUserRole(vm).then(
				function(data) {
					data.dtOptions.initComplete = function() {
						dashboardSettingService.initCompleteFunc(vm)
					}

					vm.dtColumnDefs = data.dtColumnDefs;
					vm.dtOptions = data.dtOptions;
				});
	}

	$scope.redirectToCaseDocket = function (currentProtestInfo,role,a_No,caseAccessRequestStatus,docTypeId,fileInfoSubmissionDate){
		var caseInfObj = {};
		caseInfObj.currentProtestInfo = currentProtestInfo;
		if (DashboardData.userProfileInfo.role_id == "7"){
			caseInfObj.path = "admin-case-docketsheet";	
		}else{
			caseInfObj.path = "case-docketsheet";
		}
		caseInfObj.caseAccessRequestStatus = currentProtestInfo.caseAccessRequestStatus || caseAccessRequestStatus;
		caseInfObj.a_No = currentProtestInfo.a_No || a_No ;
		caseInfObj.docTypeId = currentProtestInfo.deniedIndicatingDocTypeId || docTypeId;
		caseInfObj.fileInfoSubmissionDate = currentProtestInfo.deniedDate || fileInfoSubmissionDate;
		caseInfObj.role = currentProtestInfo.role || role;
		dashboardDataService.checkCaseAccessRequestStatus(caseInfObj);
	}

	$scope.showPasswordExpiryWarning = function(){

		if (DashboardData.numberOfDaysLeftToExpirePwd 
				&& DashboardData.numberOfDaysLeftToExpirePwd <= 10){

			var bodyText = "Your password is going to expire in  " + DashboardData.numberOfDaysLeftToExpirePwd + " days. Do you want to update your password now?"

			var customAttr = {
					headerText : "Warning"	,
					bodyText : bodyText,
					modalType : "warning",
					actionType : "samepage",
					cancelBtnReq : "Y",
					cancelBtnActionType : "samepage",
					okAndCancelText : "Y",
					okBtnText : "OK",
					cancelBtnText : "Update later"
			}

			actionMessageSvc.showModal(customAttr).then(function(result){

				if (result.cancelBtnClicked != "Y"){
					localStorageService.set("userId", DashboardData.userProfileInfo.user_Id);
					userProfileViewSvc.changePasswordModal(DashboardData.userProfileInfo);
				}else{
					dashboardDataService.cancelPasswordExpiryWarning();
				}
			})
		}
	}
	
	
	
	function getDashboardData(viewType, start,ignoreLoadingBar) {
		var startLimit = start , endLimit = startLimit + 50;

		dashboardDataService.getDashboard(viewType, startLimit, endLimit,ignoreLoadingBar,vm).then(
				function(response) {

					if (response && response.protest_Info_List) {
						
						if (response.unjoinedProtestInfoList){
							vm.fullProtestInfoList = _.uniqBy(vm.fullProtestInfoList.concat(response.unjoinedProtestInfoList),function(item){
								
								return item.a_No;
							});	
						}
						
						vm.fullJoinedProtestInfoList = _.uniqBy(vm.fullJoinedProtestInfoList.concat(response.protest_Info_List),function(item){
							
							return item.a_No;
						});	
						
						getDashboardData(viewType, 0,ignoreLoadingBar)

					}else{
						$scope.filterDashboardResults()	
					}
					
					
					


				})
	}

	getDashboardData(vm.viewType ||  null, 0,true);	
	
	$scope.filterDashboardResults = function(){
		
		
		$scope.dashboardInfo.protest_Info_List = $filter('filter')(vm.fullJoinedProtestInfoList,function(eachProtestInfo){
			
			var ret = true;
			if (vm.attorneyNameArray && vm.attorneyNameArray.length){
				
				var attorneyName = eachProtestInfo.attorney_Name && eachProtestInfo.attorney_Name.split(","),
				lastName = attorneyName && attorneyName[0].toLowerCase().trim(), firstName = attorneyName && attorneyName[1].toLowerCase().trim();
				
				if (attorneyName){
					ret  = $filter('filter')(vm.attorneyNameArray,function(eachAttorneyName){
						return (eachAttorneyName.split(",")[0].toLowerCase().trim() == lastName 
								&& eachAttorneyName.split(",")[1].toLowerCase().trim() == firstName);
					});	
					
					if(ret && !ret.length){
						ret = false;
					}
					
				}
				
			
			}
			
			if (ret && vm.caseStatusFilterList && vm.caseStatusFilterList.length){
				
				ret  = $filter('filter')(vm.caseStatusFilterList,function(eachCaseStatus){
					return (eachProtestInfo.case_Status.toLowerCase().trim() 
							=== (eachCaseStatus.id && eachCaseStatus.id.toLowerCase()));
				});
			
				if(ret && !ret.length){
					ret = false;
				}
			}
			
			return ret;
		}); 
		
		
		localStorageService.set("dboardSelectedCaseStatus", vm.caseStatusFilterList);
		
		if (!vm.viewType ||  vm.viewType !="unassigned"){
			localStorageService.set("grpIds", vm.attorneyGroupIds)
		}else{
			localStorageService.remove("grpIds");
		}
		
	}
	
}
