/*comeback to this and check if this directive always populates
docInfo because we are using docInfo to populate a_no param while uploading files*/

(function() {
	'use strict';

	var directiveId = 'submitNewDoc';

	/* @ngInject */
	angular.module('epdsApp.caseDocketSheet').directive(directiveId,['modalService','$uibModal',
	                                                                 'localStorageService','$cookies','$filter','$interpolate',
	                                                                 'submitNewDocDataSvc','fileInfoViewSvc','actionMessageSvc','regEx','toolTip','$rootScope','csrfService', submitNewDocDirective]);
	/* @ngInject */
	function submitNewDocDirective (modalService,$uibModal,localStorageService,$cookies,$filter,$interpolate,submitNewDocDataSvc,fileInfoViewSvc,actionMessageSvc,regEx,toolTip,$rootScope, csrfService) {
		return {
			
			restrict: "EA",
			scope: {
	        	docInfo : '=?', 
	        	role : '=?',
	        	docTypeDesc : '=?', 
	        	requestType : '=?', 
	        	denialDocInfo : '=?',
	        	showCancelBtn : '@?',
	        	isAssociatedDocNeeded : '@?',
	        	primaryDocFileInfo : '=?',
	        	replaceDocFileInfo : '=?',
	        	cancel : '&?',
				typeOfDoc : "=?"
	        	},
	        
	        templateUrl: function(elem,attrs){
	        	
	        	 return "scripts/app/submit-new-docs/uploadDocuments.tpl.htm";
	        },
	        
	        link: function($scope, $element, $attribute) {
	        	
	        	

				$scope.doc_InfoList = [];
	        	$scope.selectedOption = {
						doc_Type_Id : 0,
						doc_Type_Desc : "Please Select Type of Document",
						filing_Order : 0
					}
	        	
	        	$scope.doc_InfoList.push($scope.selectedOption);
	        	
	        	/*$scope.doc_InfoList.push({
					doc_Type_Id : 119,
					doc_Type_Desc : "Notice Of ____",
					filing_Order : 0
				})*/

				$scope.$watch("typeOfDoc", function(newVal) {

					if(newVal === "Notice Of  Appearance"){
						$scope.doc_InfoList.push({
							doc_Type_Id : 161,
							doc_Type_Desc : "Denial of Notice of Appearance",
							filing_Order : 0
						});
					} else if (newVal === "Request to Intervene"){
						$scope.doc_InfoList.push({
							doc_Type_Id : 160,
							doc_Type_Desc : "Denial of Request to Intervene",
							filing_Order : 0
						});
					}

				});
	        	
	        	$scope.regEx = regEx;
				$scope.toolTip = toolTip;
				
	        	$scope.form = {
	        			attachAssociatedDoc : null
	        	}
	        	
	        	
	        	
	        	/*//if this is notice of appearance or request to intervene there should be  no associated documents
        		try {
        			if ($scope.docInfo.docTypeId == "160"
        				|| $scope.docInfo.docTypeId == "161" || $scope.docInfo.docTypeId == "0") {}
        		} catch (ex) {
        			console.log("docInfo is not defined")
        		}*/
        		
        		
	        	$scope.onSelectChange = function(doc_Info){

	        		$scope.selectedOption = doc_Info;
	        		
					if ($scope.selectedOption) {
						
						submitNewDocDataSvc.displayGenerateTemplateDocButton("GAO", $scope.selectedOption,$scope.originalSelectedOptionDocId,$scope.doc_InfoList).then(function(data){
							
							
							$scope.showGenerateDocTempButton = data.showGenerateDocTempButton;
							if ($scope.showGenerateDocTempButton){
								$scope.primaryBtnText = "Upload Template"
								$rootScope.templateCounter = undefined;
								$scope.deleteTempPdfFile = true;
								$scope.generateTempBtnText = "Generate Template";
								$scope.htmlContent = null;
								$rootScope.data = null;
							}else{
								$scope.primaryBtnText = "Upload Document"
							}
							
						});
						
						$scope.selectedOption.doc_Type_Desc = $scope.selectedOption.doc_Type_Desc;
						
						if ($scope.selectedOption.doc_Type_Desc
								.indexOf("_") > -1 && typeof $scope.selectedOption.doc_Type_Desc.split("_")[1] != "undefined" 
									&& $scope.selectedOption.doc_Type_Desc.split("_")[1] == "") {

							submitNewDocDataSvc.editDocumentDescriptions($scope.selectedOption).then(function(modalInstance){
								
								modalInstance.result.then(function (result) {
									if (typeof result !== 'undefined'
										&& result !== ""
										&& result !== null) {
										
										$scope.selectedOption.doc_Type_Desc = $scope.selectedOption.doc_Type_Desc.split("_")[0] + result;
										$scope.docDescFiller = result;
										$scope.originalSelectedOptionDocId = $scope.selectedOption.doc_Type_Id;
										$scope.selectedOption.doc_Type_Id = 0;
										
									}
									
									$scope.doc_InfoList = $filter('filter')($scope.doc_InfoList, 
											{ doc_Type_Id : $scope.selectedOption.doc_Type_Id }, function (obj, test) { 
											return obj !== test;
                                    });
									
									$scope.doc_InfoList.push($scope.selectedOption)
									
							    });
							})
							
						}
						
					}
				
	        	}
	        		
				
	        	$scope.fileUploadErrors = [];
	        	$scope.generateTempBtnText = "Generate Template";
	        	$scope.$watch("role", function(newVal) {
	        		fileInfoViewSvc.getMessageForTheTypeOfFilesThisUserCanAttach($scope.role.trim()).then(function(message){
						$scope.uploadDocMessage = message;
					})
	        	});
	        	$scope.role = $attribute.role;
	        	$scope.assignSingleFileUploadFlowInstaceToScope = function(
	        			file, event, flow) {
	        		
	        		$scope.totalNumberOfPrimaryDocumentsAdded = 1;
	        		//if this is notice of appearance or request to intervene there should be  no associated documents
	        		try {
	        			if ($scope.docInfo.docTypeId == "160"
	        				|| $scope.docInfo.docTypeId == "161" || $scope.docInfo.docTypeId == "0") {
	        				$scope.form.attachAssociatedDoc = "N";
	        			}
	        		} catch (ex) {
	        			console.log("docInfo is not defined")
	        		}
	        		
	        		if ($scope.requestType == "replaceDoc"){
	        			
	        			$scope.form.attachAssociatedDoc = "N"
	        			flow.opts.query = {
								"attachmentType" : $scope.replaceDocFileInfo &&  $scope.replaceDocFileInfo.typeOfDoc,
								"fileIdentifierCode" : $scope.replaceDocFileInfo &&  $scope.replaceDocFileInfo.file_identifier,
								"a_No" : (($scope.docInfo) ? $scope.docInfo.a_No : null) 
							};
							
						flow.opts.headers =  {
	        					'X-XSRF-TOKEN' : csrfService.token 
	        					};
						$scope.primaryDocumentAdded = true;
	        			$scope.singleFileUpload = flow
	        			
	        		}else if (checkIfThisUserCanUploadDocumentWithThisFileExtension(
	        				modalService, file, $scope.role.trim())) {

	        			flow.opts.query = {
	        					"attachmentType" : $filter('camelCase')($scope.docInfo.headerText),
	        					"fileIdentifierCode" : "P",
	        					"a_No" : (($scope.docInfo) ? $scope.docInfo.a_No : null)
	        			};
	        			
	        			flow.opts.headers =  {
	        					'X-XSRF-TOKEN' : csrfService.token 
	        					};
	        			
	        			$scope.primaryDocumentAdded = true;
	        			$scope.singleFileUpload = flow
	        			$scope.totalNumberOfPrimaryDocumentsAdded = $scope.singleFileUpload.files.length;
	        			
	        			return true;
	        		} else {
	        			return false;
	        		}
	        		
	        		
	        	}
	        	
	        	
	        	$scope.downloadTempPdf = function (selectedOption){
						var clonedSelectedOption = angular.extend({},selectedOption)
						
						if (clonedSelectedOption.doc_Type_Id  == 0 ){
							clonedSelectedOption.doc_Type_Id = $scope.originalSelectedOptionDocId;
						}
						
						var fileName = $filter('camelCase')(clonedSelectedOption.doc_Type_Desc)
						submitNewDocDataSvc.downloadTempPdf(clonedSelectedOption,fileName).then(function(){
							
						})	
					}
					$scope.isNumber = angular.isNumber;
					$scope.deleteTempPdfFile = false;
					$scope.deleteTempPdf = function (selectedOption){
						$rootScope.templateCounter = undefined;
						$scope.deleteTempPdfFile = true;
						$scope.generateTempBtnText = "Generate Template";
						$scope.htmlContent = null;
						$rootScope.data = null;
					}
					
					$scope.isNaN =  function(x){
						
						return isNaN(x)
					}
	        	
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
	        	
	        	$scope.assignMultipleFileUploadFlowInstaceToScope = function(
	        			file, event, flow) {
	        		if ($scope.requestType == "replaceDoc"){
	        			return false;
	        		}else if (checkIfThisUserCanUploadDocumentWithThisFileExtension(
	        				modalService, file, $scope.role.trim())) {
	        			
	        			flow.opts.query = {
	        					"attachmentType" : $filter('camelCase')($scope.docInfo.headerText),
	        					"fileIdentifierCode" : "A",
	        					"a_No" : (($scope.docInfo !== null && typeof $scope.docInfo !== 'undefined') ? $scope.docInfo.a_No : null)
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

	        	$scope.primaryDocumentIsUploadedToServer = function() {
	        		 
	        		fileInfoViewSvc.checkForErrorsinFileUpload($scope.singleFileUpload,$scope.multipleFileUpload).then(function(data){
						$scope.fileUploadEr = data.fileUploadEr;
						 
						
						if (!$scope.isRequestAlreadySubmitted){
							
							if (!$scope.fileUploadEr
									&& !data.fileUploadInProgress
									&& ($scope.fileUploadErrors.length <= 0)) {
								
								if ($scope.requestType == "replaceDoc"){
									$scope.isRequestAlreadySubmitted = true;
									submitNewDocDataSvc.replaceDoc($scope.replaceDocFileInfo);
									
								}else if ($scope.form.attachAssociatedDoc == 'N' 
									&& $scope.requestType != "replaceDoc") {
									var form = {
											"docId" : $scope.docInfo.docTypeId,
											"typeofdocument" : $scope.docInfo.headerText,
											"docDescFiller" : ($scope.selectedOption.doc_Type_Id == 0 ? $scope.docDescFiller : null),
											"isDocConfidential" : "N",
											"comments" : $scope.form.comments,
											"requestType": $scope.requestType,
											"protestId" :$scope.docInfo.a_No,
											"denialDocInfo" : $scope.denialDocInfo,
											"content" : $scope.htmlContent
									}
									$scope.isRequestAlreadySubmitted = true;
									submitNewDocDataSvc.submitThisRequestAfterAllFilesAreUploaded(form,$scope.role,$scope.docInfo.a_No)
								}else if ($scope.form.attachAssociatedDoc == 'Y' && $scope.requestType != "replaceDoc") {
									var form = {
											"docId" : $scope.docInfo.docTypeId,
											"typeofdocument" : $scope.docInfo.headerText,
											"docDescFiller" : ($scope.selectedOption.doc_Type_Id == 0 ? $scope.docDescFiller : null),
											"isDocConfidential" : "N",
											"comments" : $scope.form.comments,
											"requestType": $scope.requestType,
											"protestId" :$scope.docInfo.a_No,
											"denialDocInfo" : $scope.denialDocInfo,
											"content" : $scope.htmlContent
									}
									$scope.isRequestAlreadySubmitted = true;
									submitNewDocDataSvc.submitThisRequestAfterAllFilesAreUploaded(form,$scope.role,$scope.docInfo.a_No)
								}
								
							}
						}
						
					});
	        		
	        		
	        		
	        		
	        	}

	        	$scope.associatedDocumentsIsUploadedToServer = function() {
	        		
	        		 
	        		fileInfoViewSvc.checkForErrorsinFileUpload($scope.singleFileUpload,$scope.multipleFileUpload).then(function(data){
						$scope.fileUploadEr = data.fileUploadEr;
						 
						if (!$scope.isRequestAlreadySubmitted){
							
							if (!$scope.fileUploadEr
									&& !data.fileUploadInProgress
									&& ($scope.fileUploadErrors.length <= 0)) {
								
								if ($scope.form.attachAssociatedDoc == 'N' 
									&& $scope.requestType != "replaceDoc") {
									var form = {
											"docId" : $scope.docInfo.docTypeId,
											"typeofdocument" : $scope.docInfo.headerText,
											"docDescFiller" : ($scope.selectedOption.doc_Type_Id == 0 ? $scope.docDescFiller : null),
											"isDocConfidential" : "N",
											"comments" : $scope.form.comments,
											"requestType": $scope.requestType,
											"protestId" :$scope.docInfo.a_No,
											"denialDocInfo" : $scope.denialDocInfo,
											"content" : $scope.htmlContent
									}
									$scope.isRequestAlreadySubmitted = true;
									submitNewDocDataSvc.submitThisRequestAfterAllFilesAreUploaded(form,$scope.role,$scope.docInfo.a_No)
								}else if ($scope.form.attachAssociatedDoc == 'Y' 
									&& $scope.requestType != "replaceDoc") {
									var form = {
											"docId" : $scope.docInfo.docTypeId,
											"typeofdocument" : $scope.docInfo.headerText,
											"docDescFiller" : ($scope.selectedOption.doc_Type_Id == 0 ? $scope.docDescFiller : null),
											"isDocConfidential" : "N",
											"comments" : $scope.form.comments,
											"requestType": $scope.requestType,
											"protestId" :$scope.docInfo.a_No,
											"denialDocInfo" : $scope.denialDocInfo,
											"content" : $scope.htmlContent
									}
									$scope.isRequestAlreadySubmitted = true;
									submitNewDocDataSvc.submitThisRequestAfterAllFilesAreUploaded(form,$scope.role,$scope.docInfo.a_No)
								}
								
							}
						}
						
					});
	        		
	        		
	        	}

	        	$scope.hideCancelButton = false;
	        	
	        	$scope.generateTemplateBasedOnDocumentType = function(typeOfDoc) {
					
					var clonedTypeOfDoc = angular.extend({},typeOfDoc);
					
					if (clonedTypeOfDoc.doc_Type_Id  == 0 ){
						clonedTypeOfDoc.doc_Type_Id = $scope.originalSelectedOptionDocId;
					}
					
					submitNewDocDataSvc.generateTemplateBasedOnDocumentType(clonedTypeOfDoc,$scope.docInfo.a_No).then(function(data){
						var companyName = data.protestInfo && data.protestInfo.company_Name;
						var bNum = data.protestInfo && data.protestInfo.b_No;
						if (data.attorneyInfo && data.attorneyInfo.middle_initial){
							data.attorneyInfo.middle_initial = data.attorneyInfo.middle_initial + ".";	
						}
						
						if (data.consolidatedBnums){
							data.protestInfo.b_No = data.consolidatedBnums
						}
						
						if (data.consolidatedProtesterNames){
							data.protestInfo.company_Name = data.consolidatedProtesterNames
						}
						$scope.protestInfo = data.protestInfo;
						$scope.attorneyInfo = data.attorneyInfo;
						$scope.todaysDate = moment().format('MMMM DD, YYYY')
						
						$scope.reportDueDate = data.reportDueDate;
						
						$scope.htmlContent = $interpolate(data.htmlContent) ($scope)
						
						$scope.protestInfo.b_No = bNum;
						$scope.protestInfo.company_Name = companyName;
						
						submitNewDocDataSvc.generateTemplateModal(clonedTypeOfDoc,"templateModalInstanceCtrl",$scope.htmlContent).then(function(data){
							
							if ($scope.showGenerateDocTempButton 
									&& data){
								$scope.htmlContent = data;
								$scope.deleteTempPdfFile = false;
								if (isNaN($rootScope.templateCounter)){
									$scope.htmlContent = null;
									$scope.generateTempBtnText = "Generate Template";
								}else{
									$scope.generateTempBtnText = "Edit Template";
								}
								$scope.tempFileName = $rootScope.tempPdfFileName; 
							} 
						})
						
						
					});
				};
	        	
	        	$scope.submitRequest = function(docInfo,attachAssociatedDocs,comments,submitnewDocForm) {
	        		
	        		if (submitnewDocForm.comments.$invalid){
						isCommentsInValidFormat(actionMessageSvc);
						return;
					}else if (submitnewDocForm.comments == ""){
						$scope.form.comments = null
						
					}

					$scope.fileUploadErrors = [];
					var isProtectedDecisionUpload = $scope.isProtectedDecisionDocType && $scope.htmlContent;
					var isPrimaryDocumentAttached = document.getElementById('primary-document-table').rows.length;
					var   checkIfThisDocDescIsPopulated = ($scope.selectedOption.doc_Type_Desc.split("_")[1] == null) ||  ($scope.selectedOption.doc_Type_Desc.split("_")[1] == "");
					
					if (!checkIfTotalUploadSizeExceedsTheMaxSize($scope, modalService)){
						return
					}
					
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
					
					
					
					if ($scope.generateTempBtnText === 'Edit Template' ){
						attachAssociatedDocs = "N";
						isPrimaryDocumentAttached = 0;
					}
					
					
					if ($scope.showGenerateDocTempButton  && $scope.selectedOption.doc_Type_Desc === "Please Select Type of Document"){//check if dropdown option is selected
						var customAttr = {
								headerText : "Error"	,
								bodyText : "Please Select Type of Document.",
								modalType : "error",
								actionType : "",
							    cancelBtnReq : "N",
							    cancelBtnActionType : ""
							}
						
						actionMessageSvc.showModal(customAttr);
					}else if ($scope.selectedOption.doc_Type_Desc
							.indexOf("_") > -1  && checkIfThisDocDescIsPopulated ) {//if this is blank option check if doc desc filler is populated
						
						submitNewDocDataSvc.editDocumentDescriptions($scope.selectedOption).then(function(modalInstance){
							
							modalInstance.result.then(function (result) {
								if (result && result !== "") {
									
									$scope.selectedOption.doc_Type_Desc = $scope.selectedOption.doc_Type_Desc.split("_")[0] + result;
									$scope.docDescFiller = result;
									$scope.originalSelectedOptionDocId = $scope.selectedOption.doc_Type_Id;
									$scope.selectedOption.doc_Type_Id = 0;
									
								}
								
								$scope.doc_InfoList = $filter('filter')($scope.doc_InfoList, 
										{ doc_Type_Id : $scope.selectedOption.doc_Type_Id }, function (obj, test) { 
										return obj !== test;
                                });
								
								$scope.doc_InfoList.push($scope.selectedOption)
								
						    });
						})
						
					}else if (($scope.showGenerateDocTempButton && $scope.htmlContent == null) && isPrimaryDocumentAttached < 1){
						
						//template option other than protected decision was selected  but neither template nor primary document was uploaded
						var customAttr = {
								headerText : "Error"	,
								bodyText : "<p>Please either generate the template or upload the document.</p>",
								modalType : "error",
								actionType : "",
							    cancelBtnReq : "N",
							    cancelBtnActionType : ""
							}
						
						actionMessageSvc.showModal(customAttr);
						
					}else {
						
						
						try {
							if (!$scope.singleFileUpload 
								|| ($scope.generateTempBtnText === 'Edit Template')){
								$scope.totalNumberOfPrimaryDocumentsAdded = 0;
							}else {
								$scope.totalNumberOfPrimaryDocumentsAdded = $scope.singleFileUpload.files.length;
							}
							
							if (!$scope.multipleFileUpload
											|| ($scope.generateTempBtnText === 'Edit Template')){
								$scope.totalNumberOfAssociatedDocumentsAdded = 0;
							}else {
								$scope.totalNumberOfAssociatedDocumentsAdded = $scope.multipleFileUpload.files.length;
							}
						}catch(ex){
							console.log(ex)
						}
						
						var isUploadNotRequired = false;
						
						
						if ($scope.htmlContent != null){
							isUploadNotRequired = true;
						}
						
						submitNewDocDataSvc.displayInvalidFormMessages("N", attachAssociatedDocs,isUploadNotRequired,
		        				$scope.totalNumberOfPrimaryDocumentsAdded,$scope.totalNumberOfAssociatedDocumentsAdded).then(function(data){
							
									
									
									
									
		        			if ((data.isFormValid 
		        					&& data.isPrimaryDocumentAttached) && !$scope.htmlContent) {
		        				
								if (data.attachAssociatedDocs == 'Y') {
									$scope.singleFileUpload.upload()
									$scope.multipleFileUpload.upload()
								} else if (data.attachAssociatedDocs == 'N') {
									$scope.singleFileUpload.upload()
								}
							}
								
							if ($scope.showGenerateDocTempButton 
									&& $scope.htmlContent && data.isFormValid){
								
								var form = {
										"docId" : $scope.docInfo.docTypeId,
										"typeofdocument" : $scope.docInfo.headerText,
										"docDescFiller" : ($scope.selectedOption.doc_Type_Id == 0 ? $scope.docDescFiller : null),
										"isDocConfidential" : "N",
										"comments" : $scope.form.comments,
										"requestType": $scope.requestType,
										"protestId" :$scope.docInfo.a_No,
										"denialDocInfo" : $scope.denialDocInfo,
										"content" : $scope.htmlContent
								}
								
								submitNewDocDataSvc.submitThisRequestAfterAllFilesAreUploaded(form,$scope.role.trim(),$scope.protestInfo.a_No);
							}
							
		        		})
							
					}
				
	        		
	        		}

	        }
		};
	};
})();