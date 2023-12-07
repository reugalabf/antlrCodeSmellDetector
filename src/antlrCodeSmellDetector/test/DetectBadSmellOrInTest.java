import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import static org.junit.Assert.*;
import java.util.Collection;
import java.util.Arrays;
import org.antlr.v4.runtime.*;

@RunWith(Parameterized.class)
public class DetectBadSmellOrInTest {
	private ExpressionSmellDetector detector;
	private SQLUtilityExpression sqlUtility;
	private String sqlQuery;
	private boolean expectedDetection;

	public DetectBadSmellOrInTest(String sqlQuery, boolean expectedDetection) {
		this.sqlQuery = sqlQuery;
		this.expectedDetection = expectedDetection;
	}

	@Parameterized.Parameters
	public static Collection<Object[]> sqlQueries() {
		return Arrays.asList(new Object[][] {
				{ "SELECT name, price FROM products WHERE category = 'fruit' OR category = 'dairy';", true },
				{ "SELECT name, price FROM products WHERE category = 'fruit' OR ZipCode = '1900';", false },
				{ "SELECT name, price FROM products WHERE category IN ('fruit', 'dairy') OR ZipCode IN (1900, 2000);",
						false },

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
		return sqlUtility.findExprContextWithOr(parser.parse());
	}

	@Test
	public void testDetectBadSmellOrIn() {
		System.out.println("\n🔵 Test:\n" + sqlQuery + "\n");
		SQLiteParser.ExprContext exprContext = parseSQLStatement(sqlQuery);
		assertNotNull("No se encontró un ExprContext con OR en la consulta SQL", exprContext);
		boolean result = detector.detectBadSmellOrIn(exprContext);
		assertEquals(
				"La detección de OR que podría ser reemplazado por IN no coincidió con la expectativa para la consulta: "
						+ sqlQuery,
				expectedDetection, result);
	}
}
