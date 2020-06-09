package ast.E;

public class Dollar extends E{
	E operando1;
	
	public Dollar(E operando1){
		this.operando1 = operando1;
	}

	@Override
	public TipoE tipoExpresion() {
		return TipoE.DOLLAR;
	}
	public E opnd1() {
		return operando1;
	} 
}
