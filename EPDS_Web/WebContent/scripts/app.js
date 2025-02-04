var epdsApp = angular.module('epdsApp',
    ['epdsApp.auth', 'oc.lazyLoad', 'ngCookies',
        'angular-loading-bar', 'ngMeta', 'angularMoment', 'cfp.loadingBar', 'csrf-token-interceptor', 'ngAria']);


epdsApp.config(['$compileProvider', function ($compileProvider) {
    $compileProvider.aHrefSanitizationWhitelist(/^\s*(https?|ftp|mailto|file|blob|javascript):/);
}])

epdsApp.constant('angularMomentConfig', {
    timezone: 'America/New_York'
});

epdsApp.factory('beforeUnload',
    ['$window', '$rootScope', function ($window, $rootScope) {
        $window.onbeforeunload = function (e) {
            var confirmation = {};
            var event = $rootScope.$broadcast('onBeforeUnload', confirmation);
            if (event.defaultPrevented) {
                return confirmation.message;
            }
        };

        $window.onunload = function () {
            $rootScope.$broadcast('onUnload');
        };

        return {};
    }]);

epdsApp.run(function (beforeUnload) {
});


// safe Apply
/* @ngInject */
epdsApp.run(function ($rootScope, $anchorScroll, $window, $uibModalStack, $route, $location) {
    $rootScope.setTitle = function (title) {
        document.title = title + " | EDS";
    }

    $rootScope.$safeApply = function () {
        var $scope, fn, force = false;
        if (arguments.length == 1) {
            var arg = arguments[0];
            if (typeof arg == 'function') {
                fn = arg;
            } else {
                $scope = arg;
            }
        } else {
            $scope = arguments[0];
            fn = arguments[1];
            if (arguments.length == 3) {
                force = !!arguments[2];
            }
        }
        $scope = $scope || this;
        fn = fn || function () {
        };
        if (force || !$scope.$$phase) {
            $scope.$apply ? $scope.$apply(fn) : $scope.apply(fn);
        } else {
            fn();
        }
    };

    $("#mainContainer").show();
    var wrap = function (method) {
        var orig = $window.window.history[method];
        $window.window.history[method] = function () {
            var retval = orig.apply(this, Array.prototype.slice.call(arguments));
            $anchorScroll();
            return retval;
        };
    };

    wrap('pushState');
    wrap('replaceState');

    $rootScope.$on("$routeChangeSuccess", function (event, currentRoute, previousRoute) {
        if ($rootScope.keepModal === true) {
            $rootScope.keepModal = false;
        } else {
            $uibModalStack.dismissAll();
        }

        window.scrollTo(0, 0);

        $rootScope.setTitle($route.current.title);
    });

    $rootScope.$on('cfpLoadingBar:loading', function () {
        $rootScope.loading = true;
    });

    $rootScope.$on('cfpLoadingBar:completed', function () {
        $rootScope.loading = false;
    });

});


/* @ngInject */
epdsApp.directive('numbersOnly', function () {
    return {
        require: 'ngModel',
        /* @ngInject */
        link: function (scope, element, attr, ngModelCtrl) {
            function fromUser(text) {
                if (text) {
                    var transformedInput = text && text.replace(/[^0-9]/g, '');
                    if (transformedInput !== text) {
                        ngModelCtrl.$setViewValue(transformedInput);
                        ngModelCtrl.$render();
                    }
                    return transformedInput;
                }
                return undefined;
            }

            ngModelCtrl.$parsers.push(fromUser);
        }
    };
});
/* @ngInject */
epdsApp.directive('dynamic', function ($compile) {
    return {
        restrict: 'A',
        replace: true,
        /* @ngInject */
        link: function (scope, ele, attrs) {
            scope.$watch(attrs.dynamic, function (html) {
                ele.html(html);
                $compile(ele.contents())(scope);
            });
        }
    };
});

/* @ngInject */
epdsApp.factory('randomString', ['$window', function randomStringFactory(w) {
        var chars = '0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz';
        var Math = w.Math;

        return function randomString(length) {
            length = length || 10;
            var string = '', rnd;
            while (length > 0) {
                rnd = Math.floor(Math.random() * chars.length);
                string += chars.charAt(rnd);
                length--;
            }
            return string;
        };
    }
    ]
);


