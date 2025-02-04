angular.module('epdsApp.auth').controller('navigationController', navigationController);

navigationController.$inject = ['$scope', '$rootScope', 'authenticationService', 'authLoginActionsSvc',
    'vcRecaptchaService', 'recaptchaConfig', 'actionMessageSvc', '$ocLazyLoad', '$location', 'Idle',
    'localStorageService', '$injector', 'regEx', 'toolTip', 'cfpLoadingBar', 'emailService']

/* @ngInject */
function navigationController($scope, $rootScope, authenticationService, authLoginActionsSvc, recaptcha,
                              recaptchaConfig, actionMessageSvc, $ocLazyLoad, $location, Idle, localStorageService,
                              $injector, regEx, toolTip, cfpLoadingBar, emailService) {
    $scope.customFormOptions = {
        validationsTemplate: 'scripts/app/registration/register-form-validations.html',
        preventInvalidSubmit: true,
        preventDoubleSubmit: true,
        setFormDirtyOnSubmit: true,
        scrollToAndFocusFirstErrorOnSubmit: true,
        scrollAnimationTime: 900,
        scrollOffset: -100,
    };

    if (window.location.host == "epds.cbca.gov") {
        $rootScope.isProd = true;
    }

    $scope.regEx = regEx;
    $scope.toolTip = toolTip;

    $scope.recaptchaConfig = recaptchaConfig;
    $rootScope.authenticated = false;
    $scope.credentials = {
        userId: null,
        userPwd: null
    }
    $scope.userPwd = '';

    $scope.setEmailPreferences = function (protestInfo) {
        $ocLazyLoad.load(['jquery-datatables', 'angular-datatables', 'dashboard', 'account-update',
            'request-to-intervene', 'angular-xeditable', 'cds', 'admin-cds'], {
            serie: true,
            cache: true
        })
        .then(function () {
            var caseDocketDataSvc = $injector.get("caseDocketDataSvc");
            caseDocketDataSvc.setCaseDocketEmailPreferences(protestInfo);
        });
    }

    $scope.joinUnJoinCases = function (protestInfo) {
        var vm = {},
            caseDocketDataSvc = $injector.get("caseDocketDataSvc");
        vm.protestInfo = protestInfo;

        $ocLazyLoad.load(
            ['jquery-datatables', 'angular-datatables', 'dashboard',
                'account-update', 'request-to-intervene',
                'angular-xeditable', 'cds', 'admin-cds'], {
                serie: true,
                cache: false
            }).then(function () {
            caseDocketDataSvc.join_UnjoinCasesDialogueBox(vm);
        });

    }

    /*$scope.key = function(event) {

        var key = event


         * if( key == 8 || key == 46 ) return false;


    }*/

    $scope.userGuide = function (guideType) {
        authenticationService.downloadUserGuides(guideType);
    }

    $scope.sendNotification = function () {
        $ocLazyLoad.load(
            ['account-update', 'request-to-intervene',
                'angular-xeditable', 'cds', 'admin-cds', 'ck-editor'], {
                serie: true,
                cache: true
            }).then(function () {
            emailService.generateTemplateModal();
        });
    }

    $scope.removeCase = function () {
        var dashboardDataSvc = $injector
        .get("dashboardDataService");

        dashboardDataSvc.removeCaseModal();
    }

    $scope.completeOption = function (protestInfo) {
        $ocLazyLoad.load(['jquery-datatables', 'angular-datatables', 'dashboard', 'account-update',
            'request-to-intervene', 'angular-xeditable', 'cds', 'admin-cds'], {
            serie: true,
            cache: true
        }).then(function () {
            var caseDocketDataSvc = $injector.get("caseDocketDataSvc");

            // Zip file needs to be created
            if (protestInfo.caseCompletionStatus.isZipCreated === false) {
                caseDocketDataSvc.complete(protestInfo);
                // enter DM#
            } else if (protestInfo.caseCompletionStatus.isZipCreated === true
                && protestInfo.caseCompletionStatus.isDmEntered === false) {
                protestInfo.headerText = "Enter DM #"
                caseDocketDataSvc.enterDM(protestInfo);
                // pending DM
            } else if (protestInfo.caseCompletionStatus.isZipCreated === true
                && protestInfo.caseCompletionStatus.isDmEntered === true
                && protestInfo.caseCompletionStatus.isDmVerfied == null) {
                caseDocketDataSvc
                .pendingVerification(protestInfo);
                // verify DM
            } else if (protestInfo.caseCompletionStatus.isZipCreated === true
                && protestInfo.caseCompletionStatus.isDmEntered === true
                && protestInfo.caseCompletionStatus.isDmVerfied === false) {
                protestInfo.headerText = "Verify DM #"

                caseDocketDataSvc.enterDM(protestInfo);
            }
        });

    }

    $scope.fileNewProtest = function () {
        $rootScope.$broadcast('fileNewProtestEvent');
    }

    $scope.contactUs = function () {
        var customAttr = {
            headerText: "Contact Us",
            bodyText: "If you have questions or Section 508 needs, please contact CBCA at 202-606-8800 or cbcaclerk@cbca.gov.",
            modalType: "info",
            actionType: "",
            cancelBtnReq: "N",
            cancelBtnActionType: ""
        }
        actionMessageSvc.showModal(customAttr);
    }

    $scope.feedBack = function () {
        var bodyText = "";
        bodyText += "<p>To provide your feedback on how we can improve EDS, " +
            "please email us at <a href=\"mailto:cbca.eds@cbca.gov\" target=\"_top\">" +
            "cbca.eds@cbca.gov<\/a>.<strong>  Do not use this email account " +
            "to communicate with CBCA regarding  pending Filings.<strong><\/p>";

        var customAttr = {
            headerText: "Send Feedback",
            bodyText: bodyText,
            modalType: "info",
            actionType: "",
            cancelBtnReq: "N",
            cancelBtnActionType: ""
        }

        actionMessageSvc.showModal(customAttr);
    }

    $scope.navClass = function (url) {
        var path = $location.path();
        if (path.indexOf(url) > -1) {
            return "active";
        } else {
            return "";
        }

    }

    $scope.$watch('credentials.userPwd', function () {
        if ($scope.credentials && $scope.credentials.userPwd) {
            $scope.credentials.userPwd = $scope.credentials.userPwd.trim();
        }
    });

    $scope.onLogin = function (credentials) {
        if (!!credentials.userId === false || !!credentials.userPwd === false) return;

        authenticationService.login(credentials)
        .then(function (response) {
            authLoginActionsSvc.redirectUser(response);
        });
    }

    $scope.resetAccount = function (activityType) {
        authenticationService.resetAccount(activityType);
    }

    if ($rootScope.sessionExpired) {
        $scope.redirectMessage = $rootScope.redirectMessage;
    }

    $scope.setWidgetId = function (widgetId) {
        recaptcha.reload($scope.widgetId);
        console.info('Created widget ID: %s', widgetId);
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

        console.info('Captcha expired. Resetting response object');

        recaptcha.reload($scope.widgetId);
        $scope.isResponseValid = null;
    };


    $scope.$on('IdleStart', function () {
        console.log("Idle Start ----------->" + new Date())
    });

    $scope.$on('IdleEnd', function () {
        console.log("Idle End ----------->" + new Date())
    });

    $scope.$on('IdleWarn', function (e, countdown) {
        console.log("IdleWarn ----------->" + new Date())
    });

    $scope.$on('InputResponseError', function (event, response) {
        if ('undefined' !== typeof response.response.inputErrors
            && null !== response.response.inputErrors
            && !$rootScope.showInputError) {

            $rootScope.showInputError = true;

            var customAttr = {
                headerText: "Error",
                bodyText: "",
                modalType: "error",
                actionType: "",
                cancelBtnReq: "N",
                cancelBtnActionType: "",
                inputErrorMessages: response.response.inputErrors
            }

            actionMessageSvc.showModal(customAttr);
        } else {
            $rootScope.showInputError = false;
        }

        /*if ('undefined' !== typeof response.response.sessionValid
                && null !== response.response.sessionValid){
            localStorageService.cookie.set("sessionValid", "Y");
            localStorageService.set("userLoggedIn", new Date());
        }*/

    });

    var idleWatch = false;

    var httpReqEventListener = $scope.$on('httpReqEvent', function () {
        if (!localStorageService.get("userLoggedIn")) {
            idleWatch = false;
            /*localStorageService.cookie.remove("sessionValid");*/
            Idle.unwatch();
        } else if (localStorageService.get("userLoggedIn")) {
            /*localStorageService.cookie.set("sessionValid", "Y");*/

            if (!idleWatch
                && !($location.url() == "/login")
                && ($location.url().indexOf("register") <= 0)
                && !($location.url() == "/forgot-password")) {

                idleWatch = true;
                Idle.watch();
            }
        }
    });

    $scope.$on('Keepalive', function () {
        console.log("Keepalive ----------->" + new Date())
    });

    var fourZeroOneEventListener = $scope.$on('401', function (event, data) {
        if (data && data.data.error === "concurrentLogin") {
            $rootScope.authenticated = false;
            $rootScope.sessionExpired = true;
            $rootScope.redirectMessage = "You have been logged out because your account has been used to login to EDS on a different device or browser."
            $location.path("/").replace();
        } else if (data &&
            (data.data.error === "authentication object not found"
                || data.data.error === "token invalid"
                || data.data.error === "tokenForFreshConcurrentSession is null")) {
            $rootScope.authenticated = false;
            $rootScope.sessionExpired = true;
            $rootScope.redirectMessage = "Your session has expired.  Please login to continue."

            $location.path("/").replace();

        } else if (data && data.data.error === "unauthorized network login") {
            $rootScope.authenticated = false;
            $rootScope.sessionExpired = true;
            $rootScope.redirectMessage = "You're not authorized to login from this network."
            $location.path("/").replace();

        }

        if (localStorageService.get("userLoggedIn")) {

            /*localStorageService.cookie.remove("sessionValid");*/
            localStorageService.remove("userLoggedIn");
            Idle.unwatch();
        }

    });


    if ($location.url() == "/login") {
        $ocLazyLoad.load(['jquery-datatables', 'angular-datatables', 'dashboard', 'account-update',
            'angular-xeditable', 'cds', 'file-info-view'], {serie: true, cache: true})
        Idle.unwatch();
    }

    var customEventListener = $rootScope.$on('fileNewProtestEvent', function () {
        var bodyText = "";

        if (!!$rootScope.userProfileInfo === false) {
            return;
        }

        if ($rootScope.userProfileInfo.role_id == "7") {
            $location.path("/protest-request");

            return;
        }

        var bodyText = "";

        $ocLazyLoad.load(['jquery-datatables', 'angular-datatables', 'dashboard', 'agency', 'protest',
            'account-update'], {
            serie: true,
            cache: true
        }).then(function () {
            var protestDataSvc = $injector.get("protestDataSvc");
            protestDataSvc.testPayDotGov().then(function (response) {
                if (response.isSuccess) {
                    bodyText += "<p>Before filing a document, you should carefully review the CBCA's filing requirements and rules on the CBCA website. "
                        + "The CBCA logo at the top of the screen will redirect you to the CBCA website. "
                        + "On our website, you should first select the Filings and Proceedings tab and then select the Filing Documents tab and the Rules tab. "
                        + "These tabs contain important information regarding what is necessary to include in your filing as well as what filings the CBCA will not accept.";

/*                    bodyText += "<p>You are strongly encouraged to review the following sections for important information: </p>";

                    bodyText += "<ul>";
                    bodyText += "  <li>&#x00a7; 21.1 Filing a case;<\/li>";
                    bodyText += "  <li>&#x00a7; 21.2 Time for filing; and<\/li>";
                    bodyText += "  <li>&#x00a7; 21.5 Protest issues not for consideration.<\/li>";
                    bodyText += "<\/ul>  ";

                    bodyText += "<p>These sections include important information regarding what is necessary to include in your protest and what protests our Office will not consider. "
                        + "No refunds of the filing fee will be made in the event a protest is dismissed for failing to comply with or  "
                        + "otherwise does not meet the requirements set forth in our Bid Protest Regulations. <\/p>";
                */
                    bodyText += "<p>DO NOT FILE IN EDS: <\/p>";

                    bodyText += "1. Documents that contain classified information<br>";
                    bodyText += "2. Documents to be submitted in camera<br>";
                    bodyText += "<p>3. Documents subject to a protective order </p>";

/*
                    bodyText += "<p>NO CLASSIFIED INFORMATION OR INFORMATION TO BE FILED IN CAMERA OR SUBJECT TO A PROTECTIVE ORDER SHOULD BE FILED IN EDS.<br>"
                        + "For guidance on filing including classified material, please go to: <a href='https://cbca.gov/howto/index.html'>How to File</a> </p>";
*/
                    bodyText += "<p>Do you want to proceed to file a new filing ?</p>";

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
                            $location.path("/protest-request");
                        }
                    })

                } else {
                    // bodyText += "<div>";
                    // bodyText += "    GAO endeavors to maintain the availability of EPDS during normal business hours, which are Monday through Friday, 8:00 a.m. to 5:30 p.m. eastern time,";
                    // bodyText += "    excluding Federal holidays or when GAO’s Headquarters are otherwise closed. While EPDS is currently available, the Pay.Gov site which processes the";
                    // bodyText += "    required filing fee for new protests is currently unavailable. If you are unable to file a new protest in EPDS during normal business hours, please refer";
                    // bodyText += "    to the EPDS instructions available here: <a href=\"http:\/\/cbca.gov\/legal\/bid-protests\/file-a-bid-protest\">http://cbca.gov/legal/bid-protests/file-a-bid-protest<\/a>.";
                    // bodyText += "    If you are unable to file a new protest in EPDS during a period other than normal business hours, please attempt";
                    // bodyText += "    to file the protest during the next period of normal business hours. All filings other than new protests must still be made in EPDS.";
                    // bodyText += "<\/div>";
                    // bodyText += "";
                    //
                    // var customAttr = {
                    //     headerText: "Warning",
                    //     bodyText: bodyText,
                    //     modalType: "error",
                    //     actionType: "samepage",
                    //     cancelBtnReq: "N",
                    //     cancelBtnActionType: "samepage",
                    //     okAndCancelText: "Y",
                    //     okBtnText: "OK",
                    //     cancelBtnText: "No"
                    // }
                    //
                    // actionMessageSvc.showModal(customAttr);
                }
            });

        });


    });


    $scope.$on('$destroy', function () {
        customEventListener();
        httpReqEventListener();
        fourZeroOneEventListener()
    });

    $scope.onRegister = function () {
        authenticationService.register();
    }

    $scope.forgotPassword = function () {
        authenticationService.forgotPassword();
    }
    $scope.onLogout = function () {
        authenticationService.logout();
    }

}
