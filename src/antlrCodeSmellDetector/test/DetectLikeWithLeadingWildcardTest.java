
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import java.util.Arrays;
import java.util.Collection;
import org.antlr.v4.runtime.*;

@RunWith(Parameterized.class)
public class DetectLikeWithLeadingWildcardTest {
	private String sqlQuery;
	private boolean expectedDetection;
	private ExpressionSmellDetector detector;
	private SQLUtilityExpression util;

	public DetectLikeWithLeadingWildcardTest(String sqlQuery, boolean expectedDetection) {
		this.sqlQuery = sqlQuery;
		this.expectedDetection = expectedDetection;
	}

	@Parameterized.Parameters
	public static Collection<Object[]> sqlQueries() {
		return Arrays.asList(new Object[][] {
				// Casos donde se espera detectar LIKE con comodín líder
				{ "SELECT * FROM products WHERE name LIKE '%apple%';", true },
				// Casos donde no se espera detectar LIKE con comodín líder
				{ "SELECT * FROM products WHERE name LIKE 'apple%';", false },
				{ "SELECT * FROM products WHERE name LIKE 'appl%e';", false },
		});
	}

	@Before
	public void setUp() {
		detector = new ExpressionSmellDetector();
		util = new SQLUtilityExpression();
	}

	private SQLiteParser.ExprContext parseSQLStatement(String sql) {
		ANTLRInputStream input = new ANTLRInputStream(sql);
		SQLiteLexer lexer = new SQLiteLexer(input);
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		SQLiteParser parser = new SQLiteParser(tokens);
		return util.findExprContextWithLike(parser.parse());
	}

	@Test
	public void testDetectLikeWithLeadingWildcard() {
		System.out.println("\n🔵 Test:\n" + sqlQuery + "\n");
		SQLiteParser.ExprContext exprContext = parseSQLStatement(sqlQuery);
		assertNotNull("Debería haberse encontrado un ExprContext", exprContext);
		boolean result = detector.detectLikeWithLeadingWildcard(exprContext);
		assertEquals(
				"La detección de LIKE con comodín líder no coincidió con la expectativa para la consulta: " + sqlQuery,
				expectedDetection, result);
	}
}
