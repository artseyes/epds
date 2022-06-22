/*
 * This directive helps displaying the Parent and child relationship in dashboard. Since we also need to 
 * compile "data-expand-collapse" directive inside this directive we are using recursion helper 
 * @param RecursionHelper A service which makes it easily possible to have recursive Angular directives.
 * @param $location .
 * @return Use the compile function from the RecursionHelper, And return the linking function(s) which it returns
*/

angular.module('epdsApp').directive("tree",['RecursionHelper','$location','dashboardDataService','$compile' , function(RecursionHelper,$location,dashboardDataService,$compile) {
	  
	return {
		restrict: "EA",
        replace: true,
        scope: {
        		protest: '=', 
        		path :'@',
        		disableCaseDocket :'@?'
        			},
        templateUrl: function(elem,attrs){

        	return "scripts/app/dashboard/dashboard-parent-child-display.html";
        },
        compile: function(element) {
            return RecursionHelper.compile(element, function(scope, iElement, iAttrs, controller, transcludeFn){

	        	scope.redirectToCaseDocket = function (currentProtestInfo,path,role,a_No,caseAccessRequestStatus,docTypeId,fileInfoSubmissionDate){
	        		var caseInfObj = {};
	        		caseInfObj.currentProtestInfo = currentProtestInfo;
	        		caseInfObj.path = path;
	        		caseInfObj.caseAccessRequestStatus = caseAccessRequestStatus;
	        		caseInfObj.a_No = a_No;
	        		caseInfObj.docTypeId = docTypeId;
	        		caseInfObj.fileInfoSubmissionDate = fileInfoSubmissionDate;
	        		caseInfObj.role = role;
	        		dashboardDataService.checkCaseAccessRequestStatus(caseInfObj);
	        		
	        	}
	          
            });
        },
    };
}]);
