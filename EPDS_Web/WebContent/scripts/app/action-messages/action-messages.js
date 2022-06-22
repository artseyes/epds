(function() {
	'use strict';

	var serviceId = 'actionMessageSvc';

	angular.module('epdsApp').factory(serviceId,
			[ '$rootScope','$uibModal','$location','$route','$q', actionMessageSvc ]);

	/* @ngInject */
	function actionMessageSvc($rootScope,$uibModal,$location,$route,$q) {

		var service = {
			showModal : showModal,
		};

		return service;

		function showModal(customObj) {

			var modalInstance =  $uibModal.open({
				templateUrl : 'scripts/app/action-messages/action-specific-messages.tpl.htm',
				controller : actionMessagesModalInstanceCtrl,
				resolve : {
					/* @ngInject */
					items : function() {
						return customObj;
					}
				},
				size : 'md',
				backdrop : 'static',
				keyboard : false,
			});
			
			return modalInstance.result;
		}
	}
	
	actionMessagesModalInstanceCtrl.$inject = ['$scope', '$rootScope', '$uibModalInstance',
	                              		'items','$ocLazyLoad','$injector'];

	 function actionMessagesModalInstanceCtrl ($scope, $rootScope, $uibModalInstance,
	 		items,$ocLazyLoad,$injector) {
	 	
		 
		 $scope.data = items;
		 
		 if (typeof items.fileInfo === 'undefined'){
			 $scope.fileInfo === null;
		 }else{
			 $scope.fileInfo = items.fileInfo; 
			 $scope.showFileInfo = items.fileInfo.file_InfoList.length > 0;
		 }
		 
		 
		 	if ('undefined' !== typeof items.inputErrorMessages){
		 		$scope.showErrors = true;
		 		$scope.listOfErrorMessages = items.inputErrorMessages;
		 	}
		 
		 
		 
		 
		 $scope.downloadFiles = function(fileName,fileId){
			 var caseInfObj = {};
				caseInfObj.fileName = fileName;
				caseInfObj.a_No = $scope.fileInfo.a_No;
				caseInfObj.fileId = fileId;
				caseInfObj.user_Id = $scope.fileInfo.user_Info.user_Id;
				caseInfObj.role	= $scope.fileInfo.role;
				
				$ocLazyLoad.load(['jquery-datatables','angular-datatables','dashboard','account-update','request-to-intervene',
	                               'angular-xeditable','cds','file-info-view'],{serie: true, cache :false}).then(function() {
			        
			    	 var fileInfoViewSvc = $injector.get("fileInfoViewSvc");
			        
			    	 fileInfoViewSvc.downloadFiles(caseInfObj);
				});
				
		 }
		 
		 $scope.ok = function(data) {
			 	data.cancelBtnClicked = "N";
		 		$uibModalInstance.close(data);
		 	};
	 	$scope.cancel = function(data) {
	 		
	 		if (data === "" || data == null || typeof data === "undefined"){
	 		$uibModalInstance.close("cancel");
	 		}else{
	 			data.cancelBtnClicked = "Y";
	 			$uibModalInstance.close(data);
	 		}
	 	};

	 }
})();

