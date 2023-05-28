package com.romasco.tool;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;

public class GenerateAst {
    public static void main(String[] args) throws IOException {
        if(args.length != 1) {
            System.out.println("Usage: generate_ast <output dir>");
            System.exit(64);
        }
        String outputDir = args[0];
        defineAst(outputDir, "Expr", Arrays.asList(
                "Binary : Expr left, Token operator, Expr right",
                "Grouping : Expr expression",
                "Literal : Object literal",
                "Unary : Token operator, Expr right"
        ));
    }

    private static void defineAst(String outputDir, String baseName, List<String> types) throws IOException {
        String path = outputDir + "/" + baseName + ".java";
        PrintWriter writer = new PrintWriter(path);

        writer.println("package com.romasco.lox;");
        writer.println();
        writer.println("import java.util.List;");
        writer.println();
        writer.println("abstract class " + baseName + " {");

        defineVisitor(writer, baseName, types);

        for(String type: types) {
            String[] split = type.split(":");
            String typeName = split[0].trim();
            String fields = split[1].trim();
            defineType(writer, baseName, typeName, fields);
        }

        //abstract visitor accept
        writer.println();
        writer.println("  abstract<R> R accept(Visitor<R> visitor);");

        writer.println("}");
        writer.close();

    }

    private static void defineVisitor(PrintWriter writer, String baseName, List<String> types) {
        writer.println("  interface Visitor<R> {");

        for(String type: types) {
            String name = type.split(":")[0].trim();
            writer.println("    R visit" + name + baseName + "(" + name + " " +  baseName.toLowerCase() + ");");
        }

        writer.println("  }");
    }

    private static void defineType(PrintWriter writer, String baseName, String typeName, String fields) {
        fields = fields.trim();
        String[] fieldStrings = fields.split(", ");

        writer.println("  static class " + typeName + " extends " + baseName + " {");

        // constructor
        writer.println("    public " + typeName + "(" + fields + ") {");
        for(String field: fieldStrings) {
            String fieldName = field.split(" ")[1].trim();
            writer.println("      this." + fieldName + " = " + fieldName + ";");
        }
        writer.println("    }");

        //accept
        writer.println("    @Override");
        writer.println("    <R> R accept(Visitor<R> visitor) {");
        writer.println("      return visitor.visit" + typeName + baseName + "(this);");
        writer.println(    "}");

        writer.println("");

        // fields
        for(String type: fields.split(",")) {
            type = type.trim();
            writer.println("    final " + type + ";");
        }
        writer.println("  }");
    }
}
