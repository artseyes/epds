


angular.module('epdsApp.caseDocketSheet').controller(
				'caseDocketFileInfoController',FileInfoViewCtrl);
FileInfoViewCtrl.$inject = ['$scope', '$filter', '$http', '$rootScope', '$window',
		'modalService', '$location', '$timeout', '$route',
		'$routeParams', '$uibModal','localStorageService','actionMessageSvc','fileInfoViewSvc','regEx','toolTip','base64']


function FileInfoViewCtrl($scope, $filter, $http, $rootScope, $window,
		modalService, $location, $timeout, $route,
		$routeParams, $uibModal,localStorageService,actionMessageSvc,fileInfoViewSvc,regEx, toolTip, base64) {

	
	$scope.splitString = function(String) {
		return String.split("_")[0];
	}
	
	$scope.urlEncodedAnum = $routeParams.aNum;
	$scope.urlDecodedsubmissionDate = base64.urldecode($routeParams.origSubmissionDate);
	$scope.docketNumber = parseInt($routeParams.docketIndexNum) + 1;
	
	
	fileInfoViewSvc.loadFileInfoViewData(base64.urldecode($scope.urlEncodedAnum),$scope.urlDecodedsubmissionDate,$routeParams.docId).then(function(response){

		$timeout(function() {
			
			//$scope.docketNumber = localStorageService.get(response.file_InfoList[0].originalSubmissionDate + "&&&&" + response.file_InfoList[0].doc_Type_Id);
			$scope.protestInfo = response.protestInfo
			$scope.role = response.protestInfo.role && response.protestInfo.role.trim();
			$scope.a_No = response.protestInfo.a_No;
			
			if (response.protestInfo.b_No === null) {
				$scope.protestInfo.b_No = "pending";
			}
			
			$scope.fileId = response.file_InfoList[0].file_Id;
			$scope.user_Info = response.user_Info;
			
			if (response.file_InfoList[0].filler != null) {
				$scope.type_Of_Doc = response.type_Of_Doc
						.split("_")[0]
						+ " "
						+ response.file_InfoList[0].filler;
			} else {
				$scope.type_Of_Doc = response.type_Of_Doc
						.split("_")[0]
			}
			
			$scope.caseStatus = response.protestInfo.case_Status.trim();
			$scope.fileInfoList = response.file_InfoList;
			$scope.isFileInfoContainsPrimaryDocument  = $filter('contains')(response.file_InfoList,function(obj){
				return (obj.file_identifier == 'P')
			});
			
			$scope.isFileInfoContainsAssociatedDocuments  = $filter('contains')(response.file_InfoList,function(obj){
				return (obj.file_identifier == 'A')
			});
			
			
			
			fileInfoViewSvc.findIfThisDocsAreAlwaysVisible($scope.fileInfoList[0].doc_Type_Id).then(function(data){
				$scope.isAlwaysVisisble = data;
				if ($scope.type_Of_Doc.trim() === "Notice Of  Appearance"){
					$scope.isAlwaysVisisble = true;	
				}
				
				if (!data){
					$scope.PO = $scope.fileInfoList[0].is_Confidential;
				}
			})
			
			$scope.is_Intervene_Approved = (response.file_InfoList[0].is_Intervene_Approved != null) ? response.file_InfoList[0].is_Intervene_Approved.trim(): null;
			if ($scope.is_Intervene_Approved === 'Y') {
				$scope.is_Intervene_Approved = true;
			} else {
				$scope.is_Intervene_Approved = false;
			}
			
			$scope.protestDocumentList = $filter('filter')(response.file_InfoList,
					{
				doc_Type_Id : "1"
			});
			$scope.doc_Type_Id = localStorageService.get("fileInfoDocTypeId");
			
			
			$scope.po = [ {
				value : 'Y',
				text : 'Y'
			}, {
				value : 'N',
				text : 'N'
			}]
			
			
			$scope.showPrimaryDeleteBtn = function(replacePrimaryFileId,file_Info){
				
				var isDisplay = !replacePrimaryFileId || (replacePrimaryFileId != file_Info.file_Id)
				
				if (!file_Info.fileName.trim()){
					isDisplay = false;
				}
				
				
				return isDisplay;
			}

			$scope.isGAOUserSupervisorOrAttorney = ($scope.user_Info.role_id == '8' || $scope.user_Info.role_id == '3');
			$scope.isGAOUserWithFullAccess = ($scope.user_Info.role_id == '7' || ($scope.isGAOUserSupervisorOrAttorney && !$scope.protestInfo.viewOnly));

			$scope.isRequestToInterveneApprovalNeedsToBeShown = 
				$scope.isGAOUserWithFullAccess  
				&&  ($scope.fileInfoList[0].doc_Type_Id == '56' 
				&& $scope.fileInfoList[0].case_access_request_status
				&& $scope.fileInfoList[0].case_access_request_status.trim() == 'P')
				&& !$scope.showSubmitNewDoc
				&& !$scope.requestApproved;
			
			
			$scope.isGAOUserSupervisorOrAttorney = ($scope.user_Info.role_id == '8' || $scope.user_Info.role_id == '3');
			$scope.isGAOUserWithFullAccess = ($scope.user_Info.role_id == '7' || ($scope.isGAOUserSupervisorOrAttorney && !$scope.protestInfo.viewOnly))
			$scope.isNoticeOfApperance = $scope.type_Of_Doc && $scope.type_Of_Doc.trim() == "Notice Of  Appearance";
			
			var isCaseAccessRequestPending =  $scope.fileInfoList[0] && $scope.fileInfoList[0].case_access_request_status && $scope.fileInfoList[0].case_access_request_status.trim() == 'P',
				isSubmitNewDocAndRequestApprovedFalse = !$scope.showSubmitNewDoc && !$scope.requestApproved,
				canApproveQuesBeDisplayed = $scope.isGAOUserWithFullAccess  && isSubmitNewDocAndRequestApprovedFalse;
			$scope.isRequestToInterveneQuesNeedsToBeShown = 
				canApproveQuesBeDisplayed &&  ($scope.fileInfoList[0].doc_Type_Id == '56' && isCaseAccessRequestPending);
			$scope.isNoticeOfAppearanceQuesNeedsToBeShown = 
				canApproveQuesBeDisplayed &&  ($scope.isNoticeOfApperance && isCaseAccessRequestPending);
		
		})

	
		
	})
	
	$scope.encodeFunctionFileURL = function(fileName,fileId) {
		var caseInfObj = {};
		caseInfObj.fileName = fileName;
		caseInfObj.fileId = fileId;
		caseInfObj.a_No = $scope.a_No;
		caseInfObj.user_Id = $scope.user_Info.user_Id;
		caseInfObj.role	= $scope.role;
		var body=angular.element ('body');
	    var iFrame = body.find("#downloadiframe");
	     
	    if (!(iFrame && iFrame.length > 0)) {
	        iFrame = angular.element ("<iframe id='downloadiframe' style='position:fixed;display:none;top:-100px;left:-100px;'/>");
	        body.append(iFrame);
	    }
	 
	    
	    iFrame.attr("src", "/epds/downloaddashboardfile?fileId=" + fileId);
		/*fileInfoViewSvc.downloadFiles(caseInfObj);*/
		
	}

	$scope.replacePrimaryDoc = function(fileId,event){
		$scope.replacePrimaryFileId = fileId;
		$scope.uploadPrimary = false;
	}
	
	$scope.cancelReplacePrimaryDoc = function(fileId,event){
		$scope.replacePrimaryFileId = null;
		$scope.isFileInfoContainsPrimaryDocument = true;
		$scope.uploadPrimary = false;
		$scope.displayUploadDoc = false;
	}
	
	$scope.chooseFromExistingDocs = function(){
		fileInfoViewSvc.uploadPrimaryDocModal($scope.fileInfoList)
	}
	
	$scope.uploadPrimaryDoc = function(){
		$scope.uploadPrimary = true;
		$scope.requestType  = "replaceDoc";
		$scope.replaceDocFileInfo = $filter('filter')($scope.fileInfoList,{ 
						file_identifier : "P",
						file_Id : $scope.replacePrimaryFileId
				});
		$scope.replaceDocFileInfo = angular.extend({},$scope.replaceDocFileInfo[0],{
						typeOfDoc : $scope.type_Of_Doc
						});
	}
	
	$scope.replaceDoc = function(fileId){
		$scope.requestType  = "replaceDoc";
		$scope.primaryDocFileInfo = null;
		$scope.replaceDocFileInfo = $filter('filter')($scope.fileInfoList,{ 
						file_Id : fileId
				});
		$scope.replaceDocFileInfo = angular.extend({},$scope.replaceDocFileInfo[0],{
			typeOfDoc : $scope.type_Of_Doc
		});
		$scope.displayUploadDoc = true;
	}
	$scope.cancelUploadPrimary = function(){
		$scope.uploadPrimary = false;
	}
	
	$scope.displayEditButtons = function(flag){
		$scope.showEditButtons = flag;
		$scope.uploadPrimary = false;
		$scope.replacePrimaryFileId = null;
	}
	
	$scope.respondToCaseAccessRequest = function (fileId, approve,accessType) {
		
		if (accessType === "intervene"){
			$scope.requestType  = "other";
			$scope.docInfo = {
					docTypeId : "160",
					uploadText : "Upload Primary Document",
					headerText : "Denial of Request to Intervene",
					a_No : $scope.a_No
			}
			
			} else if (accessType === "agency-rep-access"){
				$scope.requestType  = "other";
				$scope.docInfo = {
						docTypeId : "161",
						uploadText : "Upload Primary Document",
						headerText : "Denial of Notice of Appearance",
						a_No : $scope.a_No
				}
			}
	
		if (approve === "N"){
			$scope.denialDocInfo = {
					fileId  : fileId,
					approve : approve,
					accessType : accessType
			}
			$scope.showSubmitNewDoc = true;
		}else if (approve === "Y"){
			/*$route.reload();*/
			$scope.requestApproved = true;
			$scope.showSubmitNewDoc = false;
			$scope.isRequestToInterveneQuesNeedsToBeShown = false;
			$scope.isNoticeOfAppearanceQuesNeedsToBeShown = false;
			fileInfoViewSvc.respondToCaseAccessRequest(fileId, approve,accessType);
		}
		
		
	}

	
	/* Update Case Docket Sheet Info */
	$scope.updateCaseDocketDocumentView = function(fileId,
			typeOfchange, newValue) {
		
		if (typeOfchange === "confidential"){

			fileInfoViewSvc.updateCaseDocketDocumentView(fileId,
					typeOfchange, newValue);
		
		}else if (typeOfchange === "delete"){
			
			var bodyText = "Are you sure you want to permanently delete this file ?"
				
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
 	

		actionMessageSvc.showModal(customAttr).then(function(result){
		    			if (result.cancelBtnClicked != "Y"){
		    				fileInfoViewSvc.updateCaseDocketDocumentView(fileId,
		    						typeOfchange, newValue);
		    			}
		    })
		}
		
		
	
	}

}
UploadPrimaryDocModalInstanceCtrl.$inject =['$scope', '$rootScope','$uibModalInstance','$route','fileInfoList','fileInfoViewSvc','$filter']

