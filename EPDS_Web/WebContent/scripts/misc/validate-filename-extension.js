/*
 Protester/Intervenor – PDF & Excel
 Agency – PDF, Excel & Zip
 GAO Attorney/GAO Supervisor – PDF
 GAO Admin – Any file type
 */

var oneMBInBytes = (1024 * 1024),
maximumFileUploadSize = 551,
singleFileUploadSize = 51,
zipFileUploadSize = 551;


checkIfThisUserCanUploadDocumentWithThisFileExtension.$inject = [ 'modalService' ];
checkIfTotalUploadSizeExceedsTheMaxSize.$inject = ['$scope', 'modalService'];

/* @ngInject */
function checkIfThisUserCanUploadDocumentWithThisFileExtension(modalService,
		file, role) {

	var isSingleFileUploadSizeAllowed = true,
	isFileExtensionAllowed = true;
	
	//if zip file then it can be upto 550 MB 
	if ((file.getExtension() === "zip" || file.getExtension() === "zipx") && (file.size >= (oneMBInBytes * zipFileUploadSize))){
		
		isSingleFileUploadSizeAllowed = false;
	}else if((file.getExtension() !== "zip" && file.getExtension() !== "zipx")   && (file.size >= (oneMBInBytes * singleFileUploadSize))){
		//all other file types size cannot exceed more than 50 MB
		
		isSingleFileUploadSizeAllowed = false;
	}
	
	if (!isSingleFileUploadSizeAllowed){
		
		var customModalOptions = {
				headerText : 'Error',
				bodyText : file.name + " size is " + convertBytes(file.size, 2) +". It exceeds the file size limit. The allowed file size for ."
				+ file.getExtension() +" files is upto "  
				+ (file.getExtension() === "zip" ? zipFileUploadSize : singleFileUploadSize) + "MB.",
				closeButtonText : 'OK',
				messageType : "error"
			};

			modalService.showModal({}, customModalOptions);
	}
	
	
	if ((role == "PROTESTER" || role == "INTERVENOR"
			|| role.indexOf("INTERVENOR") >= 0 || role.indexOf("PROTESTER") >= 0)
			&& (file.getExtension() != "pdf" && file.getExtension() != "xls"
					&& file.getExtension() != "xlt"
					&& file.getExtension() != "xlm"
					&& file.getExtension() != "xlsx"
					&& file.getExtension() != "xltx"
					&& file.getExtension() != "xltm"
					&& file.getExtension() != "xlsb"
					&& file.getExtension() != "xla"
					&& file.getExtension() != "xlam"
					&& file.getExtension() != "xll"
					&& file.getExtension() != "xlw"
					&& file.getExtension() != "xlsm")) {

		var customModalOptions = {
			headerText : 'Error',
			bodyText : 'Only PDF documents can be attached',
			closeButtonText : 'OK',
			messageType : "error"
		};

		modalService.showModal({}, customModalOptions).then(function(result) {});

		 isFileExtensionAllowed = false; 
	} else if ((role == "AGENCY ADMIN" || role == "AGENCY ATTORNEY" || (role
			.indexOf("AGENCY") >= 0))
			&& (file.getExtension() != "pdf" && file.getExtension() != "xls"
					&& file.getExtension() != "xlt"
					&& file.getExtension() != "xlm"
					&& file.getExtension() != "xlsx"
					&& file.getExtension() != "xltx"
					&& file.getExtension() != "xltm"
					&& file.getExtension() != "xlsb"
					&& file.getExtension() != "xla"
					&& file.getExtension() != "xlam"
					&& file.getExtension() != "xll"
					&& file.getExtension() != "xlw"
					&& file.getExtension() != "xlsm"
					&& file.getExtension() != "docx"
					&& file.getExtension() != "docm"
					&& file.getExtension() != "dotx"
					&& file.getExtension() != "dotm"
					&& file.getExtension() != "docb"
					&& file.getExtension() != "doc"
					&& file.getExtension() != "dot"
					&& file.getExtension() != "zip"
					&& file.getExtension() != "zipx") ) {

		var customModalOptions = {
			headerText : 'Error',
			bodyText : 'Only PDF documents can be attached',
			closeButtonText : 'OK',
			messageType : "error"
		};

		modalService.showModal({}, customModalOptions).then(function(result) {
		});

		isFileExtensionAllowed = false; 
	} else if ((role == "GAO ATTORNEY" || role == "GAO SUPERVISOR")
			&& (file.getExtension() != "pdf" && file.getExtension() != "xls"
					&& file.getExtension() != "xlt"
					&& file.getExtension() != "xlm"
					&& file.getExtension() != "xlsx"
					&& file.getExtension() != "xltx"
					&& file.getExtension() != "xltm"
					&& file.getExtension() != "xlsb"
					&& file.getExtension() != "xla"
					&& file.getExtension() != "xlam"
					&& file.getExtension() != "xll"
					&& file.getExtension() != "xlw"
					&& file.getExtension() != "xlsm"
					&& file.getExtension() != "docx"
					&& file.getExtension() != "docm"
					&& file.getExtension() != "dotx"
					&& file.getExtension() != "dotm"
					&& file.getExtension() != "docb"
					&& file.getExtension() != "doc"
					&& file.getExtension() != "dot"
					&& file.getExtension() != "zip"
					&& file.getExtension() != "zipx")) {

		var customModalOptions = {
			headerText : 'Error',
			bodyText : 'Only PDF files can be attached',
			closeButtonText : 'OK',
			messageType : "error"
		};

		modalService.showModal({}, customModalOptions).then(function(result) {
		});

		isFileExtensionAllowed = false;
	} else if ((role == "GAO ADMIN")
			&& (file.getExtension() != "pdf" && file.getExtension() != "xls"
					&& file.getExtension() != "xlt"
					&& file.getExtension() != "xlm"
					&& file.getExtension() != "xlsx"
					&& file.getExtension() != "xltx"
					&& file.getExtension() != "xltm"
					&& file.getExtension() != "xlsb"
					&& file.getExtension() != "xla"
					&& file.getExtension() != "xlam"
					&& file.getExtension() != "xll"
					&& file.getExtension() != "xlw"
					&& file.getExtension() != "xlsm"
					&& file.getExtension() != "docx"
					&& file.getExtension() != "docm"
					&& file.getExtension() != "dotx"
					&& file.getExtension() != "dotm"
					&& file.getExtension() != "docb"
					&& file.getExtension() != "doc"
					&& file.getExtension() != "dot"
					&& file.getExtension() != "zip"
					&& file.getExtension() != "zipx")) {

		var customModalOptions = {
			headerText : 'Error',
			bodyText : 'Only PDF files can be attached',
			closeButtonText : 'OK',
			messageType : "error"
		};

		modalService.showModal({}, customModalOptions).then(function(result) {
		});

		isFileExtensionAllowed = false; 
	} 
	

	return isSingleFileUploadSizeAllowed && isFileExtensionAllowed;

}
/* @ngInject */
function checkIfTotalUploadSizeExceedsTheMaxSize($scope, modalService) {
	var singleFileUpload = $scope.singleFileUpload,
		multipleFileUpload = $scope.multipleFileUpload,
		totalUploadSize;


	
	if (multipleFileUpload) {
		totalUploadSize = multipleFileUpload.getSize() + singleFileUpload.getSize();
		
	} else if (singleFileUpload) {
		totalUploadSize = singleFileUpload.getSize();
	}

	if (totalUploadSize && totalUploadSize > (oneMBInBytes * maximumFileUploadSize)) {
		
		var customModalOptions = {
				headerText : 'Error',
				bodyText : 'Total upload size is exceeded! Please make sure that total upload size of all the files is <= 551MB.',
				closeButtonText : 'OK',
				messageType : "error"
			};

			modalService.showModal({}, customModalOptions);
		return false;
	}

	return true;
}


function convertBytes(bytes, precision) {
	if (isNaN(parseFloat(bytes)) || !isFinite(bytes)) return '-';
	if (typeof precision === 'undefined') precision = 1;
	var units = ['bytes', 'kB', 'MB', 'GB', 'TB', 'PB'],
		number = Math.floor(Math.log(bytes) / Math.log(1024));
	return (bytes / Math.pow(1024, Math.floor(number))).toFixed(precision) +  ' ' + units[number];
}
