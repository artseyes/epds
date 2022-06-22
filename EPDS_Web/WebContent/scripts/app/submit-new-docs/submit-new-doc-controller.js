angular.module(
    'epdsApp.caseDocketSheet')
.controller('SubmitNewDocCtrl', SubmitNewDocCtrl);

SubmitNewDocCtrl.$inject = ['$scope', '$log', '$rootScope', '$location', '$http', '$route', '$routeParams',
    'modalService', '$uibModal', '$templateCache', '$templateRequest', '$sce', '$filter', '$compile', '$interpolate',
    '$cookies', 'submitNewDocDataSvc', 'actionMessageSvc', 'fileInfoViewSvc', 'SubmitNewDocData', 'regEx', 'toolTip',
    'csrfService', '$timeout']

/* @ngInject */
function SubmitNewDocCtrl($scope, $log, $rootScope, $location, $http, $route, $routeParams, modalService, $uibModal,
                          $templateCache, $templateRequest, $sce, $filter, $compile, $interpolate, $cookies,
                          submitNewDocDataSvc, actionMessageSvc, fileInfoViewSvc, SubmitNewDocData, regEx, toolTip,
                          csrfService, $timeout) {
	$timeout(function() {
		$('#doctype1').focus();
	}, 0);

    var vm = this;
    $scope.regEx = regEx;
    $scope.toolTip = toolTip;
    $scope.urlEncodedANum = $routeParams.aNum

    $scope.totalNumberOfPrimaryDocumentsAdded = 0;
    $scope.totalNumberOfAssociatedDocumentsAdded = 0;

    $scope.fileUploadErrors = [];

    vm.data = SubmitNewDocData;
    $scope.generateDocumentBtn = SubmitNewDocData.generateDocumentBtn;
    $scope.form = SubmitNewDocData.form;
    $scope.user_Info = SubmitNewDocData.userProfileInfo;
    $scope.role = SubmitNewDocData.protestInfo.role;
    $scope.protestInfo = SubmitNewDocData.protestInfo;
    $scope.caseStatus = SubmitNewDocData.protestInfo.case_Status;
    $scope.attorneyInfo = SubmitNewDocData.attorneyInfo;

    $scope.selectedOption = {
        doc_Type_Id: "0",
        doc_Type_Desc: "Please Select Type of Document",
        filing_Order: 0
    }

    if ($scope.role != "GAO ADMIN") {
        $scope.doc_InfoList = SubmitNewDocData.doc_InfoList;

        $scope.doc_InfoList = $filter("removeWith")($scope.doc_InfoList, {
            doc_Type_Id: "1",
        })
        $scope.doc_InfoList = $filter("removeWith")($scope.doc_InfoList, {
            doc_Type_Id: "163",
        })
        $scope.doc_InfoList = $filter("removeWith")($scope.doc_InfoList, {
            doc_Type_Id: "164",
        })
        $scope.doc_InfoList = $filter("removeWith")($scope.doc_InfoList, {
            doc_Type_Id: "165",
        })
    } else {
        $scope.doc_InfoList = SubmitNewDocData.fullDocInfoList;
    }

    $scope.doc_InfoList.push($scope.selectedOption);

    fileInfoViewSvc.getMessageForTheTypeOfFilesThisUserCanAttach($scope.role.trim()).then(function (message) {
        $scope.uploadDocMessage = message;
    })

    $scope.$watch('selectedOption', function () {
        if (typeof $scope.selectedOption != 'undefined') {
            /*GE-1174: Including Proposed Public Version Doc_Ids - 217, 218, 219, 220
            * to isProtectedDecisionDocType */
            $scope.isProtectedDecisionDocType = $filter('contains')([110, 120, 138, 129, 217, 218, 219, 220], $scope.selectedOption.doc_Type_Id);
            var selectedOptionDocId;

            if ($scope.selectedOption.doc_Type_Id == 0) {
                selectedOptionDocId = $scope.originalSelectedOptionDocId;
            } else {
                selectedOptionDocId = $scope.selectedOption.doc_Type_Id;
            }

            fileInfoViewSvc.findIfThisDocsAreAlwaysVisible(selectedOptionDocId).then(function (data) {
                $scope.isAlwaysVisisble = data;
            })

            fileInfoViewSvc.findIfThisDoesNotRequireFileUpload(selectedOptionDocId).then(function (data) {
                $scope.isFileUploadNotRequired = data;
            })

            fileInfoViewSvc.findIfThisDocsAreAlwaysProtected(selectedOptionDocId).then(function (data) {
                $scope.isAlwaysProtected = data;
            })

            submitNewDocDataSvc.displayGenerateTemplateDocButton($scope.role, $scope.selectedOption, $scope.originalSelectedOptionDocId, $scope.doc_InfoList).then(function (data) {
                $scope.showGenerateDocTempButton = data.showGenerateDocTempButton;
                if ($scope.showGenerateDocTempButton) {
                    $scope.primaryBtnText = "Upload Template"
                    $rootScope.templateCounter = undefined;
                    $scope.deleteTempPdfFile = true;
                    $scope.generateTempBtnText = "Generate Template";
                    $scope.htmlContent = null;
                    $rootScope.data = null;
                } else {
                    $scope.primaryBtnText = "Upload Primary Document"
                }

            });

            $scope.selectedOption.doc_Type_Desc = $scope.selectedOption.doc_Type_Desc;

            if ($scope.selectedOption.doc_Type_Desc
                .indexOf("_") > -1 && typeof $scope.selectedOption.doc_Type_Desc.split("_")[1] != "undefined"
                && $scope.selectedOption.doc_Type_Desc.split("_")[1] == "") {

                submitNewDocDataSvc.editDocumentDescriptions($scope.selectedOption).then(function (modalInstance) {

                    modalInstance.result.then(function (result) {
                        if (typeof result !== 'undefined'
                            && result !== ""
                            && result !== null) {

                            $scope.selectedOption.doc_Type_Desc = $scope.selectedOption.doc_Type_Desc.split("_")[0] + result;
                            $scope.docDescFiller = result;
                            $scope.originalSelectedOptionDocId = $scope.selectedOption.doc_Type_Id;
                            $scope.selectedOption.doc_Type_Id = 0;

                        }

                        $scope.doc_InfoList = $filter('filter')($scope.doc_InfoList,
                            {doc_Type_Id: $scope.selectedOption.doc_Type_Id}, function (obj, test) {
                                return obj !== test;
                            });

                        $scope.doc_InfoList.push($scope.selectedOption)

                    });
                })

            }

        }
    })


    $scope.cancelPrimaryDocument = function ($flow) {
        $flow.cancel();
        $scope.totalNumberOfPrimaryDocumentsAdded = 0;
    }


    $scope.cancelAssociatedDocuments = function ($flow) {
        $flow.cancel();
        $scope.totalNumberOfAssociatedDocumentsAdded = 0;
    }


    $scope.fileUploadRetry = function ($file, $flow) {

        //need to see if we need this
    }


    $scope.fileUploadStarted = function ($file, $message, $flow) {

        $scope.fileUploadInProgress = true;
    }

    $scope.fileUploadError = function ($file, $message, $flow) {
        var message = {};
        $scope.fileUploadEr = true;
        var fileName = $file.name;
        try {
            if (JSON.parse($message)) {
                message = JSON.parse($message);
            }

        } catch (ex) {

            message = {
                error: {
                    fileError: fileName + " was not uploaded. Please upload this file again."
                }
            }
            console.log("Problem creating JSON", ex);
        }


        $scope.fileUploadErrors.push(message.error);

        $flow.removeFile($file);

    }


    $scope.fileUploadSuccess = function ($file, $message, $flow) {

    }


    $scope.assignSingleFileUploadFlowInstaceToScope = function (file, event, flow) {

        /*checkIfThisFileCanBeUploaded($scope, file,$scope.role.trim(),flow);*/
        if (checkIfThisUserCanUploadDocumentWithThisFileExtension(modalService, file, $scope.role.trim())) {

            flow.opts.query = {
                "attachmentType": $filter('camelCase')(
                    $scope.selectedOption.doc_Type_Desc),
                "fileIdentifierCode": "P",
                "a_No": $scope.protestInfo.a_No
            };

            flow.opts.headers = {

                'X-XSRF-TOKEN': csrfService.token
            };
            $scope.primaryDocumentAdded = true;
            $scope.singleFileUpload = flow
            $scope.totalNumberOfPrimaryDocumentsAdded = $scope.singleFileUpload.files.length;


        } else {
            return false;
        }

    }


    $scope.assignMultipleFileUploadFlowInstaceToScope = function (
        file, event, flow) {


        vm.multipleFileUpload = flow;

        /*checkIfThisFileCanBeUploaded($scope, file,$scope.role.trim(),flow);*/

        if (checkIfThisUserCanUploadDocumentWithThisFileExtension(
            modalService, file, $scope.role.trim())) {

            flow.opts.query = {
                "attachmentType": $filter('camelCase')(
                    $scope.selectedOption.doc_Type_Desc),
                "fileIdentifierCode": "A",
                "a_No": $scope.protestInfo.a_No
            };

            flow.opts.headers = {
                'X-XSRF-TOKEN': csrfService.token
            };
            $scope.multipleFileUpload = flow


            $scope.totalNumberOfAssociatedDocumentsAdded = $scope.multipleFileUpload.files.length;
        } else {
            return false;
        }

    }

    $scope.downloadTempPdf = function (selectedOption) {
        var clonedSelectedOption = angular.extend({}, selectedOption)

        if (clonedSelectedOption.doc_Type_Id == 0) {
            clonedSelectedOption.doc_Type_Id = $scope.originalSelectedOptionDocId;
        }

        var fileName = $filter('camelCase')(clonedSelectedOption.doc_Type_Desc)
        if (fileName.toLowerCase() === 'protecteddecision')
            fileName = 'noticeOfIssuanceOfProtectedDecision';
        submitNewDocDataSvc.downloadTempPdf(clonedSelectedOption, fileName).then(function () {

        })
    }
    $scope.isNumber = angular.isNumber;
    $scope.deleteTempPdfFile = false;
    $scope.deleteTempPdf = function (selectedOption) {
        $rootScope.templateCounter = undefined;
        $scope.deleteTempPdfFile = true;
        $scope.generateTempBtnText = "Generate Template";
        $scope.htmlContent = null;
        $rootScope.data = null;
    }

    $scope.isNaN = function (x) {

        return isNaN(x)
    }

    $scope.reset = function () {

        $route.reload();
    }


    $scope.primaryDocumentIsUploadedToServer = function () {


        var form = {
            "docId": ($scope.selectedOption.doc_Type_Id != 0 ? $scope.selectedOption.doc_Type_Id : $scope.originalSelectedOptionDocId),
            "typeofdocument": $scope.selectedOption.doc_Type_Desc,
            "docDescFiller": ($scope.selectedOption.doc_Type_Id == 0 ? $scope.docDescFiller : null),
            "isDocConfidential": $scope.form.isDocConfidential,
            "comments": $scope.form.comments,
            "requestType": "other",
            "protestId": $scope.protestInfo.a_No,
        }

        if ($scope.showGenerateDocTempButton
            && $scope.htmlContent) {
            form.content = $scope.htmlContent;
        }


        fileInfoViewSvc.checkForErrorsinFileUpload($scope.singleFileUpload, $scope.multipleFileUpload).then(function (data) {
            $scope.fileUploadEr = data.fileUploadEr;

            if (!$scope.isRequestAlreadySubmitted) {

                if ($scope.form.attachAssociatedDoc === 'N'
                    && !$scope.fileUploadEr
                    && !data.fileUploadInProgress
                    && ($scope.fileUploadErrors.length <= 0)) {

                    $scope.isRequestAlreadySubmitted = true;
                    submitNewDocDataSvc.submitThisRequestAfterAllFilesAreUploaded(form, $scope.role.trim(), $scope.protestInfo.a_No);
                } else if ($scope.form.attachAssociatedDoc === 'Y'
                    && !$scope.fileUploadEr
                    && !data.fileUploadInProgress
                    && ($scope.fileUploadErrors.length <= 0)) {

                    $scope.isRequestAlreadySubmitted = true;
                    submitNewDocDataSvc.submitThisRequestAfterAllFilesAreUploaded(form, $scope.role.trim(), $scope.protestInfo.a_No);
                }
            }
        });

    }

    $scope.associatedDocumentsIsUploadedToServer = function () {

        var form = {
            "docId": ($scope.selectedOption.doc_Type_Id != 0 ? $scope.selectedOption.doc_Type_Id : $scope.originalSelectedOptionDocId),
            "typeofdocument": $scope.selectedOption.doc_Type_Desc,
            "docDescFiller": ($scope.selectedOption.doc_Type_Id == 0 ? $scope.docDescFiller : null),
            "isDocConfidential": $scope.form.isDocConfidential,
            "comments": $scope.form.comments,
            "requestType": "other",
            "protestId": $scope.protestInfo.a_No,
        }

        if ($scope.showGenerateDocTempButton
            && $scope.htmlContent) {
            form.content = $scope.htmlContent;
        }


        fileInfoViewSvc.checkForErrorsinFileUpload($scope.singleFileUpload, $scope.multipleFileUpload).then(function (data) {
            $scope.fileUploadEr = data.fileUploadEr;

            if (!$scope.isRequestAlreadySubmitted) {

                if ($scope.form.attachAssociatedDoc === 'N'
                    && !$scope.fileUploadEr
                    && !data.fileUploadInProgress
                    && ($scope.fileUploadErrors.length <= 0)) {

                    $scope.isRequestAlreadySubmitted = true;
                    submitNewDocDataSvc.submitThisRequestAfterAllFilesAreUploaded(form, $scope.role.trim(), $scope.protestInfo.a_No, $scope.htmlContent);
                } else if ($scope.form.attachAssociatedDoc === 'Y'
                    && !$scope.fileUploadEr
                    && !data.fileUploadInProgress
                    && ($scope.fileUploadErrors.length <= 0)) {

                    $scope.isRequestAlreadySubmitted = true;
                    submitNewDocDataSvc.submitThisRequestAfterAllFilesAreUploaded(form, $scope.role.trim(), $scope.protestInfo.a_No);
                }
            }
        });


    }


    $scope.hideCancelButton = false;

    $scope.registerSubmitNewDocForm = function (submitnewDocForm,
                                                isDocConfidential, attachAssociatedDocs, comments) {

        if (submitnewDocForm.comments.$invalid) {
            isCommentsInValidFormat(actionMessageSvc);
            return;
        } else if (submitnewDocForm.comments == "") {
            $scope.form.comments = null

        }

        $scope.isRequestAlreadySubmitted = false;

        var isFinalRedactedVersion = false;
        var docId = ($scope.selectedOption.doc_Type_Id != 0 ? $scope.selectedOption.doc_Type_Id : $scope.originalSelectedOptionDocId)

        fileInfoViewSvc.isThisFinalRedactedVersionOption(docId).then(function (data) {
            isFinalRedactedVersion = data;

            if (isFinalRedactedVersion) {
                var bodyText = "<p>Final Redacted Versions of filings will be accessible by all parties, including those who are not admitted to the protective order.  </p>"
                    + "<p>Do you want to proceed ?</p>"

                var customAttr = {
                    headerText: "Warning",
                    bodyText: bodyText,
                    modalType: "warning",
                    actionType: "samepage",
                    cancelBtnReq: "Y",
                    cancelBtnActionType: "samepage",
                    okAndCancelText: "Y",
                    okBtnText: "Yes",
                    cancelBtnText: "No"
                }

                actionMessageSvc.showModal(customAttr).then(function (result) {

                    if (result.cancelBtnClicked != "Y") {
                        $scope.submitData(
                            isDocConfidential, attachAssociatedDocs, comments)
                    }

                })

            } else {
                $scope.submitData(
                    isDocConfidential, attachAssociatedDocs, comments);
            }
        })
    }

    $scope.submitData = function (isDocConfidential, attachAssociatedDocs, comments) {
        $scope.fileUploadErrors = [];
        var isProtectedDecisionUpload = $scope.isProtectedDecisionDocType && $scope.htmlContent;
        var isPrimaryDocumentAttached = document.getElementById('primary-document-table').rows.length;
        var checkIfThisDocDescIsPopulated = ($scope.selectedOption.doc_Type_Desc.split("_")[1] == null) || ($scope.selectedOption.doc_Type_Desc.split("_")[1] == "");

        if (!checkIfTotalUploadSizeExceedsTheMaxSize($scope, modalService)) {
            return
        }

        fileInfoViewSvc.checkForErrorsinFileUpload($scope.singleFileUpload, $scope.multipleFileUpload).then(function (data) {
            $scope.fileUploadEr = data.fileUploadEr;

            if ($scope.fileUploadEr) {

                var customAttr = {
                    headerText: "Error",
                    bodyText: "Please fix the errors in the files and then submit the form. ",
                    modalType: "error",
                    actionType: "",
                    cancelBtnReq: "N",
                    cancelBtnActionType: ""
                }

                actionMessageSvc.showModal(customAttr);

                return false;
            }

        });


        if ($scope.isAlwaysVisisble) {
            isDocConfidential = "N";
        } else if ($scope.isAlwaysProtected) {
            isDocConfidential = "Y";
        }

        if (($scope.generateTempBtnText === 'Edit Template' && !$scope.isProtectedDecisionDocType)) {
            attachAssociatedDocs = "N";
            isPrimaryDocumentAttached = 0;
        }


        if ($scope.selectedOption.doc_Type_Desc === "Please Select Type of Document") {//check if dropdown option is selected
            var customAttr = {
                headerText: "Error",
                bodyText: "Please Select Type of Document.",
                modalType: "error",
                actionType: "",
                cancelBtnReq: "N",
                cancelBtnActionType: ""
            }

            actionMessageSvc.showModal(customAttr);
        } else if ($scope.selectedOption.doc_Type_Desc
        .indexOf("_") > -1 && checkIfThisDocDescIsPopulated) {//if this is blank option check if doc desc filler is populated

            submitNewDocDataSvc.editDocumentDescriptions($scope.selectedOption).then(function (modalInstance) {

                modalInstance.result.then(function (result) {
                    if (result && result !== "") {

                        $scope.selectedOption.doc_Type_Desc = $scope.selectedOption.doc_Type_Desc.split("_")[0] + result;
                        $scope.docDescFiller = result;
                        $scope.originalSelectedOptionDocId = $scope.selectedOption.doc_Type_Id;
                        $scope.selectedOption.doc_Type_Id = 0;

                    }

                    $scope.doc_InfoList = $filter('filter')($scope.doc_InfoList,
                        {doc_Type_Id: $scope.selectedOption.doc_Type_Id}, function (obj, test) {
                            return obj !== test;
                        });

                    $scope.doc_InfoList.push($scope.selectedOption)

                });
            })

        } else if ($scope.isProtectedDecisionDocType && !$scope.htmlContent) { //check if this is protected decision and html content is nul meainig template is not populated
            var customAttr = {
                headerText: "Error",
                bodyText: "<p>Please generate template.</p>",
                modalType: "error",
                actionType: "",
                cancelBtnReq: "N",
                cancelBtnActionType: ""
            }

            actionMessageSvc.showModal(customAttr);
        }/*else if ( (($scope.showGenerateDocTempButton && $scope.htmlContent == null) || isProtectedDecisionUpload) && isPrimaryDocumentAttached < 2){
							
							var customAttr = {
									headerText : "Error"	,
									bodyText : "<p>Please briefly describe the document you are filing (e.g., objection to agency's 5 day letter, motion for an extension).</p>",
									modalType : "error",
									actionType : "",
								    cancelBtnReq : "N",
								    cancelBtnActionType : ""
								}
							
							actionMessageSvc.showModal(customAttr);
							
						}*/

        else if (isProtectedDecisionUpload && isPrimaryDocumentAttached < 2) {
            //come back and correct this condition
            var customAttr = {
                headerText: "Error",
                bodyText: "<p>Please upload a protected decision. </p>",
                modalType: "error",
                actionType: "",
                cancelBtnReq: "N",
                cancelBtnActionType: ""
            }

            actionMessageSvc.showModal(customAttr);

        } else if (($scope.showGenerateDocTempButton && $scope.htmlContent == null && !isProtectedDecisionUpload) && isPrimaryDocumentAttached < 2) {

            //template option other than protected decision was selected  but neither template nor primary document was uploaded
            var customAttr = {
                headerText: "Error",
                bodyText: "<p>Please either generate the template or upload the document.</p>",
                modalType: "error",
                actionType: "",
                cancelBtnReq: "N",
                cancelBtnActionType: ""
            }

            actionMessageSvc.showModal(customAttr);

        } else {


            try {
                if (!$scope.singleFileUpload
                    || ($scope.generateTempBtnText === 'Edit Template' && !$scope.isProtectedDecisionDocType)) {
                    $scope.totalNumberOfPrimaryDocumentsAdded = 0;
                } else {
                    $scope.totalNumberOfPrimaryDocumentsAdded = $scope.singleFileUpload.files.length;
                }

                if (!$scope.multipleFileUpload
                    || ($scope.generateTempBtnText === 'Edit Template'
                        && !$scope.isProtectedDecisionDocType)) {
                    $scope.totalNumberOfAssociatedDocumentsAdded = 0;
                } else {
                    $scope.totalNumberOfAssociatedDocumentsAdded = $scope.multipleFileUpload.files.length;
                }
            } catch (ex) {
                console.log(ex)
            }

            var isUploadNotRequired = false;


            if ($scope.isFileUploadNotRequired == true || ($scope.htmlContent != null)) {
                isUploadNotRequired = true;
            }

            submitNewDocDataSvc.displayInvalidFormMessages(isDocConfidential, attachAssociatedDocs, isUploadNotRequired,
                $scope.totalNumberOfPrimaryDocumentsAdded, $scope.totalNumberOfAssociatedDocumentsAdded).then(function (data) {

                if ((data.isFormValid
                    && data.isPrimaryDocumentAttached) &&
                    (!$scope.htmlContent || isProtectedDecisionUpload) && $scope.isFileUploadNotRequired == false) {

                    if (data.attachAssociatedDocs == 'Y') {
                        $scope.singleFileUpload.upload()
                        $scope.multipleFileUpload.upload()
                    } else if (data.attachAssociatedDocs == 'N') {
                        $scope.singleFileUpload.upload()
                    }
                }

                if ($scope.showGenerateDocTempButton
                    && $scope.htmlContent && data.isFormValid) {

                    var form = {
                        "docId": ($scope.selectedOption.doc_Type_Id != 0 ? $scope.selectedOption.doc_Type_Id : $scope.originalSelectedOptionDocId),
                        "typeofdocument": $scope.selectedOption.doc_Type_Desc,
                        "docDescFiller": ($scope.selectedOption.doc_Type_Id == 0 ? $scope.docDescFiller : null),
                        "isDocConfidential": $scope.form.isDocConfidential,
                        "comments": $scope.form.comments,
                        "requestType": "other",
                        "protestId": $scope.protestInfo.a_No,
                        "content": $scope.htmlContent
                    }

                    if (!isProtectedDecisionUpload)
                        submitNewDocDataSvc.submitThisRequestAfterAllFilesAreUploaded(form, $scope.role.trim(), $scope.protestInfo.a_No);
                }


                if ($scope.isFileUploadNotRequired) {

                    var form = {
                        "docId": ($scope.selectedOption.doc_Type_Id != 0 ? $scope.selectedOption.doc_Type_Id : $scope.originalSelectedOptionDocId),
                        "typeofdocument": $scope.selectedOption.doc_Type_Desc,
                        "docDescFiller": ($scope.selectedOption.doc_Type_Id == 0 ? $scope.docDescFiller : null),
                        "isDocConfidential": "N",
                        "comments": $scope.form.comments,
                        "requestType": "other",
                        "protestId": $scope.protestInfo.a_No,
                    }

                    submitNewDocDataSvc.submitThisRequestAfterAllFilesAreUploaded(form, $scope.role.trim(), $scope.protestInfo.a_No);
                }
            })

        }
    }

    $scope.generateTemplateBasedOnDocumentType = function (typeOfDoc) {

        var clonedTypeOfDoc = angular.extend({}, typeOfDoc);

        if (clonedTypeOfDoc.doc_Type_Id == 0) {
            clonedTypeOfDoc.doc_Type_Id = $scope.originalSelectedOptionDocId;
        }

        submitNewDocDataSvc.generateTemplateBasedOnDocumentType(clonedTypeOfDoc, $scope.protestInfo.a_No).then(function (data) {
            var companyName = data.protestInfo && data.protestInfo.company_Name;
            var bNum = data.protestInfo && data.protestInfo.b_No;
            if (data.attorneyInfo && data.attorneyInfo.middle_initial) {
                data.attorneyInfo.middle_initial = data.attorneyInfo.middle_initial + ".";
            }

            if (data.consolidatedBnums) {
                data.protestInfo.b_No = data.consolidatedBnums
            }

            if (data.consolidatedProtesterNames) {
                data.protestInfo.company_Name = data.consolidatedProtesterNames
            }
            $scope.protestInfo = data.protestInfo || SubmitNewDocData.protestInfo;
            $scope.attorneyInfo = data.attorneyInfo;
            $scope.todaysDate = moment().format('MMMM DD, YYYY')

            $scope.reportDueDate = data.reportDueDate;

            $scope.htmlContent = $interpolate(data.htmlContent)($scope)

            $scope.protestInfo.b_No = bNum;
            $scope.protestInfo.company_Name = companyName;

            submitNewDocDataSvc.generateTemplateModal(clonedTypeOfDoc, "templateModalInstanceCtrl", $scope.htmlContent).then(function (data) {
                if ($scope.showGenerateDocTempButton
                    && data != null
                    && typeof data != 'undefined') {
                    $scope.htmlContent = data;
                    $scope.deleteTempPdfFile = false;
                    if (isNaN($rootScope.templateCounter)) {
                        $scope.htmlContent = null;
                        $scope.generateTempBtnText = "Generate Template";
                    } else {
                        $scope.generateTempBtnText = "Edit Template";
                    }
                    $scope.tempFileName = $rootScope.tempPdfFileName;
                }
            })


        });
    };
}

