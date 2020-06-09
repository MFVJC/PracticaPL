package asem;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

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
					SentenciaAbstracta referenciaDeclaracion = tabla.getSentenciaDeclaracion(((Iden)llamadaProcedimiento.getNombre_funcion()).getNombre());
					if(referenciaDeclaracion!=null) {
						llamadaProcedimiento.setReferencia(referenciaDeclaracion);
						//habría que guardar la referenciaDeclaracion dentro del objeto InstCallProc
						llamadaProcedimiento.getArgumentos().forEach(x -> vincula(x));
					}else {
						GestionErroresTiny.errorSemantico("El procedimiento " + llamadaProcedimiento.getNombre_funcion() + " no ha sido declarado. Solo se puede llamar a funciones declaradas anteriormente");
					}
					break;
				case DECL:
					//no se realmente que es esa clase. Supongo que para declarar cualquier variable incluso vectores (yo creo que sería mejor separar)
					InstDeclaracion declaracion = (InstDeclaracion) sentencia;
					Iden identificadorV = (Iden)declaracion.getIden();
					identificadorV.setConstante(declaracion.isConstant());
					identificadorV.setReferencia(declaracion);
					vincula(declaracion.getTipo());
					tabla.insertaSimbolo(identificadorV.getNombre(), declaracion);
					E valorInicial = declaracion.getValor().get(0); //esto va haber que cambiarlo cuando se refactorice
					if(valorInicial != null) {
						vincula(valorInicial);
					}
					break;
				case DECLFUN:
					InstDeclFun declaracionFuncion = (InstDeclFun) sentencia;
					Tipo tipo = declaracionFuncion.getTipo(); //no vale si es proc
					//String tipoF = declaracionFuncion.getTipoF();// null si procedimiento
					vincula(declaracionFuncion.getTipo());
					tabla.insertaSimbolo(((Iden)declaracionFuncion.getIden()).getNombre(), sentencia);
					//los argumentos de la funcion tienen que ser de tipo E
					
					//FALTAAAAAAAAAAAAAAA
					
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
					}else {
						instSwitch.setReferencia(referenciaVariableSwitch);
					
					List<Pair<E, List<I>>> casos = instSwitch.getCases();
					for(Pair<E, List<I>> caso : casos) {
						
						tabla.nuevaTablaSimbolos();
						caso.getValue().forEach(x->vincula(x));
						tabla.eliminaTablaSimbolos();
					}
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
				LlamadaFuncion llamada = (LlamadaFuncion) expresion;
				SentenciaAbstracta referenciaFuncion = tabla.getSentenciaDeclaracion(llamada.getNombre_funcion());
				if(referenciaFuncion == null) {
					GestionErroresTiny.errorSemantico("Llamada a función " + llamada.getNombre_funcion() + " no existente.");
				}else {
					//guardamos para luego poder comprobar los tipos
					llamada.setReferencia(referenciaFuncion);
					llamada.setTipoReturn(((InstDeclFun)referenciaFuncion).getTipo());
					llamada.getArgumentos().forEach(x->vincula(x));
				}
				break;
			case IDEN:
				Iden identificador = (Iden) expresion;
				SentenciaAbstracta refIdentificador = tabla.getSentenciaDeclaracion(identificador.getNombre());
				if(refIdentificador == null) {
					GestionErroresTiny.errorSemantico("El identificador " + identificador.getNombre() + " no ha sido declarado.");
				}else {
					if(refIdentificador instanceof InstDeclaracion) {
						//guardo el tipo de la variable en el identificador para la comprobación de tipos posterior
						identificador.setTipo(((InstDeclaracion)refIdentificador).getTipo());
					}else {
						GestionErroresTiny.errorSemantico("ERROR INESPERADO EN EL PROGRAMA.");
					}
				}
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
				Iden identificador = (Iden)instruccionAsignacion.getIden();
				if(identificador.esConstante()) {
					GestionErroresTiny.errorSemantico("Error de tipos. El identificador " + identificador.getNombre() + " corresponde con una constante por lo que no es modificable.");
				}else {
				Tipo tipoAsignar = tiposExpresion(instruccionAsignacion.getValor());
					if(tipoAsignar.tipoEnumerado() == EnumeradoTipos.STRUCT) {
						GestionErroresTiny.errorSemantico("Error de tipos. Operación no soportada: no se pueden asignar structs o punteros a una variable");
					}else if (tipoAsignar.tipoEnumerado() ==EnumeradoTipos.PUNTERO){
						EPointer pointer = (EPointer) instruccionAsignacion.getValor();
						TipoPuntero tipoPuntero = (TipoPuntero) tipoAsignar;
						if(tipoPuntero.getClaseApuntada().tipoEnumerado() == pointer.getTipo().tipoEnumerado()) {
							//entonces los dos punteros son del mismo tipo y no hay problema
							return true;
						}
					}else {
						if(tipoAsignar.tipoEnumerado() == identificador.getTipo().tipoEnumerado()){
							return true;
						}
						GestionErroresTiny.errorSemantico("Error de tipos en la asignación. Los tipos no coinciden.");
					}
				}
				break;
			case CALLPROC:
				InstCallProc intruccionLlamadaFuncion  = (InstCallProc) instruccion;
				SentenciaAbstracta declaracion = intruccionLlamadaFuncion.getReferencia();
				InstDeclFun declaracionFuncion = (InstDeclFun) declaracion;
				List<E> argumentos = intruccionLlamadaFuncion.getArgumentos();
				int i = 0;
				boolean correctArguments = true;
				for(Pair<Tipo,E> atributo : declaracionFuncion.getArgs()) {
					if(tiposExpresion(argumentos.get(i)).tipoEnumerado() != atributo.getKey().tipoEnumerado()) {
						correctArguments = false;
					}else {
						GestionErroresTiny.errorSemantico("Error tipos. El parámetro número " + i + " no concuerda con el tipo del atributo de la función.");
					}
					i++;
				}
				if(correctArguments)
					return correctArguments;
				break;
			case DECL:
				InstDeclaracion instruccionDeclaracion = (InstDeclaracion) instruccion;
				if(instruccionDeclaracion.getIden().tipoExpresion() == TipoE.IDEN) {
					//luego hay que comprobar que el valor con el que quieres asignar 
				}else {
					GestionErroresTiny.errorSemantico("Error de tipos. La variable tiene que ser necesariamente un identificador.");
				}
				break;
			case DECLFUN:
				InstDeclFun instruccionDeclaracionFuncion = (InstDeclFun) instruccion;
				
				break;
			case IF:
				InstIf instruccionIf = (InstIf) instruccion;
				if(tiposExpresion(instruccionIf.getCondicion()).tipoEnumerado() == EnumeradoTipos.BOOLEAN){
				AtomicBoolean correcto = new AtomicBoolean(true);
				instruccionIf.getCuerpo_if().forEach(x->{ correcto.set(correcto.get() && compruebaTipos(x));});
				
				if(instruccionIf.getCuerpo_else() != null) {
					instruccionIf.getCuerpo_else().forEach(x->{ correcto.set(correcto.get() && compruebaTipos(x));});
				}
				return correcto.get();
				}else {
					GestionErroresTiny.errorSemantico("Error de tipos. La condición del if debe ser booleana");
				}
				break;
			case STRUCT:
				InstStruct instruccionStruct = (InstStruct) instruccion;
				AtomicBoolean correcto = new AtomicBoolean(true);
				instruccionStruct.getDeclaraciones().forEach(x->{ correcto.set(correcto.get() && compruebaTipos(x));});
				return correcto.get();
			case SWITCH:
				InstSwitch instruccionSwitch = (InstSwitch) instruccion;
				E condicion = instruccionSwitch.getCondicion();
				Tipo tipoCondicion = tiposExpresion(condicion);
				AtomicBoolean correct = new AtomicBoolean(true);
				for(Pair<E,List<I>> caso : instruccionSwitch.getCases()) {
					if(tipoCondicion.tipoEnumerado() != tiposExpresion(caso.getKey()).tipoEnumerado()) {
						correct.set(false);
						GestionErroresTiny.errorSemantico("Error de tipos. Los tipos de los case deben coincidir con la expresión del switch.");
						
					}
					caso.getValue().forEach(x->{correct.set(correct.get() && compruebaTipos(x));});
					return correct.get();
				}
				break;
			case WHILE:
				InstWhile instruccionWhile = (InstWhile) instruccion;
				if(tiposExpresion(instruccionWhile.getCondicion()).tipoEnumerado() == EnumeradoTipos.BOOLEAN) {
					AtomicBoolean correctWhile = new AtomicBoolean(true);
					instruccionWhile.getCuerpo().forEach(x->{correctWhile.set(correctWhile.get()  && compruebaTipos(x));});
					return correctWhile.get();
				}else {
					GestionErroresTiny.errorSemantico("Error de tipos. La condición del while debe ser booleana.");
				}
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
		E expresion = (E) sentencia;
		switch(expresion.tipoExpresion()) {
		case ASTERISK:
			//lo de la derecha tiene que ser un vector
			Asterisk asteriscoExp = (Asterisk) expresion;
			//necesitamos vector
			//if(tiposExpresion(asteriscoExp.opnd1()).tipoEnumerado() == EnumeradoTipos.)
			break;
		case BASICFALSE:
			return new TipoBoolean();
		case BASICTRUE:
			return new TipoBoolean();
		case NUM:
			return new TipoInt();
		case FUNCION:
			LlamadaFuncion llamada = (LlamadaFuncion) expresion;
			List<Iden> variable = (List<Iden>)(List<?>) llamada.getArgumentos();
			List<Tipo> tiposLlamada = new ArrayList<>();
			variable.forEach(x-> tiposLlamada.add(x.getTipo()));
			InstDeclFun declaracionFuncion = (InstDeclFun) llamada.getReferencia();
			int i = 0;
			boolean coincidenTipos = true;
			for(Pair<Tipo,E> atributo : declaracionFuncion.getArgs()) {
				if(atributo.getKey().tipoEnumerado() != tiposLlamada.get(i).tipoEnumerado()){
					coincidenTipos = false;
					GestionErroresTiny.errorSemantico("Error de tipos. El tipo del parámetro " + i + " no coincide con el del respectivo argumento");
				}
				i++;
			}
			if(coincidenTipos) {
				return llamada.getTipoReturn();
			}
			break;
		case IDEN:
			Iden identificador = (Iden) expresion;
			return identificador.getTipo();
		case NOT:
			Not not  = (Not) expresion;
			if(tiposExpresion(not.opnd1()).tipoEnumerado() == EnumeradoTipos.BOOLEAN) {
				return new TipoBoolean();
			}
			GestionErroresTiny.errorSemantico("El operando de un NOT debe ser booleano");
			break;
		default:
			break;
		
		}
		//aquí hay que añadir las expresiones que representan a los tipos, identificadores y llamadas a funciones
		break;
	default:
		break;
	}
	return new TipoError();

  }
}