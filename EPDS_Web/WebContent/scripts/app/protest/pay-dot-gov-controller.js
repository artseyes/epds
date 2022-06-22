angular.module('epdsApp.dashboard').controller('payDotGovController',
		PayDotGovCtrl);

PayDotGovCtrl.$inject = [ '$scope', '$location', '$http', '$routeParams',
		'modalService', 'localStorageService', 'protestDataSvc',
		'actionMessageSvc', '$cookies' ];

function PayDotGovCtrl($scope, $location, $http, $routeParams, modalService,
		localStorageService, protestDataSvc, actionMessageSvc, $cookies) {

	

	$scope.displayPayPending = true;

	/*if ($routeParams.token || localStorageService.get("payDotGovToken")) {}else {
		$scope.message = localStorageService.get("response");
	}*/

	var token, aNum;

	if (localStorageService.isSupported) {
		token = localStorageService.get("payDotGovToken");
		aNum = localStorageService.get("newProtestANum");
		localStorageService.set("userLoggedIn", new Date());
	} else {
		token = $cookies.get("payDotGovToken");
		aNum = $cookies.get("newProtestANum");
	}

	$http(
			{
				url : '/epds/check-transaction-status',
				method : 'GET',
				headers : {
					'Content-Type' : 'application/json'
				},
				params : {
					token : $routeParams.token || token || null,
					a_Num : aNum || null
				}
			})
			.then(
					function(data) {

						var response = data && data.data;
						var genericErrorMess = "There was an error checking transaction status. Please don't file the protest again. Instead contact PLCG at protests@cbca.gov. ";
						var isPaymentSuccess = response
								&& response.isPaymentSuccess;

						if (response) {
							$scope.displayPayPending = false;
						}

						if (isPaymentSuccess === 'Y') {

							var customModalOptions = {
								headerText : 'Payment Success',
								bodyText : 'Your payment to pay.gov has been received.  Your payment tracking id is: '
										+ response.payDotGovTrackingId,
								closeButtonText : 'OK',
								messageType : "paymentSuccess"
							};

							modalService.showModal({}, customModalOptions);

						} else if (response && response.payDotGovError) { 
							
							
							var customModalOptions = {
									headerText : 'Payment Failed',
									bodyText : response.payDotGovError && response.payDotGovError.errorDetail || genericErrorMess,
									closeButtonText : 'OK',
									messageType : "paymentFailure"
								};

							modalService.showModal({}, customModalOptions);
							
						}else {

							var bodyText = genericErrorMess;

							if (response && response.payDotGovTrackingId) {
								bodyText += " Your payment tracking id is: "
										+ response.payDotGovTrackingId;
							}
							var customModalOptions = {
								headerText : 'Payment Failed',
								bodyText : bodyText,
								closeButtonText : 'OK',
								messageType : "paymentFailure"
							};

							modalService.showModal({}, customModalOptions);

						}

					},
					function(error) {

						var customModalOptions = {
							headerText : 'Payment Error',
							bodyText : "There was an error checking transaction status. Please don't file the protest again. Instead contact PLCG at protests@cbca.gov. ",
							closeButtonText : 'OK',
							messageType : "paymentFailure"
						};

						modalService.showModal({}, customModalOptions);

						return error;
					});

};

