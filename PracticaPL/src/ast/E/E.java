package ast.E;

import ast.SentenciaAbstracta;
import ast.T.EnumeradoTipoGeneral;

public abstract class E extends SentenciaAbstracta{
	protected int fila;
	protected int columna;
   public abstract TipoE tipoExpresion(); 
   //hay que refactorizar esta clase y estos métodos
   public E opnd1() {throw new UnsupportedOperationException("opnd1");} 
   public E opnd2() {throw new UnsupportedOperationException("opnd2");} 
   public String num() {throw new UnsupportedOperationException("num");}
   public int getFila() {return fila;}
   public int getColumna() {return columna;}
   public EnumeradoTipoGeneral tipoSentencia(){
	   return EnumeradoTipoGeneral.EXPRESION;
   }
}
