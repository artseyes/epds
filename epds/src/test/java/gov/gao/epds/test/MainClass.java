package gov.gao.epds.test;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.Seconds;

/**
 * Basic symmetric encryption example
 */
public class MainClass {
  
	
	private volatile static String  variable = "";
	public static void main(String[] args) throws Exception {
		
		
		
		
		
		
		DateTime lastActiveRequest = new DateTime();
		
		DateTime thirtySecsAgo = lastActiveRequest.minusSeconds(30);
		
		System.out.println(Seconds.secondsBetween(thirtySecsAgo, lastActiveRequest).getSeconds());
		
		
		
		
		/*
	  
	  
	  
	 
	  for (int i =8; i<20; i++){
		  String str = String.valueOf(i) + ".dec";
		 
		  if(str.lastIndexOf(".") != -1){
			  String nonDecimalPart = StringUtils.leftPad(str.substring(0,str.lastIndexOf(".")), 2,'0'),
					  decimalPart = str.substring(str.lastIndexOf(".") + 1);
			  
			  str = nonDecimalPart  + "." + decimalPart;
		  }else if(str.length() == 1){
			  str = StringUtils.leftPad(str, 2,'0'); 
		  }

		  System.out.println(str);
		  
		  
	  }
	  String currentTimeStamp = new SimpleDateFormat("yyyyMMddhhmmss").format(new Date());
	  
	  System.out.println(currentTimeStamp);
	  String[] arrayOfLastParentANos = {"A-125" ,"A-124","A-123"};

		// to make sure there is not repeatition of a_no
		Set<String> setOfLastParentAnos = new TreeSet<String>(Arrays.asList(arrayOfLastParentANos));
		setOfLastParentAnos.add("A-126");
		
		String newLastParentANum = StringUtils.join(setOfLastParentAnos, ";");
		
		System.out.println(newLastParentANum);
	  
	  List<String> stringList = new ArrayList<String>();
	  
	  stringList.add("a");
	  stringList.add("b");
	  stringList.add("c");
	  
	  for (int i=0; i< stringList.size();i++){
		  String each = stringList.get(i);
		  
		  if (each.equalsIgnoreCase("c")){
			  stringList.remove(i--);
		  }
	  }
	  
	  System.out.println();
	  
	  Pattern p = Pattern.compile("[\\p{Alnum}\\p{Punct}]");

      Matcher m = p.matcher("One day! when I was walking. I found your pants? just kidding.......");
      
      int count = 0;
     
      while (m.find()) {
    	  
    	  
          count++;
          System.out.println("\nMatch number: " + count);
          System.out.println("start() : " + m.start());
          System.out.println("end()   : " + m.end());
          System.out.println("group() : " + m.group());
      }
	  
	  String patternToMatch = "[\\\\!#$%*+:;?~]+";
	  Pattern p = Pattern.compile(patternToMatch);
	  Matcher m = p.matcher("Password! # $ - % = + : ; ? ~");
	  boolean characterFound = m.find();
	  int count = 0;
	  
	  if (characterFound) {
          count++;
          System.out.println("\nMatch number: " + count);
          System.out.println("start() : " + m.start());
          System.out.println("end()   : " + m.end());
          System.out.println("group() : " + m.group());
      }
	  
  */
		
	List<String> testJoinedString = new ArrayList<String>();
	testJoinedString.add("A,1");
	testJoinedString.add("B,2");
	testJoinedString.add("C,3");
	
	System.out.println(StringUtils.join(testJoinedString,";"));
	}

  
  
  
  public static Date addBusinessDays(Date date, int days) {

	  	DateTime result = new DateTime(date);
	   
	  	result =  result.plusDays(days);
	    while (isWeekEnd(result)) {
	    	result = result.plusDays(1);
	    }
        
	    return result.toDate();
	}
  
  private static DateTime getPreviousBusinessDate(DateTime result) {
	    while (isWeekEnd(result)) {
	        result = result.minusDays(1);
	    }

	    return result;
	}
  
  private static boolean isWeekEnd(DateTime dateTime) {
	    int dayOfWeek = dateTime.getDayOfWeek();
	    return dayOfWeek == DateTimeConstants.SATURDAY || dayOfWeek == DateTimeConstants.SUNDAY;
	}
  
}