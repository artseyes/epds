'use strict';
/* @ngInject */
angular.module(
		'epdsApp.dashboard').directive('oldpassword',['$q', '$timeout','$http',
		                              				'localStorageService','$httpParamSerializerJQLike','authenticationService', function($q, $timeout,$http,
				localStorageService,$httpParamSerializerJQLike,authenticationService) {
	return {
		require : 'ngModel',
		
		link : function(scope, elm, attrs, ctrl) {

			ctrl.$asyncValidators.oldpassword = function(modelValue, viewValue) {
				if (ctrl.$isEmpty(modelValue)) {
					return $q.when();
				}

				var def = $q.defer();
				var length = 0;
				var credentials = {};
				credentials.userPwd = viewValue
				credentials.userPwd = authenticationService.encodePassword(credentials);;
				
				if (typeof viewValue != 'undefined' && viewValue !== null){
					length = viewValue.length;
				}
				
				 if (length >= 12 ){
						$timeout(function() {
							
								var params = {
									user_id : localStorageService.get("userId"),
									password : credentials.userPwd
								}
								 $http({
										url : '/epds/user/validate-old-password',
										method : 'POST',
										headers : {
											'Content-Type' : 'application/x-www-form-urlencoded'
										},
										data: $httpParamSerializerJQLike(_.omitBy(params, _.isNil)),
										ignoreLoadingBar: true
									}).then(function(response) {
									var data = response.data;
									if (data.message === "Valid") {
										def.resolve();
									} else {
										def.reject();
									}
		
								});
						}, 20);
				 }

				return def.promise;
			};
		}
	};
}]);
