'use strict';

angular.module('epdsApp.adminDashboard').controller('manageAttorneysController', ManageAttorneyCtrl)

ManageAttorneyCtrl.$inject = ['$scope', '$log', '$rootScope', '$location', '$http', '$route', '$routeParams',
    '$uibModal', '$templateCache', '$templateRequest', '$sce', '$filter', '$compile', '$interpolate', 'partiesDataSvc',
    'manageAttorneyInfoSvc', 'caseDocketDataSvc', 'actionMessageSvc', '$timeout', 'regEx', 'toolTip']


function ManageAttorneyCtrl($scope, $log, $rootScope, $location, $http, $route, $routeParams, $uibModal, $templateCache,
                            $templateRequest, $sce, $filter, $compile, $interpolate, partiesDataSvc, manageAttorneyInfoSvc,
                            caseDocketDataSvc, actionMessageSvc, $timeout, regEx, toolTip) {
    $scope.displayEditAttorneyForm = false;

    $scope.role = "GAO";
    $scope.form = {
        email: null,
        title: null,
        prefix: null,
        firstname: null,
        mi: null,
        lastname: null,
        suffix: null,
        phonenumber: null,
        faxnumber: null,
        groupId: null,

    }

    $scope.customFormOptions = {
        validationsTemplate: 'scripts/app/admin/manage-attorney-info/register-form-validations.html',
        preventInvalidSubmit: true,
        preventDoubleSubmit: true,
        setFormDirtyOnSubmit: true,
        scrollToAndFocusFirstErrorOnSubmit: true,
        scrollAnimationTime: 900,
        scrollOffset: -100,
    };

    manageAttorneyInfoSvc.loadManageAttorneyView().then(function (response) {
        $timeout(function() {
            $('#gao_user').focus();
        }, 0);

        $scope.gaoRoles = response.gaoRoles;
        $scope.groups = response.gaoGroups;
        $scope.attorneyInfoList = response.gao_User_Info_List

        $scope.displayAttorneyInfo = function (selectedOption) {

            $scope.getSelectedAttorneyUserInfoFromGAO_userInfoList = $filter(
                'filter')($scope.attorneyInfoList, {
                user_Id: selectedOption,
            });

            $scope.attorneyInfo = $scope.getSelectedAttorneyUserInfoFromGAO_userInfoList[0];
            $scope.origGaoId = $scope.attorneyInfo.gao_user_id;

            $scope.selectedAttorneyGroupId = $scope.attorneyInfo && $filter('filter')($scope.groups, {
                id: $scope.attorneyInfo.group_No,
            });

            $scope.selectedAttorneyGroupId = $scope.selectedAttorneyGroupId && $scope.selectedAttorneyGroupId[0];

            $scope.selectedAttorneyRole = $scope.attorneyInfo && $filter('filter')($scope.gaoRoles, {
                id: $scope.attorneyInfo.role_id,
            });

            $scope.selectedAttorneyRole = $scope.selectedAttorneyRole && $scope.selectedAttorneyRole[0];
        }
    })

    $scope.showAddAttorneyForm = false;
    $scope.showAttorneyForm = function () {
        $scope.showAddAttorneyForm = true;
        $timeout(function() {
            $('#email').focus();
        }, 0);
    }
    $scope.registerAttorneyCancel = function () {
        $scope.showAddAttorneyForm = false;
        $timeout(function() {
            $('#gao_user').focus();
        }, 0);
    }

    $scope.registerAttorneyInfo = function (form) {

        manageAttorneyInfoSvc.registerAttorneyInfo(form).then(function (data) {
            if (typeof data.success != 'undefined' && data.success) {
                var customAttr = {
                    headerText: "Success",
                    bodyText: "You have successfully registered the user.",
                    modalType: "success",
                    actionType: "",
                    cancelBtnReq: "N",
                    cancelBtnActionType: ""
                }

                actionMessageSvc.showModal(customAttr).then(function () {
                    $route.reload();
                });
            }
        });
    }
    $scope.representativeAddressDetails = '';


    /*$scope.deleteAttorneyInfo = function(userId) {

        $http({
            url : '/epds/delete-user',
            method : 'POST',
            headers : {
                'Content-Type' : 'application/json'
            },
            params : {
                "user_Id" : userId,
                "isGAO_User" : "Y"
            }
        }).then(function(response) {
        })
    }*/

    $scope.editForm = function (isEdit) {
        if (isEdit === "Y") {
            $scope.displayEditAttorneyForm = true;
            $timeout(function() {
                $('#a_gaoId').focus();
            }, 0);
        } else if (isEdit === "N") {
            $route.reload();
            $scope.displayEditAttorneyForm = false;
            $timeout(function() {
                $('#gao_user').focus();
            }, 0);
        }
    }

    $scope.deleteAttorneyInfo = function (firstName, lastName,
                                          userId) {

        var partyType = {
            id: "1",
            bodyText: "Are you sure you want to permanently delete " + lastName + " ," + firstName + "EPDS user account",
            isGAOUser: "Y"


        }
        $scope.headerText = "Delete User"

        partiesDataSvc.deletePartiesFromTheCase(userId,
            firstName, lastName, partyType)

    }

    $scope.updateAttorneyInfo = function (editAttorneyInfoForm, attorneyInfo) {
        var form = {
            title: attorneyInfo.title,
            groupNo: (typeof $scope.selectedAttorneyGroupId != 'undefined' ? $scope.selectedAttorneyGroupId.id : null),
            prefix: attorneyInfo.prefix,
            lastName: attorneyInfo.last_Name,
            firstName: attorneyInfo.first_Name,
            middle_initial: attorneyInfo.middle_initial,
            suffix: attorneyInfo.suffix,
            email: attorneyInfo.email,
            phoneNo: attorneyInfo.phone_No ? $scope.phonenumberCountryCode + attorneyInfo.phone_No : attorneyInfo.phone_No,
            faxNo: attorneyInfo.fax_No ? $scope.faxnumberCountryCode + attorneyInfo.fax_No : attorneyInfo.fax_No,
            epds_role_id: $scope.selectedAttorneyRole.id,
            role: $scope.selectedAttorneyRole.label,
            isGAO_User: "Y",
            gaoId: attorneyInfo.gao_user_id
        }

        manageAttorneyInfoSvc.updateUserInfo(form).then(function (data) {
            $route.reload();
        })

    }
}

