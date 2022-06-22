angular.module('epdsApp.caseDocketSheet')
.controller(
    'caseDocketSheetController', CaseDocketCtrl);


CaseDocketCtrl.$inject = ['$scope', '$location', '$http', '$filter', 'DTColumnDefBuilder',
    '$resource', '$timeout', '$rootScope', 'DTOptionsBuilder',
    '$route', '$routeParams', '$uibModal', 'modalService',
    'caseDocketDataSvc', 'userInfoService', 'Idle', 'CaseDocketData', 'base64']


function CaseDocketCtrl($scope, $location, $http, $filter, DTColumnDefBuilder,
                        $resource, $timeout, $rootScope, DTOptionsBuilder,
                        $route, $routeParams, $uibModal, modalService, caseDocketDataSvc, userInfoService, Idle, CaseDocketData, base64) {

    var vm = this;

    if (!CaseDocketData || CaseDocketData === "" || CaseDocketData.status === 401)
        return;

    vm.response = CaseDocketData;

    $scope.downloadOfflineCds = function () {
        caseDocketDataSvc.downloadOfflineCds(CaseDocketData.a_No);
    }

    if (CaseDocketData.protestInfo.roleId != "7") {
        $timeout(function () {
            $('#focus_start').focus();
        }, 0);

        vm.dtColumnDefs = [
            DTColumnDefBuilder.newColumnDef(0).withOption('sWidth', '5%'),
            DTColumnDefBuilder.newColumnDef(1).notSortable().withOption('sWidth', '5%'),
            DTColumnDefBuilder.newColumnDef(2).notSortable().withOption('sWidth', '20%'),
            DTColumnDefBuilder.newColumnDef(3).withOption('sWidth', '5%'),
            DTColumnDefBuilder.newColumnDef(4).withOption('sWidth', '10%'),
            DTColumnDefBuilder.newColumnDef(5).notSortable().withOption('sWidth', '10%'),
            DTColumnDefBuilder.newColumnDef(6).notSortable().withOption('sWidth', '25%'),
            DTColumnDefBuilder.newColumnDef(7).notSortable().withOption('sWidth', '25%')];

        vm.fileInfoList = CaseDocketData.fileInfoList;

        vm.dtOptions = DTOptionsBuilder
        .newOptions()
        // Datatables DOM documentation: https://datatables.net/reference/option/dom
        // AngularJS datatables withDOM documentation: https://surgbook.net/node_modules/angular-datatables/#!/overrideBootstrapOptions
        .withDOM("<'row'lfr>tip")
        .withLanguage(
            {
                "sSearch": "Filter Records : "
            })
        .withOption('lengthMenu', [50, 100, 150, 200])
        .withDisplayLength(100)
        .withOption('stateSave', true)
        .withOption(
            'initComplete',
            function (settings) {
                if ((CaseDocketData.fileInfoList && CaseDocketData.fileInfoList.length) <= 100) {
                    $(".dataTables_paginate").hide();
                }
            });

        if (vm.fileInfoList != null) {
            $timeout(function () {
                $('#caseInfoTable').animateCss('fadeInLeftBig');
                $('#gaoInfoTable').animateCss('fadeInRightBig');
                $('#fileInfoTable').animateCss('fadeOutUpBig');
            }, 500);


        }
    } else if (CaseDocketData.protestInfo.roleId == "7") {
        $location.path("/admin-case-docketsheet/" + base64.urlencode(CaseDocketData.a_No));
    }

    $scope.roleId = CaseDocketData.protestInfo.roleId;
    $scope.isViewOnly = CaseDocketData.isViewOnly;
    $scope.user_Info = CaseDocketData.user_Info;
    $scope.protestInfo = CaseDocketData.protestInfo;
    $scope.consolidatedProtests = CaseDocketData.consolidatedProtests;
    $scope.parent_B_No = CaseDocketData.parent_B_No;
    $scope.po = CaseDocketData.po;
    $scope.caseStatus = CaseDocketData.caseStatus;
    $scope.attorneyInfo = CaseDocketData.attorneyInfo;
    $scope.fileInfoList = CaseDocketData.fileInfoList;
    $scope.a_No = CaseDocketData.a_No
    $scope.role = CaseDocketData.role && CaseDocketData.role.trim();

    if (($scope.roleId == "3"
        || $scope.roleId == "7"
        || $scope.roleId == "8") && !$scope.isViewOnly) {
        $scope.showAddMinuteEntryButton = "Y"
    }
    $scope.daysRemaining = CaseDocketData.daysRemaining;
    $scope.intervenorCompanyNameList = CaseDocketData.intervenorCompanyNameList;
    $scope.supplementalProtest_Info_B_No_List = CaseDocketData.supplementalProtest_Info_B_No_List;
    $scope.assignedAttorneyUserId = CaseDocketData.attorneyInfo && CaseDocketData.attorneyInfo.user_Id;
    $scope.hideCaseCompleteButton = CaseDocketData.hideCaseCompleteButton;


    $scope.updateDM = function (oldDM, newDM) {
        caseDocketDataSvc.updateDMInfo($scope.protestInf, newDM);
    }

    $scope.redirectToConsolidatedCaseDocketSheet = function (a_No) {
        var path = "/case-docketsheet";
        caseDocketDataSvc.redirectToConsolidatedCaseDocketSheet(path, a_No);
    }

    caseDocketDataSvc.getListOfDataForEditing(vm).then(function (data) {
        $scope.caseStatuses = data.caseStatuses;
        $scope.protectiveOrder = data.protectiveOrder;
        $scope.caseTypes = data.protectiveOrder;
    })
    $scope.splitString = function (String) {
        return String.split("_")[0];
    }

    $scope.redirectToCaseDocketFileInfo = function (protestId, submissionDate, docTypeId, fileAlert, docketNum) {
        caseDocketDataSvc.redirectToCaseDocketFileInfo(protestId, submissionDate, docTypeId, fileAlert, docketNum);
    }

    /* Update Protest Info */
    $scope.updateProtestInfo = function (oldValue, typeOfchange,
                                         newValue) {
        caseDocketDataSvc.updateProtestInfo(oldValue, typeOfchange,
            newValue, vm.response)
    }

    $scope.addComments = function (a_No, doc_Type_Id, file_id,
                                   comments) {

        caseDocketDataSvc.addCommentsModal(a_No, doc_Type_Id, file_id,
            comments, vm);
    }

    $scope.setCaseDocketEmailPreferences = function () {
        caseDocketDataSvc.setCaseDocketEmailPreferences(vm);
    }

    $scope.addAttorneyNotes = function (a_No, doc_Type_Id,
                                        file_id, comments) {
        caseDocketDataSvc.addAttorneyNotesModal(a_No, doc_Type_Id,
            file_id, comments, vm);

    }


    $scope.addMinuteEntry = function () {
        caseDocketDataSvc.addMinuteEntryModal(vm.response);
    }

};


