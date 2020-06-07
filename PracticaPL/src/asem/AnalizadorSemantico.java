package asem;

import ast.SentenciaAbstracta;
import ast.E.E;
import ast.I.*;
import errors.GestionErroresTiny;

public class AnalizadorSemantico {
	private SentenciaAbstracta sentenciaRaiz;
	private TablaSimbolos tabla = new TablaSimbolos();
	public AnalizadorSemantico(SentenciaAbstracta sentenciaRaiz) {
		this.sentenciaRaiz = sentenciaRaiz;
	}
	
	
	
	public void vincula(SentenciaAbstracta sentencia) {
		//INSTRUCCION,EXPRESION,CASE,TIPOS, EXPRESION_BINARIA,EXPRESION_UNARIA
		switch(sentencia.tipoSentencia()) {
		
			case INSTRUCCION:
				I instruccion = (I) sentencia;
				switch(instruccion.tipoInstruccion()) {
				case ASIG:
					InstAsignacion asignacion = (InstAsignacion) sentencia;
					vincula(asignacion.getIden());
					vincula(asignacion.getValor());
					break;
				case CALLPROC:
					InstCallProc llamadaProcedimiento = (InstCallProc) sentencia;
					SentenciaAbstracta referenciaDeclaracion = tabla.getSentenciaDeclaracion(llamadaProcedimiento.getNombre_funcion());
					if(referenciaDeclaracion!=null) {
						//habría que guardar la referenciaDeclaracion dentro del objeto InstCallProc
						llamadaProcedimiento.getArgumentos().forEach(x -> vincula(x));
					}else {
						GestionErroresTiny.errorSemantico("El procedimiento " + llamadaProcedimiento.getNombre_funcion() + " no ha sido declarado. Solo se puede llamar a funciones declaradas anteriormente");
					}
					break;
				case DECL:
					//no se realmente que es esa clase. Supongo que para declarar cualquier variable incluso vectores (yo creo que sería mejor separar)
					InstDeclaracion declaracion = (InstDeclaracion) sentencia;
					

					break;
				case DECLFUN:
					InstDeclFun declaracionFuncion = (InstDeclFun) sentencia;
					String tipo = declaracionFuncion.getTipo(); // null si procedimiento
					
					//Lo hace con el tipo de lo que se devuelve
					//vincula(declaracionFuncion.getRet().tipo());
					
					//esto tampoco podemos hacerlo porque nuestros identificadores en realidad no valen para nada
					//tabla.insertaSimbolo(((Iden)declaracionFuncion.getIden())-, sentencia);
					break;
				case IF:
					break;
				case STRUCT:
					break;
				case SWITCH:
					break;
				case WHILE:
					break;
				default:
					break;
				
				}
			
			break;
			
			case EXPRESION_BINARIA:
				
			break;
			case EXPRESION:
			E expresion = (E) sentencia;
				
			break;
			
			case TIPOS:
				
			break;
		}
	}
	
}