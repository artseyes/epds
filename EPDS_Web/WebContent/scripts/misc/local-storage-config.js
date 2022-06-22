 /* @ngInject */
epdsApp.config(function(localStorageServiceProvider) {
	localStorageServiceProvider.setStorageType('sessionStorage');
	
	localStorageServiceProvider.setStorageCookie(0, '/', false);
});