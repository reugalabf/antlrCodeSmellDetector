import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import static org.junit.Assert.*;
import java.util.Collection;
import java.util.Arrays;
import org.antlr.v4.runtime.*;


@RunWith(Parameterized.class)
public class DetectBadSmellCountTest {
    private ExpressionSmellDetector detector;
    private SQLUtilityExpression sqlUtility;
    private String sqlQuery;
    private boolean expectedDetection;
    
    public DetectBadSmellCountTest(String sqlQuery, boolean expectedDetection) {
        this.sqlQuery = sqlQuery;
        this.expectedDetection = expectedDetection;
    }
    
    @Parameterized.Parameters
    public static Collection<Object[]> sqlQueries() {
        return Arrays.asList(new Object[][] {
        	{ "SELECT name, (SELECT COUNT(*) FROM orders WHERE user_id = users.id) AS order_count FROM users;", true },
        	{ "SELECT COUNT(*) FROM users;", true },
        	{ "SELECT COUNT(ID) FROM users;", false },
        });
    }

    @Before
    public void setUp() {
        detector = new ExpressionSmellDetector();
        sqlUtility = new SQLUtilityExpression();
    }

    private SQLiteParser.ExprContext parseSQLStatement(String sql) {
        ANTLRInputStream input = new ANTLRInputStream(sql);
        SQLiteLexer lexer = new SQLiteLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        SQLiteParser parser = new SQLiteParser(tokens);
        return sqlUtility.findFirstExprContextWithCount(parser.parse());
    }

    @Test
    public void testDetectBadSmellCount() {
    	System.out.println("\n🔵 Test:\n" + sqlQuery + "\n");
        SQLiteParser.ExprContext exprContext = parseSQLStatement(sqlQuery);

        // ExprContext
        assertNotNull("No se encontró ExprContext con COUNT(*) en la consulta SQL", exprContext);
        boolean result = detector.detectBadSmellCount(exprContext);
        assertEquals("La detección de COUNT(*) no coincidió con la expectativa para la consulta: " + sqlQuery, expectedDetection, result);
    }


}
