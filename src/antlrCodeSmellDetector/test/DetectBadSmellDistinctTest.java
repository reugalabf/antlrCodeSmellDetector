import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Collection;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.ParseTree;

@RunWith(Parameterized.class)
public class DetectBadSmellDistinctTest {
	private SQLSmellDetectorVisitor visitor;

	private String sqlQuery;
	private boolean expectedDetection;

	public DetectBadSmellDistinctTest(String sqlQuery, boolean expectedDetection) {
		this.sqlQuery = sqlQuery;
		this.expectedDetection = expectedDetection;
	}

	@Parameterized.Parameters
	public static Collection<Object[]> sqlQueries() {
		return Arrays.asList(new Object[][] {
				// Casos donde se espera detectar el uso de DISTINCT
				{ "SELECT DISTINCT name FROM users;", true },
				// Posible badSmell
				{ "SELECT DISTINCT name FROM users JOIN orders ON users.id = orders.user_id;", true },
				{ "SELECT DISTINCT employee.name FROM employees JOIN departments ON employees.department_id = departments.id WHERE departments.name = 'Sales';",
						true },
				// Casos donde se espera no detectar problemas con DISTINCT
				{ "SELECT name FROM users;", false }, { "SELECT name FROM users GROUP BY name;", false },

		});
	}

	@Before
	public void setUp() {
		visitor = new SQLSmellDetectorVisitor();
	}

	private SQLiteParser.Select_coreContext parseSQLStatement(String sql) {
		ANTLRInputStream input = new ANTLRInputStream(sql);
		SQLiteLexer lexer = new SQLiteLexer(input);
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		SQLiteParser parser = new SQLiteParser(tokens);
		return findSelectCoreContext(parser.parse());
	}

	public SQLiteParser.Select_coreContext findSelectCoreContext(ParseTree tree) {
		if (tree instanceof SQLiteParser.Select_coreContext) {
			return (SQLiteParser.Select_coreContext) tree;
		}

		for (int i = 0; i < tree.getChildCount(); i++) {
			ParseTree child = tree.getChild(i);
			SQLiteParser.Select_coreContext foundContext = findSelectCoreContext(child);
			if (foundContext != null) {
				return foundContext;
			}
		}

		return null; // No se encontró un Select_coreContext en este camino del árbol
	}

	@Test
	public void testVisitSelectCoreWithDistinctAndJoin() {
		System.out.println("\n🔵 Test:\n" + sqlQuery + "\n");
		SQLiteParser.Select_coreContext selectCoreContext = parseSQLStatement(sqlQuery);
		assertNotNull("No se encontró Select_coreContext en la consulta SQL", selectCoreContext);

		visitor.visitSelect_core(selectCoreContext);
		boolean detectionResult = visitor.hasDetectedDistinct();
		assertEquals("La detección de DISTINCT no coincidió con la expectativa para la consulta: " + sqlQuery,
				expectedDetection, detectionResult);

	}

}
