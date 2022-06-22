(function() {
  "use strict";
  /* @ngInject */
  angular.module("internationalPhoneNumber", []).directive('internationalPhoneNumber', [
    '$timeout', function($timeout) {
      return {
        restrict: 'A',
        require: '^ngModel',
        scope: {
          ngModel: '='
        },
        /* @ngInject */
        link: function(scope, element, attrs, ctrl) {

          const phoneUtil = libphonenumber.PhoneNumberUtil.getInstance();
          const PNF = libphonenumber.PhoneNumberFormat;

          var options = {
            autoHideDialCode: true,
            nationalMode: true,
            numberType: '',
            preferredCountries: ['us', 'ca'],
            responsiveDropdown: false,
            utilsScript: "scripts/vendor/intl-tel-input/build/js/utils.js",
            formatOnDisplay: true,
            autoFormat: false,
            autoPlaceholder: "off"
          };

          // watch once and initialize
          var watchOnce = scope.$watch('ngModel', function(newValue) {
            return scope.$$postDigest(function() {
              element.intlTelInput(options);
              element.intlTelInput('loadUtils', options.utilsScript);

              // using scope to save country code so non-US + codes can be saved to DB on update
              // Using control name for a dynamic variable so both phone/fax ora differing name can be used separately
              scope.$parent[ctrl.$name + 'CountryCode'] = "";

              return watchOnce();
            });
          });

          // watch until get a valid value, if it has a +, call handlePhoneFormatting to set flag
          var checkOnce = scope.$watch('ngModel', function(newValue) {
            if (!!newValue) {
              $timeout(function() {
                if (newValue[0] === '+') {
                  handlePhoneFormatting(newValue);
                }
                checkOnce();
              }, 0);
            }
          });

          var handlePhoneFormatting = function(newValue) {
            var number;
            if (!!newValue) {
              number = newValue;
            } else {
              number = element.intlTelInput("getNumber");
            }
            if (number === "") {
              if (ctrl.$name === "phonenumber") {
                ctrl.$setValidity('invalidNumber', false);
              }
              return;
            }

            try {
              // assuming US region. If a number with a + comes in it ignores the region
              number = phoneUtil.parse(number, "US");
            } catch (e) {
              ctrl.$setValidity('invalidNumber', false);
              return;
            }

            var newString = phoneUtil.format(number, PNF.NATIONAL);
            scope.$parent[ctrl.$name + 'CountryCode'] = "";

            var regionCode = phoneUtil.getRegionCodeForNumber(number);
            if (regionCode) {
              element.intlTelInput('setCountry', regionCode);
              const countryCode = phoneUtil.getCountryCodeForRegion(regionCode);
              if (countryCode !== 1) {
                scope.$parent[ctrl.$name + 'CountryCode'] = "+" + countryCode + " ";
              }
            }

            ctrl.$setValidity('invalidNumber', phoneUtil.isPossibleNumber(number));
            element.intlTelInput('setNumber', newString);

            return ctrl.$setViewValue(newString);
          }

          element.on('blur', function(event) {
            return scope.$apply(handlePhoneFormatting())
          });

          element.on("countrychange", function() {
            return scope.$apply(handlePhoneFormatting())
          });

          element.on('$destroy', function() {
            element.intlTelInput('destroy');
            return element.off('blur');
          });
        }
      };
    }
  ]);

}).call(this);
