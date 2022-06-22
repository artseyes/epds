
(function () {
	
	/* @ngInject */
	angular.module('epdsApp.registration').directive('uniqueid',['$q', '$timeout','$http','$compile','base64','$httpParamSerializerJQLike','regEx', 
	                                                             function($q, $timeout,$http,$compile,base64,$httpParamSerializerJQLike,regEx) {
		return {
			require : 'ngModel',
			
			link : function(scope, elm, attrs, ctrl) {
				
				
				
			ctrl.$asyncValidators.uniqueid = function(modelValue, viewValue) {
					
					if (ctrl.$isEmpty(modelValue)) {
						return $q.when();
					}
					
				

					var def = $q.defer();

					$timeout(function() {
						checkIfEmailExists(modelValue,def)
					}, 2000);

					return def.promise;
				};
				function checkIfEmailExists(email,def){

					    var emailREGEX = regEx.email;
						var checkIfThisIsAValidEmailAddress = emailREGEX.exec(email)

						if (checkIfThisIsAValidEmailAddress) {
							var params = {
									email : base64.urlencode(email)
							}
							 $http({
								url : '/epds/user/check-if-user-exists/',
								method : 'POST',
								headers: {
									'Accept': 'application/json',
									'Content-Type': 'application/json'
										},
							    data: angular.toJson(params),
							    ignoreLoadingBar: true
							})
							.then(function(response) {
								var data = response.data;
								if (data.message === "N") {
									attrs.validationMsgUniqueid = "Email is Valid";
									def.resolve(attrs);
								}else if (data.message === "Y") {
									attrs.validationMsgUniqueid = "Email Already exists";
									def.reject(attrs);
								} else {
									attrs.validationMsgUniqueid = data.message;
									def.reject(data.message);
								}
			
							});
						
						}else{
							
							return def.reject();
						}
				
				}
			
			}
		};
	}]);


	
	angular.module('epdsApp.registration').directive('authorized',['$q', '$timeout','regEx', function($q, $timeout,regEx) {
		  return {
				require : 'ngModel',
				scope : {
					role :'='
				},
				
		    link: function(scope, elm, attrs, ctrl) {

		      ctrl.$asyncValidators.authorized = function(modelValue, viewValue) {

		        if (ctrl.$isEmpty(modelValue)) {
		          
		          return $q.when();
		        }

		        var def = $q.defer();

		        $timeout(function() {
		        	var role = scope.role;
					var trustedDomainList = [];
					var trustedAgencyDomains =['hq.dodea.edu', 'dodea.edu', 'fs.fed.us','si.edu'];
					if (modelValue && modelValue.length > 0) {
						
						
						if (role === "GAO") {
							trustedDomainList = ['gov']
						}else {
							trustedDomainList = ['gov','mil']
						}
						

						try {
							var emailREGEX = regEx.email;
							var isValidEmailExtension = false;
							var checkIfThisIsAValidEmailAddress = emailREGEX.exec(modelValue)

							if (!checkIfThisIsAValidEmailAddress) {
								attrs.message = modelValue + ' is not a valid email';
								ctrl.$setValidity("",false);
								return def.reject();
							}
							
							var fullyQualifiedDomain = checkIfThisIsAValidEmailAddress[0].split("@")[1]
							
							
							isValidEmailExtension =_.find(trustedDomainList,function(item){
									return item === _.last(fullyQualifiedDomain.split("."));
								})
							
								
							if(window.location.host != "epds.cbca.gov") {
								ctrl.$setValidity("",true);
								def.resolve();
								
							}else if ((!isValidEmailExtension 
									&& trustedAgencyDomains.indexOf(fullyQualifiedDomain) == -1) 
									&& role == "6") {
								attrs.message = checkIfThisIsAValidEmailAddress[0] + ' is not allowed.  Please enter an email address with an authorized domain. Ex : ' +  "@" +trustedAgencyDomains.join(", @") + " OR " + "."+ trustedDomainList.join(", .");
								ctrl.$setValidity("",false);
								def.reject();
							}else if (!isValidEmailExtension && role == "GAO") {
								response.message = checkIfThisIsAValidEmailAddress[0] + ' is not allowed.  Please enter an email address with an authorized domain. Ex : .gov';
								
								ctrl.$setValidity("",false);
								def.reject();
							}else{
								ctrl.$setValidity("",true);
								def.resolve();
								
							}
							
							return def.promise;

						} catch (err) {
							
							return def.reject();
						}
					}

				
		        }, 100);

		        
		        return def.promise;
		      };
		    }
		  };
		}]);
	
	
	
}());





