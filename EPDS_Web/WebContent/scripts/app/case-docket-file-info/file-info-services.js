(function() {
	'use strict';

	var serviceId = 'fileInfoViewSvc';

	angular.module('epdsApp.caseDocketSheet').factory(serviceId,
			[ '$filter', '$http', '$rootScope', '$window',
				'modalService', '$location', '$timeout', '$route',
				'$routeParams', '$uibModal','localStorageService','actionMessageSvc',
				'$httpParamSerializerJQLike','$q','userInfoService','navigationSvc','Idle',
				fileInfoViewSvc ]);

	/* @ngInject */
	function fileInfoViewSvc($filter, $http, $rootScope, $window,
			modalService, $location, $timeout, $route,
			$routeParams, $uibModal,localStorageService,actionMessageSvc,$httpParamSerializerJQLike,$q,userInfoService,navigationSvc,Idle) {

		var service = {
			loadFileInfoViewData : loadFileInfoViewData,
			updateCaseDocketDocumentView : updateCaseDocketDocumentView,
			downloadFiles : downloadFiles,
			respondToCaseAccessRequest : respondToCaseAccessRequest,
			findIfThisDocsAreAlwaysVisible : findIfThisDocsAreAlwaysVisible,
			findIfThisDocsAreAlwaysProtected : findIfThisDocsAreAlwaysProtected,
			findIfThisDoesNotRequireFileUpload :findIfThisDoesNotRequireFileUpload,
			findIfThisIsAProtectedDecisionDocType :findIfThisIsAProtectedDecisionDocType,
			loadFileInfoListWhenCaseAccessReqDenied : loadFileInfoListWhenCaseAccessReqDenied,
			getMessageForTheTypeOfFilesThisUserCanAttach : getMessageForTheTypeOfFilesThisUserCanAttach,
			uploadPrimaryDocModal : uploadPrimaryDocModal,
			isThisFinalRedactedVersionOption : isThisFinalRedactedVersionOption,
			checkForErrorsinFileUpload : checkForErrorsinFileUpload,
		};

		return service;

		
		function loadFileInfoViewData(aNum,origSubmissionDate,docId) {
			
			var params = {
				"protestId" : aNum,
				"submissionDate" : origSubmissionDate,
				doc_Type_Id : docId,
				fileAlert : "Y"
			}
			
			return $http({
						url : '/epds/viewcasedocketFileInfo',
						method : 'POST',
						headers : {
							'Content-Type' : 'application/x-www-form-urlencoded'
						},
						data: $httpParamSerializerJQLike(_.omitBy(params, _.isNil)),
					}).then(
							function(data) {

								$rootScope.authenticated = true;
								userInfoService.setUserInfo(data.data.user_Info);
						
								var navigationObj = {
										navigationType : "caseDocketSheet",
										caseStatus : "N/A",
										roleId :  data.data.protestInfo.roleId,
										protestInfo : data.data.protestInfo,
										isViewOnly : data.data.protestInfo.viewOnly
								} 
								navigationSvc.setListOfRoutesBasedOnRole(navigationObj);

								
								return data.data;
							

							
							},
							function(error) {
								
								return error;
							});

		}
		
		function loadFileInfoListWhenCaseAccessReqDenied(protestId,submissionDate,docTypeId,fileAlert) {
			
			var 	params = {
					"protestId" : protestId,
					"submissionDate" : submissionDate,
					doc_Type_Id : docTypeId,
					fileAlert : fileAlert 
				}
				
				return $http({
							url : '/epds/case-access-request-denied-file-info',
							method : 'POST',
							headers : {
								'Content-Type' : 'application/x-www-form-urlencoded'
							},
							data: $httpParamSerializerJQLike(_.omitBy(params, _.isNil)),
						}).then(
								function(data) {
									
									userInfoService.setUserInfo(data.data.user_Info);
										
									return data.data;
								
								},
								function(error) {
									
									return error;
								});

			}
		function updateCaseDocketDocumentView (fileId,
				typeOfchange, newValue) {
			var bodyText = "";
			if (typeOfchange === "confidential") {
				bodyText = "You have successfully updated the confidential status for this case."
			} else if (typeOfchange === "delete") {
				bodyText = "You have  successfully deleted the file."
			}else if (typeOfchange === "markAsPrimary") {
				bodyText = "You have  successfully marked the document as primary."
			}else if (typeOfchange === "replaceDoc") {
				bodyText = "You have  successfully replaced the document."
			}

			
		var params = {
				file_Id : String(fileId),
				newValue : String(newValue),
			}
		
			return $http({
								url : '/epds/change-file-attribute/'+ typeOfchange,
								method : 'POST',
								headers : {
									'Content-Type' : 'application/x-www-form-urlencoded'
								},
								data: $httpParamSerializerJQLike(_.omitBy(params, _.isNil)),
							}).then(
							function(data) {

								var customModalOptions = {
									headerText : 'success',
									bodyText : bodyText,
									closeButtonText : 'OK',
									messageType : "success"
								};

								modalService.showModal({},
										customModalOptions).then(
										function(result) {
										});
							
								return data.data;
							},
							function(error) {
								console
										.log("Error occured when loading Request of Grantee or Third Party "
												+ JSON.stringify(error));
								return error;
							});		
					

		}
		
		function respondToCaseAccessRequest (fileId, approve,accessType){

			var params = {
				"file_Id" : fileId,
				"response" : approve,
				"accessType" : accessType,
			}

		 return $http({
					url : '/epds/respond-request-to-access',
					method : 'POST',
					headers : {
						'Content-Type' : 'application/x-www-form-urlencoded'
					},
					data: $httpParamSerializerJQLike(_.omitBy(params, _.isNil)),
				}).then(
							function(data) {
									
								var customAttr = {
										headerText : "Success"	,
										bodyText : "",
										modalType : "success",
										actionType : "",
									    cancelBtnReq : "N",
									    cancelBtnActionType : ""
									}
								
								if (accessType === "intervene" && approve === "Y"){
									customAttr.bodyText = "You have successfully acknowledged the Grantee or Third Party. "
									actionMessageSvc.showModal(customAttr);	
								} else if (accessType === "agency-rep-access" && approve === "Y"){
									customAttr.bodyText = "You have successfully acknowledged the agency's representative. "
									actionMessageSvc.showModal(customAttr);	
								}	
								
								
							
								
							 return data.data;
							},
							function(error) {
								
								return error;
							});		
					

		
		}
		function downloadFiles(caseInfObj) {
			
			var fileName  = $filter('camelCase')(caseInfObj.fileName)
			
			
			var params = {
				/*filename : caseInfObj.fileName,
				protestId : caseInfObj.a_No,
				userId : caseInfObj.user_Id,
				role : caseInfObj.role,*/
				fileId : caseInfObj.fileId
			}
			
	return $http({
				url : '/epds/downloaddashboardfile',
				method : 'POST',
				headers : {
					'Content-Type' : 'application/json'
				},
				responseType : 'arraybuffer',
				params : params
			}).then(
					function(response) {
						var data = response.data;
						var status = response.status;
						var headers = response.headers;
						
						var octetStreamMime = 'application/octet-stream';
						var success = false;
						// Get the headers
						headers = headers();
						try {
							
							var contentType = headers['content-type']
											|| octetStreamMime;
			                var isFileSaverSupported = !!new Blob;
			                var file = new Blob([data], { type: contentType });
			                saveAs(file, caseInfObj.fileName);
			                success = true;
			            } catch (e) {
			            	console.log("Filesaver is not supported with the following exception")
			            	console.log(e)
			            }
			            
			            if (!success) {
			            	
									var fileName = $filter('camelCase')(caseInfObj.fileName);
			
									// Determine the content type from the header or default
									// to "application/octet-stream"
									var contentType = headers['content-type']
											|| octetStreamMime;
			
									try {
										// Try using msSaveBlob if supported
										console.log("Trying saveBlob method ...");
										var blob = new Blob([ data ], {
											type : contentType
										});
										if (navigator.msSaveBlob)
											navigator.msSaveBlob(blob, filename);
										else {
											// Try using other saveBlob implementations, if
											// available
											var saveBlob = navigator.webkitSaveBlob
													|| navigator.mozSaveBlob
													|| navigator.saveBlob;
											if (saveBlob === undefined)
												throw "Not supported";
											saveBlob(blob, filename);
										}
										console.log("saveBlob succeeded");
										success = true;
									} catch (ex) {
										console
												.log("saveBlob method failed with the following exception:");
										console.log(ex);
									}

			            }
						
						if (!success) {
							// Get the blob url creator
							var urlCreator = window.URL || window.webkitURL
									|| window.mozURL || window.msURL;
							if (urlCreator) {
								// Try to use a download link
								var link = document.createElement('a');
								if ('download' in link) {
									// Try to simulate a click
									try {
										// Prepare a blob URL
										console
												.log("Trying download link method with simulated click ...");
										var blob = new Blob([ data ], {
											type : contentType
										});
										var url = urlCreator
												.createObjectURL(blob);
										link.setAttribute('href', url);

										// Set the download attribute (Supported
										// in Chrome 14+ / Firefox 20+)
										link.setAttribute("download", filename);

										// Simulate clicking the download link
										var event = document
												.createEvent('MouseEvents');
										event.initMouseEvent('click', true,
												true, window, 1, 0, 0, 0, 0,
												false, false, false, false, 0,
												null);
										link.dispatchEvent(event);
										console
												.log("Download link method with simulated click succeeded");
										success = true;

									} catch (ex) {
										console
												.log("Download link method with simulated click failed with the following exception:");
										console.log(ex);
									}
								}

								if (!success) {
									// Fallback to window.location method
									try {
										// Prepare a blob URL
										// Use application/octet-stream when
										// using window.location to force
										// download
										console
												.log("Trying download link method with window.location ...");
										var blob = new Blob([ data ], {
											type : octetStreamMime
										});
										var url = urlCreator
												.createObjectURL(blob);
										window.location = url;
										console
												.log("Download link method with window.location succeeded");
										success = true;
									} catch (ex) {
										console
												.log("Download link method with window.location failed with the following exception:");
										console.log(ex);
									}
								}

							}
						}

						if (!success) {
							// Fallback to window.open method
							console
									.log("No methods worked for saving the arraybuffer, using last resort window.open");
							window.open(httpPath, '_blank', '');
						}
					}
			).catch(function(response) {
				console.log("Request failed with status: " + response.status);

				// Optionally write the error out to scope
				var errorDetails = "Request failed with status: " + response.status;
			});		
			
		}
		
		function getMessageForTheTypeOfFilesThisUserCanAttach(role){
			
			var message = "";
			
			
			if (role === "PROTESTER" || role === "INTERVENOR"){
				message = "Only PDF files can be attached";
			}else if ((role === "AGENCY ADMIN" || role === "AGENCY ATTORNEY" || (role
					.indexOf("AGENCY") >= 0))){
				message = "Only PDF files can be attached";
				
			}else if ((role === "GAO ATTORNEY" || role === "GAO SUPERVISOR")){
				message = "Only PDF can be attached";
			}else if ((role === "GAO ADMIN")){
				message = "Only PDF files can be attached";
			}else{
				message = null;
			}
			
			return  $q.when(message)
			
		}
		
		function uploadPrimaryDocModal(fileInfoList) {
			 /*Idle.watch();*/
			 $uibModal
				.open({
					templateUrl : 'scripts/app/case-docket-file-info/choosePrimaryDoc.tpl.htm',
					controller : UploadPrimaryDocModalInstanceCtrl,
					resolve : {
						fileInfoList : function() {
							return fileInfoList;
						}
					},
					animation : true,
					size : 'md',
				}).result.catch(angular.noop);

		}
		
		
		function checkForErrorsinFileUpload(singleFileUpload,multipleFileUpload) {

			var fileUpload = _iterateTheFileArray(singleFileUpload);
			
			if (!fileUpload.fileUploadEr && !fileUpload.fileUploadInProgress){
			
				fileUpload = _iterateTheFileArray(multipleFileUpload);
					
			}
			
			return $q.when(fileUpload)

		}
		
		
		function _iterateTheFileArray(fileArray){
			
			var obj = {};
			
			obj.fileUploadEr = false;
			
			obj.fileUploadInProgress = false;
			
			if ('undefined' !== typeof fileArray){
				
				for (var i = 0, len = fileArray.files.length; i < len; i++) {
					 
					var eachFile = fileArray.files[i];
					
					if (eachFile.error) {
						obj.fileUploadEr = true;
				    break;
				  }else if (!eachFile.isComplete()){
					  obj.fileUploadInProgress = true;
					  break;
				  }
				}
			}
			
			
			return obj;
		}
		
		function findIfThisDocsAreAlwaysVisible(docId){
			
			var listOfDocIds = [2,3,4,5,12,17,19,20,21,22,27,28,30,31,32,33,38,39,
			                    41,42,43,44,46,53,54,56,57,58,59,67,70,82,83,91,92,100,101,103,104,
			                    105,106,107,108,111,112,114,115,116,117,118,121,122,124,125,126,127,
			                    130,131,133,134,135,136,139,140,142,143,144,145,147,154,155,157,158,159,
			                    160,161,162,111,130,121,139,46,56,147,172,235,236,237,238]
				
			var isAlwaysVisible = $filter('contains')(listOfDocIds,docId);
			
			return $q.when(isAlwaysVisible)
		
		
		}
		
		function findIfThisDocsAreAlwaysProtected(docId){
			
			var listOfDocIds = [120,110,138,129,55,84,93,102,71,156,40,168,29,18,220,219,218,217];
				
			var isAlwaysProtected = $filter('contains')(listOfDocIds,docId);
			
			return $q.when(isAlwaysProtected)
		
		
		}
		
		
		function findIfThisDoesNotRequireFileUpload(docId){
			
			var listOfDocIds = [27,38,12,179,53,100,91,82,67,154,
			                    234,233,231,232,215,214,213,212,211,210]
				
			var isFileUploadRequired = $filter('contains')(listOfDocIds,docId);
			
			return $q.when(isFileUploadRequired)
			
		}
		
		function isThisFinalRedactedVersionOption(docId){
			
			var listOfDocIds = [17,101,92,83,28,70,39,54,180,155]
				
			var flag = $filter('contains')(listOfDocIds,docId);
			
			return $q.when(flag)
		
		
		}
		
		function findIfThisIsAProtectedDecisionDocType(docId){
			
			var listOfDocIds = [110,120,138,129]
				
			var flag = $filter('contains')(listOfDocIds,docId);
			
			return $q.when(flag)
		
		
		}
	}
})();
