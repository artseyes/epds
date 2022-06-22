 /* @ngInject */
angular.module('templateDocumentTypesFilter', []).filter('bydoctype',
		function() {
	 /* @ngInject */
			return function(docInfoList, docIds) {
				var out = [];
				angular.forEach(docInfoList, function(eachDocInfo, index) {
					angular.forEach(docIds, function(eachTemplateDocId, index) {
						if (eachTemplateDocId === eachDocInfo.doc_Type_Id) {
							out.push(eachDocInfo);
						}
					})
				});

				return out;
			}
		});