function UploadPrimaryDocModalInstanceCtrl ($scope, $rootScope,$uibModalInstance,$route,fileInfoList,fileInfoViewSvc,$filter) {

	$scope.fileInfoList = $filter('where')(fileInfoList, {file_identifier : 'A'});
	$scope.selectedOption = $scope.fileInfoList[0];
	$scope.markThisDocumentAsPrimary = function(selectedOption){
		$uibModalInstance.dismiss('cancel');
		fileInfoViewSvc.updateCaseDocketDocumentView(selectedOption.file_Id,
				"markAsPrimary", "P");
	}
	$scope.cancel = function() {
		$uibModalInstance.dismiss('cancel');
	};
}

epdsApp

.directive('fileDownload', [function () {
    return {
        restrict: 'A',
        replace: true,
        template: '<button class="btn btn-default" data-ng-click="download()"><span class="glyphicon glyphicon-download"></span></button>',
        /* @ngInject */
        controller: ['$rootScope', '$scope', '$element', '$attrs', '$timeout', function ($rootScope, $scope, $element, $attrs, $timeout) {
            $scope.progress = 0;

            function prepare(url) {
               /* dialogs.wait("Please wait", "Your download starts in a few seconds.", $scope.progress);*/
            	
                fakeProgress();
            }
            function success(url) {
                $rootScope.$broadcast('dialogs.wait.complete');
            }
            function error(response, url) {
                /*dialogs.error("Couldn't process your download!");*/
            	
            }

            function fakeProgress() {
                $timeout(function () {
                    if ($scope.progress < 95) {
                        $scope.progress += (96 - $scope.progress) / 2;
                        $rootScope.$broadcast('dialogs.wait.progress', { 'progress': $scope.progress });
                        fakeProgress();
                    }
                }, 250);
            }

            $scope.download = function () {
                $scope.progress = 0;
                $.fileDownload($attrs.href, { prepareCallback: prepare, successCallback: success, failCallback: error });
            }
        }]
    }
}]);
