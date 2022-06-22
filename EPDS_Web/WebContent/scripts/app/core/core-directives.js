(function () {
	'use strict';
	
	angular.module('epdsApp.core')
	
	.directive('removeModal', ['$document', function ($document) {
	    return {
	        restrict: 'A',
	        link: function (scope, element, attrs) {
	            element.on('click', function () {
	                $document[0].body.classList.remove('modal-open');
	                    angular.element($document[0].getElementsByClassName('modal-backdrop')).remove();
	                    angular.element($document[0].getElementsByClassName('modal')).remove();
	                });
	            }
	        };
	    }]);
	
	
	angular.module('epdsApp.core').directive('targetBlank', [function() {
		  return {
		    compile: function(element) {
		      var elems = (element.prop("tagName") === 'A') ? element : element.find('a');
		      elems.attr("target", "_blank");
		    }
		  };
		}]);
	
	angular.module('epdsApp.core').directive('lowercase', function() {
		   return {
		     require: 'ngModel',
		     
		     link: function(scope, element, attrs, modelCtrl) {
		        var lowerCase = function(inputValue) {
		           if(inputValue === undefined) inputValue = '';
		           var lowerCased = inputValue.toLowerCase();
		           if(lowerCased !== inputValue) {
		              modelCtrl.$setViewValue(lowerCased);
		              modelCtrl.$render();
		            }         
		            return lowerCased;
		         }
		         modelCtrl.$parsers.push(lowerCase);
		         lowerCase(scope[attrs.ngModel]);  // capitalize initial value
		     }
		   };
		});

	// not sure why this was used in input fields over the fields built in maxlength
	// angular.module('epdsApp.core').directive("limitTo", [function() {
	//   return {
	//       restrict: "A",
	//
	//           link: function(scope, elem, attrs) {
	//               var limit = parseInt(attrs.limitTo);
	//               angular.element(elem).on("keydown", function(event) {
	//                   if(event.keyCode > 47 && event.keyCode < 127) {
	//                       if (this.value.length == limit)
	//                           return false;
	//                   }
	//               });
	//           }
	//       }
	//   }]);
	
	
	angular.module('epdsApp.core').directive('match', match);
	/* @ngInject */
	function match ($parse) {
	    return {
	        require: '?ngModel',
	        restrict: 'A',

	        link: function(scope, elem, attrs, ctrl) {
	            if(!ctrl || !attrs.match) {
	                return;
	            }

	            var matchGetter = $parse(attrs.match);
	            var caselessGetter = $parse(attrs.matchCaseless);
	            var noMatchGetter = $parse(attrs.notMatch);
	            var matchIgnoreEmptyGetter = $parse(attrs.matchIgnoreEmpty);

	            scope.$watch(getMatchValue, function(){
	                ctrl.$$parseAndValidate();
	            });

	            ctrl.$validators.match = function(modelValue, viewValue){
	              var matcher = modelValue || viewValue;
	              var match = getMatchValue();
	              var notMatch = noMatchGetter(scope);
	              var value;

	              if (matchIgnoreEmptyGetter(scope) && !viewValue) {
	                return true;
	              }

	              if (matcher) {
					  if (caselessGetter(scope)) {
						  value = matcher.toLowerCase() === match.toLowerCase()
					  } else {
						  value = matcher === match;
					  }
				  }
	              /*jslint bitwise: true */
	              value ^= notMatch;
	              /*jslint bitwise: false */
	              return !!value;
	            };

	            function getMatchValue(){
	                var match = matchGetter(scope);
	                if(angular.isObject(match) && match.hasOwnProperty('$viewValue')){
	                    match = match.$viewValue;
	                }
	                return match;
	            }
	        }
	    };
	}
}());
