package ast.E;

public class Asterisk extends E{
	E operando1;
	
	public Asterisk(E operando1){
		this.operando1 = operando1;
	}

	@Override
	public TipoE tipoExpresion() {
		return TipoE.ASTERISK;
	}
	public E opnd1() {
		return operando1;
	} 
}
