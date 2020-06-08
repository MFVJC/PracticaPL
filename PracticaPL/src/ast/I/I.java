package ast.I;

import ast.SentenciaAbstracta;
import ast.I.TipoI;
import ast.T.EnumeradoTipoGeneral;

public abstract class I extends SentenciaAbstracta{
   public abstract TipoI tipoInstruccion(); 
   public I opnd1() {throw new UnsupportedOperationException("opnd1");} 
   public I opnd2() {throw new UnsupportedOperationException("opnd2");} 
   public String num() {throw new UnsupportedOperationException("num");}
   public EnumeradoTipoGeneral tipoSentencia() {
	   return EnumeradoTipoGeneral.INSTRUCCION;
   }
}
