'use strict';

angular.module('epdsApp.adminDashboard')
.controller('adminEditTemplateDocumentsController',editTemplatesCtrl)

editTemplatesCtrl.$inject =['editTemplateSvc','$scope','$location','$rootScope','submitNewDocDataSvc','$filter',
	'$route','actionMessageSvc', '$timeout']
function editTemplatesCtrl (editTemplateSvc,$scope,$location,$rootScope,submitNewDocDataSvc,$filter,
							$route,actionMessageSvc, $timeout){
	
	
	$scope.htmlContent = null;
	$rootScope.data = null;
	
	editTemplateSvc.getDockInfoList().then(function(data){
		$timeout(function() {
			$('#doctype').focus();
		}, 0);

		$scope.selectedOption = {
				doc_Type_Id : "0",
				doc_Type_Desc : "Please Select Type of Document",
			}
		
		$scope.docInfoList = data;
		
		$scope.docInfoList.push($scope.selectedOption);
		
		$scope.generateTempBtnText = "Generate Template";
		
		$scope.$watch('selectedOption',function() {
			if (typeof $scope.selectedOption != 'undefined') {
				
				$scope.selectedOption.doc_Type_Desc = $scope.selectedOption.doc_Type_Desc;
				
			}
		})
	})
	
	$scope.downloadTempPdf = function (selectedOption) {
		var fileName = $filter('camelCase')(selectedOption.doc_Type_Desc)
		if (fileName.toLowerCase() === 'protecteddecision')
		    fileName = 'noticeOfIssuanceOfProtectedDecision';
		submitNewDocDataSvc.downloadTempPdf(selectedOption,fileName).then(function(){
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
		
		$scope.generateTemplateBasedOnDocumentType = function(typeOfDoc) {
			if (typeOfDoc.doc_Type_Id  == 0 ){
				typeOfDoc.doc_Type_Id = $scope.originalSelectedOptionDocId;
			}
			submitNewDocDataSvc.generateTemplateBasedOnDocumentType(typeOfDoc).then(function(data){
				
				/*$scope.protestInfo = data.protestInfo;
				$scope.attorneyInfo = data.attorneyInfo;
				$scope.todaysDate = moment().format('MMMM DD YYYY')
				$scope.htmlContent = $interpolate(data.htmlContent) ($scope)*/
				
				submitNewDocDataSvc.generateTemplateModal(typeOfDoc,"editTemplateModalInstanceCtrl",data.htmlContent).then(function(data){
					
					if (data != null && typeof data != 'undefined'){
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
		
		$scope.saveEdit = function(){
			editTemplateSvc.saveAsHtmlTemplate($scope.htmlContent,$scope.selectedOption).then(function(data){
				var customAttr = {
						headerText : "Success"	,
						bodyText : "Your edit has been successfully saved.",
						modalType : "success",
						actionType : "",
					    cancelBtnReq : "N",
					    cancelBtnActionType : ""
					}
				
				actionMessageSvc.showModal(customAttr).then(function(){
					$route.reload();
				});
			});
		}
		
		
}


editTemplateModalInstanceCtrl.$inject = ['$scope', '$rootScope', '$uibModalInstance',
                              		'items','editTemplateSvc','submitNewDocDataSvc','actionMessageSvc','Idle'];

 function editTemplateModalInstanceCtrl ($scope, $rootScope, $uibModalInstance,
 		items,editTemplateSvc,submitNewDocDataSvc,actionMessageSvc,Idle) {
 	
 	$scope.title = items.title;
 	if ($rootScope.data === null || typeof $rootScope.data === 'undefined' || $rootScope.currentTemplateDocId != items.typeOfDoc.doc_Type_Id) {
 		
 		$scope.data = items.htmlContent;
 	} else{
 		
 		$scope.data = $rootScope.data;
 	}
 	$scope.ok = function(htmlContent) {
 		$rootScope.templateCounter = 0;
 		$rootScope.templateCounter++;
 		$scope.data = htmlContent;
 		$rootScope.data = $scope.data;
 		$rootScope.currentTemplateDocId = items.typeOfDoc.doc_Type_Id;
 		$rootScope.setData($rootScope.data);
 		submitNewDocDataSvc.createPdf(htmlContent,items.typeOfDoc).then(function(data){
 			$rootScope.tempPdfFileName = data.fileName;
 		});
 		
 		$uibModalInstance.close(htmlContent);
 		var customAttr = {
				headerText : "Review"	,
				bodyText : "You can review the edits by downloading the pdf file before submitting. Please select Save Edits for submitting your edits.",
				modalType : "info",
				actionType : "",
			    cancelBtnReq : "N",
			    cancelBtnActionType : ""
			}
		
		actionMessageSvc.showModal(customAttr).then(function(){
		});
 	};
 	
 	$scope.cancel = function(htmlContent) {
 		
 		$rootScope.templateCounter--;
 		$scope.data = htmlContent;
 		$uibModalInstance.close(htmlContent);
 	};

 }
(function() {
	'use strict';

	var serviceId = 'editTemplateSvc';
	
	angular.module('epdsApp.adminDashboard').factory(serviceId,
			[  '$rootScope', '$http', '$filter', '$uibModal','$uibModalStack',
				'modalService', '$location', '$timeout','userInfoService','$httpParamSerializerJQLike','$route','navigationSvc', editTemplateSvc ]);

	function editTemplateSvc($rootScope, $http, $filter, $uibModal,$uibModalStack,
			modalService, $location, $timeout,userInfoService,$httpParamSerializerJQLike,$route,navigationSvc,$q) {
		
		var service = {
				getDockInfoList : getDockInfoList,
				saveAsHtmlTemplate : saveAsHtmlTemplate
		};

		return service;

		function getDockInfoList() {

			
			return $http({
				url : '/epds/edit-templates-view',
				method : 'POST',
				headers : {
					'Content-Type' : 'application/json'
				},
			})
					.then(
							function(data) {
						
								if (data.data.authorized) {
									$rootScope.authenticated = true;
									userInfoService
											.setUserInfo(data.data.user_Info);
									var navigationObj ={
											navigationType : "dashboard",
											caseStatus : "N/A",
											roleId :  data.data.user_Info.role_id
												
									} 
									navigationSvc.setListOfRoutesBasedOnRole(navigationObj);
								}else{
									$rootScope.authenticated = false;
									$rootScope.sessionExpired = true;
									$rootScope.redirectMessage = "You are not authorized"
									$location.path("/").replace();
								}
								
								return _filterDocInfoListByTemplateDocIds(data.data.docInfoList);
							},
							function(error) {
								
								return error;
							});

		}
		
    
		function _filterDocInfoListByTemplateDocIds(docInfoList) {
			var filteredDocInfoList = [];
			
			var listOfDocIds = [103,104,135,106,116,126,107,127,136,117,112,122,131,140,105,114,115,125,134,124,133,137,128,119,109,191,193,195,197,235,236,237,238,110,120,138,129];
			
			filteredDocInfoList = $filter("filter")(docInfoList, function(listItem){
				return $filter('contains')(listOfDocIds,listItem.doc_Type_Id);
            });
			
			return filteredDocInfoList;

		}

		
		function saveAsHtmlTemplate (content,typeOfDoc){
			
			var params = {
					content : content,
					docId : typeOfDoc.doc_Type_Id
			}
			
			return 	$http({
				
				url : '/epds/save-edited-template',
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
		
	}
})();				
