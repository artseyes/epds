'use strict';

angular.module('epdsApp.caseDocketSheet')
.controller(
    'adminCaseDocketSheetController', AdminCaseDocketCtrl);


AdminCaseDocketCtrl.$inject = ['$scope', '$location', '$http', '$filter',
    'DTColumnDefBuilder',
    '$resource', '$timeout', '$rootScope', 'DTOptionsBuilder',
    '$route', '$routeParams', '$uibModal', 'modalService', 'localStorageService', 'dashboardDataService',
    'userInfoService', 'caseDocketDataSvc', 'CaseDocketData', 'moment', 'regEx', 'toolTip', 'submitNewDocDataSvc', 'base64']


function AdminCaseDocketCtrl($scope, $location, $http, $filter, DTColumnDefBuilder,
                             $resource, $timeout, $rootScope, DTOptionsBuilder,
                             $route, $routeParams, $uibModal, modalService, localStorageService, dashboardDataService, userInfoService, caseDocketDataSvc, CaseDocketData, moment, regEx, toolTip, submitNewDocDataSvc, base64) {


    var vm = this;

    vm.response = CaseDocketData;

    $scope.downloadOfflineCds = function () {
        caseDocketDataSvc.downloadOfflineCds(CaseDocketData.a_No);
    }

    $scope.dateOptions = {
        'dropdownSelector': '#dropdown1',
        startView: 'day',
        minView: 'minute',
        minuteStep: 1,
        screenReader: {'previous': 'go previous', 'next': 'go next'}
    }

    $scope.opened = {};

    $scope.open = function ($event, elementOpened) {
        $event.preventDefault();
        $event.stopPropagation();

        $scope.opened[elementOpened] = !$scope.opened[elementOpened];
    };

    if (CaseDocketData.protestInfo.roleId == "7") {
        $timeout(function () {
            $('#focus_start').focus();
        }, 0);

        vm.dtColumnDefs = [
            DTColumnDefBuilder.newColumnDef(0).withOption('sWidth', '5%'),
            DTColumnDefBuilder.newColumnDef(1).notSortable().withOption('sWidth', '5%'),
            DTColumnDefBuilder.newColumnDef(2).notSortable().withOption('sWidth', '20%'),
            DTColumnDefBuilder.newColumnDef(3).withOption('sWidth', '5%'),
            DTColumnDefBuilder.newColumnDef(4).withOption('sWidth', '10%'),
            DTColumnDefBuilder.newColumnDef(5).withOption('sWidth', '10%'),
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
                    $(".dataTables_paginate")
                    .hide();
                }
            });
        if (vm.fileInfoList != null) {
            $timeout(function () {
                $('#caseInfoTable').animateCss('fadeInLeftBig');
                $('#gaoInfoTable').animateCss('fadeInRightBig');
                $('#protestTable').animateCss('bounceInRight');
            }, 1000);

        }
    } else if (CaseDocketData.protestInfo.roleId != "7") {
        $location.path("/case-docketsheet/" + base64.urlencode(CaseDocketData.a_No));
    }

    $scope.companyStatus =
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
    $scope.role = CaseDocketData.role.trim();


    $scope.daysRemaining = CaseDocketData.daysRemaining;
    $scope.intervenorCompanyNameList = CaseDocketData.intervenorCompanyNameList;
    $scope.supplementalProtest_Info_B_No_List = CaseDocketData.supplementalProtest_Info_B_No_List;
    $scope.assignedAttorneyUserId = CaseDocketData.attorneyInfo.user_Id;
    $scope.hideCaseCompleteButton = CaseDocketData.hideCaseCompleteButton;
    $scope.docInfoList = CaseDocketData.docInfoList;
    $scope.docInfoListFallback = angular.copy($scope.docInfoList);
    $scope.assignedAttorneyUserId = CaseDocketData.attorneyInfo.user_Id;

    caseDocketDataSvc.getListOfAttorneys().then(function (data) {

        $scope.attorneyInfoList = data.gao_User_Info_List
        vm.response.attorneyInfoList = data.gao_User_Info_List;

        if (typeof $scope.attorneyInfo === 'undefined'
            || $scope.attorneyInfo.first_Name === "pending") {
            $scope.assignedAttorneyUserId = $scope.attorneyInfoList[0].user_id;
        }

    })


    $scope.redirectToConsolidatedCaseDocketSheet = function (a_No) {
        var path = "/admin-case-docketsheet"
        caseDocketDataSvc.redirectToConsolidatedCaseDocketSheet(path, a_No);
    }
    caseDocketDataSvc.getListOfDataForEditing(vm).then(function (data) {
        $scope.caseStatuses = data.caseStatuses;
        $scope.protectiveOrder = data.protectiveOrder;
        $scope.caseTypes = data.caseTypes;
        $scope.companyStatuses = data.companyStatuses;
    })
    $scope.splitString = function (String) {
        return String.split("_")[0];
    }

    $scope.redirectToCaseDocketFileInfo = function (protestId, submissionDate, docTypeId, fileAlert, docketNum) {
        caseDocketDataSvc.redirectToCaseDocketFileInfo(protestId, submissionDate, docTypeId, fileAlert, docketNum);
    }

    $scope.updateDM = function (oldDM, newDM) {
        caseDocketDataSvc.updateDMInfo($scope.protestInfo, newDM);
    }

    /* Update Protest Info */
    $scope.updateProtestInfo = function (oldValue, typeOfchange,
                                         newValue) {
        caseDocketDataSvc.updateProtestInfo(oldValue, typeOfchange,
            newValue, vm.response)

        $scope.attorneyInfo = vm.response.attorneyInfo;
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

    $scope.splitString = function (String) {
        return String.split("_")[0];
    }


    $scope.changeCaseStatusToComplete = function () {
        caseDocketDataSvc.changeCaseStatusToComplete(vm.response);
    }


    /* Update Case Docket Sheet Info */

    $scope.updateCaseDocketSheetInfo = function (fileId,
                                                 typeOfchange, newValue, submitterRole) {


        if (typeOfchange === "type-of-document") {

            $scope.selectedDocOption = $filter('filter')($scope.docInfoList, function (value) {
                return value.doc_Type_Id === newValue;
            });


            if ($scope.selectedDocOption && $scope.selectedDocOption[0].doc_Type_Desc.indexOf("_") > -1) {
                submitNewDocDataSvc.editDocumentDescriptions($scope.selectedDocOption).then(function (modalInstance) {

                    modalInstance.result.then(function (result) {

                        if (result) {

                            newValue = newValue + "&&&&" + result;
                            caseDocketDataSvc.updateCaseDocketSheetInfo(fileId,
                                typeOfchange, newValue, submitterRole, vm.response);

                        }


                    });
                })
            } else {
                caseDocketDataSvc.updateCaseDocketSheetInfo(fileId,
                    typeOfchange, newValue, submitterRole, vm.response);
            }


        } else {
            caseDocketDataSvc.updateCaseDocketSheetInfo(fileId,
                typeOfchange, newValue, submitterRole, vm.response);
        }


    }

    $scope.filterByCaseTypes = function () {
        var filteredDocInfoList = $filter('where')(vm.docInfoList, {
            doc_Type_Id: $scope.protestInfo.caseType
        }, {
            doc_Type_Id: "ALL"
        })

    }


    $scope.endDateBeforeRender = endDateBeforeRender;

    function endDateBeforeRender($view, $dates, dateRangeStart) {
        var dateRangeStart = new Date();

        if (dateRangeStart) {
            var activeDate = moment(dateRangeStart).add(1, 'minute');

            $dates.filter(function (date) {
                return date.localDateValue() >= activeDate.valueOf()
            }).forEach(function (date) {
                date.selectable = false;
            })
        }
    }

    $scope.editAgencyName = function () {
        caseDocketDataSvc.editAgencyName(vm.response);
    }

    $scope.addMinuteEntry = function () {
        caseDocketDataSvc.addMinuteEntryModal(vm.response);
    }


}


