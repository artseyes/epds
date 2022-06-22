angular.module('epdsApp.auth')
.constant('recaptchaConfig', {
    sitekey: '6LcFmBITAAAAABGxr2-en8pjLBjLaAum4w5kgn_h',//public key
    theme: 'light'
});

(function () {
    'use strict';

    var serviceId = 'authenticationService';

    angular.module('epdsApp.auth').factory(serviceId,
        ['$http', '$rootScope', '$uibModal', 'localStorageService', '$q', 'base64', 'Idle', '$location',
            '$httpParamSerializerJQLike', '$ocLazyLoad', 'randomString', '$injector', 'csrfService', authenticationService]);

    /* @ngInject */
    function authenticationService($http, $rootScope, $uibModal,
                                   localStorageService, $q, base64,
                                   Idle, $location, $httpParamSerializerJQLike, $ocLazyLoad, randomString, $injector, csrfService) {

        var service = {
            login: onLogin,
            logout: onLogout,
            checkPasswordVaidity: checkPasswordValidity,
            register: register,
            forgotPassword: forgotPassword,
            VerifyRecaptcha: VerifyRecaptcha,
            resetAccount: resetAccount,
            rulesOfBehavior: rulesOfBehaviorModal,
            invalidateSession: invalidateSession,
            getCredentials: getCredentials,
            encodePassword: encodePassword,
            submitRulesOfBehavior: submitRulesOfBehavior,
            downloadUserGuides: downloadUserGuides,
            openNewTab: openNewTab,
            updateTabLocation: updateTabLocation,
            openBlob: openBlob

        };


        $rootScope.$on('IdleTimeout', function () {

            service.logout("sessionTimeOut")
        });

        return service;

        function onLogin(credentials) {
            var params = {
                "email": base64.urlencode(credentials.userId) /*credentials.userId*/,
                "password": getCredentials(credentials)/*base64.urlencode(credentials.userPwd)credentials.userPwd*/
            }

            return $http({
                url: '/epds/user/login',
                method: 'POST',
                headers: {
                    'Accept': 'application/json',
                    'Content-Type': 'application/json'
                },
                data: angular.toJson(params)/*$httpParamSerializerJQLike(_.omitBy(params, _.isNil))*/

            })
            .then(function (data) {
                    return data.data
                },
                function (error) {
                    return error;
                });
        }


        function downloadUserGuides(guideType) {

            var userId = 0;
            var myNewTab;
            if (window.navigator && !window.navigator.msSaveOrOpenBlob) {
                myNewTab = openNewTab();
            }


            if ($rootScope.userProfileInfo && $rootScope.userProfileInfo.user_Id) {
                userId = $rootScope.userProfileInfo.user_Id;
            }

            return $http({
                url: "/epds/user-guides",
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                responseType: 'arraybuffer',
                params: {userId: userId, guideType: guideType || null}
            }).then(
                function (data) {

                    openBlob(data.data, 'application/pdf', "user-guide.pdf", myNewTab)
                },
                function (error) {
                    console.log("the request is error")
                    return error;
                });

        }

        function openNewTab() {
            var newTabWindow = window.open();
            return newTabWindow;
        }

        function updateTabLocation(tabLocation, tab) {
            if (!tabLocation) {
                tab.close();
            }
            tab.location.href = tabLocation;
        }

        function openBlob(response, contentType, filename, myNewTab) {

            var blob = new Blob([response], {type: contentType});

            if (window.navigator && window.navigator.msSaveOrOpenBlob) {
                window.navigator.msSaveOrOpenBlob(blob, filename);
            } else {

                var URL = window.URL;
                var downloadUrl = URL.createObjectURL(blob);
                //open in the same window
                //window.location.href = downloadUrl;
                updateTabLocation(downloadUrl, myNewTab);
                window.open(downloadUrl, '_blank');
                // cleanup
                setTimeout(function () {
                    URL.revokeObjectURL(downloadUrl);
                }, 100);

            }

        }

        function getCredentials(credentials) {
            var hmac = "";
            var key = "";
            if (credentials
                && credentials.userPwd) {
                key = randomString(32);
                var shaObj = new jsSHA("SHA-512", "TEXT");
                /*shaObj.setHMACKey(key, "TEXT");*/
                shaObj.update(credentials.userPwd);
                hmac = shaObj.getHash("HEX");
            }


            return base64.urlencode(hmac + ":" + key);
        }

        function encodePassword(credentials) {
            var b64Encode = "";
            var key = "";
            if (credentials
                && credentials.userPwd) {
                key = randomString(32);
                b64Encode = credentials.userPwd;
            }


            return base64.urlencode(b64Encode);
        }


        function checkPasswordValidity(viewValue) {

            return $http({
                url: url,
                method: 'POST',
                headers: {
                    'Accept': 'application/json',
                    'Content-Type': 'application/json'
                },

            }).then(
                function (data) {

                    return data.data

                },
                function (error) {

                    return error;
                });

        }


        function onLogout(sessionTimeOut) {

            Idle.unwatch();
            if (!sessionTimeOut) {
                $rootScope.redirectMessage = "";

            }


            return $http({
                url: '/epds/logout',
                method: 'POST',
                headers: {
                    'Accept': 'application/json',
                    'Content-Type': 'application/json'
                },

            })
            .then(
                function (data) {
                    localStorageService.clearAll();
                    localStorageService.cookie.clearAll();
                    $location.path("/login").replace();

                    return data.data
                },
                function (error) {
                    localStorageService.clearAll();
                    $location.path("/login").replace();
                    return error;
                });

        }

        function register() {

            Idle.unwatch();
            $location.path("/register").replace();
        }

        function forgotPassword() {

            Idle.unwatch();

            $uibModal.open({
                templateUrl: 'scripts/services/registration/resetPassword.tpl.html',
                controller: resetPasswordCtrlModalCtrl,
                animation: true,
                size: 'sm',
                keyboard: false,
                backdrop: false
            }).result.catch(angular.noop);

        }

        function VerifyRecaptcha(response) {


            var obj = {
                recaptchaResponse: response
            }

            return $http({
                url: '/epds/user/verify-captcha-response',
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded'
                },
                data: $httpParamSerializerJQLike(_.omitBy(obj, _.isNil)),
            })
            .then(
                function (data) {

                    return data.data
                },
                function (error) {

                    return error;
                });

        }

        function resetAccount(activityType) {

            $ocLazyLoad.load(['jquery-datatables', 'angular-datatables', 'dashboard', 'account-update', 'admin-dashboard', 'account-reset', 'angular-xeditable',
                'cds', 'agency', 'registeration', 'parties', 'manage-gao'], {
                serie: true,
                cache: false
            }).then(function () {

                var accountResetService = $injector.get("accountResetService");

                accountResetService.resetAccountModal(activityType);
            });

        }

        function rulesOfBehaviorModal(email) {

            var modalInstance = $uibModal.open({
                templateUrl: 'scripts/app/authentication/rulesOfBehavior.tpl.htm',
                controller: rulesOfBehaviorModalCtrl,
                resolve: {
                    email: function () {
                        return email;
                    },
                },
                animation: true,
                size: 'lg',
                keyboard: false,
                backdrop: 'static'
            });

            return modalInstance.result;
        }

        function submitRulesOfBehavior(email) {

            var obj = {
                email: base64.urlencode(email)
            }
            return $http({
                url: '/epds/user/rulesOfBehavior',
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded'
                },
                data: $httpParamSerializerJQLike(_.omitBy(obj, _.isNil)),
            })
            .then(
                function (data) {

                    return data.data
                },
                function (error) {

                    return error;
                });
        }


        function invalidateSession(credentials) {

            var obj = {
                email: credentials.userId
            }
            return $http({
                url: '/epds/user/invalidateSession',
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded'
                },
                data: $httpParamSerializerJQLike(_.omitBy(obj, _.isNil)),
            })
            .then(
                function (data) {

                    return data.data
                },
                function (error) {

                    return error;
                });
        }


        /*$rootScope.$on("IdleTimeout", service.logout);*/


    }


    angular.module('epdsApp.auth').controller('authFeedbackCtrl', ['$scope', '$element', 'close', 'data', function ($scope, $element, close, data) {

        $scope.data = data;

        $scope.close = function (result) {
            $element.modal('hide');
            close(result, 200);
        };

    }]);
})();

