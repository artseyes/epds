

angular.module('epdsApp.caseDocketSheet')
/* @ngInject */
.run(function(editableOptions, editableThemes) {
	editableOptions.theme = 'bs3';

	editableThemes['bs3'].submitTpl = '<button type="submit" class="btn btn-sm btn-success">'
			+ '<span>Save</span></button>';
	editableThemes['bs3'].cancelTpl = '<button type="button" class="btn btn-sm btn-warning" ng-click="$form.$cancel()">'
			+ '<span>Cancel</span></button>';
});
(function() {
	'use strict';

	var serviceId = 'caseDocketDataSvc';
	
	angular.module('epdsApp.caseDocketSheet').factory(
			serviceId,
			[ '$location', '$http', '$filter', 'DTColumnDefBuilder',
				'$resource', '$timeout', '$rootScope', 'DTOptionsBuilder',
				'$route', '$routeParams', '$uibModal', 'modalService','localStorageService','navigationSvc','userInfoService',
				'$httpParamSerializerJQLike','$q','actionMessageSvc','Idle','authenticationService','base64','$window','$injector','$ocLazyLoad',caseDocketDataSvc ]);
	/* @ngInject */
	function caseDocketDataSvc($location, $http, $filter, DTColumnDefBuilder,
			$resource, $timeout, $rootScope, DTOptionsBuilder,
			$route, $routeParams, $uibModal, modalService,localStorageService,
			navigationSvc,userInfoService,$httpParamSerializerJQLike,$q,actionMessageSvc,Idle,authenticationService,base64,$window,$injector,$ocLazyLoad) {

		var service = {
				getCaseDocketInfo : getCaseDocketInfo,
				getCaseDocketSheetSettings : getCaseDocketSheetSettings,
				initCompleteFunc : initCompleteFunc,
				getListOfDataForEditing : getListOfDataForEditing,
				getListOfAttorneys : getListOfAttorneys,
				changeCaseStatusToComplete : changeCaseStatusToComplete,
				updateProtestInfo : updateProtestInfo,
				updateCaseDocketSheetInfo : updateCaseDocketSheetInfo,
				addCommentsModal : addCommentsModal,
				setCaseDocketEmailPreferences : setCaseDocketEmailPreferences,
				addAttorneyNotesModal : addAttorneyNotesModal,
				editAgencyName : editAgencyName,
				join_UnjoinCasesDialogueBox : join_UnjoinCasesDialogueBox,
				addMinuteEntryModal : addMinuteEntryModal,
				updateAgencyInfo : updateAgencyInfo,
				addMinuteEntryFunc : addMinuteEntryFunc,
				updateEmailPreferences : updateEmailPreferences,
				addNotes : addNotes,
				addCommentsFunc : addCommentsFunc, 
				redirectToCaseDocketFileInfo : redirectToCaseDocketFileInfo,
				join_unJoinChild_BNumber_To_Parent_BNumber : join_unJoinChild_BNumber_To_Parent_BNumber,
				validateJoinUnjoinCases : validateJoinUnjoinCases,
				redirectToConsolidatedCaseDocketSheet : redirectToConsolidatedCaseDocketSheet,
				validateDMInfo : validateDMInfo,
				updateDMInfo :updateDMInfo,
				addDMInfo :addDMInfo,
				verifyDMInfo : verifyDMInfo,
				complete : completeOption,
				pendingVerification : pendingVerificationOption,
				verify: verifyDMOption,
				enterDM : enterDM,
				downloadOfflineCds : downloadOfflineCds,
				openNewTab : authenticationService.openNewTab,
				updateTabLocation : authenticationService.updateTabLocation,
				openBlob : authenticationService.openBlob,
				
				
				
				
		};

		return service;

		function getCaseDocketInfo(aNum) {
			
			function getAnum(aNum){
				var a_Num  = aNum || localStorageService.get("gc_A_no") || localStorageService.get("caseDocketa_No");
				try {
					a_Num = $window.atob(a_Num)
				} catch(e) {
					a_Num = aNum;
				   console.log("it is not a base 64 encoded string")
				}
				
				return a_Num;
			}
			
			var aNum = getAnum(aNum);
			localStorageService.set("caseDocketa_No",aNum);
			
			
			var params = {
				"a_No" : aNum
			}
			return $http({
				url : '/epds/casedocketsheet',
				method : 'POST',
				headers : {
					'Content-Type' : 'application/x-www-form-urlencoded'
				},
				data: $httpParamSerializerJQLike(_.omitBy(params, _.isNil)),
			})
			.then(
				function(data) {
					var response  = data && data.data;

					if (response && response.protestInfo && response.protestInfo.caseAccessRequestType){
						$ocLazyLoad.load(['jquery-datatables','angular-datatables','dashboard','account-update'],
							{serie: true, cache :true}).then(function() {

								var caseInfoObj = {};

								angular.extend(caseInfoObj,response,{
									currentProtestInfo : response.protestInfo,
									isCaseDocketLoaded : true
								},response.protestInfo);


								var dashboardDataService = $injector.get("dashboardDataService");
								dashboardDataService.checkCaseAccessRequestStatus(caseInfoObj);
								// just came back from displaying a error modal while routing to dashboard
								// inform app.js $routeChangeSuccess to not dismiss it
								$rootScope.keepModal = true;
							});

						$location.path("/dashboard").replace();
						return "";
					}

					if (response && response.caseDocketNotFound){
						$rootScope.sessionExpired = true;
						$rootScope.redirectMessage = "The Case Docket that you are looking for is not found. "
						localStorageService.remove("caseDocketa_No");
						localStorageService.remove("gc_A_no");
						localStorageService.remove("gc_role");
						$location.path("/login").replace();
					}

					$rootScope.authenticated = true;
					userInfoService.setUserInfo(response.user_Info);

					var navigationObj = {
							navigationType : "caseDocketSheet",
							caseStatus : "N/A",
							roleId :  response.protestInfo.roleId,
							protestInfo : response.protestInfo,
							isViewOnly : response.protestInfo.viewOnly
					};
					navigationSvc.setListOfRoutesBasedOnRole(navigationObj);

					return _getCaseDocketSheetData(response);

				},
				function(error) {
					return error;
				}
			);

		}
		
		function redirectToConsolidatedCaseDocketSheet (path,a_No){
			
			localStorageService.set("caseDocketa_No",a_No);
			$route.reload();
			$location.path(path +"/" + base64.urlencode(a_No));
			
		}
		function getCaseDocketSheetSettings (vm){
			

			vm.dtColumnDefs = [
					DTColumnDefBuilder.newColumnDef(1).notSortable(),
					DTColumnDefBuilder.newColumnDef(2).notSortable(),
					DTColumnDefBuilder.newColumnDef(5).notSortable(),
					DTColumnDefBuilder.newColumnDef(6).notSortable() ];
			
			/*vm.fileInfoList = response.fileInfoList;*/
			vm.dtOptions = DTOptionsBuilder.newOptions()
									.withLanguage({ "sSearch" : "Filter Records : " })
					.withOption('initComplete', function(settings) {});
			
			
			return $q.when(vm);
		
		}
		
		function initCompleteFunc(vm) {
			

			if (($filter('unique')
					(vm.fileInfoList,"originalSubmissionDate").length) <= 10) {
				$(".dataTables_paginate").hide();
			}
		
			return $q.when(vm);
		}
		
		function getListOfDataForEditing(vm){
			
			vm.caseStatuses = [ {
				value : 'OPEN',
				text : 'OPEN'
			}, {
				value : 'CLOSED',
				text : 'CLOSED'
			}, ];
			vm.protectiveOrder = [ {
				value : 'Y',
				text : 'Y'
			}, {
				value : 'N',
				text : 'N'
			}, ];

			vm.caseTypes = [ {
				value : 'PROTEST',
				text : 'PROTEST'
			},{
				value : 'SUPPLEMENTAL',
				text : 'SUPPLEMENTAL'
			}, {
				value : 'RECONSIDERATION',
				text : 'RECONSIDERATION'
			}, {
				value : 'ENTITLEMENT',
				text : 'ENTITLEMENT'
			}, {
				value : 'COST-CLAIM',
				text : 'COST-CLAIM'
			}, ];
			
			vm.companyStatuses = [ {
				value : 'APPEAL',
				text : 'APPEAL'
			},{
				value : 'APPEAL RECON',
				text : 'APPEAL RECON'
			},{
				value : 'DEBT',
				text : 'DEBT'
			},{
				value : 'EAJA COST',
				text : 'EAJA COST'
			},{
				value : 'FCIC',
				text : 'FCIC'
			},{
				value : 'FCIC RECON',
				text : 'FCIC RECON'
			},{
				value : 'FEMA',
				text : 'FEMA'
			},{
				value : 'FMCSA',
				text : 'FMCSA'
			},{
				value : 'ISDA',
				text : 'ISDA'
			},{
				value : 'ISDA RECON',
				text : 'ISDA RECON'
			},{
				value : 'OTHER',
				text : 'OTHER'
			},{
				value : 'PETITION',
				text : 'PETITION'
			},{
				value : 'RATE',
				text : 'RATE'
			},{
				value : 'RELOCATION',
				text : 'RELOCATION'
			},{
				value : 'TRAVEL',
				text : 'TRAVEL'
			},{
				value : 'TRR RECON',
				text : 'TRR RECON'
			}];
			return $q.when(vm);
		}
		
		function getListOfAttorneys(){
			
		return $http({
				url : '/epds/get-attorney-list',
				method : 'GET',
				headers : {
					'Content-Type' : 'application/json'
				},
			}).then(function(data) {
									
									return data.data;

							},
							function(error) {
								
								return error;
							});

		}
		
		function changeCaseStatusToComplete(protestInfo){
			
			return $http({
					url : '/epds/case-completed',
					method : 'POST',
					headers : {
						'Content-Type' : 'application/json'
					},
					params : {
						aNum : protestInfo.a_No
					}	
				})
						.then(
								function(data) {
									
										return data.data;

								},
								function(error) {
									console
											.log("Error occured when completing the case"
													+ JSON.stringify(error));
									return error;
								});

			}
		
    function validateJoinUnjoinCases(bNum,parentBnum){
			
    	var params = {
					b_Num :bNum,
					parentBnum : parentBnum
			}
			return $http({
					url : '/epds/validate-join',
					method : 'POST',
					headers : {
						'Content-Type' : 'application/x-www-form-urlencoded'
					},
					data: $httpParamSerializerJQLike(_.omitBy(params, _.isNil)),
					ignoreLoadingBar: true
				})
						.then(
								function(data) {
										
									return data.data;

								},
								function(error) {
									
									return error;
								});

			}
		
		function updateProtestInfo (oldValue, typeOfchange,newValue,vm) {


			/* Moved the Var here 04/21/2022  */
			if (typeOfchange === "Attorney Info") {
				vm.getselectedAttorneyUserInfoFromGAO_userInfoList = $filter('filter')(vm.attorneyInfoList, {
					user_Id : newValue,
				});

				vm.attorneyInfo = vm.getselectedAttorneyUserInfoFromGAO_userInfoList[0]


			}

			/* Commenting Out BNumber Check 04/21/2022  */
/*
			if (typeOfchange ===  "B Number") {
				var b_No = newValue,
					checkIfThisBNumberStartWithPrefix = $filter("startsWith")(b_No, "B-", true),
					bNumBeforeDecimalIfPresent = b_No.split(".")[0],
					bNumberAfterRemovingPrefixAndSuffix = bNumBeforeDecimalIfPresent.split("-")[1] || b_No,
			lengthOfbNumberAfterRemovingPrefixAndSuffix = bNumberAfterRemovingPrefixAndSuffix && bNumberAfterRemovingPrefixAndSuffix.length;


			/* Moved '}' upto to close off the If Statement 04/21/2022  */

/*
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
					
					return;
				}
*/
				/*   if (!checkIfThisBNumberStartWithPrefix){
					b_No = "B-" + b_No
				}
				*/
				

				/*   if (!b_No.split(".")[1]){
					b_No += ".1"
				}
                */
			/* Commenting Out BNumber Check 04/21/2022  */

			/*
			newValue  = b_No;
			}
			*/


			/* Moved '}' upto close off the If Statement 04/21/2022  */


			/* Move This two lines down 04/21/2022   newValue  = b_No;  */



			var params =  {
				oldValue : (typeof oldValue != 'undefined') ? oldValue : "null",
				newValue : newValue,
				newValue2 : vm.attorneyInfo.group_No,
				aNum : vm.protestInfo.a_No
			}

			return $http({
						url : '/epds/change-protest_Info-attribute/' + typeOfchange,
						method : 'POST',
						headers : {
							'Content-Type' : 'application/x-www-form-urlencoded'
						},
						data: $httpParamSerializerJQLike(_.omitBy(params, _.isNil)),
					}).then(
							function(data) {
								
								var bodyText = ' You have  successfully updated '+ typeOfchange + ".";
								var customAttr = {
										headerText : "Success"	,
										bodyText : bodyText ,
										modalType : "success",
										actionType : "",
									    cancelBtnReq : "N",
									    cancelBtnActionType : ""
									}
								
								if (data.data && data.data.isBNumberExists){
									_checkIfBNumbersExists(data.data,customAttr);
								}else{
									actionMessageSvc.showModal(customAttr).then(function(result){
										if (typeOfchange.indexOf("Status") >= 0){
											$route.reload();
										}
									})
								}
								
								
							
									return data.data;

							},
							function(error) {
								
								return error;
							});
		
		}
		
		function _checkIfBNumbersExists(data,customAttr){
			
			if (data.isBNumberExists === "Y"){
				var bodyText = 'The CBCA Number you have entered already exists.  Please check the CBCA Number and try again.';
				customAttr = {
						headerText : "Error"	,
						bodyText : bodyText ,
						modalType : "error",
						actionType : "",
					    cancelBtnReq : "N",
					    cancelBtnActionType : ""
					}
				
				actionMessageSvc.showModal(customAttr);
			}else{
				actionMessageSvc.showModal(customAttr).then(function(){
					$route.reload();	
				});
			}
			
			
		}
		function updateCaseDocketSheetInfo(fileId,typeOfchange, newValue, submitterRole,vm){
			
			
			if (typeOfchange === "Submission date") {
				
				moment.tz.add('America/New_York|EST EDT|50 40|01010101010101010101010|1BQT0 1zb0 Op0 1zb0 Op0 1zb0 Op0 1zb0 Op0 1zb0 Op0 1zb0 Rd0 1zb0 Op0 1zb0 Op0 1zb0 Op0 1zb0 Op0 1zb0|21e6');
				newValue = moment(newValue,"ddd MMM DD YYYY HH:mm:ss [GMT]ZZ").tz(
				"America/New_York").format('MMM DD YYYY HH:mm:ss z')

			} else if (typeOfchange == "type-of-document") {

				/*var  filteredDocInfoList = $filter('where')(vm.docInfoList, {
															doc_Type_Id : newValue
													});
				console.log(filteredDocInfoList)
				
				newValue = vm.getDocIdFromDocTypeDesc[0].doc_Type_Id;*/

			}

			var params = {
				file_Id : String(fileId),
				newValue : String(newValue),
				aNum :vm.protestInfo.a_No
			}
			return $http({
						url : '/epds/change-file-attribute/'+ typeOfchange,
						method : 'POST',
						headers : {
							'Content-Type' : 'application/x-www-form-urlencoded'
						},
						data: $httpParamSerializerJQLike(_.omitBy(params, _.isNil)),
					}).then(
							function(data) {
								
								var customModalOptions = {
										headerText : 'success',
										bodyText : ' You have  successfully updated '
												+ typeOfchange,
										closeButtonText : 'OK',
										messageType : "success"
									};

									modalService.showModal({},
											customModalOptions).then(
											function(result) {
											});
								},
								
							function(error) {
								
								return error;
							});
					
		
		}
		
		 function addCommentsModal(a_No, doc_Type_Id, file_id,
				comments,vm) {
			 /*Idle.watch();*/
			 $uibModal
				.open({
					templateUrl : 'views/dialogue-box-html-templates/add-notes-minute-entry.html',
					controller : addCommentsModalInstanceCtrl,
					resolve : {
						fileId : function() {
							return file_id;
						},
						comments : function() {
							return comments;
						}
					},
					animation : true,
					size : 'md',
				}).result.catch(angular.noop);

		}

		function setCaseDocketEmailPreferences(protestInfo) {
			/*Idle.watch();*/
			$uibModal
					.open({
						title: 'Email Preferences',
						templateUrl : 'views/dialogue-box-html-templates/email-preferences.html',
						controller : setEmailPreferencesModalInstanceCtrl,
						animation : true,
						resolve : {
							protestInfo : function() {
								return protestInfo;
							}
						},
						size : 'md',
					}).result.catch(angular.noop);

		}

	function addAttorneyNotesModal (a_No, doc_Type_Id,
				file_id, comments,vm) {

		/*Idle.watch();*/
		$uibModal
					.open({
						templateUrl : 'views/dialogue-box-html-templates/add-notes-minute-entry.html',
						controller : addAttorneyNotesModalInstanceCtrl,
						resolve : {
							fileId : function() {
								return file_id;
							},
							comments : function() {
								return comments;
							}
						},
						animation : true,
						size : 'md',
					}).result.catch(angular.noop);

		}



	function editAgencyName(vm) {
		/*Idle.watch();*/
		$uibModal.open({
					templateUrl : 'views/dialogue-box-html-templates/agency-drop-down-dialog.html',
					controller : editAgencyNameModalInstanceCtrl,
					resolve : {
						
						oldValue : function() {
							return vm.protestInfo.agency_Name;
						},
						
						protestInfo : function(){
							return vm.protestInfo;
						}
						
					},
					animation : true,
					size : 'lg',
				}).result.catch(angular.noop);

		}

	function join_UnjoinCasesDialogueBox(vm) {

		/*Idle.watch();*/
		$uibModal
				.open({
					templateUrl : 'views/dialogue-box-html-templates/join-unjoin-cases.html',
					controller : join_UnjoinCasesModalInstanceCtrl,
					resolve : {
						protestInfo : function() {
							return vm.protestInfo;
						},
					},
					animation : true,
					size : 'md',
				}).result.catch(angular.noop);

		}

		function addMinuteEntryModal (vm) {

			$uibModal
					.open({
						templateUrl : 'views/dialogue-box-html-templates/add-notes-minute-entry.html',
						controller : addMinuteEntryModalInstanceCtrl,
						resolve : {
							protestInfo : function() {
								return vm.protestInfo;
							},
						},
						animation : true,
						size : 'md',
					}).result.catch(angular.noop);

		}
		
		function updateAgencyInfo(oldValue,tier_1_AgencySelectedOption,tier_2_AgencySelectedOption,aNum){
			
			if (tier_1_AgencySelectedOption && tier_1_AgencySelectedOption.agency_Id == 0){
				
				var customAttr = {
						headerText : "Error"	,
						bodyText : "Please select the Agency",
						modalType : "error",
						actionType : "",
					    cancelBtnReq : "N",
					    cancelBtnActionType : ""
					}
				
				actionMessageSvc.showModal(customAttr);
				
				return;
			}
			
			
			var params = {
				oldValue : oldValue,
				aNum:aNum,
				newValue : "null",
				agency_tier1 : tier_1_AgencySelectedOption && tier_1_AgencySelectedOption.agency_Id,
				agency_tier2 : tier_2_AgencySelectedOption && tier_2_AgencySelectedOption.agency_Id

			}
			
			return $http({
					url : '/epds/change-protest_Info-attribute/Agency Name',
					method : 'POST',
					headers : {
						'Content-Type' : 'application/x-www-form-urlencoded'
					},
					data: $httpParamSerializerJQLike(_.omitBy(params, _.isNil)),
					
			}).then(
					function(data) {
						
						return data.data;
					},
						
					function(error) {
						
						return error;
					});
		}
		
		function addMinuteEntryFunc(comments,protestInfo){
			var params = {
				"attorney_note" : comments,
				'protestId': protestInfo.a_No
			}
			return $http({
				url : '/epds/add-attachments/minute-entry',
				method : 'POST',
				headers : {
					'Content-Type' : 'application/x-www-form-urlencoded'
				},
				data: $httpParamSerializerJQLike(_.omitBy(params, _.isNil)),
		}).then(
				function(data) {
					
					return data.data;
				},
					
				function(error) {
					
					return error;
				});
		}
		
		
		function updateEmailPreferences(val,aNum){
			
			
		return $http({
				url : '/epds/update-email-preferences',
				method : 'GET',
				headers : {
					'Content-Type' : 'application/json'
				},
				params : {
					value : val,
					aNum : aNum
				}
		}).then(
				function(data) {
					
					return data.data;
				},
					
				function(error) {
					
					return error;
				});
		}
		
		
		function addNotes(fileId,comments){
			
			var params = {
				fileId : fileId,
				note : comments
			}
			
			return $http({
						url : '/epds/add-attorney-note',
						method : 'POST',
						headers : {
							'Content-Type' : 'application/x-www-form-urlencoded'
						},
						data: $httpParamSerializerJQLike(_.omitBy(params, _.isNil)),
				}).then(
						function(data) {
							
							return data.data;
						},
							
						function(error) {
							
							return error;
						});
		}
		
    function addCommentsFunc(fileId,comments){
			
			var params = {
					file_Id : String(fileId),
					newValue : String(comments),
				}
			return $http({
						url : '/epds/change-file-attribute/comments',
						method : 'POST',
						headers : {
							'Content-Type' : 'application/x-www-form-urlencoded'
						},
						data: $httpParamSerializerJQLike(_.omitBy(params, _.isNil)),
				}).then(
						function(data) {
							
							return true;
						},
							
						function(error) {
							
							return error;
						});
		}




		function join_unJoinChild_BNumber_To_Parent_BNumber(listOfBNumbers,typeOfAction,aNum){
			
			console.log($filter("join")(listOfBNumbers,","), JSON.stringify(listOfBNumbers))
			
			var params = {
				oldValue : "null",
				newValue : "null",
				aNum:aNum,
				listOfBNumbers : $filter("join")(listOfBNumbers,","),
			}
			
			 if (typeOfAction === "joined") {
					var pathVariable = "joinCases"
				} else if (typeOfAction === "unjoined") {
					var pathVariable = "unJoinCases"
				}
			
			return $http({
						url : '/epds/change-protest_Info-attribute/' + pathVariable,
						method : 'POST',
						headers : {
							'Content-Type' : 'application/x-www-form-urlencoded'
						},
						data: $httpParamSerializerJQLike(_.omitBy(params, _.isNil)),
					}).then(
							function(data) {
								
								return data.data;
							},
								
							function(error) {
								console
										.log("Error occured when retrieving list of parties "
												+ JSON.stringify(error));
								return error;
							});
		}
		
		
		
		
		function validateDMInfo(dmNumber, aNum){
			
			var params = {
					id : dmNumber,
					aNum : aNum
				}
			return $http({
						url : '/epds/validateDmInfo',
						method : 'POST',
						headers : {
							'Content-Type' : 'application/x-www-form-urlencoded'
						},
						data: $httpParamSerializerJQLike(_.omitBy(params, _.isNil)),
						ignoreLoadingBar: true
				}).then(
						function(data) {
							
							return data.data;
						},
							
						function(error) {
							console
									.log("Error occured when validating DM Info "
											+ JSON.stringify(error));
							return error;
						});
		}
		
		function updateDMInfo(protestInfo,dmNumber){
				
				var params = {
						aNum : protestInfo.a_No,
						id : dmNumber,
					}
				return $http({
							url : '/epds/updateDmInfo',
							method : 'POST',
							headers : {
								'Content-Type' : 'application/x-www-form-urlencoded'
							},
							data: $httpParamSerializerJQLike(_.omitBy(params, _.isNil)),
					}).then(
							function(data) {
								
								return data.data;
							},
								
							function(error) {
								console
										.log("Error occured when updating DM Info "
												+ JSON.stringify(error));
								return error;
							});
			}
		
		function addDMInfo(protestInfo,dmNumber){
					
			var params = {
					aNum : protestInfo.a_No,
					id : dmNumber,
				}
					return $http({
								url : '/epds/addDMInfo',
								method : 'POST',
								headers : {
									'Content-Type' : 'application/x-www-form-urlencoded'
								},
								data: $httpParamSerializerJQLike(_.omitBy(params, _.isNil)),
						}).then(
								function(data) {
									
									return data.data;
								},
									
								function(error) {
									
									return error;
								});
				}
		
		function verifyDMInfo(protestInfo,dmNumber){
					
					var params = {
							aNum : protestInfo.a_No,
							id : dmNumber,
						}
					return $http({
								url : '/epds/verifyDmInfo',
								method : 'POST',
								headers : {
									'Content-Type' : 'application/x-www-form-urlencoded'
								},
								data: $httpParamSerializerJQLike(_.omitBy(params, _.isNil)),
						}).then(
								function(data) {
									
									return data.data;
								},
									
								function(error) {
									console
											.log("Error occured when verifyingDM info "
													+ JSON.stringify(error));
									return error;
								});
				}
		function redirectToCaseDocketFileInfo(protestId,submissionDate,docTypeId,fileAlert,docketIndexNum){
			
			localStorageService.set("protestId",protestId);
			localStorageService.set("fileInfoSubmissionDate",submissionDate);
			localStorageService.set("fileInfoDocTypeId",docTypeId);
			localStorageService.set("fileAlertStatus",fileAlert);
			localStorageService.set(submissionDate + "&&&&" + docTypeId,docketIndexNum);
			
			var aNum = base64.urlencode(protestId);
			var origSubmissionDate = base64.urlencode(submissionDate);
			
			$location.path("/casedocket-file-info-view/"+ aNum + "/" + origSubmissionDate + "/" + docTypeId + "/" + docketIndexNum);
		}
		function _getCaseDocketSheetData(response){
			var vm = {};
			
			localStorageService.remove("gc_A_no","gc_role");
			localStorageService.set("caseDocketProtestInfo",response.protestInfo);
			localStorageService.set("caseDocketa_No",response.protestInfo.a_No);
			localStorageService.set("isViewOnly",response.protestInfo.viewOnly);
			
			
			var today = new moment(new Date())
			var daysPassed  = today.diff(response.protestInfo.public_decision_date,'days');
			
			if (daysPassed > 60 
					&& (response.protestInfo && response.protestInfo.role.trim().indexOf("GAO") > -1)){
				
				response.protestInfo.case_Status =  "PUBLIC DECISION +60 DAYS";
				
				if (response.protestInfo.caseCompletionStatus.isZipCreated === true
						&& response.protestInfo.caseCompletionStatus.isDmEntered === true){
					response.protestInfo.case_Status =  "READY TO COMPLETE";
				}
				
				if (response.protestInfo.caseCompletionStatus.isZipCreated === true
						&& response.protestInfo.caseCompletionStatus.isDmEntered === true
						&& response.protestInfo.caseCompletionStatus.isDmVerfied === true ){
					response.protestInfo.case_Status =  "COMPLETE";
				}
			}
			
			vm.roleId = response.protestInfo.roleId;
			vm.docInfoList = response.whole_Doc_Info_Map;
			vm.isViewOnly = response.protestInfo.viewOnly;
			vm.user_Info = response.user_Info;
			vm.protestInfo = response.protestInfo;
			vm.consolidatedProtests = response.protestInfo.listOf_ConsolidatedProtest_Info;
			vm.po = response.protestInfo && response.protestInfo.po.trim();
			vm.caseStatus = response.protestInfo.case_Status.trim();
			vm.attorneyInfo = response.attorneyInfo;
			vm.fileInfoList = response.fileInfoList;
			vm.a_No = response.protestInfo.a_No
			vm.role = response.protestInfo && response.protestInfo.role && response.protestInfo.role.trim();
			vm.daysRemaining = response.daysRemaining;
			
			if (response.intervenorCompanyNameList != null
					|| typeof response.intervenorCompanyNameList != 'undefined') {
				vm.intervenorCompanyNameList = response.intervenorCompanyNameList;
			}
			
			
			if (response.attorneyInfo === null) {
				vm.attorneyInfo = {
					first_Name : "pending",
					email : "pending",
					phone_No : "pending",
					street : "pending",
				}
			} else {
				vm.assignedAttorneyUserId = response.attorneyInfo.user_Id;
			}

			/*if (response.protestInfo.b_No === null
					|| response.protestInfo.b_No === "") {
				vm.protestInfo.b_No = "pending";
			}*/

			if (vm.attorneyInfo.first_Name === "pending"
					|| vm.caseStatus === 'OPEN'
					|| (response.protestInfo.b_No === null
							|| response.protestInfo.b_No === "")) {
				vm.hideCaseCompleteButton = true;
			}
			
			if (vm.role.indexOf("GAO") <= -1){
				vm.fileInfoList = $filter("removeWith")(vm.fileInfoList,{ 
					doc_Type_Id : "158", 
				})
			}
			
			var numberOfDays = 0;
			
			if (vm.protestInfo.caseCompletionStatus.caseCompleted != null){
				numberOfDays = today.diff(vm.protestInfo.caseCompletionStatus.caseCompleted,'days');
			}
			
			if (numberOfDays >= 11){
				vm.fileInfoList = $filter("removeWith")(vm.fileInfoList,{ 
					doc_Type_Id : "158", 
				})
			}
			
			vm.fileInfoList = $filter('unique')(vm.fileInfoList,"originalSubmissionDate")
			vm.fileInfoList = $filter("toArray")(vm.fileInfoList)
			angular.forEach(vm.fileInfoList, function(item) {
				item.uisubmission_Date = new Date(item.originalSubmissionDate)
				item.transient_Date = new Date(item.transient_Date)
				});
			vm.fileInfoList.sort(sortByDateAscAndTimeAscDateObj)
			vm.fileInfoList = $filter("filterBy")(vm.fileInfoList,"uisubmission_Date");
			
			
			return vm;
		}
		
		
		function sortByDateAscAndTimeAscDateObj (lhs, rhs) {
			
			 var results;
	         var lhs =  lhs.uisubmission_Date;
	         var rhs = rhs.uisubmission_Date;

	         results = lhs.getYear() > rhs.getYear() ? 1 : lhs.getYear() < rhs.getYear() ? -1 : 0;

	         if (results === 0) results = lhs.getMonth() > rhs.getMonth() ? 1 : lhs.getMonth() < rhs.getMonth() ? -1 : 0;

	         if (results === 0) results = lhs.getDate() > rhs.getDate() ? 1 : lhs.getDate() < rhs.getDate() ? -1 : 0;

	         if (results === 0) results = lhs.getHours() > rhs.getHours() ? 1 : lhs.getHours() < rhs.getHours() ? -1 : 0;

	         if (results === 0) results = lhs.getMinutes() > rhs.getMinutes() ? 1 : lhs.getMinutes() < rhs.getMinutes() ? -1 : 0;

	         if (results === 0) results = lhs.getSeconds() > rhs.getSeconds() ? 1 : lhs.getSeconds() < rhs.getSeconds() ? -1 : 0;

	         return results;
	     }
		
		
		function completeOption(protestInfo){
           
			
			var bodyText = "Do you want to close the docket and create a zip file of all case pleadings ?"
				
				var customAttr = {
						headerText : "Complete"	,
						bodyText : bodyText,
						modalType : "info",
						actionType : "samepage",
					    cancelBtnReq : "Y",
					    cancelBtnActionType : "samepage",
					    okAndCancelText : "Y",
					    okBtnText : "Yes",
					    cancelBtnText : "No"
					}
		
		actionMessageSvc.showModal(customAttr).then(function(data){
			
			var bodyText = "Are you sure you want to close the docket ? "
			var customAttr = {
					headerText : "Warning"	,
					bodyText : bodyText,
					modalType : "warning",
					actionType : "samepage",
				    cancelBtnReq : "Y",
				    cancelBtnActionType : "samepage",
				    okAndCancelText : "Y",
				    okBtnText : "Yes",
				    cancelBtnText : "No"
				}
			
			if (data.cancelBtnClicked != "Y"){
				
				actionMessageSvc.showModal(customAttr).then(function(result){
					
					if (result.cancelBtnClicked != "Y"){
						
						
						changeCaseStatusToComplete(protestInfo).then(function(Completed){
							
							if (Completed.isSuccess){
								var bodyText = "<p>You have successfully created the zipfile. The case has not yet been completed." +
										" Please follow the instructions in the EPDS GAO user manual under the" +
										" section <strong>Case Completion Workflow</strong>.</p>"
									
									var customAttr = {
											headerText : "Complete"	,
											bodyText : bodyText,
											modalType : "info",
											actionType : "samepage",
											cancelBtnReq : "N",
										}
							
							actionMessageSvc.showModal(customAttr).then(function(){
								$route.reload();
							})
							}
						});			
				    }
				});
			}
		});
		}
		
		function enterDM(protestInfo){
			
			$uibModal
			.open({
				templateUrl : 'scripts/app/admin/case-docket-sheet/dmVerification.tpl.htm',
				controller : enterOrVerifyDmCtrl,
				resolve : {
					protestInfo : function() {
						return protestInfo;
					},
				},
				animation : true,
				size : 'md',
			}).result.catch(angular.noop);
		}
		
		function downloadOfflineCds(aNum){
			
		    var myNewTab ;
		    if (window.navigator && !window.navigator.msSaveOrOpenBlob){
		    	myNewTab  = service.openNewTab();	
		    }
		    
			return $http({
				url : "/epds/download-offline-cds/" + aNum,
				method : 'POST',
				headers : {
					'Content-Type' : 'application/json'
				},
				responseType:'arraybuffer',
			}).then(
					function(data) {
						
						service.openBlob(data.data,'application/pdf',"cds.pdf",myNewTab)
					},
					function(error) {
						console.log("There was a problem downloading offline cds")
						return error;
					});
			
		}
		function pendingVerificationOption(protestInfo){
			
			var bodyText = "DM# verification is still pending."
				
				var customAttr = {
						headerText : "Pending Verification"	,
						bodyText : bodyText,
						modalType : "info",
						actionType : "samepage",
					    cancelBtnReq : "N",
					    
					}
		
		actionMessageSvc.showModal(customAttr);
			
		}
		
		function verifyDMOption(protestInfo){
			
		}

		
	}
})();
