package ningyuan.pan.util.tests;

import static org.junit.Assert.*;

import java.util.Set;

import ningyuan.pan.util.Persistence;

import org.apache.jdbm.DB;
import org.apache.jdbm.DBMaker;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;


public class PersistenceTest {

	private static String CACHE_DB_LOCATION = "./dbtest";
	private String TABLE_NAME = "testtable";
	private static DB db;
	private int size;
	private Set<String> table;
	
	@Before
	public void setUp(){
		db = Persistence.connect(CACHE_DB_LOCATION);
	}
	
	@Test
	public void testConnect() {
		assertNotNull("The files of Database can not be created.", db);
	}
	
	@Test
	public void testAddTable(){
		assertNotNull("files of database can not be created.", db);
		table = db.createHashSet(TABLE_NAME);
		Persistence.close(CACHE_DB_LOCATION);
		db = Persistence.connect(CACHE_DB_LOCATION);
		table = db.getHashSet(TABLE_NAME);
		assertNotNull("Table in database can not be added.", table);
		db.deleteCollection(TABLE_NAME);
	}
	
	@Test
	public void testDelTable(){
		assertNotNull("files of database can not be created.", db);
		table = db.createHashSet(TABLE_NAME);
		Persistence.close(CACHE_DB_LOCATION);
		db = Persistence.connect(CACHE_DB_LOCATION);
		table = db.getHashSet(TABLE_NAME);
		assertNotNull("Table in database can not be added.", table);
		db.deleteCollection(TABLE_NAME);
		Persistence.close(CACHE_DB_LOCATION);
		db = Persistence.connect(CACHE_DB_LOCATION);
		table = db.getHashSet(TABLE_NAME);
		assertNull("Table in database can not be deleted.", table);
	}
	
	@Test
	public void testAddData(){
		String test = "test";
		assertNotNull("files of database can not be created.", db);
		table = db.createHashSet(TABLE_NAME);
		table.add(test);
		Persistence.close(CACHE_DB_LOCATION);
		db = Persistence.connect(CACHE_DB_LOCATION);
		table = db.getHashSet(TABLE_NAME);
		assertNotNull("Table in database can not be added.", table);
		assertTrue("Data can not be added in table", table.contains(test));
		db.deleteCollection(TABLE_NAME);
	}
	
	@Test
	public void testDelData(){
		String test = "test";
		assertNotNull("files of database can not be created.", db);
		table = db.createHashSet(TABLE_NAME);
		table.add(test);
		Persistence.close(CACHE_DB_LOCATION);
		db = Persistence.connect(CACHE_DB_LOCATION);
		table = db.getHashSet(TABLE_NAME);
		assertNotNull("Table in database can not be added.", table);
		assertTrue("Data can not be added in table", table.contains(test));
		table.remove(test);
		Persistence.close(CACHE_DB_LOCATION);
		db = Persistence.connect(CACHE_DB_LOCATION);
		table = db.getHashSet(TABLE_NAME);
		assertFalse("Table in database can not be deleted.", table.contains(test));
		db.deleteCollection(TABLE_NAME);
	}
	
	@After
	public void tearDown(){
		Persistence.close(CACHE_DB_LOCATION);
	}
	
	@AfterClass
	public static void oneTimeTearDown(){
		db = DBMaker.openFile(CACHE_DB_LOCATION)
				.deleteFilesAfterClose()
				.disableTransactions()
				.enableEncryption("password", false)
				.make();
		if(db != null){
			db.close();
		}
	}
}