editAgencyNameModalInstanceCtrl.$inject = ['$scope', '$rootScope', '$http',
    '$uibModalInstance', 'oldValue', 'modalService', 'caseDocketDataSvc', 'agencyDropDownService', 'protestInfo'];

function editAgencyNameModalInstanceCtrl($scope, $rootScope, $http,
                                         $uibModalInstance, oldValue, modalService, caseDocketDataSvc, agencyDropDownService, protestInfo) {

    $scope.updateAgencyInfo = function (tier_1_AgencySelectedOption,
                                        tier_2_AgencySelectedOption) {

        if (!agencyDropDownService.validateAgencyInfo(tier_1_AgencySelectedOption, tier_2_AgencySelectedOption)) {
            return;
        }


        caseDocketDataSvc.updateAgencyInfo(oldValue, tier_1_AgencySelectedOption, tier_2_AgencySelectedOption, protestInfo.a_No).then(function () {

            $uibModalInstance.dismiss('cancel');
            var customModalOptions = {
                headerText: 'success',
                bodyText: 'You have successfully updated the Agency Name. ',
                closeButtonText: 'OK',
                messageType: "success"
            };

            modalService.showModal({}, customModalOptions)
            .then(function (result) {
            });

        })


    }

    $scope.cancel = function () {
        $uibModalInstance.dismiss('cancel');
    };
}

addMinuteEntryModalInstanceCtrl.$inject = ['$scope', '$rootScope', '$http',
    '$uibModalInstance', 'protestInfo', 'modalService', 'actionMessageSvc', '$route', 'caseDocketDataSvc', 'regEx', 'toolTip']

