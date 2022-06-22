
emailNotificationModalInstanceCtrl.$inject = [ '$scope', '$rootScope',
		'$uibModalInstance', 'emailService', 'actionMessageSvc' ];

function emailNotificationModalInstanceCtrl($scope, $rootScope,
		$uibModalInstance, emailService, actionMessageSvc) {

	$scope.ok = function(subject, emailBody) {

		var bodyText = "";

		if (!subject) {
			bodyText += "Please enter Email subject";
		}

		if (!emailBody) {
			bodyText += "Email body is empty";
		}

		var customAttr = {
			headerText : "",
			bodyText : bodyText,
			modalType : "error",
			actionType : "samepage",
			cancelBtnReq : "N",
			okBtnText : "OK",
		}

		if (!subject || !emailBody) {
			actionMessageSvc.showModal(customAttr);
		}

		var form = {
			subject : subject,
			emailBody : emailBody
		}

		emailService.sendNotification(form).then(function() {
			$uibModalInstance.close(form);
		});
	};

	$scope.cancel = function(htmlContent) {

		$uibModalInstance.close(htmlContent);
	};

}

(function() {
	'use strict';

	var serviceId = 'emailService';

	angular.module('epdsApp.core').factory(
			serviceId,
			[ '$rootScope', '$http', '$filter', '$uibModal', '$uibModalStack',
					'$httpParamSerializerJQLike', 'actionMessageSvc', '$q',
					'Idle', emailService ]);
	
	function emailService($rootScope, $http, $filter, $uibModal,
			$uibModalStack, $httpParamSerializerJQLike, actionMessageSvc, $q,
			Idle) {

		var service = {
			generateTemplateModal : generateTemplateModal,
			sendNotification : sendNotification,

		};

		return service;

		function generateTemplateModal() {

			Idle.watch();

			var modalInstance = $uibModal
					.open({
						templateUrl : 'scripts/app/core/email-notifications/notification.htm?bust='
								+ Math.random().toString(36).slice(2),
						controller : emailNotificationModalInstanceCtrl,
						size : 'lg',
						keyboard : true,
						backdrop : 'static',
					/*resolve : {
						htmlContent : function() {
							return htmlContent;
						},
					}*/
					}).result.catch(angular.noop);

			return modalInstance.result;
		}

		function sendNotification(formParams) {

			return $http({
				url : '/epds/notification',
				method : 'POST',
				headers : {
					'Content-Type' : 'application/x-www-form-urlencoded'
				},
				data : $httpParamSerializerJQLike(_.omitBy(formParams, _.isNil)),
			}).then(function(data) {

			}, function(error) {

				return error;
			});

		}

	}
})();
