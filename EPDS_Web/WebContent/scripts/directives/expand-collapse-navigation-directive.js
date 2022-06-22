
 /* @ngInject */
epdsApp.directive('navigationExpandCollapse', function() {
	return {
		restrict : 'EA',
		 /* @ngInject */
		link : function(scope, element, attrs) {
			$(element).on("click", function() {
				
				if ($(window).width() <= 992) {
					
					$('.row-offcanvas').toggleClass('active');
					$('.left-side').removeClass("collapse-left");
					$(".right-side").removeClass("strech");
					$('.row-offcanvas').toggleClass("relative");
				} else {
					$('.left-side').toggleClass("collapse-left");
					$(".right-side").toggleClass("strech");
				}
			});

		}
	}

});