// in dashboard we are having a problem where the tooltip is not placed in the  right place
// this is a fix for that issue.
/* @ngInject */
epdsApp.directive('popup', function () {
    return {
        restrict: 'A',
        scope: {
            popUpContent: '@?',
            options: '=popup'
        },
        /* @ngInject */
        link: function (scope, element, attrs) {
            /*scope.$watch('ngModel', function(val) {
                element.attr('data-content', val);
            });*/

            element.attr('data-content', attrs.popUpContent);

            var options = scope.options || {};

            var title = options.title || null;
            var placement = options.placement || 'right';
            var html = options.html || false;
            var delay = options.delay ? angular.toJson(options.delay) : null;
            var trigger = options.trigger || 'hover';

            element.attr('title', title);
            element.attr('data-placement', placement);
            element.attr('data-html', html);
            element.attr('data-delay', delay);
            element.popover({
                trigger: trigger
            });
        }
    };
});


//Set the default for all the http requests.
/* @ngInject */
epdsApp.config(['$httpProvider', function ($httpProvider) {
    delete $httpProvider.defaults.headers.common['X-Requested-With'];
    $httpProvider.defaults.headers.post['Accept'] = 'application/json, text/javascript';
    $httpProvider.defaults.headers.post['Content-Type'] = 'application/json; charset=utf-8';
    $httpProvider.defaults.headers.post['Access-Control-Max-Age'] = '1728000';
    $httpProvider.defaults.headers.common['Access-Control-Max-Age'] = '1728000';
    $httpProvider.defaults.headers.common['Accept'] = 'application/json, text/javascript';
    $httpProvider.defaults.headers.common['Content-Type'] = 'application/json; charset=utf-8';
    $httpProvider.defaults.useXDomain = true;

    //initialize get if not there
    if (!$httpProvider.defaults.headers.get) {
        $httpProvider.defaults.headers.get = {};
    }
    //disable IE ajax request caching
    $httpProvider.defaults.headers.get['If-Modified-Since'] = '0';

    // extra
    $httpProvider.defaults.headers.get['Cache-Control'] = 'no-cache';
    $httpProvider.defaults.headers.get['Pragma'] = 'no-cache';

}]);

/* @ngInject */
epdsApp.directive('backSpaceNotBackButton', [function () {
    return {
        restrict: 'A',
        /* @ngInject */
        link: function (scope, element, attrs) {
            // This will stop backspace from acting like the back button
            $(element).on("keydown", function (e) {
                var elid = $(document.activeElement)
                .filter(
                    "input:not([type], [readonly])," +
                    "input[type=text]:not([readonly]), " +
                    "input[type=password]:not([readonly]), " +
                    "input[type=search]:not([readonly]), " +
                    "input[type=number]:not([readonly]), " +
                    "input[type=email]:not([readonly]), " +
                    "input[type=date]:not([readonly]), " +
                    "input[type=datetime]:not([readonly]), " +
                    "input[type=datetime-local]:not([readonly]), " +
                    "input[type=month]:not([readonly]), " +
                    "input[type=tel]:not([readonly]), " +
                    "input[type=time]:not([readonly]), " +
                    "input[type=url]:not([readonly]), " +
                    "input[type=week]:not([readonly]), " +
                    "textarea")[0];
                if (e.keyCode === 8 && !elid) {
                    return false;
                }
            });
        }
    }
}])


//need to comeback ...globally append app server URL.
/*epdsApp.config(function ($httpProvider) {
    $httpProvider.interceptors.push(function ($q) {
        return {
            'request': function (config) {
            	
            	if (config.url.indexOf('scripts') === -1 
            			|| config.url.indexOf('views') === -1 
            			|| config.url.indexOf('styles') === -1) {
			      }else{
			    	  
			      }
                return config || $q.when(config);

            }

        }
    });
});*/

