package ast.I;

import java.util.List;

import ast.E.E;
import ast.T.Tipo;
import javafx.util.Pair;

public class InstDeclFun extends I {
	//hay que cambiar el AS.java y el .cup para a�adir este tipo
	//tipo del return
	private Tipo tipo; //El tipo de una funcion es el tipo de la expresion que se devuelve
	private E nombreFuncion;
	private List<Pair<Tipo, E>> args;
	private List<I> cuerpo;
	private E valorReturn; //La expresion que se devuelve

 public InstDeclFun(Tipo tipo, E nombreFuncion, List<Pair<Tipo, E>> args, List<I> cuerpo, E ret) {
		this.tipo = tipo;
	 	this.nombreFuncion = nombreFuncion;
		this.args = args;
		this.cuerpo = cuerpo;
		this.valorReturn = ret;
	}
 
 public TipoI tipoInstruccion() {return TipoI.DECLFUN;}
 public String toString() {
	   String aux;
	   if(tipo != null) aux = "{{_DeclF_}{" + tipo + "}" + nombreFuncion + "{{_Args__}";
	   else aux = "{{_DeclP_}" + nombreFuncion + "{{_Args__}";
	   
	   int i = 0;
	   for(Pair<Tipo, E> arg : args) {
		   aux += "{{_Arg" + i + "__}{" + arg.getKey() + "}";
		   aux += arg.getValue();
		   aux += "}";
		   i++;
	   }
	  
	   aux += "}{{_Cuer__}";
	   for(I ins : cuerpo) aux += ins.toString();
	   if(valorReturn != null) aux += "{{__Ret__}" + valorReturn + "}";
	   aux += "}}";
	   
	   return aux;
 	}

	public E getIden() {
		return nombreFuncion;
	}
	
	public List<Pair<Tipo, E>> getArgs() {
		return args;
	}
	
	public List<I> getCuerpo() {
		return cuerpo;
	}
	
	public E getReturn() {
		return valorReturn;
	}
	
	public Tipo getTipo() {
		return tipo;
	}
}
