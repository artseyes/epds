
(function() {
	'use strict';

	var directiveId = 'requestToInterveneForm';

	angular.module('epdsApp.dashboard').directive(directiveId,['RecursionHelper',
	                                                           'modalService',
	                                                           'interveneDataSvc',
	                                                           '$uibModal','localStorageService','$cookies','$ocLazyLoad','$injector','regEx','toolTip','actionMessageSvc','$uibModalStack','csrfService', interveneReqDirective]);
	/* @ngInject */
	function interveneReqDirective (RecursionHelper,modalService,interveneDataSvc,$uibModal,localStorageService,$cookies,$ocLazyLoad,$injector,regEx,toolTip,actionMessageSvc, $uibModalStack, csrfService) {
		return {
			
			restrict: "EA",
			scope: {
	        	protestInformation: '=', 
	        	cancel : '&',
	        	displayEmailAddress: '@?' 
	        	
	        	},
	        
	        templateUrl: function(elem,attrs){
	        	
	        	 return "scripts/app/request-to-intervene/intervene-request-form.html";
	        },
	        
	        link: function($scope, $element, $attribute) {
	        	
	        	$scope.regEx = regEx;
	        	$scope.toolTip = toolTip;
	        	
	        	
	        	var fileInfoViewSvc ;
	        	
	        	$scope.options = null;
	        	$scope.companyAddressDetails = '';
	        	$scope.fileUploadErrors = [];
	        	$scope.form = {
	        			intervenorCompanyName : "",
	        			address1 : "",
	        			address2 : "",
	        			zipCode : "",
	        			city : "",
	        			state : "",
	        			country : "",
	        			a_No : ""
	        		}
	        	
	        	$scope
	        	.$watch(
	        			'companyAddressDetails',
	        			function() {
	        				
	        				if (typeof $scope.companyAddressDetails.address_components != 'undefined') {

	        					$scope.companyAddressDetails = retrieveAddressDetailsFromUserSelection(
	        							$scope,
	        							$scope.companyAddressDetails);
	        					$scope.form.address1 = $scope.companyAddressDetails.streetAddr,
	        					$scope.form.zipCode = $scope.companyAddressDetails.zipcode
	        					$scope.form.country = $scope.companyAddressDetails.country,
	        					$scope.form.state = $scope.companyAddressDetails.state,
	        					$scope.form.city = $scope.companyAddressDetails.city
	        				}
	        			});


	        	$scope.role = "INTERVENOR";
	        	
	        	
	        	$ocLazyLoad.load(['international-phone-number','jquery-datatables','angular-datatables',
                                  'dashboard','angular-xeditable','cds','file-info-view','submit-new-doc','advance-search','request-to-intervene'],{serie: true, cache :false}).then(function() {
			        
                     fileInfoViewSvc  = $injector.get("fileInfoViewSvc");
			        
			    	 fileInfoViewSvc.getMessageForTheTypeOfFilesThisUserCanAttach($scope.role.trim()).then(function(message){
							$scope.uploadDocMessage = message;
						})
				});
	        	
	        	
				
				/*$scope.sizeStatusTooltip = "To determine the protester's size for a procurement, " +
					"the filer should locate the solicitation's applicable North American Industrial Classification System (NAICS) code " +
					"and consult the Small Business Administration's size standards in Title 13," +
					" Part 121 of the Code of Federal Regulations.  " +
					"If a protester does not know its applicable size for the procurement at issue, the filer should select" 
					+ '  "Large." '
					
				$scope.htmlPopover = $sce.trustAsHtml('<p style="font-family: cursive;">' 
						+ $scope.sizeStatusTooltip 
						+ '</p>');*/
				
				$scope.totalNumberOfPrimaryDocumentsAdded = 0;
				
	        	$scope.cancelPrimaryDocument = function ($flow){
	        		$flow.cancel();
	        		$scope.totalNumberOfPrimaryDocumentsAdded = 0;
	        	}
	        	
	        	$scope.totalNumberOfAssociatedDocumentsAdded = 0;
	        	
	        	$scope.cancelAssociatedDocuments = function ($flow){
	        		$flow.cancel();
	        		$scope.totalNumberOfAssociatedDocumentsAdded = 0;
	        	}
	        	
	        	
	        	$scope.fileUploadRetry = function( $file, $flow ){
	        		
	        		//need to see if we need this
	        	}
	        	
	        	if (!$scope.cancel){
	        		$scope.cancel = function(){
	        			$uibModalStack.dismissAll(); 
	        		}
	        	}
	        	
	        	$scope.fileUploadStarted = function( $file, $message, $flow ){
	        		
	        		$scope.fileUploadInProgress = true;
	        	}
	        	
	        	$scope.fileUploadError = function($file, $message, $flow ){
	        		
	        		var message  = {};
	        		$scope.fileUploadEr = true;
	        		var fileName  = $file.name;
	        		
	        		try {
	        			if (JSON.parse($message)){
	        				message = JSON.parse($message);	
	        			}
	        		  
	        		}catch (ex){
	        			console.log("Problem creating JSON",ex);
	        			message = {
	        					error : {
	        						fileError	: fileName + " was not uploaded. Please upload this file again."
	        					}
	        			}
	        		}
	        		
	        		
	        		$scope.fileUploadErrors.push(message.error);
	        		
	        		$flow.removeFile($file);
	        		
	        	}
	        	
	        	
	        	$scope.fileUploadSuccess = function ( $file, $message, $flow ){
	        		
	        	}
	        	$scope.assignSingleFileUploadFlowInstaceToScope = function(
	        			file, event, flow) {
	        		$scope.form.attachAssociatedDoc = 'N';
	        		if (checkIfThisUserCanUploadDocumentWithThisFileExtension(
	        				modalService, file, $scope.role.trim())) {

	        			flow.opts.query = {
	        					"attachmentType" : "RequestToIntervene",
	        					"fileIdentifierCode" : "P",
	        					"a_No" : $scope.protestInformation.a_No	
	        			};
	        			
	        			flow.opts.headers =  {
	        					'X-XSRF-TOKEN' : csrfService.token 
	        					};
	        			
	        			$scope.protestDocumentAdded = true;
	        			$scope.singleFileUpload = flow
	        			$scope.totalNumberOfPrimaryDocumentsAdded = $scope.singleFileUpload.files.length;
	        		} else {
	        			return false;
	        		}
	        	}
	        	$scope.assignMultipleFileUploadFlowInstaceToScope = function(
	        			file, event, flow) {
	        		
	        		if (checkIfThisUserCanUploadDocumentWithThisFileExtension(
	        				modalService, file, $scope.role.trim())) {
	        			flow.opts.query = {
	        					"attachmentType" : "RequestToIntervene",
	        					"fileIdentifierCode" : "A",
	        					"a_No" : $scope.protestInformation.a_No
	        			};
	        			
	        			flow.opts.headers =  {
	        					'X-XSRF-TOKEN' : csrfService.token
	        					};
	        			
	        			$scope.multipleFileUpload = flow;
	        			$scope.totalNumberOfAssociatedDocumentsAdded = $scope.multipleFileUpload.files.length;
	        		} else {
	        			return false;
	        		}
	        	}

	        	
	        	
	        	
	        	var listOfANumbers = [];
	        	$scope.protestDocumentIsUploadedToServer = function() {
	        		
	        		
	        		fileInfoViewSvc.checkForErrorsinFileUpload($scope.singleFileUpload,$scope.multipleFileUpload).then(function(data){
						$scope.fileUploadEr = data.fileUploadEr;
						 
						if (!$scope.isRequestAlreadySubmitted){
							
							if (!$scope.fileUploadEr
									&& !data.fileUploadInProgress
									&& ($scope.fileUploadErrors.length <= 0)) {
								
								if ($scope.form.attachAssociatedDoc === 'N') {
									
									$scope.form.a_No = $scope.protestInformation.a_No;
									angular.forEach($scope.protestInformation.listOf_ConsolidatedProtest_Info,
											function(object, index) {
										listOfANumbers.push(object.a_No)
									});
									$scope.isRequestAlreadySubmitted = true;
									interveneDataSvc.submitRequestToIntervene($scope.form,listOfANumbers)
								}else if ($scope.form.attachAssociatedDoc === 'Y') {
									
									$scope.form.a_No = $scope.protestInformation.a_No;
									$scope.isRequestAlreadySubmitted = true;
									interveneDataSvc.submitRequestToIntervene($scope.form)
								}
							}
						}
					});
	        		
	        	}

	        	$scope.associatedDocumentsIsUploadedToServer = function() {

	        		/*if ($scope.form.attachAssociatedDoc == 'Y') {
	        			
	        			$scope.form.a_No = $scope.protestInformation.a_No;
	        			interveneDataSvc.submitRequestToIntervene($scope.form)
	        		}*/
	        		fileInfoViewSvc.checkForErrorsinFileUpload($scope.singleFileUpload,$scope.multipleFileUpload).then(function(data){
						$scope.fileUploadEr = data.fileUploadEr;
						 
						if (!$scope.isRequestAlreadySubmitted){
							
							if (!$scope.fileUploadEr
									&& !data.fileUploadInProgress
									&& ($scope.fileUploadErrors.length <= 0)) {
								
								if ($scope.form.attachAssociatedDoc == 'N') {
									
									$scope.form.a_No = $scope.protestInformation.a_No;
									angular.forEach($scope.protestInformation.listOf_ConsolidatedProtest_Info,
											function(object, index) {
										listOfANumbers.push(object.a_No)
									});
									$scope.isRequestAlreadySubmitted = true;
									interveneDataSvc.submitRequestToIntervene($scope.form,listOfANumbers)
								}else if ($scope.form.attachAssociatedDoc == 'Y') {
									
									$scope.isRequestAlreadySubmitted = true;
									$scope.form.a_No = $scope.protestInformation.a_No;
									interveneDataSvc.submitRequestToIntervene($scope.form)
								}
							}
						}
					})
	        	}

	        	
	        	$scope.hideCancelButton = false;
	        	$scope.registerProtestInfo = function(isFormValid,attachAssociatedDocs,interveneProtestRequestForm) {
	        		
	        		if (interveneProtestRequestForm.comments.$invalid){
						isCommentsInValidFormat(actionMessageSvc);
						return;
					}else if ($scope.form.comments ==""){
						$scope.form.comments = null
						
					}
	        		
	        		
	        		if (!checkIfTotalUploadSizeExceedsTheMaxSize($scope, modalService)){
						return
					}
	        		
	        		$scope.isRequestAlreadySubmitted = false;
	        		$scope.fileUploadErrors = [];
	        		
	        		fileInfoViewSvc.checkForErrorsinFileUpload($scope.singleFileUpload,$scope.multipleFileUpload).then(function(data){
						$scope.fileUploadEr = data.fileUploadEr;
						
						if ($scope.fileUploadEr){
							
							var customAttr = {
									headerText : "Error"	,
									bodyText : "Please fix the errors in the files and then submit the form. ",
									modalType : "error",
									actionType : "",
								    cancelBtnReq : "N",
								    cancelBtnActionType : ""
								}
							
							actionMessageSvc.showModal(customAttr);
							
							return false;
						}						
					
					});
	        		
	        		
	        		try {
						if (!$scope.singleFileUpload){
							$scope.totalNumberOfPrimaryDocumentsAdded = 0;
						}else {
							$scope.totalNumberOfPrimaryDocumentsAdded = $scope.singleFileUpload.files.length;
						}
						if (!$scope.multipleFileUpload){
							$scope.totalNumberOfAssociatedDocumentsAdded = 0;
						}else {
							$scope.totalNumberOfAssociatedDocumentsAdded = $scope.multipleFileUpload.files.length;
						}
					}catch(ex){
						console.log(ex)
					}
					
					
	        		interveneDataSvc.showRequestToInterveneInvalidFormMessages(isFormValid,attachAssociatedDocs,
	        				$scope.totalNumberOfPrimaryDocumentsAdded,$scope.totalNumberOfAssociatedDocumentsAdded).then(function(data){
	        			
	        			if (data.isFormValid && data.isPrimaryDocumentAttached) {
	        				$scope.hideCancelButton = true;
							if ($scope.form.attachAssociatedDoc == 'Y') {
								$scope.singleFileUpload.upload()
								$scope.multipleFileUpload.upload()
							} else if ($scope.form.attachAssociatedDoc == 'N') {
								$scope.singleFileUpload.upload()
							}
						}

	        		});
	        	}

	        }
		};
	};
})();


