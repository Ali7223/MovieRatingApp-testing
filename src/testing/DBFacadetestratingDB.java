package testing;

import static org.junit.Assert.assertTrue;

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

public class DBFacadetestratingDB extends TestCase{
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
	
	
	
	
	//checks is the movie already rated by the user before and this method returns true 
	//in our case username 'Ali' rated 'sholay' and its in the database
	
	@Test
	public void testget_ratingReturnsTrue() {
		boolean res=DBFacade.getInstance().get_rating( rr.getUsername(),rr.getMoviename());
		assertEquals(true,res);
		
	}
	

	//this method below will check if the movie is not rated by the user before and it returns false
	
	@Test
	public void testget_ratingReurnsFalse() {
		boolean res=DBFacade.getInstance().get_rating( "Alex",rr.getMoviename());
		assertEquals(false,res);
	}
	
	//Adding rating method is executed after checking if the user has rated the movie before
    //it returns a rating object when the movie is not rated by the user before
	//movies that are presented in the database could only be rated
		
		@Test
		public void testAddingRatingReturnsRatingObject() {
			
			ratingbean rs=DBFacade.getInstance().addingrating("Alex",mr.getName(), 10);
			
			//checking return values
			
			assertTrue(rs.getMoviename().equals(mr.getName()));
			assertTrue(rs.getUsername().equals("Alex"));
			assertTrue(rs.getRating()==10);
			
		}
		
	//Adding rating method returns null when user tries to rate a movie not present in the database
		
		@Test
		
		public void testAddingRatingReturnsNull() {
			
			ratingbean rs=DBFacade.getInstance().addingrating("Alex","movie not in database", 10);
			Assert.assertNull(rs);
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


