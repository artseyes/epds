angular.module('epdsApp.auth').config(sessionConfig)
.run(sessionConfigEventHandler);

sessionConfig.$inject = ['IdleProvider', 'KeepaliveProvider']


/* This function is used to configure the sessionConfigurations
 * Time is given in seconds
 * */

function sessionConfig(IdleProvider, KeepaliveProvider) {
	
	IdleProvider.idle(60*20); // 20 mins
	IdleProvider.timeout(60*2);// 2 mins  : how much time the session timeout warning needs to be displayed
	KeepaliveProvider.interval(120); // heartbeat every 30 seconds
	KeepaliveProvider.http('/epds/refresh'); // URL that makes sure session is alive

}

sessionConfigEventHandler.$inject = ['$rootScope', 'Idle', '$uibModal','$location',
                         '$log','$uibModalStack']


/*
 * This function is used to configure the different event
 * 
 * */

function sessionConfigEventHandler($rootScope, Idle, $uibModal,$location,$log,$uibModalStack) {
	
	var timedout, warning;
	
	  $rootScope.$on('IdleStart', function() {
		  $log.info("Idle Start -------------->" , new Date()) 
		  
	      $uibModalStack.dismissAll('IdleStart'); 
	       
	         warning = $uibModal.open({
	        	 templateUrl: 'scripts/app/authentication/time-out-warning.tp.html',
	          windowClass: 'modal-danger'
	        }).result.catch(angular.noop);
	      });

	  
	    // the user has come back from AFK and is doing stuff. if you are warning them, you can use this to hide the dialog
	  $rootScope.$on('IdleEnd', function() {
		  
		  $log.info("Idle End ---------------->" , new Date())
		  $uibModalStack.dismissAll('IdleEnd');  
		  window.document.title = "EPDS";
		  
	      });
	  
	  $rootScope.$on('IdleTimeout',
				function() {
		  
		  			$log.info("Idle Timeout ---------------->" , new Date())
		  			
		  			$uibModalStack.dismissAll('Idle Timeout');
					$rootScope.authenticated = false;
					$rootScope.sessionExpired = true;
					$rootScope.redirectMessage = "Your session has expired.  Please login to continue."
				

				});



}
