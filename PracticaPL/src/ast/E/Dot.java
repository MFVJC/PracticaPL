package ast.E;

public class Dot extends EBin {
   public Dot(E opnd1, E opnd2) {
	     super(opnd1,opnd2);  
   }     
   public TipoE tipo() {return TipoE.DOT;}
   public String toString() {
		  return "{{__Dot__}" + opnd1().toString() + opnd2().toString() + "}";
   }
}