/* @ngInject */
epdsApp.directive('uibModalWindow', function () {
    return {
        restrict: 'EA',
        /* @ngInject */
        link: function (scope, element) {
            $(".modal-dialog").draggable({
                /*containment:[-500,-127,0,0],*/
                handle: ".modal-header"
            });
        }
    }
});


/* @ngInject */
// epdsApp.run(function ($anchorScroll, $window, $rootScope, $uibModalStack, $location) {
//     $("#mainContainer").show();
//     var wrap = function (method) {
//         var orig = $window.window.history[method];
//         $window.window.history[method] = function () {
//             var retval = orig.apply(this, Array.prototype.slice.call(arguments));
//             $anchorScroll();
//             return retval;
//         };
//     };
//
//     wrap('pushState');
//     wrap('replaceState');
//
//     $rootScope.$on("$routeChangeSuccess", function (event, currentRoute, previousRoute) {
//         if ($rootScope.keepModal === true) {
//             $rootScope.keepModal = false;
//         } else {
//             $uibModalStack.dismissAll();
//         }
//
//         window.scrollTo(0, 0);
//
//         // if ($location.path().indexOf("login") >= 0
//         //     || $location.path().indexOf("register") >= 0
//         //     || $location.path().indexOf("user-guide") >= 0) {
//         //
//         //     $('.row-offcanvas').removeClass('active');
//         //     $('.left-side').removeClass("collapse-left");
//         //     $(".right-side").removeClass("strech");
//         //     $('.row-offcanvas').removeClass("relative");
//         //
//         // } else {
//         //
//         //     $('.row-offcanvas').toggleClass('active');
//         //     $('.left-side').removeClass("collapse-left");
//         //     $(".right-side").removeClass("strech");
//         //     $('.row-offcanvas').toggleClass("relative");
//         // }
//
//
//     });
// });

/* @ngInject */
epdsApp.config(['cfpLoadingBarProvider', function (cfpLoadingBarProvider) {
    /*cfpLoadingBarProvider.latencyThreshold = 500;*/
    cfpLoadingBarProvider.spinnerTemplate = '   <div class="loading">Loading&#8230;  ' +
        '   <div class="sk-cube-grid">  ' +
        '     <div class="sk-cube sk-cube1"></div>  ' +
        '     <div class="sk-cube sk-cube2"></div>  ' +
        '     <div class="sk-cube sk-cube3"></div>  ' +
        '     <div class="sk-cube sk-cube4"></div>  ' +
        '     <div class="sk-cube sk-cube5"></div>  ' +
        '     <div class="sk-cube sk-cube6"></div>  ' +
        '     <div class="sk-cube sk-cube7"></div>  ' +
        '     <div class="sk-cube sk-cube8"></div>  ' +
        '     <div class="sk-cube sk-cube9"></div>  ' +
        '   </div>  ' +
        '  </div>  ';
    /*cfpLoadingBarProvider.includeSpinner = true;*/

    cfpLoadingBarProvider.parentSelector = '#mainContainer';
}])

/* @ngInject */
epdsApp.config(function (csrfProvider) {
    csrfProvider.config({
        url: '/epds/',
        maxRetries: 1,
        csrfHttpType: 'get',
        csrfTokenHeader: 'X-XSRF-TOKEN',
        httpTypes: ['POST'] //CSRF token will be added only to these method types 
    });
})

/* @ngInject */
epdsApp.filter("sort", function ($filter) {
    return function (input) {
        var input = input || "";
        var array = input.split(';');
        array = array.sort(function (a, b) {
            if (a < b)
                return -1;
            if (a > b)
                return 1;
            return 0;
        });
        return $filter("join")(array, ";")

    };
});