(function() {
	'use strict';

	var directiveId = 'agencyRepAccessList';

	angular.module('epdsApp.dashboard').directive(directiveId,['modalService',
	                                                           'interveneDataSvc','$uibModal','localStorageService',
	                                                           '$cookies','advanceSearchDataSvc','$location','$compile','$rootScope', agencyRepAccessListDirective]);

	function agencyRepAccessListDirective (modalService,interveneDataSvc,$uibModal,localStorageService,$cookies,advanceSearchDataSvc,$location,$compile,$rootScope) {
		return {
			
			restrict: "EA",
			terminal: true,
			scope: {
	        	protestInfoList: '=', 
	        	showAgencyCases : '@',
	        	submitNoticeOfAppearance : '&',
	        	redirect : '&',
	        	role : '=?'
	        	},
	        
	        templateUrl: function(elem,attrs){
	        	
	        	 return "scripts/app/request-to-intervene/protestLists.tpl.htm";
	        },
	        
	        link: function($scope, $element, $attribute) {
	        	
	        	/*$compile($element.contents())($scope.$new());*/
	        	/*console.log(localStorageService.get("caseAccessRequestType"))*/
				if ($attribute.showAgencyCases){
					
					var vm = this;
		        	advanceSearchDataSvc.getDashboardSettings(vm).then(
							function(data) {
								if(data != null){
									$scope.dtOptions = data.dtOptions.initComplete = function() {
										advanceSearchDataSvc.initCompleteFunc(vm);
									}
								}
							});
				}
	        	
	        }
		};
	};
})();