angular.module(
    'epdsApp.parties')
.controller('PartiesCtrl', PartiesCtrl);
PartiesCtrl.$inject = ['$rootScope', '$scope', '$http', '$filter', '$uibModal', 'modalService', '$location', '$timeout',
	'partiesDataSvc', 'PartiesData', 'base64', 'actionMessageSvc', '$routeParams']

function PartiesCtrl($rootScope, $scope, $http, $filter, $uibModal, modalService, $location, $timeout, partiesDataSvc,
					 PartiesData, base64, actionMessageSvc, $routeParams) {
    $timeout(function () {
        $('#headerInfo').animateCss('slideInRight');
        $("#partiesView").animateCss('fadeInUpBig')
        $('#focus_start').focus();
    }, 500);

    $scope.urlEncodedANum = $routeParams.aNum

    $scope.loggedInUserInfo = PartiesData.user_Info
    $scope.attorneyInfo = PartiesData.attorneyInfo;
    $scope.protestInfo = PartiesData.protestInfo;
    $scope.isViewOnly = PartiesData.protestInfo.viewOnly;
    $scope.role = PartiesData.protestInfo.role;
    $scope.isProtesterAssigned = PartiesData.isProtesterAssigned;
    $scope.protester_parties_list = PartiesData.protester_parties_list;
    $scope.listOfIntervenorList = PartiesData.listOfIntervenorParty;
    $scope.orphanIntComDetailList = PartiesData.orphanIntComDetailList
    $scope.primary_agency_list = PartiesData.primary_agency_list;
    $scope.secondary_agency_list = PartiesData.secondary_agency_list;

    $scope.isGAOUserSupervisorOrAttorney = ($scope.loggedInUserInfo.role_id == '8' || $scope.loggedInUserInfo.role_id == '3');
    $scope.isGAOUserWithFullAccess = ($scope.loggedInUserInfo.role_id == '7' || ($scope.isGAOUserSupervisorOrAttorney && !$scope.protestInfo.viewOnly));
    $scope.agencyUser = ($scope.loggedInUserInfo.role_id == '5' || $scope.loggedInUserInfo.role_id == '6');
    $scope.isAgencyPOCUser = ($scope.loggedInUserInfo.role_id == '5');
    $scope.isFirmIdSame = ($scope.loggedInUserInfo.firm_id == $scope.protestInfo.agency_Info_Id) || $filter('contains')($scope.protestInfo.primaryAgencyInfoIds, $scope.protestInfo.agency_Info_Id);
    $scope.canSecAgencyAddAgencyBtnBeDisplayed = false;
    $scope.canPrimaryAgencyAddAgencyBtnBeDisplayed = false;

    $scope.addIntervenor = function () {
        $uibModal.open({
            templateUrl: 'scripts/app/parties/addIntervenor.htm',
            backdrop: 'static',
            controller: ['$scope', '$uibModalInstance', function ($scope, $uibModalInstance) {
                $scope.protestInformation = PartiesData.protestInfo;
                $scope.cancel = function () {
                    $uibModalInstance.dismiss('cancel');
                };
            }],
            //controllerAs: 'vm',
            /*resolve: {
                error: function () { return error },
            }*/
        }).result.catch(angular.noop);
    }

    /* Adding parties to the case */

    $scope.addPartiesToTheCase = function (partyType, inviterRole, companyName, companyAddr) {
        partiesDataSvc.addPartiesToTheCase(partyType, inviterRole, companyName, companyAddr, $scope.protestInfo);
    }

    $scope.editPartyInfo = function (partyType, form) {
        form.aNum = form && PartiesData.protestInfo.a_No;
        form.partyType = partyType;
        partiesDataSvc.editPartiesInfo(form);
    }

    $scope.removeIntervenorAccessFromThisCase = function (intervenorInfo) {
        if (intervenorInfo && intervenorInfo[0] && intervenorInfo[0].intervenorCompanyInfo) {
            intervenorInfo = intervenorInfo && (intervenorInfo[0].intervenorCompanyInfo);
        }

        if (intervenorInfo.companyAddress) {
            intervenorInfo.companyAddress = base64.urlencode(intervenorInfo.companyAddress)
        }

        if (intervenorInfo.companyDetail) {
            intervenorInfo.companyDetail = base64.urlencode(intervenorInfo.companyDetail)
        }

        var bodyText = "<p> Are you sure  you want to remove this intervenor's access from this case? </p>"

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
                partiesDataSvc.removeIntervenor(intervenorInfo, PartiesData.protestInfo.a_No);
            }

        })
    }

    if ($scope.isGAOUserWithFullAccess
        && ($scope.secondary_agency_list && $scope.secondary_agency_list.length <= 0)) {
        $scope.canSecAgencyAddAgencyBtnBeDisplayed = true;
    }

    if ($scope.primary_agency_list && $scope.primary_agency_list.length <= 0) {
        if ($scope.isGAOUserWithFullAccess
            || ($scope.isAgencyPOCUser && $scope.isFirmIdSame)) {

            $scope.canPrimaryAgencyAddAgencyBtnBeDisplayed = true;
        }
    }
}

invitePartiesToTheCaseModalInstanceCtrl.$inject = ['$scope', '$uibModalInstance', '$http', 'modalService',
	'modalOptions', '$uibModal', 'partiesDataSvc', 'items', 'regEx', 'toolTip'];

