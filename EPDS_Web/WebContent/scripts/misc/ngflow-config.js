 /* @ngInject */
epdsApp.config([ 'flowFactoryProvider', function(flowFactoryProvider) {
	
	/* var csrfHeader;
	  angular.injector(['ngCookies']).invoke(['$cookies', function($cookies) {
	    csrfHeader = $cookies.get("XSRF-TOKEN")
	  }]);
	  
	  console.log("csrfHeader",csrfHeader)*/
	
	var _generatedUIDs = {};
	
	flowFactoryProvider.defaults = {
		target : '/epds/upload-epds-documents',
		permanentErrors : [ 404, 403,415, 500, 501 ],
		chunkSize : 1000000,// 1mb
		maxChunkRetries : 3,
		chunkRetryInterval : 5000,
		simultaneousUploads : 4,
		testChunks : false,
		fileParameterName :'file',
		method : /*"octet"*/"multipart",
		uploadMethod : "POST",
		generateUniqueIdentifier : function(file) {

			
			while (true) {
				var uid = ("0000" + ((Math.random() * Math.pow(
						36, 4)) | 0).toString(36)).slice(-4);
				if (!_generatedUIDs.hasOwnProperty(uid)) {
					_generatedUIDs[uid] = true;
					return uid;
				}
			}

        },
        
       /* headers: {
		  'Accept': 'application/json, application/xml, text/plain, text/html, *.*',
		  'Content-Type': 'application/x-www-form-urlencoded; charset=utf-8',
		  },*/
		
		 /* headers: {'X-XSRF-TOKEN' : csrfHeader}	*/
	};

	
	flowFactoryProvider.on('catchAll', function(event) {
			console.log('catchAll', arguments);
	});
	// Can be used with different implementations of Flow.js
	// flowFactoryProvider.factory = fustyFlowFactory;
	
	/*flowFactoryProvider.on('fileError', function (file, message, chunk) {
		 
		console.log("virus was detected in ", file.name ,"the file is deleted", message, file.fileError);
		var obj = {
				file : file,
				message : message,
				chunk  : chunk
		}
		
		
		$rootScope.$broadcast('flowFileError', obj);
		
		});*/
} ]);