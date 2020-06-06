package ast.E;

import ast.SentenciaAbstracta;
import ast.T.TipoGeneral;

public abstract class E extends SentenciaAbstracta{
   public abstract TipoE tipo(); 
   public E opnd1() {throw new UnsupportedOperationException("opnd1");} 
   public E opnd2() {throw new UnsupportedOperationException("opnd2");} 
   public String num() {throw new UnsupportedOperationException("num");}
   public TipoGeneral tipoSentencia(){
	   return TipoGeneral.EXPRESION;
   }
}
