(function(angular){
	'use strict'
	
    angular.module('epdsApp').config(['$httpProvider', function($httpProvider) {
                $httpProvider.interceptors.push('ErrorInterceptor');
        } 
    ])
   
    .factory('ErrorInterceptor', [
        '$q', '$injector', '$location', '$rootScope', 'applicationLoggingService' , '$log',
        ErrorInterceptorFactory
    ]);

 /* @ngInject */
    function ErrorInterceptorFactory ($q, $injector,$location,$rootScope,applicationLoggingService,$log){

        return {
	    	
	    	request: function (config) {
	  
	    		$rootScope.$broadcast('httpReqEvent');
	    		
	    		
	            return config;
	        },
	        
	        requestError: function (rejection) {
	        	
	            $log.error('Request error:', rejection);
	            
	            
	            var error = {
	                    method: rejection.config.method,
	                    url: rejection.config.url,
	                    message: rejection.data,
	                    status: rejection.status
	                };
	            
	            applicationLoggingService.error(JSON.stringify(error));
	            
	            return $q.reject(rejection);
	        },
	        
	        response : function (response){
	        	
	        	$rootScope.$broadcast('InputResponseError', {
					response : response.data 
					});
	        	 
	        	 return response;
	        },
	    	
	      

			responseError : function(rejection) {
	
					var error = {
						method : rejection.config.method,
						url : rejection.config.url,
						message : rejection.data,
						status : rejection.status
					};
	
					applicationLoggingService.error(JSON.stringify(error));
	
					switch (rejection.status) {
	
					case 401:
	
						$log.debug(rejection.data)
						if (rejection.data && !rejection.data.isBaseUrlAccess){
							
							$rootScope.$safeApply(function() {
								$rootScope.$broadcast('401', {
									data : rejection.data
								});
							})
						}
						
						break;
	
					case 404:
						break;
	
					case 500:
						if (!rejection.config.headers.skipInterceptor) {
							$injector.get('$uibModal').open({
								templateUrl : 'scripts/app/error/500Error.tpl.htm',
								controller: ['error', function(error){
									var vm = this;
									if (error && error.message){
										vm.error = angular.extend({},{
											method : error.method,
											'URL' : error.url,
											'Date': Date.now()
										},error.message);
									}

								}],
								controllerAs: 'vm',
								resolve: {
									error: function () { return error },
								}
							});
						}
						break;
	
					default:
						break;
					}
	
					/* If not a 401, do nothing with this error.
					 * This is necessary to make a `responseError`
					 * interceptor a no-op. */
					return $q.reject(rejection);
				}
	        
	    };
    }

}(window.angular));
