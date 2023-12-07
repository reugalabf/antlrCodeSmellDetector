import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import static org.junit.Assert.*;
import java.util.Collection;
import java.util.Arrays;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.ParseTree;



@RunWith(Parameterized.class)
public class DetectSelectStarTest {
    private SQLSmellDetectorVisitor visitor;
    private String sqlQuery;
    private boolean expectedDetection;
    
    public DetectSelectStarTest(String sqlQuery, boolean expectedDetection) {
        this.sqlQuery = sqlQuery;
        this.expectedDetection = expectedDetection;
    }
    
    @Parameterized.Parameters
    public static Collection<Object[]> sqlQueries() {
        return Arrays.asList(new Object[][] {
        	// Casos donde se espera detectar SELECT *
            { "SELECT * FROM users;", true },
            { "SELECT *, name FROM employees;", true },

            // Casos donde no se espera detectar SELECT *
            { "SELECT name FROM users;", false },
            { "SELECT id, name FROM employees;", false },
            { "SELECT u.name FROM users u;", false },
        });
    }

    @Before
    public void setUp() {
        visitor = new SQLSmellDetectorVisitor();
    }

    private SQLiteParser.Result_columnContext parseSQLStatement(String sql) {
        ANTLRInputStream input = new ANTLRInputStream(sql);
        SQLiteLexer lexer = new SQLiteLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        SQLiteParser parser = new SQLiteParser(tokens);
        return findResultColumnContext(parser.parse());
    }
    
    public SQLiteParser.Result_columnContext findResultColumnContext(ParseTree tree) {
        if (tree instanceof SQLiteParser.Result_columnContext) {
            return (SQLiteParser.Result_columnContext) tree;
        }

        for (int i = 0; i < tree.getChildCount(); i++) {
            ParseTree child = tree.getChild(i);
            SQLiteParser.Result_columnContext foundContext = findResultColumnContext(child);
            if (foundContext != null) {
                return foundContext;
            }
        }

        return null; // No se encontró un Result_columnContext en este camino del árbol
    }

    @Test
    public void testDetectSelectStar() {
    	System.out.println("\n🔵 Test:\n" + sqlQuery + "\n");
        SQLiteParser.Result_columnContext resultColumnContext = parseSQLStatement(sqlQuery);
        assertNotNull("No se encontró un Result_columnContext en la consulta SQL", resultColumnContext);

        visitor.visitResult_column(resultColumnContext);
        boolean detectionResult = visitor.hasDetectedSelectStar();
        assertEquals("La detección de SELECT * no coincidió con la expectativa para la consulta: " + sqlQuery,
                     expectedDetection, detectionResult);
    }

}

