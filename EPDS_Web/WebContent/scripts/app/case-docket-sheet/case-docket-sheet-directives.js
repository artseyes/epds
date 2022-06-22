
angular.module('epdsApp.caseDocketSheet').directive('join', function($q, $timeout,$http,localStorageService,$httpParamSerializerJQLike,caseDocketDataSvc) {
	return {
		require : 'ngModel',
		
		link : function(scope, elm, attrs, ctrl) {

			ctrl.$asyncValidators.uniqueid = function(modelValue, viewValue) {
				if (ctrl.$isEmpty(modelValue)) {
					return $q.when();
				}

				var def = $q.defer();
					var bNum = "" ;
					var regex = /^(?!\.?$)\d{0,6}(\.\d{0,2})?$/;
					
					caseDocketDataSvc.validateJoinUnjoinCases(viewValue,attrs.parentBNum).then(function(data){
						if (data.response === "valid") {
							def.resolve();
						}else{
							attrs.validationMsg = data.response;
							def.reject();
						}
					});

				return def.promise;
			};
		}
	};
});

angular.module('epdsApp.caseDocketSheet').directive('dmNumber', function($q, $timeout,$http,localStorageService,$httpParamSerializerJQLike,caseDocketDataSvc) {
	return {
		require : 'ngModel',
		link : function(scope, elm, attrs, ctrl) {

			ctrl.$asyncValidators.dm = function(modelValue, viewValue) {
				if (ctrl.$isEmpty(modelValue)) {
					return $q.when();
				}

				var def = $q.defer();
					
					caseDocketDataSvc.validateDMInfo(viewValue,attrs.anum).then(function(data){
						
						if (data.isExists === false) {
							def.resolve();
						}else{
							def.reject();
						}
					});

				return def.promise;
			};
		}
	};
});
