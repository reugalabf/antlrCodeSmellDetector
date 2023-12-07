import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

// https://dev.to/abdelrahmanallam/7-bad-practices-to-avoid-when-writing-sql-queries-for-better-performance-c87

public class SQLCodeSmellDetector {
	public static void main(String[] args) throws Exception {
		// Query del visitor
		String sqlQuery = "SELECT * FROM users;";
		
		ANTLRInputStream input = new ANTLRInputStream(sqlQuery);
		SQLiteLexer lexer = new SQLiteLexer(input);

		CommonTokenStream tokens = new CommonTokenStream(lexer);
		SQLiteParser parser = new SQLiteParser(tokens);

		ParseTree tree = parser.parse();
		SQLSmellDetectorVisitor visitor = new SQLSmellDetectorVisitor();
		visitor.visit(tree);

		// Print arbol
		// System.out.println(tree.toStringTree(parser));

	}
}
