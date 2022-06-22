var mainApp = angular.module('mainApp', ['ngRoute', 'angular.filter','LocalStorageModule','ngCookies']);

mainApp.config(function (localStorageServiceProvider) {
	  localStorageServiceProvider
	    .setStorageType('sessionStorage');
	});

mainApp.controller('caseDocketSheetController', function($scope, $window,
    $location,localStorageService,$filter) {


    $scope.variable = $window.globalVariable;
    $scope.protestInfo = $scope.variable.protestInfo;
    $scope.fileInfoList = $scope.variable.fileInfoList
    $scope.fileInfoList = $filter('unique')($scope.fileInfoList,"submission_Date")
	$scope.fileInfoList = $filter("toArray")($scope.fileInfoList)
	$scope.fileInfoList = $filter("filterBy")($scope.fileInfoList,"submission_Date");
    
    $scope.attorneyInfo = $scope.variable.attorney_Info;
    $scope.consolidatedProtests = $scope.variable.consolidatedProtestInfoList;
    $scope.intervenorCompanyNameList = $scope.variable.intervenorCompanyNameList;
    
    
    $scope.storeFilingInformationInSession = function (aNo,submissionDate,doc_Type_Id,filePath){
		
    	 if(localStorageService.isSupported) {
    		 localStorageService.clearAll();
    		 localStorageService.set("aNo", aNo); 
    		 localStorageService.set("submissionDate", submissionDate);
    		 localStorageService.set("doc_Type_Id", doc_Type_Id);
    	 }else if(localStorageService.cookie.isSupported) {
    		 localStorageService.cookie.clearAll();
    		 localStorageService.cookie.set("aNo", aNo);
    		 localStorageService.cookie.set("submissionDate", submissionDate);
    		 localStorageService.cookie.set("doc_Type_Id", doc_Type_Id);
    	  }
    }

});


mainApp.controller('fileInfoViewController',
    function($scope, $window, $location, $filter,localStorageService) {
	
	 $scope.variable = $window.globalVariable;
     $scope.protestInfo = $scope.variable.protestInfo;
     
		console
				.log(
					localStorageService.get("aNo"),
					localStorageService.get("submissionDate"),
					localStorageService.get("doc_Type_Id"))
	

	console.log("FleInfoList",localStorageService.get("fileInfoList"));
       

        if(localStorageService.isSupported) {
        	
        	
        	 var a_No = localStorageService.get("aNo");
             var submissionDate = localStorageService.get("submissionDate");
             var doc_Id = localStorageService.get("doc_Type_Id");
             
        	if (localStorageService.get("fileInfoList") != null){
            	$scope.fileInfoList = localStorageService.get("fileInfoList");
            }else{
            	$scope.fileInfoList = $filter('filter')(
                        $scope.variable.fileInfoList, {
                            submission_Date: submissionDate,
                         /*   a_No: $scope.variable.protestInfo.a_No,*/
                            doc_Type_Id: doc_Id
                        });
            	localStorageService.set("fileInfoList",$scope.fileInfoList);
            }
        	
          }else if(localStorageService.cookie.isSupported) {
        	
        	  var a_No = localStorageService.cookie.get("aNo");
              var submissionDate = localStorageService.cookie.get("submissionDate");
              var doc_Id = localStorageService.cookie.get("doc_Type_Id");
              
         	if (localStorageService.cookie.get("fileInfoList") != null){
             	$scope.fileInfoList = localStorageService.cookie.get("fileInfoList");
             }else{
             	$scope.fileInfoList = $filter('filter')(
                         $scope.variable.fileInfoList, {
                             submission_Date: submissionDate,
                             /*a_No: $scope.variable.protestInfo.a_No,*/
                             doc_Type_Id: doc_Id
                         });
             	
             	 localStorageService.cookie.set("fileInfoList",$scope.fileInfoList);
             }
         	
    	  }

        
        if ($scope.fileInfoList[0].filler != null) {
			$scope.type_Of_Doc = $scope.fileInfoList[0].docTypeName.split("_")[0]
					+ " "
					+ $scope.fileInfoList[0].filler;
		} else {
			$scope.type_Of_Doc = $scope.fileInfoList[0].docTypeName.split("_")[0]
		}

        $scope.attorneyInfo = $scope.variable.attorneyInfo;


        $scope.getRelativePathFromServerFilePath = function(filePath) {

        	//need to change based on final server final path server file path
        	//as of now tmpFiles is being used as the root path
        	//maye we can just use /app so we need to replace tmpFiles with app
        	
             return filePath.split("tmpFiles")[1];
        }

        $scope.getFileName = function(filePath) {

            return filePath.replace(/^.*(\\|\/|\:)/, '');
        }

    });