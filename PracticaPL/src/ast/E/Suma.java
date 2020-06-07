package ast.E;

import ast.E.TipoE;

public class Suma extends EBin {
   public Suma(E opnd1, E opnd2) {
     super(opnd1,opnd2);  
   }     
   public TipoE tipoExpresion() {return TipoE.SUMA;}
   public String toString() {return "{{__Suma_}" + opnd1().toString() + opnd2().toString() + "}";}
}
