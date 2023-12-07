import java.util.function.Predicate;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;

public class SQLUtilityExpression {

	public SQLiteParser.ExprContext findExprContext(ParseTree tree, Predicate<SQLiteParser.ExprContext> predicate) {
		if (tree instanceof SQLiteParser.ExprContext && predicate.test((SQLiteParser.ExprContext) tree)) {
			return (SQLiteParser.ExprContext) tree;
		}

		for (int i = 0; i < tree.getChildCount(); i++) {
			SQLiteParser.ExprContext foundContext = findExprContext(tree.getChild(i), predicate);
			if (foundContext != null) {
				return foundContext;
			}
		}

		return null;
	}

	public SQLiteParser.ExprContext findExprContextWithLike(ParseTree tree) {
		return findExprContext(tree, ctx -> ctx.getChildCount() >= 3 && ctx.getChild(1) instanceof TerminalNode
				&& "LIKE".equalsIgnoreCase(ctx.getChild(1).getText()));
	}

	public SQLiteParser.ExprContext findFirstExprContextWithSelect(ParseTree tree) {
		return findExprContext(tree, ctx -> ctx instanceof SQLiteParser.ExprContext
				&& ctx.children.stream().anyMatch(child -> child instanceof SQLiteParser.Select_stmtContext));
	}

	public SQLiteParser.ExprContext findFirstExprContextWithCount(ParseTree tree) {
		return findExprContext(tree,
				ctx -> ctx.function_name() != null && "COUNT".equalsIgnoreCase(ctx.function_name().getText()));
	}

	public SQLiteParser.ExprContext findExprContextWithOr(ParseTree tree) {
		return findExprContext(tree, ctx -> ctx.K_OR() != null);
	}

	// ...
}
