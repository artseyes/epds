/*
 * This directive helps displaying the Parent and child relationship in dashboard. Since we also need to 
 * compile "data-expand-collapse" directive inside this directive we are using recursion helper 
 * @param RecursionHelper A service which makes it easily possible to have recursive Angular directives.
 * @param $location .
 * @return Use the compile function from the RecursionHelper, And return the linking function(s) which it returns
*/
 /* @ngInject */
epdsApp.directive("tree", function(RecursionHelper,$location) {
		  
			return {
		        restrict: "EA",
		        scope: {protest: '=', path :'@'},
		        templateUrl: function(elem,attrs){
		        	if ($location.path() == "/dashboard"){
						var url = ""
					}
		            return "scripts/app/dashboard/dashboard-parent-child-display.html";
		        },
		        compile: function(element) {
		            return RecursionHelper.compile(element);
		        },
		        /* @ngInject */
		        link: function(scope, element, attrs){
		          
		        }
		    };
		});