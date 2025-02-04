angular.module(
		'epdsApp.caseDocketSheet')
.controller('OtherProtestCtrl', OtherProtestCtrl);		

OtherProtestCtrl.$inject = [ '$scope', '$http', '$location', '$window',
                             '$rootScope', 'ngFabForm', '$routeParams', 'protestDataSvc' ,'modalService','$cookies','navigationSvc','fileInfoViewSvc','actionMessageSvc','$filter','base64','csrfService'];

function OtherProtestCtrl ($scope, $http, $location, $window, $rootScope,
		ngFabForm, $routeParams, protestDataSvc,modalService,$cookies,navigationSvc,fileInfoViewSvc,actionMessageSvc,$filter,base64,csrfService) {
	$scope.fileUploadErrors = [];
	$scope.docConfidentialLabel = "Do any of these documents contain information that is subject to a protective \n" +
		"                                                order entered by the judge in this case?  The filer will select Yes if the filing \n" +
		"                                                includes this type of information AND a Protective Order has been entered in the appeal.";
	
	$scope.requestForRecon = [{
		label : "PROTESTER",
		id :1
	},{
		label : "INTERVENOR",
		id :2
	},{
		label : "AGENCY",
		id :3
	}]
	
	$scope.urlEncodedANum = $routeParams.aNum
	if ($routeParams.typeOfProtest === "reconsideration") {
		$scope.typeOfProtestFiling = "Request for Reconsideration"
	} else if ($routeParams.typeOfProtest === "entitlement") {
		$scope.typeOfProtestFiling = "Request for Entitlement"
		$scope.requestForRecon = $filter("removeWith")($scope.requestForRecon,{ 
			id : "2", 
		})
	} else {
		$scope.typeOfProtestFiling = "Request for Cost Claims"
		$scope.requestForRecon = $filter("removeWith")($scope.requestForRecon,{ 
			id : "2", 
		})
	}

	$rootScope.setTitle($scope.typeOfProtestFiling);
	
	var checkForFileUploadErrors = function(){
		
		fileInfoViewSvc.checkForErrorsinFileUpload($scope.singleFileUpload,$scope.multipleFileUpload).then(function(data){
			$scope.fileUploadEr = data.fileUploadEr;
			 
			if(!$scope.isRequestAlreadySubmitted){
				
				if ($scope.form.attachAssociatedDoc === 'N' 
					&& !$scope.fileUploadEr
					&& !data.fileUploadInProgress
					&& ($scope.fileUploadErrors.length <= 0)) {
					
					$scope.isRequestAlreadySubmitted = true;

					
					protestDataSvc.registerOtherProtests($scope.form,$scope.protestInfo.a_No,$routeParams.typeOfProtest,$scope.filerType).then(function(response){
						
						if ($scope.role === "GAO ADMIN" ){
							$location.path("/admin-dashboard/unassigned");
						}else{
							$location.path("/dashboard");
						}
					})

				
				}else if ($scope.form.attachAssociatedDoc === 'Y' 
					&& !$scope.fileUploadEr
					&& !data.fileUploadInProgress
					&& ($scope.fileUploadErrors.length <= 0)) {
					
					$scope.isRequestAlreadySubmitted = true;
					
					protestDataSvc.registerOtherProtests($scope.form,$scope.protestInfo.a_No,$routeParams.typeOfProtest,$scope.filerType).then(function(response){
						
						if ($scope.role === "GAO ADMIN" ){
							$location.path("/admin-dashboard/unassigned");
						}else{
							$location.path("/dashboard");
						}
					})

				
				}
			}
		});
	}
	
	var params = {
			typeOfProtest : $routeParams.typeOfProtest,
			protestId : base64.urldecode($routeParams.aNum)
	}

	protestDataSvc.loadProtestFilingForm(params).then(function(response) {
		
		if ($routeParams.typeOfProtest === "reconsideration" 
			|| $routeParams.typeOfProtest === "entitlement" 
				|| $routeParams.typeOfProtest === "cost-claim"){
			var navigationObj = {
					navigationType : "caseDocketSheet",
					caseStatus : "N/A",
					roleId :  response.protestInfo.roleId,
					protestInfo : response.protestInfo,
					isViewOnly : response.protestInfo.viewOnly
			} 
			navigationSvc.setListOfRoutesBasedOnRole(navigationObj);
		}
        $scope.a_No = response.aNum;
		$scope.typeOfProtest = $routeParams.typeOfProtest;
		$scope.intervenorCompNameList = response.intervenorCompNameList;
		$scope.agencyNameList = response.agencyNameList;
		
		if (!response.intervenorCompNameList || !response.intervenorCompNameList.length){
			$scope.requestForRecon = $filter("removeWith")($scope.requestForRecon,{ 
				id : "2", 
			})
		}
		$scope.form = response.form;
		$scope.role = response.user_Role.trim();
		$scope.protestInfo = response.protestInfo;
		$scope.caseStatus = response.protestInfo.case_Status.trim();
		
		fileInfoViewSvc.getMessageForTheTypeOfFilesThisUserCanAttach($scope.role.trim()).then(function(message){
			$scope.uploadDocMessage = message;
		})
		
	})
	
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
		
	$scope.fileUploadStarted = function( $file, $message, $flow ){
		
		$scope.fileUploadInProgress = true;
	}
	
	$scope.fileUploadError = function($file, $message, $flow ){
		var message =  {};
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
	
	
	$scope.assignSingleFileUploadFlowInstaceToScope = function(
			file, event, flow) {

		if (checkIfThisUserCanUploadDocumentWithThisFileExtension(
				modalService, file, $scope.role.trim())) {
			flow.opts.query = {
				"attachmentType" : "protest",
				"fileIdentifierCode" : "P",
                "a_No" : $scope.a_No
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
				"attachmentType" : "protest",
				"fileIdentifierCode" : "A",
                "a_No" : $scope.a_No
			};
			
			flow.opts.headers =  {
					'X-XSRF-TOKEN' : csrfService.token
					};
			$scope.multipleFileUpload = flow
			$scope.totalNumberOfAssociatedDocumentsAdded = $scope.multipleFileUpload.files.length;

		} else {
			return false;
		}

	}

	$scope.protestDocumentIsUploadedToServer = function() {
		
		checkForFileUploadErrors();
	}

	$scope.associatedDocumentsIsUploadedToServer = function() {

		checkForFileUploadErrors();
	};
	
	

	$scope.registerProtestInfo = function(isFormValid, attachAssociatedDocs,filerType) {
		
		$scope.filerType = filerType;
		
		
		if (filerType && filerType.id =="2"){
			$scope.form.company_name = $scope.selectedIntervenorComp;	
		}else if (filerType && filerType.id =="3"){
			$scope.form.company_name = $scope.selectedAgencyName;	
		}
		if ($scope.typeOfProtest === "reconsideration"  
			&& (filerType === null 
					|| typeof filerType === 'undefined' ) 
					&& $scope.protestInfo.roleId == "7"){
			
			var customAttr = {
					headerText : "Error"	,
					bodyText : "Please select filer type",
					modalType : "error",
					actionType : "",
				    cancelBtnReq : "N",
				    cancelBtnActionType : ""
				}
			
			actionMessageSvc.showModal(customAttr);
		}
		
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
		
		
		$scope.isRequestAlreadySubmitted = false;
		$scope.fileUploadErrors = [];
				
				
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
		
		protestDataSvc.showInvalidFormMessages(isFormValid,
				attachAssociatedDocs,$scope.totalNumberOfPrimaryDocumentsAdded,$scope.totalNumberOfAssociatedDocumentsAdded).then(function(data) {

					if (data.isFormValid && data.isProtestDocumentAttached) {

						if (data.attachAssociatedDocs === 'Y') {
							$scope.singleFileUpload.upload()
							$scope.multipleFileUpload.upload()
						} else if (data.attachAssociatedDocs === 'N') {
							$scope.singleFileUpload.upload()
						}
					}
				})
				
	}
	
	

	
	
};
