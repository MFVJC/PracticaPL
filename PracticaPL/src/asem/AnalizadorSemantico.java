package asem;

import java.util.List;

import ast.SentenciaAbstracta;
import ast.E.*;
import ast.I.*;
import ast.T.*;
import errors.GestionErroresTiny;
import javafx.util.Pair;

public class AnalizadorSemantico {
	private List<I> programa;
	private TablaSimbolos tabla = new TablaSimbolos();
	public AnalizadorSemantico(List<I> programa) {
		this.programa = programa;
	}
	
	public void analizaSemantica() {
		
	}
	
	public void vincula(SentenciaAbstracta sentencia) {
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
					InstIf instIf = (InstIf) sentencia;
					vincula(instIf.getCondicion());
					tabla.nuevaTablaSimbolos();
					instIf.getCuerpo_if().forEach(x->vincula(x));
					tabla.eliminaTablaSimbolos();
					List<I> cuerpoElse = instIf.getCuerpo_else();
					if(cuerpoElse != null) {
						tabla.nuevaTablaSimbolos();
						instIf.getCuerpo_else().forEach(x->vincula(x));
						tabla.eliminaTablaSimbolos();
					}
					break;
				case STRUCT:
					InstStruct instStruct = (InstStruct) sentencia;
					//faltaría meter la referencia a la sentencia abstracta
					tabla.insertaSimbolo(((Iden) instStruct.getIden()).getNombre(), instStruct);
					tabla.nuevaTablaSimbolos();
					instStruct.getDeclaraciones().forEach(x->vincula(x));
					tabla.eliminaTablaSimbolos();
					break;
				case SWITCH:
					InstSwitch instSwitch = (InstSwitch) sentencia;
					//aquí con la condicion cogemos la referencia de la tabla de símbolos
					SentenciaAbstracta referenciaVariableSwitch = tabla.getSentenciaDeclaracion(((Iden)instSwitch.getCondicion()).getNombre());
					if(referenciaVariableSwitch == null) {
						GestionErroresTiny.errorSemantico("La variable " + ((Iden)instSwitch.getCondicion()).getNombre() + " no ha sido declarada");
					}
					List<Pair<E, List<I>>> casos = instSwitch.getCases();
					for(Pair<E, List<I>> caso : casos) {
						tabla.nuevaTablaSimbolos();
						caso.getValue().forEach(x->vincula(x));
						tabla.eliminaTablaSimbolos();
					}
					//Si no hacemos los cases vamos a perder la información de la SentenciaAbstracta correspondiente al case.
					break;
				case WHILE:
					InstWhile instWhile = (InstWhile) sentencia;
					vincula(instWhile.getCondicion()); // así veo el tipo
					tabla.nuevaTablaSimbolos();
					instWhile.getCuerpo().forEach(x-> vincula(x));
					tabla.eliminaTablaSimbolos();
					break;
				default:
					break;
				
				}
			
			break;
			
			case EXPRESION_BINARIA:
				EBin expresionBinaria = (EBin) sentencia;
					//en el caso de que sea un punto solo tengo que vincular el primer operando
					vincula(expresionBinaria.opnd1());
					if(expresionBinaria.tipoExpresion() != TipoE.DOT) {
						vincula(expresionBinaria.opnd2());
					}
			break;
			case EXPRESION:
			E expresion = (E) sentencia;
				
			switch(expresion.tipoExpresion()) {
			case FUNCION:
				break;
			case IDEN:
				break;
			case NOT:
				Not expNot = (Not) expresion;
				vincula(expNot.opnd1());
				break;
			case ASTERISK:
				Asterisk asterisk = (Asterisk) expresion;
				vincula(asterisk.opnd1());
			break;
			default:
				break;
			
			}
			break;
			
