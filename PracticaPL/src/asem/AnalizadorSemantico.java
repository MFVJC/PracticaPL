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
	
	public boolean analizaSemantica() {
		//Vinculamos todas las instrucciones del programa
		System.out.println("Se inicia el proceso de an�lisis sem�ntico del c�digo.");
		tabla.abreBloque();
		for(I instruccion : programa) vincula(instruccion);
		tabla.cierraBloque();
		
		//Comprobamos tipos
		AtomicBoolean correcto = new AtomicBoolean(true);
	programa.forEach(x -> {correcto.set(compruebaTipos(x) && correcto.get());});
		if(correcto.get()) System.out.println("Comprobacion de Tipos sin errores");
		return correcto.get();
	}
	
	public void vincula(SentenciaAbstracta sentencia) {
		switch(sentencia.tipoSentencia()) {
			//Instrucciones
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
							//habr�a que guardar la referenciaDeclaracion dentro del objeto InstCallProc
							llamadaProcedimiento.getArgumentos().forEach(x -> vincula(x));
						}else {
							GestionErroresTiny.errorSemantico("El procedimiento " + llamadaProcedimiento.getNombre_funcion() + " no ha sido declarado. Solo se puede llamar a funciones declaradas anteriormente");
						}
						break;
					case DECL:
						//no se realmente que es esa clase. Supongo que para declarar cualquier variable incluso vectores (yo creo que ser�a mejor separar)
						InstDeclaracion declaracion = (InstDeclaracion) sentencia;
						//System.out.println("Entramos en declaracion con " +  declaracion.getIden());
						//System.out.println(declaracion.getTipo().tipoEnumerado());
						Iden identificadorV = (Iden)declaracion.getIden();
						identificadorV.setConstante(declaracion.isConstant());
						identificadorV.setReferencia(declaracion);
						identificadorV.setTipo(declaracion.getTipo());
						vincula(declaracion.getTipo());
						tabla.insertaId(identificadorV.getNombre(), declaracion);
						//System.out.println(identificadorV.getTipo());
						List<E> valorInicial = declaracion.getValor(); //esto va haber que cambiarlo cuando se refactorice
						if(valorInicial != null) valorInicial.forEach(x -> vincula(x));
						//aqu� falta comprobar que pasa si es un vector
						break;
					case DECLFUN:
						//System.out.println("Vinculando funcion");
						InstDeclFun declaracionFuncion = (InstDeclFun) sentencia;
						
						Tipo tipoFuncion = declaracionFuncion.getTipo(); //no vale si es proc
						if(tipoFuncion != null) vincula(tipoFuncion);
						Iden identificadorFuncion = (Iden)declaracionFuncion.getIden();
						tabla.insertaId(identificadorFuncion.getNombre(), declaracionFuncion);
						identificadorFuncion.setConstante(true);

						tabla.abreBloque();
						List<Pair<Tipo, E>> listaParametros = declaracionFuncion.getArgs();
						for(Pair<Tipo, E> parametro : listaParametros) {
							//System.out.println("Se vincula el par�metro: " + ((Iden)parametro.getValue()).getNombre() + " con tipo " + parametro.getKey().toString());
							//esto no hay que vincularlo
							tabla.insertaId(((Iden)parametro.getValue()).getNombre(), declaracionFuncion);
							Iden identificadorParametro = (Iden)parametro.getValue();
							//System.out.println("Guardando en el iden "+ identificadorParametro + " el tipo " + parametro.getKey());
							identificadorParametro.setTipo(parametro.getKey());
							tabla.anadeTipoVariable(identificadorParametro.getNombre(), parametro.getKey());
							vincula(parametro.getKey());
						}
						
						List<I> cuerpoFuncion = declaracionFuncion.getCuerpo();
						cuerpoFuncion.forEach(x -> vincula(x));
						//esto estaba mal pra procedimientos
						if(tipoFuncion!= null)vincula(declaracionFuncion.getReturn());
						tabla.cierraBloque();
						break;
					case IF:
						InstIf instIf = (InstIf) sentencia;
						vincula(instIf.getCondicion());
						tabla.abreBloque();
						instIf.getCuerpoIf().forEach(x->vincula(x));
						tabla.cierraBloque();
						List<I> cuerpoElse = instIf.getCuerpoElse();
						if(cuerpoElse != null) {
							tabla.abreBloque();
							instIf.getCuerpoElse().forEach(x->vincula(x));
							tabla.cierraBloque();
						}
						break;
					case STRUCT:
						InstStruct instStruct = (InstStruct) sentencia;
						//faltar�a meter la referencia a la sentencia abstracta
						tabla.insertaId(((Iden) instStruct.getIden()).getNombre(), instStruct);
						tabla.abreBloque();
						instStruct.getDeclaraciones().forEach(x->vincula(x));
						tabla.cierraBloque();
						break;
					case SWITCH:
						InstSwitch instSwitch = (InstSwitch) sentencia;
						//aqu� con la condicion cogemos la referencia de la tabla de s�mbolos
				
						SentenciaAbstracta referenciaVariableSwitch = tabla.getSentenciaDeclaracion(((Iden)instSwitch.getCondicion()).getNombre());
						vincula((Iden)instSwitch.getCondicion());
						if(referenciaVariableSwitch == null) {
							GestionErroresTiny.errorSemantico("La variable " + ((Iden)instSwitch.getCondicion()).getNombre() + " no ha sido declarada");
						}else {
							instSwitch.setReferencia(referenciaVariableSwitch);
							
							List<Pair<E, List<I>>> casos = instSwitch.getCases();
							for(Pair<E, List<I>> caso : casos) {
								
								tabla.abreBloque();
								caso.getValue().forEach(x->vincula(x));
								tabla.cierraBloque();
							}
						}
						//Si no hacemos los cases vamos a perder la informaci�n de la SentenciaAbstracta correspondiente al case.
						break;
					case WHILE:
						InstWhile instWhile = (InstWhile) sentencia;
						vincula(instWhile.getCondicion()); // as� veo el tipo
						tabla.abreBloque();
						instWhile.getCuerpo().forEach(x-> vincula(x));
						tabla.cierraBloque();
						break;
					default:
						break;
					
				}
			
			break;
			//Expresiones Binarias
			case EXPRESION_BINARIA:
				EBin expresionBinaria = (EBin) sentencia;
					//en el caso de que sea un punto solo tengo que vincular el primer operando
					vincula(expresionBinaria.opnd1());
					if(expresionBinaria.tipoExpresion() != TipoE.DOT) {
						vincula(expresionBinaria.opnd2());
					}
			break;
			//Expresiones
			case EXPRESION:
			E expresion = (E) sentencia;
				
			switch(expresion.tipoExpresion()) {
				case FUNCION:
					LlamadaFuncion llamada = (LlamadaFuncion) expresion;
					SentenciaAbstracta referenciaFuncion = tabla.getSentenciaDeclaracion(((Iden) llamada.getNombreFuncion()).getNombre());
					if(referenciaFuncion == null) {
						GestionErroresTiny.errorSemantico("Llamada a funci�n " + ((Iden) llamada.getNombreFuncion()).getNombre() + " no existente.");
					}else {
						//guardamos para luego poder comprobar los tipos
						llamada.setReferencia(referenciaFuncion);
						llamada.setTipoReturn(((InstDeclFun)referenciaFuncion).getTipo());
						llamada.getArgumentos().forEach(x->vincula(x));
					}
					break;
				case IDEN:
					Iden identificador = (Iden) expresion;
					//System.out.println("Guardando el identificador: " + identificador.getNombre());
					SentenciaAbstracta refIdentificador = tabla.getSentenciaDeclaracion(identificador.getNombre());
					if(refIdentificador == null) {
						GestionErroresTiny.errorSemantico("El identificador " + identificador.getNombre() + " no ha sido declarado.");
					}else {
						if(refIdentificador instanceof InstDeclaracion) {
							//System.out.println("Guardando el tipo de la variable " + identificador.getNombre());
							//en realidad esto lo est�s haciendo cada vez que aparece uno
							//guardo el tipo de la variable en el identificador para la comprobaci�n de tipos posterior
							tabla.anadeTipoVariable(identificador.getNombre(), ((InstDeclaracion)refIdentificador).getTipo());
							identificador.setTipo(((InstDeclaracion)refIdentificador).getTipo());
						}else if(refIdentificador instanceof InstDeclFun) { //instancia de InstDeclFun
							//identificador.
							InstDeclFun declaracionFuncion = (InstDeclFun) refIdentificador;
							for(Pair<Tipo,E> argumento: declaracionFuncion.getArgs()) {
								Iden identificadorArgumento = (Iden)argumento.getValue();
								if(identificadorArgumento.getNombre() .equals(identificador.getNombre())) {
									//System.out.println("Guardando el tipo correctamente");
									identificador.setTipo(argumento.getKey());
								}
							}
							//GestionErroresTiny.errorSemantico("ERROR INESPERADO EN EL PROGRAMA.");
							
						}else if(refIdentificador instanceof InstStruct){
							//System.out.println(identificador.getNombre());
							TipoStruct tipoVariable = new TipoStruct(identificador.getNombre());
							tabla.anadeTipoVariable(identificador.getNombre(), tipoVariable);
							identificador.setTipo(tipoVariable);
							//System.out.println("Nos sigue faltando el caso de ");
							//System.out.println(refIdentificador);
						}else {
							System.out.println("nuevo caso");
						}
					}
					break;
				case NOT:
					Not expNot = (Not) expresion;
					vincula(expNot.opnd1());
					break;
				case DOLLAR:
					Dollar asterisk = (Dollar) expresion;
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
					vincula(((TipoPuntero)tipo).getTipoApuntado());
					break;
				case STRUCT:
					TipoStruct tipoStruct = (TipoStruct) tipo;
					SentenciaAbstracta referenciaSentencia = tabla.getSentenciaDeclaracion(tipoStruct.getNombreStruct());
					if(referenciaSentencia == null) {
						GestionErroresTiny.errorSemantico("Struct " + tipoStruct.getNombreStruct() + " no declarado.");
					}else {
						tipoStruct.setReferencia(referenciaSentencia);
						//guardo la referencia a la sentencia en la que se declar� dentro del nodo
					}	
					break;
				case ARRAY:
					vincula(((TipoArray)tipo).getTipoBase());
					vincula(((TipoArray) tipo).getDimension()); //No estoy seguro de si hay que vincular la dimension (JC)
					
					break;
				default:
					break;
				}
			break;
		default:
			break;
		}
	}
	
	//hay que a�adir tipo como atributo de InstDeclaracion InstDeclFun y dem�s para guardar la referencia del tipo
	public boolean compruebaTipos(SentenciaAbstracta sentencia) {
		if(sentencia.tipoSentencia() == EnumeradoTipoGeneral.INSTRUCCION) { //se comprueban instrucciones en esta funci�n
			I instruccion = (I) sentencia;
			switch(instruccion.tipoInstruccion()) {
			case ASIG:
				InstAsignacion instruccionAsignacion = (InstAsignacion) instruccion;
					if(instruccionAsignacion.getIden() instanceof Iden) {
						Iden identificador = (Iden) instruccionAsignacion.getIden();
						if(identificador.esConstante()) {
							GestionErroresTiny.errorSemantico("Error de tipos. El identificador " + identificador.getNombre() + " corresponde con una constante o una funci�n por lo que no es modificable.");
						}
					}
					E iden = instruccionAsignacion.getIden();
					Tipo tipoOriginal = tiposExpresion(instruccionAsignacion.getIden());
					Tipo tipoAsignar = tiposExpresion(instruccionAsignacion.getValor());
					//hay que recoger los errores aqu�
					
					//System.out.println("Explorando asignacion de" + instruccionAsignacion.getIden() + " " + instruccionAsignacion.getValor() );
					if(tipoOriginal.tipoEnumerado() == tipoAsignar.tipoEnumerado()) {
						//System.out.println("En serio se puede hacer as� de facil?");
						return true;
						
					}else {
						GestionErroresTiny.errorSemantico("Error de tipos en la asignaci�n. Los tipos no coinciden. Intentando asignar a " + instruccionAsignacion.getIden().toString() + " el valor " + instruccionAsignacion.getValor().toString()+ ".Tipos: " + tipoOriginal + " " + tipoAsignar);

					}
//						if(tipoAsignar.tipoEnumerado() == EnumeradoTipos.STRUCT) {
//							GestionErroresTiny.errorSemantico("Error de tipos. Operaci�n no soportada: no se pueden asignar structs o punteros a una variable");
//						}else if (tipoAsignar.tipoEnumerado() == EnumeradoTipos.PUNTERO){
//							New pointer = (New) instruccionAsignacion.getValor();
//							TipoPuntero tipoPuntero = (TipoPuntero) tipoAsignar;
//							if(tipoPuntero.getTipoApuntado().tipoEnumerado() == pointer.getTipo().tipoEnumerado()) {
//								//entonces los dos punteros son del mismo tipo y no hay problema
//								//Esto creo que esta mal, puede ser que ambos apunten a otro tipo puntero que apunte a su vez a tipos distintos (JC)
//								//Ciertoo
//								return true;
//							}else {
//							//esto cuando sea iden
//								Iden identificador = (Iden) instruccionAsignacion.getIden();
//								if(tipoAsignar.tipoEnumerado() == identificador.getTipo().tipoEnumerado()){
//								return true;
//								}
//							GestionErroresTiny.errorSemantico("Error de tipos en la asignaci�n. Los tipos no coinciden.");
//							}
//						}
				break;
			case CALLPROC:
				InstCallProc intruccionLlamadaFuncion  = (InstCallProc) instruccion;
				//System.out.println("Llega hasta llamada a funci�n");
				SentenciaAbstracta declaracion = intruccionLlamadaFuncion.getReferencia();
				InstDeclFun declaracionFuncion = (InstDeclFun) declaracion;
				List<E> argumentos = intruccionLlamadaFuncion.getArgumentos();
				int i = 0;
				boolean correctArguments = true;
				for(Pair<Tipo,E> atributo : declaracionFuncion.getArgs()) {
					if(tiposExpresion(argumentos.get(i)).tipoEnumerado() != atributo.getKey().tipoEnumerado()) {
						correctArguments = false;
					} else {
						GestionErroresTiny.errorSemantico("Error tipos. El par�metro n�mero " + i + " no concuerda con el tipo del atributo de la funci�n. Atributo: " + ((Iden)atributo.getValue()).getNombre());
					}
					i++;
				}
				if(correctArguments) return correctArguments;
				break;
			case DECL:
				InstDeclaracion instruccionDeclaracion = (InstDeclaracion) instruccion;
				if(instruccionDeclaracion.getIden().tipoExpresion() == TipoE.IDEN) {
					//System.out.println("Explorando identificador " + ((Iden)instruccionDeclaracion.getIden()).getNombre());
					Tipo tipoDeclaracion = instruccionDeclaracion.getTipo();
					boolean correct = true;
					if(instruccionDeclaracion.getValor() != null) {//Esta inicializada
						if(tipoDeclaracion.tipoEnumerado() == EnumeradoTipos.ARRAY) {
							Tipo tipoValores = ((TipoArray)tipoDeclaracion).getTipoBase();
							for(E valor : instruccionDeclaracion.getValor()) {
								Tipo aux = tiposExpresion(valor);
								if(aux.tipoEnumerado() != ((TipoArray)tipoDeclaracion).getTipoBase().tipoEnumerado()) {
									//System.out.println("El tipo de la instruccion de declaracion es " +((TipoArray)tipoDeclaracion).getTipoBase().tipoEnumerado().toString()+ " y el del valor es " + aux.tipoEnumerado().toString());
									correct = false;
									GestionErroresTiny.errorSemantico("Error tipos. El tipo de la declaraci�n no concuerda con su valor inicial. Intentando asignar al tipo " + tipoValores + " el tipo " + aux );
									break;
								}
							}
						}else {
							E valor = instruccionDeclaracion.getValor().get(0);
							Tipo aux = tiposExpresion(valor);
							if(aux.tipoEnumerado() != tipoDeclaracion.tipoEnumerado()) {
								//System.out.println("El tipo de la instruccion de declaracion es " +tipoDeclaracion.tipoEnumerado().toString()+ " y el del valor es " + aux.tipoEnumerado().toString());
								correct = false;
								GestionErroresTiny.errorSemantico("Error tipos. El tipo de la declaraci�n no concuerda con su valor inicial. Asignando a " + instruccionDeclaracion.getIden().toString() + " el valor " + valor.toString() + ". Con tipos " + tipoDeclaracion + " y " + aux);
							}
					}
					}
					return correct;
				}else {
					GestionErroresTiny.errorSemantico("Error de tipos. La variable tiene que ser necesariamente un identificador.");
				}
				break;
			case DECLFUN:
				InstDeclFun instruccionDeclaracionFuncion = (InstDeclFun) instruccion;
				//System.out.println("Checkeando los tipos de la funcion " + ((Iden)instruccionDeclaracionFuncion.getIden()).getNombre());
				//la x o la y no tiene tipo
				Tipo tipoRealReturn = null;
				if(instruccionDeclaracionFuncion.getTipo() != null)
					tipoRealReturn = tiposExpresion(instruccionDeclaracionFuncion.getReturn());
				//System.out.println("Checkeando el tipo de la funci�n " + instruccionDeclaracionFuncion.toString());
				if(instruccionDeclaracionFuncion.getIden().tipoExpresion() == TipoE.IDEN) {
					AtomicBoolean correcto = new AtomicBoolean(true);
					//System.out.println("El valor del return es" + instruccionDeclaracionFuncion.getReturn() );
					//System.out.println("Con tipo " + tipoRealReturn);
					
					if(tipoRealReturn != null && tipoRealReturn.tipoEnumerado() != instruccionDeclaracionFuncion.getTipo().tipoEnumerado()){
						GestionErroresTiny.errorSemantico("Error de tipos. El tipo del return no coincide con el de la funci�n.");
					}
					//esto peta a veces y llega null
					instruccionDeclaracionFuncion.getCuerpo().forEach(x -> {correcto.set(compruebaTipos(x) && correcto.get());});
					if(instruccionDeclaracionFuncion.getTipo() != null) correcto.set(correcto.get() && tipoRealReturn == instruccionDeclaracionFuncion.getTipo());
					return correcto.get();
				} else {
					GestionErroresTiny.errorSemantico("Error de tipos. El nombre de la funcion tiene que ser necesariamente un identificador.");
				}
				
				break;
			case IF:
				InstIf instruccionIf = (InstIf) instruccion;
				if(tiposExpresion(instruccionIf.getCondicion()).tipoEnumerado() == EnumeradoTipos.BOOLEAN){
				AtomicBoolean correcto = new AtomicBoolean(true);
				instruccionIf.getCuerpoIf().forEach(x -> {correcto.set(compruebaTipos(x) && correcto.get());});
				
				if(instruccionIf.getCuerpoElse() != null) {
					instruccionIf.getCuerpoElse().forEach(x -> {correcto.set(compruebaTipos(x) && correcto.get());});
				}
				return correcto.get();
				}else {
					GestionErroresTiny.errorSemantico("Error de tipos. La condici�n del if debe ser booleana");
				}
				break;
			case STRUCT:
				InstStruct instruccionStruct = (InstStruct) instruccion;
				AtomicBoolean correcto = new AtomicBoolean(true);
				instruccionStruct.getDeclaraciones().forEach(x -> {correcto.set(compruebaTipos(x) && correcto.get());});
				return correcto.get();
			case SWITCH:
				InstSwitch instruccionSwitch = (InstSwitch) instruccion;
				E condicion = instruccionSwitch.getCondicion();
				Tipo tipoCondicion = tiposExpresion(condicion);
				AtomicBoolean correct = new AtomicBoolean(true);
				//el tipo de la condicion llega null
				for(Pair<E,List<I>> caso : instruccionSwitch.getCases()) {
					if(!caso.getValue().isEmpty()) {
						if(caso.getKey()!=null) {
							if(tipoCondicion.tipoEnumerado() != tiposExpresion(caso.getKey()).tipoEnumerado()) {
								correct.set(false);
								GestionErroresTiny.errorSemantico("Error de tipos. Los tipos de los case deben coincidir con la expresi�n del switch.");
							
							}
						}
						caso.getValue().forEach(x -> {correct.set(compruebaTipos(x) && correct.get());});
					}
				}

				return correct.get();
			case WHILE:
				InstWhile instruccionWhile = (InstWhile) instruccion;
				if(tiposExpresion(instruccionWhile.getCondicion()).tipoEnumerado() == EnumeradoTipos.BOOLEAN) {
					AtomicBoolean correctWhile = new AtomicBoolean(true);
					instruccionWhile.getCuerpo().forEach(x -> {correctWhile.set(compruebaTipos(x) && correctWhile.get());});
					return correctWhile.get();
				}else {
					GestionErroresTiny.errorSemantico("Error de tipos. La condici�n del while debe ser booleana.");
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
				//System.out.println("Va a calcular el tipo de " + operando1.toString());
				//System.out.println(operando1.tipoExpresion());
				Tipo tipoOperando1 = tiposExpresion(operando1);
				Tipo tipoOperando2 = null;
				if(ebin.tipoExpresion() != TipoE.DOT) {
					tipoOperando2 = tiposExpresion(operando2);
				}
				if(tipoOperando1 == null  || (ebin.tipoExpresion()!= TipoE.DOT && tipoOperando2 ==null)) {
					System.out.println("Devuelven null cuando eso nunca deber�a pasar. ha tenido que haber alg�n fallo previo. Operandos: " + operando1.toString() + " y " + operando2.toString());
					if(tipoOperando1 == null)System.out.println(operando1 +" "+ operando1.tipoExpresion());
					if(tipoOperando2==null)System.out.println(operando2 + " " + operando2.tipoExpresion() + ((Iden)operando2).getTipo());
				}
				else {
				if (tipoOperando1.tipoEnumerado() != EnumeradoTipos.ERROR || (ebin.tipoExpresion() != TipoE.DOT && tipoOperando2.tipoEnumerado() != EnumeradoTipos.ERROR)) {
				//System.out.println("Analizando expresi�n binaria con " + operando1.toString() + " " + operando2.toString() + " con tipos " + tipoOperando1.toString() + " y " + tipoOperando2.toString());
				
				switch(ebin.tipoExpresion()) {
				case AND:
					if(tipoOperando1.tipoEnumerado()==EnumeradoTipos.BOOLEAN && tipoOperando2.tipoEnumerado()==EnumeradoTipos.BOOLEAN) {
						//los dos operandos son booleanos entonces devolvemos un booleano
						return new TipoBoolean();
					}
					GestionErroresTiny.errorSemantico("Error de tipos. Uno de los operandos del AND no es booleano. Operandos: " + operando1.toString() + " y " + operando2.toString());
					break;
				case DIV:
					if(tipoOperando1.tipoEnumerado()==EnumeradoTipos.INT && tipoOperando2.tipoEnumerado()==EnumeradoTipos.INT){
						return new TipoInt();
					}
					GestionErroresTiny.errorSemantico("Error de tipos. Uno de los operandos de la divisi�n no es entero. Operandos: " + operando1.toString() + " y " + operando2.toString());
		
					break;
				case DOT:
					//tenemos que comprobar que operando1 es un struct
					//
					if(tipoOperando1.tipoEnumerado() == EnumeradoTipos.STRUCT) {
						//falta comprobar que el punto corresponde con un campo del struct
						Iden atributo = (Iden) ebin.opnd2();
						//System.out.println("Guardando atributo " + atributo.getNombre());
						//podemos coger la referencia al struct del TipoStruct
						 //nos hace falta meter las referencias para esto
						TipoStruct tipoStruct = (TipoStruct) tipoOperando1;
						InstStruct sentenciaStruct = (InstStruct) tipoStruct.getReferencia(); // esto es null
						if(sentenciaStruct == null) {
							GestionErroresTiny.errorSemantico("Debes especificar un struct definido y no un struct general. Struct: " + operando1);
						}
						else{
							
						
						for(I instruccion : sentenciaStruct.getDeclaraciones()) {
							if(instruccion instanceof InstDeclaracion) {
								Iden atributoStruct = (Iden)((InstDeclaracion) instruccion).getIden();
								if(atributoStruct.getNombre() .equals(atributo.getNombre())) {
									//Entonces si que existe la variable
									return atributoStruct.getTipo();
								}
							}
						}	
						}
					}
						GestionErroresTiny.errorSemantico("Error de tipos. Tipo de operandos inv�lido para el . Operandos: " + operando1.toString() + " y " + operando2.toString());
					
					
					break;
				case ELEV:
					if(tipoOperando1.tipoEnumerado()==EnumeradoTipos.INT && tipoOperando2.tipoEnumerado()==EnumeradoTipos.INT){
						return new TipoInt();
					}
					GestionErroresTiny.errorSemantico("Error de tipos. Tipo de operandos inv�lido para el operador **. Operandos: " + operando1.toString() + " y " + operando2.toString());
		
					break;
				case EQUAL:
					//podemos tener booleanos o enteros
					if((tipoOperando1.tipoEnumerado() == EnumeradoTipos.BOOLEAN || tipoOperando1.tipoEnumerado() == EnumeradoTipos.INT) && tipoOperando1.tipoEnumerado() == tipoOperando2.tipoEnumerado() ) {
						return new TipoBoolean();
					}
					GestionErroresTiny.errorSemantico("Error de tipos. Los tipos para la comparaci�n de igualdad no coinciden o no son v�lidos. Operandos: " + operando1.toString() + " y " + operando2.toString());
					break;
				case GREATEREQUAL:
					if(tipoOperando1.tipoEnumerado()==EnumeradoTipos.INT && tipoOperando2.tipoEnumerado()==EnumeradoTipos.INT){
						return new TipoBoolean();
					}
					GestionErroresTiny.errorSemantico("Error de tipos. Los tipos para la comparaci�n de mayor-igual no son enteros. Operandos: " + operando1.toString() + " y " + operando2.toString());
					break;
				case GREATERTHAN:
					if(tipoOperando1.tipoEnumerado()==EnumeradoTipos.INT && tipoOperando2.tipoEnumerado()==EnumeradoTipos.INT){
						return new TipoBoolean();
					}
					GestionErroresTiny.errorSemantico("Error de tipos. Los tipos para la comparaci�n de mayor no son enteros. Operandos: " + operando1.toString() + " y " + operando2.toString());
		
					break;
				case LESSEQUAL:
					if(tipoOperando1.tipoEnumerado()==EnumeradoTipos.INT && tipoOperando2.tipoEnumerado()==EnumeradoTipos.INT){
						return new TipoBoolean();
					}
					GestionErroresTiny.errorSemantico("Error de tipos. Los tipos para la comparaci�n de menor-igual no son enteros. Operandos: " + operando1.toString() + " y " + operando2.toString());
					break;
				case LESSTHAN:
					if(tipoOperando1.tipoEnumerado()==EnumeradoTipos.INT && tipoOperando2.tipoEnumerado()==EnumeradoTipos.INT){
						return new TipoBoolean();
					}
					GestionErroresTiny.errorSemantico("Error de tipos. Los tipos para la comparaci�n de menor no son enteros. Operandos: " + operando1.toString() + " y " + operando2.toString());
					break;
				case MUL:
					if(tipoOperando1.tipoEnumerado()==EnumeradoTipos.INT && tipoOperando2.tipoEnumerado()==EnumeradoTipos.INT){
						return new TipoInt();
					}
					GestionErroresTiny.errorSemantico("Error de tipos. Los tipos para la multiplicaci�n no son enteros. Operandos: " + operando1.toString() + " y " + operando2.toString());
					break;
				case NOTEQUAL:
					if((tipoOperando1.tipoEnumerado() == EnumeradoTipos.BOOLEAN || tipoOperando1.tipoEnumerado() == EnumeradoTipos.INT) && tipoOperando1.tipoEnumerado() == tipoOperando2.tipoEnumerado() ) {
						return new TipoBoolean();
					}
					GestionErroresTiny.errorSemantico("Error de tipos. Los tipos para la comparaci�n de desigualdad no coinciden o no son v�lidos. Operandos: " + operando1.toString() + " y " + operando2.toString());
					break;
				case OR:
					if(tipoOperando1.tipoEnumerado()==EnumeradoTipos.BOOLEAN && tipoOperando2.tipoEnumerado()==EnumeradoTipos.BOOLEAN) {
						//los dos operandos son booleanos entonces devolvemos un booleano
						return new TipoBoolean();
					}
					GestionErroresTiny.errorSemantico("Error de tipos. Uno de los operandos del OR no es booleano. Operandos: " + operando1.toString() + " y " + operando2.toString());
					break;
				case RESTA:
					if(tipoOperando1.tipoEnumerado()==EnumeradoTipos.INT && tipoOperando2.tipoEnumerado()==EnumeradoTipos.INT){
						return new TipoInt();
					}
					GestionErroresTiny.errorSemantico("Error de tipos. Los tipos para la resta no son enteros. Operandos: " + operando1.toString() + " y " + operando2.toString());
					break;
				case SQUAREBRACKET:
					if(tipoOperando1.tipoEnumerado() == EnumeradoTipos.ARRAY && tipoOperando2.tipoEnumerado() == EnumeradoTipos.INT) {
						return (((TipoArray) tipoOperando1).getTipoBase());
					}
					GestionErroresTiny.errorSemantico("Error de tipos. Se esta accediendo a un array erroneamente. Operandos: " + operando1.toString() + " y " + operando2.toString());
					break;
				case SUMA:
					//hay alguno que es null
					if(tipoOperando1.tipoEnumerado()==EnumeradoTipos.INT && tipoOperando2.tipoEnumerado()==EnumeradoTipos.INT){
						return new TipoInt();
					}
					GestionErroresTiny.errorSemantico("Error de tipos. Los tipos para la suma no son enteros. Operandos: " + operando1.toString() + " y " + operando2.toString());
					break;
				default:
					break;
				
				}
				}
				break;
				}
			case EXPRESION:
				E expresion = (E) sentencia;
				switch(expresion.tipoExpresion()) {
				case DOLLAR:
					//lo de la derecha tiene que ser un vector
					Dollar asteriscoExp = (Dollar) expresion;
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
					List<E> argumentos = llamada.getArgumentos(); //esto no siempre es as�
					List<Tipo> tiposLlamada = new ArrayList<>();
					//System.out.println("Entra en la llamada a funci�n a " + ((Iden)llamada.getNombreFuncion()).getNombre());
					for(E argumento: argumentos) {
						if(argumento instanceof Iden) {
							tiposLlamada.add(((Iden)argumento).getTipo());
						}else if(argumento instanceof SquareBracket) {
							tiposLlamada.add(tiposExpresion(argumento));
						}
					}
					InstDeclFun declaracionFuncion = (InstDeclFun) llamada.getReferencia();
					int i = 0;
					boolean coincidenTipos = true;
					//aqu� saltar�a una excepci�n si es no estaba declarada y se parar�a el programa
					for(Pair<Tipo,E> atributo : declaracionFuncion.getArgs()) {
						if(atributo.getKey().tipoEnumerado() != tiposLlamada.get(i).tipoEnumerado()){
							coincidenTipos = false;
							GestionErroresTiny.errorSemantico("Error de tipos. El tipo del par�metro " + i + " no coincide con el del respectivo argumento " + ((Iden)atributo.getValue()).getNombre());
						}
						i++;
					}
					if(coincidenTipos) {
						return llamada.getTipoReturn();
					}
					break;
				case IDEN:
					Iden identificador = (Iden) expresion;
					//System.out.println("Devolviendo tipo " + identificador.getTipo().toString() + " correspondiente a " + identificador.getNombre());
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
				//aqu� hay que a�adir las expresiones que representan a los tipos, identificadores y llamadas a funciones
				break;
			default:
				break;
				}
		
		return new TipoError();
	}
}
