epdsApp.controller('secQuesCtrl', secQuesCtrl);

secQuesCtrl.$inject = [ '$scope', '$rootScope', '$http',
		'ngFabForm', '$timeout', '$filter', 'modalService', 'base64',
		'$location', 'localStorageService', 'data', 'resetPasswordService',
		'$uibModalStack','ModalSvc','authFeedbackMessagesSvc','regEx','toolTip']



function secQuesCtrl($scope, $rootScope, $http, ngFabForm,
		$timeout, $filter, modalService, base64, $location,
		localStorageService, data,resetPasswordService,$uibModalStack,ModalSvc,authFeedbackMessagesSvc) {

		$scope.questions = [];
		
		if (typeof data.secQIdToQuestionMap !== 'undefined'){
		
			angular.forEach(data.secQIdToQuestionMap, function(key, value){
			     
				$scope.questions.push({id: key, ques: value});
			});
			
		}
		
		$scope.Question = $scope.questions[0];
		$scope.numberOfAttempts = 1;
		$scope.error = null
		$scope.checkifThisAnswerIsCorrect = function (id,answer,numberOfAttempts){
			
			
			
			var form = {
					user_id : data.user_id,
					secQId  : id,
					answer : answer,
					numberOfAttempts : numberOfAttempts
			}
			resetPasswordService.checkIfthisAnswerIsCorrect(form).then(
					function(response) {
						
						if (response.isCorrect === "N"){
							$scope.error = "The answer doesn't match"
							if (response.numberOfAttempts > 2 && response.numberOfAttempts <= 4){
								$scope.Question = $scope.questions[1];
							}else if (response.numberOfAttempts > 4 && response.numberOfAttempts <= 6){
								$scope.Question = $scope.questions[2];
							}else if (response.numberOfAttempts > 6){
								$uibModalStack.dismissAll()
								var obj = {};
								authFeedbackMessagesSvc.getFeedbackMessages("nonVendorAccountResetFailure").then(function(response){
									obj.data = response.data;
								})
								ModalSvc.showModal({
									templateUrl: "scripts/app/authentication/authFeedbackMessages.tpl.htm",
									controller: "authFeedbackCtrl",
									inputs: obj
								}).then(function(modal) {
									 modal.element.modal();
									  modal.close.then(function(result) {
										  $location.path("/").replace();
									  });
								});
							
							}
							
							$scope.numberOfAttempts = response.numberOfAttempts;
						}else if (response.isCorrect === "Y"){

							$uibModalStack.dismissAll()
							var obj = {};
							authFeedbackMessagesSvc.getFeedbackMessages("nonVendorTempPassword").then(function(response){
								obj.data = response.data;
							})
							ModalSvc.showModal({
								templateUrl: "scripts/app/authentication/authFeedbackMessages.tpl.htm",
								controller: "authFeedbackCtrl",
								inputs: obj
							}).then(function(modal) {
								 modal.element.modal();
								  modal.close.then(function(result) {
									  $location.path("/").replace();
								  });

							});
						
						
						}
					});
		}
	
}