//user Info service

(function () {
    'use strict';

    var serviceId = 'userInfoService';


    angular.module('epdsApp.auth').factory(serviceId,
        ['$rootScope', '$q', userInfoService]);

    /* @ngInject */
    function userInfoService($rootScope, $q) {


        var service = {
            setUserInfo: setUserInfo,
            getRoleId: getRoleId,
            getRoleFromRoleId: getRoleFromRoleId
        };

        return service;

        function setUserInfo(userProfileInfo) {

            $rootScope.userProfileInfo = userProfileInfo;
        }

        function getRoleId(role) {

            var retVal = $rootScope.userProfileInfo && $rootScope.userProfileInfo.role_id;

            return $q.when(retVal);
        }


        function getRoleFromRoleId(roleId) {


            var retVal = $rootScope.userProfileInfo && $rootScope.userProfileInfo.role;

            return $q.when(retVal);
        }
    }
})();


angular.module('epdsApp.auth').directive('password', function ($q, $timeout, $http, localStorageService,
                                                               $httpParamSerializerJQLike, authenticationService) {
    return {
        require: 'ngModel',

        link: function (scope, elm, attrs, ctrl) {

            ctrl.$asyncValidators.password = function (modelValue, viewValue) {
                if (ctrl.$isEmpty(modelValue)) {
                    return $q.when();
                }

                var def = $q.defer();
                var length = 0;

                var credentials = {};
                credentials.userPwd = viewValue;
                if (viewValue) {
                    length = viewValue.length;
                }

                credentials.userPwd = authenticationService.encodePassword(credentials);

                if (length >= 12) {
                    $timeout(function () {
                        var params = {
                            user_id: localStorageService.get("userId"),
                            password: credentials.userPwd
                        }
                        $http({
                            url: '/epds/user/validate-password',
                            method: 'POST',
                            headers: {
                                'Content-Type': 'application/x-www-form-urlencoded'
                            },
                            data: $httpParamSerializerJQLike(_.omitBy(params, _.isNil)),

                            /*headers: {
                                'Accept': 'application/json',
                                'Content-Type': 'application/json'
                                    },
                            data: angular.toJson(params),*/
                            ignoreLoadingBar: true
                        }).then(function (response) {
                            var data = response.data;
                            if (data.message === "Valid password") {
                                scope.listOfErrorMessages = null;
                                def.resolve();
                            } else {
                                scope.listOfErrorMessages = data.data;
                                def.reject();
                            }

                        });
                    }, 20);
                } else {
                    scope.listOfErrorMessages = null;
                    def.reject();
                }


                return def.promise;
            };


        }
    };
});

