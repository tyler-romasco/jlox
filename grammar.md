expression -> literal | grouping | unary | binary; \
literal    -> NUMBER | STRING | "true" | "false" | "nil" ; \
grouping   -> "(" expression ")" \
unary      -> "!'| "-" expression; \
binary     -> expression operator expression; \
operator   ->  "==" | "!=" | "<" | "<=" | ">" | ">="
                | "+"  | "-"  | "*" | "/" ;