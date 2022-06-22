/*epdsApp.config([ '$routeProvider', '$locationProvider',
		function($routeProvider, $locationProvider) {

			var version = "?bust=" + (new Date()).getTime();
			$routeProvider.when('/advance-search', {
				templateUrl : 'views/advanced-search.html' + version,
			})

			if (window.history && window.history.pushState) {
				$locationProvider.html5Mode({
					enabled : true,
					requireBase : true,
				});
			}

		} ]);*/