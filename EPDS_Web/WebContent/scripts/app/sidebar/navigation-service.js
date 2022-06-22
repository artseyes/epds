(function () {
    'use strict';

    var serviceId = 'navigationSvc';

    /* @ngInject */
    angular.module('epdsApp').factory(serviceId,
        ['$rootScope', '$q', 'userInfoService', '$location', 'base64', navigationSvc]);

    /* @ngInject */
    function navigationSvc($rootScope, $q, userInfoService, $location, base64) {

        var service = {
            setListOfRoutesBasedOnRole: setListOfRoutesBasedOnRole,
        };

        return service;

        function setListOfRoutesBasedOnRole(object) {

            $('#sideBarNavigation').hide();

            if ($location.path().indexOf("profile") < 0) {
                if (object.navigationType == "dashboard") {
                    $rootScope.routes = getListOfRoutesInDashboardBasedOnRole(object)
                } else if (object.navigationType == "caseDocketSheet") {
                    $rootScope.routes = getListOfRoutesInCaseDocketSheetBasedOnRole(object)
                }
                $('#sideBarNavigation').fadeIn()
                $('#sideBarNavigation').animateCss('fadeInLeft');
            }

            return $q.when($rootScope.routes);
        }

        function getListOfRoutesInDashboardBasedOnRole(object) {

            var routes = [];

            if ((object.roleId == "1"
                || object.roleId == "2"
                || object.roleId == "4"
                || object.roleId == "9")
                && object.navigationType == "dashboard") {

                routes = [{
                    title: "Active Cases",
                    href: "dashboard",
                    menuIconClass: "fa fa-dashboard"
                }, {
                    title: "File New Case",
                    href: "javascript:void(0)",
                    menuIconClass: "fa fa-file",
                    isFileNewProtestOption: "Y",
                },
                    {
                        title: "Request to Intervene",
                        href: "intervene",
                        menuIconClass: "fa fa-search"
                    },

                ]


                return routes;
            } else if (
                (object.roleId == "5"
                    || object.roleId == "6")
                && object.navigationType == "dashboard") {


                routes [0] = {
                    title: "Active Cases",
                    href: "dashboard",
                    menuIconClass: "fa fa-dashboard"
                }

                if (object.roleId == "6") {
                    routes[1] = {
                        title: "Join a Case",
                        href: "intervene",
                        menuIconClass: "fa fa-search"
                    }
                }
                return routes;
            } else if (
                (object.roleId == "3"
                    || object.roleId == "8")
                && object.navigationType == "dashboard") {

                routes = [{
                    title: "Active Cases",
                    href: "dashboard",
                    menuIconClass: "fa fa-dashboard"
                }, {
                    title: "Advanced Search",
                    href: "advance-search/1",
                    menuIconClass: "fa fa-search"
                },

                ]

                return routes;
            } else if (object.roleId == "7"
                && object.navigationType == "dashboard") {

                routes = [

                    {
                        title: "Unassigned Cases",
                        href: "admin-dashboard/unassigned",
                        menuIconClass: "fa fa-dashboard"
                    }, {
                        title: "Assigned Cases",
                        href: "admin-dashboard/assigned",
                        menuIconClass: "fa fa-dashboard"
                    }, {
                        title: "File New Case",
                        href: "javascript:void(0)",
                        menuIconClass: "fa fa-file",
                        isFileNewProtestOption: "Y",
                    }, {
                        title: "Advanced Search",
                        href: "advance-search/1",
                        menuIconClass: "fa fa-search"
                    }

                ]


                routes[0].submenu = [
                    {
                        title: "Manage Agency Contacts",
                        href: "manage-agency-contacts",
                        menuIconClass: ""
                    },
                    {
                        title: "Manage GAO Users",
                        href: "manage-attorneys",
                        menuIconClass: ""
                    },
                    {
                        title: "Remove case",
                        href: "javascript:void(0)",
                        menuIconClass: "",
                        isRemoveCase: true
                    },
                    {
                        title: "Send System Notifications",
                        href: "javascript:void(0)",
                        menuIconClass: "",
                        isNotification: true
                    },
                    {
                        title: "Edit Templates",
                        href: "edit-template-documents",
                        menuIconClass: ""
                    },
                    {
                        title: "Audit Reports",
                        href: "report/1",
                        menuIconClass: ""
                    }, {
                        title: "Manage Agency Account Resets",
                        href: "/admin/manage-agency-accounts",
                        menuIconClass: ""
                    },
                    {
                        title: "Manage Account Resets",
                        href: "admin/account/reset",
                        menuIconClass: ""
                    }, {
                        title: "Delete User Accounts",
                        href: "admin/account/delete",
                        menuIconClass: ""
                    }

                ]
                return routes;
            }

        }


        function getListOfRoutesInCaseDocketSheetBasedOnRole(object) {

            var today = new moment(new Date())
            var numberOfDays = 0;
            var urlEncodedAnum = object.protestInfo && base64.urlencode(object.protestInfo.a_No);

            if (object.protestInfo.public_decision_date != null) {
                numberOfDays = today.diff(object.protestInfo.public_decision_date, 'days');
            }

            var completeOptionTitle = "";

            //Zip file needs to be created
            if (object.protestInfo.caseCompletionStatus.isZipCreated === false) {
                completeOptionTitle = "Complete"
                $rootScope.verifyDm = false;
                //enter DM#
            } else if (object.protestInfo.caseCompletionStatus.isZipCreated === true
                && object.protestInfo.caseCompletionStatus.isDmEntered === false) {
                completeOptionTitle = "Final Record"
                $rootScope.verifyDm = false;
                //pending DM
            } else if (object.protestInfo.caseCompletionStatus.isZipCreated === true
                && object.protestInfo.caseCompletionStatus.isDmEntered === true
                && object.protestInfo.caseCompletionStatus.isDmVerfied === null) {
                completeOptionTitle = "Pending Verification"
                $rootScope.verifyDm = false;
                //verify DM
            } else if (object.protestInfo.caseCompletionStatus.isZipCreated === true
                && object.protestInfo.caseCompletionStatus.isDmEntered === true
                && object.protestInfo.caseCompletionStatus.isDmVerfied === false) {
                completeOptionTitle = "Verify DM"
                $rootScope.verifyDm = true;
            }

            var routes = [];

            if ((object.roleId == "1"
                || object.roleId == "2"
                || object.roleId == "4"
                || object.roleId == "9")
                && object.navigationType == "caseDocketSheet") {

                if ($location.path().indexOf("docketsheet") > -1) {
                    routes [0] = {
                        title: "Active Cases",
                        href: "dashboard",
                        menuIconClass: "fa fa-dashboard"
                    }
                } else if ($location.path().indexOf("docketsheet") <= 0) {
                    routes [0] = {
                        title: "Case Docket Sheet",
                        href: "case-docketsheet/" + urlEncodedAnum,
                        menuIconClass: "fa fa-dashboard"
                    }
                }

                if (!object.protestInfo.viewOnly && numberOfDays <= 60
                    && object.protestInfo.caseCompletionStatus.isZipCreated !== true) {

                    routes[1] = {
                        title: "Submit New Documents",
                        href: "submit-new-doc-form/" + urlEncodedAnum,
                        menuIconClass: "fa fa-paperclip"
                    }
                }

                if (object.protestInfo.caseCompletionStatus.isZipCreated !== true) {
                    routes.push({
                        title: "Parties",
                        href: "parties/" + urlEncodedAnum,
                        menuIconClass: "fa fa-group"
                    })

                    routes.push({
                        title: "Email Preferences",
                        href: "javascript:void(0)",
                        menuIconClass: "fa fa-envelope",
                        isEmailPreferenceOption: "Y",
                        protestInfo: object.protestInfo
                    })
                }

                if (object.protestInfo.case_Status != 'OPEN') {

                    routes.push({
                        title: "Request for Reconsideration ",
                        href: "other-protest/reconsideration/" + urlEncodedAnum,
                        menuIconClass: "fa fa-paperclip"
                    })
                }

                if (object.protestInfo.case_Status != 'OPEN' &&
                    (object.roleId == "1"
                        || object.roleId == "4")) {

                    routes.push({
                        title: "Request for Entitlement ",
                        href: "other-protest/entitlement/" + urlEncodedAnum,
                        menuIconClass: "fa fa-paperclip"
                    })
                    routes.push({
                        title: "Claim for Costs",
                        href: "other-protest/cost-claim/" + urlEncodedAnum,
                        menuIconClass: "fa fa-paperclip"
                    })
                }


                return routes;

            } else if ((object.roleId == "5" || object.roleId == "6") && object.navigationType == "caseDocketSheet") {


                if ($location.path().indexOf("docketsheet") > -1) {
                    routes [0] = {
                        title: "Active Cases",
                        href: "dashboard",
                        menuIconClass: "fa fa-dashboard"
                    }
                } else if ($location.path().indexOf("docketsheet") <= 0) {
                    userInfoService.getRoleFromRoleId(object.roleId).then(function (role) {
                        routes [0] = {
                            title: "Case Docket Sheet",
                            href: "case-docketsheet/" + urlEncodedAnum,
                            menuIconClass: "fa fa-dashboard"
                        }
                    })
                }

                if (!object.protestInfo.viewOnly && numberOfDays <= 60
                    && object.protestInfo.caseCompletionStatus.isZipCreated !== true) {

                    routes[1] = {
                        title: "Submit New Documents",
                        href: "submit-new-doc-form/" + urlEncodedAnum,
                        menuIconClass: "fa fa-paperclip"
                    }
                }

                if (object.protestInfo.caseCompletionStatus.isZipCreated !== true) {

                    routes.push({
                        title: "Manage Agency Attorneys/Parties",
                        href: "parties/" + urlEncodedAnum,
                        menuIconClass: "fa fa-group"
                    })

                    routes.push({
                        title: "Email Preferences",
                        href: "javascript:void(0)",
                        menuIconClass: "fa fa-envelope",
                        isEmailPreferenceOption: "Y",
                        protestInfo: object.protestInfo
                    })
                }

                if (object.protestInfo.case_Status != 'OPEN') {

                    routes.push({
                        title: "Request for Reconsideration ",
                        href: "other-protest/reconsideration/" + urlEncodedAnum,
                        menuIconClass: "fa fa-paperclip"
                    })
                }

                return routes;

            } else if ((object.roleId == "3"
                || object.roleId == "8")
                && object.navigationType == "caseDocketSheet") {


                if ($location.path().indexOf("docketsheet") > -1) {
                    routes [0] = {
                        title: "Active Cases",
                        href: "dashboard",
                        menuIconClass: "fa fa-dashboard"
                    }
                } else if ($location.path().indexOf("docketsheet") <= 0) {
                    routes [0] = {
                        title: "Case Docket Sheet",
                        href: "case-docketsheet/" + urlEncodedAnum,
                        menuIconClass: "fa fa-dashboard"
                    }
                }

                console.info("number of days", numberOfDays)
                if (!object.protestInfo.viewOnly
                    && object.protestInfo.caseCompletionStatus.isZipCreated !== true) {

                    routes[1] = {
                        title: "Submit New Documents",
                        href: "submit-new-doc-form/" + urlEncodedAnum,
                        menuIconClass: "fa fa-paperclip"
                    }
                }

                if (object.protestInfo.caseCompletionStatus.isZipCreated !== true) {

                    routes.push({
                        title: "Parties",
                        href: "parties/" + urlEncodedAnum,
                        menuIconClass: "fa fa-group"
                    })

                    if (!object.protestInfo.viewOnly) {
                        routes.push({
                            title: "Email Preferences",
                            href: "javascript:void(0)",
                            menuIconClass: "fa fa-envelope",
                            isEmailPreferenceOption: "Y",
                            protestInfo: object.protestInfo
                        })
                    }

                }


                routes.push({
                    title: "Advanced Search",
                    href: "advance-search/" + urlEncodedAnum,
                    menuIconClass: "fa fa-search",
                })


                if (numberOfDays > 60) {
                    if (completeOptionTitle !== "") {
                        routes.push({
                            title: completeOptionTitle,
                            href: "javascript:void(0)",
                            menuIconClass: "fa fa-link",
                            isCompleteOption: "Y",
                            protestInfo: object.protestInfo
                        })
                    }

                }
                return routes;

            } else if (object.roleId == "7"
                && object.navigationType == "caseDocketSheet") {

                if ($location.path().indexOf("docketsheet") > -1) {

                    var activeCases = [
                        {
                            title: "Unassigned Cases",
                            href: "admin-dashboard/unassigned",
                            menuIconClass: "glyphicon glyphicon-briefcase"
                        }, {
                            title: "Assigned Cases",
                            href: "admin-dashboard/assigned",
                            menuIconClass: "glyphicon glyphicon-briefcase"
                        }
                    ]
                    routes.activeCases = activeCases;

                    if (object.protestInfo.caseCompletionStatus.isZipCreated !== true) {
                        routes[0] = {
                            title: "Submit New Documents",
                            href: "submit-new-doc-form/" + urlEncodedAnum,
                            menuIconClass: "fa fa-paperclip"
                        }
                        routes[1] = {
                            title: "Join / Unjoin Cases",
                            href: "javascript:void(0)",
                            menuIconClass: "fa fa-link",
                            isJoinOption: "Y",
                            protestInfo: object.protestInfo
                        }
                        routes[2] = {
                            title: "Parties",
                            href: "parties/" + urlEncodedAnum,
                            menuIconClass: "fa fa-group"
                        }

                    }

                    routes.push({
                        title: "Advanced Search",
                        href: "advance-search/" + urlEncodedAnum,
                        menuIconClass: "fa fa-search",
                    })


                    routes.push({
                        title: "Email Preferences",
                        href: "javascript:void(0)",
                        menuIconClass: "fa fa-envelope",
                        isEmailPreferenceOption: "Y",
                        protestInfo: object.protestInfo
                    })

                    if (object.protestInfo.case_Status != 'OPEN') {

                        routes.push({
                            title: "Request for Reconsideration ",
                            href: "other-protest/reconsideration/" + urlEncodedAnum,
                            menuIconClass: "fa fa-paperclip"
                        })

                        routes.push({
                            title: "Request for Entitlement ",
                            href: "other-protest/entitlement/" + urlEncodedAnum,
                            menuIconClass: "fa fa-paperclip"
                        })

                        routes.push({
                            title: "Claim for Costs",
                            href: "other-protest/cost-claim/" + urlEncodedAnum,
                            menuIconClass: "fa fa-paperclip"
                        })

                        if (numberOfDays > 60) {

                            if (completeOptionTitle !== "") {
                                routes.push({
                                    title: completeOptionTitle,
                                    href: "javascript:void(0)",
                                    menuIconClass: "fa fa-link",
                                    isCompleteOption: "Y",
                                    protestInfo: object.protestInfo
                                })
                            }
                        }

                    }


                } else if ($location.path().indexOf("docketsheet") <= 0) {

                    routes [0] = {
                        title: "Case Docket Sheet",
                        href: "admin-case-docketsheet/" + urlEncodedAnum,
                        menuIconClass: "glyphicon glyphicon-briefcase"
                    }

                    if (object.protestInfo.caseCompletionStatus.isZipCreated !== true) {

                        routes[1] = {
                            title: "Submit New Documents",
                            href: "submit-new-doc-form/" + urlEncodedAnum,
                            menuIconClass: "fa fa-paperclip"
                        }

                        routes[2] = {
                            title: "Join / Unjoin Cases",
                            href: "javascript:void(0)",
                            menuIconClass: "fa fa-link",
                            isJoinOption: "Y",
                            protestInfo: object.protestInfo
                        }
                        routes[3] = {
                            title: "Parties",
                            href: "parties/" + urlEncodedAnum,
                            menuIconClass: "fa fa-group"
                        }
                    }

                    routes.push({
                        title: "Advanced Search",
                        href: "advance-search/" + urlEncodedAnum,
                        menuIconClass: "fa fa-search",
                    })


                    if (object.protestInfo.case_Status != 'OPEN') {

                        routes.push({
                            title: "Request for Reconsideration ",
                            href: "other-protest/reconsideration/" + urlEncodedAnum,
                            menuIconClass: "fa fa-paperclip"
                        })

                        routes.push({
                            title: "Request for Entitlement ",
                            href: "other-protest/entitlement/" + urlEncodedAnum,
                            menuIconClass: "fa fa-paperclip"
                        })

                        routes.push({
                            title: "Claim for Costs",
                            href: "other-protest/cost-claim/" + urlEncodedAnum,
                            menuIconClass: "fa fa-paperclip"
                        })

                        if (numberOfDays > 60) {

                            if (completeOptionTitle !== "") {
                                routes.push({
                                    title: completeOptionTitle,
                                    href: "javascript:void(0)",
                                    menuIconClass: "fa fa-link",
                                    isCompleteOption: "Y",
                                    protestInfo: object.protestInfo
                                })
                            }
                        }

                    }


                }

                return routes;
            }

        }

    }
})();
