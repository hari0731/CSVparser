import com.opencsv.CSVReader;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang.time.DateUtils;

import java.sql.Connection;
import java.util.Date;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Date;


public class CSVLoader {

    private static final
    String intsert = "INSERT INTO ${table}(${keys}) VALUES(${values})";
    private static final String table = "\\$\\{table\\}";
    private static final String key = "\\$\\{keys\\}";
    private static final String val = "\\$\\{values\\}";

    private Connection connection;
    private char seprator;


    public CSVLoader(Connection connection) {
        this.connection = connection;
        //Set default separator
        this.seprator = ',';
    }

    public void loadCSV(String csvFile, String tableName,
                        boolean truncateBeforeLoad) throws Exception {

        CSVReader csvReader = null;

        try {

            csvReader = new CSVReader(new FileReader(csvFile), this.seprator);

        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception(e.getMessage());
        }

        String[] headerRow = csvReader.readNext();


        String questionmarks = StringUtils.repeat("?,", headerRow.length);
        questionmarks = (String) questionmarks.subSequence(0, questionmarks.length() - 1);

        String query = intsert.replaceFirst(table, tableName);
        query = query.replaceFirst(key, StringUtils.join(headerRow, ","));
        query = query.replaceFirst(val, questionmarks);

        System.out.println("Query: " + query);

        String[] nextLine;
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = this.connection;
            con.setAutoCommit(false);
            ps = con.prepareStatement(query);

            if(truncateBeforeLoad) {
                //delete data from table before loading csv
                con.createStatement().execute("DELETE FROM " + tableName);
            }

            final int batchSize = 1000;
            int count = 0;
            Date date = null;
            while ((nextLine = csvReader.readNext()) != null) {

                if (null != nextLine) {
                    int index = 1;
                    for (String string : nextLine) {

                        if (null != date) {
                            ps.setDate(index++, new java.sql.Date(date.getTime()));
                        } else {
                            ps.setString(index++, string);
                        }
                    }
                    ps.addBatch();
                }
                if (++count % batchSize == 0) {
                    ps.executeBatch();
                }
            }
            ps.executeBatch(); // insert remaining records
            con.commit();
        } catch (Exception e) {
            con.rollback();
            e.printStackTrace();
            throw new Exception(e.getMessage());
        } finally {
            if (null != ps)
                ps.close();
            if (null != con)
                con.close();

            csvReader.close();
        }
    }



}
