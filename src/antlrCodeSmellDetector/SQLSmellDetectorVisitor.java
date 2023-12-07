public class SQLSmellDetectorVisitor extends SQLiteBaseVisitor<Void> {
	private final ExpressionSmellDetector expressionSmellDetector = new ExpressionSmellDetector();
	// Variable para almacenar el resultado de la detección
	private boolean detectedSelectStar = false;
	private boolean detectedHasOrderByWithOutLimit = false;
	private boolean detectedDistinct = false;

	@Override
	public Void visitResult_column(SQLiteParser.Result_columnContext ctx) {
		if (ctx.getText().equals("*")) {
			System.out.println("🔴 Bad Practice // Detectado uso de SELECT *: " + ctx.getText());
			detectedSelectStar = true; // Actualiza la variable si se detecta SELECT *
		}
		return visitChildren(ctx);
	}

	@Override
	public Void visitExpr(SQLiteParser.ExprContext ctx) {
		expressionSmellDetector.detectAll(ctx);
		return visitChildren(ctx); // Continúa visitando el resto del árbol
	}

	@Override
	public Void visitFactored_select_stmt(SQLiteParser.Factored_select_stmtContext ctx) {
		// Verifica si hay una cláusula ORDER BY
		boolean hasOrderBy = ctx.ordering_term() != null && !ctx.ordering_term().isEmpty();

		// Verifica si hay una cláusula LIMIT
		boolean hasLimit = ctx.K_LIMIT() != null;

		// Si hay ORDER BY pero no LIMIT, reportar como mala práctica
		if (hasOrderBy && !hasLimit) {
			detectedHasOrderByWithOutLimit = true;
			System.out.println("🔴 Bad Practice // Uso de ORDER BY sin LIMIT.");
		}

		// Continuar visitando otros nodos
		return visitChildren(ctx);
	}

	@Override
	public Void visitSelect_core(SQLiteParser.Select_coreContext ctx) {
		boolean hasJoins = ctx.join_clause() != null; // Verifica si hay cláusulas de join
		boolean isDistinctUsed = ctx.K_DISTINCT() != null; // Verifica si se usa DISTINCT

		if (isDistinctUsed && hasJoins) {
			detectedDistinct = true;
			// Si hay DISTINCT y joins, podría ser un bad smell
			System.out.println("🔴 Bad Practice // Uso potencialmente problemático de SELECT DISTINCT con joins: "
					+ ctx.getText());
		} else if (isDistinctUsed) {
			detectedDistinct = true;
			// Solo DISTINCT, pero aún así revisar si podría ser un bad smell
			System.out.println("🔴 Bad Practice // Uso de SELECT DISTINCT: " + ctx.getText());
		}

		return visitChildren(ctx);
	}

	public boolean hasDetectedHasOrderByWithOutLimit() {
		return detectedHasOrderByWithOutLimit;
	}

	public boolean hasDetectedDistinct() {
		return detectedDistinct;
	}

	public boolean hasDetectedSelectStar() {
		return detectedSelectStar;
	}

}
