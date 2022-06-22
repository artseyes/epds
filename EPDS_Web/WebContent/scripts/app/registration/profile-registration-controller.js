angular.module('epdsApp.registration').controller('registrationCtrl',
    registrationCtrl);

registrationCtrl.$inject = ['$scope', '$http', '$location', '$window', '$rootScope',
    'ngFabForm', '$uibModal', 'modalService', 'agencyDropDownService', '$routeParams',
    'registrationService', 'ModalSvc', 'authFeedbackMessagesSvc', 'authenticationService',
    'vcRecaptchaService', 'recaptchaConfig', 'Idle', 'actionMessageSvc', 'localStorageService', 'regEx',
    'toolTip']

function registrationCtrl($scope, $http, $location, $window, $rootScope,
                          ngFabForm, $uibModal, modalService, agencyDropDownService, $routeParams, registrationService, ModalSvc,
                          authFeedbackMessagesSvc, authenticationService, recaptcha, recaptchaConfig, Idle, actionMessageSvc, localStorageService, regEx, toolTip) {

    $scope.recaptchaConfig = recaptchaConfig;
    registrationService.getRegistrationFieldsBasedOnRole($routeParams.role).then(
        function (data) {
            $scope.fieldText = data.fieldText
        });
    $scope.regex = regEx;
    /*registrationService.getFormInputRegexPatterns().then(function(regex){

    })*/

    $scope.role = $routeParams.role;
    /*$scope.prefix = [
                    { id: 1, name: 'Mr.'},
                    { id: 2, name: 'Mrs.'}
                ];*/
    Idle.unwatch();

    /*if ($location.url() == "/login"){
        Idle.unwatch();
    }else{
        Idle.watch();
    }*/

    if ($routeParams.role == "6") {
        $scope.tier2SelectedOption = {};
        agencyDropDownService.getListOfTier1Agencies().then(
            function (data) {
                $scope.tier1SelectedOption = data.tier1SelectedOption;
                $scope.tier1AgencyList = data.tier1AgencyList;
            });
    }

    $scope.customFormOptions = {
        validationsTemplate: 'scripts/app/registration/register-form-validations.html',
        preventInvalidSubmit: true,
        preventDoubleSubmit: true,
        setFormDirtyOnSubmit: true,
        scrollToAndFocusFirstErrorOnSubmit: true,
        scrollAnimationTime: 900,
        scrollOffset: -100,
    };

    $scope.form = {
        email: null,
        prefix: null,
        firstname: null,
        lastname: null,
        suffix: null,
        phonenumber: null,
        faxnumber: null,
        address1: null,
        address2: null,
        zipcode: null,
        city: null,
        state: null,
        country: null,
    }

    $scope.options = null;
    $scope.representativeAddressDetails = '';

    $scope.$watch('representativeAddressDetails', function () {
        if (typeof $scope.representativeAddressDetails.address_components != 'undefined') {
            $scope.representativeAddressDetails = retrieveAddressDetailsFromUserSelection(
                $scope, $scope.representativeAddressDetails);
            $scope.form.address1 = $scope.representativeAddressDetails.streetAddr;
            $scope.form.zipcode = $scope.representativeAddressDetails.zipcode;
            $scope.form.country = $scope.representativeAddressDetails.country;
            $scope.form.state = $scope.representativeAddressDetails.state;
            $scope.form.city = $scope.representativeAddressDetails.city;
        }
    });

    $scope.setWidgetId = function (widgetId) {
        recaptcha.reload($scope.widgetId);
        $scope.widgetId = widgetId;
    };

    $scope.setResponse = function (response) {
        authenticationService.VerifyRecaptcha(response).then(function (data) {
            if (!data.isResponseValid) {
                recaptcha.reload($scope.widgetId);
            }
            $scope.isResponseValid = data.isResponseValid;
        });
    };

    $scope.cbExpiration = function () {
        recaptcha.reload($scope.widgetId);
        $scope.isResponseValid = null;
    };

    $scope.isRegisterFormValid = function (form, terms) {
        $scope.formValidatonMessage = "Register"
        if (form.$invalid) {
            $scope.formValidatonMessage = "Please enter all of the required fields."
        } else if (!terms) {
            $scope.formValidatonMessage = "You must acknowledge before you can register for an account. "
        }
        return form.$invalid || !terms;
    };


    $scope.registerUserInfo = function (form, tier1SelectedOption, tier2SelectedOption) {
        if ($routeParams.role == "6"
            && !agencyDropDownService.validateAgencyInfo(tier1SelectedOption, tier2SelectedOption)) {
            return;
        }

        form.tier1Id = tier1SelectedOption;
        form.tier2Id = tier2SelectedOption;
        form.role = $routeParams.role;
        form.phonenumber = form.phonenumber ? $scope.phonenumberCountryCode + form.phonenumber : form.phonenumber;
        form.faxnumber = form.faxnumber ? $scope.faxnumberCountryCode + form.faxnumber : form.faxnumber;

        registrationService.validateRegisterationForm(form).then(function (validationResponse) {
            if (validationResponse.isSuccess) {
                authenticationService.rulesOfBehavior(form.email).then(function (rulesOfBehavior) {
                    if (rulesOfBehavior.isSuccess) {
                        registrationService.registerUser(form).then(function (response) {
                            if (typeof response.inputErrors !== 'undefined') {
                                var customAttr = {
                                    headerText: "Error",
                                    bodyText: "",
                                    modalType: "error",
                                    actionType: "",
                                    cancelBtnReq: "N",
                                    cancelBtnActionType: "",
                                    inputErrorMessages: response.inputErrors
                                }

                                actionMessageSvc.showModal(customAttr);
                            }

                            var obj = {}
                            if (response.isSuccess) {
                                authFeedbackMessagesSvc.getFeedbackMessages("regSuccess").then(function (response) {
                                    obj.data = response.data;

                                    actionMessageSvc.showModal(obj.data).then(function (result) {
                                        $location.path("/").replace();
                                    })
                                })
                            }
                        });
                    }
                });
            }
        })
    }
}

