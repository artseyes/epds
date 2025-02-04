
//Parties Data service is used to retrieve list of parties associated with a specific case

(function() {
	'use strict';

	var serviceId = 'partiesDataSvc';

	angular.module('epdsApp.parties').factory(
			serviceId,
			[ '$rootScope', '$http', '$filter', '$uibModal','$uibModalStack',
				'modalService', '$location', '$timeout','userInfoService','$httpParamSerializerJQLike','$route','navigationSvc','regEx','base64', partiesDataSvc ]);

	function partiesDataSvc($rootScope, $http, $filter, $uibModal,$uibModalStack,
			modalService, $location, $timeout,userInfoService,$httpParamSerializerJQLike,$route,navigationSvc, regEx, base64) {

		var service = {
				getListOfParties : getListOfParties,
				updateProtectiveOrder : updateProtectiveOrder,
				addPartiesToTheCase : addPartiesToTheCase,
				deletePartiesFromTheCase : deletePartiesFromTheCase,
				invitePartiesToTheCase : invitePartiesToTheCase,
				deleteUser : deleteUser,
				getPrimaryRepsOrAgencyRepsInfo : getPrimaryRepsOrAgencyRepsInfo,
				validateInvitationRequests : validateInvitationRequests,
				checkIfEmailContainsTrustedDomain : checkIfEmailContainsTrustedDomain,
				confirmPrimaryOrAgencyRepInfo : confirmPrimaryOrAgencyRepInfo,
				removeIntervenor : removeIntervenor,
				editPartiesInfo : editPartiesInfo,
				updatePartiesInfo : updatePartiesInfo
		};

		return service;

		function getListOfParties(aNum) {
			
			return $http({
				url : '/epds/view-manage-parties',
				method : 'POST',
				headers: {
					'Accept': 'application/json',
					'Content-Type': 'application/json'
						},
				params : {
					aNum : base64.urldecode(aNum)
				}		
			    
			})
					.then(
							function(data) {
						
								var navigationObj = {
										navigationType : "caseDocketSheet",
										caseStatus : "N/A",
										roleId :  data.data.protestInfo.roleId,
										protestInfo : data.data.protestInfo,
										isViewOnly : data.data.protestInfo.viewOnly
								} 
								navigationSvc.setListOfRoutesBasedOnRole(navigationObj);
								return _getPartiesList(data.data);
							},
							function(error) {
								
								return error;
							});

		}
		
		function updateProtectiveOrder(userId, updateStatus, aNum) {
			
			var params = {
				shouldAdmit : updateStatus,
				userId : userId,
				aNum : aNum
			}
			
			return $http({
						url : '/epds/admit-to-po',
						method : 'POST',
						headers : {
							'Content-Type' : 'application/x-www-form-urlencoded'
						},
						data: $httpParamSerializerJQLike(_.omitBy(params, _.isNil)),
					})
					.then(
							function(data) {
								$route.reload();
								return data.data;
							},
							function(error) {
								
								return error;
							});

		}
		
		function addPartiesToTheCase(partyType,inviterRole,intervenorCompanyName,intervenorCompanyAddr,protestInfo){

			var modalTemplateOptions = {};
			var items = {};
			items.aNum = protestInfo.a_No;
			if (inviterRole === "SA") {
    			modalTemplateOptions = {
    				headerText : "Select Secondary Agency Representative",
    				requestType : "assignAttorney",
    				placeHolder : "Enter Secondary Agency Attorney email address",
    				buttonText : "Select"
    			}
    		} else if (partyType === "Secondary Representative") {
    			modalTemplateOptions = {
    				headerText : "Add Secondary Representatives",
    				requestType : "addSecondaryRep",
    				placeHolder : "Enter Representative Email Address"
    			}
    		} else if (partyType == "Assign Primary Representative") {
    			modalTemplateOptions = {
    				headerText : "Assign Primary Representative",
    				requestType : "assignAttorney",
    				placeHolder : "Enter representative email address",
    				buttonText : "Assign Primary Rep"
    			}
    		} else if (partyType == "Assign Agency Representative") {
    			modalTemplateOptions = {
    				headerText : "Assign Agency Representative",
    				requestType : "assignAttorney",
    				placeHolder : "Enter attorney email address",
    				buttonText : "Assign Agency Rep"
    			}
    		}
    		items.companyAddr = intervenorCompanyAddr;
    		items.companyName = intervenorCompanyName;
    		items.typeOfRequest = partyType;
    		items.inviterRole = inviterRole;
    		
    		$uibModal
    				.open({
    					templateUrl : 'scripts/app/parties/invite-assign-additonal-reps.html',
    					controller : invitePartiesToTheCaseModalInstanceCtrl,
    					resolve : {
    						modalOptions : function() {
    							return modalTemplateOptions;
    						},
    						items : function() {
    							return items;
    						}
    					},
    					size : 'md',
    					backdrop : 'static',
    					keyboard : false,
    				}).result.catch(angular.noop);

    	
		}
		function deletePartiesFromTheCase (userId,
				firstName, lastName, partyType,aNum){

    		
    		var obj = {
				userId : userId,
				firstName :firstName,
				lastName : lastName,
				partyType : partyType,
			}
    		
    		if (aNum){
    			obj.aNum  = aNum;
    		}
    		var headerText = "";
    		
    		if (partyType === "SECONDARY PROTESTER" 
    			|| partyType === "SECONDARY INTERVENOR") {
    			headerText = "Delete Secondary Representative"
    		} else if (partyType === "PROTESTER" || partyType === "INTERVENOR") {
    			headerText = "Delete Primary Representative"
    		} else if (partyType === "Agency Attorney") {
    			headerText = "Delete Agency Attorney"
    		}else if (partyType.id == "1"){
    			headerText = "Delete User"
    		}
    		
    		obj.headerText = headerText;
    		
    		$uibModal
    				.open({
    					templateUrl : 'scripts/app/parties/delete-parties-confirmation.html?bust=' + Math.random().toString(36).slice(2),
    					controller : deletePartiesFromTheCaseModalInstanceCtrl,
    					resolve : {
    						data : function() {
    							return obj
    						}
    						
    					},
    					size : 'md',
    					backdrop : 'static'
    				}).result.catch(angular.noop);
    	
			
		}
		
		
		
		
			function editPartiesInfo(form) {
				
					
				if (form.partyType === "protester"){
					form.oldCompanyName = form.company_Name;
					form.companyName = form.company_Name;
					form.address1 = form.company_address1;
					form.address2 = form.company_address2;
					form.city = form.company_City;
					form.state = form.company_State;
					form.country = form.company_Country;
					form.zipCode = form.company_Zipcode;
					
					
				}
				
				if (form.partyType === "intervenor"){
					var aNum = form.aNum;
					var partyType = form.partyType;
					
					if (form && form[0] && form[0].intervenorCompanyInfo){
						form = form && (form[0].intervenorCompanyInfo);
					}
					form.aNum = aNum;
					form.partyType = partyType;

					if (form && form.companyAddress){
						form.oldCompanyName = form.companyName;
						var addressParts = form.companyAddress.split(/\r|\n|,/);
						var addressLength = addressParts && addressParts.length;
						form.address1 = addressParts && addressParts[0] || "";
						if (addressLength == 6){
							form.address2 = addressParts && addressParts[addressParts.length -5] || null;
						}
						form.city = addressParts && addressParts[addressLength -4] || null;
						form.state = addressParts && addressParts[addressLength -3].trim() || null;
						form.zipCode = addressParts && addressParts[addressLength -2].trim() || null;
						form.country = addressParts && addressParts[addressLength -1] || null;
					}

					form.companyName = form.companyName;

				}
				var modalInstanceCtrl = $uibModal.open({
					templateUrl : 'scripts/app/parties/protesterOrIntervenorEditTemp.htm',
					controller : editPartyInfoModalInstanceCtrl,
					resolve : {
						form : function() {
							return form
						}
					},
					size : 'md',
					backdrop : 'static'
				}).result.catch(angular.noop);
			}


		function invitePartiesToTheCase(repEmail,inviter_Type,typeOfRequest,companyName,companyAddr,aNum) {
			
			var params = {
				email : repEmail,
				aNum : aNum
			}
			if (inviter_Type === "P"){
				params.inviter_Type = "protester";
			}else if (inviter_Type === "I"){
				params.inviter_Type = "intervenor";
				params.companyName = companyName;
				params.companyAddr = companyAddr;
			}else if (inviter_Type === "SA"){
				params.inviter_Type = "secondary-agency";
			}
			return $http({
						url : '/epds/invite-secondary-user',
						method : 'POST',
						headers : {
							'Content-Type' : 'application/x-www-form-urlencoded'
						},
						data: $httpParamSerializerJQLike(_.omitBy(params, _.isNil)),
					})
					.then(
							function(data) {
								
								 if (inviter_Type === "P" 
									 || inviter_Type === "I") {
									 typeOfRequest = "Secondary Representative"
					    		} else if (inviter_Type === "A") {
					    			typeOfRequest = "Assign Agency Representative"
					    		}
								validateInvitationRequests(data.data,typeOfRequest,inviter_Type,aNum);
								return data.data;
							},
							function(error) {
								
								return error;
							});

				}
		
		function updatePartiesInfo(form) {
			
			
			return $http({
						url : '/epds/update-party-info',
						method : 'POST',
						headers : {
							'Content-Type' : 'application/x-www-form-urlencoded'
						},
						data: $httpParamSerializerJQLike(_.omitBy(form, _.isNil)),
					})
					.then(
							function(data) {
								return data.data;
							},
							function(error) {
								
								return error;
							});

				}
		
		
		function deleteUser(secondaryRepUserId,aNum) {
					
				var params = {
					secondary_User_Id : secondaryRepUserId
				}	

				if (aNum){
					params.aNum = aNum;
				}
			return $http({
				url : '/epds/delete-secondary-user',
				method : 'POST',
				headers : {
					'Content-Type' : 'application/x-www-form-urlencoded'
				},
				data: $httpParamSerializerJQLike(_.omitBy(params, _.isNil)),
			    
			}).then(
							function(data) {
								return data.data
							},
							function(error) {
								
								return error;
							});

				}
		function _getPartiesList(data){
			
			
			if (!data.attorneyInfo) {
				data.attorneyInfo = {
					first_Name : "pending",
					email : "pending",
					phone_No : "pending",
					street : "pending",
				}
			}
			if (!data.protestInfo.b_No) {
				data.protestInfo.b_No = "pending";
				
			}
			
				$rootScope.authenticated = true;
				userInfoService
						.setUserInfo(data.user_Info);

				if (data.role) {
					if (data.role === "ATTORNEY") {
						data.hideOtherOptions = "AT"
					} else if (data.role === "SUPERVISOR") {
						data.hideOtherOptions = "S"
					} else if (data.role
							.indexOf("AGENCY") >= 0) {
						data.hideOtherOptions = "AG"
					} else {
						data.hideOtherOptions = "P"
					}
				} else {
					data.hideOtherOptions = "P"
				}
				
				data.protester_parties_list = $filter('orderBy')(data.protester_parties_list, 'role');
				
				
				return data;
			

		
		}
		
		
		function getPrimaryRepsOrAgencyRepsInfo(repEmail,inviter_Type,typeOfRequest,companyName,companyAddr,aNum){
			
			
			var params = {
					email : repEmail,
					aNum : aNum
					}
			
			//come back to this && checkIfEmailContainsTrustedDomain(repEmail) aadd this condition
			if (typeOfRequest === "Assign Agency Representative" 
				&& inviter_Type !== "SA") {
				
				params.inviter_Type = "agency-attorney"
					
			}else if (inviter_Type === "P") {
				
				params.inviter_Type = "protester";
				
			}else if (inviter_Type === "I") {
				
				params.inviter_Type = "intervenor";
				
			}else if (inviter_Type === "SA") {
				params.inviter_Type = "secondary-agency"
					
			}
			
			return $http({
				url : '/epds/get-attorney-info',
				method : 'POST',
				headers : {
					'Content-Type' : 'application/x-www-form-urlencoded'
				},
				data: $httpParamSerializerJQLike(_.omitBy(params, _.isNil)),
			})
			.then(
					function(data) {
						validateInvitationRequests(data.data, typeOfRequest,inviter_Type,companyName,companyAddr,aNum);
					},
					function(error) {
						
						return error;
					});

		}
		
		function confirmPrimaryOrAgencyRepInfo(repEmail,inviter_Type,typeOfRequest,companyName,companyAddr,aNum){
			
			var params = {
				email : repEmail,
				aNum : aNum
			}
			
			console.log("confirm primary or agency info ",inviter_Type)
			if (typeOfRequest === "Assign Agency Representative") {
				
				params.assignType = "agency-attorney"
					
			}else if (inviter_Type === "P") {
				
				params.assignType = "primary-protester"
					
			}else if (inviter_Type === "I") {
				
				params.assignType = "primary-intervenor"
				params.companyName = companyName	
				params.companyAddr = companyAddr
				
			}else if (inviter_Type === "SA") {
				
				params.assignType = "secondary-agency"
					
				}
			
			return $http({
				url : '/epds/assign-rep',
				method : 'POST',
				headers : {
					'Content-Type' : 'application/x-www-form-urlencoded'
				},
				data: $httpParamSerializerJQLike(_.omitBy(params, _.isNil)),
			})
			.then(
					function(data) {
						$uibModalStack.dismissAll();
						$route.reload();
					},
					function(error) {
						
						return error;
					});

		
		}
		
		function _checkIfProtesterOrIntervenorIsAssigned(parties_list,role){
			
			return $filter('filter')(parties_list, {role : role}).length > 0;
		}
		
		function removeIntervenor(intervenorInfo,aNum){
			
		var params = {
				intervenorFileId : intervenorInfo.intervenorFileId,
				companyName : intervenorInfo.companyName,
				companyAddress : intervenorInfo.companyAddress,
				companyDetail: 	intervenorInfo.companyDetail,
				aNum : aNum
		}
		
		 return $http({
				url : '/epds/remove-intervenor',
				method : 'POST',
				headers : {
					'Content-Type' : 'application/x-www-form-urlencoded'
				},
				data: $httpParamSerializerJQLike(_.omitBy(params, _.isNil)),
			})
			.then(
					function(data) {
						$route.reload();
					},
					function(error) {
						
						return error;
					});
		}
		function validateInvitationRequests(data, typeOfRequest,inviter_Type,companyName,companyAddr,aNum) {

			var obj = {
					inviter_Type : 	inviter_Type,
					typeOfRequest : typeOfRequest,
					companyName :companyName,
					companyAddr : companyAddr,
					aNum : aNum
			};
			
			
			if (data.validation === "noSuchEmail") {
				
				var bodyText = "There is no EDS user account associated with that email." +
						"  Please enter a different email address or check with the user you are attempting to add to see if the user has an account."
				var customModalOptions = {
					headerText : 'Error',
					bodyText : bodyText,
					closeButtonText : 'OK',
					messageType : "error"
				};
				modalService.showModal({}, customModalOptions).then(function(result) {
				});
			}else if (data.validation === "notFromSameAgency") {
				
				var bodyText = "The user you are trying to add is associated with another agency. You can only add representatives from the same agency. ";
				var customModalOptions = {
					headerText : 'Error',
					bodyText : bodyText,
					closeButtonText : 'OK',
					messageType : "error"
				};
				modalService.showModal({}, customModalOptions).then(function(result) {
				});
			}else if (data.validation === "invalidAgency") {
				
				var bodyText = "This is not an EDS registered agency account. "
				var customModalOptions = {
					headerText : 'Error',
					bodyText : bodyText,
					closeButtonText : 'OK',
					messageType : "error"
				};
				modalService.showModal({}, customModalOptions).then(function(result) {
				});
			} else if (data.validation === "notASecondaryAgency") {
				
				var bodyText = "You cannot add primary agency representative as a secondary agency representative. "
				var customModalOptions = {
					headerText : 'Error',
					bodyText : bodyText,
					closeButtonText : 'OK',
					messageType : "error"
				};
				modalService.showModal({}, customModalOptions).then(function(result) {
				});
			} else if (data.validation === "alreadyInvited") {

				var customModalOptions = {
					headerText : 'Error',
					bodyText : 'The user you are trying to add has a pending invitation to join the case.',
					closeButtonText : 'OK',
					messageType : "error"
				};
				modalService.showModal({}, customModalOptions).then(function(result) {
				});
			} else if (data.validation === "hasAccess") {

				var customModalOptions = {
					headerText : 'Error',
					bodyText : 'The user you are trying to add has either already joined the case or has a pending invitation to join the case.',
					closeButtonText : 'OK',
					messageType : "error"
				};
				modalService.showModal({}, customModalOptions).then(function(result) {
				});
			}else if (data.validation === "sameEmail") {
				var customModalOptions = {
					headerText : 'Error',
					bodyText : 'Same email',
					closeButtonText : 'OK',
					messageType : "error"
				};
				modalService.showModal({}, customModalOptions).then(function(result) {
				});
			} else if (data.validation === "limitCrossed") {
				
				if (typeOfRequest === "Secondary Representative") {

					var customModalOptions = {
						headerText : 'Error',
						bodyText : 'Each party may only have 10 representatives in EDS.  If you would like to substitute a representative, please remove one of the current representatives and then add the new representative.',
						closeButtonText : 'OK',
						messageType : "error"
					};
					modalService.showModal({}, customModalOptions).then(
							function(result) {
							});
				} else {
					var customModalOptions = {
						headerText : 'Error',
						bodyText : 'Each party may only have 10 representatives in EDS.  If you would like to substitute a representative, please remove one of the current representatives and then add the new representative.',
						closeButtonText : 'OK',
						messageType : "error"
					};
					modalService.showModal({}, customModalOptions).then(
							function(result) {
							});
				}

			} else if (data.validation === "valid") {
				
				if (typeOfRequest === "Secondary Representative") {
					
					$uibModalStack.dismissAll();
					var customModalOptions = {
						headerText : 'Success',
						bodyText : 'Your invitation was successfully submitted.',
						closeButtonText : 'OK',
						messageType : "success"
					};
					modalService.showModal({}, customModalOptions).then(
							function(result) {
							});
				} else if (typeOfRequest === "Assign Agency Representative"
						|| typeOfRequest === "Assign Primary Representative") {

					if (data.attorney_Info) {
						obj.attorney_Info = data.attorney_Info;

						$uibModal
								.open({
									templateUrl : 'scripts/app/parties/agency-attorney-info-confirmation.html?bust=' + Math.random().toString(36).slice(2),
									controller : assignPrimaryRep_AgencyAttorneysModalInstanceCtrl,
									resolve : {
										items : function() {
											return obj
										}
									},
									size : 'md',
									backdrop : 'static'
								}).result.catch(angular.noop);
					}

				}

			}
		}
		function checkIfEmailContainsTrustedDomain(repEmail) {

			if (repEmail && repEmail.length > 0) {

				var trustedDomainList = [ 'gov', 'mil' ]

				try {
					var emailREGEX = regEx.email;
					var checkIfThisIsAValidEmailAddress = emailREGEX.exec(repEmail)

					if (!checkIfThisIsAValidEmailAddress) {
						var customModalOptions = {
							headerText : 'Error',
							bodyText : repEmail + ' is not a valid email',
							closeButtonText : 'OK',
							messageType : "error"
						};
						modalService.showModal({}, customModalOptions).then(
								function(result) {
								});
					}

					var isValidEmailExtension = false;

					
					var emailFullyQualifiedDomain = checkIfThisIsAValidEmailAddress[1].split(".")
					var getEmailAddExtension = emailFullyQualifiedDomain[emailFullyQualifiedDomain.length -1].toLowerCase();
					
					for (var i = 0; i < trustedDomainList.length; i++) {
						if (getEmailAddExtension == trustedDomainList[i]) {
							isValidEmailExtension = true;
							break;
						}
					}

					if (!isValidEmailExtension) {
						var customModalOptions = {
							headerText : 'Error',
							bodyText :'.' + getEmailAddExtension
									+ ' is not allowed.  Please enter a compliant email address  with .gov or .mil extension.',
							closeButtonText : 'OK',
							messageType : "error"
						};
						modalService.showModal({}, customModalOptions).then(
								function(result) {
								});

						return false;
					}
					return true;

				} catch (err) {
					return false;
				}
			}

		}
		

	}
})();
