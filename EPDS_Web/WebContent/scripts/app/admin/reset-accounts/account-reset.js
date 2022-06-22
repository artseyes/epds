angular.module('epdsApp.adminDashboard').controller('accountResetCtrl',
    accountResetCtrl);

accountResetCtrl.$inject = ['$scope', '$uibModal']

/* @ngInject */
function accountResetCtrl($scope, $uibModal, activityType) {

    $uibModal.open({
        templateUrl: 'scripts/app/admin/reset-accounts/accountReset.tpl.htm',
        controller: accountResetCtrlModalCtrl,
        animation: true,
        size: 'md',
        resolve: {
            userInfo: function () {
                return null;
            }, activityType: function () {
                return null;
            },
        },
        keyboard: false,
        backdrop: true
    }).result.catch(angular.noop);

}

/* @ngInject */
angular.module('epdsApp.adminDashboard').controller(
    'accountResetCtrlModalCtrl', accountResetCtrlModalCtrl);
accountResetCtrlModalCtrl.$inject = ['$http', '$scope', '$rootScope',
    '$uibModalStack', 'modalService', '$uibModal', '$uibModalInstance',
    '$location', 'accountResetService', 'userInfo', 'Idle', 'regEx',
    'toolTip', 'activityType']

function accountResetCtrlModalCtrl($http, $scope, $rootScope, $uibModalStack,
                                   modalService, $uibModal, $uibModalInstance, $location,
                                   accountResetService, userInfo, Idle, regEx, toolTip, activityType) {


    /* Idle.watch(); */
    $scope.userInfo = userInfo;
    if (activityType == 'delete') {
        $scope.modalTitle = "Account Delete";
        $scope.btnLabel = "Delete";
        $scope.modalText = "delete";
    } else {
        $scope.modalTitle = "Account Reset";
        $scope.btnLabel = "Reset";
        $scope.modalText = "reset";
    }

    if (!!$rootScope.userProfileInfo === false) {
        return;
    }

    if ($rootScope.userProfileInfo.role_id != "7") {
        $location.path("/dashboard").replace();
    }

    $scope.reset = function (email) {

        accountResetService
        .getUserInfo(email)
        .then(
            function (response) {

                $scope.userInfo = response.data;

                $uibModal
                .open({
                    templateUrl: 'scripts/app/admin/reset-accounts/info-confirmation.tpl.htm',
                    controller: accountResetCtrlModalCtrl,
                    animation: true,
                    size: 'md',
                    resolve: {
                        userInfo: function () {
                            return response.data;
                        },
                        activityType: function () {
                            return activityType;
                        },
                    },
                    keyboard: false,
                    backdrop: true
                }).result.catch(angular.noop);
            })

    }

    $scope.ok = function (email) {
        $uibModalStack.dismissAll();
    }

    $scope.confirm = function (userId) {
        var promise;

        if (activityType == 'reset') {
            accountResetService.resetAccount(userId)
            .then(
                function (response) {
                    if (response.isSuccess) {
                        $uibModalInstance.dismiss('cancel');
                        $uibModal
                        .open({
                            templateUrl: 'scripts/app/admin/reset-accounts/feedbackMessages.tpl.htm',
                            controller: accountResetCtrlModalCtrl,
                            animation: true,
                            size: 'md',
                            resolve: {
                                userInfo: function () {
                                    return null;
                                },
                                activityType: function () {
                                    return null;
                                },
                            },
                            keyboard: false,
                            backdrop: true
                        }).result.catch(angular.noop);
                    }
                });
        }

        if (activityType == 'delete') {
            accountResetService.deleteAccount(userId);
        }


    }

    $scope.cancel = function (val) {
        $uibModalInstance.dismiss('cancel');
    }

}

(function () {
    'use strict';

    var serviceId = 'accountResetService';

    angular.module('epdsApp.adminDashboard')
    .factory(
        serviceId,
        ['$http', 'base64', '$uibModal', '$rootScope', '$httpParamSerializerJQLike', 'manageAttorneyInfoSvc',
            accountResetService]);

    /* @ngInject */
    function accountResetService($http, base64, $uibModal, $rootScope, $httpParamSerializerJQLike, manageAttorneyInfoSvc) {

        var service = {
            getUserInfo: getUserInfo,
            resetAccount: resetAccount,
            resetAccountModal: resetAccountModal,
            resetAgencyAccount: resetAgencyAccount,
            deleteAccount: deleteAccount
        };

        return service;

        function getUserInfo(email) {

            return $http({
                url: '/epds/user/get-user-info/' + base64.urlencode(email),
                method: 'GET',
                headers: {
                    'Content-Type': 'application/json'
                },
            }).then(function (data) {
                return data.data;

            }, function (error) {

                return error;
            });

        }

        function resetAccount(userId) {

            return $http({
                url: '/epds/user/reset-account/' + userId,
                method: 'GET',
                headers: {
                    'Content-Type': 'application/json'
                },
            }).then(function (data) {

                return data.data;
            }, function (error) {

                return error;
            });
        }

        function deleteAccount(userId) {

            return manageAttorneyInfoSvc.deleteUser(userId, 'N')
        }

        function resetAgencyAccount(form) {

            var params = form;

            return $http({
                url: '/epds/reset-agency-accounts',
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded'
                },
                data: $httpParamSerializerJQLike(_.omitBy(params, _.isNil)),
            }).then(function (data) {

                return data.data;

            }, function (error) {

                return error;
            });
        }

        function resetAccountModal(activityType) {
            return $http({
                url: '/epds/manage-account-reset',
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
            })
            .then(function (data) {
				if (data.data.authorized) {
					$rootScope.authenticated = true;
					$uibModal.open({
						templateUrl: 'scripts/app/admin/reset-accounts/accountReset.tpl.htm',
						controller: accountResetCtrlModalCtrl,
						animation: true,
						size: 'md',
						resolve: {
							userInfo: function () {
								return null;
							},
							activityType: function () {
								return activityType;
							},
						},
						keyboard: true,
						backdrop: 'static',
					}).result.catch(angular.noop);

				} else {
					$rootScope.authenticated = false;
					$rootScope.sessionExpired = true;
					$rootScope.redirectMessage = "You are not authorized"
					$location.path("/").replace();
				}

				return data.data;
			}, function (error) {

				return error;
			});

        }
    }
})();