/* @ngInject */
editDocumentDescriptionsModalInstanceCtrl.$inject = ['$scope',
    '$uibModalInstance', 'items', 'modalService', '$rootScope', 'actionMessageSvc', 'regEx', 'toolTip'];

/* @ngInject */
function editDocumentDescriptionsModalInstanceCtrl($scope, $uibModalInstance, items, modalService, $rootScope, actionMessageSvc, regEx, toolTip) {


    $scope.regEx = regEx;
    $scope.toolTip = toolTip;

    $scope.headerText = items.selectedOption
    $scope.OK = function (docTypeDesc) {
        var customAttr = {
            headerText: "Error",
            bodyText: "Please briefly describe the document you are filing.(E.g., objection to agency's 5 day letter, request for an extension, etc.).",
            modalType: "error",
            actionType: "",
            cancelBtnReq: "N",
            cancelBtnActionType: ""
        }

        if (docTypeDesc == null || docTypeDesc == "") {

            actionMessageSvc.showModal(customAttr).then(function (result) {

                /*modalInstance.result.then(function (result) {
                    console.log("my custom modal is working ",result)
                });*/
            })

        } else {
            $uibModalInstance.close(docTypeDesc);
        }
    }

    $scope.cancel = function (docTypeDesc) {
        $uibModalInstance.close("");
    }
}


