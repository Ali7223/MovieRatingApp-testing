package testing;



import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.ArrayList;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import application.MRApp;
import dbadapter.Configuration;
import dbadapter.DBFacade;
import dbadapter.moviebean;
import dbadapter.ratingbean;
import dbadapter.userbean;
import junit.framework.TestCase;

public class DBFacadetestDB extends TestCase{
	

	private userbean usr;
	private moviebean mr;
	private ratingbean rr;

	@Before
	public void setUp() throws Exception {
		usr= new userbean("Ali","saadali",19);
		mr = new moviebean("sholay",java.sql.Date.valueOf("1975-06-06"), "Action", "Amitabh","haath o se begair",0.00f);
		rr= new ratingbean("sholay","Ali", 10);
		
		
		
		//Preparing Sql 
		String sqldelete="DROP TABLE IF EXISTS moviedatabase,userdatabase,rating";
		String sqlcreateratingtable="Create table rating(moviename varchar(100) not null,username varchar(20) not null,rate integer ,foreign key (moviename) references moviedatabase(name),check(rate between 1 and 10))";
		String sqlcreatemovietable="CREATE TABLE moviedatabase (name varchar(50) primary key NOT NULL,released_date date DEFAULT NULL,genre varchar(20) NOT NULL,director varchar(20) NOT NULL,mainActor varchar(20) NOT NULL, avg_rating float DEFAULT NULL)";
		
		String sqlcreateusertable="CREATE TABLE userdatabase (username varchar(20) primary key NOT NULL,email varchar(20) NOT NULL,age int(11) NOT NULL)";
		
		String sqlInsert1="Insert into moviedatabase(name, released_date,genre,director,mainActor,avg_rating) values(?,?,?,?,?,?)";
		String sqlInsert2="Insert into userdatabase(username,email,age) values(?,?,?)";
		String sqlInsert3="Insert into rating(moviename,username,rate) values(?,?,?)";
		
		//preparing statement
		
		
		
		try (Connection connection = DriverManager
				.getConnection(
						"jdbc:" + Configuration.getType() + "://" + Configuration.getServer() + ":"
								+ Configuration.getPort() + "/" + Configuration.getDatabase(),
						Configuration.getUser(), Configuration.getPassword())) {
			try(PreparedStatement ps=connection.prepareStatement(sqldelete)){
				ps.executeUpdate(sqldelete);
			}
            try(PreparedStatement pscreateusertable=connection.prepareStatement(sqlcreateusertable)){
            	pscreateusertable.executeUpdate(sqlcreateusertable);
            }
            try(PreparedStatement pscreatemovietable=connection.prepareStatement(sqlcreatemovietable)){
            	pscreatemovietable.executeUpdate(sqlcreatemovietable);
            }
            try(PreparedStatement pscreateratingtable=connection.prepareStatement(sqlcreateratingtable)){
            	pscreateratingtable.executeUpdate(sqlcreateratingtable);
            }
            try(PreparedStatement psInsert1=connection.prepareStatement(sqlInsert1)){
            	psInsert1.setString(1, mr.getName());
            	psInsert1.setDate(2, mr.getRd_date());
            	psInsert1.setString(3, mr.getGenre());
            	psInsert1.setString(4, mr.getDirector());
            	psInsert1.setString(5, mr.getMain_actor());
            	psInsert1.setFloat(6, mr.getAverage());
            	psInsert1.executeUpdate();
            
            }
            
            try(PreparedStatement psInsert2=connection.prepareStatement(sqlInsert2)){
            	psInsert2.setString(1, usr.getUsername());
            	psInsert2.setString(2, usr.getEmail());
            	psInsert2.setInt(3, usr.getAge());
            	psInsert2.executeUpdate();
            	
            }
            
            try(PreparedStatement psInsert3=connection.prepareStatement(sqlInsert3)){
            	psInsert3.setString(1, rr.getMoviename());
            	psInsert3.setString(2, rr.getUsername());
            	psInsert3.setInt(3, rr.getRating());
            	psInsert3.executeUpdate();
            	
            }
			
	    }catch (Exception e){
		e.printStackTrace();
		
	     }
		
	}
	//test get_username checks if the user already exist in the database if the user exist the method returns false otherwise true
    //we are checking for true case that user does not exist in the database
	
	@Test 
	public void testget_usernameReturnstrue() {
		boolean res=DBFacade.getInstance().get_Username("ALex", "Alex@123", 20);
		assertEquals(true,res);
		
	}
	