			case TIPOS:
				Tipo tipo = (Tipo) sentencia;
				switch(tipo.tipoEnumerado()) {
				case PUNTERO:
					vincula(((TipoPuntero)tipo).getClaseApuntada());
					break;
				case STRUCT:
					TipoStruct tipoStruct = (TipoStruct) tipo;
					SentenciaAbstracta referenciaSentencia = tabla.getSentenciaDeclaracion(tipoStruct.getNombreStruct());
					if(referenciaSentencia == null) {
						GestionErroresTiny.errorSemantico("Struct " + tipoStruct.getNombreStruct() + " no declarado.");
					}else {
						tipoStruct.setReferencia(referenciaSentencia);
						//guardo la referencia a la sentencia en la que se declaró dentro del nodo
					}
					
					break;
				default:
					break;
				}
			break;
		case EXPRESION_UNARIA:
			break;
		default:
			break;
		}
	}
	
	//hay que añadir tipo como atributo de InstDeclaracion InstDeclFun y demás para guardar la referencia del tipo
	public boolean compruebaTipos(SentenciaAbstracta sentencia) {
		if(sentencia.tipoSentencia() == EnumeradoTipoGeneral.INSTRUCCION) { //se comprueban instrucciones en esta función
			I instruccion = (I) sentencia;
			switch(instruccion.tipoInstruccion()) {
			case ASIG:
				InstAsignacion instruccionAsignacion = (InstAsignacion) instruccion;
				//hay que comprobar que la variable a la que intentas acceder no es constante
				Tipo tipoAsignar = tiposExpresion(instruccionAsignacion.getValor());
				
				
				
				
				break;
			case CALLPROC:
				break;
			case DECL:
				break;
			case DECLFUN:
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
			
		}
		
		
		return false;
	}
public Tipo tiposExpresion(SentenciaAbstracta sentencia) {
	switch(sentencia.tipoSentencia()) {
	case EXPRESION_BINARIA:
		EBin ebin = (EBin) sentencia;
		E operando1= ebin.opnd1();
		E operando2 = ebin.opnd2();
		Tipo tipoOperando1 = tiposExpresion(operando1);
		Tipo tipoOperando2 = tiposExpresion(operando2);
		switch(ebin.tipoExpresion()) {
		case AND:
			if(tipoOperando1.tipoEnumerado()==EnumeradoTipos.BOOLEAN && tipoOperando2.tipoEnumerado()==EnumeradoTipos.BOOLEAN) {
				//los dos operandos son booleanos entonces devolvemos un booleano
				return new TipoBoolean();
			}
			GestionErroresTiny.errorSemantico("Error de tipos. Uno de los operandos del AND no es booleano");
			break;
		case DIV:
			if(tipoOperando1.tipoEnumerado()==EnumeradoTipos.INT && tipoOperando2.tipoEnumerado()==EnumeradoTipos.INT){
				return new TipoInt();
			}
			GestionErroresTiny.errorSemantico("Error de tipos. Uno de los operandos de la división no es entero");

			break;
		case DOT:
			//tenemos que comprobar que operando1 es un struct
			//
			if(tipoOperando1.tipoEnumerado() == EnumeradoTipos.STRUCT) {
				//falta comprobar que el punto corresponde con un campo del struct
				Iden atributo = (Iden) ebin.opnd2();
				//podemos coger la referencia al struct del TipoStruct
				 //nos hace falta meter las referencias para esto
				TipoStruct tipoStruct = (TipoStruct) tipoOperando1;
				InstStruct sentenciaStruct = (InstStruct) tipoStruct.getReferencia();
				for(I instruccion : sentenciaStruct.getDeclaraciones()) {
					if(instruccion instanceof InstDeclaracion) {
						Iden atributoStruct = (Iden)((InstDeclaracion) instruccion).getIden();
						if(atributoStruct.getNombre() == atributo.getNombre()) {
							//Entonces si que existe la variable
							return atributoStruct.getTipo();
						}
					}
				}
			}
				GestionErroresTiny.errorSemantico("Error de tipos. Tipo de operandos inválido para el .");
			
			
			break;
		case ELEV:
			if(tipoOperando1.tipoEnumerado()==EnumeradoTipos.INT && tipoOperando2.tipoEnumerado()==EnumeradoTipos.INT){
				return new TipoInt();
			}
			GestionErroresTiny.errorSemantico("Error de tipos. Tipo de operandos inválido para el operador **");

			break;
		case EQUAL:
			//podemos tener booleanos o enteros
			if((tipoOperando1.tipoEnumerado() == EnumeradoTipos.BOOLEAN || tipoOperando1.tipoEnumerado() == EnumeradoTipos.INT) && tipoOperando1.tipoEnumerado() == tipoOperando2.tipoEnumerado() ) {
				return new TipoBoolean();
			}
			GestionErroresTiny.errorSemantico("Error de tipos. Los tipos para la comparación de igualdad no coinciden o no son válidos");
			break;
		case GREATEREQUAL:
			if(tipoOperando1.tipoEnumerado()==EnumeradoTipos.INT && tipoOperando2.tipoEnumerado()==EnumeradoTipos.INT){
				return new TipoBoolean();
			}
			GestionErroresTiny.errorSemantico("Error de tipos. Los tipos para la comparación de mayor-igual no son enteros");
			break;
		case GREATERTHAN:
			if(tipoOperando1.tipoEnumerado()==EnumeradoTipos.INT && tipoOperando2.tipoEnumerado()==EnumeradoTipos.INT){
				return new TipoBoolean();
			}
			GestionErroresTiny.errorSemantico("Error de tipos. Los tipos para la comparación de mayor no son enteros");

			break;
		case LESSEQUAL:
			if(tipoOperando1.tipoEnumerado()==EnumeradoTipos.INT && tipoOperando2.tipoEnumerado()==EnumeradoTipos.INT){
				return new TipoBoolean();
			}
			GestionErroresTiny.errorSemantico("Error de tipos. Los tipos para la comparación de menor-igual no son enteros");
			break;
		case LESSTHAN:
			if(tipoOperando1.tipoEnumerado()==EnumeradoTipos.INT && tipoOperando2.tipoEnumerado()==EnumeradoTipos.INT){
				return new TipoBoolean();
			}
			GestionErroresTiny.errorSemantico("Error de tipos. Los tipos para la comparación de menor no son enteros");
			break;
		case MUL:
			if(tipoOperando1.tipoEnumerado()==EnumeradoTipos.INT && tipoOperando2.tipoEnumerado()==EnumeradoTipos.INT){
				return new TipoInt();
			}
			GestionErroresTiny.errorSemantico("Error de tipos. Los tipos para la multiplicación no son enteros");
			break;
		case NOTEQUAL:
			if((tipoOperando1.tipoEnumerado() == EnumeradoTipos.BOOLEAN || tipoOperando1.tipoEnumerado() == EnumeradoTipos.INT) && tipoOperando1.tipoEnumerado() == tipoOperando2.tipoEnumerado() ) {
				return new TipoBoolean();
			}
			GestionErroresTiny.errorSemantico("Error de tipos. Los tipos para la comparación de desigualdad no coinciden o no son válidos");
			break;
		case OR:
			if(tipoOperando1.tipoEnumerado()==EnumeradoTipos.BOOLEAN && tipoOperando2.tipoEnumerado()==EnumeradoTipos.BOOLEAN) {
				//los dos operandos son booleanos entonces devolvemos un booleano
				return new TipoBoolean();
			}
			GestionErroresTiny.errorSemantico("Error de tipos. Uno de los operandos del OR no es booleano");
			break;
		case RESTA:
			if(tipoOperando1.tipoEnumerado()==EnumeradoTipos.INT && tipoOperando2.tipoEnumerado()==EnumeradoTipos.INT){
				return new TipoInt();
			}
			GestionErroresTiny.errorSemantico("Error de tipos. Los tipos para la resta no son enteros");

			break;
		case SQUAREBRACKET:
			
			
			
			
			
			break;
		case SUMA:
			if(tipoOperando1.tipoEnumerado()==EnumeradoTipos.INT && tipoOperando2.tipoEnumerado()==EnumeradoTipos.INT){
				return new TipoInt();
			}
			GestionErroresTiny.errorSemantico("Error de tipos. Los tipos para la suma no son enteros");
			break;
		default:
			break;
		
		}
		break;
	case EXPRESION:
		//aquí hay que añadir las expresiones que representan a los tipos, identificadores y llamadas a funciones
		break;
	case EXPRESION_UNARIA:
		break;
	case INSTRUCCION:
		break;
	case TIPOS:
		break;
	default:
		break;
	}
	return null;

  }
}