angular.module('epdsApp.adminDashboard').controller('ManageAgencyCtrl', ManageAgencyCtrl);

ManageAgencyCtrl.$inject = ['$scope', '$log', '$rootScope', '$location', '$http', '$route',
    '$routeParams', '$uibModal', '$templateCache',
    '$templateRequest', '$sce', '$filter', '$compile', '$interpolate',
    'partiesDataSvc', 'manageAgencyInfoSvc', 'caseDocketDataSvc', 'actionMessageSvc', 'agencyDropDownService', 'regEx',
	'toolTip', '$timeout']


function ManageAgencyCtrl($scope, $log, $rootScope, $location, $http, $route, $routeParams, $uibModal, $templateCache,
                          $templateRequest, $sce, $filter, $compile, $interpolate, partiesDataSvc, manageAgencyInfoSvc,
						  caseDocketDataSvc, actionMessageSvc, agencyDropDownService, regEx, toolTip, $timeout) {

    $scope.displayEditAttorneyForm = false;


    $scope.options = null;
    $scope.representativeAddressDetails1 = ''
    $scope.representativeAddressDetails2 = '';
    $scope.updateAgencyAddressForm = false;


    $scope.form = {
        email: null,
        prefix: null,
        firstName: null,
        middle_initial: null,
        lastName: null,
        suffix: null,
        phoneNo: null,
        faxNo: null,
        address1: null,
        address2: null,
        city: null,
        state: null,
        zipCode: null,
        country: null,

    }

    $scope.agencyRepAddressForm = {
        address1: null,
        address2: null,
        city: null,
        state: null,
        zipCode: null,
        country: null,

    }


    $scope
    .$watch(
        'representativeAddressDetails1',
        function () {

            if (typeof $scope.representativeAddressDetails1.address_components != 'undefined') {


                $scope.representativeAddressDetails1 = retrieveAddressDetailsFromUserSelection(
                    $scope,
                    $scope.representativeAddressDetails1);
                $scope.agencyRepAddressForm.address1 = $scope.representativeAddressDetails1.streetAddr,
                    $scope.agencyRepAddressForm.zipCode = $scope.representativeAddressDetails1.zipcode
                $scope.agencyRepAddressForm.country = $scope.representativeAddressDetails1.country,
                    $scope.agencyRepAddressForm.state = $scope.representativeAddressDetails1.state,
                    $scope.agencyRepAddressForm.city = $scope.representativeAddressDetails1.city
            }
        });

    $scope
    .$watch(
        'representativeAddressDetails2',
        function () {

            if (typeof $scope.representativeAddressDetails2.address_components != 'undefined') {

                $scope.representativeAddressDetails2 = retrieveAddressDetailsFromUserSelection(
                    $scope,
                    $scope.representativeAddressDetails2);
                $scope.form.address1 = $scope.representativeAddressDetails2.streetAddr,
                    $scope.form.zipCode = $scope.representativeAddressDetails2.zipcode
                $scope.form.country = $scope.representativeAddressDetails2.country,
                    $scope.form.state = $scope.representativeAddressDetails2.state,
                    $scope.form.city = $scope.representativeAddressDetails2.city
            }
        });


    $scope.tier2SelectedOption = {};

    manageAgencyInfoSvc.loadManageAgencyView().then(function (response) {

        agencyDropDownService.getListOfTier1Agencies().then(function (data) {
            $scope.tier1SelectedOption = data.tier1SelectedOption;
            $scope.tier1AgencyList = data.tier1AgencyList;

            $timeout(function () {
                $('#agency_tier_1').focus();
            }, 0);
        });
    })


    $scope.displayAgencyPointOfContacts = function (tier1SelectedOption, tier2SelectedOption) {
        $scope.registerAgencyForm = false;
        manageAgencyInfoSvc.getListOfAgencyPOC(tier1SelectedOption, tier2SelectedOption).then(function (response) {

            $scope.agency_user_Info_List = response.user_Info_List;

            $scope.agency_user_Info_List = $filter("remove")($scope.agency_user_Info_List, null)

            if ($scope.agency_user_Info_List != null) {
                try {

                    $scope.agencyRepAddressForm.address1 = $scope.agency_user_Info_List[0].address1,
                        $scope.agencyRepAddressForm.address2 = $scope.agency_user_Info_List[0].address2,
                        $scope.agencyRepAddressForm.zipCode = $scope.agency_user_Info_List[0].zip_Code
                    $scope.agencyRepAddressForm.country = $scope.agency_user_Info_List[0].country,
                        $scope.agencyRepAddressForm.state = $scope.agency_user_Info_List[0].state,
                        $scope.agencyRepAddressForm.city = $scope.agency_user_Info_List[0].city
                    $scope.showUpdateAgencyAddForm = true;

                } catch (ex) {
                    console.log(ex)
                }
            } else {
                $scope.showUpdateAgencyAddForm = false;
                var customAttr = {
                    headerText: "Info",
                    bodyText: "There are no Agency POC's assigned to this agency yet. Please select 'Register New Agency User' to assign Agency POC for this agency. ",
                    modalType: "info",
                    actionType: "",
                    cancelBtnReq: "N",
                    cancelBtnActionType: ""
                }

                actionMessageSvc.showModal(customAttr);
            }

        });

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

    $scope.registerAgencyForm = false;

    $scope.showRegisterAgencyForm = function () {
        $scope.registerAgencyForm = true;
    }
    $scope.registerAgencyCancel = function () {
        $scope.form = {
            email: null,
            prefix: null,
            firstName: null,
            middle_initial: null,
            lastName: null,
            suffix: null,
            phoneNo: null,
            faxNo: null,
            address1: null,
            address2: null,
            city: null,
            state: null,
            zipCode: null,
            country: null,

        }
        $scope.registerAgencyForm = false;

        $timeout(function () {
            $('#agency_tier_1').focus();
        }, 0);
    }

    $scope.registerAgencyInfo = function (form, tier1SelectedOption, tier2SelectedOption) {


        try {

            if (!agencyDropDownService.validateAgencyInfo(tier1SelectedOption, tier2SelectedOption)) {
                return;
            }


            if ('undefined' !== tier2SelectedOption
                || tier2SelectedOption !== "e"
                || null !== tier2SelectedOption) {
                form.tier2_agency_id = tier2SelectedOption.agency_Id;
            }

        } catch (ex) {
            console.log(ex)
        }

        form.address1 = $scope.form.address1;
        form.address2 = $scope.form.address2;
        form.zipCode = $scope.form.zipCode;
        form.country = $scope.form.country;
        form.state = $scope.form.state;
        form.city = $scope.form.city;
        form.tier1_agency_id = tier1SelectedOption.agency_Id;
        form.phoneNo = form.phoneNo ? $scope.phonenumberCountryCode + form.phoneNo : form.phoneNo;
        form.faxNo = form.faxNo ? $scope.faxnumberCountryCode + form.faxNo : form.faxNo;

        manageAgencyInfoSvc.registerAgencyInfo(form).then(function (data) {

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
            } else if (typeof data.exists != 'undefined' && data.exists) {
                var customAttr = {
                    headerText: "Success",
                    bodyText: "User already exists. ",
                    modalType: "success",
                    actionType: "",
                    cancelBtnReq: "N",
                    cancelBtnActionType: ""
                }

                actionMessageSvc.showModal(customAttr)
            }
        });
    }

    $scope.editForm = function (isEdit) {
        if (isEdit === "Y") {
            $scope.displayEditAttorneyForm = true;
        } else if (isEdit === "N") {
            $scope.displayEditAttorneyForm = false;
        }
    }

    $scope.deleteAgencyInfo = function (firstName, lastName,
                                        userId) {

        var partyType = {
            id: "1",
            bodyText: "Are you sure you want to permanently delete " + lastName + " ," + firstName + "EDS user account",
            isGAOUser: "N",
            fullDelete: "Y"


        }
        $scope.headerText = "Delete User"

        partiesDataSvc.deletePartiesFromTheCase(userId,
            firstName, lastName, partyType)

    }


    $scope.updateAgencyInfo = function (editAttorneyInfoForm,
                                        agencyInfo, tier1SelectedOption, tier2SelectedOption) {

        var form = {
            prefix: agencyInfo.prefix,
            lastName: agencyInfo.last_Name,
            firstName: agencyInfo.first_Name,
            middle_initial: agencyInfo.middle_initial,
            suffix: agencyInfo.suffix,
            email: agencyInfo.email,
            phoneNo: agencyInfo.phone_No ? $scope.phonenumberCountryCode + agencyInfo.phone_No : agencyInfo.phone_No,
            faxNo: agencyInfo.fax_No ? $scope.faxnumberCountryCode + agencyInfo.fax_No : agencyInfo.fax_No,
            address1: $scope.agencyRepAddressForm.address1,
            address2: $scope.agencyRepAddressForm.address2,
            zipCode: $scope.agencyRepAddressForm.zipCode,
            country: $scope.agencyRepAddressForm.country,
            state: $scope.agencyRepAddressForm.state,
            city: $scope.agencyRepAddressForm.city,
            tier1_agency_id: tier1SelectedOption.agency_Id,
            tier2_agency_id: tier2SelectedOption.agency_Id,
            isGAO_User: "N"
        }


        manageAgencyInfoSvc.updateUserInfo(form).then(function (data) {
            $route.reload();
        })


    }


    $scope.updateAgencyAddress = function (agencyRepAddressForm, tier1SelectedOption, tier2SelectedOption) {

        // Commenting out. this code has never run successfully. Checked back through source history.
        // agencyRepAddressForm has always been the var passed in, no local var defined until after, and not using the $scope.form.
        // Leaving here in case there's a bug found in update agency address.
        // Maybe agencyRepAddressForm needs to be just form, then var below renamed?
        // try {
        //     if (typeof form.tier2Id === 'undefined') {
        //         form.tier2Id.agency_Id = "null";
        //     }
        //
        //     if (typeof form.tier2Id.agency_Id !== 'undefined') {
        //         nameOfFirm = form.tier2Id.agency_Name;
        //     } else {
        //         nameOfFirm = form.tier1Id.agency_Name;
        //     }
        // } catch (ex) {
        //     console.log(ex)
        // }


        var form = {

            address1: $scope.agencyRepAddressForm.address1,
            address2: $scope.agencyRepAddressForm.address2,
            zipCode: $scope.agencyRepAddressForm.zipCode,
            country: $scope.agencyRepAddressForm.country,
            state: $scope.agencyRepAddressForm.state,
            city: $scope.agencyRepAddressForm.city,
            tier1_agency_id: tier1SelectedOption.agency_Id,
            tier2_agency_id: tier2SelectedOption.agency_Id,
        }


        manageAgencyInfoSvc.updateAgencyAddress(form).then(function (data) {
            $route.reload();
        })


    }

    $scope.cancelUpdate = function () {
        $timeout(function () {
            $('#agency_tier_1').focus();
        }, 0);
    }
}

