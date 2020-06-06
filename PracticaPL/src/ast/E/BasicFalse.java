package ast.E;

import ast.E.TipoE;

public class BasicFalse extends E {
   public BasicFalse() {}     
   public TipoE tipo() {return TipoE.BASICFALSE;}
   public String toString() {return "{false}";}
}
