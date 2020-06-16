En la primera seccion de PL_Practica3_Memoria.pdf reflejamos todos los cambios de implementacion que hemos tomado con respecto
a la primera entrega y segunda entrega.

En la carpeta testFiles se encuentra un juego de pruebas. Estas son:
- Pruebas de fallos semanticos.
- Pruebas de generacion de codigo.

Para introducir un archivo al programa, debemos pasarlo como argumento en el main.
Al ejecutar un archivo, se creara otro llamada CodigoMaquina<nombreArchivo>.txt.

--------X---------
CAMBIOS REALIZDOS EN EL DIA EXTRA:

Hemos logrado implementar la gestion de punteros con el correcto acceso a la memoria dinamica.
En cuanto a la declaracion y llamada de funciones, somos capaces de declarar y llamarlas siempre y
cuando los parametros de estas no sean structs ni arrays. Con variables simples si funciona.
Tambien, hemos añadido y cambiado pruebas a la carpeta testFiles.

Archivos Cambiados para realizar los cambios:
Bloque
GeneradorCodigo
AnalizadorSemantico
InstDeclFun
Main (Solamente comentarios y imprimir por pantalla)