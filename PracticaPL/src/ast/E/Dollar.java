package ast.E;

public class Dollar extends E{
	E operando1;
	public Dollar(E operando1,int fila,int columna){
		this.operando1 = operando1;
	     this.fila = fila;
	     this.columna = columna;
		
	}

	@Override
	public TipoE tipoExpresion() {
		return TipoE.DOLLAR;
	}
	public E opnd1() {
		return operando1;
	} 
	
   public String toString() {
		  return "{{_Dolar_}" + opnd1().toString() + "}";
   }
	
}
