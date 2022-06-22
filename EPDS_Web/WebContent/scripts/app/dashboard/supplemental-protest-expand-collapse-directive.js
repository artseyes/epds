/* @ngInject */
epdsApp.directive('expandCollapse', function() {
	return {
		restrict : 'EA',
		
		link : function(scope, element, attrs) {
			$(element).on("click",
					function() {
						
						
						$(this).find('i').toggleClass('glyphicon-plus-sign')
								.toggleClass('glyphicon-minus-sign');
						/*$(this).closest('ul').toggle()*/
						var id = $(this).prop('id')
						/*$(document.getElementById('supplemental-protest-list_' + id))*/
						
						$(this).parent().find('ul').toggle();
						/*$(document.getElementById('supplemental-protest-list_' + id)).toggle();*/
						
					});

		}
	}

});
