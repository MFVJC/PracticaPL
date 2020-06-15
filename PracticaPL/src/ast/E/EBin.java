package ast.E;

import ast.T.EnumeradoTipoGeneral;

public abstract class EBin extends E {
   private E opnd1;
   private E opnd2;
   private int fila;
   private int columna;
   public EBin(E opnd1, E opnd2,int fila,int columna) {
     this.opnd1 = opnd1;
     this.opnd2 = opnd2;
     this.fila = fila;
     this.columna = columna;
   }
   public E opnd1() {return opnd1;}
   public E opnd2() {return opnd2;} 
   public EnumeradoTipoGeneral tipoSentencia() {
	   return EnumeradoTipoGeneral.EXPRESION_BINARIA;
   }
}
