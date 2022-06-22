angular.module('epdsApp.core', ['ngResource', 'RecursionHelper',
                				'angular.filter', 'CaseFilter', 'flow',
                				'LocalStorageModule', 'ngRoute', 'ui.bootstrap',
                				/*'ngDialog'*/, 'ngMessages', /*'internationalPhoneNumber',*/ 
                				'ngFabForm','ngCacheBuster',
                				/*'ui.bootstrap.datetimepicker', 'ngBootstrap',
                				'nDaterangepicker',*/ 'templateDocumentTypesFilter']);



angular.module('epdsApp.auth', [ 'ngIdle','angularModalSvc','ngAnimate', 'ab-base64','vcRecaptcha','epdsApp.core','ngPatternRestrict']);

angular.module('epdsApp.registration',['epdsApp.auth']);
angular.module(
		'epdsApp.dashboard',['epdsApp.core']);
		
angular.module('epdsApp.adminDashboard', [ 'epdsApp.core' ])

angular.module(
		'epdsApp.caseDocketSheet',['epdsApp.core','xeditable']);

angular.module(
		'epdsApp.parties',['epdsApp.core']);

