//advanceSearchDataSvc Data service is used to retrieve list of parties associated with a specific case

(function() {
	'use strict';

	var serviceId = 'advanceSearchDataSvc';

	angular.module('epdsApp.dashboard').factory(
			serviceId,
			[ '$rootScope', '$http', '$filter', '$uibModal','$uibModalStack',
				'modalService', '$location', '$timeout','userInfoService','$httpParamSerializerJQLike','$route','$q','DTOptionsBuilder',
				'navigationSvc','$routeParams','localStorageService', advanceSearchDataSvc ]);

	/* @ngInject */
	function advanceSearchDataSvc($rootScope, $http, $filter, $uibModal,$uibModalStack,
			modalService, $location, $timeout,userInfoService,$httpParamSerializerJQLike,$route,$q,DTOptionsBuilder,navigationSvc,$routeParams,localStorageService) {

	
		var service = {
				loadAdvanceSearchView : loadAdvanceSearchView,
				searchProtestInfo :searchProtestInfo,
				getCaseTypesAndCaseStatus : getCaseTypesAndCaseStatus,
				getDashboardSettings :  getDashboardSettings,
				initCompleteFunc : initCompleteFunc,
				
		};

		return service;

		function loadAdvanceSearchView() {


			return $http({
				url : '/epds/advance-search-view',
				method : 'GET',
				headers: {
					'Accept': 'application/json',
					'Content-Type': 'application/json'
						},
			    
			})
			.then(
					function(data) {

						$rootScope.authenticated = true;
						userInfoService
								.setUserInfo(data.data.user_Info);
						
						userInfoService.getRoleId(data.data.user_Role).then(function(roleId){
							
							if ($routeParams.reqType == "1"){
								var navigationObj = {
										navigationType : "dashboard",
										caseStatus : "N/A",
										roleId :  roleId
											
								} 
							}else if ($routeParams.reqType && $routeParams.reqType != "2"){
								
								var navigationObj = {
										navigationType : "caseDocketSheet",
										caseStatus : "N/A",
										protestInfo : localStorageService.get("caseDocketProtestInfo"),
										isViewOnly : localStorageService.get("isViewOnly"),
										roleId :  roleId
											
								} 
							}
							navigationSvc.setListOfRoutesBasedOnRole(navigationObj);
						});
						
						return data.data;
					},
					function(error) {
						
						return error;
					});

		}
		
		
		
		function searchProtestInfo(b_No, a_No,
				selectedCaseType, company_Name, selectedLawGroup,
				selectedAttorney, solicitation_No,
				selectedCaseStatus, tier1AgencyId, tier2AgencyId,
				startSubmissionDate, endSubmissionDate,
				startDueDate, endDueDate,partyInfo,intervenorCompName, onlyPrimaryANos) {

			
			
			var params =_getRequestParams(b_No, a_No,
				selectedCaseType, company_Name, selectedLawGroup,
				selectedAttorney, solicitation_No,
				selectedCaseStatus, tier1AgencyId, tier2AgencyId,
				startSubmissionDate, endSubmissionDate,
				startDueDate, endDueDate,partyInfo,intervenorCompName, onlyPrimaryANos);

			return $http({
				url : '/epds/advance-search',
				method : 'POST',
				headers : {
					'Content-Type' : 'application/x-www-form-urlencoded'
				},
				// $httpParamSerializerJQLike changed in how it converts null/undefined, need to remove those keys before serializing
				data: $httpParamSerializerJQLike(_.omitBy(params, _.isNil)),
			})
			.then(
					function(data) {

						return data.data;
					},
					function(error) {
						
						return error;
					});

		}
		
		function getCaseTypesAndCaseStatus (){
			var obj = {};
			obj.caseStatuses = [ {
				value : 'OPEN',
				text : 'OPEN'
			}, {
				value : 'CLOSED',
				text : 'CLOSED'

			},];

			obj.caseTypes = [ {
				value : 'ALT DISPUTE RESOLU',
				text : 'ALT DISPUTE RESOLU'
			}, {
				value : 'APPEAL',
				text : 'APPEAL'
			}, {
				value : 'DEBT',
				text : 'DEBT'
			}, {
				value : 'EAJA COST',
				text : 'EAJA COST'
			}, {
				value : 'FCIC',
				text : 'FCIC'
			}, {
				value : 'FCIC RECON',
				text : 'FCIC RECON'
			}, {
				value : 'FEMA',
				text : 'FEMA'
			}, {
				value : 'FMCSA',
				text : 'FMCSA'
			}, {
				value : 'ISDA',
				text : 'ISDA'
			}, {
				value : 'ISDA RECON',
				text : 'ISDA RECON'
			}, {
				value : 'MEDIATION DOCUMENTS',
				text : 'MEDIATION DOCUMENTS'
			}, {
				value : 'OTHER',
				text : 'OTHER'
			}, {
				value : 'PETITION',
				text : 'PETITION'
			}, {
				value : 'RATE',
				text : 'RATE'
			}, {
				value : 'RELOCATION',
				text : 'RELOCATION'
			}, {
				value : 'TRAVEL',
				text : 'TRAVEL'
			}, {
				value : 'TRR RECON',
				text : 'TRR RECON'
			}, ];
			
			return $q.when(obj);
		}
		
		function getDashboardSettings(vm){
			console.log(DTOptionsBuilder);
			if (vm != null ){
			vm.dtOptions = DTOptionsBuilder
			.newOptions()
			// Datatables DOM documentation: https://datatables.net/reference/option/dom
			// AngularJS datatables withDOM documentation: https://surgbook.net/node_modules/angular-datatables/#!/overrideBootstrapOptions
			.withDOM("<'row'lfr>tip")
			.withBootstrap()
			.withBootstrapOptions(
					{
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
			.withLanguage({
				"sSearch" : "Filter Records : "
			})
			}
			return $q.when(vm);
			
		}
		
		function initCompleteFunc(vm){
			if (vm.protestInfoLis
				&& vm.protestInfoList.length <= 10) {
				$(".dataTables_paginate").hide();
			}
			
			
			$('#protestTable').on('search.dt', function(e) {

				var value = $('.dataTables_filter input').val();
				var protestTable = $('#protestTable').DataTable();


				protestTable.on('draw', function() {
					var body = $(protestTable.table().body());
					body.unhighlight();
					body.highlight(protestTable.search());
				});

			});
		
		}
		
		function _getRequestParams(b_No, a_No,
				selectedCaseType, company_Name, selectedLawGroup,
				selectedAttorney, solicitation_No,
				selectedCaseStatus, tier1AgencyId, tier2AgencyId,
				startSubmissionDate, endSubmissionDate,
				startDueDate, endDueDate, partyInfo, intervenorCompName, onlyPrimaryANos){
			
			
			if (!tier1AgencyId) {
				$('#t2id').hide();
			}else if (!tier2AgencyId) {
				tier2AgencyId['agency_Id'] = null;
			}
			/*else if (tier1AgencyId.agency_Name === tier2AgencyId.agency_Name) {
				tier2AgencyId.agency_Id = "0";
			}*/

			var startSubmission_Date = null
			var endSubmission_Date = null
			var startDue_Date = null
			var endDue_Date = null

			if (company_Name === "") {
				company_Name = null
			}
			if (solicitation_No === "") {
				solicitation_No = null
			}

			if (startSubmissionDate != null) {
				startSubmission_Date = moment(startSubmissionDate)
						.format('MM/DD/YYYY')
			}
			if (endSubmissionDate != null) {
				endSubmission_Date = moment(endSubmissionDate).format('MM/DD/YYYY')
			}
			if (startDueDate != null) {
				startDue_Date = moment(startDueDate).format('MM/DD/YYYY')
			}
			if (endDueDate != null) {
				endDue_Date = moment(endDueDate).format('MM/DD/YYYY')
			}
			
			
					
			var params = {
					a_No : a_No,
					b_No : b_No,
					case_Status : selectedCaseStatus ? selectedCaseStatus : null,
					case_Type : selectedCaseType ? selectedCaseType : null,
					company_Name : company_Name ? company_Name : null,
					startSubmission_Date : startSubmission_Date ? startSubmission_Date : null ,
					endSubmission_Date : endSubmission_Date ? endSubmission_Date : null,
					startDue_Date : startDue_Date ? startDue_Date : null,
					endDue_Date : endDue_Date ? endDue_Date : null,
					solicitation_No : solicitation_No ? solicitation_No : null,
					lawGroup : selectedLawGroup ? selectedLawGroup : null,
					attorneyId : selectedAttorney ? selectedAttorney : null,
					tier1AgencyId : tier1AgencyId ? tier1AgencyId.agency_Id : 0,
					tier2AgencyId : tier2AgencyId ? tier2AgencyId.agency_Id : 0,
					partyInfo : partyInfo ? partyInfo : null,
					intervenorCompName : intervenorCompName ? intervenorCompName :null,
					onlyPrimaryANos : onlyPrimaryANos ? onlyPrimaryANos : false
				};
			
			return  params;
			
		}
		

	}
})();