function addMinuteEntryModalInstanceCtrl($scope, $rootScope, $http,
                                         $uibModalInstance, protestInfo, modalService, actionMessageSvc, $route, caseDocketDataSvc, regEx, toolTip) {

    $scope.headerText = "Add Minute Entry";
    $scope.type = "minuteEntry";
    $scope.comments = "";

    $scope.regEx = regEx;
    $scope.toolTip = toolTip;


    $scope.addMinuteEntry = function (comments) {


        if (comments === null
            || typeof comments === 'undefined' || comments === "") {
            isCommentsInValidFormat(actionMessageSvc);
            return;
        } else if (comments !== null
            && typeof comments !== 'undefined' || comments !== "") {
            // check for curly quotes and replace with regular
            comments = comments.replace(/[‘’]/g, "'").replace(/[“”]/g, '"');

            caseDocketDataSvc.addMinuteEntryFunc(comments, protestInfo).then(function () {
                $route.reload();
                $uibModalInstance.dismiss('cancel');

            })

        }


    }
    $scope.cancel = function () {
        $uibModalInstance.dismiss('cancel');
    };
}

setEmailPreferencesModalInstanceCtrl.$inject = ['$scope', '$rootScope', '$http',
    '$uibModalInstance', 'modalService', 'protestInfo', 'actionMessageSvc', '$route', 'caseDocketDataSvc']


function setEmailPreferencesModalInstanceCtrl($scope, $rootScope, $http,
                                              $uibModalInstance, modalService, protestInfo, actionMessageSvc, $route, caseDocketDataSvc) {

    $scope.emailPreference = protestInfo.casedocket_email_preferences;


    if ($scope.emailPreference === "N") {
        $scope.text = "Email notifications are currently inactive for this case. Do you want to recieve email notifications for this case?"
    } else {
        $scope.text = "Email notifications are currently active  for this case.  Do you want to stop receiving email notifications for this case?"
    }


    $scope.updateEmailPreferences = function (val) {


        if (val == "Y") {
            val = "N";
        } else if (val == "N") {
            val = "Y";
        }

        if (protestInfo.casedocket_email_preferences == "Y") {

            var bodyText = "Warning - you are turning off email notification for this case. It is your responsibility to review the docket for any new "
                + "filings and you bear the risk for failure to learn of any new filings.";

            var customAttr = {
                headerText: "Warning",
                bodyText: bodyText,
                modalType: "warning",
                actionType: "samepage",
                cancelBtnReq: "Y",
                cancelBtnActionType: "samepage",
                okAndCancelText: "Y",
                okBtnText: "OK",
                cancelBtnText: "Cancel"
            }

            actionMessageSvc.showModal(customAttr)
            .then(function (result) {

                if (result.cancelBtnClicked != "Y") {
                    $scope.submitResponse(val);
                }
            })

        } else {
            $scope.submitResponse(val);
        }

    }


    $scope.submitResponse = function (val) {

        caseDocketDataSvc
        .updateEmailPreferences(val, protestInfo.a_No)
        .then(
            function () {
                $uibModalInstance.dismiss('cancel');
                var customAttr = {
                    headerText: "Success",
                    bodyText: "You have successfully updated email preferences for this case.",
                    modalType: "success",
                    actionType: "",
                    cancelBtnReq: "N",
                    cancelBtnActionType: ""
                }

                actionMessageSvc.showModal(customAttr).then(
                    function (result) {

                        $route.reload();
                    })

            })

    }

    $scope.cancel = function () {
        $uibModalInstance.dismiss('cancel');
    };
}

addAttorneyNotesModalInstanceCtrl.$inject = ['$scope', '$rootScope', '$http',
    '$uibModalInstance', 'fileId', 'comments', 'modalService', 'caseDocketDataSvc', '$route', 'actionMessageSvc', 'regEx', 'toolTip']


