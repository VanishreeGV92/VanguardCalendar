package com.calendar.vang;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;
import javax.jdo.Query;
import javax.mail.Session;
import javax.jdo.PersistenceManager;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.calendar.vang.CustomerJDO;
import com.calendar.vang.EventJDO;
import com.calendar.vang.CustomerJDO;
import com.calendar.vang.PMF;
import com.google.gson.Gson;

import com.google.api.server.spi.BackendService.Properties;
import com.google.appengine.api.datastore.Key;
//import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.utils.SystemProperty;

@Controller
@RequestMapping(value = "/users")
public class VcalController {

	private static final Logger log = Logger.getLogger(EventJDO.class.getName());
	//private EventJDO eventJDO;
	
	@RequestMapping(value = "/LoginWithGoogle")
	public ModelAndView go() {
		return new ModelAndView(
				"redirect:https://accounts.google.com/o/oauth2/auth?redirect_uri=http://localhost:8888/users/oauth2callback&response_type=code&client_id=85042476566-ce9on7bakbt6j67g9g4chshet0csfi6g.apps.googleusercontent.com&approval_prompt=force&scope=email&access_type=online");
	}
	
	@RequestMapping(value = "/oauth2callback", method = RequestMethod.GET)
	public ModelAndView getF_authorized_code(HttpServletRequest req,
			HttpServletResponse resp) throws IOException, JSONException{


		// code for getting authorization_code

		String auth_code= req.getParameter("code");
		
		System.out.println(auth_code);

		// Code for getting access token from the authorization_code

		
		URL url = new URL("https://www.googleapis.com/oauth2/v4/token?"
				+ "client_id=85042476566-ce9on7bakbt6j67g9g4chshet0csfi6g.apps.googleusercontent.com"
				+ "&client_secret=ljuO7iI1LnZt9juwCyGAPmT4&" + "redirect_uri=http://localhost:8888/users/oauth2callback&"
				+ "grant_type=authorization_code&" + "code=" + auth_code);
		
		
		HttpURLConnection connect = (HttpURLConnection) url.openConnection();
		connect.setRequestMethod("POST");
		connect.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		connect.setDoOutput(true);
		BufferedReader in = new BufferedReader(new InputStreamReader(connect.getInputStream()));
		String inputLine;
		String response = "";
		while ((inputLine = in.readLine()) != null) {
			response += inputLine;
		}
		in.close();
		System.out.println(response.toString());
		JSONParser parser = new JSONParser();
		JSONObject json_access_token = null;
		try {
			json_access_token = new JSONObject(response);
		} catch (JSONException e) {

			e.printStackTrace();
		}          
		// String access_token="";
		String access_token = null;
		try {
			access_token = (String) json_access_token.get("access_token");
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		System.out.println("Access token =" + access_token);

		// code for getting user details by sending access token.......

		URL obj1 = new URL("https://www.googleapis.com/oauth2/v3/userinfo?access_token=" + access_token);
		HttpURLConnection conn = (HttpURLConnection) obj1.openConnection();
		BufferedReader in1 = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		String inputLine1;
		String responsee = "";
		while ((inputLine1 = in1.readLine()) != null) {
			responsee += inputLine1;
		}
		in1.close();
		System.out.println(responsee.toString());
		JSONObject json_user_details = null;
		try {
			json_user_details = new JSONObject(responsee);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		PrintWriter p=resp.getWriter();
		

		
		HttpSession session=req.getSession();
		System.out.println(json_user_details);
	       session.setAttribute("email",(String) json_user_details.get("email"));
//	        // Get session creation time.
	       Date createTime = new Date(session.getCreationTime());
//	        // Get last access time of this web page.
	      Date lastAccessTime = new Date(session.getLastAccessedTime());
	        session.setAttribute("email",(String) json_user_details.get("email"));
		String userEmail = null;
		String eventTitle = null;
		String eventSday = null;
		String eventTime = null;
		//String userName = null;
		try {
				userEmail = (String) json_user_details.get("email");
				eventTitle = (String) json_user_details.get("title");
				eventSday = (String) json_user_details.get("startday");
				eventTime = (String) json_user_details.get("eventtime");
			//userName = (String) json_user_details.get("name");
		} catch (JSONException e) {		// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		
	

		

	
	

		PersistenceManager pmf = PMF.get().getPersistenceManager();
		if (userEmail != null) {
			Query user = pmf.newQuery(CustomerJDO.class);
			user.setFilter(" email == '" + userEmail + "'");
			
			
			@SuppressWarnings("unchecked")
			List<CustomerJDO> CustomerJDOData = (List<CustomerJDO>) user.execute();
				if (!(CustomerJDOData.isEmpty()))
			{
					System.out.println("to prevent from null");
			}

				else {
					CustomerJDO objPojo = new CustomerJDO();
				objPojo.setEmail(userEmail);
			


				pmf.makePersistent(objPojo);
			}		}
		
		
		
			
		
		
		PrintWriter pp=resp.getWriter();
		pp.println("Welcome,"+json_user_details.get("email")+"!");
		//pp.println("Username is"+json_user_details.get("name"));
 // System.out.println("This is the end of the code");

  return new ModelAndView("welcome.jsp?mail=" + json_user_details.get("email"));
 
  
  
      
      
     
	}
  
//	@RequestMapping(value="sendConMail")
//	public void doPost(HttpServletRequest request,
//			HttpServletResponse response) throws IOException {
//		
//		String email  = request.getParameter("email");
//		Properties props = new Properties(0, email);
//		Session session =Session.getInstance(null);
//		String body = "Hi, your event is successfully created! \n" +Message;
//		
//	}


	
	
	
    
//	return new ModelAndView(
//			"welcome.jsp?mail=" + json_user_details.get("email") + "&username=" + json_user_details.get("name"));
/*
*/

//	 @RequestMapping(value = "/SendConfirmationmationEmail")
//	  	public void doPost(HttpServletRequest reque,
//	  			HttpServletResponse respen) throws IOException{
//	  		
//	  		String email  = reque.getParameter("email");
//	  				
//	  		String confimationMessage = reque.getParameter("Message");
//	  		Properties props = new Properties();
//	  	    Session session =Session.getDefaultInstance(props, null);
//	  	    
//	  	    String body = "Hi, your event is successfully created! \n" +Message;
//	  	    
//	  	}
//	
	
	
	@RequestMapping(value = "/LoginWithFacebook")
	public ModelAndView goF() {
		System.out.println("The Login Facebook get method is calling");
		return new ModelAndView(
				"redirect:https://www.facebook.com/dialog/oauth?client_id=878442045640711&redirect_uri=http://localhost:8888/users/fbhome&scope=email");
	}
	@RequestMapping(value = "/fbhome", method = RequestMethod.GET)
	public ModelAndView getF_authorization_code(HttpServletRequest request,
			HttpServletResponse response) throws IOException, JSONException{
	
		System.out.println("This si the fbhome is calling");
		String outputString = ""; 
		CustomerJDO fbp = null;
	    try
	    {
	      String rid = request.getParameter("request_ids");
	      if (rid != null)
	      {
	        response.sendRedirect("https://www.facebook.com/dialog/oauth?client_id=878442045640711&redirect_uri=http://localhost:8888/users/fbhome");
	      }
	      else
	      {
	        String code = request.getParameter("code");
	        if (code != null)
	        {
	          URL url = new URL(
	            "https://graph.facebook.com/oauth/access_token?client_id=878442045640711&redirect_uri=http://localhost:8888/users/fbhome&client_secret=5b59120f0d1a964830cf5a2281812326&code=" + 
	            code);
	          HttpURLConnection conn = (HttpURLConnection)url
	            .openConnection();
	          conn.setRequestMethod("POST");
	          conn.setConnectTimeout(20000);
	          
	          BufferedReader reader = new BufferedReader(
	            new InputStreamReader(conn.getInputStream()));
	          String line;
	          while ((line = reader.readLine()) != null)
	          {
	            outputString = outputString + line;
	          }
	          System.out.println(outputString);
	          String accessToken = null;
	          if (outputString.indexOf("access_token") != -1)
	          {
	            accessToken = outputString.substring(15, outputString.indexOf("&"));
	          }
	          System.out.println(accessToken);
	          url = new URL("https://graph.facebook.com/me?access_token=" +accessToken);
	          System.out.println(url);
	          URLConnection conn1 = url.openConnection();
	          conn1.setConnectTimeout(7000);
	          outputString = "";
	          reader = new BufferedReader(new InputStreamReader(
	            conn1.getInputStream()));
	          while ((line = reader.readLine()) != null) {
	            outputString = outputString + line;
	            
	          }
	          System.out.println("This is reader data"+reader);
	          reader.close();
	          System.out.println("This is output String"+outputString);
	          fbp = (CustomerJDO)new Gson().fromJson(outputString, 
	            CustomerJDO.class);
	          System.out.println(fbp);
	          request.setAttribute("auth", fbp);

	          System.out.println(fbp.getUname());
	          
	          
	          
	          PersistenceManager pm = PMF.get().getPersistenceManager();
	if (fbp.getUname() != null) {
		Query q = pm.newQuery(CustomerJDO.class);
	
	  			q.setFilter(" email == '" + fbp.getUname() + "'");
  			@SuppressWarnings("unchecked")
 			List<CustomerJDO> CustomerJDOData = (List<CustomerJDO>) q.execute();
  			if (!(CustomerJDOData.isEmpty()))
	  			{
  				System.out.println("to prevent from null");
	  			}

  			else {
  				CustomerJDO objPojo = new CustomerJDO();
  				objPojo.setEmail(fbp.getUname());

	  				pm.makePersistent(objPojo);
	  			}
	  		}
	        } 
	      }
	    }
	    catch (Exception e)
	    {
	      e.printStackTrace();
	    }
	    System.out.println(fbp.getUname());
//	    
//	    PersistenceManager p1 = PMF.get().getPersistenceManager();
//		if (fbp.getUname() != null) {
//			Query ue = p1.newQuery(EventJDO.class);
//		
//		  			ue.setFilter(" email == '" + fbp.getUname() + "'");
//	  			@SuppressWarnings("unchecked")
//	 			List<EventJDO> EventJDOData = (List<EventJDO>) ue.execute();
//	  			if (!(EventJDOData.isEmpty()))
//		  			{
//	  				System.out.println("to prevent from null");
//		  			}
//
//	  			else {
//	  				CustomerJDO objPojo = new CustomerJDO();
//	  				objPojo.setEmail(fbp.getUname());
//
//		  				pm.makePersistent(objPojo);
//		  			}
//		  		}
//		        } 
//		      }
//		    }
//		    catch (Exception e)
//		    {
//		      e.printStackTrace();
//		    }
//		    System.out.println(fbp.getUname());
	    

		PrintWriter pp=response.getWriter();
		pp.println("Email ID is"+fbp.getEmail());
		//pp.println("Username is"+fbp.getUname());
  System.out.println("This is the end of the code");
  
	   //return new ModelAndView("welcome.jsp?mail=" + fbp.getEmail());
	

	
    		/*
    		 * private void TransferFunds(Key fromKey, Key toKey, long amount)
{
    using (var transaction = _db.BeginTransaction())
    {
        var entities = transaction.Lookup(fromKey, toKey);
        entities[0]["balance"].IntegerValue -= amount;
        entities[1]["balance"].IntegerValue += amount;
        transaction.Update(entities);
        transaction.Commit();
    }
}
    		 */
  return new ModelAndView("welcome.jsp?mail="  + fbp.getUname());
}
	
	
	
	@RequestMapping(value = "/save", method = RequestMethod.GET)
	public void LandingPage(ModelMap model,HttpServletRequest re, HttpServletResponse rep) throws IOException {
		 System.out.println("All the datas are sending from backend to front end.... running");
		 String retVal ="";

		 PersistenceManager pm= PMF.get().getPersistenceManager();

			HashMap<String,Object> response = new HashMap<String,Object>();
		response.put("status", false);
			 HttpSession session=re.getSession(false);
			 if(session!=null){
			        String name=(String)session.getAttribute("email");
			        System.out.println(name+"This is the session data email");
		      
			        Query q	=	pm.newQuery("SELECT FROM " + EventJDO.class.getName() + " WHERE email =='"+name+"'");

			       
				
							 List<EventJDO> EventJDOData = (List<EventJDO>) q.execute();
					if(EventJDOData.size()>0)
				{
					Gson obj = new Gson();
					retVal = obj.toJson(EventJDOData);
					response.put("status", true);
					response.put("EventJDOData", retVal);
					}
			 }

				//q.setOrdering("date desc");

				rep.getWriter().write(retVal);
				//return retVal;

	}

	@RequestMapping(value = "/save", method = RequestMethod.POST)
	public void SavingData(@RequestParam("title") String title,@RequestParam("start") String start,@RequestParam("end") String end, ModelMap model,HttpServletRequest req, HttpServletResponse resp) throws IOException {

	 System.out.println("Saving new User's event");

//		 System.out.println(body);
		 HttpSession session=req.getSession(false);
		 if(session!=null){
		        String name=(String)session.getAttribute("email");
		        System.out.println(name+"This is Adding  part email accessing data");
		 }
		 
		PersistenceManager pm= PMF.get().getPersistenceManager();
			Gson gson = new Gson();
			JSONObject JSON = null;
			try {
				 if(session!=null){
				        String name=(String)session.getAttribute("email");
				        System.out.println(name+"This is Adding  part email accessing data");

//				JSON = new JSONObject(body);
				
//				String title1=JSON.getString("title");
//				String startday=JSON.getString("start");
//				String eventtime=JSON.getString("end");
				//start=19-05-2017&end=6%3A30am&title=

      System.out.println(title);
      System.out.println(start);
      System.out.println(end);

				// Persisting new Phone details
				EventJDO r=new EventJDO();
				r.setTitle(title);
			r.setStartday(start);
			r.setEventtime(end);
				r.setEmail(name);

		        pm.makePersistent(r);

		        String s = "Succcessfully Event created!";
				System.out.println(s);
				resp.getWriter().write("");
				 }
			}
			finally {
				pm.close();
			}


	}


	@RequestMapping(method=RequestMethod.GET, value="/{id}")
    @ResponseBody
	 public String editing(HttpServletRequest request,HttpServletResponse resp, ModelMap model,@PathVariable String id) throws IOException {
    		PersistenceManager pm= PMF.get().getPersistenceManager();


   		System.out.println("Update Get is calling");
   		long key = Long.parseLong(id);
			Query q = pm.newQuery(EventJDO.class);
		 q.setFilter("id == idParameter");
			//q.setFilter("name == nameParameter");
			//q.setFilter("id == idParameter"+"&&"+"id == '"+id+"'");
			q.declareParameters("String idParameter");
			List<EventJDO> list= (List<EventJDO>) q.execute(key);
			EventJDO	UDobj	=	(EventJDO)list.get(0);

			Gson obj = new Gson();
			String retVal = obj.toJson(UDobj);

			return retVal;
	}
	 @RequestMapping(method=RequestMethod.PUT, value="/{id}")
	 public void updateEntry(@RequestBody String data, HttpServletRequest request, HttpServletResponse resp,@PathVariable String id) throws IOException{

	 		String k=id;
	 		long key=Long.parseLong(k);
	 		PersistenceManager pm= PMF.get().getPersistenceManager();


	 	HttpSession session=request.getSession(false);
	 		 if(session!=null){
	 		        String name=(String)session.getAttribute("email");
	 		        System.out.println(name+"This is Update part email accessing data");
	 		 }

	 		    Key key1=KeyFactory.stringToKey(k);
	 		    JSONObject JSON = null;
	 			try {
	 				JSON = new JSONObject(data);
	 			String title=JSON.getString("title");
	 			String startday=JSON.getString("startday");
	 				String eventtime=JSON.getString("eventtime");
	 				


	 				EventJDO US = pm.getObjectById(EventJDO.class, key1);
	 				US.setTitle(title);
	 				US.setStartday(startday);
	 				US.setEventtime(eventtime);

	 			resp.getWriter().write(data);



	 			} catch (JSONException e) {
	 				// TODO Auto-generated catch block
	 				e.printStackTrace();
	 		}
	 			}

   @RequestMapping(method=RequestMethod.DELETE, value="/{id}")
		   public void deleteData(HttpServletRequest request,HttpServletResponse resp, @PathVariable String id) throws IOException {



    	String k=id;
		long key=Long.parseLong(k);

    	try{

	    	PersistenceManager pm = PMF.get().getPersistenceManager();

	    	//EventJDO c = pm.get
    		EventJDO c = pm.getObjectById(EventJDO.class, key);
    		pm.deletePersistent(c);
   	}
	catch(Exception e){
System.out.println(e);
    	}
   	resp.getWriter().write(k);
		 }
    @RequestMapping(value = "/Logout")
    public void Logout(HttpServletRequest request,HttpServletResponse resp) throws IOException {


			HttpSession session=request.getSession();
			session.invalidate();
		 resp.sendRedirect("../../index.html");
		 }
	
}