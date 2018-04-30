// DBconnect.cpp : Defines the entry point for the console application.
//

#include "stdafx.h"
#include <exception> 

#include "mysql_connection.h"

#include <cppconn/driver.h>
#include <cppconn/exception.h>
#include <cppconn/resultset.h>
#include <cppconn/statement.h>

using namespace std;

void TestQuery() 
{
	sql::Driver *driver;
	sql::Connection *con;
	sql::Statement *stmt;
	sql::ResultSet *res;

	try {
		driver = get_driver_instance();
		std::cout << "majVer: " << driver->getMajorVersion() << ", mimVer: " << driver->getMinorVersion() << endl;

		cout << endl;

		con = driver->connect("tcp://127.0.0.1:3306", "root", "Yura@MySQL");

		/* Connect to the MySQL test database */
		con->setSchema("test_schema");
		stmt = con->createStatement();

		res = stmt->executeQuery("SELECT id, val FROM table1;");
		while (res->next()) {
			cout << "id: " << res->getInt(1) << ", val:" << res->getString(2) << endl;
		}
		delete res;
		delete stmt;
		delete con;

	}
	catch (sql::SQLException &e) {
		cout << "# ERR: SQLException in " << __FILE__;
		cout << "(" << __FUNCTION__ << ") on line " << __LINE__ << endl;
		cout << "# ERR: " << e.what();
		cout << " (MySQL error code: " << e.getErrorCode();
		cout << ", SQLState: " << e.getSQLState() << " )" << endl;
	}

	cout << endl;

}

void TestConnection() 
{
	sql::Driver *driver;
	sql::Connection *con;
	sql::Statement *stmt;
	sql::ResultSet *res;

	try {
		driver = get_driver_instance();
		std::cout << "majVer: " << driver->getMajorVersion() << ", mimVer: " << driver->getMinorVersion();

		con = driver->connect("tcp://127.0.0.1:3306", "root", "Yura@MySQL");

		/* Connect to the MySQL test database */
		con->setSchema("test_schema");
		stmt = con->createStatement();
		res = stmt->executeQuery("SELECT 'Hello World!' AS _message");
		while (res->next()) {
			cout << "\t... MySQL replies: ";
			/* Access column data by alias or column name */
			cout << res->getString("_message") << endl;
			cout << "\t... MySQL says it again: ";
			/* Access column data by numeric offset, 1 is the first column */
			cout << res->getString(1) << endl;
		}
		delete res;
		delete stmt;
		delete con;

	}
	catch (sql::SQLException &e) {
		cout << "# ERR: SQLException in " << __FILE__;
		cout << "(" << __FUNCTION__ << ") on line " << __LINE__ << endl;
		cout << "# ERR: " << e.what();
		cout << " (MySQL error code: " << e.getErrorCode();
		cout << ", SQLState: " << e.getSQLState() << " )" << endl;
	}

	cout << endl;
}

int main()
{
	cout << "TestConnection()";
	TestConnection();

	cout << endl;

	cout << "TestQuery()";
	TestQuery();

	cin.get();
	return 0;
}