/* @ngInject */
epdsApp
.config([
    '$routeProvider',
    '$locationProvider',
    function ($routeProvider, $locationProvider) {
        var version = "?bust=" + (new Date()).getTime();
        $routeProvider
        .when('/login', {
            title: 'Login',
            templateUrl: 'scripts/app/authentication/login-page.html',
            controller: 'navigationController',
            resolve: {
                /* @ngInject */
                checkRoute: ['$q', '$rootScope', '$location', '$http', function ($q, $rootScope, $location, $http) {
                    var deferred = $q.defer();

                    $http.get("/epds/").then(function (response) {
                        var data = response.data;
                        if (data.roleId && data.roleId == "7") {
                            $location.path("/admin-dashboard/unassigned").replace();
                        } else {
                            $location.path("/dashboard").replace();
                        }
                        deferred.resolve(true);
                    }).catch(function () {
                        //deferred.reject();
                        //$location.path("/login")
                        deferred.resolve(true);
                    });

                    return deferred.promise;

                }]
            }
        }).when('/user-guide', {
            title: 'User Guide',
            templateUrl: 'scripts/app/user-guides/protesterManual.htm',
            controller: 'UserGuideCtrl',
            resolve: {
                /* @ngInject */
                loadModule: ['$ocLazyLoad', function ($ocLazyLoad) {
                    return $ocLazyLoad.load(['user-guide'], {serie: true});
                }]
            }
        }).when('/register', {
            title: 'Register',
            templateUrl: 'scripts/app/core/blankPage.htm',
            controller: 'accountSelectionCtrl',
            resolve: {
                /* @ngInject */
                loadModule: ['$ocLazyLoad', function ($ocLazyLoad) {
                    return $ocLazyLoad.load(['agency', 'registeration'], {serie: true});
                }]
            }
        }).when('/forgot-password', {
            title: 'Forgot password',
            templateUrl: 'scripts/app/core/blankPage.htm',
            controller: 'resetPasswordCtrl'
        }).when('/register/:role', {
            title: 'Register',
            templateUrl: 'scripts/app/registration/register.html',
            controller: 'registrationCtrl',
            resolve: {
                /* @ngInject */
                loadModule: ['$ocLazyLoad', function ($ocLazyLoad) {
                    return $ocLazyLoad.load(['google-maps', 'international-phone-number', 'agency', 'registeration'], {serie: true});
                }]
            }
        }).when('/profile', {
            title: 'Profile',
            templateUrl: 'scripts/app/account-update/profile.html',
            controller: 'userProfileInfoController',
            resolve: {
                /* @ngInject */
                loadModule: ['$ocLazyLoad', function ($ocLazyLoad) {
                    return $ocLazyLoad.load(['google-maps', 'international-phone-number', 'jquery-datatables', 'angular-datatables', 'dashboard', 'agency', 'registeration', 'account-update'],
                        {serie: true, cache: true});
                }]
            }
        })
        .when('/dashboard', {
            title: 'Dashboard',
            templateUrl: 'scripts/app/dashboard/dashboard.html',
            controller: 'DashboardCtrl',
            controllerAs: 'dashboardInfo',
            resolve: {
                /* @ngInject */
                DashboardData: ['$ocLazyLoad', '$injector', function ($ocLazyLoad, $injector) {
                    return $ocLazyLoad.load(['jquery-datatables', 'angular-datatables', 'dashboard', 'account-update'],
                        {serie: true, cache: true})
                    .then(function () {
                        var dashboardDataService = $injector.get("dashboardDataService");
                        return dashboardDataService.getDashboard('userIdBased', 0, 100);
                    });
                }]
            },
        })
        .when('/case-docketsheet', {
            title: 'Case Docket Sheet',
            templateUrl: 'scripts/app/case-docket-sheet/case-docketsheet.html',
            controller: 'caseDocketSheetController',
            controllerAs: 'caseDocketInfo',
            resolve: {
                /* @ngInject */
                CaseDocketData: ['$ocLazyLoad', '$injector', function ($ocLazyLoad, $injector) {
                    return $ocLazyLoad.load(['jquery-datatables', 'angular-datatables',
                        'angular-xeditable', 'cds'], {serie: true, cache: true})
                    .then(function () {
                        var caseDocketDataSvc = $injector.get("caseDocketDataSvc");
                        return caseDocketDataSvc.getCaseDocketInfo();
                    });
                }]
            },
        })
        .when('/case-docketsheet/:a_No', {//for now keeping both with aNum and without aNum routes
            title: 'Case Docket Sheet',
            templateUrl: 'scripts/app/case-docket-sheet/case-docketsheet.html',
            controller: 'caseDocketSheetController',
            controllerAs: 'caseDocketInfo',
            resolve: {
                /* @ngInject */
                CaseDocketData: ['$ocLazyLoad', '$injector', '$route', function ($ocLazyLoad, $injector, $route) {
                    return $ocLazyLoad.load(['jquery-datatables', 'angular-datatables',
                        'angular-xeditable', 'cds'], {serie: true, cache: true})
                    .then(function () {
                        var caseDocketDataSvc = $injector.get("caseDocketDataSvc");
                        return caseDocketDataSvc.getCaseDocketInfo($route.current.params.a_No);
                    });
                }]
            },
        })
        /*.when('/case-docketsheet/:a_No', {
            templateUrl : 'scripts/app/core/blankPage.htm',
            controller: 'emailDocketAccessController',
        })*/
        .when('/submit-new-doc-form/:aNum', {
            title: 'Submit New Document',
            templateUrl: 'scripts/app/submit-new-docs/submit-new-doc-form.html',
            controller: 'SubmitNewDocCtrl',
            resolve: {
                /* @ngInject */
                SubmitNewDocData: ['$ocLazyLoad', '$injector', '$route', function ($ocLazyLoad, $injector, $route) {
                    return $ocLazyLoad.load(['jquery-datatables', 'angular-datatables', 'dashboard', 'angular-xeditable',
                        'cds', 'ck-editor', 'file-info-view', 'advance-search', 'request-to-intervene', 'submit-new-doc'],
                        {serie: true, cache: true})
                    .then(function () {
                        var submitNewDocDataSvc = $injector.get("submitNewDocDataSvc");
                        return submitNewDocDataSvc.loadSubmitNewDocPage($route.current.params.aNum);
                    });
                }]
            }
        })
        .when('/protest-request', {
            title:  'File New Case',
            templateUrl: 'scripts/app/protest/protest-filing-form.html',
            controller: 'ProtestCtrl',
            resolve: {
                /* @ngInject */
                loadModule: ['$ocLazyLoad', '$q', 'actionMessageSvc', '$location', function ($ocLazyLoad, $q, actionMessageSvc, $location) {
                    return $ocLazyLoad.load(['google-maps', 'international-phone-number', 'jquery-datatables', 'angular-datatables', 'dashboard', 'agency', 'protest'], {serie: true});
                }]
            },
        })
        .when('/intervene', {
            title: 'Request To Join',
            templateUrl: 'scripts/app/request-to-intervene/intervene-protest.html',
            controller: 'InterveneCtrl',
            resolve: {
                /* @ngInject */
                loadModule: ['$ocLazyLoad', function ($ocLazyLoad) {
                    return $ocLazyLoad.load(['google-maps', 'international-phone-number', 'jquery-datatables', 'angular-datatables',
                        'dashboard', 'angular-xeditable', 'cds', 'file-info-view', 'submit-new-doc', 'advance-search', 'request-to-intervene'], {serie: true});
                }]
            }
        })
        .when('/parties/:aNum', {
            title: 'Parties',
            templateUrl: 'scripts/app/parties/parties.html',
            controller: 'PartiesCtrl',
            resolve: {
                /* @ngInject */
                PartiesData: ['$ocLazyLoad', '$injector', '$route', '$location', function ($ocLazyLoad, $injector, $route, $location) {
                    return $ocLazyLoad.load(['google-maps', 'jquery-datatables', 'angular-datatables',
                        'dashboard', 'account-update', 'admin-dashboard', 'request-to-intervene', 'angular-xeditable',
                        'cds', 'agency', 'registeration', 'parties', 'manage-gao'],
                        {serie: true, cache: true})
                    .then(function () {
                        var partiesDataSvc = $injector.get("partiesDataSvc");
                        return partiesDataSvc.getListOfParties($route.current.params.aNum);
                    }, function (error) {
                        $location.path("/login");
                    });
                }]
            }
        })
        .when('/other-protest/:typeOfProtest/:aNum', {
            title: 'Other Protests',
            templateUrl: 'scripts/app/other-protests/other-protest-filing.html',
            controller: 'OtherProtestCtrl',
            resolve: {
                /* @ngInject */
                loadModule: ['$ocLazyLoad', '$q', '$route', function ($ocLazyLoad, $q, $route) {
                    return $ocLazyLoad.load(['jquery-datatables', 'angular-datatables', 'dashboard', 'protest', 'angular-xeditable', 'cds', 'file-info-view', 'other-protest'], {serie: true});
                }]
            },
        })
        .when('/casedocket-file-info-view/:aNum/:origSubmissionDate/:docId/:docketIndexNum', {
            title:  'Document View',
            templateUrl: 'scripts/app/case-docket-file-info/case-docket-file-info-view.html',
            controller: 'caseDocketFileInfoController',
            resolve: {
                /* @ngInject */
                loadModule: ['$ocLazyLoad', function ($ocLazyLoad) {
                    return $ocLazyLoad.load(['jquery-datatables', 'angular-datatables', 'dashboard', 'angular-xeditable', 'cds', 'ck-editor', 'file-info-view', 'advance-search', 'request-to-intervene', 'submit-new-doc'], {serie: true});
                }]
            }
        })
        .when('/advance-search/:reqType', {
            title:  'Advanced Search',
            templateUrl: 'scripts/app/advance-search/advanced-search.html',
            controller: 'advanceSearchCtrl',
            controllerAs: 'dashboardInfo',
            resolve: {
                /* @ngInject */
                loadModule: ['$ocLazyLoad', function ($ocLazyLoad) {
                    return $ocLazyLoad.load(['jquery-datatables', 'angular-datatables', 'dashboard', 'agency', 'advance-search', 'request-to-intervene'], {serie: true});
                }]
            }
        })
        .when('/report/:reqType', {
            title:  'Audit Reports',
            templateUrl: 'scripts/app/advance-search/reports.htm',
            controller: 'advanceSearchCtrl',
            controllerAs: 'dashboardInfo',
            resolve: {
                /* @ngInject */
                loadModule: ['$ocLazyLoad', function ($ocLazyLoad) {
                    return $ocLazyLoad.load(['jquery-datatables', 'angular-datatables', 'dashboard', 'agency', 'advance-search', 'request-to-intervene'], {serie: true});
                }]
            }
        })
        .when('/payment', {
            templateUrl: 'views/test/pay-gov.html',
            resolve: {
                /* @ngInject */
                loadModule: ['$ocLazyLoad', '$q', function ($ocLazyLoad, $q) {
                    return $ocLazyLoad.load(['jquery-datatables', 'angular-datatables', 'dashboard', 'protest'], {serie: true});
                }]
            },
        })
        .when('/payment-status', {
            templateUrl: 'scripts/app/core/blankPage.htm',
            controller: 'payDotGovController',
            resolve: {
                /* @ngInject */
                loadModule: ['$ocLazyLoad', '$q', function ($ocLazyLoad, $q) {
                    return $ocLazyLoad.load(['jquery-datatables', 'angular-datatables', 'dashboard', 'protest'], {serie: true});
                }]
            },
        })
        .when('/protectedDashboard', {
            templateUrl: 'scripts/app/core/blankPage.htm',
            controller: 'payDotGovController',
            resolve: {
                /* @ngInject */
                loadModule: ['$ocLazyLoad', '$q', function ($ocLazyLoad, $q) {
                    return $ocLazyLoad.load(['jquery-datatables', 'angular-datatables', 'dashboard', 'protest'], {serie: true});
                }]
            },
        })
        .when('/payDotGovTokenError', {
            templateUrl: 'scripts/app/core/blankPage.htm',
            controller: 'payDotGovController',
            resolve: {
                /* @ngInject */
                loadModule: ['$ocLazyLoad', '$q', function ($ocLazyLoad, $q) {
                    return $ocLazyLoad.load(['jquery-datatables', 'angular-datatables', 'dashboard', 'protest'], {serie: true});
                }]
            },
        })
        .when('/admin-dashboard/:viewType', {
            title: 'Dashboard',
            templateUrl: 'scripts/app/admin/dashboard/admin-dashboard.html',
            controller: 'AdminDashboardCtrl',
            controllerAs: 'dashboardInfo',
            resolve: {
                /* @ngInject */
                DashboardData: ['$ocLazyLoad', '$injector', '$route', '$location', function ($ocLazyLoad, $injector, $route, $location) {
                    return $ocLazyLoad.load(['jquery-datatables', 'angular-datatables', 'dashboard', 'account-update',
                        'admin-dashboard', 'angular-xeditable', 'cds', 'file-info-view'], {
                        serie: true,
                        cache: true
                    }).then(function () {
                        var dashboardDataService = $injector.get("dashboardDataService");
                        return dashboardDataService.getDashboard($route.current.params.viewType, 0, 50);
                    }, function (error) {
                        $location.path("/login");
                    });
                }]
            },
        })
        .when('/edit-template-documents', {
            title: 'Edit Templates',
            templateUrl: 'scripts/app/admin/edit-templates/admin-edit-template-documents.html',
            controller: 'adminEditTemplateDocumentsController',
            resolve: {
                /* @ngInject */
                loadModule: ['$ocLazyLoad', '$q', '$location', function ($ocLazyLoad, $q, $location) {
                    return $ocLazyLoad.load(['jquery-datatables', 'angular-datatables', 'dashboard', 'account-update', 'admin-dashboard', 'angular-xeditable',
                        'cds', 'ck-editor', 'file-info-view', 'submit-new-doc', 'edit-templates'], {
                        serie: true,
                        cache: true
                    })
                    .then(angular.noop, function (error) {
                            $location.path("/login");
                        }
                    );
                }]
            },
        })
        .when('/manage-agency-contacts', {
            title: 'Manage Agency Info',
            templateUrl: 'scripts/app/admin/manage-agency-info/manage-agency-info.html',
            controller: 'ManageAgencyCtrl',
            resolve: {
                /* @ngInject */
                loadModule: ['$ocLazyLoad', '$location', function ($ocLazyLoad, $location) {
                    return $ocLazyLoad.load(['google-maps', 'international-phone-number', 'jquery-datatables', 'angular-datatables', 'dashboard', 'account-update', 'admin-dashboard', 'agency', 'registeration', 'parties', 'angular-xeditable', 'cds', 'manage-gao', 'manage-agency'], {serie: true})
                    .then(function () {
                        }, function (error) {
                            $location.path("/login");
                        }
                    );
                }]
            }
        })
        .when('/manage-attorneys', {
            title: 'Manage Judge User Info',
            templateUrl: 'scripts/app/admin/manage-attorney-info/manage-attorneys-info.html',
            controller: 'manageAttorneysController',
            resolve: {
                /* @ngInject */
                loadModule: ['$ocLazyLoad', '$location', function ($ocLazyLoad, $location) {
                    return $ocLazyLoad.load(['google-maps', 'international-phone-number', 'jquery-datatables', 'angular-datatables',
                        'dashboard', 'account-update', 'admin-dashboard', 'angular-xeditable',
                        'cds', 'agency', 'registeration', 'parties', 'manage-gao'], {serie: true})
                    .then(function () {
                        }, function (error) {
                            $location.path("/login");
                        }
                    );
                }]
            }
        })
        .when('/admin-case-docketsheet', {
            title: 'Case Docket Sheet',
            templateUrl: 'scripts/app/admin/case-docket-sheet/admin-case-docketsheet.html',
            controller: 'adminCaseDocketSheetController',
            controllerAs: 'caseDocketInfo',
            resolve: {
                /* @ngInject */
                CaseDocketData: ['$ocLazyLoad', '$injector', '$location', function ($ocLazyLoad, $injector, $location) {
                    return $ocLazyLoad.load(['jquery-datatables', 'date-picker', 'angular-datatables', 'dashboard',
                        'account-update', 'request-to-intervene', 'agency',
                        'angular-xeditable', 'cds', 'admin-cds', 'file-info-view', 'submit-new-doc'], {serie: true})
                    .then(function () {
                        var caseDocketDataSvc = $injector.get("caseDocketDataSvc");
                        return caseDocketDataSvc.getCaseDocketInfo();
                    }, function (error) {
                        $location.path("/login");
                    });
                }]
            }
        })
        .when('/admin-case-docketsheet/:a_No',
            {
                title: 'Case Docket Sheet',
                templateUrl: 'scripts/app/admin/case-docket-sheet/admin-case-docketsheet.html',
                controller: 'adminCaseDocketSheetController',
                controllerAs: 'caseDocketInfo',
                resolve: {
                    /* @ngInject */
                    CaseDocketData: ['$ocLazyLoad', '$injector', '$route', '$location', function ($ocLazyLoad, $injector, $route, $location) {
                        return $ocLazyLoad.load(['jquery-datatables', 'date-picker', 'angular-datatables', 'dashboard',
                            'account-update', 'request-to-intervene', 'agency',
                            'angular-xeditable', 'cds', 'admin-cds', 'file-info-view', 'submit-new-doc'], {serie: true})
                        .then(function () {
                                var caseDocketDataSvc = $injector.get("caseDocketDataSvc");
                                return caseDocketDataSvc.getCaseDocketInfo($route.current.params.a_No);
                            }, function (error) {
                                $location.path("/login");
                            }
                        );
                    }]
                }
            })
        .when('/admin-case-docketsheet/:role/:a_No', {
            templateUrl: 'scripts/app/core/blankPage.htm',
            controller: 'gcDocketSheetController',
        })
        .when('/admin/account-reset', {
            title: 'Manage Judge User Info',
            templateUrl: 'scripts/app/core/blankPage.htm',
            controller: 'accountResetCtrl'
        })
        .when('/admin/manage-agency-accounts', {
            title: 'Agency Account Reset',
            templateUrl: 'scripts/app/core/blankPage.htm',
            controller: 'agencyAccountResetCtrl',
            resolve: {
                /* @ngInject */
                loadModule: ['$ocLazyLoad', '$location', function ($ocLazyLoad, $location) {
                    return $ocLazyLoad.load(['jquery-datatables', 'angular-datatables', 'agency', 'dashboard', 'account-update', 'admin-dashboard', 'manage-gao', 'manage-agency', 'account-reset', 'parties'], {
                        serie: true,
                        cache: true
                    })
                    .then(function () {
                        }, function (error) {
                            $location.path("/login");
                        }
                    );
                }]
            }
        })
        .otherwise({
            redirectTo: '/login'
        });

        if (window.history && window.history.pushState) {
            $locationProvider.html5Mode({
                enabled: true,
                requireBase: true,
            });
        }
    }]);

