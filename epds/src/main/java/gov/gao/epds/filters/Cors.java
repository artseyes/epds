package gov.gao.epds.filters;
/**
     * CORS configuration related properties
     */
	public class Cors {
		
		/**
		 * Comma separated whitelisted URLs for CORS.
		 * Should contain the applicationURL at the minimum.
		 * Not providing this property would disable CORS configuration.
		 */
		
		private String[] allowedOrigins = {"https://epdstest.edc.usda.gov/","10.203.62.13:8080","10.203.65.12:8080","localhost:8080"};
		
		
		/**
		 * Methods to be allowed, e.g. GET,POST,...
		 */
		
		private String[] allowedMethods = {"GET", "HEAD", "POST", "PUT", "DELETE", "TRACE", "OPTIONS", "PATCH"};
		
		/**
		 * Request headers to be allowed, e.g. content-type,accept,origin,x-requested-with,x-xsrf-token,...
		 */
		private String[] allowedHeaders = {
				"Accept",
				"Accept-Encoding",
				"Accept-Language",
				"Cache-Control",
				"Connection",
				"Content-Length",
				"Content-Type",
				"Cookie",
				"Host",
				"Origin",
				"Pragma",
				"Referer",
				"User-Agent",
				"x-requested-with",
				"X-XSRF-TOKEN",
				"X-XSRF-PARAM"};
		
		/**
		 * Response headers that you want to expose to the client JavaScript programmer, e.g. "X-XSRF-TOKEN".
		 * I don't think we need to mention here the headers that we don't want to access through JavaScript.
		 * Still, by default, we have provided most of the common headers.
		 *  
		 * <br>
		 * See <a href="http://stackoverflow.com/questions/25673089/why-is-access-control-expose-headers-needed#answer-25673446">
		 * here</a> to know why this could be needed.
		 */		
		private String[] exposedHeaders = {
				"Cache-Control",
				"Connection",
				"Content-Type",
				"Date",
				"Expires",
				"Pragma",
				"Server",
				"Set-Cookie",
				"Transfer-Encoding",
				"X-Content-Type-Options",
				"X-XSS-Protection",
				"X-Frame-Options",
				"X-Application-Context",
				"X-XSRF-TOKEN",
				"X-XSRF-PARAM"};
		
		/**
		 * CORS <code>maxAge</code> long property
		 */
		private long maxAge = 3600L;

		public String[] getAllowedOrigins() {
			return allowedOrigins;
		}

		public void setAllowedOrigins(String[] allowedOrigins) {
			this.allowedOrigins = allowedOrigins;
		}

		public String[] getAllowedMethods() {
			return allowedMethods;
		}

		public void setAllowedMethods(String[] allowedMethods) {
			this.allowedMethods = allowedMethods;
		}

		public String[] getAllowedHeaders() {
			return allowedHeaders;
		}

		public void setAllowedHeaders(String[] allowedHeaders) {
			this.allowedHeaders = allowedHeaders;
		}

		public String[] getExposedHeaders() {
			return exposedHeaders;
		}

		public void setExposedHeaders(String[] exposedHeaders) {
			this.exposedHeaders = exposedHeaders;
		}

		public long getMaxAge() {
			return maxAge;
		}

		public void setMaxAge(long maxAge) {
			this.maxAge = maxAge;
		}
		
    }