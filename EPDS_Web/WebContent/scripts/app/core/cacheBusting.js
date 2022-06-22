/*Reminder for Amer need to find the right place to place this core module because agency dropdown is part of core module*/

/*angular.module('epdsApp.core', ['datatables', 'ngResource', 'datatables.columnfilter',
                                'datatables.buttons','datatables.scroller',
                				'datatables.colvis', 'datatables.bootstrap', 'RecursionHelper',
                				'angular.filter', 'CaseFilter','ui.filters', 'flow',
                				'LocalStorageModule', 'ngRoute', 'ui.bootstrap','datatables.fixedcolumns',
                				'ngDialog', 'ngMessages', 'internationalPhoneNumber', 
                				'ngFabForm', 'xeditable','ngCacheBuster',
                				'ui.bootstrap.datetimepicker', 'ngBootstrap',
                				'nDaterangepicker', 'templateDocumentTypesFilter',
                				'angularjs-dropdown-multiselect']);
*/

angular.module('epdsApp.core')
                				
                				
 /*  
  * http://javascript.info/tutorial/character-classes
  * Most useful classes are:

		\d
		A digit, any character from 0 to 9
		\s
		A whitespace character, like tab, newline etc.
		\w
		A symbol of Latin alphabet or a digit or an underscore '_'
		
		There are characters which have special use in regexps: [ \ ^ $ . | ? * + ( ).
  * 
  * */
       /* @ngInject */         				
.constant('regEx', {
	email : /^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/,
	email_max : 320,
	prefix : /^([a-zA-Z0-9\. ]{0,10})+$/,
	prefix_max : 10,
	firstName : /^([\w\-\s ']{0,25})+$/,
	firstName_max : 25,
	middleInitial : /^([a-zA-Z0-9]{0,1})+$/,
	middleInitial_max: 1,
	lastName : /^([\w\-\s ']{0,25})+$/,
	lastName_max : 25,
	suffix :/^([a-zA-Z0-9\. ]{0,10})+$/,
	suffix_max : 10,
	nameOfFirm : /^[-:,;<=>'\?./\(/\)//\//@#&+\w\s]*$/,
	nameOfFirm_max : 200,
	address : /^[-:;,<=>'\?./#&+\w\s]*$/,
	address_max : 100,
	zipcode : /^(\d{5}(-\d{4})?|[A-Z]\d[A-Z] *\d[A-Z]\d)$/,
	zipcode_max : 10,
	city : /^[-:;,<=>'\?./#&+\w\s]*$/,
	city_max : 20,
	state : /^[-:;,<=>'\?./#&+\w\s]*$/,
	state_max : 20,
	country : /^([a-zA-Z ]{0,20})+$/,
	country_max : 20,
	comments : /^[-\u2018\u2019\u201c\u201d:,;<=>()'"`\?./#&@+\w\s]*$/,
	solNum : /^[\w\-\s\.]+$/,
	solNum_max : 25,
	protestId : /^([a-zA-Z0-9-. ]{0,25})+$/,
	protestId_max : 25,
	typeOfDoc : /^[-:;<=>'\?./#&+\w\s]*$/,
	phone_max : 25,
	gaoId : /^([0-9]{0,10})+$/,
	gaoId_max : 10,
	gaoTitle : /^([\w\-\s ']{0,255})+$/,
	gaoTitle_max : 255,
})
/* @ngInject */
.constant('toolTip', {
	
	email : "Enter email address associated with EDS.",
	prefix : "Prefix",
	firstName : "Enter your first name. ",
	middleInitial : "Enter your middle Initial.",
	lastName : "Last Name",
	suffix :"Suffix",
	nameOfFirm : "Name of the firm. ",
	address : "Address", 
	zipcode : "Zipcode",
	city : "City", 
	state : "State",	
	country : "Country", 	
	comments : 'Comments',
	solNum : "Please enter solicitation #. ",
	companyName : "Please enter the company name.",
	protestId1 : "Please enter File/B#",	
	protestId2 : "Please enter EDS Ctrl#",
	typeOfDoc : "Enter document description"
	
});






(function () {
	'use strict';
	
	angular.module('epdsApp.core').config(['httpRequestInterceptorCacheBusterProvider', function(httpRequestInterceptorCacheBusterProvider){
		  //httpRequestInterceptorCacheBusterProvider.setMatchlist([/.*scripts.*/,/.*views.*/],true);
		// /.*epds.*//.*views.*/
		httpRequestInterceptorCacheBusterProvider.setMatchlist([/.*scripts.*/,/.*epds.*/,/.*views.*/],true);
		}]);
	
	
	
		
}());


function isCommentsInValidFormat (actionMessageSvc){
	
	var bodyText = "";	
	
	bodyText += "<p> Comments are not entered in valid format. Please make sure Comments only contain characters from following categories";

	bodyText += "<ul>";
	bodyText += "  <li>English uppercase characters (A to Z)<\/li>";
	bodyText += "  <li>English lowercase characters (a to z)<\/li>";
	bodyText += "  <li>Numbers (0 to 9) and<\/li>";
	bodyText += "  <li>Special characters (For example, .,_, #, (), -, ? . : ; ' \" &)<\/li>";
	bodyText += "<\/ul>  ";

	bodyText += "</p>"

		var customAttr = {
			headerText : "Error",
			bodyText : bodyText,
			modalType : "error",
			actionType : "samepage",
			cancelBtnReq : "N",
			cancelBtnActionType : "samepage",
			okAndCancelText : "Y",
			okBtnText : "OK",
			cancelBtnText : "No"
		}
	
	actionMessageSvc.showModal(customAttr);
}



