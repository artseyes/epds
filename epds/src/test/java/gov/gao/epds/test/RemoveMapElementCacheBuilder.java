/**
 * 
 */
package gov.gao.epds.test;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author MHussaini
 *
 */
public class RemoveMapElementCacheBuilder {

	
	private static Map<String,Object> map = new ConcurrentHashMap<String,Object>();
	
	/*private static LoadingCache<Integer, Map<String, Object>> graphs = 
			
			CacheBuilder.newBuilder()
			    .concurrencyLevel(4)
			    .expireAfterWrite(3, TimeUnit.MINUTES)
			    .build(
			        new CacheLoader<Integer, Map<String, Object>>() {

					@Override
					public Map<String, Object> load(Integer arg0) throws Exception {
						graphs.invalidate(arg0);
						
						return getMap();
					}

			        });
	
	
	public static Map<String, Object> getMap() {
		return map;
	}
	
	
	public static void put(Integer Id,String key, Object value) {
		map.put(key, value);
		
		getGraphs().put(Id, map);
	}
	
	
	public static LoadingCache<Integer, Map<String, Object>> getGraphs() {
		return graphs;
	}*/
	
}


