/* @ngInject */
angular.module('epdsApp.auth').directive('restrictSpecialChars', function($parse) {
    return {
        restrict: 'A',
        require: 'ngModel',
        
        link: function(scope, iElement, iAttrs, controller) {
            scope.$watch(iAttrs.ngModel, function(value) {
                if (!value) {
                    return;
                }
                $parse(iAttrs.ngModel).assign(scope, value.replace(new RegExp(iAttrs.restrictSpecialChars, 'g'), '').replace(/\s+/g, '-'));
            });
        }
    }
});