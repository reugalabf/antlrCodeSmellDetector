import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;

// class para modularizar sobre el parser de ExprContext
public class ExpressionSmellDetector {

	public void detectAll(SQLiteParser.ExprContext ctx) {
		// ... métodos de detección relacionados con ExprContext ...
		detectSubqueriesInSelect(ctx);
		detectLikeWithLeadingWildcard(ctx);
		detectBadSmellCount(ctx);
		detectBadSmellOrIn(ctx);
	}

	boolean detectSubqueriesInSelect(SQLiteParser.ExprContext ctx) {
		// Detectar select en subqueries
		// Recorrer todos los hijos de la expresión
		for (int i = 0; i < ctx.getChildCount(); i++) {
			ParseTree child = ctx.getChild(i);
			// Verificar si el hijo es la clase correspondiente
			// para subconsultas)
			if (child instanceof SQLiteParser.Select_stmtContext) {
				System.out.println("🔴 Bad Practice // Detectado uso de subconsulta en SELECT:\n" + child.getText());
				return true;
			}
		}
		return false;

	}

	boolean detectSubqueriesInFrom(SQLiteParser.Sql_stmt_listContext stmtList) {
		for (SQLiteParser.Sql_stmtContext stmt : stmtList.sql_stmt()) {
			if (stmt.factored_select_stmt() != null) {
				SQLiteParser.Select_coreContext selectCore = stmt.factored_select_stmt().select_core(0);
				if (selectCore != null) {
					for (SQLiteParser.Table_or_subqueryContext tableOrSubquery : selectCore.table_or_subquery()) {
						if (tableOrSubquery.select_stmt() != null) {
							System.out.println("🔴 Bad Practice // Detectado uso de subconsulta en FROM:\n"
									+ tableOrSubquery.select_stmt().getText());
							return true;
						}
					}
				}
			}
		}
		return false;
	}

	boolean detectLikeWithLeadingWildcard(SQLiteParser.ExprContext ctx) {
		// Lógica para detectar LIKE con comodín líder

		// Verifica si el contexto tiene el formato esperado para una expresión LIKE
		if (ctx.getChildCount() == 3 && ctx.getChild(1) instanceof TerminalNode) {
			TerminalNode operatorNode = (TerminalNode) ctx.getChild(1);
			// Comprobar si el operador es LIKE
			if (operatorNode.getSymbol().getType() == SQLiteParser.K_LIKE) {
				// Obtener el lado derecho de la expresión LIKE
				ParseTree right = ctx.getChild(2);

				// Si el lado derecho es una cadena literal
				if (right instanceof SQLiteParser.ExprContext) {
					SQLiteParser.ExprContext rightExpr = (SQLiteParser.ExprContext) right;

					// Obtener el texto de la expresión
					String pattern = rightExpr.getText();

					// Eliminar las comillas al principio y al final del patrón
					if (pattern.startsWith("'") && pattern.endsWith("'")) {
						pattern = pattern.substring(1, pattern.length() - 1);
					}
					// Comprobar si la cadena comienza con '%'
					if (pattern.startsWith("%")) {
						System.out.println(
								"🔴 Bad Practice // Detectado uso de LIKE con comodín líder: " + ctx.getText());
						return true;
					}
				}
			}
		}
		return false;
	}

	boolean detectBadSmellCount(SQLiteParser.ExprContext ctx) {
		// Lógica para detectar COUNT(*)
		// Verifica si la expresión es una llamada a función
		if (ctx.function_name() != null) {
			String functionName = ctx.function_name().getText().toUpperCase();
			// Comprobar si la función es COUNT
			if ("COUNT".equals(functionName)) {
				// Iterar sobre los hijos del contexto de la expresión
				for (int i = 0; i < ctx.getChildCount(); i++) {
					ParseTree child = ctx.getChild(i);
					// Comprobar si algún hijo es un '*'
					if (child instanceof TerminalNode && child.getText().equals("*")) {
						System.out.println("🔴 Bad Practice // Uso ineficiente de COUNT: " + ctx.getText());
						return true;
					}
				}
			}
		}
		return false;
	}

	boolean detectBadSmellOrIn(SQLiteParser.ExprContext ctx) {
		// Lógica para detectar OR, posible IN (,)

		// Verifica si la expresión contiene el operador OR
		if (ctx.K_OR() != null) {
			// Verifica si los lados izquierdo y derecho del OR son comparaciones de
			// igualdad
			if (isSimpleEquality(ctx.expr(0)) && isSimpleEquality(ctx.expr(1))) {
				SQLiteParser.ExprContext left = ctx.expr(0);
				SQLiteParser.ExprContext right = ctx.expr(1);

				// Verificar si ambas comparaciones son sobre la misma columna
				if (sameColumn(left, right)) {
					System.out.println(
							"🔴 Bad Practice // Detectado uso potencialmente ineficiente de OR que podría ser reemplazado por IN: "
									+ ctx.getText());
					return true;
				}
			}
		}
		return false;
	}

	private boolean isSimpleEquality(SQLiteParser.ExprContext expr) {
		// Verifica si la estructura de la expresión es columna = valor
		if (expr.getChildCount() == 3 && expr.getChild(1).getText().equals("=")) {
			String leftSide = expr.getChild(0).getText();
			String rightSide = expr.getChild(2).getText();

			// Verifica si el lado izquierdo parece ser un nombre de columna y el derecho
			// un valor literal
			boolean leftIsColumn = Character.isLetter(leftSide.charAt(0));
			boolean rightIsLiteral = rightSide.startsWith("'") || rightSide.startsWith("\""); // asumiendo que los
																								// valores literales
																								// están entre comillas
			return leftIsColumn && rightIsLiteral;
		}
		return false;
	}

	private boolean sameColumn(SQLiteParser.ExprContext left, SQLiteParser.ExprContext right) {
		// Verifica si ambos lados de la expresión comparan la misma columna
		return left.getChild(0).getText().equals(right.getChild(0).getText());
	}

}
