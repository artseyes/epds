'use strict';

angular.module(
    'epdsApp.dashboard')
.controller('ProtestCtrl', ProtestCtrl);

ProtestCtrl.$inject = ['$scope', '$http', '$uibModal', '$location', '$window',
    '$rootScope', 'ngFabForm', 'modalService', '$timeout',
    'localStorageService', 'agencyDropDownService', 'protestDataSvc', '$cookies', 'actionMessageSvc', '$ocLazyLoad', '$sce', '$injector', 'regEx', 'toolTip', 'csrfService']


function ProtestCtrl($scope, $http, $uibModal, $location, $window,
                     $rootScope, ngFabForm, modalService, $timeout, localStorageService,
                     agencyDropDownService, protestDataSvc, $cookies, actionMessageSvc, $ocLazyLoad, $sce, $injector, regEx, toolTip, csrfService) {

    $scope.regEx = regEx;
    $scope.toolTip = toolTip;
    $scope.fileUploadErrors = [];
    $scope.show_pop_up_text = false;

    var fileInfoViewSvc;

    $scope.popup = {
        options: {
            title: null,
            placement: 'right',
            delay: {show: 800, hide: 100}
        }
    };

    $timeout(function () {
        $('#protestInfoForm').show();
        $('#company_name').focus();
    }, 1000);

    var params = {
        'typeOfProtest': 'protest'
    }

    $scope.sizeStatusTooltip = "To determine the protester’s size for a procurement, the filer should locate the solicitation's " +
        "applicable North American Industrial Classification System (NAICS) code and consult the Small Business Administration's size standards in Title 13,  Part 121 of the Code of Federal Regulations.   " +
        "If a protester does not know its applicable size for the procurement at issue, the filer should select \"Large.\"  This information is collected for statistical purposes."

    $scope.docConfidentialLabel = "Do any of these documents contain information that is subject to a protective \n" +
        "                                                order entered by the judge in this case?  The filer will select Yes if the filing \n" +
        "                                                includes this type of information AND a Protective Order has been entered in the appeal."

    // $scope.htmlPopover = $sce.trustAsHtml('<p>'
    // 	+ $scope.sizeStatusTooltip
    // 	+ '</p>');
    $scope.htmlPopover = $sce.trustAsHtml($scope.sizeStatusTooltip);


    protestDataSvc.loadProtestFilingForm(params).then(function (response) {
        $scope.form = response.form;
        $scope.role = response.user_Role.trim();

        $scope.a_No = response.aNum;

        $ocLazyLoad.load(['jquery-datatables', 'angular-datatables', 'dashboard', 'account-update', 'request-to-intervene',
            'angular-xeditable', 'cds', 'file-info-view'], {serie: true, cache: false}).then(function () {

            fileInfoViewSvc = $injector.get("fileInfoViewSvc");

            fileInfoViewSvc.getMessageForTheTypeOfFilesThisUserCanAttach($scope.role.trim()).then(function (message) {
                $scope.uploadDocMessage = message;
            })
        });


        agencyDropDownService.getListOfTier1Agencies().then(
            function (data) {
                $scope.tier1SelectedOption = data.tier1SelectedOption;
                $scope.tier1AgencyList = data.tier1AgencyList;
            });


    })


    $scope.customFormOptions = {
        // validationsTemplate : 'scripts/vendor/angular/ng-fab-form/templates/default-validation-msgs.html',
        validationsTemplate: 'default-validation-msgs.html',
        preventInvalidSubmit: true,
        preventDoubleSubmit: true,
        setFormDirtyOnSubmit: true,
        scrollToAndFocusFirstErrorOnSubmit: true,
        scrollAnimationTime: 900,
        scrollOffset: -100,
    };

    $scope.options = null;
    $scope.companyAddressDetails = ''
    $scope.representativeAddressDetails = '';


    $scope.$watch(
        'form.isDocConfidential',
        function () {

            if ($scope.form && $scope.form.isDocConfidential) {
                var bodyText;

                if ($scope.form.isDocConfidential == "X") {
                    bodyText = "Unless precluded by law, CBCA will release any material not marked as protected to a requesting party outside the " +
                        "government, in accordance with CBCA’s disclosure of " +
                        "records rules at 4 C.F.R. part 81.";
                } else if ($scope.form.isDocConfidential == "Y") {
                    bodyText = "You must mark the protected information in the document as required by the Protective Order before filing. "
                //    bodyText = "Because you have marked your filing as containing information that should be withheld from public disclosure, " +
               //         "you are required to place a statement advising of that fact on the front page of the submission.  4 C.F.R. § 21.1(g)." +
                 //       "  Additionally, you must identify the information to be withheld wherever it appears, and " +
                //        "file a redacted copy of the filing which omits the information, with CBCA and the agency, within 1 day" +
                //        " after filing the filing with CBCA.  Id.  Unless precluded by law, CBCA will release any unmarked material to a" +
                 //       " requesting party outside the government, in accordance with CBCA’s disclosure of records rules at 4 C.F.R. part 81.";
                }

                if (bodyText) {
                    var customAttr = {
                        headerText: "",
                        bodyText: bodyText,
                        modalType: "info",
                        actionType: "",
                        cancelBtnReq: "N",
                        cancelBtnActionType: ""
                    }
                    actionMessageSvc.showModal(customAttr);
                }
            }
        });


    $scope
    .$watch(
        'form.email',
        function () {

            if ($scope.form && $scope.form.email) {

                protestDataSvc.getUserInfoByEmail($scope.form.email).then(function (response) {

                    if (response && response.user_Info) {
                        angular.extend($scope.form, response.userInfoObj)
                    }
                })

            }
        });


    $scope
    .$watch(
        'companyAddressDetails',
        function () {

            if (typeof $scope.companyAddressDetails.address_components != 'undefined') {

                $scope.companyAddressDetails = retrieveAddressDetailsFromUserSelection(
                    $scope,
                    $scope.companyAddressDetails);
                $scope.form.company_address1 = $scope.companyAddressDetails.streetAddr,
                    $scope.form.company_city = $scope.companyAddressDetails.city,
                    $scope.form.company_state = $scope.companyAddressDetails.state,
                    $scope.form.company_zipcode = $scope.companyAddressDetails.zipcode,
                    $scope.form.company_country = $scope.companyAddressDetails.country;
            }
        });
    $scope
    .$watch(
        'representativeAddressDetails',
        function () {

            if (typeof $scope.representativeAddressDetails.address_components != 'undefined') {

                $scope.representativeAddressDetails = retrieveAddressDetailsFromUserSelection(
                    $scope,
                    $scope.representativeAddressDetails);
                $scope.form.address1 = $scope.representativeAddressDetails.streetAddr,
                    $scope.form.zipcode = $scope.representativeAddressDetails.zipcode
                $scope.form.country = $scope.representativeAddressDetails.country,
                    $scope.form.state = $scope.representativeAddressDetails.state,
                    $scope.form.city = $scope.representativeAddressDetails.city
            }
        });

    $scope.totalNumberOfPrimaryDocumentsAdded = 0;

    $scope.cancelPrimaryDocument = function ($flow) {
        $flow.cancel();
        $scope.totalNumberOfPrimaryDocumentsAdded = 0;
    }

    $scope.totalNumberOfAssociatedDocumentsAdded = 0;

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
            console.log("Problem creating JSON", ex);
            message = {
                error: {
                    fileError: fileName + " was not uploaded. Please upload this file again."
                }
            }
        }
        $scope.fileUploadErrors.push(message.error);

        $flow.removeFile($file);

    }


    $scope.fileUploadSuccess = function ($file, $message, $flow) {

        //need to see if we need this
        //$flow.removeFile($file);
    }


    $scope.assignSingleFileUploadFlowInstaceToScope = function (
        file, event, flow) {


        if (checkIfThisUserCanUploadDocumentWithThisFileExtension(
            modalService, file, $scope.role.trim())) {
            flow.opts.query = {
                "attachmentType": "protest",
                "fileIdentifierCode": "P",
                "a_No": $scope.a_No
            };

            flow.opts.headers = {
                'X-XSRF-TOKEN': csrfService.token
            };
            $scope.protestDocumentAdded = true;
            $scope.singleFileUpload = flow

            $scope.totalNumberOfPrimaryDocumentsAdded = $scope.singleFileUpload.files.length;

        } else {
            return false;
        }

    }


    $scope.assignMultipleFileUploadFlowInstaceToScope = function (
        file, event, flow) {


        if (checkIfThisUserCanUploadDocumentWithThisFileExtension(
            modalService, file, $scope.role.trim())) {

            flow.opts.query = {
                "attachmentType": "protest",
                "fileIdentifierCode": "A",
                "a_No": $scope.a_No
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

    $scope.registerProtestAfterAllDocumentsAreUploadedToServer = function () {
        $scope.isProtestAlreadyRegistered = true;

        if (localStorageService.isSupported) {
            localStorageService.set("newProtestANum", $scope.a_No);
        } else {
            $cookies.set("newProtestANum", $scope.a_No)
        }

        $scope.protestInfoFormJson.a_No = $scope.a_No;
        protestDataSvc.registerProtest($scope.protestInfoFormJson).then(function (response) {
            // check for response.data.  It holds errors that come back. If it doesn't exist, we were successful.
            if (!response.data) {
                if ($scope.role == "GAO ADMIN") {
                    $location.path("/admin-dashboard/unassigned")
                } else {
                    $location.path("/dashboard")
                }

                // if (response.user_Role == "GAO ADMIN") {
                //     $location.path("/admin-dashboard/unassigned")
                // } else {
                //
                //     protestDataSvc.startPayDotGovOnlineTransaction($scope.a_No).then(function (response) {
                //         $scope.payDotGovFunc(response);
                //     });
                // }
            }

        })

    }

    $scope.protestDocumentIsUploadedToServer = function () {
        $scope.checkForFileUploadErrors();
    }

    $scope.associatedDocumentsIsUploadedToServer = function () {
        $scope.checkForFileUploadErrors();
    };


    $scope.checkForFileUploadErrors = function () {

        fileInfoViewSvc.checkForErrorsinFileUpload($scope.singleFileUpload, $scope.multipleFileUpload).then(function (data) {
            $scope.fileUploadEr = data.fileUploadEr;

            if (!$scope.isProtestAlreadyRegistered) {

                if ($scope.form.attachAssociatedDoc == 'N'
                    && !$scope.fileUploadEr
                    && !data.fileUploadInProgress
                    && ($scope.fileUploadErrors.length <= 0)) {
                    $scope.registerProtestAfterAllDocumentsAreUploadedToServer();

                } else if ($scope.form.attachAssociatedDoc == 'Y'
                    && !$scope.fileUploadEr
                    && !data.fileUploadInProgress
                    && ($scope.fileUploadErrors.length <= 0)) {
                    $scope.registerProtestAfterAllDocumentsAreUploadedToServer();
                }
            }
        });


    }

    $scope.payDotGovFunc = function (response) {

        var url = response && response.payDotGovPaymentUrl,
            token = response && response.payDotGovToken,
            appId = response && response.payDotGovAppId,
            params = 'token=' + token + '&tcsAppID=' + appId;
        url = url + params;

        if (localStorageService.isSupported) {
            localStorageService.set("payDotGovToken", token);
        } else {
            $cookies.set("payDotGovToken", token)
        }

        if (url && token && appId) {
            $window.location.href = url;
        } else {
            console.log("Paydotgov payment url was  not resolved")
        }


    }

    $scope.cancel = function () {
        if ($scope.role == "GAO ADMIN") {
            $location.path("/admin-dashboard/unassigned")
        } else {
            $location.path("/dashboard")
        }
    }

    $scope.hideCancelButton = false;
    $scope.registerProtestInfo = function (isFormValid,
                                           attachAssociatedDocs, form, tier1SelectedOption, tier2SelectedOption, protestInfoForm) {


        if (!agencyDropDownService.validateAgencyInfo(tier1SelectedOption, tier2SelectedOption)) {
            return;
        }

        if (protestInfoForm.comments.$invalid) {
            isCommentsInValidFormat(actionMessageSvc);
            return;
        } else if ($scope.form.comments == "") {
            $scope.form.comments = null

        }

        if (form.comments != null) {
            // check for curly quotes and replace with regular
            form.comments = form.comments.replace(/[\u2018\u2019]/g, "'").replace(/[\u201c\u201d]/g, '"');
        }

        if (!checkIfTotalUploadSizeExceedsTheMaxSize($scope, modalService)) {
            return
        }

        $scope.fileUploadErrors = [];
        $scope.isProtestAlreadyRegistered = false;

        form.tier1Id = tier1SelectedOption;
        form.tier2Id = (!tier2SelectedOption ? null : tier2SelectedOption);

        form.userRole = $scope.role.trim();


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

        var bodyText = "<p>You will automatically be directed to Pay.gov to pay the filing fee." +
            "  Your protest filing will not be complete until you have successfully made your payment." +
            "  Upon successfully making your payment, you will automatically be returned to EDS.CBCA.gov." +
            "  Once payment is made, you will not have the opportunity to edit your filing." +
            "  You will automatically be directed to Pay.gov to pay the filing fee of $350. <\/p>";

        bodyText += "<p>Do you want to proceed to Pay.gov?<\/p>";

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

        try {
            if (!$scope.singleFileUpload) {
                $scope.totalNumberOfPrimaryDocumentsAdded = 0;
            } else {
                $scope.totalNumberOfPrimaryDocumentsAdded = $scope.singleFileUpload.files.length;
            }
            if (!$scope.multipleFileUpload) {
                $scope.totalNumberOfAssociatedDocumentsAdded = 0;
            } else {
                $scope.totalNumberOfAssociatedDocumentsAdded = $scope.multipleFileUpload.files.length;
            }
        } catch (ex) {
            console.log(ex)
        }


        protestDataSvc.showInvalidFormMessages(isFormValid, attachAssociatedDocs,
            $scope.totalNumberOfPrimaryDocumentsAdded, $scope.totalNumberOfAssociatedDocumentsAdded).then(function (data) {

            if (data.isFormValid && data.isProtestDocumentAttached) {
                $scope.protestInfoFormJson = form;
                if (localStorageService.isSupported) {
                    localStorageService.set("protestInfoForm", form);
                } else {
                    $cookies.set("protestInfoForm", form)
                }

                $scope.hideCancelButton = true;

                protestDataSvc.validateProtestInfo(form).then(function (result) {

                    if (result.isSuccess) {

                        // if ($scope.role == "GAO ADMIN") {

                            if (data.attachAssociatedDocs == 'Y') {
                                $scope.singleFileUpload.upload()
                                $scope.multipleFileUpload.upload()
                            } else if (data.attachAssociatedDocs == 'N') {
                                $scope.singleFileUpload.upload()
                            }

                        // } else {
                        //
                        //     actionMessageSvc.showModal(customAttr).then(function (result) {
                        //
                        //         if (result.cancelBtnClicked == "N") {
                        //             if (data.attachAssociatedDocs == 'Y') {
                        //                 $scope.singleFileUpload.upload()
                        //                 $scope.multipleFileUpload.upload()
                        //             } else if (data.attachAssociatedDocs == 'N') {
                        //                 $scope.singleFileUpload.upload()
                        //             }
                        //         }
                        //
                        //
                        //     });
                        // }
                    } else {


                    }

                })


            }
        })

    }


    $scope.companyStatuses = [ {
        value : 'APPEAL',
        text : 'Contract Disputes Act Appeal'
    },{
        value : 'FEMA',
        text : 'FEMA Arbitration'
    }]
}

