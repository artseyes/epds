/* @ngInject */

epdsApp.directive('boxCollapse', function () {
    return {
        restrict: 'EA',
        /* @ngInject */
        link: function (scope, element, attrs) {
            $(element).on("click", function () {
                var box = element.parents(".box").first();
                //Find the body and the footer
                var box_content = box.find("> .box-body, > .box-footer, > form  >.box-body, > form > .box-footer");
                if (!box.hasClass("collapsed-box")) {
                    //Convert minus into plus
                    element.children(":first")
                    .removeClass('fa-minus')
                    .addClass('fa-plus')
                    .next()
                    .html(' Expand');
                    //Hide the content
                    box_content.slideUp(500, function () {
                        box.addClass("collapsed-box");
                    });
                } else {
                    //Convert plus into minus
                    element.children(":first")
                    .removeClass('fa-plus')
                    .addClass('fa-minus')
                    .next()
                    .html(' Collapse');
                    //Show the content
                    box_content.slideDown(500, function () {
                        box.removeClass("collapsed-box");
                    });
                }

            });

        }
    }

});
