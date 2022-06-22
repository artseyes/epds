 /* @ngInject */
angular.module(
		'epdsApp.caseDocketSheet').directive('ckEditor',['Idle','$rootScope',function(Idle,$rootScope) {
			 
			
			return {
				 require : '?ngModel',
				 restrict : 'C',
				 /* @ngInject */
				 link : function(scope, elm, attr, model) {
					 var removePlugins = " ";
					 
					 var extraPlugins=""
					 if (elm.hasClass('notification')){
						 removePlugins = "preview,print,newpage,scayt,wsc,strinsert "
					 }else{
						 extraPlugins = ",strinsert"
					 }
					 
					 
					 var isReady = false;
					 var data = [];
					 var ck = CKEDITOR
					 .replace(
							 elm[0],
							 {
								 // skin : "office2013",
								 resize_dir : "both",
								 allowedContent : true,
								 removePlugins : 'exportpdf,sourcearea,about,templates,div,link,forms,save,flash,language,iframe,tableselection,image,smiley,horizontalrule, ' + removePlugins,
								 //extraPlugins : "textsignature,strinsert,table,tabletools"
								 /*extraPlugins : 'widget,lineutils,clipboard,placeholder,dialog,dialogui,strinsert,richcombo',*/
								 extraPlugins : 'widget,widgetselection,lineutils,footnotes' + extraPlugins
								  
							 });
					
					
					 console.log($rootScope.userProfileInfo)
					 //CKEDITOR.config.extraAllowedContent = '*(*);*{*}';
					 //CKEDITOR.config.resize_dir = 'both';
					// CKEDITOR.config.skin = "office2013";
					 //CKEDITOR.config.resize_maxWidth = '100%';
					 //CKEDITOR.config.resize_maxWidth =$( window ).width();
					 console.log("all plugins options", CKEDITOR.config.plugins)
					
					 function setData() {
						 if (!data.length) {
							 return;
						 }

						 var d = data.splice(0, 1);
						 ck.setData(d[0] || '<span></span>',
								 function() {
							 setData();
							 isReady = true;
						 });
					 }

					
					 ck.on('instanceReady', function(event) {
						 
						/* //event.editor.resize($( window ).width());
						 event.editor.on('resize',function(reEvent){
							 //reEvent.editor.resize($( window ).width())
							 console.log($( window ).width())
							 CKEDITOR.config.resize_maxWidth = $( window ).width();
						    });*/
						 
						 
						 
						 if (model) {
							 setData();
						 }
					 });

					 elm.on('$destroy', function() {
						 /* $(':input').focus() */
						 ck.destroy(false);
					 });

					 if (model) {
						 
						 ck.on('change', function() {
							 /* Idle.watch() */

							 scope.$apply(function() {
								 var data = ck.getData();
								 if (data == '<span></span>') {
									 data = null;
								 }
								 model.$setViewValue(data);
							 });
						 });

						 model.$render = function(value) {
							 if (model.$viewValue === undefined) {
								 model.$setViewValue(null);
								 model.$viewValue = null;
							 }

							 data.push(model.$viewValue);

							 if (isReady) {
								 isReady = false;
								 setData();
							 }
						 };
					 }

				 }
			 };
		 } ]);
