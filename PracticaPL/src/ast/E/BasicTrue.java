package ast.E;

import ast.E.TipoE;

public class BasicTrue extends E {
   public BasicTrue() {}     
   public TipoE tipoExpresion() {return TipoE.BASICTRUE;}
   public String toString() {return "{true}";}
}
