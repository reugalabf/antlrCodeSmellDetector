import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.ParseTree;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Collection;

@RunWith(Parameterized.class)
public class DetectOrderByWithoutLimitTest {
	private SQLSmellDetectorVisitor visitor;
	private String sqlQuery;
	private boolean expectedDetection;

	public DetectOrderByWithoutLimitTest(String sqlQuery, boolean expectedDetection) {
		this.sqlQuery = sqlQuery;
		this.expectedDetection = expectedDetection;
	}

	@Parameterized.Parameters
	public static Collection<Object[]> sqlQueries() {
		return Arrays.asList(new Object[][] {
				// Casos donde se espera detectar el uso de ORDER BY sin LIMIT
				{ "SELECT name FROM users ORDER BY name;", true },
				{ "SELECT id, date FROM orders ORDER BY date DESC;", true },
				{ "SELECT product_name FROM products ORDER BY price ASC;", true },

				// Casos donde no se espera detectar el problema
				{ "SELECT name FROM users;", false }, { "SELECT id, date FROM orders;", false },
				{ "SELECT product_name FROM products WHERE price > 100;", false },
				{ "SELECT name FROM users ORDER BY name LIMIT 10;", false },
				{ "SELECT id FROM orders ORDER BY date DESC LIMIT 5;", false },
				{ "SELECT price, name, category FROM products ORDER BY price ASC LIMIT 20;", false },
		});
	}

	@Before
	public void setUp() {
		visitor = new SQLSmellDetectorVisitor();
	}

	private SQLiteParser.Factored_select_stmtContext parseSelectStatement(String sql) {
		ANTLRInputStream input = new ANTLRInputStream(sql);
		SQLiteLexer lexer = new SQLiteLexer(input);
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		SQLiteParser parser = new SQLiteParser(tokens);
		return findFactoredSelectStmtContext(parser.parse());
	}

	private SQLiteParser.Factored_select_stmtContext findFactoredSelectStmtContext(ParseTree tree) {
		if (tree instanceof SQLiteParser.Factored_select_stmtContext) {
			return (SQLiteParser.Factored_select_stmtContext) tree;
		}

		for (int i = 0; i < tree.getChildCount(); i++) {
			ParseTree child = tree.getChild(i);
			SQLiteParser.Factored_select_stmtContext foundContext = findFactoredSelectStmtContext(child);
			if (foundContext != null) {
				return foundContext;
			}
		}

		return null; // No se encontró un Factored_select_stmtContext en este camino del árbol
	}

	@Test
	public void testDetectOrderByWithoutLimit() {
		System.out.println("\n🔵 Test:\n" + sqlQuery + "\n");
		SQLiteParser.Factored_select_stmtContext selectContext = parseSelectStatement(sqlQuery);
		assertNotNull("No se encontró un Factored_select_stmtContext en la consulta SQL", selectContext);
		visitor.visitFactored_select_stmt(selectContext);
		boolean detectionResult = visitor.hasDetectedHasOrderByWithOutLimit();
		assertEquals("La detección de Order By sin Limit no coincidió con la expectativa para la consulta: " + sqlQuery,
				expectedDetection, detectionResult);

	}

}