(function () {
    'use strict';

    var serviceId = 'manageAttorneyInfoSvc';

    angular.module('epdsApp.adminDashboard').factory(serviceId,
        ['$rootScope', '$http', '$filter', '$uibModal', '$uibModalStack',
            'modalService', '$location', '$timeout', 'userInfoService', '$httpParamSerializerJQLike', '$route', 'navigationSvc', 'partiesDataSvc', 'actionMessageSvc', '$q', manageAttorneyInfoSvc]);

    function manageAttorneyInfoSvc($rootScope, $http, $filter, $uibModal, $uibModalStack,
                                   modalService, $location, $timeout, userInfoService, $httpParamSerializerJQLike, $route, navigationSvc, partiesDataSvc, actionMessageSvc, $q) {

        var service = {
            loadManageAttorneyView: loadManageAttorneyView,
            deleteAttorneyInfo: deleteAttorneyInfo,
            registerAttorneyInfo: registerAttorneyInfo,
            updateUserInfo: updateUserInfo,
            getListOfGAOUsrs: getListOfGAOUsrs,
            deleteUser: deleteUser
        };

        return service;

        function loadManageAttorneyView() {


            return $http({
                url: '/epds/manage-attorney-contacts-view',
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
            })
            .then(
                function (data) {

                    if (data.data.authorized) {
                        $rootScope.authenticated = true;
                        userInfoService
                        .setUserInfo(data.data.user_Info);
                        var navigationObj = {
                            navigationType: "dashboard",
                            caseStatus: "N/A",
                            roleId: data.data.user_Info.role_id
                        }

                        navigationSvc.setListOfRoutesBasedOnRole(navigationObj);
                    } else {

                        $rootScope.authenticated = false;
                        $rootScope.sessionExpired = true;
                        $rootScope.redirectMessage = "You are not authorized"
                        $location.path("/").replace();
                    }

                    return getListOfGAOUsrs().then(function (response) {

                        return getListOfRolesAndGroups(response);
                    });
                },
                function (error) {

                    return error;
                });

        }

        function getListOfRolesAndGroups(response) {

            var vm = {};

            var gaoGroups = [];

            vm.gao_User_Info_List = response.gao_User_Info_List;

            var filteredAttorneyInfoListByUniqueGroupId = $filter('unique')(response.gao_User_Info_List, "group_No");

            angular.forEach(filteredAttorneyInfoListByUniqueGroupId, function (value, key) {

                if (value.group_No != null) {
                    gaoGroups.push({
                        id: value.group_No,
                        label: "Group " + value.group_No
                    })
                }

                gaoGroups = $filter("orderBy")(gaoGroups, 'id')
            });

            vm.gaoGroups = gaoGroups;
            vm.gaoRoles = [{
                label: "ADMIN",
                id: 7

            }, {
                label: "ATTORNEY",
                id: 3

            }, {
                label: "SUPERVISOR",
                id: 8

            }]

            return vm;
        }

        function deleteAttorneyInfo(firstName, lastName, userId) {
            var partyType = {
                id: "1",
                bodyText: "Are you sure you want to permanently delete " + lastName + " ," + firstName + "EPDS user account"

            }
            $scope.headerText = "Delete Attorney Info"

            partiesDataSvc.deletePartiesFromTheCase(userId,
                firstName, lastName, partyType)
        }

        function registerAttorneyInfo(form) {

            var form = {
                title: form.title,
                gaoId: form.gaoId,
                groupNo: form.groupId.id,
                prefix: form.prefix,
                lastName: form.lastname,
                firstName: form.firstname,
                middle_initial: form.mi,
                suffix: form.suffix,
                email: form.email,
                phoneNo: form.phonenumber,
                faxNo: form.faxnumber,
                epds_role_id: form.role.id,
                role: form.role.label
            }

            return $http({
                url: '/epds/register-gao-info',
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded'
                },
                data: $httpParamSerializerJQLike(_.omitBy(form, _.isNil)),
            }).then(
                function (data) {

                    return data.data;

                },
                function (error) {

                    return error;
                });
        }

        function deleteUser(userId, isGAOUser) {

            var params = {
                user_Id: userId,
                isGAO_User: isGAOUser,
            }

            return $http({
                url: '/epds/delete-user',
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded'
                },
                data: $httpParamSerializerJQLike(_.omitBy(params, _.isNil)),
            }).then(
                function (data) {

                    if (data.data.isSuccess) {
                        var customAttr = {
                            headerText: "Success",
                            bodyText: "You have successfully deleted the user from EPDS",
                            modalType: "success",
                            actionType: "",
                            cancelBtnReq: "N",
                            cancelBtnActionType: ""
                        }

                        actionMessageSvc.showModal(customAttr).then(function () {
                            $route.reload();
                        });

                    } else if (!data.data.authorized) {
                        $rootScope.authenticated = false;
                        $rootScope.sessionExpired = true;
                        $rootScope.redirectMessage = "You are not authorized"
                        $location.path("/").replace();
                    }

                    return data.data;

                },
                function (error) {

                    return error;
                });

        }

        function getListOfGAOUsrs() {

            return $http({
                url: '/epds/get-gao-user-list',
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded'
                },
            }).then(
                function (data) {

                    return data.data;

                },
                function (error) {

                    return error;
                });
        }

        function updateUserInfo(form) {

            var params = form;

            return $http({
                url: '/epds/edit-user-info',
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded'
                },
                data: $httpParamSerializerJQLike(_.omitBy(params, _.isNil)),
            }).then(
                function (data) {

                    return data.data;

                },
                function (error) {

                    return error;
                });
        }

    }
})();


(function () {
    'use strict';

    angular.module('epdsApp.adminDashboard').directive('gaoid', ['$q', '$timeout', '$http', '$compile', '$httpParamSerializerJQLike',
        function ($q, $timeout, $http, $compile, $httpParamSerializerJQLike) {
            return {
                require: 'ngModel',

                link: function (scope, elm, attrs, ctrl) {
                    ctrl.$asyncValidators.gaoid = function (modelValue, viewValue) {
                        if (ctrl.$isEmpty(modelValue)) {
                            return $q.when();
                        }

                        var def = $q.defer();

                        var params = {
                            gaoId: modelValue
                        }
                        $http({
                            url: '/epds/validate-gao-id',
                            method: 'POST',
                            headers: {
                                'Content-Type': 'application/x-www-form-urlencoded'
                            },
                            data: $httpParamSerializerJQLike(_.omitBy(params, _.isNil)),
                            ignoreLoadingBar: true
                        })
                        .then(function (response) {
                            var data = response.data;
                            var origGaoId = ctrl.$$scope.origGaoId;
                            if (!data.isExists || (data.gaoId === origGaoId)) {
                                def.resolve();
                            } else {
                                def.reject();
                            }

                        });


                        return def.promise;
                    };

                }
            };
        }]);


})();