	//we are now checking for the false case when user already exist and he tries to register
	
	@Test 
	public void testget_usernameReturnsfalse() {
		boolean res=DBFacade.getInstance().get_Username(usr.getUsername(), usr.getEmail(), usr.getAge());
		assertEquals(false,res);
		
	}
	
	@Test
	public void testcreatingaccount() {
     
		String username="Ali123";
		String email="saad";
		int age=20;
		userbean res=DBFacade.getInstance().CreatingAccount(username, email, age);
		
		//checking condition if its inserted in the database then res shall have the equal values declared above
		assertTrue(username.equals(res.getUsername()));
		assertTrue(email.equals(res.getEmail()));
		assertTrue(age==res.getAge());
		
		
		}
		
	
	//to check that the method returns null when a user tries to create account with same credentials already existing in the database 
	@Test
	public void testCreatingAccoungWithExistingUsername() {
		String username="Ali";
		String email="saadali";
		int age=20;
		userbean res=DBFacade.getInstance().CreatingAccount(username, email, age);
		
		Assert.assertNull(res);
		
		
	}
	
	//before adding a new movie mrapp sends a message to dbfacade with get_addmovie if the movie already present in the database
	//the method returns a movie object if not then it returns null
	@Test
	public void testget_addMovie() {
		//checking with the movie that we inserted in the database while setting up
		moviebean m=DBFacade.getInstance().get_addmovie(mr.getName(), mr.getRd_date(), mr.getGenre(), mr.getDirector(), mr.getMain_actor());
		
		//checking return values to confirm 
		
		assertTrue(m.getName().equals(mr.getName()));
		assertTrue(m.getRd_date().equals(mr.getRd_date()));
		assertTrue(m.getGenre().equals(mr.getGenre()));
		assertTrue(m.getDirector().equals(mr.getDirector()));
		assertTrue(m.getMain_actor().equals(mr.getMain_actor()));
		
	}
 
	//if the movie not present in the data base
	@Test
	public void testAddingMovieReturnsNull() {
		String name="batman";
		java.sql.Date rd_date=java.sql.Date.valueOf("2012-06-04");
		String genre="thriller";
		String director="chrisopher nolan";
		String mainactor="bruce wayne";
		
		moviebean m=DBFacade.getInstance().get_addmovie(name, rd_date, genre, director, mainactor);
		
		Assert.assertNull(m);
		
		
	}
	//AddingMovie is executed when get_addmovie returns null which means that the movie is not present in the database 
    //AddingMovie returns a movie object when the movie update query is executed else returns null
	@Test
	public void testAddingmovieReturnsMovieObject() {
		String name="batman";
		java.sql.Date rd_date=java.sql.Date.valueOf("2012-06-04");
		String genre="thriller";
		String director="chrisopher nolan";
		String mainactor="bruce wayne";
		
		moviebean m=DBFacade.getInstance().Addingmovie(name, rd_date, genre, director, mainactor);
		
		assertTrue(m.getName().equals(name));
		assertTrue(m.getRd_date().equals(rd_date));
		assertTrue(m.getGenre().equals(genre));
		assertTrue(m.getDirector().equals(director));
		assertTrue(m.getMain_actor().equals(mainactor));
	
	}
	
	//when the movie already exists in the database 
	@Test
	public void testAddingMoviereturnnull() {
		moviebean m=DBFacade.getInstance().Addingmovie(mr.getName(), mr.getRd_date(), mr.getGenre(), mr.getDirector(), mr.getMain_actor());
				Assert.assertNull(m);
	}
	
	
	

		
	
	

	//test case for browse movie one movie was added into the database while setting up
	@Test
	public void testsearchmovieContainsOneElement() {
		ArrayList<moviebean> m=MRApp.getInstance().searchmovies();
		
		assertTrue(m.get(0).getName().equals(mr.getName()));
		assertTrue(m.size()==1);
		
	}
	

	@After
	public void tearDown() {

		// SQL statements
		String sqlCleanDB = "DROP TABLE IF EXISTS moviedatabase,userdatabase,rating";

		// Perform database updates
		try (Connection connection = DriverManager
				.getConnection(
						"jdbc:" + Configuration.getType() + "://" + Configuration.getServer() + ":"
								+ Configuration.getPort() + "/" + Configuration.getDatabase(),
						Configuration.getUser(), Configuration.getPassword())) {

			try (PreparedStatement psClean = connection.prepareStatement(sqlCleanDB)) {
				psClean.executeUpdate();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