(function () {
    'use strict';

    var serviceId = 'manageAgencyInfoSvc';
    /* @ngInject */
    angular.module('epdsApp.adminDashboard').factory(serviceId,
        ['$rootScope', '$http', '$filter', '$uibModal', '$uibModalStack',
            'modalService', '$location', '$timeout', 'userInfoService', '$httpParamSerializerJQLike',
            '$route', 'navigationSvc', 'partiesDataSvc', 'actionMessageSvc', '$q', manageAgencyInfoSvc]);

    function manageAgencyInfoSvc($rootScope, $http, $filter, $uibModal, $uibModalStack,
                                 modalService, $location, $timeout, userInfoService, $httpParamSerializerJQLike, $route, navigationSvc, partiesDataSvc, actionMessageSvc, $q) {

        var service = {
            loadManageAgencyView: loadManageAgencyView,
            deleteAgencyInfo: deleteAgencyInfo,
            registerAgencyInfo: registerAgencyInfo,
            updateUserInfo: updateUserInfo,
            updateAgencyAddress: updateAgencyAddress,
            deleteUser: deleteUser,
            getListOfAgencyPOC: getListOfAgencyPOC

        };

        return service;

        function loadManageAgencyView() {


            return $http({
                url: '/epds/manage-agency-contacts-view',
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

                    return data.data;
                },
                function (error) {

                    return error;
                });

        }


        function deleteAgencyInfo(firstName, lastName, userId) {

            partiesDataSvc.deletePartiesFromTheCase(userId,
                firstName, lastName, partyType)
        }

        function registerAgencyInfo(form) {


            return $http({
                url: '/epds/register-agency-info',
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
                ignoreLoadingBar: true
            }).then(
                function (data) {

                    if (data.data.isSuccess) {
                        var customAttr = {
                            headerText: "Success",
                            bodyText: "You have successfully deleted the user from EDS",
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

        function updateUserInfo(form) {

            var params = form;

            return $http({
                url: '/epds/edit-agency-info',
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


        function updateAgencyAddress(form) {

            var params = form;

            return $http({
                url: '/epds/update-agency-add',
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


        function getListOfAgencyPOC(
            tier_1_AgencySelectedOption,
            tier_2_AgencySelectedOption) {

            var params = {
                tier1_Agency_Id: tier_1_AgencySelectedOption.agency_Id,

            }

            if ('undefined' === tier_2_AgencySelectedOption
                || tier_2_AgencySelectedOption === "e"
                || null === tier_2_AgencySelectedOption) {
                params.tier2_Agency_Id = "null";
            } else {
                params.tier2_Agency_Id = tier_2_AgencySelectedOption.agency_Id;
            }

            if (angular.equals({}, tier_2_AgencySelectedOption)) {
                params.tier2_Agency_Id = "null"
            }
            return $http({
                url: '/epds/view-agency-user-info',
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
