package com.romasco.lox;

public class AstPrinter implements Expr.Visitor<String> {

    String print(Expr expr) {
        return expr.accept(this);
    }
    @Override
    public String visitBinaryExpr(Expr.Binary expr) {
        return parenthesize(expr.operator.lexeme, expr.left, expr.right);
    }

    @Override
    public String visitGroupingExpr(Expr.Grouping expr) {
        return parenthesize("Grouping", expr.expression);
    }

    @Override
    public String visitLiteralExpr(Expr.Literal expr) {
        if(expr.literal == null) return "nil";
        return expr.literal.toString();
    }

    @Override
    public String visitUnaryExpr(Expr.Unary expr) {
        return parenthesize(expr.operator.lexeme, expr.right);
    }

    private String parenthesize(String name, Expr... exprs) {
        StringBuilder builder = new StringBuilder("(");
        builder.append(name);
        for(Expr expr: exprs) {
            builder.append(" ").append(expr.accept(this));
        }
        builder.append(")");
        return builder.toString();
    }
}