angular.module('epdsApp.auth').directive('userid', ['$q', '$timeout', '$http', '$compile', 'base64',
    '$httpParamSerializerJQLike', 'regEx',
    function ($q, $timeout, $http, $compile, base64, $httpParamSerializerJQLike, regEx) {
        return {
            require: 'ngModel',

            link: function (scope, elm, attrs, ctrl) {
                ctrl.$asyncValidators.userid = function (modelValue, viewValue) {
                    if (ctrl.$isEmpty(modelValue)) {
                        return $q.when();
                    }

                    var def = $q.defer();

                    checkIfEmailExists(modelValue, def)

                    return def.promise;
                };

                function checkIfEmailExists(email, def) {
                    var emailREGEX = regEx.email;
                    var checkIfThisIsAValidEmailAddress = emailREGEX.exec(email)

                    if (checkIfThisIsAValidEmailAddress) {
                        var params = {
                            email: base64.urlencode(email)
                        }
                        $http({
                            url: '/epds/user/check-if-user-exists/',
                            method: 'POST',
                            headers: {
                                'Accept': 'application/json',
                                'Content-Type': 'application/json'
                            },
                            data: angular.toJson(params),
                            ignoreLoadingBar: true
                        })
                        .then(function (response) {
                            var data = response.data;
                            if (data.message == "Y") {
                                def.resolve();
                            } else {
                                def.reject();
                            }

                        });

                    } else {
                        return def.reject();
                    }

                }

            }
        };
    }]);


angular.module('epdsApp.auth').controller('rulesOfBehaviorModalCtrl', rulesOfBehaviorModalCtrl);
rulesOfBehaviorModalCtrl.$inject = ['$scope', '$uibModalStack',
    '$uibModal', '$uibModalInstance', 'authenticationService', 'email']

function rulesOfBehaviorModalCtrl($scope, $uibModalStack,
                                  $uibModal, $uibModalInstance, authenticationService, email) {


    $scope.OK = function () {

        authenticationService.submitRulesOfBehavior(email).then(function (data) {
            $uibModalInstance.close(data);
        });

    }


    $scope.cancel = function () {
        var data = {};

        data.isSuccess = false;
        $uibModalInstance.close(data);

    }

}
