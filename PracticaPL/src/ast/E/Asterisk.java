package ast.E;

public class Asterisk extends E{
	public Asterisk(E opnd1){
		
	}

	@Override
	public TipoE tipoExpresion() {
		return TipoE.ASTERISK;
	}
}
