
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import static org.junit.Assert.assertEquals;
import java.util.Arrays;
import java.util.Collection;
import org.antlr.v4.runtime.*;

@RunWith(Parameterized.class)
public class DetectSubqueriesInSelectTest {
	private String sqlQuery;
	private boolean expectedDetection;
	private ExpressionSmellDetector detector;
	private SQLUtilityExpression util;

	public DetectSubqueriesInSelectTest(String sqlQuery, boolean expectedDetection) {
		this.sqlQuery = sqlQuery;
		this.expectedDetection = expectedDetection;
	}

	@Parameterized.Parameters
	public static Collection<Object[]> sqlQueries() {
		return Arrays.asList(new Object[][] {
				// Casos de prueba con expectativas de bad smell
				{ "SELECT name FROM users WHERE id IN (SELECT user_id FROM orders);", true },
				{ "SELECT department_id, COUNT(*) FROM employees GROUP BY department_id HAVING COUNT(*) > (SELECT COUNT(*) / 10 FROM employees);",
						true },
				{ "SELECT name, (SELECT MAX(salary) FROM employees WHERE department = users.department) AS max_salary FROM users;",
						true },
				{ "SELECT name FROM users WHERE EXISTS (SELECT * FROM orders WHERE orders.user_id = users.id);", true },
				{ "SELECT name, (SELECT AVG(salary) FROM employees WHERE department = users.department), (SELECT COUNT(*) FROM orders WHERE user_id = users.id) FROM users;",
						true },
				{ "SELECT users.name, COUNT(orders.id) AS order_count FROM users JOIN orders ON users.id = orders.user_id GROUP BY users.id;",
						false },
				{ "SELECT users.name, AVG(orders.price) AS avg_order_price FROM users JOIN orders ON users.id = orders.user_id GROUP BY users.id;",
						false },
				{ "SELECT users.name, COUNT(orders.id) AS order_count, SUM(orders.amount) AS total_spent FROM users JOIN orders ON users.id = orders.user_id GROUP BY users.id;",
						false },
				{ "SELECT users.name FROM users JOIN orders ON users.id = orders.user_id WHERE orders.amount > 1000 GROUP BY users.id;",
						false },

				// Casos de prueba con expectativas sin bad smell
				{ "SELECT name, COUNT(order_id) FROM orders GROUP BY user_id;", false },
				{ "SELECT department_id, COUNT(*) FROM employees GROUP BY department_id HAVING COUNT(*) > 5;", false },
				{ "SELECT name, salary FROM users;", false }, { "SELECT name FROM users WHERE id > 1000;", false },
				{ "SELECT name FROM users;", false },
				{ "SELECT users.name, COUNT(orders.id) AS order_count FROM users JOIN orders ON users.id = orders.user_id GROUP BY users.id;",
						false },
				{ "SELECT users.name FROM users JOIN orders ON users.id = orders.user_id WHERE orders.amount > 1000 GROUP BY users.id;",
						false } });
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
		return util.findFirstExprContextWithSelect(parser.parse());
	}

	@Test
	public void testDetectSubqueriesInSelect() {
		System.out.println("\n🔵 Test:\n" + sqlQuery + "\n");

		SQLiteParser.ExprContext exprContext = parseSQLStatement(sqlQuery);
		boolean result = (exprContext != null) && detector.detectSubqueriesInSelect(exprContext);
		assertEquals(
				"La detección de subconsultas en SELECT no coincidió con la expectativa para la consulta: " + sqlQuery,
				expectedDetection, result);
	}
}