templateModalInstanceCtrl.$inject = ['$scope', '$rootScope', '$uibModalInstance',
    'items', 'submitNewDocDataSvc'];

/* @ngInject */
function templateModalInstanceCtrl($scope, $rootScope, $uibModalInstance,
                                   items, submitNewDocDataSvc) {

    $scope.title = items.title;
    if ($rootScope.data == null || typeof $rootScope.data === 'undefined' || $rootScope.currentTemplateDocId != items.typeOfDoc.doc_Type_Id) {
        $scope.data = items.htmlContent;
    } else {
        $scope.data = $rootScope.data;
    }
    $scope.ok = function (htmlContent) {
        $rootScope.templateCounter = 0;
        $rootScope.templateCounter++;
        $scope.data = htmlContent;
        $rootScope.data = $scope.data;
        $rootScope.currentTemplateDocId = items.typeOfDoc.doc_Type_Id;
        $rootScope.setData($rootScope.data);
        submitNewDocDataSvc.createPdf(htmlContent, items.typeOfDoc).then(function (data) {
            $rootScope.tempPdfFileName = data.fileName;
        });

        $uibModalInstance.close(htmlContent);
    };

    $scope.cancel = function (htmlContent) {
        $rootScope.templateCounter--;
        $scope.data = htmlContent;
        $uibModalInstance.close(htmlContent);
    };

}
