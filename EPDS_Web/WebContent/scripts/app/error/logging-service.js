
/**
 * Service that gives us a nice Angular-esque wrapper around the
 * stackTrace.js pintStackTrace() method. 
 */
epdsApp.factory(
    "traceService",
    function(){
        return({
            print: StackTrace
        });
    }
);

/**
 * Override Angular's built in exception handler, and tell it to 
 * use our new exceptionLoggingService which is defined below
 */
epdsApp.provider(
    "$exceptionHandler",{
        $get: function(exceptionLoggingService){
            return(exceptionLoggingService);
        }
    }
);

/**
 * Exception Logging Service, currently only used by the $exceptionHandler
 * it preserves the default behaviour ( logging to the console) but 
 * also posts the error server side after generating a stacktrace.
 */
epdsApp.factory(
    "exceptionLoggingService",
    ["$log","$window", "traceService",
    function($log, $window, traceService){
        function error(exception, cause){
            // check for ignore exceptions from a cancelled promise and return if found
            // promise.reject(new Error('ignore')) or promise.reject('ignore') will both work
            // normal rejects should use 'new Error' to get a nice stack trace
            var errorException;
            if (typeof exception === "object") {
                if (exception.message.toLowerCase() === "ignore") return;
                errorException = exception;
            } else {
                if (exception === "Possibly unhandled rejection: ignore") return;
                errorException = new Error(exception);
            }
            // preserve the default behaviour which will log the error
            // to the console, and allow the application to continue running.
            $log.error.apply($log, arguments);

            // now try to log the error to the server side.
            try{
                var errorMessage = exception.toString();

                var stackTrace ;
                // use our traceService to generate a stack trace
                traceService.print.fromError(errorException).then(function(error){
                	stackTrace = error;
                	

          		  var data = {
							url: $window.location.href,
		                    message: errorMessage,
		                    type: "exception",
		                    stackTrace: JSON.stringify(stackTrace),
		                    /*cause: ( cause || ""),
		                    location : JSON.stringify(location)*/
						
          		  }
          		  
          		  $.ajax({
                        type: "POST",
                        url: "/epds/logger", 
                        contentType: "application/json; charset=utf-8",
                        data: JSON.stringify(data),
                        dataType: "json",
                    });
                });
                
               
            } catch (loggingError){
                $log.warn("Error server-side logging failed");
                $log.log(loggingError);
            }
        }
        return(error);
    }]
);


/**
 * Application Logging Service to give us a way of logging 
 * error / debug statements from the client to the server.
 */
epdsApp.factory(
    "applicationLoggingService",
    ["$log","$window",function($log, $window){
        return({
            error: function(message){
                // preserve default behaviour
                $log.error.apply($log, arguments);
                // send server side
                $.ajax({
                    type: "POST",
                    url: "/epds/logger",
                    contentType: "application/json",
                    data: angular.toJson({
                        url: $window.location.href,
                        message: message,
                        type: "error"
                    })
                });
            },
            debug: function(message){
                $log.log.apply($log, arguments);
                $.ajax({
                    type: "POST",
                    url: "/clientlogger",
                    contentType: "application/json",
                    data: angular.toJson({
                        url: $window.location.href,
                        message: message,
                        type: "debug"
                    })
                });
            }
            
        });
    }]
);


