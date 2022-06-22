epdsApp.controller('tempUserCtrl', tempUserCtrl);

tempUserCtrl.$inject = [ '$scope', '$rootScope', '$http',
		'ngFabForm', '$timeout', '$filter', 'modalService', 'base64',
		'$location', 'localStorageService', 'data', 'resetPasswordService',
		'$uibModalStack','ModalSvc','authFeedbackMessagesSvc',
		'$httpParamSerializerJQLike','Idle','actionMessageSvc','$window',
		'authenticationService','regEx','toolTip']



function tempUserCtrl($scope, $rootScope, $http, ngFabForm,
		$timeout, $filter, modalService, base64, $location,
		localStorageService, data,resetPasswordService,$uibModalStack,
		ModalSvc,authFeedbackMessagesSvc,$httpParamSerializerJQLike,
		Idle,actionMessageSvc,$window,authenticationService,regEx,toolTip) {

	Idle.unwatch();
	localStorageService.set("userId",data.user_id)
	
	
	$scope.listOfErrorMessages = null;
	
	$scope.role = data.auth_role_id;
	$scope.accountStatusId = data.account_status_id;
	if ($scope.role != "1"){
		$scope.headerText = "Please update your password and security questions"
	}else{
		$scope.headerText = "Please update your password"
	}
	$scope.form = {
			password : "",
			retypepassword : "",
			answer1 : "",
			answer2 : "",
			answer3 : "",
	};
	
	$http({
		url : '/epds/user/get-security-questions',
		method : 'GET',
		headers: {
			'Accept': 'application/json',
			'Content-Type': 'application/json'
				},
	    
	}).then(function(response) {
		var data = response.data;
		var selectedOption = {
				 security_q_id : 0,
				 security_question : "Please Select Security Question. "
		 } 
		
		 data.list.push(selectedOption);
		
		$scope.listOfSecurityQuestions = data.list
		$scope.selectedOption1 = selectedOption
		$scope.selectedOption2 = selectedOption
		$scope.selectedOption3 = selectedOption
	});
	
	
	$scope.securityQuestionHelp = function(){
		var obj = {};
		authFeedbackMessagesSvc.getFeedbackMessages("securityQuestionHelp").then(function(response){
			obj.data = response.data;
			actionMessageSvc.showModal(obj.data);
		})
		
	
	}
	
	
	
	$scope.onQuestion1Selected  = function(selectoption1,selectoption2,selectoption3){
		
		if(selectoption1.security_q_id === selectoption2.security_q_id){
			$scope.selectedOption2 = $scope.listOfSecurityQuestions[$scope.listOfSecurityQuestions.length -1]
		}
	
		if(selectoption1.security_q_id === selectoption3.security_q_id){
			$scope.selectedOption3 = $scope.listOfSecurityQuestions[$scope.listOfSecurityQuestions.length -1]
		}
		
	}
	
	
	$scope.onQuestion2Selected = function(selectoption1,selectoption2,selectoption3){
		
		
		if(selectoption2.security_q_id === selectoption1.security_q_id){
			$scope.selectedOption1 = $scope.listOfSecurityQuestions[$scope.listOfSecurityQuestions.length -1]
		}
	
		if(selectoption2.security_q_id === selectoption3.security_q_id){
			$scope.selectedOption3 = $scope.listOfSecurityQuestions[$scope.listOfSecurityQuestions.length -1]
		}
		
	}
	
	$scope.onQuestion3Selected = function(selectoption1,selectoption2,selectoption3){
			
			if(selectoption3.security_q_id === selectoption2.security_q_id){
				$scope.selectedOption2 = $scope.listOfSecurityQuestions[$scope.listOfSecurityQuestions.length -1]
			}
		
			if(selectoption3.security_q_id === selectoption1.security_q_id){
				$scope.selectedOption1 = $scope.listOfSecurityQuestions[$scope.listOfSecurityQuestions.length -1]
			}
			
		}
	

				
				$scope.questions = [];
			if ($scope.role === "8"){
				angular.forEach(data.secQIdToQuestionMap, function(key, value){
				     
					$scope.questions.push({id: key, ques: value});
				});
			}
			$scope.Question = $scope.questions[0];
			$scope.numberOfAttempts = 0;
			$scope.checkifThisAnswerIsCorrect = function (id,answer,numberOfAttempts){
				var form = {
						user_id : data.user_id,
						secQId  : id,
						answer : answer,
						numberOfAttempts : 0
				}
				resetPasswordService.checkIfthisAnswerIsCorrect(form).then(
						function(response) {
							
							if (response.isCorrect === "N"){
								$scope.Question = $scope.questions[response.numberOfAttempts];
								$scope.numberOfAttempts = response.numberOfAttempts;
							}else {
								var customModalOptions = {
										headerText : 'Error',
										bodyText : 'You must select 3 different security questions.',
										closeButtonText : 'OK',
										messageType : "error"
									};

									modalService.showModal({}, customModalOptions);
							}
						});
			}
	

		$scope.updateInfo = function(form, selectoption1, selectoption2,
			selectoption3) {
			
			var isAnyOfSecQuesNotSelected = (selectoption1.security_q_id == "0" 
	 										|| selectoption2.security_q_id == "0" 
	 										|| selectoption3.security_q_id == "0");
			
			if (isAnyOfSecQuesNotSelected && ($scope.role != "1" && $scope.role != "8") 
					&& ($scope.accountStatusId != "8" 
							&& $scope.accountStatusId != "5"  
							&& $scope.accountStatusId != "6")){
	 			
	 			var customAttr = {
						headerText : "Error"	,
						bodyText : "Please make sure you have selected a security question. ",
						modalType : "error",
						actionType : "",
					    cancelBtnReq : "N",
					    cancelBtnActionType : ""
					}
				
				actionMessageSvc.showModal(customAttr);
	 			
	 			return;
	 			
	 		}
			
			
		var credentials = {};
		
		credentials.userPwd = form.password;
		
		form.seqQue1Id = selectoption1.security_q_id
		form.seqQue2Id = selectoption2.security_q_id
		form.seqQue3Id = selectoption3.security_q_id
		
		var password = authenticationService.encodePassword(credentials);
		var retypepassword =  authenticationService.encodePassword(credentials);
		
		var form = {
				user_id : data.user_id,
				answer1: form.answer1,
				answer2: form.answer2,
				answer3: form.answer3,
				/*password: authenticationService.getCredentials(credentials.userPwd = form.password),
				retypepassword: authenticationService.getCredentials(credentials.userPwd = form.retypepassword),*/
				password: String(password),
				retypepassword: String(retypepassword),
				seqQue1Id: form.seqQue1Id,
				seqQue2Id:form.seqQue2Id,
				seqQue3Id: form.seqQue3Id,	
		}
		$http({
			url : '/epds/user/update-profile/N',
			method : 'POST',
			headers : {
				'Content-Type' : 'application/x-www-form-urlencoded'
			},
			data : $httpParamSerializerJQLike(_.omitBy(form, _.isNil))
		}).then(function(response) {
			var data = response.data;
			var obj = {};
			if (data.isSuccess) {
				$uibModalStack.dismissAll()

				authFeedbackMessagesSvc.getFeedbackMessages("updatedInfoFirstTime").then(function(response){
					obj.data = response.data;
					actionMessageSvc.showModal(obj.data).then(function(data){
						Idle.watch();
						/*$window.location.reload();*/
						$location.path("/dashboard").replace();
					});
					
				
				})
				
			}else{
				authFeedbackMessagesSvc.getFeedbackMessages("passwordOrSecurityQueUpdateError").then(function(response){
					obj.data = response.data;
					actionMessageSvc.showModal(obj.data);
				})
			}
			
		});
	}
		
		
		$scope.cancel = function(){
			$uibModalStack.dismissAll()
		}
}


epdsApp.directive('complexPassword', function() {
	  return {
	    require: 'ngModel',
	    link: function(scope, elm, attrs, ctrl) {
	      ctrl.$parsers.unshift(function(password) {
	        var hasUpperCase = /[A-Z]/.test(password);
	        var hasLowerCase = /[a-z]/.test(password);
	        var hasNumbers = /\d/.test(password);
	        var hasNonalphas = /\W/.test(password);
	        var characterGroupCount = hasUpperCase + hasLowerCase + hasNumbers + hasNonalphas;
	        /*var hasWhiteSpaces = '^((?!.*[\s])(?=.*[A-Z])(?=.*\d).{12,24})'.test(password);*/
	        
	        
	        if ((password.length >= 12) && (characterGroupCount >= 4)) {
	          ctrl.$setValidity('complexity', true);
	          return password;
	        }
	        else {
	          ctrl.$setValidity('complexity', false);
	          return undefined;
	        }

	      });
	    }
	  }
	});



