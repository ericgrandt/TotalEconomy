<sub>[back](sql_proposal.md)</sub>

### SQL queries

I previously deleted the ``SqlManager`` and ``SqlQuery`` classes from TE but ended up creating a new version of them.

### SqlManager

Not much to mention here. The ``SqlManager`` manages the main ``DataSource``, migration detection, table creation and similar stuff.

### SqlQuery

The new ``SqlQuery`` class is a lot closer to the original jdbc ``Statement`` interface. Its purpose is really just reducing nested ``try-with-resources`` blocks and - more importantly - allow the use of named parameters so query strings become much more clear.

Here's just a quick example of how to use it:

```java
// Initialize variables
DataSource myDataSource = totaleconomy.getSqlManager().getDataSource();
String myQueryString = "SELECT * FROM `accounts` WHERE `uid` = :account_uid";
UUID mySampleUUID = UUID.randomUUID();

// Try-with-resources (SqlQuery implements ``AutoClosable``!)
try (SqlQuery myQuery = new SqlQuery(myDataSource, myQueryString)) { 
  myQuery.setParameter("account_uid", mySampleUUID);
  PreparedStatement myStatement = myQuery.getStatement();
  statement.executeQuery();
  ResultSet result = statement.getResultSet();
 
  // ResultSet starts always "before-first" - Is there an actual result?
  if (!result.next()) {
    // No rows
    return;
  }
  // Have rows.
  
} catch (SQLException e) {
  // catch block omitted
}
```

In my opinion this solution is more versatile as it does not abstract the original jdbc objects but manages creating and closing them properly.