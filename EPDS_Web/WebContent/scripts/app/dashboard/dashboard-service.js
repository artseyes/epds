//Dashboard Data service is used to retrieve Dashboard records for the user
(function() {
	'use strict';

	var serviceId = 'dashboardDataService';
	
	
	angular.module('epdsApp.dashboard').factory(
			serviceId,
			[ '$http', '$location', '$rootScope', 'userInfoService',
					'localStorageService','$httpParamSerializerJQLike',
					'modalService',
					'actionMessageSvc','$ocLazyLoad','navigationSvc','$uibModal','$filter','$injector','base64','$q', dashboardDataService ]);
	/* @ngInject */
	function dashboardDataService($http, $location, $rootScope,
			userInfoService, localStorageService, $httpParamSerializerJQLike, modalService, actionMessageSvc, $ocLazyLoad, navigationSvc, $uibModal, $filter,$injector,base64,$q) {

		var service = {
			getDashboard : getDashboard,
			checkCaseAccessRequestStatus : checkCaseAccessRequestStatus,
			cancelPasswordExpiryWarning : cancelPasswordExpiryWarning,
			removeCaseModal : removeCaseModal,
			toggleGlobalEmailPreferences: toggleGlobalEmailPreferences
		};

		return service;

		function getDashboard(dashboardType,startLimit,endLimit,ignoreLoader,vm) {
			
			var cancel = $q.defer();
			var params = {
					protestTableType : dashboardType
			}
			
			if (startLimit){
				params.startLimit = startLimit;
			}else{
				params.startLimit = 0;
			}
			
			if (endLimit){
				params.endLimit = endLimit;
			}else{
				params.endLimit = 50;
			}
			
			if (vm && vm.fullProtestInfoList){
					params.alreadyAvailableANums = 	_.map(vm.fullProtestInfoList,'a_No').join(",")
			}
			
			
			localStorageService.remove("caseDocketProtestInfo","isViewOnly","caseDocketa_No");
			
			if (vm && vm.caseStatusFilterList || localStorageService.get("dboardSelectedCaseStatus")) {
				params.caseStatusList =  _.map(vm && vm.caseStatusFilterList || localStorageService.get("dboardSelectedCaseStatus"),'id').join(",") || ["OPEN"].join(",");
			}
			
			if (vm && vm.attorneyGroupIds) {
				params.attorneyGroupIds =  _.uniq(vm.attorneyGroupIds);
				params.attorneyGroupIds = params.attorneyGroupIds.map(Number).join(",")
			}
			
			if (!vm || !vm.gaoUserList){
				params.isFullReloadReq = true;
			}else{
				params.isFullReloadReq = false;
			}

			 $http({
				url : '/epds/dashboard',
				method : 'POST',
				headers : {
					'Content-Type' : 'application/x-www-form-urlencoded'
				},
				data: $httpParamSerializerJQLike(_.omitBy(params, _.isNil)),
				ignoreLoadingBar : ignoreLoader || false,
				timeout: cancel.promise, 
			    cancel: cancel 
				
			})
					.then(
							function(data) {
						
								$rootScope.authenticated = true;
								
								if (params.isFullReloadReq){
								
									userInfoService
											.setUserInfo(data.data.userProfileInfo);
									userInfoService.getRoleId(data.data.role).then(function(roleId){
										var navigationObj ={
												navigationType : "dashboard",
												caseStatus : "N/A",
												roleId :  roleId
													
										} 
										navigationSvc.setListOfRoutesBasedOnRole(navigationObj);
									});
									
									if (data.data && data.data.role) {
										if (data.data.role === "GAO ATTORNEY") {
											data.data.hideOtherOptions = "AT"
										} else if (data.data.role === "GAO SUPERVISOR") {
											data.data.hideOtherOptions = "S"
										} else if (data.data.role
												.indexOf("AGENCY") >= 0) {
											data.data.hideOtherOptions = "AG"
										} else {
											data.data.hideOtherOptions = "P"
										}
									}
								}

								cancel.resolve(data.data);
								//return data.data;
							},
							function(error) {
								// need to reject so the controller isn't called, but new angular exception handler
								// calls the logging service with an unhandled exception. Tell it to ignore.
								cancel.reject(new Error('ignore'));
							});
			
			
			return cancel.promise;

		}
		
		function checkCaseAccessRequestStatus(caseInfObj){
			
			switch (caseInfObj.caseAccessRequestStatus) {
			
			case "P":
				var customAttr = {};
				if (caseInfObj.currentProtestInfo.caseAccessRequestType.toUpperCase().indexOf("INTERVENE") > -1){
						customAttr = {
							headerText : "Request Pending " + caseInfObj.currentProtestInfo.b_No,
							bodyText : "Your request to intervene is pending.  You will only have access to this case's docket if your request is granted by GAO.",
							modalType : "info",
							actionType : "",
						    cancelBtnReq : "N",
						    cancelBtnActionType : "",
						    caseInfObj :caseInfObj
						}
				}else if (caseInfObj.currentProtestInfo.caseAccessRequestType.toUpperCase().indexOf("APPEARANCE") > -1){
						customAttr = {
							headerText : "Request Pending " + caseInfObj.currentProtestInfo.b_No,
							bodyText : "Your notice of appearance is pending.  You will only have access to " +
									"this case's docket if your notice of appearance is acknowledged by GAO.",
							modalType : "info",
							actionType : "",
						    cancelBtnReq : "N",
						    cancelBtnActionType : "",
						    caseInfObj :caseInfObj
						}
				}
				actionMessageSvc.showModal(customAttr);
				break;
				
			case "D":
				var parentANo = caseInfObj.currentProtestInfo && caseInfObj.currentProtestInfo.parent_A_No;

				$ocLazyLoad.load(['jquery-datatables','angular-datatables','dashboard','account-update',
				                  
				                  'angular-xeditable','cds','file-info-view','request-to-intervene'],{serie: true, cache :false}).then(function() {
			        
			    	 var fileInfoViewSvc = $injector.get("fileInfoViewSvc");
			        
			    	 fileInfoViewSvc.loadFileInfoListWhenCaseAccessReqDenied(parentANo || caseInfObj.a_No, caseInfObj.fileInfoSubmissionDate ||  caseInfObj.deniedDate, caseInfObj.docTypeId || caseInfObj.deniedIndicatingDocTypeId, "Y").then(function(response){
							
							var customAttr = {};
							if (caseInfObj.currentProtestInfo.caseAccessRequestType.toUpperCase().indexOf("INTERVENE") > -1){
									customAttr = {
										headerText : "Request Rejected " + caseInfObj.currentProtestInfo.b_No,
										bodyText : "Your request to intervene is denied.  Please refer to the PDF file included here for additional information.",
										modalType : "error",
										actionType : "",
									    cancelBtnReq : "N",
									    cancelBtnActionType : "",
									    caseInfObj :caseInfObj,
									    fileInfo : response
									}
							}else if (caseInfObj.currentProtestInfo.caseAccessRequestType.toUpperCase().indexOf("APPEARANCE") > -1){
									customAttr = {
										headerText : "Request Rejected " + caseInfObj.currentProtestInfo.b_No,
										bodyText : "Your appearance has not been acknowledged by GAO.  Please refer to the PDF file included here for additional information.",
										modalType : "error",
										actionType : "",
									    cancelBtnReq : "N",
									    cancelBtnActionType : "",
									    caseInfObj :caseInfObj,
									    fileInfo : response
									}
							}
								
								actionMessageSvc.showModal(customAttr);
						 });
				});
				
				
				break;

			default:
				if (caseInfObj && !caseInfObj.isCaseDocketLoaded){
					localStorageService.remove("gc_A_no","gc_role");
					localStorageService.set("caseDocketa_No",caseInfObj.a_No);
					$location.path("/"+ caseInfObj.path + "/" + base64.urlencode(caseInfObj.a_No));	
				}
				
				break;
			}
		}

		
		function cancelPasswordExpiryWarning(){
			
			
			return $http({
				url : '/epds/cancelPasswordExpiryWarning',
				method : 'POST',
				headers : {
					'Content-Type' : 'application/x-www-form-urlencoded'
				},
				
			}).then(function(data) {
								
								
							},
							function(error) {
								
								return error;
							});
			
		}
		
		function toggleGlobalEmailPreferences(yOrN){
			
			
			return $http({
				url : '/epds/toggle-email-preferences/' + yOrN,
				method : 'GET',
				headers : {
					'Content-Type' : 'application/x-www-form-urlencoded'
				},
				
			}).then(function(data) {
								
								return data.data;
							},
							function(error) {
								
								return error;
							});
			
		}
		
		function removeCaseModal() {
			// had a case where a refresh on a non-dashboard page wouldn't have the removeCaseCtrlModalCtrl loaded
			$ocLazyLoad.load(['admin-dashboard'],{serie: true, cache :false}).then(function() {
				$uibModal.open({
					templateUrl : 'scripts/app/admin/remove-cases/removeCase.htm',
					controller : removeCaseCtrlModalCtrl,
					animation : true,
					size : 'md',
					resolve : {
						userInfo : function() {
							return null;
						}
					},
					keyboard : true,
					backdrop : 'static'
				}).result.catch(angular.noop);
			});
		}
		
		function _update(srcObj, destObj) {
			  for (var key in destObj) {
			    if(destObj.hasOwnProperty(key) && srcObj.hasOwnProperty(key)) {
			      destObj[key] = srcObj[key];
			    }
			  }
			}
	}
})();

