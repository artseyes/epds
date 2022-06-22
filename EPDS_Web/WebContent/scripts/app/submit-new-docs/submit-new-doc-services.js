(function () {
    'use strict';

    var serviceId = 'submitNewDocDataSvc';

    angular.module('epdsApp.caseDocketSheet').factory(
        serviceId,
        ['$log', '$rootScope', '$location', '$http', '$route',
            '$routeParams', 'modalService', '$uibModal',
            '$templateCache', '$templateRequest', '$sce', '$filter',
            '$compile', '$interpolate', '$cookies', 'userInfoService', '$httpParamSerializerJQLike',
            '$q', 'actionMessageSvc', 'fileInfoViewSvc', 'navigationSvc', 'Idle', 'base64', submitNewDocDataSvc]);

    /* @ngInject */
    function submitNewDocDataSvc($log, $rootScope, $location, $http, $route,
                                 $routeParams, modalService, $uibModal,
                                 $templateCache, $templateRequest, $sce, $filter,
                                 $compile, $interpolate, $cookies, userInfoService, $httpParamSerializerJQLike, $q, actionMessageSvc, fileInfoViewSvc, navigationSvc, Idle, base64) {

        var service = {
            loadSubmitNewDocPage: loadSubmitNewDocPage,
            submitThisRequestAfterAllFilesAreUploaded: submitThisRequestAfterAllFilesAreUploaded,
            displayGenerateTemplateDocButton: displayGenerateTemplateDocButton,
            editDocumentDescriptions: editDocumentDescriptions,
            generateTemplateBasedOnDocumentType: generateTemplateBasedOnDocumentType,
            generateTemplateModal: generateTemplateModal,
            displayInvalidFormMessages: displayInvalidFormMessages,
            createPdf: createPdf,
            downloadTempPdf: downloadTempPdf,
            replaceDoc: replaceDoc

        };

        return service;

        function loadSubmitNewDocPage(aNum) {


            return $http({
                url: '/epds/submit-new-doc-form',
                method: 'POST',
                headers: {
                    'Accept': 'application/json',
                    'Content-Type': 'application/json'
                },
                params: {
                    aNum: base64.urldecode(aNum)
                }

            }).then(
                function (data) {

                    $rootScope.authenticated = true;
                    userInfoService
                    .setUserInfo(data.data.userProfileInfo);

                    var navigationObj = {
                        navigationType: "caseDocketSheet",
                        roleId: data.data.protestInfo.roleId,
                        protestInfo: data.data.protestInfo,
                        isViewOnly: data.data.protestInfo.viewOnly //come back later
                    }
                    navigationSvc.setListOfRoutesBasedOnRole(navigationObj);

                    if (typeof data.data.role != 'undefined') {
                        if (data.data.role === "GAO ATTORNEY") {
                            data.data.hideOtherOptions = "AT"
                        } else if (data.data.role === "GAO SUPERVISOR") {
                            data.data.hideOtherOptions = "S"
                        } else if (data.data.role
                        .indexOf("AGENCY") >= 0) {
                            data.data.hideOtherOptions = "AG"
                        } else {
                            data.data.hideOtherOptions = "P"
                        }
                    } else {
                        data.data.hideOtherOptions = "P"
                    }

                    return _getSubmitNewDocData(data.data);

                },
                function (error) {

                    return error;
                });

        }

        function editDocumentDescriptions(selectedOption) {
            var items = {
                selectedOption: selectedOption
            }
            var modalInstance = $uibModal.open({
                templateUrl: 'scripts/app/submit-new-docs/fill-out-doc-desc.tpl.html?bust=' + Math.random().toString(36).slice(2),
                controller: editDocumentDescriptionsModalInstanceCtrl,
                resolve: {
                    items: function () {
                        return items;
                    }
                },
                size: 'md',
                backdrop: 'static',
                keyboard: false,
            })


            return $q.when(modalInstance);
        }

        function submitThisRequestAfterAllFilesAreUploaded(form, role, a_No) {

            // check for curly quotes and replace with regular
			if (form && form.comments) {
				var comments = form.comments.replace(/[‘’]/g, "'").replace(/[“”]/g, '"');
				form.comments = comments;
			}

            var params = form;

            return $http({

                url: '/epds/add-attachments/' + form.requestType,
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded'
                },
                data: $httpParamSerializerJQLike(_.omitBy(params, _.isNil)),
            })
            .then(
                function (data) {

                    if ('undefined' === typeof data.data.inputErrors
                        || null === data.data.inputErrors) {
                        _checkForExceptionalCases(form, role, a_No);
                    }

                    return data.data;
                },
                function (error) {

                    return error;
                });

        }


        function _checkForExceptionalCases(form, role, a_No) {

            if (form.docId == "161" || form.docId == "160") {

                fileInfoViewSvc.respondToCaseAccessRequest(
                    form.denialDocInfo.fileId, form.denialDocInfo.approve,
                    form.denialDocInfo.accessType);

                var customAttr = {
                    headerText: "Success",
                    bodyText: "",
                    modalType: "success",
                    actionType: "",
                    cancelBtnReq: "N",
                    cancelBtnActionType: ""
                }

                if (form.denialDocInfo.accessType == "intervene") {
                    customAttr.bodyText = "You have successfully denied intervenor's access to this case. "
                } else if (form.denialDocInfo.accessType == "agency-rep-access") {
                    customAttr.bodyText = "You have successfully denied agency representative access to this case. "
                }

                actionMessageSvc.showModal(customAttr).then(function () {
                    _redirectUser(form, role, a_No)
                });


            } else if (form.requestType == "agency-rep-request") {
                $location.path("/dashboard");
            } else {
                _redirectUser(form, role, a_No)
            }
        }

        function _redirectUser(form, role, a_No) {
            if (role != "GAO ADMIN") {
                $location.path('/case-docketsheet/' + base64.urlencode(a_No));
            } else if (role == "GAO ADMIN") {
                $location.path('/admin-case-docketsheet/' + base64.urlencode(a_No));
            }
        }


        function displayInvalidFormMessages(isDocConfidential, attachAssociatedDocs, isThisDocBelongToTemplate, numberOfPrimaryDocumentsAdded, numberOfAssociatedDocumentsAdded) {
            var obj = {};

            obj.isFormValid = false;
            obj.isPrimaryDocumentAttached = false;
            obj.isAssocatedDocumentsAttached = false;
            obj.isDocConfidential = isDocConfidential;
            obj.attachAssociatedDocs = attachAssociatedDocs;


            if (numberOfPrimaryDocumentsAdded != 1 && !isThisDocBelongToTemplate) {
                var customModalOptions = {
                    headerText: 'Error',
                    bodyText: 'This type of filing requires that you upload a document.  Please upload a document to continue.  Information regarding appropriate file types is included in the EPDS user guides.',
                    closeButtonText: 'OK',
                    messageType: "error"
                };

                modalService.showModal({}, customModalOptions);

            } else if (obj.isDocConfidential == null) {

                var customModalOptions = {
                    headerText: 'Error',
                    bodyText: 'Please indicate whether the document(s) you are submitting include confidential, proprietary, or information otherwise not subject to public release.',
                    closeButtonText: 'OK',
                    messageType: "error"
                };

                modalService.showModal({}, customModalOptions);

            } else if ((obj.attachAssociatedDocs == null) && (!isThisDocBelongToTemplate)) {

                var customModalOptions = {
                    headerText: 'Error',
                    bodyText: 'Please indicate whether you want to attach associated documents.',
                    closeButtonText: 'OK',
                    messageType: "error"
                };

                modalService.showModal({}, customModalOptions);

            } else if ((obj.attachAssociatedDocs == 'Y'
                && (typeof numberOfAssociatedDocumentsAdded == 'undefined'
                    || numberOfAssociatedDocumentsAdded <= 0)) && !isThisDocBelongToTemplate) {

                var customModalOptions = {
                    headerText: 'Error',
                    bodyText: 'Please attach associated document(s) or select NO.',
                    closeButtonText: 'OK',
                    messageType: "error"
                };

                modalService.showModal({}, customModalOptions);

            } else if (isThisDocBelongToTemplate
                && (typeof numberOfAssociatedDocumentsAdded == 'undefined'
                    || typeof numberOfPrimaryDocumentsAdded == 'undefined')) {
                obj.isPrimaryDocumentAttached = false;
                obj.isAssocatedDocumentsAttached = false;
                obj.isFormValid = true;
            } else {
                obj.isPrimaryDocumentAttached = true;
                obj.isAssocatedDocumentsAttached = true;
                obj.isFormValid = true;
            }

            return $q.when(obj);
        }

        function _getSubmitNewDocData(data) {
            var vm = {};

            vm.generateDocumentBtn = false;
            vm.form = {
                isDocConfidential: null,
                comments: null
            }
            vm.user_Info = data.userProfileInfo;
            vm.role = data.protestInfo.role.trim();
            vm.protestInfo = data.protestInfo;
            vm.caseStatus = data.protestInfo.case_Status.trim();
            vm.attorneyInfo = data.attorneyInfo;

            vm.doc_InfoList = _filterDocInfoListBasedOnCaseType(data.protestInfo.case_Type,
                data.doc_InfoList);

            /*if (vm.role != "GAO ADMIN") {
                vm.selectedOption = {
                        doc_Type_Id : 0,
                        doc_Type_Desc : "Please Select Type of Document",
                        role : "Default"
                    }
                 vm.doc_InfoList.push(vm.selectedOption)
            } else {

            }*/
            vm.fullDocInfoList = _filterDocInfoListBasedOnCaseType(data.protestInfo.case_Type, data.doc_InfoList);

            if (data.attorneyInfo === null) {
                vm.attorneyInfo = {
                    first_Name: "pending",
                    email: "pending",
                    phone_No: "pending",
                    street: "pending",
                }
            }

            if (data.protestInfo.b_No === null) {
                vm.protestInfo.b_No = "pending";
            }

            return vm;

        }


        function _filterDocInfoListBasedOnCaseType(caseType, docInfoList) {

            var filteredDocInfoList = []


            if ((caseType.toUpperCase().indexOf("PRO") > -1)
                || caseType.toUpperCase() === "PROTEST") {

                filteredDocInfoList = $filter('filter')(docInfoList, {
                    case_Type: "PROTEST",
                });
                return filteredDocInfoList;
            } else if ((caseType.toUpperCase().indexOf("RECON") > -1)
                || caseType.toUpperCase() === "RECONSIDERATION") {
                filteredDocInfoList = $filter('filter')(docInfoList, {
                    case_Type: "RECONSIDERATION",
                });

                return filteredDocInfoList;

            } else if ((caseType.toUpperCase().indexOf("ENT") > -1)
                || caseType.toUpperCase() === "ENTITLEMENT") {
                filteredDocInfoList = $filter('filter')(docInfoList, {
                    case_Type: "ENTITLEMENT",
                });

                return filteredDocInfoList;

            } else if ((caseType.toUpperCase().indexOf("COST") > -1)
                || caseType.toUpperCase() === "COST-CLAIM") {
                filteredDocInfoList = $filter('filter')(docInfoList, {
                    case_Type: "COSTS",
                });

                return filteredDocInfoList;
            }
        }

        function displayGenerateTemplateDocButton(role, selectedOption, originalSelectedOptionDocId, docInfoList) {

            var role = (role.indexOf("GAO") > -1);
            var obj = {};

            if (selectedOption.doc_Type_Id == 0 && role) {

                selectedOption = $filter('filter')(docInfoList,
                    {doc_Type_Id: originalSelectedOptionDocId}, function (obj, test) {
                        return obj === test;
                    });

                obj.showGenerateDocTempButton = _isTheDocTypeRequiresTemplate(originalSelectedOptionDocId)

            } else if (role) {
                obj.showGenerateDocTempButton = _isTheDocTypeRequiresTemplate(selectedOption.doc_Type_Id)
            }


            return $q.when(obj)
        }


        function generateTemplateBasedOnDocumentType(typeOfDoc, aNum) {

            var params = {
                docId: typeOfDoc.doc_Type_Id
            }


            if (aNum) {
                params.aNum = aNum
            }

            return $http({

                url: '/epds/templates',
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded'
                },
                data: $httpParamSerializerJQLike(_.omitBy(params, _.isNil)),
            })
            .then(
                function (data) {

                    return data.data;
                },
                function (error) {

                    return error;
                });

        }

        function generateTemplateModal(typeOfDoc, modalInstanceCtrl, htmlContent) {

            Idle.watch();
            var data;
            var htmlContent = htmlContent;
            var items = {};

            items.typeOfDoc = typeOfDoc;
            items.htmlContent = htmlContent;
            items.title = typeOfDoc.doc_Type_Desc;

            $rootScope.setData = function (data) {
                data = $rootScope.data;

            };
            var modalInstance;

            if (modalInstanceCtrl == "editTemplateModalInstanceCtrl") {

                modalInstance = $uibModal.open({
                    templateUrl: 'scripts/app/submit-new-docs/templateModal.html?bust=' + Math.random().toString(36).slice(2),
                    controller: editTemplateModalInstanceCtrl,
                    size: 'lg',
                    backdrop: 'static',
                    resolve: {
                        items: function () {
                            return items;
                        },
                    }
                });
            } else if (modalInstanceCtrl == "templateModalInstanceCtrl") {
                modalInstance = $uibModal.open({
                    templateUrl: 'scripts/app/submit-new-docs/templateModal.html?bust=' + Math.random().toString(36).slice(2),
                    controller: templateModalInstanceCtrl,
                    size: 'lg',
                    backdrop: 'static',
                    resolve: {
                        items: function () {
                            return items;
                        },
                    }
                });
            }


            return modalInstance.result;


        }


        function createPdf(content, typeOfDoc) {
            // Non-breaking hyphens don't appear correctly in the PDF, replacing with hyphens
            content = content && content.replace(/\u2011/g, "-");
            var params = {
                content: content,
                docId: typeOfDoc.doc_Type_Id
            }

            return $http({

                url: '/epds/create-pdf',
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded'
                },
                data: $httpParamSerializerJQLike(_.omitBy(params, _.isNil)),
            })
            .then(
                function (data) {

                    return data.data;
                },
                function (error) {

                    return error;
                });

        }


        function downloadTempPdf(typeOfDoc, fileName) {

            var params = {
                docId: typeOfDoc.doc_Type_Id
            }
            return $http({

                url: '/epds/download-temp-pdf',
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                responseType: 'arraybuffer',
                params: params
            }).then(function (response) {
                var data = response.data;
                /*console.log(data)
                var file = new Blob([data], { type: 'application/pdf' });
                saveAs(file, fileName + '.pdf');*/

                try {
                    var isFileSaverSupported = !!new Blob;
                    var file = new Blob([data], {type: 'application/pdf'});
                    saveAs(file, fileName + '.pdf');
                } catch (e) {
                    console.log("Filesaver is not supported and we cannot download this files")
                }
            });

        }


        function replaceDoc(fileInfo) {
            fileInfoViewSvc.updateCaseDocketDocumentView(fileInfo.file_Id, "replaceDoc", "")
        }

        function _isTheDocTypeRequiresTemplate(docId) {

            var listOfDocIds = [103, 104, 135, 106, 116, 126, 107, 127, 136, 117, 112, 122, 131, 140, 105, 115, 125, 134, 124, 114,
                133, 137, 128, 119, 109, 110, 120, 138, 129, 235, 236, 237, 238, 191, 193, 195, 197, 217, 218, 219, 220, 160, 161]

            var isThisDocTypeRequiresTemplate = $filter('contains')(listOfDocIds, docId);

            return isThisDocTypeRequiresTemplate;


        }

    }
})();
