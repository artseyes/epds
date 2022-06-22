/* This Function is used to populated address fields Based on What Address user selects from input field */

retrieveAddressDetailsFromUserSelection.$inject = ['$scope'];

/* @ngInject */
function retrieveAddressDetailsFromUserSelection($scope, addressDetails) {
	
	var addr = {}
	var streetAddr = addressDetails.formatted_address.split(",")[0]
	if (addressDetails.name === streetAddr) {
		addr.streetAddr = addressDetails.formatted_address.split(",")[0]
	} else if (addressDetails.name !== streetAddr) {
		addr.streetAddr = addressDetails.name + ","
				+ addressDetails.formatted_address.split(",")[0]
	}
	for (var i = 0; i < addressDetails.address_components.length; i++) {
		var city = state = zipcode = country = '';
		var types = addressDetails.address_components[i].types.join(",");
		if (types === "sublocality,political" || types === "locality,political"
				|| types === "neighborhood,political"
				|| types === "administrative_area_level_3,political") {

			if (types === "locality,political") {
				addr.city = addressDetails.address_components[i].long_name;
			}
		}
		if (types == "administrative_area_level_1,political") {
			addr.state = addressDetails.address_components[i].long_name;
		}
		if (types == "postal_code" || types == "postal_code_prefix,postal_code") {
			addr.zipcode = addressDetails.address_components[i].long_name;
		}
		if (types == "country,political") {
			addr.country = addressDetails.address_components[i].long_name;
		}
	}

	return addr;
}