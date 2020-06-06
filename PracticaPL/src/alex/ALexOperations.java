package alex;
import asint.ClaseLexica;

public class ALexOperations {
  private AnalizadorLexicoTiny alex;
  public ALexOperations(AnalizadorLexicoTiny alex) {
   this.alex = alex;   
  }
  public UnidadLexica unidadId() {
     return new UnidadLexicaMultivaluada(alex.fila(),ClaseLexica.IDEN, alex.lexema()); 
  } 
  public UnidadLexica unidadEnt() {
     return new UnidadLexicaMultivaluada(alex.fila(),ClaseLexica.ENT,alex.lexema()); 
  } 
  public UnidadLexica unidadSuma() {
     return new UnidadLexicaMultivaluada(alex.fila(),ClaseLexica.MAS, "+"); 
  } 
  public UnidadLexica unidadResta() {
     return new UnidadLexicaMultivaluada(alex.fila(),ClaseLexica.MENOS, "-"); 
  } 
  public UnidadLexica unidadMul() {
     return new UnidadLexicaMultivaluada(alex.fila(),ClaseLexica.POR, "*"); 
  } 
  public UnidadLexica unidadDiv() {
     return new UnidadLexicaMultivaluada(alex.fila(),ClaseLexica.DIV, "/"); 
  }
  public UnidadLexica unidadMod() {
     return new UnidadLexicaMultivaluada(alex.fila(),ClaseLexica.MOD, "%"); 
  }
  public UnidadLexica unidadIgual() {
     return new UnidadLexicaMultivaluada(alex.fila(),ClaseLexica.IGUAL, "="); 
  } 
  public UnidadLexica unidadComa() {
     return new UnidadLexicaMultivaluada(alex.fila(),ClaseLexica.COMA, ","); 
  } 
  public UnidadLexica unidadPuntoComa() {
     return new UnidadLexicaMultivaluada(alex.fila(),ClaseLexica.PCOMA, ";"); 
  } 
  public UnidadLexica unidadPunto() {
	     return new UnidadLexicaMultivaluada(alex.fila(),ClaseLexica.PUNTO, "."); 
  } 
  public UnidadLexica unidadEof() {
     return new UnidadLexicaMultivaluada(alex.fila(),ClaseLexica.EOF, "EOF"); 
  }
  public UnidadLexica unidadAnd() {
	  return new UnidadLexicaMultivaluada(alex.fila(),ClaseLexica.AND, "&");
  }
  public UnidadLexica unidadOr() {
	  return new UnidadLexicaMultivaluada(alex.fila(),ClaseLexica.OR, "|");
  }
  public UnidadLexica unidadNot() {
	  return new UnidadLexicaMultivaluada(alex.fila(),ClaseLexica.NOT, "!");
  }
  public UnidadLexica unidadLT() {
	  return new UnidadLexicaMultivaluada(alex.fila(),ClaseLexica.LT, "<");
  }
  public UnidadLexica unidadGT() {
	  return new UnidadLexicaMultivaluada(alex.fila(),ClaseLexica.GT, ">");
  }
  public UnidadLexica unidadLE() {
	  return new UnidadLexicaMultivaluada(alex.fila(),ClaseLexica.LE, ">=");
  }
  public UnidadLexica unidadGE() {
	  return new UnidadLexicaMultivaluada(alex.fila(),ClaseLexica.GE, "<=");
  }
  public UnidadLexica unidadLA() {
	  return new UnidadLexicaMultivaluada(alex.fila(),ClaseLexica.LA, "{");
  }
  public UnidadLexica unidadLC() {
	  return new UnidadLexicaMultivaluada(alex.fila(),ClaseLexica.LC, "}");
  }
  public UnidadLexica unidadCA() {
	  return new UnidadLexicaMultivaluada(alex.fila(),ClaseLexica.CA, "[");
  }
  public UnidadLexica unidadCC() {
	  return new UnidadLexicaMultivaluada(alex.fila(),ClaseLexica.CC, "]");
  }
  public UnidadLexica unidadPA() {
	  return new UnidadLexicaMultivaluada(alex.fila(),ClaseLexica.PA, "(");
  }
  public UnidadLexica unidadPC() {
	  return new UnidadLexicaMultivaluada(alex.fila(),ClaseLexica.PC, ")");
  }  
  public UnidadLexica unidadSaltoLinea() {
	  return new UnidadLexicaMultivaluada(alex.fila(),ClaseLexica.SL, "Salto De Linea");
  }  
  public UnidadLexica unidadIf() {
	  return new UnidadLexicaMultivaluada(alex.fila(),ClaseLexica.IF, "If");
  }  
  public UnidadLexica unidadDo() {
	  return new UnidadLexicaMultivaluada(alex.fila(),ClaseLexica.DO, "Do");
  }  
  public UnidadLexica unidadElev() {
	  return new UnidadLexicaMultivaluada(alex.fila(),ClaseLexica.ELEV, "**");
  }  
  public UnidadLexica unidadEqual() {
	  return new UnidadLexicaMultivaluada(alex.fila(),ClaseLexica.EQUAL, "==");
  }  
  public UnidadLexica unidadInt() {
	  return new UnidadLexicaMultivaluada(alex.fila(),ClaseLexica.INT, "Int");
  }
  public UnidadLexica unidadThen() {
	  return new UnidadLexicaMultivaluada(alex.fila(),ClaseLexica.THEN, "Then");
  }  
  public UnidadLexica unidadTrue() {
	  return new UnidadLexicaMultivaluada(alex.fila(),ClaseLexica.TRUE, "True");
  }
  public UnidadLexica unidadFalse() {
	  return new UnidadLexicaMultivaluada(alex.fila(),ClaseLexica.FALSE, "False");
  }
  public UnidadLexica unidadElse() {
	  return new UnidadLexicaMultivaluada(alex.fila(),ClaseLexica.ELSE, "Else");
  }
  public UnidadLexica unidadProc() {
	  return new UnidadLexicaMultivaluada(alex.fila(),ClaseLexica.PROC, "Proc");
  }
  public UnidadLexica unidadConst() {
	  return new UnidadLexicaMultivaluada(alex.fila(),ClaseLexica.CONST, "Const");
  }
  public UnidadLexica unidadWhile() {
	  return new UnidadLexicaMultivaluada(alex.fila(),ClaseLexica.WHILE, "While");
  }
  public UnidadLexica unidadSwitch() {
	  return new UnidadLexicaMultivaluada(alex.fila(),ClaseLexica.SWITCH, "Switch");
  }
  public UnidadLexica unidadReturn() {
	  return new UnidadLexicaMultivaluada(alex.fila(),ClaseLexica.RETURN, "Return");
  }
  public UnidadLexica unidadBoolean() {
	  return new UnidadLexicaMultivaluada(alex.fila(),ClaseLexica.BOOLEAN, "Boolean");
  }
  public UnidadLexica unidadDefault() {
	  return new UnidadLexicaMultivaluada(alex.fila(),ClaseLexica.DEFAULT, "Default");
  }
  public UnidadLexica unidadCase() {
	  return new UnidadLexicaMultivaluada(alex.fila(),ClaseLexica.CASE, "Case");
  }
  public UnidadLexica unidadStruct() {
	  return new UnidadLexicaMultivaluada(alex.fila(), ClaseLexica.STRUCT, "Struct");
  }  
  public void error() {
    System.err.println("***"+alex.fila()+" Caracter inesperado: "+alex.lexema());
  }
}
