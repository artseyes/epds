/*
 * Request to intervene services has all the services which are used by the directives and html 
*/
(function() {
	'use strict';

	var serviceId = 'interveneDataSvc';

	angular.module('epdsApp.dashboard').factory(
			serviceId,
			[ '$rootScope', '$http', '$filter', '$uibModal','$uibModalStack',
				'modalService', '$location', '$timeout','userInfoService','$httpParamSerializerJQLike','$q','navigationSvc', interveneDataSvc ]);

	/* @ngInject */
	function interveneDataSvc($rootScope, $http, $filter, $uibModal,$uibModalStack,
			modalService, $location, $timeout,userInfoService,$httpParamSerializerJQLike,$q,navigationSvc) {

		var service = {
				loadRequestToIntervenePage : loadRequestToIntervenePage,
				searchProtestInfo : searchProtestInfo,
				showRequestToInterveneInvalidFormMessages : showRequestToInterveneInvalidFormMessages,
				submitRequestToIntervene : submitRequestToIntervene,
				validateBNumberForRequestToIntervene :validateBNumberForRequestToIntervene,
				getListOfProtestsForAgencyRepAccess  : getListOfProtestsForAgencyRepAccess ,
				getListOfProtestsForIntervenorRepAccess : getListOfProtestsForIntervenorRepAccess
				
		};

		return service;

		function loadRequestToIntervenePage() {
			
			
			return $http({
				url : '/epds/request-to-case-access',
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
									
									var navigationObj = {
											navigationType : "dashboard",
											caseStatus : "N/A",
											roleId :  roleId
												
									} 
									navigationSvc.setListOfRoutesBasedOnRole(navigationObj);
								});
								
								return data.data;
							},
							function(error) {
								
								return error;
							});

		}
		
		function searchProtestInfo(b_No,isJoinUnjoinValidation) {
			
			var params = {
					b_Num : b_No,
					isJoinUnjoinValidation : String(isJoinUnjoinValidation)
			}
			
			return 	$http({
						url : '/epds/searchBnumber',
						method : 'POST',
						headers : {
							'Content-Type' : 'application/x-www-form-urlencoded'
						},
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
		
		function getListOfProtestsForAgencyRepAccess(b_No) {
					
					var params = {
							bNo : b_No,
					}
					
					return 	$http({
								url : '/epds/get-list-of-protests-for-agency-rep-access',
								method : 'POST',
								headers : {
									'Content-Type' : 'application/x-www-form-urlencoded'
								},
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
		
		function getListOfProtestsForIntervenorRepAccess(b_No) {
			
			var params = {
					bNo : b_No,
			}
			
			return 	$http({
						url : '/epds/get-list-of-protests-for-intervenor-access',
						method : 'POST',
						headers : {
							'Content-Type' : 'application/x-www-form-urlencoded'
						},
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
		
		function submitRequestToIntervene(form,listOfProtestIds){
			
			var params = {
					address1 : form.address1,
					address2 : form.address2,
					zipCode : form.zipCode,
					city : form.city,
					state : form.state,
					country : form.country,
					intervenorEmailAddress : form.intervenorEmailAddress ? form.intervenorEmailAddress : null,
					isDocConfidential : form.isDocConfidential,
					intervenorCompanyName : form.intervenorCompanyName,
					comments : form.comments,
					protestId :form.a_No,
					listOfProtestIds : $filter("join")(listOfProtestIds,","),
			}
			 
			
			
		return 	$http({
			
				url : '/epds/add-attachments/intervene',
				method : 'POST',
				headers : {
					'Content-Type' : 'application/x-www-form-urlencoded'
				},
				data: $httpParamSerializerJQLike(_.omitBy(params, _.isNil)),
			})
			.then(
					function(data) {
						
						var customModalOptions = {
								headerText : 'Success',
								bodyText : 'You have successfully submitted a request to intervene.',
								closeButtonText : 'OK',
								messageType : "success"
							};

							modalService.showModal({}, customModalOptions).then(
									function(result) {
										if (!form.intervenorEmailAddress){
											$location.path("/dashboard");
										}
										
									}).catch(angular.noop);
							
						return data.data;
					},
					function(error) {
						
						return error;
					});
		
		}
		
		function validateBNumberForRequestToIntervene(data){
			
			var obj = {};
			obj.showProtestInfo = false;
			if (data.response == "does not exist") {

				var customModalOptions = {
					headerText : 'Error',
					bodyText : "B# does not exist. Please enter a different B#",
					closeButtonText : 'OK',
					messageType : "error"
				};
				modalService.showModal({},
						customModalOptions)
						.then(function(result) {
						});
			} else if (data.response == "already has access") {

				var customModalOptions = {
					headerText : 'Error',
					bodyText : "You already have access to the case",
					closeButtonText : 'OK',
					messageType : "error"
				};
				modalService.showModal({},
						customModalOptions)
						.then(function(result) {
						});
			} else if (data.response == "request submitted") {
				var customModalOptions = {
					headerText : 'Error',
					bodyText : 'You have already submitted request to intervene in this case. Please wait until you get approved',
					closeButtonText : 'OK',
					messageType : "error"
				};
				modalService.showModal({},
						customModalOptions)
						.then(function(result) {
						});

			} else if (data.response == "same user") {

				var customModalOptions = {
					headerText : 'Error',
					bodyText : "You cannot submit Request to Intervene in the case which you have submitted",
					closeButtonText : 'OK',
					messageType : "error"
				};
				modalService.showModal({},
						customModalOptions)
						.then(function(result) {
						});

			} else {
				obj.showProtestInfo = true;
			}
			
			return $q.when(obj);
		}
		
		
		function showRequestToInterveneInvalidFormMessages(isFormValid,attachAssociatedDocs,numberOfPrimaryDocAttached,totalNumberOfAssociatedDocumentAttached) {

			var obj = {}
			
			obj.isFormValid = isFormValid;
			obj.attachAssociatedDocs = attachAssociatedDocs;
			obj.isAssocatedDocumentsAttached = totalNumberOfAssociatedDocumentAttached;
			obj.isProtestDocumentAttached = document.getElementById('protest-table').rows.length;

			if (isFormValid) {

				if (numberOfPrimaryDocAttached != 1) {
					var customModalOptions = {
						headerText : 'Error',
						bodyText : 'This type of filing requires that you upload a document.  Please upload your document to continue.  Information regarding appropriate file types is included in the EPDS user guides.',
						closeButtonText : 'OK',
						messageType : "error"
					};

					obj.isFormValid = false;
					modalService.showModal({}, customModalOptions).then(
							function(result) {
							});

				} else if (attachAssociatedDocs == 'Y'
						&& obj.isAssocatedDocumentsAttached <= 0) {

					obj.isFormValid = false;
					var customModalOptions = {
						headerText : 'Error',
						bodyText : 'Please attach associated document(s) or select NO.',
						closeButtonText : 'OK',
						messageType : "error"
					};

					modalService.showModal({}, customModalOptions).then(
							function(result) {
							});

				}else{
					obj.isPrimaryDocumentAttached = true;
					obj.isAssocatedDocumentsAttached = true;
					obj.isFormValid = true;
				}

			} else {
				obj.isFormValid = false;
				var customModalOptions = {
					headerText : 'Error',
					bodyText : 'Please review the form. You have not entered some required fields',
					closeButtonText : 'OK',
					messageType : "error"
				};

				modalService.showModal({}, customModalOptions).then(function(result) {
				});
			}
			
			return $q.when(obj)

		}
		

	}
})();