function invitePartiesToTheCaseModalInstanceCtrl($scope, $uibModalInstance, $http, modalService, modalOptions,
												 $uibModal, partiesDataSvc, items, regEx, toolTip) {
    $scope.options = modalOptions;
    $scope.repEmail = "";

    /* send invitation request to secondary representatives */

    $scope.sendInvite = function (repEmail) {
        partiesDataSvc.invitePartiesToTheCase(repEmail, items.inviterRole, items.typeOfRequest, items.companyName, items.companyAddr, items.aNum);

    };

    /* get primary or agency rep  Info */

    $scope.assignAttorney = function (repEmail, typeOfRequest) {
        partiesDataSvc.getPrimaryRepsOrAgencyRepsInfo(repEmail, items.inviterRole, items.typeOfRequest, items.companyName, items.companyAddr, items.aNum);
    }

    $scope.cancel = function () {
        $uibModalInstance.dismiss('cancel');
    };
};

editPartyInfoModalInstanceCtrl.$inject = ['$scope', '$uibModalInstance',
    '$http', 'modalService', '$uibModal', 'partiesDataSvc', 'form', 'regEx', 'toolTip', 'actionMessageSvc', '$route'];

function editPartyInfoModalInstanceCtrl($scope, $uibModalInstance,
                                        $http, modalService, $uibModal, partiesDataSvc, form, regEx, toolTip, actionMessageSvc, $route) {
    $scope.regex = regEx;
    $scope.form = form;
    $scope.$watch('companyAddressDetails',function () {
            if ($scope.companyAddressDetails && $scope.companyAddressDetails.address_components) {
                $scope.companyAddressDetails = retrieveAddressDetailsFromUserSelection(
                    $scope,
                    $scope.companyAddressDetails);
                $scope.form.address1 = $scope.companyAddressDetails.streetAddr,
                    $scope.form.zipCode = $scope.companyAddressDetails.zipcode
                $scope.form.country = $scope.companyAddressDetails.country,
                    $scope.form.state = $scope.companyAddressDetails.state,
                    $scope.form.city = $scope.companyAddressDetails.city
            }
        });
    /*
     * Update parties info
    */
    $scope.updateInfo = function (interveneProtestRequestForm, form) {
        /*if (interveneProtestRequestForm.$invalid || interveneProtestRequestForm.$pending){
            return;
        }*/

        partiesDataSvc.updatePartiesInfo(form).then(function (response) {

            if (response == "") {
                $uibModalInstance.dismiss('cancel');

                // removed delete success modal, with the reload it was only flashing on the screen
                // and user will see the updates after the reload
                $route.reload();
            }
        });
    };

    $scope.cancel = function () {
        $uibModalInstance.dismiss('cancel');
    };
};

/* final confirmation before adding primary representative / agency attorney */

assignPrimaryRep_AgencyAttorneysModalInstanceCtrl.$inject = ['$scope', '$uibModalInstance', '$http', 'items', '$window', 'partiesDataSvc', 'regEx', 'toolTip'];

function assignPrimaryRep_AgencyAttorneysModalInstanceCtrl($scope, $uibModalInstance, $http, items, $window, partiesDataSvc, regEx, toolTip) {
    $scope.attorneyInfo = items.attorney_Info;

    if (items.typeOfRequest === "Assign Primary Representative") {
        $scope.confirmationType = "protester"
    } else if (items.typeOfRequest === "Assign Agency Representative") {
        $scope.confirmationType = "attorney"
    }
    $scope.confirmAttorneyInfo = function (email) {
        partiesDataSvc.confirmPrimaryOrAgencyRepInfo(email, items.inviter_Type, items.typeOfRequest, items.companyName, items.companyAddr, items.aNum);
        $uibModalInstance.dismiss('cancel');

    }
    $scope.cancel = function () {
        $uibModalInstance.dismiss('cancel');
    }
}

/* final confirmation before deleting the secondary users */
deletePartiesFromTheCaseModalInstanceCtrl.$inject = ['$scope',
    '$uibModalInstance', '$http', 'modalService', '$window', 'partiesDataSvc', 'data',
    '$route', 'manageAttorneyInfoSvc', 'regEx', 'toolTip'];

function deletePartiesFromTheCaseModalInstanceCtrl($scope, $uibModalInstance, $http, modalService, $window,
												   partiesDataSvc, data, $route, manageAttorneyInfoSvc, regEx, toolTip) {

    $scope.userId = data.userId;
    $scope.firstName = data.firstName;
    $scope.lastName = data.lastName;
    $scope.headerText = data.headerText;
    $scope.partyType = data.partyType;
    $scope.deleteUser = function (secondaryRepUserId) {
        $uibModalInstance.dismiss('cancel');

        if (typeof data.partyType.isGAOUser === 'undefined') {
            partiesDataSvc.deleteUser(secondaryRepUserId, data.aNum).then(function (response) {
                if (response.isSuccess) {
                    var customModalOptions = {
                        headerText: 'success',
                        bodyText: data.firstName + '   ' + data.lastName
                            + '  has been successfully removed.',
                        closeButtonText: 'OK',
                        messageType: "success"
                    };

                    modalService.showModal({}, customModalOptions);
                }
            })
        } else if (data.partyType.isGAOUser === 'Y') {
            manageAttorneyInfoSvc.deleteUser(secondaryRepUserId, "Y");
        } else if (data.partyType.fullDelete === 'Y') {
            manageAttorneyInfoSvc.deleteUser(secondaryRepUserId, "N");
        }
    }

    $scope.cancel = function () {
        $uibModalInstance.dismiss('cancel');
    }
}

