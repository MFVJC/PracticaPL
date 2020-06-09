package alex;

import errors.GestionErroresTiny;

%%
%cup
%line
%class AnalizadorLexicoTiny
%unicode
%public

%{
  private ALexOperations ops;
  private GestionErroresTiny errores;
  public String lexema() {return yytext();}
  public int fila() {return yyline+1;}
  public void fijaGestionErrores(GestionErroresTiny errores) {
   this.errores = errores;
  }
%}


%eofval{
  return ops.unidadEof();
%eofval}

%init{
  ops = new ALexOperations(this);
%init}

letra  = ([A-Z]|[a-z])
digitoPositivo = [1-9]
digito = ({digitoPositivo}|0)
parteEntera = {digitoPositivo}{digito}*
parteDecimal = {digito}* {digitoPositivo}
separador = [ \t\r\b\n]
comentario = #[^\n]* 
int = int
boolean = boolean
if = if
else = else
then = then
return = return
proc = proc
while = while
do = do
switch = switch
case = case
default = default
const = const
true = true
false = false
struct = struct
new = new

identificador = {letra}({letra}|{digito}|_)*
numeroEntero = ({parteEntera}|0)

operadorSuma = \+
operadorResta = \-
operadorMultiplicacion = \*
operadorElevar = \*\*
operadorDivision = /
operadorModulo = %

operadorAnd = &
operadorOr = \|
operadorNot = !
operadorEqual = \=\=
operadorLT = <
operadorGT = >
operadorLE = <=
operadorGE = >=

llaveApertura = \{
llaveCierre = \}
parentesisApertura = \(
parentesisCierre = \)
corcheteApertura = \[
corcheteCierre = \]
igual = \=
coma  = \,
puntoComa = \;
punto = \.

%%
{separador}               {}
{comentario}              {}

{int}                     {return ops.unidadInt();}
{boolean}                 {return ops.unidadBoolean();}
{if}                      {return ops.unidadIf();}
{else}                    {return ops.unidadElse();}
{then}                    {return ops.unidadThen();}
{return}                  {return ops.unidadReturn();}
{proc}                    {return ops.unidadProc();}
{while}                   {return ops.unidadWhile();}
{do}                      {return ops.unidadDo();}
{switch}                  {return ops.unidadSwitch();}
{case}                    {return ops.unidadCase();}
{default}                 {return ops.unidadDefault();}
{const}                   {return ops.unidadConst();}
{true}                    {return ops.unidadTrue();}
{false}                   {return ops.unidadFalse();}
{struct}				  {return ops.unidadStruct();}
{new}					  {return ops.unidadNew();}

{identificador}           {return ops.unidadId();}

{numeroEntero}            {return ops.unidadEnt();}

{operadorSuma}            {return ops.unidadSuma();}
{operadorResta}           {return ops.unidadResta();}
{operadorMultiplicacion}  {return ops.unidadMul();}
{operadorElevar}          {return ops.unidadElev();}
{operadorDivision}        {return ops.unidadDiv();}
{operadorModulo}		  {return ops.unidadMod();}

{operadorAnd}             {return ops.unidadAnd();}
{operadorOr}              {return ops.unidadOr();}
{operadorNot}             {return ops.unidadNot();}
{operadorEqual}           {return ops.unidadEqual();}
{operadorLT}              {return ops.unidadLT();}
{operadorGT}              {return ops.unidadGT();}
{operadorLE}              {return ops.unidadLE();}
{operadorGE}              {return ops.unidadGE();}

{llaveApertura}           {return ops.unidadLA();}
{llaveCierre}           {return ops.unidadLC();}
{corcheteApertura}           {return ops.unidadCA();}
{corcheteCierre}           {return ops.unidadCC();}
{parentesisApertura}      {return ops.unidadPA();}
{parentesisCierre}        {return ops.unidadPC();}
{igual}                   {return ops.unidadIgual();} 
{coma}                    {return ops.unidadComa();}
{puntoComa}               {return ops.unidadPuntoComa();}
{punto}					  {return ops.unidadPunto();}

[^]                       {ops.error();}  