function addAttorneyNotesModalInstanceCtrl($scope, $rootScope, $http,
                                           $uibModalInstance, fileId, comments, modalService, caseDocketDataSvc, $route, actionMessageSvc, regEx, toolTip) {

    $scope.headerText = "Add GAO Notes";
    $scope.type = "notes";
    $scope.comments = comments;
    $scope.regEx = regEx;
    $scope.toolTip = toolTip;

    if (null !== comments && comments.length > 0) {

        $scope.headerText = "Edit GAO Notes";
    }

    $scope.addNotes = function (comments) {


        if (comments === null
            || typeof comments === 'undefined' || comments === "") {
            isCommentsInValidFormat(actionMessageSvc);
            return;
        } else if (comments !== null
            && typeof comments !== 'undefined' || comments !== "") {
            // check for curly quotes and replace with regular
            comments = comments.replace(/[‘’]/g, "'").replace(/[“”]/g, '"');

            caseDocketDataSvc.addNotes(fileId, comments).then(function () {
                $uibModalInstance.dismiss('cancel');
                $route.reload();
            })

        }

    }

    $scope.cancel = function () {
        $uibModalInstance.dismiss('cancel');
    };
}


addCommentsModalInstanceCtrl.$inject = ['$scope', '$rootScope', '$http',
    '$uibModalInstance', 'fileId', 'comments', 'modalService', 'caseDocketDataSvc', '$route', 'actionMessageSvc', 'regEx', 'toolTip']

function addCommentsModalInstanceCtrl($scope, $rootScope, $http,
                                      $uibModalInstance, fileId, comments, modalService, caseDocketDataSvc, $route, actionMessageSvc, regEx, toolTip) {

    $scope.headerText = "Add Comments";
    $scope.type = "notes";
    $scope.comments = comments;

    $scope.regEx = regEx;
    $scope.toolTip = toolTip;

    if (null !== comments && comments.length > 0) {

        $scope.headerText = "Edit Comments";
    }


    $scope.addNotes = function (comments) {

        if (comments === null
            || typeof comments === 'undefined' || comments === "") {
            isCommentsInValidFormat(actionMessageSvc);
            return;
        } else if (comments !== null
            && typeof comments !== 'undefined' || comments !== "") {
            // check for curly quotes and replace with regular
            comments = comments.replace(/[‘’]/g, "'").replace(/[“”]/g, '"');

            $uibModalInstance.dismiss('cancel');

            caseDocketDataSvc.addCommentsFunc(fileId, comments).then(function (response) {

                if (response && !response.status) {

                    $uibModalInstance.dismiss('cancel');
                    var customModalOptions = {
                        headerText: 'success',
                        bodyText: 'You have successfully updated comments.',
                        closeButtonText: 'OK',
                        messageType: "success"
                    };

                    modalService.showModal({}, customModalOptions);
                }


            })
        }


    }

    $scope.cancel = function () {
        $uibModalInstance.dismiss('cancel');
    };
}

enterOrVerifyDmCtrl.$inject = ['$scope', '$rootScope', '$http',
    '$uibModalInstance', 'modalService', 'caseDocketDataSvc', '$route', 'protestInfo', 'ngFabForm', 'actionMessageSvc']

function enterOrVerifyDmCtrl($scope, $rootScope, $http,
                             $uibModalInstance, modalService, caseDocketDataSvc, $route, protestInfo, ngFabForm, actionMessageSvc) {


    $scope.customFormOptions = {
        validationsTemplate: 'views/templates/dm-validations.html',
        preventInvalidSubmit: true,
        preventDoubleSubmit: true,
        setFormDirtyOnSubmit: true,
        scrollToAndFocusFirstErrorOnSubmit: true,
        scrollAnimationTime: 900,
        scrollOffset: -100,
    };

    $scope.headerText = protestInfo.headerText;

    $scope.ok = function (dmNumber) {


        caseDocketDataSvc.validateDMInfo(dmNumber, protestInfo.a_No).then(function (data) {
            if ($scope.headerText === "Enter DM #") {

                if (data.isExists === false) {
                    caseDocketDataSvc.updateDMInfo(protestInfo, dmNumber).then(function () {
                        $route.reload();
                        $uibModalInstance.dismiss('cancel');
                    });
                } else if (data.isExists === true) {
                    var bodyText = "The DM# you have entered already exists.  Please check the DM# and try again."
                    var customAttr = {
                        headerText: "Error",
                        bodyText: bodyText,
                        modalType: "error",
                        actionType: "samepage",
                        cancelBtnReq: "N",
                    }

                    actionMessageSvc.showModal(customAttr);
                } else {
                    var bodyText = "DM# is not entered in the correct format.  DM#s should be entered as 7 digit numbers only. "
                    var customAttr = {
                        headerText: "Error",
                        bodyText: bodyText,
                        modalType: "error",
                        actionType: "samepage",
                        cancelBtnReq: "N",
                    }

                    actionMessageSvc.showModal(customAttr);
                }
            } else if ($scope.headerText == "Verify DM #") {

                if (data.isExists === true) {
                    caseDocketDataSvc.verifyDMInfo(protestInfo, dmNumber).then(function (data) {
                        if (data.isEqual === true) {
                            $route.reload();
                            $uibModalInstance.dismiss('cancel');
                        } else {
                            var bodyText = "The DM# you have entered does not match.<br>Prior Code: " + data.dbDMNumber + ", Verification Code: " + dmNumber + "<br>  Please check the DM# and try again."
                            var customAttr = {
                                headerText: "Error",
                                bodyText: bodyText,
                                modalType: "error",
                                actionType: "samepage",
                                cancelBtnReq: "N",
                            }

                            actionMessageSvc.showModal(customAttr);
                        }
                    });
                } else {
                    var bodyText = "The DM# you have entered does not exist.  Please check the DM# and try again."
                    var customAttr = {
                        headerText: "Error",
                        bodyText: bodyText,
                        modalType: "error",
                        actionType: "samepage",
                        cancelBtnReq: "N",
                    }

                    actionMessageSvc.showModal(customAttr);
                }
            }
        });
    }

    $scope.cancel = function () {
        $uibModalInstance.dismiss('cancel');
    };
}