/* This service is used to set  Dashboard Setting based on user Role */
/*
 * 
 * Come back to this amer we are using localstorageService.get(role) replace that with vm.role
*/
(function() {
	'use strict';

	var serviceId = 'dashboardSettingService';

	
	angular.module('epdsApp.dashboard').factory(
			serviceId,
			[ 'DTColumnDefBuilder', 'DTOptionsBuilder', '$q',
					'localStorageService','$rootScope', dashboardSettingService ]);
	/* @ngInject */
	function dashboardSettingService(DTColumnDefBuilder, DTOptionsBuilder, $q,
			localStorageService,$rootScope) {

		var service = {
			getDashboardSettingsBasedOnUserRole : getDashboardSettingsBasedOnUserRole,
			initCompleteFunc : initCompleteFunc
		};

		return service;

		
		function getDashboardSettingsBasedOnUserRole(vm) {

			var showOrHideColumnFilter = [];
			var defaultOrderColumnIndex = [ 1 ];

			var langOption = {

				"sEmptyTable" : "No data available in table",
				"sInfo" : "Showing _START_ to _END_ of _TOTAL_ entries",
				"sInfoEmpty" : "Showing 0 to 0 of 0 entries",
				"sInfoFiltered" : "(filtered from _MAX_ total entries)",
				"sInfoPostFix" : "",
				"sInfoThousands" : ",",
				"sLengthMenu" : "Show _MENU_ entries",
				"sLoadingRecords" : "Loading...",
				"sProcessing" : "Processing...",
				"sSearch" : "Filter Records :",
				"oPaginate" : {
					"sFirst" : "First",
					"sLast" : "Last",
					"sNext" : "Next",
					"sPrevious" : "Previous"
				},
				"oAria" : {
					"sSortAscending" : ": activate to sort column ascending",
					"sSortDescending" : ": activate to sort column descending"
				}

			}

			if (vm.role === "GAO SUPERVISOR") {

				langOption['sZeroRecords'] = "There are no open cases assigned to your group.  Please select filter by group to search for another group's cases.";

			} else {
				langOption['sZeroRecords'] = "Zero records found";

				showOrHideColumnFilter = [ 6, 7 ];
			}

			
			if (vm.viewType === "unassigned") {

				showOrHideColumnFilter = [ 0, 6, 7 ];
				vm.dtColumnDefs = [
						DTColumnDefBuilder.newColumnDef(0).notVisible().notSortable(),
						//DTColumnDefBuilder.newColumnDef(0).notSortable(),
						DTColumnDefBuilder.newColumnDef(4).notSortable(),
						DTColumnDefBuilder.newColumnDef(5).notSortable(),
						DTColumnDefBuilder.newColumnDef(6).notVisible(),
						DTColumnDefBuilder.newColumnDef(7).notVisible(), ];

			} else if (vm.viewType === "assigned") {
				vm.dtColumnDefs = [
				        /*DTColumnDefBuilder.newColumnDef(0).withOption('sWidth', '5%'),           
						DTColumnDefBuilder.newColumnDef(1).notSortable().withOption('sWidth', '10%'),
						DTColumnDefBuilder.newColumnDef(2),
						DTColumnDefBuilder.newColumnDef(3),
						DTColumnDefBuilder.newColumnDef(4),*/
						DTColumnDefBuilder.newColumnDef(4).notSortable(),
						DTColumnDefBuilder.newColumnDef(5).notSortable(),
						/*DTColumnDefBuilder.newColumnDef(7).withOption('sWidth', '10%'),
						DTColumnDefBuilder.newColumnDef(8).withOption('sWidth', '10%'),
						DTColumnDefBuilder.newColumnDef(9).withOption('sWidth', '10%'),
						DTColumnDefBuilder.newColumnDef(10).withOption('sWidth', '10%'),*/];
			} else if(vm.role !== "GAO SUPERVISOR"){
				vm.dtColumnDefs = [ DTColumnDefBuilder.newColumnDef(6)
									.notSortable() ];
			}

			vm.dtOptions = DTOptionsBuilder
				.newOptions()
				// Datatables DOM documentation: https://datatables.net/reference/option/dom
				// AngularJS datatables withDOM documentation: https://surgbook.net/node_modules/angular-datatables/#!/overrideBootstrapOptions
				.withDOM("<'row'lfr>tip")
				.withBootstrap()
				.withBootstrapOptions({
							ColVis : {
								classes : {
									masterButton : 'btn btn-primary tweaked-margin-left'
								}
							},
							pagination : {
								classes : {
									ul : 'pagination pagination-sm '
								}
							}
						})
				.withButtons([{
					extend: 'colvis',
					text: 'Show/Hide Columns',
					// columns: ':gt(0)',
					columns: function ( idx, data, node ) {
						var isVisible = $.inArray( idx, showOrHideColumnFilter ) !== -1;
						return !isVisible;
					},
					init: function(api, node, config) {
						// Remove default ColVis classes
						$(node).removeClass('btn-default');
						$(node).removeClass('buttons-collection');
						$(node).removeClass('buttons-colvis');

						// The DataTables/BS3 combo has an <li> with tabindex="0" and an <a> with an href="#"
						// Normally the user clicks and interacts with only the <li>. But a keyboard user can tab and navigate
						// to both and hitting enter on the <a> does a double activation on the <a> and <li>
						// Need to dynamically remove the href so both mouse and keyboard users only activate the <li>
						$(node[0]).on('click', function() {
							$('.dt-button-collection .buttons-columnVisibility').each(function() {
								var $li = $(this);
								var $a = $li.find('a');
								if ( $a.attr('href') ) {
									$a.removeAttr("href");
								}
							});
						});
					},
					// TODO: Remove this code or enable it to add the checkboxes next to the names in the dropdown of the ColVis button
					// init: function ( dt, node, config ) {
					//
					// 	// The dropdown only exists on the DOM once we click on the node, so we need to add an onclick
					// 	// to the node so that when it is open we can modify the dropdown.
					// 	$(node[0]).on('click', function(){
					//
					// 		// Now we can loop over all the items in the dropdown and add a checkbox next to each of them
					// 		$('.dt-button-collection .buttons-columnVisibility').each(function(){
					// 			var $li = $(this);
					// 			var $cb = $('<input>', {
					// 					type:'checkbox',
					// 					style:'margin: 0 .25em 0 0; vertical-align: middle;'}
					// 				).prop('checked', $(this).hasClass('active') );
					//
					// 			// Before appending a checkbox, we need to make sure that there isn't already one there
					// 			// otherwise we get a checkbox each time we open up the dropdown.
					// 			if ($li.has('input').length === 0) {
					// 				$li.find('a').prepend($cb);
					// 			} else {
					// 				$li.find('input').prop('checked', $(this).hasClass('active') );
					// 			}
					// 		});
					//
					// 		// We need to add an on click listener to each li in the dropdown, this way if the user
					// 		// doesn't click directly on the checkbox, we can toggle it's checked state.
					// 		$('li.buttons-columnVisibility').on('click', function(){
					// 			var $li = $(this).closest('li');
					// 			var	$cb = $li.find('input:checkbox');
					// 			$cb.prop('checked', $li.hasClass('active') );
					// 		});
					// 	});
					// },
					className : 'btn btn-primary colvis-override'
				}])
				// .withColumnFilter()
				// .withColVis()
				// Add a state change function
				// .withColVisStateChange(function(iColumn, bVisible) {
				// 	console.log($rootScope.userProfileInfo)
				//     console.log('The column' + iColumn + ' has changed its status to ' + bVisible)
				//     })
				// .withColVisOption('aiExclude', showOrHideColumnFilter)
				.withOption('searchHighlight', true)
				.withOption('stateSave', true)
				.withOption('order', [ 2, 'asc' ])
				.withLanguage(langOption)
				.withOption('lengthMenu', [25, 50, 100, 150])
				.withDisplayLength(25)
				.withOption('initComplete', function(settings) {});

			return $q.when(vm);

		}

		function initCompleteFunc(vm) {
			
			var groupColumnNum = 6, caseStatusColumnNum = 9;
			

			
			if (vm.role == "AGENCY ADMIN"){
				caseStatusColumnNum =10;
			}
			/*debugger
			if (vm.role == "GAO ADMIN"){
				
				caseStatusColumnNum =6;
			}*/
			
			
			if (vm.protest_Info_List != null
					&& vm.protest_Info_List.length <= 25) {
				$(".dataTables_paginate").hide();
			}
            
			
			/*vm.dashboardProtestTable = $('#protestTable').dataTable().yadcf([ {
				column_number : groupColumnNum,
				filter_type : "multi_select",
				filter_container_id : "groupfilter"
			},{
				column_number : groupColumnNum + 1,
				filter_type : "multi_select",
				filter_container_id : "groupfilter"
			},{

				column_number : caseStatusColumnNum,
				filter_type : "multi_select",
				filter_container_id : "caseStatusfilter"
			
			} ]);*/
			
			var groupArr = [];
            angular.forEach(vm.selectedGroup, function(value, key) {
            	groupArr.push(value.id)
    			
    		})
    		
    		var caseStatusArr = [];
            angular.forEach(vm.selectedCaseStatus, function(value, key) {
            	caseStatusArr.push(value.id)
    			
    		})
			/*yadcf.exFilterColumn(vm.dashboardProtestTable,[ [groupColumnNum, groupArr],[caseStatusColumnNum, caseStatusArr] ],
                    true);*/

			/*expanding supplemental protest when filtering dashboard records*/

			$('#protestTable').on('search.dt', function(e) {

				if (e.which == 8 || e.which == 46) {
					return false;
				}
				var value = $('.dataTables_filter input').val();
				var protestTable = $('#protestTable').DataTable();


				protestTable.on('draw', function() {
					var body = $(protestTable.table().body());
					body.unhighlight();
					body.highlight(protestTable.search());
				});

			});

			return vm;
		}
	}
})();