$.fn.extend({
    animateCss: function (animationName) {
        var animationEnd = 'webkitAnimationEnd mozAnimationEnd MSAnimationEnd oanimationend animationend';
        $(this).addClass('animated ' + animationName).one(animationEnd, function () {
            $(this).removeClass('animated ' + animationName);
        });
    }
});

/* @ngInject */
// epdsApp.run(['$rootScope', '$route', '$location', function ($rootScope, $route, $location) {
//     $rootScope.$on('cfpLoadingBar:loading', function () {
//         $rootScope.loading = true;
//     });
//
//     $rootScope.$on('cfpLoadingBar:completed', function () {
//         /*$(':input').focus();*/
//         $rootScope.loading = false;
//     });
//     $rootScope.$on('$routeChangeSuccess', function () {
//         document.title = $route.current.title + " | EPDS";
//     });
// }]);

/* @ngInject */
// epdsApp.directive('maxlength', ['$compile', '$log', function ($compile, $log) {
//     return {
//         restrict: 'A',
//         require: 'ngModel',
//         /* @ngInject */
//         link: function (scope, elem, attrs, ctrl) {
//             attrs.$set("ngTrim", "false");
//             var maxlength = parseInt(attrs.maxlength, 10);
//             ctrl.$parsers.push(function (value) {
//                 //$log.info("In parser function value = [" + value + "].");
//                 if (value.length > maxlength) {
//                     //$log.info("The value [" + value + "] is too long!");
//                     value = value.substr(0, maxlength);
//                     ctrl.$setViewValue(value);
//                     ctrl.$render();
//                     //$log.info("The value is now truncated as [" + value + "].");
//                 }
//                 return value;
//             });
//         }
//     };
// }]);