join_UnjoinCasesModalInstanceCtrl.$inject = ['$scope', '$rootScope', '$http',
    '$uibModalInstance', 'protestInfo', 'modalService', 'interveneDataSvc', 'caseDocketDataSvc', '$route', 'ngFabForm', '$filter']

function join_UnjoinCasesModalInstanceCtrl($scope, $rootScope, $http,
                                           $uibModalInstance, protestInfo, modalService, interveneDataSvc, caseDocketDataSvc, $route, ngFabForm, $filter) {


    $scope.protestInfo = protestInfo;
    $scope.customFormOptions = {
        validationsTemplate: 'views/templates/join-unjoin-validations.html',
        preventInvalidSubmit: true,
        preventDoubleSubmit: true,
        setFormDirtyOnSubmit: true,
        scrollToAndFocusFirstErrorOnSubmit: true,
        scrollAnimationTime: 900,
        scrollOffset: -100,
    };
    var counter = 0;
    $scope.listOfbNumbers = [{
        id: counter,
        label: 'B-',
        bNumber: 'B-'
    }];

    $scope.templistOfbNumbers = [];
    $scope.newItem = function ($event) {
        counter++;
        $scope.listOfbNumbers.push({
            id: counter,
            label: 'B-',
            bNumber: 'B-'
        });

        $event.preventDefault();
    }
    $scope.inlinef = function ($event, inlinecontrol) {
        var checkbox = $event.target;
        if (checkbox.checked) {
            $('#' + inlinecontrol).css('display', 'inline');
        } else {
            $('#' + inlinecontrol).css('display', '');
        }

    }
    $scope.showitems = function ($event) {
        $('#displayitems').css('visibility', 'none');
    }


    $scope.join_UnjoinCases = function (bNumber, typeOfAction) {

        $scope.listOfbNumbers = $filter("unique")($scope.listOfbNumbers, "bNumber");
        angular
        .forEach(
            $scope.listOfbNumbers,
            function (object, index) {
                if (index === 0) {
                    $scope.listOfbNumbers = []
                    $scope.listOfbNumbers.push(object.bNumber);
                } else {
                    $scope.listOfbNumbers.push(object.bNumber);
                }

            });

        caseDocketDataSvc.join_unJoinChild_BNumber_To_Parent_BNumber($scope.listOfbNumbers, typeOfAction, protestInfo.a_No).then(function () {

            $uibModalInstance.dismiss('cancel');
            var customModalOptions = {
                headerText: 'success',
                bodyText: ' You have  successfully  ' + typeOfAction + '  cases.',
                closeButtonText: 'OK',
                messageType: "success"
            };
            modalService.showModal({}, customModalOptions).then(
                function (result) {
                });
        })
    }

    $scope.cancel = function () {
        $uibModalInstance.dismiss('cancel');
    };
}

